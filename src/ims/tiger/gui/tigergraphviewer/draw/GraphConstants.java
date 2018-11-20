/*
 * File:     GraphConstants.java
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

import org.apache.log4j.Logger;

import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.util.ArrayList;


/**
 * Diese Klasse definiert die Konstanten fuer den Graphalgorithmus.
 * Die meisten Konstanten koennen ueber die XML-Konfigurationsdatei
 * im Konfigurationsverzeichnis, die wichtigsten auch vom User angepasst werden.
 */
public class GraphConstants {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(GraphConstants.class);

    /** Holds value of property DOCUMENT ME! */
    public static final String VIEWERFILE_XML = "tigersearch_viewer.xml";

    /* Konstanten, die nicht veraendert werden koennen. */

    /** Holds value of property DOCUMENT ME! */
    public static final BasicStroke lineWidth = new BasicStroke(1.0f);

    /** Holds value of property DOCUMENT ME! */
    public static final BasicStroke ellipseLineWidth = new BasicStroke(0.8f);

    /** Holds value of property DOCUMENT ME! */
    public static final BasicStroke arrowWidth = new BasicStroke(2.0f);

    /** Holds value of property DOCUMENT ME! */
    public static int nodeEllipseBorder = 4;

    /** Holds value of property DOCUMENT ME! */
    public static int unitIncrement = 50;

    /* Konstanten, die vom User veraendert werden koennen */

    /** Holds value of property DOCUMENT ME! */
    public static int windowX = 150;

    /** Holds value of property DOCUMENT ME! */
    public static int windowY = 150;

    /** Holds value of property DOCUMENT ME! */
    public static int windowWidth = 800;

    /** Holds value of property DOCUMENT ME! */
    public static int windowHeight = 600;

    /* Konstanten, die vom User UND ueber die XML-Datei konfiguriert werden koennen. */

    /** Holds value of property DOCUMENT ME! */
    public static Color panelBackgroundColor = new Color(204, 204, 204);

    /** Holds value of property DOCUMENT ME! */
    public static Color panelColor = new Color(0, 0, 0);

    /** Holds value of property DOCUMENT ME! */
    public static Color nodeBackgroundColor = new Color(255, 255, 255);

    /** Holds value of property DOCUMENT ME! */
    public static Color nodeColor = new Color(0, 0, 0);

    /** Holds value of property DOCUMENT ME! */
    public static Color nodeMatchColor = new Color(255, 0, 0);

    /** Holds value of property DOCUMENT ME! */
    public static Color nodeMatchSubgraphColor = new Color(150, 0, 0);

    /** Holds value of property DOCUMENT ME! */
    public static Color nodeHighlightedColor = new Color(0, 0, 178);

    /** Holds value of property DOCUMENT ME! */
    public static Color nodeImplodedColor = new Color(0, 200, 0);

    /** Holds value of property DOCUMENT ME! */
    public static Color secondaryEdgeColor = new Color(0, 160, 0);

    /** Holds value of property DOCUMENT ME! */
    public static Color labelBackgroundColor = new Color(177, 180, 200);

    /** Holds value of property DOCUMENT ME! */
    public static Color virtualRootNodeColor = new Color(128, 128, 128);

    /** Holds value of property DOCUMENT ME! */
    public static String suppressEdgeLabel = "";

    /** Holds value of property DOCUMENT ME! */
    public static String suppressFeatureValue = "";

    /** Holds value of property DOCUMENT ME! */
    public static int treeTerminalFeatures = 3;

    /** Holds value of property DOCUMENT ME! */
    public static int textContext = 0;

    /* Konstanten, die nur ueber die XML-Datei konfiguriert werden koennen. */

    /** Holds value of property DOCUMENT ME! */
    public static Font[] labelFont = { new Font("Dialog", Font.PLAIN, 9) };

    /** Holds value of property DOCUMENT ME! */
    public static Font[] nodeFont = { new Font("Dialog", Font.BOLD, 11) };

    /** Holds value of property DOCUMENT ME! */
    public static Font[] leafFont = { new Font("Dialog", Font.PLAIN, 14) };

    /** Holds value of property DOCUMENT ME! */
    public static Font[] tooltipFont = { new Font("Dialog", Font.PLAIN, 14) };

