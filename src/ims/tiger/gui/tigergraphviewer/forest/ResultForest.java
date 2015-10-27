/*
 * File:     ResultForest.java
 * Project:  MPI Linguistic Application
 * Date:     07 February 2007
 *
 * Copyright (C) 2001-2007  Max Planck Institute for Psycholinguistics
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

/*
 * IMS, University of Stuttgart
 * TIGER Treebank Project
 * Copyright 1999-2003, all rights reserved
 */
package ims.tiger.gui.tigergraphviewer.forest;

import ims.tiger.corpus.Header;
import ims.tiger.corpus.Sentence;

import ims.tiger.gui.tigergraphviewer.draw.DisplaySentence;

import ims.tiger.query.api.MatchResult;
import ims.tiger.query.api.QueryIndexException;

import ims.tiger.query.internalapi.InternalCorpusQueryManager;

import org.apache.log4j.Logger;

import java.util.Arrays;


/** Implementiert das Forest-Interface zur Repraesentation von
 *  Anfrageergebnissen.
 */
public class ResultForest implements Forest {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(ResultForest.class);
    private InternalCorpusQueryManager manager;
    private MatchResult result;
    private int matchno;
    private int sentno;
    private int submatchno;
    private int[] match_nodes;
    private int subgraph_node;
    private DisplaySentence sentence;

    /**
     * Creates a new ResultForest instance
     *
     * @param result DOCUMENT ME!
     * @param manager DOCUMENT ME!
     *
     * @throws ForestException DOCUMENT ME!
     */
    public ResultForest(MatchResult result, InternalCorpusQueryManager manager)
        throws ForestException {
        this.result = result;
        this.manager = manager;

        if ((result == null) || (result.size() == 0)) {
            throw new ForestException("Empty forest.");
        }

        setMatchNumber(0);
    }

    /** Liefert Zugriff auf den Korpus-Header. */
    public Header getHeader() {
        return manager.getHeader();
    }

