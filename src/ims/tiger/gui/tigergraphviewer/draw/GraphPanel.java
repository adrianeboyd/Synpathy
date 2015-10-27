/*
 * File:     GraphPanel.java
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
/*
 * IMS, University of Stuttgart
 * TIGER Treebank Project
 * Copyright 1999-2003, all rights reserved
 */
package ims.tiger.gui.tigergraphviewer.draw;

import ims.tiger.corpus.Header;
import ims.tiger.corpus.NT_Node;
import ims.tiger.corpus.Node;
import ims.tiger.corpus.Sentence;
import ims.tiger.corpus.T_Node;

import ims.tiger.gui.shared.MultiLineToolTip;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.gui.tigergraphviewer.forest.CorpusForest;

import ims.tiger.util.UtilitiesCollection;

import org.apache.log4j.Logger;

import org.jdom.Element;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.ItemSelectable;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JToolTip;
import javax.swing.RepaintManager;
import javax.swing.ToolTipManager;


/**
 * Diese Klasse definiert den Graphalgorithmus. Sie stellt die innere Zeichenflaeche des
 * GraphViewers dar, d.h. ohne Navigationsleiste.
 */
public class GraphPanel extends JPanel implements Printable, ItemSelectable {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(GraphPanel.class.getName());

    /** Holds value of property DOCUMENT ME! */
    static final int HIGHLIGHT_ON = 1;

    /** Holds value of property DOCUMENT ME! */
    static final int HIGHLIGHT_OFF = 2;

    /** Holds value of property DOCUMENT ME! */
    static final int MATCH_HIGHLIGHT_ON = 3;

    /** Holds value of property DOCUMENT ME! */
    static final int MATCH_HIGHLIGHT_OFF = 4;
    private ArrayList itemListeners = new ArrayList();
    private BufferedImage bufferedImage;
    private Dimension graphDimension = new Dimension();
    private DisplayNode highlightedNode;
    private DisplayNode highlightedSubgraphRootNode;
    private DisplaySentence sentence;
    private Graphics2D bufferedImageGraphics;
    private Header header;
    private java.util.List secEdgeList = new ArrayList();
    private PageFormat pFormat;
    private TIGERGraphViewerConfiguration config;
    private boolean corpusModified = false;
    private boolean focusOnMatch = false;
    private boolean showSecondaryEdges = true;

    /** Difference of this panel height and height of the tree */
    private int delta_y = 0;

    /**
     * Konstruktor stellt Verbindung zum umgebenden ForestViewer her.
     */
    public GraphPanel(TIGERGraphViewerConfiguration config) {
        this.config = config;
        setBackground(GraphConstants.panelBackgroundColor);
        setForeground(GraphConstants.panelColor);

        // Highlights
        sentence = null;
        highlightedNode = null;
        highlightedSubgraphRootNode = null;

        // Tool tips
        ToolTipManager.sharedInstance().registerComponent(this);
        ToolTipManager.sharedInstance().setEnabled(false);

        // Event-Listener
        addMouseListener(new GraphPanelMouseListener(this));
        addMouseMotionListener(new GraphPanelMouseMotionListener(this));

        addComponentListener(new ComponentAdapter() {
                public synchronized void componentResized(ComponentEvent e) {
                    if (sentence == null) {
                        return;
                    }

                    setScroll();
                }
            });
    }

    /**
     * sets adequate mouse listener to make panel editable/uneditable
     *
     * @param b true, if editable
     */
    public void setEditable(boolean b) {
        MouseListener[] ml = getMouseListeners();

        for (int i = 0; i < ml.length; i++) {
            removeMouseListener(ml[i]);
        }

        addMouseListener(b ? new GraphPanelEditorMouseListener(this)
                           : new GraphPanelMouseListener(this));
    }

    /**
     * Setzt den FocusOnMatch-Modus.
     *
     * @param focusOnMatch DOCUMENT ME!
     */
    public void setFocusOnMatch(boolean focusOnMatch) {
        this.focusOnMatch = focusOnMatch;
    }