    /** Holds value of property DOCUMENT ME! */
    public static int nodeVerticalCorrection = 0;

    /** Holds value of property DOCUMENT ME! */
    public static int labelVerticalCorrection = 0;

    /** Holds value of property DOCUMENT ME! */
    public static int nodeHorizontalCorrection = 0;

    /** Holds value of property DOCUMENT ME! */
    public static int labelHorizontalCorrection = 0;

    /** Holds value of property DOCUMENT ME! */
    public static int leafMinWidth = 50;

    /** Holds value of property DOCUMENT ME! */
    public static int leafMaxWidth = 200;

    /** Holds value of property DOCUMENT ME! */
    public static int leafHeight = 20;

    /** Holds value of property DOCUMENT ME! */
    public static int leafGapX = 10;

    /** Holds value of property DOCUMENT ME! */
    public static int leafGapY = 1;

    /** Holds value of property DOCUMENT ME! */
    public static int nodeMinWidth = 25;

    /** Holds value of property DOCUMENT ME! */
    public static int nodeMaxWidth = 50;

    /** Holds value of property DOCUMENT ME! */
    public static int nodeHeight = 20;

    /** Holds value of property DOCUMENT ME! */
    public static int labelMinWidth = 16;

    /** Holds value of property DOCUMENT ME! */
    public static int labelMaxWidth = 40;

    /** Holds value of property DOCUMENT ME! */
    public static int labelHeight = 12;

    /** Holds value of property DOCUMENT ME! */
    public static int labelDistance = 35;

    /** Holds value of property DOCUMENT ME! */
    public static int treeHorizontalMargin = 30;

    /** Holds value of property DOCUMENT ME! */
    public static int treeVerticalMargin = 20;

    /** Holds value of property DOCUMENT ME! */
    public static int treeLevelYOffset = 60;

    /** Holds value of property DOCUMENT ME! */
    public static int treeLockedHeight = 15;

    /** Holds value of property DOCUMENT ME! */
    public static int treeLockedWidth = 20;

    /** Holds value of property DOCUMENT ME! */
    public static boolean leafEllipseIfImploded = true;

    /** Holds value of property DOCUMENT ME! */
    public static boolean virtualRootNodeDisplay = true;

    /** Holds value of property DOCUMENT ME! */
    public static boolean secondaryEdgeDisplay = true;