    /**
     * DOCUMENT ME!
     *
     * @param IDs DOCUMENT ME!
     *
     * @throws ForestException DOCUMENT ME!
     */
    public void filter(String[] IDs) throws ForestException {
        if (result == null) {
            throw new ForestException("No forest present!");
        }

        MatchResult filteredResult = new MatchResult();
        int localSentno;
        int[] localMatch_nodes;
        byte[] bmatch_nodes;
        String sentenceID;
        String fragmentID;
        Arrays.sort(IDs);

        for (int loopmatchno = 0; loopmatchno < result.size(); loopmatchno++) {
            try {
                localSentno = result.getSentenceNumberAt(loopmatchno);
                sentenceID = manager.getSentenceID(localSentno);
                fragmentID = sentenceID.substring(0, sentenceID.indexOf('.'));

                if (Arrays.binarySearch(IDs, fragmentID) > -1) {
                    for (int loopsubmatchno = 0;
                            loopsubmatchno < result.getSentenceSubmatchSize(
                                localSentno); loopsubmatchno++) {
                        localMatch_nodes = result.getSentenceSubmatchAt(localSentno,
                                loopsubmatchno);
                        bmatch_nodes = new byte[localMatch_nodes.length];

                        for (int i = 0; i < localMatch_nodes.length; i++) {
                            bmatch_nodes[i] = (byte) localMatch_nodes[i];
                        }

                        filteredResult.insertMatch(localSentno, bmatch_nodes);
                    }
                }
            } catch (QueryIndexException qe) {
                logger.error(qe.getMessage());
            } catch (IndexOutOfBoundsException e) {
                throw new ForestException("Internal Error. Match nr " +
                    loopmatchno + " not defined.");
            }
        }

        filteredResult.insertIsFinished();
        filteredResult.setVariableNames(result.getVariableNames());
        filteredResult.setVariableTypes(result.getVariableTypes());
        result = filteredResult;

        if (result.size() > 0) {
            setMatchNumber(0);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param fileName DOCUMENT ME!
     */
    public void exportMatchingTerminals(String fileName) {
        Result2XML.exportMatchingTerminals(fileName, manager, result);
    }

    private void setMatchNumber(int number) {
        matchno = number;
        sentno = result.getSentenceNumberAt(matchno);

        Sentence sent = null;

        try {
            sent = manager.getSentence(sentno);
        } catch (QueryIndexException e) {
            logger.error("Could not look up the corpus graph", e);
        }

        sentence = new DisplaySentence(sent);

        setSubMatchNumber(0);
    }

    private void setSubMatchNumber(int number) {
        submatchno = number;

        match_nodes = result.getSentenceSubmatchAt(sentno, submatchno);

        try {
            subgraph_node = manager.subgraph(sentno, match_nodes);
        } catch (QueryIndexException e) {
            subgraph_node = -1;
        }

        sentence.setMatchNodes(match_nodes);
        sentence.setMatchSubgraphNode(subgraph_node);
    }

    /** Ist die Anzahl aller Submatches in dieser Implementation ueberhaupt vorhanden? */
    public boolean submatchesSumPossible() {
        return true;
    }

    /** Gibt die Anzahl aller Submatches zurueck. */
    public int getSubMatchesSum() {
        return result.submatchSize();
    }

    /** Liefert die Nummer des aktuellen Submatches zurueck. */
    public int getCurrentSubMatchNumber() {
        return submatchno + 1;
    }

    /** Sind Submatches in dieser Implementation ueberhaupt vorhanden? */
    public boolean submatchesPossible() {
        return true;
    }

    /** Gibt die Anzahl der Submatches zurueck. */
    public int getSubMatchesSize() {
        return result.getSentenceSubmatchSize(sentno);
    }

    /** Gibt es einen weiteren Submatch? */
    public boolean isNextSubMatch() {
        return ((submatchno + 1) < result.getSentenceSubmatchSize(sentno));
    }

    /** Gehe zum naechsten Submatch. */
    public DisplaySentence nextSubMatch() {
        setSubMatchNumber(submatchno + 1);

        return sentence;
    }

    /** Gehe zum ersten Submatch. */
    public DisplaySentence gotoFirstSubMatch() {
        setSubMatchNumber(0);

        return sentence;
    }

    /** Gibt es einen vorigen Submatch? */
    public boolean isPreviousSubMatch() {
        return (submatchno > 0);
    }

    /** Gehe zum vorigen Submatch. */
    public DisplaySentence previousSubMatch() {
        setSubMatchNumber(submatchno - 1);

        return sentence;
    }

    /** Gehe zum n-ten Submatch. */
    public DisplaySentence gotoSubMatch(int number) {
        if ((number < 0) || (number >= getSubMatchesSize())) {
            return null;
        }

        setSubMatchNumber(number);

        return sentence;
    }

    /** Einsicht des Kontexts bei der Implementierung grundsaetzlich moeglich? */
    public boolean matchContextPossible() {
        return true;
    }

    /** Liefert den entsprechenden Kontextgraphen zurueck (no-<0<no+) */
    public DisplaySentence getMatchContext(int contextno) {
        int target = sentno + contextno;

        if ((target > 0) &&
                (target <= manager.getHeader().getNumberOfSentences())) {
            Sentence sent = null;

            try {
                sent = manager.getSentence(target);
            } catch (QueryIndexException e) {
                logger.error("Could not look up the context corpus graph", e);
            }

            DisplaySentence sentresult = new DisplaySentence(sent);

            return sentresult;
        } else {
            return null;
        }
    }

    /** Holt den Match, der nach der Initialisierung gueltig ist. Dies kann der
     *  erste Match sein, aber z.B. auch ein Match, bei dem beim letzten Arbeiten
     *  das Programm beendet wurde.
     */
    public DisplaySentence getInitialMatch() {
        return sentence;
    }

    /** Liefert den aktuellen Match zurueck. Wird in der Regel nicht verwendet. */
    public DisplaySentence getCurrentMatch() {
        return sentence;
    }

    /** Liefert die Nummer des aktuellen Matches zurueck. */
    public int getCurrentMatchNumber() {
        return matchno + 1;
    }

    /** Match vorwaerts bei der Implementierung grundsaetzlich moeglich? */
    public boolean nextMatchPossible() {
        return true;
    }

    /** Gibt es noch einen naechsten Match? */
    public boolean isNextMatch() {
        return ((matchno + 1) < result.size());
    }

    /** Gehe zum naechsten Match. */
    public DisplaySentence nextMatch() {
        setMatchNumber(matchno + 1);

        return sentence;
    }

    /** Match rueckwaerts bei der Implementierung grundsaetzlich moeglich? */
    public boolean previousMatchPossible() {
        return true;
    }

    /** Gibt es noch einen vorherigen Match? */
    public boolean isPreviousMatch() {
        return (matchno > 0);
    }

    /** Gehe zum vorherigen Match. */
    public DisplaySentence previousMatch() {
        setMatchNumber(matchno - 1);

        return sentence;
    }

    /** Matchnummer suchen bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoMatchNumberPossible() {
        return true;
    }

    /** Gibt es die Matchnummer? */
    public boolean isMatchNumber(int number) {
        return ((number > 0) && (number <= result.size()));
    }

    /** Gehe zur Matchnummer, beginnend mit 1! */
    public DisplaySentence gotoMatchNumber(int number) {
        setMatchNumber(number - 1);

        return sentence;
    }

    /** SatzID suchen bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoSentenceIDPossible() {
        return true;
    }

    /** Gibt es die SatzID? */
    public boolean isSentenceID(String id) {
        int number = manager.getSentenceNumber(id);

        return result.isMatchingSentence(number);
    }

    /** Gehe zur SatzID. */
    public DisplaySentence gotoSentenceID(String id) {
        int number = manager.getSentenceNumber(id);
        int position = result.getMatchPosition(number);
        setMatchNumber(position);

        return sentence;
    }

    /** Korpusanfang bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoFirstMatchPossible() {
        return true;
    }

    /** Gibt es den ersten Match? */
    public boolean isFirstMatch() {
        return true;
    }

    /** Gehe zum ersten Match. */
    public DisplaySentence gotoFirstMatch() {
        setMatchNumber(0);

        return sentence;
    }

    /** Korpusende bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoLastMatchPossible() {
        return true;
    }

    /** Gibt es den letzten Match? */
    public boolean isLastMatch() {
        return true;
    }

    /** Gehe zum letzten Match. */
    public DisplaySentence gotoLastMatch() {
        setMatchNumber(result.size() - 1);

        return sentence;
    }

    /** Ist die Anzahl der Graphen verfuegbar? */
    public boolean isForestSizeAccessible() {
        return true;
    }

    /** Gib die Anzahl aller Graphen zurueck. */
    public int getForestSize() {
        return result.size();
    }

    /** Benutze auch einen Slider zur Navigation. */
    public boolean useSliderForNavigation() {
        return true;
    }

    /** Liegen bei diesem Wald fuer jeden Satz zu highlightende Subgraphen vor? */
    public boolean isForestWithMatches() {
        return true;
    }
}