    /**
     * DOCUMENT ME!
     *
     * @param forest DOCUMENT ME!
     */
    public void setForest(CorpusForest forest) {
        MouseListener[] ml = getMouseListeners();

        for (int i = 0; i < ml.length; i++) {
            if (ml[i] instanceof GraphPanelEditorMouseListener) {
                ((GraphPanelEditorMouseListener) ml[i]).setForest(forest);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getGraphSize() {
        return graphDimension;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public TIGERGraphViewerConfiguration getGraphViewerConfiguration() {
        return config;
    }

    /**
     * Setzt den Header fuer ein neu geladenes Korpus.
     *
     * @param header DOCUMENT ME!
     */
    public synchronized void setHeader(Header header) {
        this.header = header;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Header getHeader() {
        return header;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     */
    public void setHighlightedNode(DisplayNode node) {
        highlightedNode = node;
        notifyNodeSelected(node);
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     */
    public void setHighlightedSubgraphRootNode(DisplayNode node) {
        highlightedSubgraphRootNode = node;
        setHighlightedNode(null);
    }

    /* Drucken. */
    public void setPage() {
        PrinterJob job = PrinterJob.getPrinterJob();

        if (pFormat == null) {
            pFormat = job.defaultPage();
        }

        pFormat = job.pageDialog(pFormat);
    }

    /**
     * Scrolls to hightlighted parts, if any. Otherwise to left lower corner of sentence tree
     */
    public void setScroll() {
        int x = 0;
        int y = getHeight() - 1;
        int width = 1;
        int height = 1;

        //test if at least one terminal node is marked
        for (int i = 0; i < sentence.getDisplayLeafNodeSize(); i++) {
            if (sentence.getDisplayLeafNode(i).isHighlightedNode()) {
                width = sentence.getDisplayLeafNode(i).getWidth() + 4;
                x = sentence.getDisplayLeafNode(i).getX() - (width / 2);
                scrollRectToVisible(new Rectangle(x, y, width, height));

                return;
            }
        }

        //if none is marked, test if some node is part of matched subgraph
        DisplayNode subgraphNode = sentence.getMatchSubgraphNode();

        if (subgraphNode != null) {
            if (subgraphNode instanceof DisplayT_Node) {
                x = subgraphNode.getX();
            }

            if (subgraphNode instanceof DisplayNT_Node) {
                int leftnr;
                int rightnr;
                int left;
                int right;
                int nodeDistance;

                leftnr = sentence.getLeftCorner((NT_Node) ((DisplayNT_Node) subgraphNode).getNode());
                rightnr = sentence.getRightCorner((NT_Node) ((DisplayNT_Node) subgraphNode).getNode());

                left = sentence.getDisplayLeafNode(leftnr).getX();
                right = sentence.getDisplayLeafNode(rightnr).getX();

                nodeDistance = right - left;

                if (width < getWidth()) {
                    x = left;
                    width = nodeDistance;
                } else {
                    x = subgraphNode.getX();
                }
            }
        }

        scrollRectToVisible(new Rectangle(x, y, width, height));
    }
     //end setScroll

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] getSelectedObjects() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     */
    public synchronized void setSentence(DisplaySentence sentence) {
        if (sentence == null) {
            createG2D(new Dimension(100, 100));
            this.sentence = null;

            return;
        }

        this.sentence = sentence;
        update();
    }

    /**
     * DOCUMENT ME!
     */
    void update() {
        // Ellipsenhoehe des Wurzelknotens
        // Hole Panelgroesse von aussen (vertical scrollbar)
        int window_height = 0;

        // TODO: Hier muesste eigentlich auch die Hoehe des horizontalen Balkens, falls sichtbar,
        // eingehen. Ob ein horizontaler Balken notwendig ist, kann aber erst nach Berechnung (der
        // Breite) des Baumes ermittelt werden.
        if (getParent() != null) {
            window_height = getParent().getHeight() - 3;
        }

        // nachgezogene Groesse kleiner?
        if (getHeight() < window_height) {
            window_height = getHeight();
        }

        calculateTree(getGraphics(), window_height);

        delta_y = Math.max(0, window_height - graphDimension.height);

        Dimension windowDimension = new Dimension(graphDimension.width,
                graphDimension.height + delta_y);
        bufferedImageGraphics = createG2D(windowDimension);
        setPreferredSize(windowDimension);
        revalidate();
    }

    /**
     * DOCUMENT ME!
     *
     * @param il DOCUMENT ME!
     */
    public void addItemListener(ItemListener il) {
        if (!itemListeners.contains(il)) {
            itemListeners.add(il);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JToolTip createToolTip() {
        MultiLineToolTip tip = new MultiLineToolTip(UtilitiesCollection.chooseFont(
                    GraphConstants.tooltipFont, ""));
        tip.setComponent(this);

        return tip;
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public static void disableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     */
    public static void enableDoubleBuffering(Component c) {
        RepaintManager currentManager = RepaintManager.currentManager(c);
        currentManager.setDoubleBufferingEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DisplaySentence getCurrentDisplaySentence() {
        return sentence;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Sentence getCurrentSentence() {
        return sentence.getSentence();
    }

    /**
     * Macht evtl. Highlighting im aktuellen Satz rueckgaengig.
     */
    public synchronized void normalizeHighlights() {
        if (highlightedNode != null) {
            highlightedNode.setHighlightedNode(false);
            highlightedNode = null;
        }

        if (highlightedSubgraphRootNode != null) {
            highlightSubgraph(sentence, highlightedSubgraphRootNode,
                HIGHLIGHT_OFF);
            highlightedSubgraphRootNode = null;
        }

        for (int i = 0; i < sentence.getDisplayInnerNodeSize(); i++) {
            sentence.getDisplayInnerNode(i).setHighlightedNode(false);
            sentence.getDisplayInnerNode(i).setHighlightedEdge(false);
        }

        for (int i = 0; i < sentence.getDisplayLeafNodeSize(); i++) {
            sentence.getDisplayLeafNode(i).setHighlightedNode(false);
            sentence.getDisplayLeafNode(i).setHighlightedEdge(false);
        }
    }

    /**
     * Macht das Highlighting des aktuellen Submatches rueckgaengig.
     */
    public synchronized void normalizeMatchHighlights() {
        if (sentence.isMatchSubgraphNode()) {
            highlightSubgraph(sentence,
                sentence.getDisplayNode(sentence.getMatchSubgraphNodeNumber()),
                MATCH_HIGHLIGHT_OFF);
        }
    }

    /**
     * Neuzeichnen des Panels, veranlasst von der VM.
     *
     * @param g DOCUMENT ME!
     */
    public synchronized void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (bufferedImage != null) {
            secEdgeList = GraphPanelUtil.paintSentence(sentence, header,
                    config, bufferedImageGraphics);
            g.drawImage(bufferedImage, 0, 0, this);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param shift DOCUMENT ME!
     * @param id_anhaengsel DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element paintSVG(int shift, String id_anhaengsel) {
        return GraphPanelSVGUtil.paintSVG(header, config, sentence,
            bufferedImageGraphics, graphDimension, delta_y - shift,
            id_anhaengsel, showSecondaryEdges);
    }

    /**
     * DOCUMENT ME!
     *
     * @param shift DOCUMENT ME!
     * @param id_anhaengsel DOCUMENT ME!
     * @param g DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element paintSVG(int shift, String id_anhaengsel, Graphics2D g) {
        return GraphPanelSVGUtil.paintSVG(header, config, sentence, g,
            graphDimension, delta_y - shift, id_anhaengsel, showSecondaryEdges);
    }

    /**
     * DOCUMENT ME!
     */
    public void print() {
        PrinterJob pjob = PrinterJob.getPrinterJob();

        if (pjob != null) {
            if (pFormat == null) {
                pFormat = pjob.defaultPage();
            }

            pjob.setPrintable(this, pFormat);

            if (pjob.printDialog()) {
                try {
                    pjob.print();
                } catch (PrinterException pe) {
                    logger.error("Error printing: " + pe);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param pageFormat DOCUMENT ME!
     * @param pageIndex DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
        if (pageIndex > 0) {
            return (NO_SUCH_PAGE);
        }

        Dimension size = this.getPreferredSize();

        double graphWidth = size.width;
        double graphHeight = size.height;
        double pageWidth = pageFormat.getImageableWidth();
        double pageHeight = pageFormat.getImageableHeight();

        double scaleWidth = 1;

        if (graphWidth >= pageWidth) {
            scaleWidth = pageWidth / graphWidth;
        }

        double scaleHeight = 1;

        if (graphHeight >= pageHeight) {
            scaleHeight = pageHeight / graphHeight;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        if (graphWidth >= graphHeight) {
            g2d.scale(scaleWidth, scaleWidth);
        } else {
            g2d.scale(scaleHeight, scaleHeight);
        }

        g2d.setClip(0, 0, size.width, size.height);
        disableDoubleBuffering(this);
        paint(g2d);
        enableDoubleBuffering(this);

        return (PAGE_EXISTS);
    }

    /**
     * DOCUMENT ME!
     *
     * @param il DOCUMENT ME!
     */
    public void removeItemListener(ItemListener il) {
        if (itemListeners.contains(il)) {
            itemListeners.remove(il);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    boolean isFocusOnMatch() {
        return focusOnMatch;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    java.util.List getSecEdgeList() {
        return secEdgeList;
    }

    /* ------------------------------------------------------------------ */
    /* METHODEN ZUM Highlighten von Knoten und Subgraphen                 */
    /* ------------------------------------------------------------------ */
    /* Interne Methode zum Highlighten eines Subgraphen. */
    static synchronized void highlightSubgraph(DisplaySentence sentence,
        DisplayNode node, int mode) {
        if (node instanceof DisplayT_Node) {
            switch (mode) {
            case HIGHLIGHT_ON: {
                node.setHighlightedNode(true);
                node.setHighlightedEdge(true);

                break;
            }

            case HIGHLIGHT_OFF: {
                node.setHighlightedNode(false);
                node.setHighlightedEdge(false);

                break;
            }

            case MATCH_HIGHLIGHT_ON: {
                node.setMatchHighlightedNode(true);

                break;
            }

            case MATCH_HIGHLIGHT_OFF: {
                node.setMatchHighlightedNode(false);

                break;
            }
            }
        } else {
            DisplayNT_Node nt = (DisplayNT_Node) node;

            switch (mode) {
            case HIGHLIGHT_ON: {
                nt.setHighlightedNode(true);
                nt.setHighlightedEdge(true);

                break;
            }

            case HIGHLIGHT_OFF: {
                nt.setHighlightedNode(false);
                nt.setHighlightedEdge(false);

                break;
            }

            case MATCH_HIGHLIGHT_ON: {
                nt.setMatchHighlightedNode(true);
                nt.setMatchHighlightedEdges(true);

                break;
            }

            case MATCH_HIGHLIGHT_OFF: {
                nt.setMatchHighlightedNode(false);
                nt.setMatchHighlightedEdges(false);

                break;
            }
            }

            for (int i = 0; i < nt.getChildCount(); i++) {
                highlightSubgraph(sentence, nt.getChildAt(i), mode);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     */
    void notifyNodeSelected(DisplayNode node) {
        if (node instanceof T_Node) {
            if (itemListeners.size() > 0) {
                ItemEvent e = new ItemEvent(this, 0, node, ItemEvent.SELECTED);

                for (int i = 0; i < itemListeners.size(); i++) {
                    ((ItemListener) itemListeners.get(i)).itemStateChanged(e);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param g DOCUMENT ME!
     * @param windowHeight DOCUMENT ME!
     */
    private synchronized void calculateTree(Graphics g, int windowHeight) {
        if (focusOnMatch) {
            sentence.focusOnMatch();
        }

        graphDimension = GraphCalculator.calculate(sentence,
                config.getDisplayedNTFeature(header),
                config.getDisplayedTFeatures(header), g, windowHeight);

        if (sentence.isMatchSubgraphNode()) {
            highlightSubgraph(sentence,
                sentence.getDisplayNode(sentence.getMatchSubgraphNodeNumber()),
                MATCH_HIGHLIGHT_ON);
        }
    }

    /**
     * Wandelt die Zeichenflaeche in eine 2D-Zeichenflaeche um .
     *
     * @param dim DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private Graphics2D createG2D(Dimension dim) {
        Graphics2D g2d = null;

        /* Groesse des Offscreen-Bildes festlegen */
        if ((bufferedImage == null) || (bufferedImage.getWidth() != dim.width) ||
                (bufferedImage.getHeight() != dim.height)) {
            bufferedImage = (BufferedImage) createImage(dim.width, dim.height);
        }

        if (bufferedImage != null) {
            g2d = bufferedImage.createGraphics();

            //Hintergrundfarbe setzen
            g2d.setBackground(this.getBackground());

            //...und Oberflaeche wegwischen, sonst gibt es keine Farbaenderung des Graphics2D-Hintergrundes!!!
            g2d.clearRect(0, 0, dim.width, dim.height);
        }

        return g2d;
    }
}
