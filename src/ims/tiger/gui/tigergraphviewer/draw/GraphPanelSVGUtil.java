/*
 * File:     GraphPanelSVGUtil.java
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

import ims.tiger.corpus.Header;
import ims.tiger.corpus.Sentence;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.system.Constants;

import ims.tiger.util.UtilitiesCollection;

import org.jdom.Comment;
import org.jdom.Element;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D.Float;


/**
 * DOCUMENT ME!
 * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
 * @author $Author: hasloe $
 * @version $Revision: 1.3 $
 */
public class GraphPanelSVGUtil {
    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param big DOCUMENT ME!
     * @param secedges DOCUMENT ME!
     * @param delta_y DOCUMENT ME!
     * @param id_anhaengsel DOCUMENT ME!
     */
    public static void paintSecondaryEdges(DisplaySentence sentence,
        Graphics2D big, Element secedges, int delta_y, String id_anhaengsel) {
        if (!GraphConstants.secondaryEdgeDisplay) {
            return;
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
            Point startingPoint = GraphPanelUtil.getPreferredStartingPoint(dnode1,
                    dnode2);
            Point endPoint = GraphPanelUtil.getPreferredEndPoint(dnode1, dnode2);

            // Pfeilkurve als Ellipsenbogen
            Dimension delta = new Dimension(endPoint.x - startingPoint.x,
                    endPoint.y - startingPoint.y);

            // Linke obere Ecke des die Ellipse umgebenden Rechtecks
            Point rectCorner = new Point(0, 0);

            //Centre of Label
            Float labelCentre = new Float();

            //not used in SVG
            int[] angles = new int[2];

            GraphPanelUtil.calculateArc(startingPoint, endPoint, rectCorner,
                delta, labelCentre, angles);

            String label = sentence.getSentence().getCoreferenceLabel(i);
            addNodeComment(secedges, id_anhaengsel, label, dnode1, dnode2);

            Element secedge = new Element("g");
            secedges.addContent(secedge);

            addArcToSVG(delta_y, secedge, startingPoint, endPoint, delta);

            // Kantenlabel
            Font labelFont = UtilitiesCollection.chooseFont(GraphConstants.labelFont,
                    label);
            big.setFont(labelFont);

            FontMetrics fm = big.getFontMetrics(labelFont);

            if (label.equals(GraphConstants.suppressEdgeLabel)) {
                continue;
            }
             // Label leer!

            Dimension labelSize = new Dimension(fm.stringWidth(label),
                    fm.getHeight());
            label = GraphPanelUtil.fitLabelIntoLabelBox(label, fm, labelSize);

            addLabelToSVG(delta_y, label, secedge, labelCentre, labelFont, fm,
                labelSize);
        }
         // for		
    }

    private static void addNodeComment(Element secedges, String id_anhaengsel,
        String label, DisplayNode dnode1, DisplayNode dnode2) {
        String clabel = label;

        if (label.equals(GraphConstants.suppressEdgeLabel)) {
            clabel = "";
        }
         // Label leer!

        String comment = " secondary edge " + "\"" + dnode1.getNode().getID() +
            id_anhaengsel + "\" >~" + clabel + " \"" +
            dnode2.getNode().getID() + id_anhaengsel + "\" ";
        secedges.addContent(new Comment(
                ims.tiger.util.UtilitiesCollection.trimComment(comment)));
    }

