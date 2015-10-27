/*
 * File:     DisplayNT_Node.java
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

import ims.tiger.corpus.Feature;
import ims.tiger.corpus.Header;
import ims.tiger.corpus.NT_Node;
import ims.tiger.corpus.Node;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.util.UtilitiesCollection;

import org.jdom.Comment;
import org.jdom.Element;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;


/**
 * Diese Klasse definiert den nicht-terminalen Display-Knoten. Diese Klasse
 * stellt Methoden zur Verfuegung, damit der Knoten sich selbst zeichnet.
 */
public class DisplayNT_Node extends AbstractDisplayNode {
    /** Holds value of property DOCUMENT ME! */
    private final ArrayList children = new ArrayList();

    /** Holds value of property DOCUMENT ME! */
    private final Hashtable edgeLabelHash = new Hashtable();

    /** Holds value of property DOCUMENT ME! */
    private final NT_Node node;
    private boolean imploded;
    private int level = -1;

    /**
     * Konstruktor-Variante 2: Erzeuge Objekt, uebernimm Struktur aus T_Node.
     *
     * @param node DOCUMENT ME!
     */
    public DisplayNT_Node(NT_Node node) {
        this.node = node;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DisplayNode getChildAt(int i) {
        return (DisplayNode) children.get(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Ueberpruefen, ob Kantenlabel angeklickt wurde
     *
     * @param clickx DOCUMENT ME!
     * @param clicky DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public synchronized DisplayNode getEdgeClicked(int clickx, int clicky) {
        Enumeration e = edgeLabelHash.keys();

        while (e.hasMoreElements()) {
            DisplayNode displayNode = (DisplayNode) e.nextElement();
            Rectangle2D.Double rect = (Rectangle2D.Double) edgeLabelHash.get(displayNode);

            if (rect.contains(clickx, clicky)) {
                return displayNode;
            }
        }

        return null;
    }

    /**
     * Implodierung des Knotens.
     *
     * @param imploded DOCUMENT ME!
     */
    public void setImploded(boolean imploded) {
        this.imploded = imploded;
    }

    /**
     * Implodierung abfragen
     *
     * @return DOCUMENT ME!
     */
    public boolean isImploded() {
        return imploded;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isLeaf() {
        return imploded;
    }

    /**
     * Setzen des Levels.
     *
     * @param level DOCUMENT ME!
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Abfragen des Levels.
     *
     * @return DOCUMENT ME!
     */
    public int getLevel() {
        return level;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node getNode() {
        return node;
    }

    /* ------------------------------------------------------- */
    /* Besetzen und Abfragen der Attribute                     */
    /* ------------------------------------------------------- */

    /**
     * Abfragen der Tiger-QL-Darstellung dieses Knotens.
     *
     * @param header DOCUMENT ME!
     * @param newline DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTIGERLanguageDescription(Header header, boolean newline) {
        List names = header.getAllNTFeatureNames();
        String fname;
        String fvalue;
        String description;
        Feature feature;
        StringBuffer text = new StringBuffer("[");

        for (int j = 0; j < names.size(); j++) {
            fname = (String) names.get(j);
            fvalue = node.getFeature(fname);

            if (fvalue.equals(GraphConstants.suppressFeatureValue)) {
                continue;
            }

            feature = header.getNTFeature(fname);
            description = (feature == null) ? null
                                            : feature.getDescription(fvalue);

            if (description != null) {
                fvalue = description;
            }

            text.append(fname + "=\"" + fvalue + "\" &");

            if (newline) {
                text.append("\n");
            } else {
                text.append(" ");
            }
        }

        String tttext;
        tttext = text.toString();

        if (tttext.length() > 3) {
            tttext = tttext.substring(0, tttext.length() - 3) + "]";
        }

        return tttext;
    }

    /**
     * DOCUMENT ME!
     *
     * @param child DOCUMENT ME!
     */
    public void addChild(DisplayNode child) {
        children.add(child);
    }

    /* ------------------------------------------------------- */
    /* Zeichnen des Knotens                                    */
    /* ------------------------------------------------------- */

    /**
     * Zeichnen des Knotens und der Kanten.
     *
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param g DOCUMENT ME!
     * @param xmlNode DOCUMENT ME!
     * @param delta_y DOCUMENT ME!
     * @param id_anhaengsel DOCUMENT ME!
     */
    public synchronized void paint(Header header,
        TIGERGraphViewerConfiguration config, Graphics2D g, Element xmlNode,
        int delta_y, String id_anhaengsel) {
        if (node.isVirtualRoot() && (!GraphConstants.virtualRootNodeDisplay)) {
            return;
        }

        if (xmlNode != null) {
            xmlNode.setName("g");
            xmlNode.setAttribute("type", "nt");

            if (node.isVirtualRoot()) {
                xmlNode.setAttribute("virtualRoot", "yes");
            }

            xmlNode.setAttribute("id", node.getID() + id_anhaengsel);

            if (matchNode) {
                xmlNode.setAttribute("match", "node");
            } else if (matchHighlightedNode) {
                xmlNode.setAttribute("match", "subgraph");
            } else {
                xmlNode.setAttribute("match", "none");
            }

            xmlNode.setAttribute("stroke-width",
                (new Float(GraphConstants.lineWidth.getLineWidth()).toString()));
        }

        paintEdges(g, xmlNode, delta_y, id_anhaengsel);
        paintNode(header, config, g, xmlNode, delta_y);
    }

    /**
     * Zeichnen der Kanten.
     *
     * @param g DOCUMENT ME!
     * @param xmlNode DOCUMENT ME!
     * @param delta_y DOCUMENT ME!
     * @param id_anhaengsel DOCUMENT ME!
     */
    public synchronized void paintEdges(Graphics2D g, Element xmlNode,
        int delta_y, String id_anhaengsel) {
        boolean svg_mode = false;

        if (xmlNode != null) {
            svg_mode = true;
        }

        Color edge_color = GraphConstants.nodeColor; // normal < matched < marked < always

        if (node.isVirtualRoot() && GraphConstants.virtualRootNodeDisplay) {
            edge_color = GraphConstants.virtualRootNodeColor;
        }

        if (matchEdges) {
            edge_color = GraphConstants.nodeMatchSubgraphColor;
        }

        /* Vertikale Kanten */
        if (!imploded) {
            int childx;
            int childy;
            String label;
            FontMetrics fm;

            for (int i = 0; i < children.size(); i++) {
                // i) Kante
                DisplayNode child = (DisplayNode) children.get(i);

                childx = child.getX(); // Tochterknoten
                childy = child.getY();

                int x1 = childx;
                int y1 = childy - (GraphConstants.nodeHeight / 2);
                int x2 = childx;
                int y2 = y;

                Element xmlEdge = null;

                if (svg_mode) {
                    // Kommentar zum Ziel der Kante
                    String target_id = child.getNode().getID() + id_anhaengsel;
                    String comment = "edge \"" + node.getID() + id_anhaengsel +
                        "\" >" + child.getNode().getIncomingEdgeLabel() +
                        " \"" + target_id + "\" ";
                    xmlNode.addContent(new Comment(
                            ims.tiger.util.UtilitiesCollection.trimComment(
                                comment)));

                    // Gruppe fuer Kante, Rechteck und Label
                    xmlEdge = new Element("g");
                    xmlEdge.setAttribute("type", "edge");
                    xmlNode.addContent(xmlEdge);

                    // vertikale Linie
                    Element xmlLine = new Element("line");
                    xmlLine.setAttribute("x1", (new Integer(x1)).toString());
                    xmlLine.setAttribute("y1",
                        (new Integer(y1 - delta_y)).toString());
                    xmlLine.setAttribute("x2", (new Integer(x2)).toString());
                    xmlLine.setAttribute("y2",
                        (new Integer(y2 - delta_y)).toString());
                    xmlLine.setAttribute("stroke",
                        UtilitiesCollection.getRGBCode(edge_color));

                    xmlEdge.addContent(xmlLine);
                } else {
                    g.setColor(child.isHighlightedEdge()
                        ? GraphConstants.nodeHighlightedColor : edge_color);
                    g.drawLine(x1, y1, x2, y2);
                }

                // ii) Kantenlabel
                label = child.getNode().getIncomingEdgeLabel();

                if (label == null) {
                    continue;
                }
                 // GRUNDSAETZLICH kein Label!

                if (label.equals(GraphConstants.suppressEdgeLabel)) {
                    // HIER kein Label definiert => LABEL NICHT zeichnen!
                    continue;
                }

                // Suche passenden Font aus
                Font labelFont = UtilitiesCollection.chooseFont(GraphConstants.labelFont,
                        label);
                fm = g.getFontMetrics(labelFont);
                g.setFont(labelFont);

                int labelw = fm.stringWidth(label);

                if (labelw < GraphConstants.labelMinWidth) {
                    labelw = GraphConstants.labelMinWidth;
                }

                if (GraphConstants.labelMaxWidth > 0) { // == 0 -> unbound

                    if (labelw > GraphConstants.labelMaxWidth) {
                        labelw = GraphConstants.labelMaxWidth;

                        while (fm.stringWidth(label) > (labelw -
                                fm.stringWidth("|"))) {
                            label = label.substring(0, label.length() - 1);
                        }

                        label = label + "|";
                        labelw = fm.stringWidth(label); // labelw ist die Breite der Box, die groesser sein kann als die der Schrift
                    }
                }

                int labelh = fm.getHeight();

                Color label_color = GraphConstants.nodeColor;

                if (matchEdges) {
                    label_color = GraphConstants.nodeMatchSubgraphColor;
                }

                if (child.isHighlightedEdge()) {
                    label_color = GraphConstants.nodeHighlightedColor;
                }

                int rect_x = childx - ((labelw + 4) / 2);
                int rect_y = (y + GraphConstants.labelDistance) - labelh;
                int rect_width = labelw + 4;
                int rect_height = GraphConstants.labelHeight;

                int label_x = childx - (2 / 4) - (fm.stringWidth(label) / 2) +
                    GraphConstants.labelHorizontalCorrection;
                int label_y = (y + GraphConstants.labelDistance) -
                    (labelh / 2) + fm.getMaxDescent() +
                    GraphConstants.labelVerticalCorrection;

                if (svg_mode) {
                    // Label-Rechteck
                    Element xmlRect = new Element("rect");
                    xmlRect.setAttribute("x", (new Integer(rect_x)).toString());
                    xmlRect.setAttribute("y",
                        (new Integer(rect_y - delta_y)).toString());
                    xmlRect.setAttribute("width",
                        (new Integer(rect_width)).toString());
                    xmlRect.setAttribute("height",
                        (new Integer(rect_height)).toString());
                    xmlRect.setAttribute("fill",
                        UtilitiesCollection.getRGBCode(
                            GraphConstants.labelBackgroundColor));
                    xmlRect.setAttribute("stroke",
                        UtilitiesCollection.getRGBCode(label_color));

                    xmlEdge.addContent(xmlRect);

                    // Label-Text
                    Element xmlText = new Element("text");

                    xmlText.setAttribute("x", (new Integer(childx)).toString());
                    xmlText.setAttribute("y",
                        (new Integer(label_y - delta_y)).toString());
                    xmlText.setAttribute("text-anchor", "middle");

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
                    xmlText.setAttribute("fill",
                        UtilitiesCollection.getRGBCode(label_color));
                    xmlText.addContent(label);

                    xmlEdge.addContent(xmlText);
                } else {
                    g.setStroke(GraphConstants.lineWidth);
                    g.setPaint(GraphConstants.labelBackgroundColor);

                    Rectangle2D.Double labelquader = new Rectangle2D.Double(rect_x,
                            rect_y, rect_width, rect_height);

                    g.fill(labelquader);

                    g.setColor(label_color);
                    g.draw(labelquader);

                    // Label in der Mitte der Kante platzieren
                    g.drawString(label, label_x, label_y);

                    edgeLabelHash.put(child, labelquader);
                }
            }
        }
    }

    /**
     * Zeichnen des Knotens.
     *
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param g DOCUMENT ME!
     * @param xmlNode DOCUMENT ME!
     * @param delta_y DOCUMENT ME!
     */
    public synchronized void paintNode(Header header,
        TIGERGraphViewerConfiguration config, Graphics2D g, Element xmlNode,
        int delta_y) {
        Element xmlCommon = null;
        boolean svg_mode = false;

        if (xmlNode != null) {
            svg_mode = true;
            xmlCommon = new Element("g");
            xmlNode.addContent(xmlCommon);
        }

        String fname = config.getDisplayedNTFeature(header);
        String wert;
        FontMetrics fm;

        /* 1. Horizontale Kante */
        Color edge_color = GraphConstants.nodeColor; // normal < matched < marked < always

        if (node.isVirtualRoot() && GraphConstants.virtualRootNodeDisplay) {
            edge_color = GraphConstants.virtualRootNodeColor;
        }

        if (matchEdges) {
            edge_color = GraphConstants.nodeMatchSubgraphColor;
        }

        int x1 = getSubtreeFromX();
        int y1 = y;
        int x2 = getSubtreeToX();
        int y2 = y;

        /* a) horizontale Kante */
        if (svg_mode) {
            Element xmlLine = new Element("line");
            xmlLine.setAttribute("x1", (new Integer(x1)).toString());
            xmlLine.setAttribute("y1", (new Integer(y1 - delta_y)).toString());
            xmlLine.setAttribute("x2", (new Integer(x2)).toString());
            xmlLine.setAttribute("y2", (new Integer(y2 - delta_y)).toString());
            xmlLine.setAttribute("stroke",
                UtilitiesCollection.getRGBCode(edge_color));

            xmlCommon.addContent(xmlLine);

            // FEHLT: Y
        } else {
            if (!imploded) {
                g.setColor(edge_color);
                g.setStroke(GraphConstants.lineWidth);
                g.drawLine(x1, y1, x2, y2);

                for (int j = 0; j < children.size(); j++) {
                    DisplayNode childNode = (DisplayNode) children.get(j);

                    if (childNode.isHighlightedEdge()) {
                        g.setColor(GraphConstants.nodeHighlightedColor);
                        g.drawLine(childNode.getX(), y1, getX(), y2);
                    }
                }
            }
        }

        /* 2. Ellipse */
        int ellipse_width = getWidth() +
            (2 * GraphConstants.nodeEllipseBorder);

        Color ellipse_color = GraphConstants.nodeColor; // normal < matched < marked < always

        if (node.isVirtualRoot() && GraphConstants.virtualRootNodeDisplay) {
            ellipse_color = GraphConstants.virtualRootNodeColor;
        }

        if (matchNode) {
            ellipse_color = GraphConstants.nodeMatchColor;
        } else if (matchHighlightedNode) {
            ellipse_color = GraphConstants.nodeMatchSubgraphColor;
        }

        if (highlightedNode) {
            ellipse_color = GraphConstants.nodeHighlightedColor;
        }

        if (imploded) {
            ellipse_color = GraphConstants.nodeImplodedColor;
        }

        if (svg_mode) {
            Element xmlEllipse = new Element("ellipse");
            xmlEllipse.setAttribute("cx", (new Integer(getX())).toString());
            xmlEllipse.setAttribute("cy",
                (new Integer(getY() - delta_y)).toString());
            xmlEllipse.setAttribute("rx",
                (new Integer(ellipse_width / 2)).toString());
            xmlEllipse.setAttribute("ry",
                (new Integer(getHeight() / 2)).toString());
            xmlEllipse.setAttribute("fill",
                UtilitiesCollection.getRGBCode(
                    GraphConstants.nodeBackgroundColor));
            xmlEllipse.setAttribute("stroke",
                UtilitiesCollection.getRGBCode(ellipse_color));

            xmlCommon.addContent(xmlEllipse);

            // FEHLT: Y
        } else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

            g.setStroke(GraphConstants.ellipseLineWidth);

            g.setPaint(GraphConstants.nodeBackgroundColor);
            g.fillOval(getLeftX() - GraphConstants.nodeEllipseBorder - 1,
                getLeftY(), ellipse_width, getHeight());

            g.setColor(ellipse_color);
            g.drawOval(getLeftX() - GraphConstants.nodeEllipseBorder - 1,
                getLeftY(), ellipse_width, getHeight());

            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        /* 2. Feature-Beschriftung */
        /* a) Featurewert */
        wert = node.getFeature(fname);

        if (wert == null) {
            return;
        }

        // Waehle passenden Font aus
        Font nodeFont = UtilitiesCollection.chooseFont(GraphConstants.nodeFont,
                wert);
        g.setFont(nodeFont);
        fm = g.getFontMetrics(nodeFont);

        if (!wert.equals(GraphConstants.suppressFeatureValue)) {
            // Beschriftung unterdrückt?

            /* b) Zeichnen */
            int w = fm.stringWidth(wert);

            boolean cut = false;

            if (GraphConstants.nodeMaxWidth > 0) { // == 0 => unbound

                while (fm.stringWidth(wert) > (getWidth() +
                        fm.stringWidth("|"))) {
                    cut = true;
                    wert = wert.substring(0, wert.length() - 1);
                }
            }

            if (cut) {
                wert = wert + "|";
            }

            // neue Breite fuer den abgeschnittenen Knotennamen
            w = fm.stringWidth(wert);

            int h = fm.getHeight();

            int terminal_x = (getLeftX() + (getWidth() / 2)) - (w / 2) +
                GraphConstants.nodeHorizontalCorrection;
            int terminal_y = (getLeftY() - (getHeight() / 2)) + (h / 2) +
                fm.getAscent() + fm.getDescent() +
                GraphConstants.nodeVerticalCorrection;

            if (svg_mode) {
                Element xmlText = new Element("text");

                xmlText.setAttribute("x", (new Integer(getX())).toString()); // Mittelpunkt
                xmlText.setAttribute("y",
                    (new Integer(terminal_y - delta_y)).toString());

                xmlText.setAttribute("text-anchor", "middle");
                xmlText.setAttribute("font-family", nodeFont.getName());

                if (nodeFont.isItalic()) {
                    xmlText.setAttribute("font-style", "italic");
                } else {
                    xmlText.setAttribute("font-style", "normal");
                }

                if (nodeFont.isBold()) {
                    xmlText.setAttribute("font-weight", "bold");
                } else {
                    xmlText.setAttribute("font-weight", "normal");
                }

                xmlText.setAttribute("font-size",
                    (new Integer(nodeFont.getSize())).toString());

                xmlText.setAttribute("fill",
                    UtilitiesCollection.getRGBCode(ellipse_color));

                xmlText.addContent(wert);

                xmlCommon.addContent(xmlText);

                // FEHLT: Y
            } else {
                g.setColor(ellipse_color);
                g.drawString(wert, terminal_x, terminal_y);
            }
        }
    }
}
