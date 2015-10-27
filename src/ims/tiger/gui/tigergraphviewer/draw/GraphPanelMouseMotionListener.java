/*
 * File:     GraphPanelMouseMotionListener.java
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

package ims.tiger.gui.tigergraphviewer.draw;

import ims.tiger.corpus.Feature;

import ims.tiger.util.UtilitiesCollection;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;


/**
 * $Id: GraphPanelMouseMotionListener.java,v 1.6 2007/01/03 14:21:54 klasal Exp $
 *
 * @author $author$
 * @version $Revision: 1.6 $
 */
public class GraphPanelMouseMotionListener implements MouseMotionListener {
    private GraphPanel graphPanel;

    /**
     * Creates a new GraphPanelMouseMotionListener object.
     *
     * @param graphPanel DOCUMENT ME!
     */
    public GraphPanelMouseMotionListener(GraphPanel graphPanel) {
        this.graphPanel = graphPanel;
    }

    /**
     *
     *
     * @param e DOCUMENT ME!
     */
    public void mouseDragged(MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /**
     * Mausbewegung = ToolTip aktualisieren
     *
     * @param e DOCUMENT ME!
     */
    public synchronized void mouseMoved(MouseEvent e) {
        DisplaySentence sentence = graphPanel.getCurrentDisplaySentence();

        if (sentence == null) {
            ToolTipManager.sharedInstance().setEnabled(false);

            return;
        }
         // Mausbewegung waehrend des Satz-Wechsels !!!

        DisplayNode displayLeafNode;
        String tttext;

        for (int i = 0; i < sentence.getDisplayLeafNodeSize(); i++) {
            displayLeafNode = sentence.getDisplayLeafNode(i);

            int x = e.getX();
            int y = e.getY();

            if (displayLeafNode.checkClicked(x, y)) {
                ToolTipManager.sharedInstance().setEnabled(true);

                tttext = displayLeafNode.getTIGERLanguageDescription(graphPanel.getHeader(),
                        true);

                if (displayLeafNode instanceof DisplayNT_Node) { //imploded node
                    tttext = "Imploded node: " + tttext;
                }

                UIManager.put("ToolTip.foreground",
                    new ColorUIResource(Color.black));
                UIManager.put("ToolTip.background",
                    new ColorUIResource(new Color(220, 220, 255)));

                Font tooltipSelected = UtilitiesCollection.chooseFont(GraphConstants.tooltipFont,
                        tttext);

                UIManager.getDefaults().put("ToolTip.font", tooltipSelected);

                graphPanel.setToolTipText(tttext);

                // Setze auf Default-Font zurueck (fuer Buttons usw.)
                UIManager.getDefaults().put("ToolTip.font",
                    GraphConstants.tooltipFont[0]);

                return;
            }
        }

        DisplayNT_Node displayInnerNode;

        for (int i = 0; i < sentence.getDisplayInnerNodeSize(); i++) {
            displayInnerNode = sentence.getDisplayInnerNode(i);

            int x = e.getX();
            int y = e.getY();

            if (displayInnerNode.checkClicked(x, y)) {
                ToolTipManager.sharedInstance().setEnabled(true);
                tttext = displayInnerNode.getTIGERLanguageDescription(graphPanel.getHeader(),
                        true);

                UIManager.put("ToolTip.foreground",
                    new ColorUIResource(Color.black));
                UIManager.put("ToolTip.background",
                    new ColorUIResource(new Color(220, 220, 255)));

                Font tooltipSelected = UtilitiesCollection.chooseFont(GraphConstants.tooltipFont,
                        tttext);

                UIManager.getDefaults().put("ToolTip.font", tooltipSelected);

                graphPanel.setToolTipText(tttext);

                // Setze auf Default-Font zurueck (fuer Buttons usw.)
                UIManager.getDefaults().put("ToolTip.font",
                    GraphConstants.tooltipFont[0]);

                return;
            }

            DisplayNode displayNode = displayInnerNode.getEdgeClicked(x, y);

            if (displayNode != null) {
                ToolTipManager.sharedInstance().setEnabled(true);

                String fvalue = displayNode.getNode().getIncomingEdgeLabel();

                Feature feature = graphPanel.getHeader().getEdgeFeature();
                String description = (feature == null) ? null
                                                       : feature.getDescription(fvalue);

                if (description != null) {
                    fvalue = description;
                }

                tttext = ("[" + ims.tiger.system.Constants.EDGE + "=\"" +
                    fvalue + "\"]");
                UIManager.put("ToolTip.foreground",
                    new ColorUIResource(Color.black));
                UIManager.put("ToolTip.background",
                    new ColorUIResource(new Color(220, 220, 255)));

                Font tooltipSelected = UtilitiesCollection.chooseFont(GraphConstants.tooltipFont,
                        tttext);

                UIManager.getDefaults().put("ToolTip.font", tooltipSelected);
                graphPanel.setToolTipText(tttext);
                UIManager.getDefaults().put("ToolTip.font",
                    GraphConstants.tooltipFont[0]);

                return;
            }

            for (int r = 0; r < graphPanel.getSecEdgeList().size(); r++) {
                Rectangle2D.Double rect = (Rectangle2D.Double) graphPanel.getSecEdgeList()
                                                                         .get(r);

                if (rect.contains(x, y)) {
                    ToolTipManager.sharedInstance().setEnabled(true);

                    String fvalue = sentence.getSentence().getCoreferenceLabel(r);

                    Feature feature = graphPanel.getHeader().getSecEdgeFeature();
                    String description = (feature == null) ? null
                                                           : feature.getDescription(fvalue);

                    if (description != null) {
                        fvalue = description;
                    }

                    tttext = ("[" + ims.tiger.system.Constants.SECEDGE + "=\"" +
                        fvalue + "\"]");
                    UIManager.put("ToolTip.foreground",
                        new ColorUIResource(Color.black));
                    UIManager.put("ToolTip.background",
                        new ColorUIResource(new Color(220, 220, 255)));

                    Font tooltipSelected = UtilitiesCollection.chooseFont(GraphConstants.tooltipFont,
                            tttext);

                    UIManager.getDefaults().put("ToolTip.font", tooltipSelected);
                    graphPanel.setToolTipText(tttext);
                    UIManager.getDefaults().put("ToolTip.font",
                        GraphConstants.tooltipFont[0]);

                    return;
                }
            }
        }

        //tttext = "";
        //component.setToolTipText(tttext);
        ToolTipManager.sharedInstance().setEnabled(false);
    }
}
