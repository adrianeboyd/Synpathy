/*
 * File:     DisplaySentence.java
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
package ims.tiger.gui.tigergraphviewer.draw;

import ims.tiger.corpus.NT_Node;
import ims.tiger.corpus.Node;
import ims.tiger.corpus.Sentence;
import ims.tiger.corpus.T_Node;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;


/**
 * Diese Klasse erweitert das bisherige Sentence-Objekt um eine Liste von
 * Display-Nodes. Dieses Objekt ist das Basis-Objekt, das im GraphViewer
 * visualisiert wird.
 */
public class DisplaySentence {
    /**
     *
     */
    public static Logger logger = Logger.getLogger(DisplaySentence.class);

    /* contains innerNodes */

    /** Holds value of property DOCUMENT ME! */
    private final ArrayList displayInnerNodes = new ArrayList();

    /* contrains leaf nodes */

    /** Holds value of property DOCUMENT ME! */
    private final ArrayList displayLeafNodes = new ArrayList();

    /* contains highlighted secondary edges */

    /** Holds value of property DOCUMENT ME! */
    private final ArrayList highlightedSecEdges = new ArrayList();

    /* contains map from imploded DisplayNT_Node to original DisplayT_Node */

    /** Holds value of property DOCUMENT ME! */
    private final LinkedHashMap originalLeafNodeHash = new LinkedHashMap();

    /* contains model for this gui */

    /** Holds value of property DOCUMENT ME! */
    private final Sentence sentence;
    private int[] matchnodes;
    private boolean focusOnMatch = false;
    private boolean graphCorrect = true;
    private int display_root;
    private int match_subgraph_node = -1;
    private int maxlevel;

    /**
     *
     * @param sentence model
     */
    public DisplaySentence(Sentence sentence) {
        this.sentence = sentence;
        display_root = sentence.getRoot();
        sentence.orderSentenceByPrecedence();
        init();
    }

    /**
     * Holen des n-ten NT-Display-Knotens.
     *
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DisplayNT_Node getDisplayInnerNode(int n) {
        if (n >= ims.tiger.system.Constants.CUT) {
            n -= ims.tiger.system.Constants.CUT;
        }

        return (DisplayNT_Node) displayInnerNodes.get(n);
    }

    /**
     * Anzahl der NT-Knoten.
     *
     * @return DOCUMENT ME!
     */
    public int getDisplayInnerNodeSize() {
        return displayInnerNodes.size();
    }

    /**
     *
     * @param n number of node in array
     *
     * @return node at the specified array position (may be DisplayT_Node or imploded DisplayNT_Node)
     */
    public DisplayNode getDisplayLeafNode(int n) {
        return (DisplayNode) displayLeafNodes.get(n);
    }

    /**
     * Holen eines T- oder NT-Knotens.
     *
     * @param nummer DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DisplayNode getDisplayNode(int nummer) {
        DisplayNode node = null;

        if (nummer < displayLeafNodes.size()) {
            node = getDisplayLeafNode(nummer);
        } else {
            node = getDisplayInnerNode(nummer);
        }

        return node;
    }

    /* -------------------------------------------------------- */
    /* Methoden zum Handling der Display-Knoten                 */
    /* -------------------------------------------------------- */

