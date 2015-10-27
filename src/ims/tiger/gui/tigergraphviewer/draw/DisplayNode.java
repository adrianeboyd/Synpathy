/*
 * File:     DisplayNode.java
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

import ims.tiger.corpus.Header;
import ims.tiger.corpus.Node;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import org.jdom.Element;

import java.awt.Graphics2D;


/**
 * Diese Klasse definiert einen Display-Knoten.
 */
public interface DisplayNode {
    /**
     * DOCUMENT ME!
     *
     * @param h DOCUMENT ME!
     */
    public void setHeight(int h);

    /**
     * Abfragen der Knoten-Hoehe.
     *
     * @return DOCUMENT ME!
     */
    public int getHeight();

    /**
     * DOCUMENT ME!
     *
     * @param highlighted DOCUMENT ME!
     */
    public void setHighlightedEdge(boolean highlighted);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isHighlightedEdge();

    /**
     * Markieren des Knotens.
     *
     * @param highlighted DOCUMENT ME!
     */
    public void setHighlightedNode(boolean highlighted);

    /**
     * Markierung abfragen
     *
     * @return DOCUMENT ME!
     */
    public boolean isHighlightedNode();

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    public boolean isLeaf();

    /**
     * DOCUMENT ME!
     *
     * @param leftx DOCUMENT ME!
     */
    public void setLeftX(int leftx);

    /**
     * Abfragen der x-Koordinate der linken unteren Ecke.
     *
     * @return DOCUMENT ME!
     */
    public int getLeftX();

    /**
     * Abfragen der y-Koordinate der linken unteren Ecke.
     *
     * @return DOCUMENT ME!
     */
    public int getLeftY();

    /**
     * Level des Knotens.
     *
     * @return DOCUMENT ME!
     */
    public int getLevel();

    /**
     * Match-Markierung des Knotens.
     *
     * @param matched DOCUMENT ME!
     */
    public void setMatchHighlightedNode(boolean matched);

    /**
     * Match-Markierung abfragen.
     *
     * @return DOCUMENT ME!
     */
    public boolean isMatchHighlightedNode();

    /**
     * Match-Beteiligung des Knotens.
     *
     * @param match DOCUMENT ME!
     */
    public void setMatchNode(boolean match);

    /**
     * Match-Beteiligung des Knotens abfragen.
     *
     * @return DOCUMENT ME!
     */
    public boolean isMatchNode();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node getNode();

    /**
     * DOCUMENT ME!
     *
     * @param header DOCUMENT ME!
     * @param newline DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTIGERLanguageDescription(Header header, boolean newline);

    /**
     * Sichtbarkeit des Knotens.
     *
     * @param visible DOCUMENT ME!
     */
    public void setVisible(boolean visible);

    /**
     * Sichtbarkeit abfragen
     *
     * @return DOCUMENT ME!
     */
    public boolean isVisible();

    /**
     * DOCUMENT ME!
     *
     * @param w DOCUMENT ME!
     */
    public void setWidth(int w);

    /**
     * Abfragen der Knoten-Breite.
     *
     * @return DOCUMENT ME!
     */
    public int getWidth();

    /**
     * DOCUMENT ME!
     *
     * @param x DOCUMENT ME!
     */
    public void setX(int x);

    /**
     * Abfragen der x-Koordinate (Mittelpunkt).
     *
     * @return DOCUMENT ME!
     */
    public int getX();

    /**
     * DOCUMENT ME!
     *
     * @param y DOCUMENT ME!
     */
    public void setY(int y);

    /**
     * Abfragen der y-Koordinate (Mittelpunkt).
     *
     * @return DOCUMENT ME!
     */
    public int getY();

    /**
     * DOCUMENT ME!
     *
     * @param clickx DOCUMENT ME!
     * @param clicky DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkClicked(int clickx, int clicky);

    /**
     *
     *
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param g DOCUMENT ME!
     * @param xmlNode DOCUMENT ME!
     * @param delta_y DOCUMENT ME!
     * @param id_anhaengsel DOCUMENT ME!
     */
    public void paint(Header header, TIGERGraphViewerConfiguration config,
        Graphics2D g, Element xmlNode, int delta_y, String id_anhaengsel);
}
