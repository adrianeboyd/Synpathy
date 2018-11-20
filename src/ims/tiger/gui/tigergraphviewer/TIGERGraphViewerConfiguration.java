/*
 * File:     TIGERGraphViewerConfiguration.java
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
package ims.tiger.gui.tigergraphviewer;

import ims.tiger.corpus.Header;

import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.awt.Color;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.*;


/** Diese Klasse verwaltet die Einstellungen des GraphViewers.
 *  Die Default-Einstellungen werden aus einer XML-Konfigurationsdatei
 *  eingelesen. Anschliessend wird im Home-Verzeichnis des Users nach
 *  einer serialisierten ForestViewerConfiguration-Klasse gesucht.
 */
public class TIGERGraphViewerConfiguration implements Serializable {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(TIGERGraphViewerConfiguration.class);

    /** Holds value of property DOCUMENT ME! */
    private static final String VIEWERFILE_DAT = "tigersearch_viewer.dat";
    private int windowX = 150;
    private int windowY = 150;
    private int windowWidth = 800;
    private int windowHeight = 600;

    // Unterdruecke manche Belegungen (sieht netter aus)
    private String suppressEdgeLabel = "";
    private String suppressFeatureValue = "";

    // Unterdruecke sekundaere Kanten
    private boolean secondaryEdgeDisplay = true;

    // Zeige virtuellen Wurzelknoten
    private boolean virtualRootNodeDisplay = true;

    // Graphalgorithmus-Konstanten
    private int leafMaxWidth = 200;

    // Farben; black-Belegung nur fuer Notfaelle (kein null abspeichern!)
    private Color panelBackgroundColor = new Color(0, 0, 0);
    private Color panelColor = new Color(0, 0, 0);
    private Color nodeBackgroundColor = new Color(0, 0, 0);
    private Color nodeColor = new Color(0, 0, 0);
    private Color nodeMatchColor = new Color(0, 0, 0);
    private Color nodeMatchSubgraphColor = new Color(0, 0, 0);
    private Color nodeHighlightedColor = new Color(0, 0, 0);
    private Color nodeImplodedColor = new Color(0, 0, 0);
    private Color secondaryEdgeColor = new Color(0, 0, 0);
    private Color labelBackgroundColor = new Color(0, 0, 0);
    private Color virtualRootNodeColor = new Color(0, 0, 0);

    // Voreinstellungen der darzustellenden Attribute im GraphViewer (korpusabhaengig)
    private HashMap corpusPreferencesNT = new HashMap();
    private HashMap corpusPreferencesT = new HashMap();

    // Breite des Kontextes in der Textdarstellung
    private int textContext = 0;

    // Anzahl der dargestellten terminalen Features (vom User nicht mehr aenderbar!)
    private int treeTerminalFeatures = 5;
    private String install_dir;
    private String user_dir;
    private String tigersearch_dir;

    /* --------------------------------------------------- */
    /* Kommunikationsmethoden                              */
    /* --------------------------------------------------- */

