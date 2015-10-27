/*
 * File:     GraphCalculator.java
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

/* This program is free software; you can redistribute it and/or modify
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
package ims.tiger.gui.tigergraphviewer.draw;

import ims.tiger.corpus.NT_Node;

import ims.tiger.util.UtilitiesCollection;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * $Id: GraphCalculator.java,v 1.9 2007/01/03 15:37:04 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.9 $
 */
public class GraphCalculator {
    private static List locked = new LinkedList();

    /**
     *
     *
     * @param sentence DOCUMENT ME!
     * @param ntFeatureName DOCUMENT ME!
     * @param tFeatureNames DOCUMENT ME!
     * @param g DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Dimension calculate(DisplaySentence sentence,
        String ntFeatureName, List tFeatureNames, Graphics g) {
        return calculate(sentence, ntFeatureName, tFeatureNames, g, 0);
    }

    /**
     *
     *
     * @param sentence DOCUMENT ME!
     * @param ntFeatureName DOCUMENT ME!
     * @param tFeatureNames DOCUMENT ME!
     * @param g DOCUMENT ME!
     * @param windowHeight DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Dimension calculate(DisplaySentence sentence,
        String ntFeatureName, List tFeatureNames, Graphics g, int windowHeight) {
        locked.clear();

        Dimension graphDimension = new Dimension();

        int anzahl = Math.min(tFeatureNames.size(),
                GraphConstants.treeTerminalFeatures);
        int innerTermHeight = anzahl * (GraphConstants.leafHeight +
            GraphConstants.leafGapY);
        int term_height = GraphConstants.treeVerticalMargin + innerTermHeight;

        graphDimension.height = term_height +
            (sentence.getMaxLevel() * GraphConstants.treeLevelYOffset) +
            GraphConstants.treeVerticalMargin +
            (GraphConstants.nodeHeight / 2) + 1;

        int correctedGraphHeight = Math.max(graphDimension.height, windowHeight);
        int treeWithoutTerminalsHeight = correctedGraphHeight - term_height;

        graphDimension.width = calculateLeafNodes(sentence, tFeatureNames,
                ntFeatureName, g, innerTermHeight, treeWithoutTerminalsHeight);
        calculateInnerNodes(sentence, ntFeatureName, g,
            treeWithoutTerminalsHeight);

        return graphDimension;
    }

    /* Methoden verwalten die "locked positions"
    (vgl. Annoatate-Zeichenalgorithmus) */
    private static boolean isLockedPosition(int pos) {
        Iterator it = locked.iterator();
        int lock;

        while (it.hasNext()) {
            lock = ((Integer) it.next()).intValue();

            if (java.lang.Math.abs(lock - pos) < GraphConstants.treeLockedWidth) {
                return true;
            }
        }

        return false;
    }

    private static void addLockedPosition(int pos) {
        locked.add(new Integer(pos));
    }

    private static void calculateInnerNodes(DisplaySentence sentence,
        String featureName, Graphics g, int graph_terminalstart) {
        DisplayNT_Node innerNode;
        FontMetrics fm = g.getFontMetrics();

        for (int level = 1; level <= sentence.getMaxLevel(); level++) {
            for (int i = 0; i < sentence.getDisplayInnerNodeSize(); i++) {
                innerNode = sentence.getDisplayInnerNode(i);

                if ((innerNode.isVisible()) && (innerNode.getLevel() == level)) {
                    /* Knoten im aktuellen Level */
                    innerNode.setY(graph_terminalstart -
                        (level * GraphConstants.treeLevelYOffset));

                    int min = 99999; // Suche linke und rechte Ecke der horizontalen Linie
                    int max = 0;

                    for (int j = 0; j < innerNode.getChildCount(); j++) {
                        int x = innerNode.getChildAt(j).getX();

                        if (x < min) {
                            min = x;
                        }

                        if (x > max) {
                            max = x;
                        }
                    }

                    innerNode.setSubtreeFromX(min);
                    innerNode.setSubtreeToX(max);

                    /* Hoehenkonflikt (0,..,i-1)? */
                    boolean hoehenkonflikt = false;
                    int max_hoehe = 0;

                    for (int j = 0; j < i; j++) {
                        DisplayNT_Node help = sentence.getDisplayInnerNode(j);

                        if ((help.isVisible()) & (help.getLevel() == level)) {
                            if (((min <= help.getSubtreeToX()) &&
                                    (min >= help.getSubtreeFromX())) ||
                                    ((max <= help.getSubtreeToX()) &&
                                    (max >= help.getSubtreeFromX())) ||
                                    ((help.getSubtreeFromX() >= min) &&
                                    (help.getSubtreeFromX() <= max)) ||
                                    ((help.getSubtreeToX() >= min) &&
                                    (help.getSubtreeToX() <= max))) {
                                hoehenkonflikt = true;

                                if (help.getY() > max_hoehe) {
                                    max_hoehe = help.getY();
                                }
                            }
                        }
                    }

                    if (hoehenkonflikt) {
                        innerNode.setY(max_hoehe -
                            GraphConstants.treeLockedHeight);
                    }

                    /* Wunschposition frei? */
                    int wunsch = (min + max) / 2;

                    if (isLockedPosition(wunsch)) {
                        int abstand = 0;
                        boolean posgefunden = false;

                        while (((wunsch - abstand) > min) &&
                                ((wunsch + abstand) < max)) {
                            abstand += 5;

                            if (!isLockedPosition(wunsch - abstand)) {
                                innerNode.setX(wunsch - abstand);
                                posgefunden = true;

                                break;
                            }

                            if (!isLockedPosition(wunsch + abstand)) {
                                innerNode.setX(wunsch + abstand);
                                posgefunden = true;

                                break;
                            }
                        }

                        if (!posgefunden) {
                            innerNode.setX(min);
                        }
                    } else {
                        innerNode.setX(wunsch);
                    }

                    insertLockedPosition(innerNode.getX()); // Lock neue Position

                    String ntwert;
                    int ntmaxbreite = 0;
                    int ntbreite = 0;

                    ntwert = innerNode.getNode().getFeature(featureName);

                    if (ntwert != null) {
                        ntbreite = fm.stringWidth(ntwert);
                    }

                    if (ntmaxbreite < ntbreite) {
                        ntmaxbreite = ntbreite;
                    }

                    int ntpreferred = 0;
                    ntpreferred = ntmaxbreite;

                    if (ntpreferred > GraphConstants.nodeMaxWidth) {
                        ntpreferred = GraphConstants.nodeMaxWidth;
                    }

                    if (ntpreferred < GraphConstants.nodeMinWidth) {
                        ntpreferred = GraphConstants.nodeMinWidth;
                    }

                    innerNode.setWidth(ntpreferred);
                    innerNode.setHeight(GraphConstants.nodeHeight);
                    innerNode.setLeftX(innerNode.getX() - (ntpreferred / 2));
                }
                 // Level
            }
             // visible und im Level
        }
    }

