/*
 * File:     NT_Node.java
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
 * Diese Klasse definiert den nicht-terminalen Knoten.
 * Er wird aus dem elementaren Knoten abgeleitet.
 */
public class NT_Node extends Node {
    /** Holds value of property DOCUMENT ME! */
    protected ArrayList childs;

    /** Holds value of property DOCUMENT ME! */
    protected boolean isVirtualRoot;

    /** Konstruktor. Initialisiert die geerbten Attribute und die Nachfolger-Liste. */
    public NT_Node() {
        super();
        childs = new ArrayList();
        isVirtualRoot = false;
    }

    /** Klonen des Objekts */
    public Object clone() throws CloneNotSupportedException {
        NT_Node result = new NT_Node();

        result.features = (HashMap) features.clone();
        result.parent = parent;
        result.id = id;
        result.isVirtualRoot = isVirtualRoot;

        result.childs = new ArrayList();

        for (int i = 0; i < childs.size(); i++) {
            result.addChild(((Integer) childs.get(i)).intValue());
        }

        return result;
    }

    /** Gibt es einen so genannten virtuellen Wurzelknoten, d.h. einen Knoten, der bei der Indexierung
     *  kuenstlich als Wurzelknoten eingesetzt wurde, um die Forderung nach einem zusammenhaengenden
     *  Korpusgraphen zu erfuellen.
     */
    public boolean isVirtualRoot() {
        return isVirtualRoot;
    }

    /** Markiert, dass es einen so genannten virtuellen Wurzelknoten gibt, d.h. einen Knoten, der bei der Indexierung
     *  kuenstlich als Wurzelknoten eingesetzt wurde, um die Forderung nach einem zusammenhaengenden
     *  Korpusgraphen zu erfuellen.
     */
    public void setVirtualRoot() {
        isVirtualRoot = true;
    }

    /** Setzt die Features und die Nachfolger-Liste zurueck. */
    public void reset() {
        super.reset();
        childs.clear();
        isVirtualRoot = false;
    }

    /** Setzt die gesamte Nachfolger-Liste. */
    public final void setChilds(ArrayList childs) {
        this.childs = childs;
    }

    /** Liefert die Nachfolger-Liste als ArrayList. */
    public final ArrayList getChilds() {
        return childs;
    }

    /** Liefert die Nachfolger-Liste als Array. */
    public final Object[] getChildsArray() {
        return childs.toArray();
    }

    /** Liefert die Anzahl der Nachfolger. */
    public final int getChildsSize() {
        return childs.size();
    }

    /** Liefert den Nachfolger mit der Nummer int. */
    public final Object getChildAt(int index) {
        return childs.get(index);
    }

    /** Besetzt das i-te Kind um. */
    public final void setChildAt(int index, int child) {
        childs.set(index, new Integer(child));
    }

    /** Fuegt der Liste der Nachfolger einen weiteren hinzu. */
    public final void addChild(int child) {
        childs.add(new Integer(child));
    }

    /** Liefert Information ueber den Knotentyp */
    public boolean isTerminal() {
        return false;
    }

    /** Liefert Information ueber den Knotentyp */
    public boolean isNonterminal() {
        return true;
    }

    /* Debugging-Methode: Druckt den Knoten auf dem Bildschirm. */
    public final void printNode() {
        super.printNode();

        System.out.println("Childs:");

        for (int i = 0; i < childs.size(); i++) {
            System.out.println(childs.get(i));
        }
    }
}