    /** Liest das Objekt aus dem angegebenen Pfad. Sollte es fehlen,
     *  wird die bestehende Default-Konfiguration uebernommen. Eine Warnung
     *  erfolgt NICHT.
     */
    public TIGERGraphViewerConfiguration(String tigersearch_dir,
        String install_dir, String user_dir) {
        this.install_dir = install_dir;
        this.user_dir = user_dir;
        this.tigersearch_dir = tigersearch_dir;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void readConfiguration() throws Exception {
        // 1. Default-Einstellungen lesen
        GraphConstants.readXMLConfiguration(install_dir);

        // 2. User-Einstellungen als Objekt lesen
        String file = user_dir + File.separator + tigersearch_dir +
            File.separator + VIEWERFILE_DAT;
        File file_test = new File(file);

        if (!file_test.exists()) {
            return;
        }
         // Datei noch nicht angelegt

        Object help;

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                        file));
            help = in.readObject();
            in.close();
        } catch (Exception e) { // Objektstruktur durch Update veraendert! Beim Speichern aktuell.

            if (logger.isEnabledFor(Level.WARN)) {
                logger.warn(
                    "Structure of serialized object ForestViewerConfiguration has changed.");
            }

            file_test.delete();

            return;
        }

        // 3. Objekteigenschaften uebertragen
        TIGERGraphViewerConfiguration c = null;

        try {
            c = (TIGERGraphViewerConfiguration) help;
        } catch (ClassCastException e) {
            logger.error("Error casting the ForestViewerConfiguration object.");

            return;
        }

        corpusPreferencesNT = c.getCorpusPreferencesNT();
        corpusPreferencesT = c.getCorpusPreferencesT();

        GraphConstants.windowHeight = c.getWindowHeight();
        GraphConstants.windowWidth = c.getWindowWidth();
        GraphConstants.windowX = c.getWindowX();
        GraphConstants.windowY = c.getWindowY();

        GraphConstants.suppressEdgeLabel = c.getSuppressEdgeLabel();
        GraphConstants.suppressFeatureValue = c.getSuppressFeatureValue();

        GraphConstants.secondaryEdgeDisplay = c.getSecondaryEdgeDisplay();
        GraphConstants.virtualRootNodeDisplay = c.getVirtualRootNodeDisplay();
        GraphConstants.leafMaxWidth = c.getLeafMaxWidth();
        GraphConstants.textContext = c.getTextContext();

        GraphConstants.panelBackgroundColor = c.getPanelBackgroundColor();
        GraphConstants.panelColor = c.getPanelColor();
        GraphConstants.nodeBackgroundColor = c.getNodeBackgroundColor();
        GraphConstants.nodeColor = c.getNodeColor();
        GraphConstants.nodeMatchColor = c.getNodeMatchColor();
        GraphConstants.nodeMatchSubgraphColor = c.getNodeMatchSubgraphColor();
        GraphConstants.nodeHighlightedColor = c.getNodeHighlightedColor();
        GraphConstants.nodeImplodedColor = c.getNodeImplodedColor();
        GraphConstants.secondaryEdgeColor = c.getSecondaryEdgeColor();
        GraphConstants.labelBackgroundColor = c.getLabelBackgroundColor();
        GraphConstants.virtualRootNodeColor = c.getVirtualRootNodeColor();
    }

    /** Setzt auf Wunsch des Clients die Einstellungen auf die Default-
     *  Einstellungen zurueck.
     */
    public void resetConfiguration() throws Exception {
        GraphConstants.readXMLConfiguration(install_dir);
    }

    /** Sichert die Konfiguration im User-Verzeichnis. */
    public void saveConfiguration(String user_dir) throws IOException {
        // Pfad angelegt? Sonst erzeugen!
        String path = user_dir + File.separator + tigersearch_dir +
            File.separator;
        File path_test = new File(path);

        if (!path_test.exists()) {
            path_test.mkdir();
        }

        // Datei erzeugen
        String file = path + VIEWERFILE_DAT;
        File file_test = new File(file);

        if (file_test.exists()) {
            file_test.delete();
        }

        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                    file));
        out.writeObject(this);
        out.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param windowX DOCUMENT ME!
     */
    public void setWindowX(int windowX) {
        this.windowX = windowX;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getWindowX() {
        return windowX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param windowY DOCUMENT ME!
     */
    public void setWindowY(int windowY) {
        this.windowY = windowY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getWindowY() {
        return windowY;
    }

    /**
     * DOCUMENT ME!
     *
     * @param windowWidth DOCUMENT ME!
     */
    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getWindowWidth() {
        return windowWidth;
    }

    /**
     * DOCUMENT ME!
     *
     * @param windowHeight DOCUMENT ME!
     */
    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getWindowHeight() {
        return windowHeight;
    }

    /**
     * DOCUMENT ME!
     *
     * @param suppressEdgeLabel DOCUMENT ME!
     */
    public void setSuppressEdgeLabel(String suppressEdgeLabel) {
        this.suppressEdgeLabel = suppressEdgeLabel;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSuppressEdgeLabel() {
        return suppressEdgeLabel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param suppressFeatureValue DOCUMENT ME!
     */
    public void setSuppressFeatureValue(String suppressFeatureValue) {
        this.suppressFeatureValue = suppressFeatureValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSuppressFeatureValue() {
        return suppressFeatureValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getTreeTerminalFeatures() {
        return treeTerminalFeatures;
    }

    // Farben
    public void setPanelBackgroundColor(Color panelBackgroundColor) {
        this.panelBackgroundColor = panelBackgroundColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getPanelBackgroundColor() {
        return panelBackgroundColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param panelColor DOCUMENT ME!
     */
    public void setPanelColor(Color panelColor) {
        this.panelColor = panelColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getPanelColor() {
        return panelColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeBackgroundColor DOCUMENT ME!
     */
    public void setNodeBackgroundColor(Color nodeBackgroundColor) {
        this.nodeBackgroundColor = nodeBackgroundColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getNodeBackgroundColor() {
        return nodeBackgroundColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeColor DOCUMENT ME!
     */
    public void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getNodeColor() {
        return nodeColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeMatchColor DOCUMENT ME!
     */
    public void setNodeMatchColor(Color nodeMatchColor) {
        this.nodeMatchColor = nodeMatchColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getNodeMatchColor() {
        return nodeMatchColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeMatchSubgraphColor DOCUMENT ME!
     */
    public void setNodeMatchSubgraphColor(Color nodeMatchSubgraphColor) {
        this.nodeMatchSubgraphColor = nodeMatchSubgraphColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getNodeMatchSubgraphColor() {
        return nodeMatchSubgraphColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeHighlightedColor DOCUMENT ME!
     */
    public void setNodeHighlightedColor(Color nodeHighlightedColor) {
        this.nodeHighlightedColor = nodeHighlightedColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getNodeHighlightedColor() {
        return nodeHighlightedColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodeImplodedColor DOCUMENT ME!
     */
    public void setNodeImplodedColor(Color nodeImplodedColor) {
        this.nodeImplodedColor = nodeImplodedColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getNodeImplodedColor() {
        return nodeImplodedColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param secondaryEdgeColor DOCUMENT ME!
     */
    public void setSecondaryEdgeColor(Color secondaryEdgeColor) {
        this.secondaryEdgeColor = secondaryEdgeColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getSecondaryEdgeColor() {
        return secondaryEdgeColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param labelBackgroundColor DOCUMENT ME!
     */
    public void setLabelBackgroundColor(Color labelBackgroundColor) {
        this.labelBackgroundColor = labelBackgroundColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getLabelBackgroundColor() {
        return labelBackgroundColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @param virtualRootNodeColor DOCUMENT ME!
     */
    public void setVirtualRootNodeColor(Color virtualRootNodeColor) {
        this.virtualRootNodeColor = virtualRootNodeColor;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Color getVirtualRootNodeColor() {
        return virtualRootNodeColor;
    }

    // sekundaere Kanten
    public void setSecondaryEdgeDisplay(boolean secondaryEdgeDisplay) {
        this.secondaryEdgeDisplay = secondaryEdgeDisplay;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getSecondaryEdgeDisplay() {
        return secondaryEdgeDisplay;
    }

    // virtueller Wurzelknoten
    public void setVirtualRootNodeDisplay(boolean virtualRootNodeDisplay) {
        this.virtualRootNodeDisplay = virtualRootNodeDisplay;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getVirtualRootNodeDisplay() {
        return virtualRootNodeDisplay;
    }

    // Graphalgorithmus-Konstanten
    public void setLeafMaxWidth(int leafMaxWidth) {
        this.leafMaxWidth = leafMaxWidth;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLeafMaxWidth() {
        return leafMaxWidth;
    }

    // Benutzereinstellungen, korpusabhaengig  
    public HashMap getCorpusPreferencesNT() {
        return corpusPreferencesNT;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public HashMap getCorpusPreferencesT() {
        return corpusPreferencesT;
    }

    // Graphalgorithmus-Konstanten
    public void setTextContext(int textContext) {
        this.textContext = textContext;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getTextContext() {
        return textContext;
    }

    /* --------------------------------------------------- */
    /* Korpusabhaengige Benutzervorgaben                   */
    /* --------------------------------------------------- */

    /** Benutzereinstellung: Welches Feature wird grafisch in der Ellipse dargestellt (Default: das erste)? */
    public String getDisplayedNTFeature(Header header) {
        String corpusid = header.getCorpus_ID();

        if (corpusPreferencesNT.containsKey(corpusid)) { // Korpuseinstellungen bereits da

            String candidate = (String) corpusPreferencesNT.get(corpusid);

            // Wohlgeformtheit pruefen		
            if (header.isNonterminalFeature(candidate)) {
                return candidate;
            }
        }

        // Neue Default-Einstellungen generieren
        String fname = getDefaultDisplayedNTFeature(header);
        corpusPreferencesNT.put(corpusid, fname);

        return fname;
    }

    /** Voreinstellung: Das erste Feature wird per Default dargestellt. */
    public String getDefaultDisplayedNTFeature(Header header) {
        return (String) header.getAllNTFeatureNames().get(0); // Nimm 1. Feature!
    }

    /** Umsetzen der Voreinstellungen durch das Dialogfenster. */
    public void setDisplayedNTFeature(Header header, String feature) {
        String corpusid = header.getCorpus_ID();
        corpusPreferencesNT.put(corpusid, feature);
    }

    /** Benutzereinstellung: Welche Features werden fuer Terminale dargestellt (Default: die ersten 5)? */
    public List getDisplayedTFeatures(Header header) {
        String corpusid = header.getCorpus_ID();

        if (corpusPreferencesT.containsKey(corpusid)) { // Korpuseinstellungen bereits da

            List candidates = (List) corpusPreferencesT.get(corpusid);

            // Wohlgeformtheit pruefen		
            int before = candidates.size();

            Iterator it = candidates.iterator();

            while (it.hasNext()) {
                if (!header.isTerminalFeature((String) it.next())) {
                    it.remove();
                }
            }

            int after = candidates.size();

            if (before == after) {
                return candidates;
            }
        }

        // Neue Default-Einstellungen generieren
        List features = getDefaultDisplayedTFeatures(header);
        corpusPreferencesT.put(corpusid, features);

        return features;
    }

    /** Voreinstellung: Die ersten 5 Features werden per Default dargestellt. */
    public List getDefaultDisplayedTFeatures(Header header) {
        String corpusid = header.getCorpus_ID();

        List features = (List) ((ArrayList) header.getAllTFeatureNames()).clone(); // KOPIE verarbeiten!

        while (features.size() > GraphConstants.treeTerminalFeatures) {
            features.remove(features.size() - 1);
        }

        return features;
    }

    /** Umsetzen der Voreinstellungen durch das Dialogfenster. */
    public void setDisplayedTFeatures(Header header, List features) {
        String corpusid = header.getCorpus_ID();
        corpusPreferencesT.put(corpusid, features);
    }
}
