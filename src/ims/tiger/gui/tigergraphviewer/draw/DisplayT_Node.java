/*
 * File:     DisplayT_Node.java
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
import ims.tiger.corpus.Node;
import ims.tiger.corpus.T_Node;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.util.UtilitiesCollection;

import org.jdom.Element;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.List;


/**
 * Diese Klasse definiert den terminalen Display Knoten. Diese Klasse stellt
 * Methoden zur Verfuegung, damit der Knoten sich selbst zeichnet.
 */
public class DisplayT_Node extends AbstractDisplayNode {
    /** Holds value of property DOCUMENT ME! */
    private final T_Node node;

    /**
     * Konstruktor-Variante 2: Erzeuge Objekt, uebernimm Struktur aus T_Node.
     *
     * @param t DOCUMENT ME!
     */
    public DisplayT_Node(T_Node t) {
        this.node = t;
    }

    /* ------------------------------------------------------- */
    /* Besetzen und Abfragen der Attribute                     */
    /* ------------------------------------------------------- */
    public boolean isLeaf() {
        return true;
    }

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    public int getLevel() {
        return 0;
    }

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    public Node getNode() {
        return node;
    }

    /**
     * Abfragen der Tiger-QL-Darstellung dieses Knotens.
     *
     * @param header DOCUMENT ME!
     * @param newline DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTIGERLanguageDescription(Header header, boolean newline) {
        StringBuffer text = new StringBuffer("[");
        List names = header.getAllTFeatureNames();
        String fname;
        String fvalue;
        String description;
        Feature feature;

        for (int j = 0; j < names.size(); j++) {
            fname = (String) names.get(j);
            fvalue = node.getFeature(fname);

            if ((fvalue == null) ||
                    fvalue.equals(GraphConstants.suppressFeatureValue)) {
                continue;
            }

            feature = header.getTFeature(fname);
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
        tttext = tttext.substring(0, tttext.length() - 3) + "]";

        return tttext;
    }

    /* ------------------------------------------------------- */
    /* Zeichnen des Knotens                                    */
    /* ------------------------------------------------------- */

    /**
     * Zeichnen des Knotens.
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
        boolean svg_mode = false;

        if (xmlNode != null) {
            svg_mode = true;
            xmlNode.setName("g");
            xmlNode.setAttribute("type", "t");

            xmlNode.setAttribute("id", node.getID() + id_anhaengsel);

            if (matchNode) {
                xmlNode.setAttribute("match", "node");
            } else if (matchHighlightedNode) {
                xmlNode.setAttribute("match", "subgraph");
            } else {
                xmlNode.setAttribute("match", "none");
            }
        }

        List names = config.getDisplayedTFeatures(header);
        String fname;
        String wert;
        FontMetrics fm;
        int fcount = 0;

        for (int j = 0; j < names.size(); j++) {
            /* a) Featurewert */
            fname = (String) names.get(j);
            fcount++;

            if (fcount > GraphConstants.treeTerminalFeatures) {
                break;
            }

            wert = node.getFeature(fname);

            /* c) Ist der Wert nicht vorhanden ? */
            if (wert == null) {
                return;
            }

            if (wert.equals(GraphConstants.suppressFeatureValue)) {
                continue;
            }

            // spaeter evtl. NICHT zeichnen

            /* d) Zeichnen */

            // Bestimme Font unabhaengig von der Zielform
            Font leafFont = UtilitiesCollection.chooseFont(GraphConstants.leafFont,
                    wert);

            Element xmlText = null;

            if (svg_mode) {
                xmlText = new Element("text");
                xmlText.setAttribute("font-family", leafFont.getName());

                if (leafFont.isItalic()) {
                    xmlText.setAttribute("font-style", "italic");
                } else {
                    xmlText.setAttribute("font-style", "normal");
                }

                if (leafFont.isBold()) {
                    xmlText.setAttribute("font-weight", "bold");
                } else {
                    xmlText.setAttribute("font-weight", "normal");
                }

                xmlText.setAttribute("font-size",
                    (new Integer(leafFont.getSize())).toString());
            } else {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setFont(leafFont);
            }

            // Color
            Color terminal_color = GraphConstants.nodeColor; // normal < matched < marked

            if (matchNode) {
                terminal_color = GraphConstants.nodeMatchColor;
            } else if (matchHighlightedNode) {
                terminal_color = GraphConstants.nodeMatchSubgraphColor;
            }

            if (highlightedNode) {
                terminal_color = GraphConstants.nodeHighlightedColor;
            }

            if (svg_mode) {
                xmlNode.setAttribute("fill",
                    UtilitiesCollection.getRGBCode(terminal_color));
            } else {
                g.setColor(terminal_color);
            }

            // Zeichen
            fm = g.getFontMetrics(leafFont);

            int w = fm.stringWidth(wert);
            boolean cut = false;

            if (GraphConstants.leafMaxWidth > 0) { // == 0 -> unbound

                while (fm.stringWidth(wert) > getWidth()) {
                    cut = true;
                    wert = wert.substring(0, wert.length() - 1);
                }
            }

            if (cut) {
                wert = wert + "|";
            }

            // neue Breite fuer die abgeschnittenen Woerter
            w = fm.stringWidth(wert);

            int h = fm.getHeight();

            int terminal_x = x - (w / 2);
            int terminal_y = (y - (h / 2)) + fm.getAscent() +
                GraphConstants.leafGapY +
                ((fcount - 1) * (GraphConstants.leafHeight +
                GraphConstants.leafGapY));

            // Text
            if (svg_mode) {
                xmlText.setAttribute("x", (new Integer(x)).toString());
                xmlText.setAttribute("y",
                    (new Integer(terminal_y - delta_y)).toString());
                xmlText.setAttribute("text-anchor", "middle");
                xmlText.addContent(wert);

                xmlNode.addContent(xmlText);

                // FEHLT: Y, IMPLODED
            } else {
                g.drawString(wert, terminal_x, terminal_y);
            }
        }
    }
}
