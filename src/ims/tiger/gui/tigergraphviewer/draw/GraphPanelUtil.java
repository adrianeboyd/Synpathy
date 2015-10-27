/*
 * File:     GraphPanelUtil.java
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

import ims.tiger.corpus.Header;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.util.UtilitiesCollection;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.List;


/**
 * $Id: GraphPanelUtil.java,v 1.9 2007/01/03 15:24:51 klasal Exp $  $Author:
 * klasal $ $Revision: 1.9 $
 */
public class GraphPanelUtil {
    /**
     * DOCUMENT ME!
     *
     * @param sentence
     * @param header
     * @param config DOCUMENT ME!
     * @param g
     *
     * @return hashtable with coordinates (in form of rectangle-objects) as
     *         keys and secondary edge labels as values
     */
    public static List paintSentence(DisplaySentence sentence, Header header,
        TIGERGraphViewerConfiguration config, Graphics2D g) {
        if ((sentence != null) && sentence.isGraphCorrect()) {
            /* 1. Terminalknoten zeichnen */
            DisplayNode tnode;

            for (int i = 0; i < sentence.getDisplayLeafNodeSize(); i++) {
                tnode = sentence.getDisplayLeafNode(i);

                if (tnode.isVisible()) {
                    tnode.paint(header, config, g, null, 0, null);
                }
            }

            /* 2. Nonterminalknoten zeichnen */
            DisplayNT_Node ntnode;

            for (int i = 0; i < sentence.getDisplayInnerNodeSize(); i++) {
                ntnode = sentence.getDisplayInnerNode(i);

                if (ntnode.isVisible()) {
                    ntnode.paint(header, config, g, null, 0, null);
                }
            }

            /* 3. Sekundaere Kanten zeichnen */
            if ((GraphConstants.secondaryEdgeDisplay) &&
                    (sentence.getSentence().getCoreferenceSize() > 0)) {
                return paintSecondaryEdges(sentence, g);
            }
        }

        return new ArrayList();
    }

    static Point getPreferredEndPoint(DisplayNode dnode1, DisplayNode dnode2) {
        Point point2 = new Point(0, 0);

        if (dnode2.isLeaf()) {
            point2.x = dnode2.getX();
        } // mittig
        else {
            if (dnode1.getY() == dnode2.getY()) {
                point2.x = dnode2.getX();
            } // mittig
            else {
                if (dnode2.getX() < dnode1.getX()) {
                    point2.x = dnode2.getX() + (dnode2.getWidth() / 2) +
                        GraphConstants.nodeEllipseBorder;

                    //rechts
                } else if (dnode2.getX() > dnode1.getX()) {
                    point2.x = dnode2.getX() - (dnode2.getWidth() / 2) -
                        GraphConstants.nodeEllipseBorder;

                    // links
                } else {
                    point2.x = dnode2.getX();
                }
                 // mittig
            }
        }

        if (dnode2.isLeaf()) {
            point2.y = dnode2.getY() - (GraphConstants.nodeHeight / 2);
        } // oben
        else {
            if (dnode1.getX() == dnode2.getX()) { // Halbbogen

                if (dnode1.getY() < dnode2.getY()) {
                    point2.y = dnode2.getY() - (dnode2.getHeight() / 2); // oben
                } else {
                    point2.y = dnode2.getY() + (dnode2.getHeight() / 2); // unten
                }
            } else if (dnode1.getY() == dnode2.getY()) {
                point2.y = dnode2.getY() - (dnode2.getHeight() / 2); // oben
            } else {
                point2.y = dnode2.getY();
            }
             // mittig
        }

        return point2;
    }

    static Point getPreferredStartingPoint(DisplayNode dnode1,
        DisplayNode dnode2) {
        Point point1 = new Point(0, 0);

        point1.x = dnode1.getX(); // mittig

        if (dnode1.isLeaf()) {
            point1.y = dnode1.getY() - (GraphConstants.nodeHeight / 2);
        } // oben
        else {
            if (dnode1.getY() >= dnode2.getY()) { // y1 ist unterer
                point1.y = dnode1.getY() - (dnode1.getHeight() / 2);
            } // oben
            else {
                point1.y = dnode1.getY() + (dnode1.getHeight() / 2);
            }
             // unten
        }

        return point1;
    }