    /**
     * Anzahl der T-Knoten.
     *
     * @return DOCUMENT ME!
     */
    public int getDisplayLeafNodeSize() {
        return displayLeafNodes.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getDisplay_Root() {
        return display_root;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isGraphCorrect() {
        return graphCorrect;
    }

    /**
     * DOCUMENT ME!
     *
     * @param secEdgeNr DOCUMENT ME!
     * @param highlighted DOCUMENT ME!
     */
    public void setHighlightedSecEdge(int secEdgeNr, boolean highlighted) {
        if (highlighted) {
            if (!highlightedSecEdges.contains(new Integer(secEdgeNr))) {
                highlightedSecEdges.add(new Integer(secEdgeNr));
            }
        } else {
            highlightedSecEdges.remove(new Integer(secEdgeNr));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param secEdgeNr DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isHighlightedSecEdge(int secEdgeNr) {
        return highlightedSecEdges.contains(new Integer(secEdgeNr));
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return leftmost terminal descendant
     */
    public int getLeftCorner(NT_Node node) {
        int min = getDisplayLeafNodeSize();
        int corner;

        for (int i = 0; i < node.getChildsSize(); i++) {
            int child_nr = ((Integer) node.getChildAt(i)).intValue();
            Node child_node = sentence.getNode(child_nr);

            if (child_node.isTerminal()) {
                corner = child_nr;
            } else {
                corner = getLeftCorner((NT_Node) child_node);
            }

            if (corner < min) {
                min = corner;
            }
        }

        return min;
    }

    /**
     * Besetzt die am Match beteiligten Knoten. Setzt dabei automatisch die
     * zuletzt beteiligten Knoten zurueck.
     *
     * @param newmatchnodes DOCUMENT ME!
     */
    public void setMatchNodes(int[] newmatchnodes) {
        /* 1. Alte Knoten zuruecksetzen */
        if (matchnodes != null) {
            int help;

            for (int i = 0; i < matchnodes.length; i++) {
                help = matchnodes[i];

                if (help >= 0) {
                    getDisplayNode(help).setMatchNode(false);
                }
            }
        }

        /* 2. Neue Knoten setzen */
        matchnodes = newmatchnodes;

        int help;

        for (int i = 0; i < matchnodes.length; i++) {
            help = matchnodes[i];

            if (help >= 0) {
                getDisplayNode(help).setMatchNode(true);
            }
        }
    }

    /**
     * Besetzt den Match als Subgraph mit dem Wurzelknoten no.
     *
     * @param no DOCUMENT ME!
     */
    public void setMatchSubgraphNode(int no) {
        match_subgraph_node = no;
    }

    /**
     * Gibt den WurzelKNOTEN des Match-Subgraphs zurueck.
     *
     * @return DOCUMENT ME!
     */
    public DisplayNode getMatchSubgraphNode() {
        int help = match_subgraph_node;

        if (help < 0) {
            return null;
        }

        if (help >= ims.tiger.system.Constants.CUT) { /* NT-Knoten */
            help -= ims.tiger.system.Constants.CUT;

            return (DisplayNT_Node) displayInnerNodes.get(help);
        }

        /* T-Knoten */
        return (DisplayT_Node) displayLeafNodes.get(help);
    }

    /**
     * Prueft, ob Match vorhanden ist.
     *
     * @return DOCUMENT ME!
     */
    public boolean isMatchSubgraphNode() {
        return match_subgraph_node > -1;
    }

    /**
     * Gibt die WurzelknotenNUMMER des Match-Subgraphs zurueck.
     *
     * @return DOCUMENT ME!
     */
    public int getMatchSubgraphNodeNumber() {
        return match_subgraph_node;
    }

    /**
     * Abfragen des maximalen Levels (nach Berechnung der Levels).
     *
     * @return DOCUMENT ME!
     */
    public int getMaxLevel() {
        return maxlevel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return rightmost terminal descendant
     */
    public int getRightCorner(NT_Node node) {
        int max = -1;
        int corner;

        for (int i = 0; i < node.getChildsSize(); i++) {
            int child_nr = ((Integer) node.getChildAt(i)).intValue();
            Node child_node = sentence.getNode(child_nr);

            if (child_node.isTerminal()) {
                corner = child_nr;
            } else {
                corner = getRightCorner((NT_Node) child_node);
            }

            if (corner > max) {
                max = corner;
            }
        }

        return max;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Sentence getSentence() {
        return sentence;
    }

    /* -------------------------------------------------------- */
    /* Methoden fuer GUI-Funktionalitaet: Implodieren           */
    /* -------------------------------------------------------- */

    /**
     * Hinzufuegen eines Knotens in die Imploded-Liste.
     *
     * @param displayNT_node DOCUMENT ME!
     */
    public void addImplodedNode(DisplayNT_Node displayNT_node) {
        if (focusOnMatch) {
            focusOnMatch();
        }

        int left_corner_nr = getLeftCorner((NT_Node) displayNT_node.getNode());
        setSubgraphVisible(displayNT_node, false);
        displayNT_node.setVisible(true);
        displayNT_node.setImploded(true);

        DisplayNode original = (DisplayNode) displayLeafNodes.set(left_corner_nr,
                displayNT_node);
        originalLeafNodeHash.put(displayNT_node, original);
        calculateLevels();
    }

    /**
     * Entfernen eines Knotens aus der Imploded-Liste.
     *
     * @param displayNT_node DOCUMENT ME!
     */
    public void deleteImplodedNode(DisplayNT_Node displayNT_node) {
        int index = displayLeafNodes.indexOf(displayNT_node);
        displayLeafNodes.set(index, originalLeafNodeHash.remove(displayNT_node));
        displayNT_node.setImploded(false);
        setSubgraphVisible(displayNT_node, true);
        calculateLevels();
    }

    /* -------------------------------------------------------- */
    /* Methoden fuer GUI-Funktionalitaet: Focus on match        */
    /* -------------------------------------------------------- */

    /**
     * Blendet alle Knoten ausser dem Match aus.
     */
    public void focusOnMatch() {
        focusOnMatch = true;

        /* nur Match sichtbar */
        setSubgraphVisible(getDisplayNode(display_root), false);
        setSubgraphVisible(getDisplayNode(match_subgraph_node), true);

        /* root und maxlevel neu besetzen */
        display_root = match_subgraph_node;
        maxlevel = getDisplayNode(display_root).getLevel();
    }

    /**
     * Gibt es einen virtuellen Wurzelknoten?
     *
     * @return DOCUMENT ME!
     */
    public boolean hasVirtualRootNode() {
        DisplayNode myroot = getDisplayNode(sentence.getRoot());

        if (myroot instanceof DisplayNT_Node) {
            return ((NT_Node) ((DisplayNT_Node) myroot).getNode()).isVirtualRoot();
        }

        return false;
    }

    /**
         *
         */
    public void init() {
        displayLeafNodes.clear();
        displayInnerNodes.clear();
        highlightedSecEdges.clear();
        createDisplayNodes();

        calculateLevels();

        if (matchnodes != null) {
            setMatchNodes(matchnodes);
        }
    }

    /**
         *
         */
    public void resetHighlightedSecEdges() {
        highlightedSecEdges.clear();
    }

    /**
     * Match den FocusOnMatch-Modus fuer den aktuellen Satz rueckgaengig.
     */
    public void reverseFocusOnMatch() {
        focusOnMatch = false;

        display_root = sentence.getRoot();

        if (display_root != -1) {
            maxlevel = (getDisplayNode(display_root)).getLevel();
            setSubgraphVisible(getDisplayNode(display_root), true);
        }
    }

    /**
     * Schaltet das Match-Highlighting wieder ab.
     */
    public void turnOffHighlighting() {
        this.setMatchNodes(new int[0]);
        this.setMatchSubgraphNode(-1);
    }

    /**
     * Hilfmethode besetzt rekursiv die Sichtbarkeit aller nachfolgenden
     * Knoten.
     *
     * @param node DOCUMENT ME!
     * @param visible DOCUMENT ME!
     */
    private void setSubgraphVisible(DisplayNode node, boolean visible) {
        node.setVisible(visible);

        if (node instanceof DisplayNT_Node) { // Kinder rekursiv besetzen

            DisplayNT_Node nt = (DisplayNT_Node) node;

            if (!nt.isImploded()) {
                for (int i = 0; i < nt.getChildCount(); i++) {
                    setSubgraphVisible(nt.getChildAt(i), visible);
                }
            }
        }
    }

    /* Interne Methode: Berechnet die Level der inneren Knoten. */
    private void calculateLevels() {
        ArrayList nodes = new ArrayList();
        maxlevel = 0;

        //reset values
        for (int i = 0; i < displayInnerNodes.size(); i++) {
            ((DisplayNT_Node) displayInnerNodes.get(i)).setLevel(-1);
        }

        DisplayNode node;

        for (int i = 0; i < getDisplayLeafNodeSize(); i++) {
            DisplayNode t = (DisplayNode) displayLeafNodes.get(i);

            if (t.isVisible()) {
                int parentNr = t.getNode().getParent();

                if (parentNr > -1) {
                    DisplayNT_Node parent = getDisplayInnerNode(parentNr);
                    parent.setLevel(1);
                    nodes.add(parent);
                }
            }
        }

        while (nodes.size() > 0) {
            node = (DisplayNode) nodes.remove(0);

            int currentLevel = node.getLevel();

            if (currentLevel > maxlevel) {
                maxlevel = currentLevel;
            }

            int parentNr = node.getNode().getParent();

            if (parentNr > -1) {
                DisplayNT_Node parent = getDisplayInnerNode(parentNr);
                nodes.add(parent);

                if (parent.getLevel() < (currentLevel + 1)) {
                    parent.setLevel(currentLevel + 1);
                }
            }
        }
    }

    /**
     * Besetzt die Liste der NichtTerminalknoten in Form ihrer Display-Objekte.
     */
    private void createDisplayNodes() {
        Hashtable nodeHash = new Hashtable();
        Object loopNode;

        //create leaf nodes
        for (int i = 0; i < sentence.getTerminalsSize(); i++) {
            loopNode = sentence.getTerminalAt(i);

            DisplayT_Node displayT_Node = new DisplayT_Node((T_Node) loopNode);
            displayLeafNodes.add(displayT_Node);
            nodeHash.put(new Integer(i), displayT_Node);
        }

        //create inner nodes
        for (int i = 0; i < sentence.getNonterminalsSize(); i++) {
            loopNode = sentence.getNonterminalAt(i);

            DisplayNT_Node displayNT_Node = new DisplayNT_Node((NT_Node) loopNode);
            displayInnerNodes.add(displayNT_Node);
            nodeHash.put(new Integer(i + ims.tiger.system.Constants.CUT),
                displayNT_Node);
        }

        //set children of inner nodes
        for (int i = 0; i < displayInnerNodes.size(); i++) {
            DisplayNT_Node displayNT_Node = (DisplayNT_Node) displayInnerNodes.get(i);

            for (int j = 0;
                    j < ((NT_Node) displayNT_Node.getNode()).getChildsSize();
                    j++) {
                displayNT_Node.addChild((DisplayNode) nodeHash.get(((NT_Node) displayNT_Node.getNode()).getChildAt(
                            j)));
            }
        }
    }
}