    private static int calculateLeafNodes(DisplaySentence sentence,
        List featureNames, String ntFeatureName, Graphics g,
        int inner_term_height, int graph_terminalstart) {
        DisplayNode tnode;
        String fname;
        String wert;
        FontMetrics fm = g.getFontMetrics();

        int x_position = GraphConstants.treeHorizontalMargin;

        for (int i = 0; i < sentence.getDisplayLeafNodeSize(); i++) {
            tnode = sentence.getDisplayLeafNode(i);

            if (tnode.isVisible()) {
                int maxbreite = 0;

                if (tnode instanceof NT_Node) {
                    //Sonderbehandlung der implodierten Knoten
                    if (tnode instanceof DisplayNT_Node) {
                        maxbreite = fm.stringWidth(tnode.getNode().getFeature(ntFeatureName));
                    }
                } else {
                    int fcount = 0;

                    int breite;

                    for (int j = 0; j < featureNames.size(); j++) {
                        fname = (String) featureNames.get(j);
                        wert = tnode.getNode().getFeature(fname);

                        if (wert == null) {
                            wert = "";
                        }

                        Font leafFont = UtilitiesCollection.chooseFont(GraphConstants.leafFont,
                                wert);
                        g.setFont(leafFont);
                        fm = g.getFontMetrics(leafFont);

                        fcount++;

                        if (fcount <= GraphConstants.treeTerminalFeatures) {
                            breite = fm.stringWidth(wert);

                            if (maxbreite < breite) {
                                maxbreite = breite;
                            }
                        }
                    }
                     //for
                }

                int preferred = maxbreite;

                if (preferred > GraphConstants.leafMaxWidth) {
                    preferred = GraphConstants.leafMaxWidth;
                }

                if (preferred < GraphConstants.leafMinWidth) {
                    preferred = GraphConstants.leafMinWidth;
                }

                tnode.setLeftX(x_position);

                tnode.setX(x_position + (preferred / 2));

                addLockedPosition(tnode.getX());

                tnode.setWidth(preferred);

                if (tnode instanceof DisplayNT_Node) {
                    tnode.setHeight(GraphConstants.nodeHeight);
                } else {
                    tnode.setHeight(inner_term_height);
                }

                x_position += (preferred + GraphConstants.leafGapX);
                tnode.setY(graph_terminalstart);
            }
        }
         // T-Nodes

        x_position += (-GraphConstants.leafGapX +
        GraphConstants.treeHorizontalMargin);

        return x_position;
    }

    private static void insertLockedPosition(int pos) {
        boolean gef = false;

        Iterator it = locked.iterator();
        int lock;
        int insertpos = -1;

        while ((!gef) && (it.hasNext())) {
            insertpos++;
            lock = ((Integer) it.next()).intValue();

            if (lock > pos) {
                gef = true;

                break;
            }
        }

        if (gef) {
            locked.add(insertpos, new Integer(pos));
        } else {
            locked.add(new Integer(pos));
        }
    }
}