    static Rectangle2D addLabelToArc(Graphics2D big, Float labelCentre,
        String label, FontMetrics fm, Dimension labelSize) {
        big.setStroke(GraphConstants.lineWidth);

        Rectangle2D.Double labelRectangle = new Rectangle2D.Double(labelCentre.x -
                (labelSize.width / 2), labelCentre.y - (labelSize.height / 2),
                labelSize.width, GraphConstants.labelHeight);

        big.setPaint(GraphConstants.labelBackgroundColor);
        big.fill(labelRectangle);

        big.setColor(GraphConstants.secondaryEdgeColor);
        big.draw(labelRectangle);
        big.drawString(label,
            labelCentre.x - (fm.stringWidth(label) / 2) +
            GraphConstants.labelHorizontalCorrection,
            labelCentre.y + 1 + fm.getMaxDescent() +
            GraphConstants.labelVerticalCorrection);

        return labelRectangle;
    }

    static void calculateArc(Point startingPoint, Point endPoint,
        Point rectCorner, Dimension delta, Float labelCentre, int[] angles) {
        if ((delta.width == 0) || (delta.height == 0)) { // Halbbogen bei gleichem x oder y

            if (delta.height == 0) {
                delta.height = -GraphConstants.treeLevelYOffset / 2;
                rectCorner.y = startingPoint.y + delta.height;

                if (delta.width > 0) {
                    rectCorner.x = startingPoint.x;
                } else {
                    rectCorner.x = endPoint.x;
                }

                delta.width /= 2;
                angles[0] = 0;
                angles[1] = 180;
                labelCentre.x = (2 * delta.width * 0.75f) + startingPoint.x; // (1+|cos t|)/2, t=120
                labelCentre.y = (delta.height * (float) Math.sin((Math.PI * 2) / 3)) +
                    startingPoint.y; // sin t, t=120
            }

            if (delta.width == 0) {
                delta.width = -GraphConstants.leafGapX * 2;
                rectCorner.x = startingPoint.x + delta.width;

                if (delta.height > 0) {
                    rectCorner.y = startingPoint.y;
                } else {
                    rectCorner.y = endPoint.y;
                }

                delta.height /= 2;
                angles[0] = 90;
                angles[1] = 180;
                labelCentre.x = rectCorner.x;
                labelCentre.y = startingPoint.y +
                    ((float) 2 * (float) delta.height * (float) 0.5);
            }
        } else { // Viertelbogen

            if ((delta.width > 0) && (delta.height < 0)) {
                rectCorner.x = startingPoint.x;
                rectCorner.y = endPoint.y;
                angles[0] = 90;
            }

            if ((delta.width > 0) && (delta.height > 0)) {
                rectCorner.x = startingPoint.x;
                rectCorner.y = startingPoint.y - delta.height;
                angles[0] = 180;
            }

            if ((delta.width < 0) && (delta.height > 0)) {
                rectCorner.x = endPoint.x + delta.width;
                rectCorner.y = startingPoint.y - delta.height;
                angles[0] = 270;
            }

            if ((delta.width < 0) && (delta.height < 0)) {
                rectCorner.x = endPoint.x + delta.width;
                rectCorner.y = endPoint.y;
                angles[0] = 0;
            }

            angles[1] = 90;
            labelCentre.x = (delta.width * 0.5f) + startingPoint.x; // cos t, t=60
            labelCentre.y = (delta.height * (float) Math.sin((Math.PI * 2) / 3)) +
                startingPoint.y; // sin t, t=60
        }
    }

    static String fitLabelIntoLabelBox(String label, FontMetrics fm,
        Dimension labelSize) {
        if (labelSize.width < GraphConstants.labelMinWidth) {
            labelSize.width = GraphConstants.labelMinWidth;
        }

        if (GraphConstants.labelMaxWidth > 0) { // == 0 -> unbound

            if (labelSize.width > GraphConstants.labelMaxWidth) {
                labelSize.width = GraphConstants.labelMaxWidth;

                while (fm.stringWidth(label) > (labelSize.width -
                        fm.stringWidth("|"))) {
                    label = label.substring(0, label.length() - 1);
                }

                label = label + "|";
                labelSize.width = fm.stringWidth(label);
            }
        }

        labelSize.width += 4;

        return label;
    }