    private static void addArcToSVG(int delta_y, Element secedge, Point point1,
        Point point2, Dimension delta) {
        Element xml_path = new Element("path");

        StringBuffer path = new StringBuffer();

        // move to (x1|y1)
        path.append("M ");
        path.append(point1.x);
        path.append(" ");
        path.append(point1.y - delta_y);
        path.append(" ");

        // elliptical arc: a rx ry 0 1 1 x2 y2
        if ((delta.width != 0) && (delta.height != 0)) {
            path.append("a ");
            path.append(Math.abs(delta.width));
            path.append(" ");
            path.append(Math.abs(delta.height));
            path.append(" ");

            if (delta.height > 0) {
                if (delta.width > 0) {
                    path.append("0 0 0 ");
                } else {
                    path.append("0 0 1 ");
                }
            } else {
                if (delta.width > 0) {
                    path.append("0 0 1 ");
                } else {
                    path.append("0 0 0 ");
                }
            }

            path.append(delta.width);
            path.append(" ");
            path.append(delta.height);
        } else { // Halbbogen => groessere Radien
            path.append("A ");
            path.append(Math.abs(delta.width));
            path.append(" ");
            path.append(Math.abs(delta.height));
            path.append(" ");

            if (delta.height == 0) {
                if (delta.width > 0) {
                    path.append("0 0 1 ");
                } else {
                    path.append("0 0 0 ");
                }
            } else if (delta.width == 0) {
                if (delta.height > 0) {
                    path.append("0 0 0 ");
                } else {
                    path.append("0 0 1 ");
                }
            }

            path.append(point2.x);
            path.append(" ");
            path.append(point2.y - delta_y);
        }

        xml_path.setAttribute("d", path.toString());

        secedge.addContent(xml_path);
    }

    private static void addLabelToSVG(int delta_y, String label,
        Element secedge, Float labelCentre, Font labelFont, FontMetrics fm,
        Dimension labelSize) {
        Element xmlrect = new Element("rect");
        xmlrect.setAttribute("x",
            (new Integer(Math.round(labelCentre.x - (labelSize.width / 2)))).toString());
        xmlrect.setAttribute("y",
            (new Integer(Math.round(labelCentre.y - (labelSize.height / 2) -
                    delta_y))).toString());
        xmlrect.setAttribute("width", (new Integer(labelSize.width)).toString());
        xmlrect.setAttribute("height",
            (new Integer(GraphConstants.labelHeight)).toString());
        xmlrect.setAttribute("fill",
            UtilitiesCollection.getRGBCode(GraphConstants.labelBackgroundColor));
        secedge.addContent(xmlrect);

        Element xmlText = new Element("text");
        xmlText.setAttribute("x",
            (new Integer(Math.round(labelCentre.x))).toString()); // Mittelpunkt
        xmlText.setAttribute("y",
            (new Integer(Math.round((labelCentre.y + 1 + fm.getMaxDescent() +
                    GraphConstants.labelVerticalCorrection) - delta_y))).toString());
        xmlText.setAttribute("text-anchor", "middle");
        xmlText.setAttribute("fill",
            UtilitiesCollection.getRGBCode(GraphConstants.secondaryEdgeColor));
        xmlText.setAttribute("stroke", "none");

        xmlText.setAttribute("font-family", labelFont.getName());

        if (labelFont.isItalic()) {
            xmlText.setAttribute("font-style", "italic");
        } else {
            xmlText.setAttribute("font-style", "normal");
        }

        if (labelFont.isBold()) {
            xmlText.setAttribute("font-weight", "bold");
        } else {
            xmlText.setAttribute("font-weight", "normal");
        }

        xmlText.setAttribute("font-size",
            (new Integer(labelFont.getSize())).toString());
        xmlText.setText(label);

        secedge.addContent(xmlText);
    }

