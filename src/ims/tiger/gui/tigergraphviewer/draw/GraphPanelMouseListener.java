/*
 * File:     GraphPanelMouseListener.java
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

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;


/**
 * $Id: GraphPanelMouseListener.java,v 1.7 2007/01/03 15:27:43 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.7 $
 */
public class GraphPanelMouseListener implements MouseListener {
    /** Holds value of property DOCUMENT ME! */
    protected final GraphPanel graphPanel;

    /** Holds value of property DOCUMENT ME! */
    protected DisplaySentence displaySentence;

    /** Holds value of property DOCUMENT ME! */
    protected final Toolkit toolkit = Toolkit.getDefaultToolkit();

    /**
     * Creates a new GraphPanelMouseListener object.
     *
     * @param graphPanel DOCUMENT ME!
     */
    public GraphPanelMouseListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    /**
     * Anklicken der Maus = Markieren eines Knotens oder Subgraphen.
     *
     * @param e DOCUMENT ME!
     */
    public synchronized void mouseClicked(MouseEvent e) {
        displaySentence = graphPanel.getCurrentDisplaySentence();

        if (displaySentence == null) {
            return;
        }

        int clickedNodeNr = getClickedNodeNr(e.getX(), e.getY());

        if (clickedNodeNr > -1) {
            handleNodeClick(e, clickedNodeNr);
        } else {
            DisplayNode edgeNode = getClickedEdge(e.getX(), e.getY());

            if (edgeNode != null) {
                handleEdgeClick(e, edgeNode);
            } else {
                int secEdge = getClickedSecEdge(e.getX(), e.getY());

                if (secEdge != -1) {
                    handleSecEdgeClick(e, secEdge);
                } else {
                    reset();
                }
            }

            // Click ausserhalb -> evtl. Markierungen aufheben
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseExited(MouseEvent e) {
        ToolTipManager.sharedInstance().setEnabled(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mousePressed(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param clickedEdgeChild DOCUMENT ME!
     */
    protected void handleEdgeClick(MouseEvent e, DisplayNode clickedEdgeChild) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param clickedNodeNr DOCUMENT ME!
     */
    protected void handleNodeClick(MouseEvent e, int clickedNodeNr) {
        DisplayNode node = displaySentence.getDisplayNode(clickedNodeNr);

        // Alle Markierungen rueckgaengig machen
        graphPanel.normalizeHighlights();

        // Knoten mit der rechten Taste markieren
        // Subgraph mit der linken Taste markieren
        if (SwingUtilities.isRightMouseButton(e)) {
            graphPanel.setHighlightedNode(node);
            node.setHighlightedNode(true);
            graphPanel.repaint();
        } else if (SwingUtilities.isLeftMouseButton(e)) {
            GraphPanel.highlightSubgraph(displaySentence, node,
                GraphPanel.HIGHLIGHT_ON);
            graphPanel.setHighlightedSubgraphRootNode(node);
            graphPanel.repaint();
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            if (node instanceof DisplayNT_Node) {
                // Test der mittleren Taste ==> EXPLODE
                if (((DisplayNT_Node) node).isImploded()) {
                    displaySentence.deleteImplodedNode((DisplayNT_Node) node);
                    graphPanel.update();
                    graphPanel.repaint();
                } else if (((!graphPanel.isFocusOnMatch()) &&
                        (clickedNodeNr == displaySentence.getDisplay_Root())) ||
                        ((graphPanel.isFocusOnMatch()) &&
                        (clickedNodeNr == displaySentence.getMatchSubgraphNodeNumber()))) {
                    toolkit.beep();
                } else {
                    displaySentence.addImplodedNode((DisplayNT_Node) node);
                    graphPanel.update();
                    graphPanel.repaint();
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param i DOCUMENT ME!
     */
    protected void handleSecEdgeClick(MouseEvent e, int i) {
    }

    /**
     * DOCUMENT ME!
     */
    protected void reset() {
        graphPanel.normalizeHighlights();
        graphPanel.repaint();
    }

    private DisplayNode getClickedEdge(int x, int y) {
        DisplaySentence sentence = graphPanel.getCurrentDisplaySentence();
        DisplayNT_Node node;

        for (int i = 0; i < sentence.getDisplayInnerNodeSize(); i++) {
            node = sentence.getDisplayInnerNode(i);

            if (node.isVisible()) {
                DisplayNode edgeNode = node.getEdgeClicked(x, y);

                if (edgeNode != null) {
                    return edgeNode;
                }
            }
        }

        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int getClickedNodeNr(int x, int y) {
        DisplayNode node;

        for (int i = 0; i < displaySentence.getDisplayLeafNodeSize(); i++) {
            node = displaySentence.getDisplayLeafNode(i);

            if (node.isVisible()) {
                if (node.checkClicked(x, y)) {
                    return i;
                }
            }
        }

        for (int i = 0; i < displaySentence.getDisplayInnerNodeSize(); i++) {
            node = displaySentence.getDisplayInnerNode(i);

            if (node.isVisible()) {
                if (((DisplayNT_Node) node).checkClicked(x, y)) {
                    return ims.tiger.system.Constants.CUT + i;
                }
            }
        }

        return -1;
    }

    private int getClickedSecEdge(int x, int y) {
        for (int r = 0; r < graphPanel.getSecEdgeList().size(); r++) {
            Rectangle2D.Double rect = (Rectangle2D.Double) graphPanel.getSecEdgeList()
                                                                     .get(r);

            if (rect.contains(x, y)) {
                return r;
            }
        }

        return -1;
    }
}
