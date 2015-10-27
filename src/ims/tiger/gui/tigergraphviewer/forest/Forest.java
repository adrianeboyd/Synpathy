/*
 * File:     Forest.java
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

import ims.tiger.gui.tigergraphviewer.draw.DisplaySentence;


/**
 * Dieses Interface legt die Methoden eines Baum-Waldes fest.
 * Das Interface definiert das Datemmodell des GraphViewers.
 */
public interface Forest {
    /** Holt den Match, der nach der Initialisierung gueltig ist. Dies kann der
     *  erste Match sein, aber z.B. auch ein Match, bei dem beim letzten Arbeiten
     *  das Programm beendet wurde.
     */
    public DisplaySentence getInitialMatch();

    /** Liefert die Nummer des aktuellen Submatches zurueck. */
    public int getCurrentSubMatchNumber();

    /** Ist die Anzahl aller Submatches in dieser Implementation ueberhaupt vorhanden? */
    public boolean submatchesSumPossible();

    /** Gibt die Anzahl aller Submatches zurueck. */
    public int getSubMatchesSum();

    /** Sind Submatches in dieser Implementation ueberhaupt vorhanden? */
    public boolean submatchesPossible();

    /** Gibt die Anzahl der Submatches zurueck. */
    public int getSubMatchesSize();

    /** Gibt es einen weiteren Submatch? */
    public boolean isNextSubMatch();

    /** Gehe zum naechsten Submatch. */
    public DisplaySentence nextSubMatch();

    /** Gibt es einen vorigen Submatch? */
    public boolean isPreviousSubMatch();

    /** Gehe zum vorigen Submatch. */
    public DisplaySentence previousSubMatch();

    /** Gehe zum ersten Submatch. */
    public DisplaySentence gotoFirstSubMatch();

    /** Gehe zum n-ten Submatch. */
    public DisplaySentence gotoSubMatch(int number);

    /** Liefert Zugriff auf den Korpus-Header. */
    public Header getHeader();

    /** Liefert die Nummer des aktuellen Matches zurueck. */
    public int getCurrentMatchNumber();

    /** Match vorwaerts bei der Implementierung grundsaetzlich moeglich? */
    public boolean nextMatchPossible();

    /** Gibt es noch einen naechsten Match? */
    public boolean isNextMatch();

    /** Gehe zum naechsten Match. */
    public DisplaySentence nextMatch();

    /** Match rueckwaerts bei der Implementierung grundsaetzlich moeglich? */
    public boolean previousMatchPossible();

    /** Gibt es noch einen vorherigen Match? */
    public boolean isPreviousMatch();

    /** Gehe zum vorherigen Match. */
    public DisplaySentence previousMatch();

    /** Einsicht des Kontexts bei der Implementierung grundsaetzlich moeglich? */
    public boolean matchContextPossible();

    /** Liefert den entsprechenden Kontextgraphen zurueck (no-<0<no+) */
    public DisplaySentence getMatchContext(int contextno);

    /** Matchnummer suchen bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoMatchNumberPossible();

    /** Gibt es die Matchnummer? */
    public boolean isMatchNumber(int number);

    /** Gehe zur Matchnummer. */
    public DisplaySentence gotoMatchNumber(int number);

    /** SatzID suchen bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoSentenceIDPossible();

    /** Gibt es die SatzID? */
    public boolean isSentenceID(String id);

    /** Gehe zur SatzID. */
    public DisplaySentence gotoSentenceID(String id);

    /** Korpusanfang bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoFirstMatchPossible();

    /** Gibt es den ersten Match? */
    public boolean isFirstMatch();

    /** Gehe zum ersten Match. */
    public DisplaySentence gotoFirstMatch();

    /** Korpusende bei der Implementierung grundsaetzlich moeglich? */
    public boolean gotoLastMatchPossible();

    /** Gibt es den letzten Match? */
    public boolean isLastMatch();

    /** Gehe zum letzten Match. */
    public DisplaySentence gotoLastMatch();

    /** Ist die Anzahl der Graphen verfuegbar? */
    public boolean isForestSizeAccessible();

    /** Gib die Anzahl aller Graphen zurueck. */
    public int getForestSize();

    /** Benutze auch einen Slider zur Navigation. */
    public boolean useSliderForNavigation();

    /** Liegen bei diesem Wald fuer jeden Satz zu highlightende Subgraphen vor? */
    public boolean isForestWithMatches();
}