    /** Zeichnet den Graphen im SVG-Format. Liefert ein <svg>-Wurzelelement
     *  zurueck. Man beachte, dass die y-Position ALLER Primitive hochgezogen
     *  wird, falls das aeussere Panel zu gross ist.
     */
    public static Element paintSVG(Header header,
        TIGERGraphViewerConfiguration config, DisplaySentence sentence,
        Graphics2D big, Dimension graphDimension, int delta_y,
        String id_anhaengsel, boolean showSecondaryEdges) {
        if (sentence == null) {
            return null;
        }

        // <svg width="" height="">
        Element svgroot = new Element("svg");
        svgroot.setAttribute("width", "" + graphDimension.width);
        svgroot.setAttribute("height", "" + graphDimension.height);

        //   <g type="sentence">
        Element sentroot = new Element("g");
        sentroot.setAttribute("type", "sentence");
        sentroot.setAttribute("id",
            sentence.getSentence().getSentenceID() + id_anhaengsel);
        svgroot.addContent("\n  ");
        svgroot.addContent(sentroot);

        /* 1. Hintergrundfarbe erzeugen */

        //       <g type="bgcolor">
        Element bgcolor = new Element("g");
        bgcolor.setAttribute("type", "bgcolor");
        sentroot.addContent(bgcolor);

        //         <!-- create background color -->
        bgcolor.addContent(new Comment(" create background color "));

        //         <rect ... />
        Element rect = new Element("rect");
        rect.setAttribute("x", "0");
        rect.setAttribute("y", "0");
        rect.setAttribute("width", "" + graphDimension.width);
        rect.setAttribute("height", "" + graphDimension.height);
        rect.setAttribute("fill",
            UtilitiesCollection.getRGBCode(GraphConstants.panelBackgroundColor));

        //       </g>
        bgcolor.addContent(rect);

        /* 1. Terminalknoten zeichnen */
        DisplayNode tnode;

        for (int i = 0; i < sentence.getDisplayLeafNodeSize(); i++) {
            tnode = sentence.getDisplayLeafNode(i);

            if (tnode.isVisible()) {
                sentroot.addContent("\n    ");

                //  <!-- terminal node ... -->
                String comment = " terminal node " + "\"" +
                    tnode.getNode().getID() + id_anhaengsel + "\":" +
                    tnode.getTIGERLanguageDescription(header, false) + " ";
                sentroot.addContent(new Comment(
                        ims.tiger.util.UtilitiesCollection.trimComment(comment)));

                //  <g type="t" ... >
                Element xmlNode = new Element("g");
                tnode.paint(header, config, big, xmlNode, delta_y, id_anhaengsel);
                sentroot.addContent(xmlNode);

                //  </g>
            }
        }

        /* 2. Nonterminalknoten zeichnen */
        DisplayNT_Node ntnode;

        for (int i = 0; i < sentence.getDisplayInnerNodeSize(); i++) {
            ntnode = sentence.getDisplayInnerNode(i);

            if (ntnode.isVisible()) {
                sentroot.addContent("\n    ");

                //  <!-- nonterminal node ... -->
                String comment = " nonterminal node " + "\"" +
                    ntnode.getNode().getID() + id_anhaengsel + "\":" +
                    ntnode.getTIGERLanguageDescription(header, false) + " ";
                sentroot.addContent(new Comment(
                        ims.tiger.util.UtilitiesCollection.trimComment(comment)));

                //  <g type="nt">
                Element xmlNode = new Element("g");
                ntnode.paint(header, config, big, xmlNode, delta_y,
                    id_anhaengsel);
                sentroot.addContent(xmlNode);

                //  </g>
            }
        }

        /* 3. Sekundaere Kanten zeichnen */
        if ((showSecondaryEdges) &&
                (sentence.getSentence().getCoreferenceSize() > 0)) {
            sentroot.addContent("\n    ");
            sentroot.addContent(new Comment(" secondary edges "));

            Element secedges = new Element("g");
            secedges.setAttribute("type", "secedges");
            secedges.setAttribute("fill", "none");
            secedges.setAttribute("stroke",
                UtilitiesCollection.getRGBCode(
                    GraphConstants.secondaryEdgeColor));
            secedges.setAttribute("stroke-width",
                (new java.lang.Float(GraphConstants.lineWidth.getLineWidth()).toString()));

            paintSecondaryEdges(sentence, big, secedges, delta_y, id_anhaengsel);
            sentroot.addContent(secedges);
        }

        // </svg>
        return svgroot;
    }
}
