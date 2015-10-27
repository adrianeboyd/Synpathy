/*
 * File:     MatchResult.java
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
package ims.tiger.query.api;

import ims.tiger.query.internalapi.InternalCorpusQueryManager;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;


/** This class represents the results of a corpus query. */
public class MatchResult implements Serializable {
    /** Holds value of property DOCUMENT ME! */
    public static int T = 0;

    /** Holds value of property DOCUMENT ME! */
    public static int NT = 1;

    /** Holds value of property DOCUMENT ME! */
    public static int FREC = 2;
    private HashMap matches;
    private int[] sentence_numbers;
    private String[] varnames;
    private int[] vartypes;

    /**
     * Creates a new MatchResult instance
     */
    public MatchResult() {
        matches = new HashMap();
        sentence_numbers = null;
        varnames = null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param second DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(MatchResult second) {
        if (this.size() != second.size()) {
            return false;
        }

        int sentence;

        for (int i = 0; i < this.size(); i++) {
            if (this.getSentenceNumberAt(i) != second.getSentenceNumberAt(i)) {
                return false;
            }

            sentence = this.getSentenceNumberAt(i);

            int[] nodes1;
            int[] nodes2;

            if (this.getSentenceSubmatchSize(sentence) != second.getSentenceSubmatchSize(
                        sentence)) {
                return false;
            }

            for (int j = 0; j < getSentenceSubmatchSize(sentence); j++) {
                nodes1 = this.getSentenceSubmatchAt(sentence, j);
                nodes2 = second.getSentenceSubmatchAt(sentence, j);

                if (nodes1.length != nodes2.length) {
                    return false;
                }

                for (int k = 0; k < nodes1.length; k++) {
                    if (nodes1[k] != nodes2[k]) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * DOCUMENT ME!
     */
    public void print() {
        prettyPrint(null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param manager DOCUMENT ME!
     */
    public void prettyPrint(InternalCorpusQueryManager manager) {
        int sentence;

        for (int i = 0; i < sentence_numbers.length; i++) {
            sentence = getSentenceNumberAt(i);
            System.out.print(sentence + ":  ");

            for (int j = 0; j < getSentenceSubmatchSize(sentence); j++) {
                System.out.print(" (");

                int[] nodes = getSentenceSubmatchAt(sentence, j);

                if (manager != null) {
                    try {
                        System.out.print(manager.subgraph(sentence, nodes));
                    } catch (Exception e) {
                        System.out.print("??? " + e.getMessage());
                    }

                    System.out.print(" -> ");
                }

                for (int k = 0; k < nodes.length; k++) {
                    System.out.print(getVariableName(k) + "=" + nodes[k] + " ");
                }

                System.out.print(") ");
            }

            System.out.println("");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param varnames DOCUMENT ME!
     */
    public void setVariableNames(String[] varnames) {
        this.varnames = varnames;
    }

    /**
     * DOCUMENT ME!
     *
     * @param vartypes DOCUMENT ME!
     */
    public void setVariableTypes(int[] vartypes) {
        this.vartypes = vartypes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param nodes DOCUMENT ME!
     */
    public void insertMatch(int sentence, byte[] nodes) {
        insertMatch(new Integer(sentence), nodes);
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param nodes DOCUMENT ME!
     */
    public void insertMatch(Integer sentence, byte[] nodes) {
        if (matches.containsKey(sentence)) { // Einfuegen in Liste

            List l = (List) matches.get(sentence);
            l.add(nodes);
        } else { // Neue Liste anlegen

            List l = new ArrayList();
            l.add(nodes);
            matches.put(sentence, l);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void insertIsFinished() {
        /* Sortierte Satzliste erzeugen */
        Object[] help = matches.keySet().toArray();
        sentence_numbers = new int[help.length];

        for (int i = 0; i < help.length; i++) {
            sentence_numbers[i] = ((Integer) help[i]).intValue();
        }

        Arrays.sort(sentence_numbers);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int size() {
        return matches.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int submatchSize() {
        int submatches = 0;

        for (int i = 0; i < size(); i++) {
            submatches += getSentenceSubmatchSize(getSentenceNumberAt(i));
        }

        return submatches;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getVariableSize() {
        return varnames.length;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getVariableNames() {
        return varnames;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVariableName(int i) {
        return varnames[i];
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getVariableTypes() {
        return vartypes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getVariableType(int i) {
        return vartypes[i];
    }

    /**
     * DOCUMENT ME!
     *
     * @param matchno DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getSentenceNumberAt(int matchno) {
        return sentence_numbers[matchno];
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isMatchingSentence(int sentence) {
        return matches.containsKey(new Integer(sentence));
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getMatchPosition(int sentence) {
        for (int i = 0; i < sentence_numbers.length; i++) {
            if (sentence == sentence_numbers[i]) {
                return i;
            }
        }

        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getSentenceSubmatchSize(int sentence) {
        return ((List) matches.get(new Integer(sentence))).size();
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param submatchno DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getSentenceSubmatchAt(int sentence, int submatchno) {
        List l = (List) matches.get(new Integer(sentence));
        byte[] b = (byte[]) l.get(submatchno);

        int[] result = new int[b.length];

        for (int i = 0; i < b.length; i++) {
            result[i] = ims.tiger.util.UtilitiesCollection.byte2int(b[i]);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     */
    public void orderSentenceSubmatches(int sentence) {
        List l = (List) matches.get(new Integer(sentence));

        // Heuristik: erstes Byte zaehlt (per Bubblesort auf LinkedList)
        boolean swap = true;
        ListIterator i1;
        ListIterator i2;
        Object o1;
        Object o2;

        while (swap) {
            i1 = l.listIterator();
            i2 = l.listIterator();

            if (i2.hasNext()) {
                i2.next();
            } // i2 immer einen Schritt vorweg
            else {
                return;
            }

            swap = false;

            while ((i1.hasNext()) && (i2.hasNext())) {
                o1 = i1.next();
                o2 = i2.next();

                if (((byte[]) o1)[0] > ((byte[]) o2)[0]) {
                    swap = true;
                    i1.set(o2);
                    i2.set(o1);
                }
            }
        }
    }
}
