/*
 * File:     Node.java
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
package ims.tiger.corpus;

import java.util.*;


/**
 * Diese Klasse definiert den elementaren Knoten durch seine Features und seinen Vorgaenger.
 * Sie wird durch die Klassen fuer Terminal- und Nichtterminalknoten erweitert.
 */
public class Node {
    /** Holds value of property DOCUMENT ME! */
    protected HashMap features;

    /** Holds value of property DOCUMENT ME! */
    protected int parent;

    /** Holds value of property DOCUMENT ME! */
    protected String id;

    /** Konstruktor. Featureliste und Vorgaenger werden initialisiert. */
    public Node() {
        features = new HashMap();
        parent = -1;
    }

    /** Klonen des Objekts */
    public Object clone() throws CloneNotSupportedException {
        Node result = new Node();

        result.features = (HashMap) features.clone();
        result.parent = parent;
        result.id = id;

        return result;
    }

    /** Zuruecksetzen der Features. */
    public void reset() {
        features.clear();
        parent = -1;
        id = "";
    }

    /** Setzen der Knoten-ID. */
    public final void setID(String id) {
        this.id = id;
    }

    /** Abfragen der Knoten-ID. */
    public final String getID() {
        return id;
    }

    /** Ist die ID besetzt? */
    public boolean isID() {
        return (id.length() > 0);
    }

    /** Zuweisung einer neuen Featureliste. */
    public final void setFeatures(HashMap features) {
        this.features = features;
    }

    /** Liefert alle gesetzten Features als HashMap. */
    public final HashMap getFeatures() {
        return features;
    }

    /** Liefert alle gesetzten Features mit ihren Attributen als String. */
    public final String getAllFeatureValuePairs() {
        StringBuffer ausgabe = new StringBuffer();

        Object[] names = getAllFeatureNamesArray();

        String name;

        for (int i = 0; i < names.length; i++) {
            name = (String) names[i];

            ausgabe.append(name + "=\"" + getFeature(name) + "\" ");
        }

        if (ausgabe.length() > 0) {
            ausgabe.deleteCharAt(ausgabe.length() - 1);
        }

        return ausgabe.toString();
    }

    /** Liefert alle gesetzten Features mit ihren Attributen als String,
      * sofern nicht im Ausschluss-Vektor angegeben. */
    public final String getAllFeatureValuePairs(Vector attributes) {
        StringBuffer ausgabe = new StringBuffer();

        String feature;
        String value;

        for (int i = 0; i < attributes.size(); i++) {
            feature = (String) attributes.elementAt(i);

            value = getFeature(feature);

            ausgabe.append(feature + "=\"" + value + "\" ");
        }

        if (ausgabe.length() > 0) {
            ausgabe.deleteCharAt(ausgabe.length() - 1);
        }

        return ausgabe.toString();
    }

    /** Fuegt der Featureliste ein neues Attribut-Wert-Paar hinzu. */
    public final void addFeature(String feature, String value) {
        features.put(feature, value);
    }

    /** Ueberschreibt ein Attribut-Wert-Paar. */
    public final void setFeature(String feature, String value) {
        features.put(feature, value);
    }

    /** Liefert die Featurebelegung eines Features. */
    public final String getFeature(String feature) {
        return (String) features.get(feature);
    }

    /** Liefert die Kantenbelegung der eingehenden Kante. Prueft NICHT die Existenz der Kante. */
    public final String getIncomingEdgeLabel() {
        return (String) features.get(ims.tiger.system.Constants.EDGE);
    }

    /** Setzt die Kantenbelegung der eingehenden Kante. Prueft NICHT die Existenz der Kante. */
    public final void setIncomingEdgeLabel(String label) {
        setFeature(ims.tiger.system.Constants.EDGE, label);
    }

    /** Prueft die Existenz der eigehenden Kante. */
    public final boolean isIncomingEdgeLabel() {
        return features.containsKey(ims.tiger.system.Constants.EDGE);
    }

    /** Ist das Feature vorhanden? */
    public final boolean isFeature(String feature) {
        return features.containsKey(feature);
    }

    /** Liefert alle gesetzten Featurenamen als Set. */
    public final Set getAllFeatureNames() {
        return features.keySet();
    }

    /** Liefert alle gesetzten Featurenamen als Array. */
    public final Object[] getAllFeatureNamesArray() {
        Set menge = getAllFeatureNames();

        return menge.toArray();
    }

    /** Liefert alle gesetzten Featurewerte als Set. */
    public final Object[] getAllFeatureValues() {
        return ((Collection) features.values()).toArray();
    }

    /** Setzt den Vorgaenger des Knotens .*/
    public final void setParent(int parent) {
        this.parent = parent;
    }

    /** Liefert den Vorgaenger des Knotens. */
    public final int getParent() {
        return parent;
    }

    /** Liefert Information ueber den Knotentyp */
    public boolean isTerminal() {
        return false;
    }

    /** Liefert Information ueber den Knotentyp */
    public boolean isNonterminal() {
        return false;
    }

    /* Debugging-Methode: Druckt den Knoten auf dem Bildschirm. */
    public void printNode() {
        Set menge = getAllFeatureNames();
        Object[] alle = menge.toArray();

        System.out.println("=== Node ID:" + getID() + " === ");

        for (int i = 0; i < alle.length; i++) {
            System.out.println((String) alle[i] + " is " +
                getFeature((String) alle[i]));
        }

        System.out.println("Parent:" + parent);
    }
}
