/*
 * File:     CorpusForest.java
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

import ims.tiger.query.api.QueryIndexException;

import ims.tiger.query.internalapi.InternalCorpusQueryManager;

import org.apache.log4j.Logger;


/** Definiert ein darzustellendes Korpus als Liste von Einzelgraphen.
 *  Dies ist das einzige statische Datenmodell, das zur Zeit im GraphViewer visualisiert
 *  wird. Zu einem spaeteren Zeitpunkt werden auch TIGER-XML-Dokumente
 *  dieses Interface implementieren.
 */
public class CorpusForest implements Forest {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(CorpusForest.class);
    private InternalCorpusQueryManager manager;
    private int sentno;
    private int sentcount;
    private DisplaySentence sentence;
    private boolean modified;

    /** Konstruktor uebergibt Manager */
    public CorpusForest(InternalCorpusQueryManager manager)
        throws ForestException {
        this.manager = manager;

        sentcount = manager.getHeader().getNumberOfSentences();

        //if (sentcount == 0) { throw new ForestException("Empty corpus!"); }
        if (sentcount > 0) {
            setMatchNumber(1);
        } else {
            sentno = -1;
        }
    }

    /** Liefert Zugriff auf den Korpus-Header. */
    public Header getHeader() {
        return manager.getHeader();
    }

    /** Setze aktuelle Satznummer um */
    private void setMatchNumber(int number) {
        sentno = number;

        Sentence sent = null;

        try {
            sent = manager.getSentence(sentno);
        } catch (QueryIndexException e) {
            logger.error("Could not look up the corpus graph", e);
        }

        sentence = new DisplaySentence(sent);
    }

    /** Einsicht des Kontexts bei der Implementierung grundsaetzlich moeglich? */
    public boolean matchContextPossible() {
        return true;
    }

    /** Liefert den entsprechenden Kontextgraphen zurueck (no-<0<no+) */
    public DisplaySentence getMatchContext(int contextno) {
        int target = sentno + contextno;

        if ((target >= 0) && (target < sentcount)) {
            Sentence sent = null;

            try {
                sent = manager.getSentence(target);
            } catch (QueryIndexException e) {
                logger.error("Could not look up the context corpus graph", e);
            }

            DisplaySentence result = new DisplaySentence(sent);

            return result;
        } else {
            return null;
        }
    }

    /** Ist die Anzahl aller Submatches in dieser Implementation ueberhaupt vorhanden? */
    public boolean submatchesSumPossible() {
        return false;
    }

    /** Gibt die Anzahl aller Submatches zurueck. */
    public int getSubMatchesSum() {
        return 0;
    }

    /** Sind Submatches in dieser Implementation ueberhaupt vorhanden? */
    public boolean submatchesPossible() {
        return false;
    }

    /** Liefert die Nummer des aktuellen Submatches zurueck. */
    public int getCurrentSubMatchNumber() {
        return 0;
    }

    /** Gibt die Anzahl der Submatches zurueck. */
    public int getSubMatchesSize() {
        return 0;
    }

    /** Gibt es einen weiteren Submatch? */
    public boolean isNextSubMatch() {
        return false;
    }

    /** Gehe zum naechsten Submatch. */
    public DisplaySentence nextSubMatch() {
        return null;
    }

    /** Gehe zum ersten Submatch. */
    public DisplaySentence gotoFirstSubMatch() {
        return null;
    }

    /** Gibt es einen vorigen Submatch? */
    public boolean isPreviousSubMatch() {
        return false;
    }

    /** Gehe zum vorigen Submatch. */
    public DisplaySentence previousSubMatch() {
        return null;
    }

    /** Gehe zum n-ten Submatch. */
    public DisplaySentence gotoSubMatch(int number) {
        return null;
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
        return sentno;
    }

    /** Match vorwaerts bei der Implementierung grundsaetzlich moeglich? */
    public boolean nextMatchPossible() {
        return true;
    }

    /** Gibt es noch einen naechsten Match? */
    public boolean isNextMatch() {
        return (sentno < sentcount);
    }

    /** Gehe zum naechsten Match. */
    public DisplaySentence nextMatch() {
        setMatchNumber(sentno + 1);

        return sentence;
    }

    /** Match rueckwaerts bei der Implementierung grundsaetzlich moeglich? */
    public boolean previousMatchPossible() {
        return true;
    }

    /** Gibt es noch einen vorherigen Match? */
    public boolean isPreviousMatch() {
        return (sentno > 1);
    }

    /** Gehe zum vorherigen Match. */
    public DisplaySentence previousMatch() {
        setMatchNumber(sentno - 1);

        return sentence;
    }

    /** Matchnummer suchen bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoMatchNumberPossible() {
        return true;
    }

    /** Gibt es die Matchnummer? */
    public boolean isMatchNumber(int number) {
        return ((number > 0) && (number <= sentcount));
    }

    /** Gehe zur Matchnummer, beginnend mit 1! */
    public DisplaySentence gotoMatchNumber(int number) {
        setMatchNumber(number);

        return sentence;
    }

    /** SatzID suchen bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoSentenceIDPossible() {
        return true;
    }

    /** Gibt es die SatzID? */
    public boolean isSentenceID(String id) {
        int number = manager.getSentenceNumber(id);

        return (number > 0);
    }

    /** Gehe zur SatzID. */
    public DisplaySentence gotoSentenceID(String id) {
        int number = manager.getSentenceNumber(id);
        setMatchNumber(number);

        return sentence;
    }

    /** Korpusanfang bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoFirstMatchPossible() {
        return true;
    }

    /** Gibt es den ersten Match? */
    public boolean isFirstMatch() {
        return sentcount > 0;
    }

    /** Gehe zum ersten Match. */
    public DisplaySentence gotoFirstMatch() {
        setMatchNumber(1);

        return sentence;
    }

    /** Korpusende bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoLastMatchPossible() {
        return true;
    }

    /** Gibt es den letzten Match? */
    public boolean isLastMatch() {
        return sentcount > 0;
    }

    /** Gehe zum letzten Match. */
    public DisplaySentence gotoLastMatch() {
        setMatchNumber(sentcount);

        return sentence;
    }

    /** Ist die Anzahl der Graphen verfuegbar? */
    public boolean isForestSizeAccessible() {
        return true;
    }

    /** Gib die Anzahl aller Graphen zurueck. */
    public int getForestSize() {
        return sentcount;
    }

    /** Benutze auch einen Slider zur Navigation. */
    public boolean useSliderForNavigation() {
        return true;
    }

    /** Liegen bei diesem Wald fuer jeden Satz zu highlightende Subgraphen vor? */
    public boolean isForestWithMatches() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public InternalCorpusQueryManager getCorpus() {
        return manager;
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setModified(boolean b) {
        modified = b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isModified() {
        return modified;
    }
}
