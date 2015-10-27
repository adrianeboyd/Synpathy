/*
 * File:     AbstractDisplayNode.java
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


/**
 * $Id: AbstractDisplayNode.java,v 1.1 2007/01/03 11:46:23 klasal Exp $
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public abstract class AbstractDisplayNode implements DisplayNode {
    /** Holds value of property DOCUMENT ME! */
    protected boolean highlightedEdge;

    /** Holds value of property DOCUMENT ME! */
    protected boolean highlightedNode;

    /** Holds value of property DOCUMENT ME! */
    protected boolean matchEdges;

    /** Holds value of property DOCUMENT ME! */
    protected boolean matchHighlightedNode;

    /** Holds value of property DOCUMENT ME! */
    protected boolean matchNode;

    /** Holds value of property DOCUMENT ME! */
    protected boolean visible = true;

    /** Holds value of property DOCUMENT ME! */
    protected int height;

    /** Holds value of property DOCUMENT ME! */
    protected int leftx;

    /** Holds value of property DOCUMENT ME! */
    protected int lefty;

    /** Holds value of property DOCUMENT ME! */
    protected int subtreeFromX;

    /** Holds value of property DOCUMENT ME! */
    protected int subtreeToX;

    /** Holds value of property DOCUMENT ME! */
    protected int width;

    /** Holds value of property DOCUMENT ME! */
    protected int x;

    /** Holds value of property DOCUMENT ME! */
    protected int y;

    /**
     * Setzen der Knoten-Hoehe.
     *
     * @param height DOCUMENT ME!
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Abfragen der Knoten-Hoehe.
     *
     * @return DOCUMENT ME!
     */
    public int getHeight() {
        return height;
    }

    /**
     * Markieren der Kanten im Subgraphen.
     *
     * @param highlightedEdge DOCUMENT ME!
     */
    public void setHighlightedEdge(boolean highlightedEdge) {
        this.highlightedEdge = highlightedEdge;
    }

    /**
     * Kanten-Markierung abfragen
     *
     * @return DOCUMENT ME!
     */
    public boolean isHighlightedEdge() {
        return highlightedEdge;
    }

    /**
     * Markieren des Knotens.
     *
     * @param highlightedNode DOCUMENT ME!
     */
    public void setHighlightedNode(boolean highlightedNode) {
        this.highlightedNode = highlightedNode;
    }

    /**
     * Knoten-Markierung abfragen.
     *
     * @return DOCUMENT ME!
     */
    public boolean isHighlightedNode() {
        return highlightedNode;
    }

    /**
     * Setzen der x-Koordinate der linken unteren Ecke.
     *
     * @param leftx DOCUMENT ME!
     */
    public void setLeftX(int leftx) {
        this.leftx = leftx;
    }

    /**
     * Abfragen der x-Koordinate der linken unteren Ecke.
     *
     * @return DOCUMENT ME!
     */
    public int getLeftX() {
        return leftx;
    }

    /**
     * Setzen der y-Koordinate der linken unteren Ecke.
     *
     * @param lefty DOCUMENT ME!
     */
    public void setLeftY(int lefty) {
        this.lefty = lefty;
    }

    /**
     * Abfragen der y-Koordinate der linken unteren Ecke.
     *
     * @return DOCUMENT ME!
     */
    public int getLeftY() {
        return lefty;
    }

    /**
     * Markieren der Kanten im Subgraphen.
     *
     * @param matchEdges DOCUMENT ME!
     */
    public void setMatchHighlightedEdges(boolean matchEdges) {
        this.matchEdges = matchEdges;
    }

    /**
     * Kanten-Markierung abfragen
     *
     * @return DOCUMENT ME!
     */
    public boolean isMatchHighlightedEdges() {
        return matchEdges;
    }

    /**
     * Markieren des Knotens.
     *
     * @param matchHighlightedNode DOCUMENT ME!
     */
    public void setMatchHighlightedNode(boolean matchHighlightedNode) {
        this.matchHighlightedNode = matchHighlightedNode;
    }

    /**
     * Knoten-Markierung abfragen.
     *
     * @return DOCUMENT ME!
     */
    public boolean isMatchHighlightedNode() {
        return matchHighlightedNode;
    }

    /**
     * Match-Beteiligung des Knotens.
     *
     * @param matchNode DOCUMENT ME!
     */
    public void setMatchNode(boolean matchNode) {
        this.matchNode = matchNode;
    }

    /**
     * Match-Beteiligung des Knotens abfragen.
     *
     * @return DOCUMENT ME!
     */
    public boolean isMatchNode() {
        return matchNode;
    }

    /**
     * Setzen der x-Koordinate des linkesten Nachfolgers.
     *
     * @param subtreeFromX DOCUMENT ME!
     */
    public void setSubtreeFromX(int subtreeFromX) {
        this.subtreeFromX = subtreeFromX;
    }

    /**
     * Abfragen der x-Koordinate des linkesten Nachfolgers.
     *
     * @return DOCUMENT ME!
     */
    public int getSubtreeFromX() {
        return subtreeFromX;
    }

    /**
     * Setzen der x-Koordinate des rechtesten Nachfolgers.
     *
     * @param subtreeToX DOCUMENT ME!
     */
    public void setSubtreeToX(int subtreeToX) {
        this.subtreeToX = subtreeToX;
    }

    /**
     * Abfragen der x-Koordinate des rechtesten Nachfolgers.
     *
     * @return DOCUMENT ME!
     */
    public int getSubtreeToX() {
        return subtreeToX;
    }

    /**
     * Sichtbarkeit des Knotens.
     *
     * @param visible DOCUMENT ME!
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * Sichtbarkeit abfragen
     *
     * @return DOCUMENT ME!
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Setzen der Knoten-Breite.
     *
     * @param width DOCUMENT ME!
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Abfragen der Knoten-Breite.
     *
     * @return DOCUMENT ME!
     */
    public int getWidth() {
        return width;
    }

    /**
     * Setzen der x-Koordinate (Mittelpunkt).
     *
     * @param x DOCUMENT ME!
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Abfragen der x-Koordinate (Mittelpunkt).
     *
     * @return DOCUMENT ME!
     */
    public int getX() {
        return x;
    }

    /**
     * Setzen der y-Koordinate (Mittelpunkt).
     *
     * @param y DOCUMENT ME!
     */
    public void setY(int y) {
        this.y = y;
        lefty = y - (GraphConstants.leafHeight / 2);
    }

    /**
     * Abfragen der y-Koordinate (Mittelpunkt).
     *
     * @return DOCUMENT ME!
     */
    public int getY() {
        return y;
    }

    /**
     * Ueberpruefen, ob Knoten angeklickt wurde
     *
     * @param clickx DOCUMENT ME!
     * @param clicky DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public synchronized boolean checkClicked(int clickx, int clicky) {
        return ((clickx >= leftx) && (clickx <= (leftx + width)) &&
        (clicky >= lefty) && (clicky <= (lefty + height)));
    }
}