    /** Methode liest die Einstellungen des Administrators ein. */
    public static void readXMLConfiguration(String install_dir)
        throws IOException {
        /* Konstanten fuer diese Methode */
        String att_const = "att";

        /* SAX-Parser einbinden */
        SAXBuilder builder = new SAXBuilder(true);

        Document doc = null;

        /* Dokument parsen */
        try {
            if (install_dir.indexOf('!') > -1) {
                String path = install_dir +
                    (install_dir.endsWith("/") ? "" : "/") + "config/" +
                    VIEWERFILE_XML;
                doc = builder.build(new URL("jar:" + path));
            } else {
                String path = install_dir +
                    (install_dir.endsWith(File.separator) ? "" : File.separator) +
                    "config" + File.separator + VIEWERFILE_XML;
                File f = new File(path);
                doc = builder.build(f);
            }
        } catch (JDOMException e) {
            logger.error("Error parsing the bookmark config file", e);
            throw new IOException(e.getMessage());
        }

        Element root = doc.getRootElement();
        java.util.List config_file = root.getChildren();

        /* 1. Konfigurations-Ebenen anordnen nach Prioritaet */
        Element level;
        String lname;
        ArrayList config = new ArrayList();

        for (int i = 0; i < config_file.size(); i++) {
            level = (Element) config_file.get(i);
            lname = level.getAttributeValue("level");

            if (lname.equals("default")) {
                config.add(0, level);
            } // default -> first
            else if (lname.equals("os")) {
                config.add(1, level);
            } // os -> middle
            else if (lname.equals("local")) {
                config.add(level);
            }
             // local -> last
        }

        boolean my_os = false;
        String my_os_name = (System.getProperty("os.name")).toLowerCase();

        /* 2. Konfigurations-Ebenen abarbeiten */
        for (int i = 0; i < config.size(); i++) {
            /* a) Level bestimmen */
            level = (Element) config.get(i);
            lname = level.getAttributeValue("level");

            if (lname.equals("os")) { // Betriebssystem muss stimmen

                String compare = (level.getAttributeValue("name")).toLowerCase();

                if (!my_os_name.startsWith(compare)) {
                    continue;
                } else {
                    my_os = true;
                }
            }

            /* b) Parameter einlesen und zuweisen */
            java.util.List setups = level.getChildren();
            Element setup;
            String setup_name;
            String att;

            for (int j = 0; j < setups.size(); j++) {
                setup = (Element) setups.get(j);
                setup_name = (setup.getName()).toLowerCase();

                // Farb-Einstellung
                if (setup_name.equals("color")) {
                    att = (setup.getAttributeValue(att_const)).toLowerCase();

                    int r = 0;
                    int g = 0;
                    int b = 0;

                    try {
                        r = setup.getAttribute("r").getIntValue();
                        g = setup.getAttribute("g").getIntValue();
                        b = setup.getAttribute("b").getIntValue();
                    } catch (DataConversionException e) {
                    }

                    setColor(att, r, g, b);
                }

                // Font-Einstellung
                if (setup_name.equals("font")) {
                    att = (setup.getAttributeValue(att_const)).toLowerCase();

                    // Durchlaufe Kandidaten / Lege Feld an
                    java.util.List fontchildren = setup.getChildren();
                    Element fontel;
                    Font[] fonts = new Font[fontchildren.size()];

                    // Traversiere alle Moeglichkeiten
                    for (int k = 0; k < fontchildren.size(); k++) {
                        fontel = (Element) fontchildren.get(k);

                        String name = (fontel.getAttributeValue("name")).toLowerCase();

                        if (name == null) {
                            name = "Dialog";
                        }

                        String styles = (fontel.getAttributeValue("style")).toLowerCase();
                        int style = Font.PLAIN;

                        if (styles != null) {
                            if (styles.equals("bold")) {
                                style = Font.BOLD;
                            }

                            if (styles.equals("italic")) {
                                style = Font.ITALIC;
                            }
                        }

                        int size = 12;

                        try {
                            size = fontel.getAttribute("size").getIntValue();
                        } catch (DataConversionException e) {
                        }

                        Font newfont = new Font(name, style, size);
                        fonts[k] = newfont;
                    }

                    setFont(att, fonts);
                }

                // Laengen-Einstellung
                if (setup_name.equals("span")) {
                    att = (setup.getAttributeValue(att_const)).toLowerCase();

                    int units = 0;

                    try {
                        units = setup.getAttribute("units").getIntValue();
                    } catch (DataConversionException e) {
                    }

                    setLength(att, units);
                }

                // Boolean-Einstellung
                if (setup_name.equals("bool")) {
                    att = (setup.getAttributeValue(att_const)).toLowerCase();

                    String yesnos = (setup.getAttributeValue("value")).toLowerCase();
                    boolean yesno = false;

                    if (yesnos.equals("yes")) {
                        yesno = true;
                    }

                    setBoolean(att, yesno);
                }

                // Parameter-Einstellung
                if (setup_name.equals("param")) {
                    att = (setup.getAttributeValue(att_const)).toLowerCase();

                    String value = setup.getAttributeValue("value");
                    setParameter(att, value);
                }
            }
             // Parameter
        }
         // Ebenen
    }

    /* Hilfsmethode besetzt eine Boolean-Konstante */
    private static void setBoolean(String att, boolean yesno) {
        if (att.equals("leafellipseifimploded")) {
            leafEllipseIfImploded = yesno;
        }

        if (att.equals("virtualrootnodedisplay")) {
            virtualRootNodeDisplay = yesno;
        }

        if (att.equals("secondaryedgedisplay")) {
            secondaryEdgeDisplay = yesno;
        }
    }