    /**
     * Zeichnet (optional) die sekundaeren Kanten als Ellipsenbahnen.
     *
     * @param sentence DOCUMENT ME!
     * @param big DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    static List paintSecondaryEdges(DisplaySentence sentence, Graphics2D big) {
        List secEdgeList = new ArrayList();

        if (!GraphConstants.secondaryEdgeDisplay) {
            return secEdgeList;
        }

        for (int i = 0; i < sentence.getSentence().getCoreferenceSize(); i++) {
            // Display-Knoten holen
            DisplayNode dnode1 = sentence.getDisplayNode(sentence.getSentence()
                                                                 .getCoreferenceNode1(i));
            DisplayNode dnode2 = sentence.getDisplayNode(sentence.getSentence()
                                                                 .getCoreferenceNode2(i));

            if ((!dnode1.isVisible()) || (!dnode2.isVisible())) {
                continue;
            }

            // Wunschpositionen fuer Anfang/Ende des Pfeils
            Point startingPoint = getPreferredStartingPoint(dnode1, dnode2);
            Point endPoint = getPreferredEndPoint(dnode1, dnode2);

            // Pfeilkurve als Ellipsenbogen
            Dimension delta = new Dimension(endPoint.x - startingPoint.x,
                    endPoint.y - startingPoint.y);

            // Linke obere Ecke des die Ellipse umgebenden Rechtecks
            Point rectCorner = new Point(0, 0);

            //Centre of Label
            Float labelCentre = new Float();

            int[] angles = new int[2];

            calculateArc(startingPoint, endPoint, rectCorner, delta,
                labelCentre, angles);

            int startAngle = angles[0];
            int arcAngle = angles[1];

            Color color = GraphConstants.secondaryEdgeColor;

            if (sentence.isHighlightedSecEdge(i)) {
                color = color.darker();
            }

            big.setColor(color);
            big.setStroke(GraphConstants.lineWidth);

            // Rechteck oben links: (x1,y1) -- Hoehe, Breite (h,b) -- Winkel: Start, Gap
            big.drawArc(rectCorner.x + 1, rectCorner.y,
                2 * Math.abs(delta.width), 2 * Math.abs(delta.height),
                startAngle, arcAngle);

            // x1+1, da sonst Kanten uebereinander			
            // Pfeilspitze
            // For CGN not drawn since corpus is not consistent with respect to orientation A.K.
            Point arrowHead1 = new Point();
            Point arrowHead2 = new Point();

            if (startingPoint.y == endPoint.y) { //  unten
                arrowHead1.x = endPoint.x - 3;
                arrowHead1.y = endPoint.y - 5;
                arrowHead2.x = endPoint.x + 3;
                arrowHead2.y = endPoint.y - 5;
            } else if (startingPoint.x == endPoint.x) { // rechts
                arrowHead1.x = endPoint.x - 5;
                arrowHead1.y = endPoint.y - 3;
                arrowHead2.x = endPoint.x - 5;
                arrowHead2.y = endPoint.y + 3;
            } else if (startingPoint.x < endPoint.x) { // rechts
                arrowHead1.x = endPoint.x - 5;
                arrowHead1.y = endPoint.y - 3;
                arrowHead2.x = endPoint.x - 5;
                arrowHead2.y = endPoint.y + 3;
            } else { // links
                arrowHead1.x = endPoint.x + 5;
                arrowHead1.y = endPoint.y - 3;
                arrowHead2.x = endPoint.x + 5;
                arrowHead2.y = endPoint.y + 3;
            }

            big.setStroke(GraphConstants.arrowWidth);
            big.drawLine(endPoint.x, endPoint.y, arrowHead1.x, arrowHead1.y);
            big.drawLine(endPoint.x, endPoint.y, arrowHead2.x, arrowHead2.y);

            // Kantenlabel
            String label = sentence.getSentence().getCoreferenceLabel(i);

            Font labelFont = UtilitiesCollection.chooseFont(GraphConstants.labelFont,
                    label);
            big.setFont(labelFont);

            FontMetrics fm = big.getFontMetrics(labelFont);

            if (!label.equals(GraphConstants.suppressEdgeLabel)) {
                Dimension labelSize = new Dimension(fm.stringWidth(label),
                        fm.getHeight());

                label = fitLabelIntoLabelBox(label, fm, labelSize);

                Rectangle2D labelRectangle = addLabelToArc(big, labelCentre,
                        label, fm, labelSize);
                secEdgeList.add(labelRectangle);
            }
        }
         // for

        return secEdgeList;
    }
}