    /* Hilfsmethode besetzt eine Farb-Konstante */
    private static void setColor(String att, int r, int g, int b) {
        if (att.equals("panelbackgroundcolor")) {
            panelBackgroundColor = new Color(r, g, b);
        }

        if (att.equals("panelcolor")) {
            panelColor = new Color(r, g, b);
        }

        if (att.equals("nodebackgroundcolor")) {
            nodeBackgroundColor = new Color(r, g, b);
        }

        if (att.equals("nodecolor")) {
            nodeColor = new Color(r, g, b);
        }

        if (att.equals("nodematchcolor")) {
            nodeMatchColor = new Color(r, g, b);
        }

        if (att.equals("nodematchsubgraphcolor")) {
            nodeMatchSubgraphColor = new Color(r, g, b);
        }

        if (att.equals("nodehighlightedcolor")) {
            nodeHighlightedColor = new Color(r, g, b);
        }

        if (att.equals("nodeimplodedcolor")) {
            nodeImplodedColor = new Color(r, g, b);
        }

        if (att.equals("labelbackgroundcolor")) {
            labelBackgroundColor = new Color(r, g, b);
        }

        if (att.equals("virtualrootnodecolor")) {
            virtualRootNodeColor = new Color(r, g, b);
        }

        if (att.equals("secondaryedgecolor")) {
            secondaryEdgeColor = new Color(r, g, b);
        }
    }

    /* Hilfsmethode besetzt eine Font-Konstante */
    private static void setFont(String att, Font[] fonts) {
        if (att.equals("labelfont")) {
            labelFont = fonts;
        }

        if (att.equals("nodefont")) {
            nodeFont = fonts;
        }

        if (att.equals("leaffont")) {
            leafFont = fonts;
        }

        if (att.equals("tooltipfont")) {
            tooltipFont = fonts;
        }
    }

    /* Hilfsmethode besetzt eine Laengen-Konstante */
    private static void setLength(String att, int pixel) {
        if (att.equals("windowx")) {
            windowX = pixel;
        }

        if (att.equals("windowy")) {
            windowY = pixel;
        }

        if (att.equals("windowwidth")) {
            windowWidth = pixel;
        }

        if (att.equals("windowheight")) {
            windowHeight = pixel;
        }

        if (att.equals("nodeverticalcorrection")) {
            nodeVerticalCorrection = pixel;
        }

        if (att.equals("labelverticalcorrection")) {
            labelVerticalCorrection = pixel;
        }

        if (att.equals("nodehorizontalcorrection")) {
            nodeHorizontalCorrection = pixel;
        }

        if (att.equals("labelhorizontalcorrection")) {
            labelHorizontalCorrection = pixel;
        }

        if (att.equals("leafminwidth")) {
            leafMinWidth = pixel;
        }

        if (att.equals("leafmaxwidth")) {
            leafMaxWidth = pixel;
        }

        if (att.equals("leafheight")) {
            leafHeight = pixel;
        }

        if (att.equals("leafgapx")) {
            leafGapX = pixel;
        }

        if (att.equals("leafgapy")) {
            leafGapY = pixel;
        }

        if (att.equals("nodeminwidth")) {
            nodeMinWidth = pixel;
        }

        if (att.equals("nodemaxwidth")) {
            nodeMaxWidth = pixel;
        }

        if (att.equals("nodeheight")) {
            nodeHeight = pixel;
        }

        if (att.equals("labelminwidth")) {
            labelMinWidth = pixel;
        }

        if (att.equals("labelmaxwidth")) {
            labelMaxWidth = pixel;
        }

        if (att.equals("labelheight")) {
            labelHeight = pixel;
        }

        if (att.equals("labeldistance")) {
            labelDistance = pixel;
        }

        if (att.equals("treehorizontalmargin")) {
            treeHorizontalMargin = pixel;
        }

        if (att.equals("treeverticalmargin")) {
            treeVerticalMargin = pixel;
        }

        if (att.equals("treelevelyoffset")) {
            treeLevelYOffset = pixel;
        }

        if (att.equals("treeterminalfeatures")) {
            treeTerminalFeatures = pixel;
        }

        if (att.equals("treelockedwidth")) {
            treeLockedWidth = pixel;
        }

        if (att.equals("treelockedheight")) {
            treeLockedHeight = pixel;
        }

        if (att.equals("textcontext")) {
            textContext = pixel;
        }
    }

    /* Hilfsmethode besetzt eine Boolean-Konstante */
    private static void setParameter(String att, String value) {
        if (att.equals("suppressedgelabel")) {
            suppressEdgeLabel = value;
        }

        if (att.equals("suppressfeaturevalue")) {
            suppressFeatureValue = value;
        }
    }
}
