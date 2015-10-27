/*
 * File:     Feature.java
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

import java.io.*;

import java.util.*;


/**
 * Diese Klasse definiert ein Feature fuer die Definition im Header. Diese Informationen werden
 * beispielsweise bei der Praesentation des Korpus in der GUI verwendet.<p>
 *
 * ACHTUNG! Da diese Klasse bei der Indexierung serialisiert wird, darf sie NICHT mehr veraendert werden.
 */
public class Feature implements Serializable {
    // ACHTUNG! Verhalten der Methoden wurde veraendert, aber NICHT die 
    // Methoden ergaenzt oder geloescht.	

    /** Holds value of property DOCUMENT ME! */
    static final private long serialVersionUID = -1611499558385184384L;
    private String name;
    private byte stored_type;
    private List items;
    private List descriptions;
    private Hashtable itemHash;

    /** Konstruktor. */
    public Feature() {
        name = "";
        stored_type = -1;
        items = new ArrayList();
        descriptions = new ArrayList();
        itemHash = new Hashtable();
    }

    /** Konstruktur mit Namensuebergabe. */
    public Feature(String name) {
        this();
        setName(name);
    }

    /** Zuweisung des Namens. */
    public final void setName(String name) {
        this.name = name;
    }

    /** Abfragen des Namens. */
    public final String getName() {
        return name;
    }

    /** Automatische Zuweisung des Typs. */
    public final void setDefaultType() {
        stored_type = ims.tiger.system.Constants.INT_LIST_TYPE;

        if (itemSize() < 65534) { // "-1", "virtual"
            stored_type = ims.tiger.system.Constants.SHORT_LIST_TYPE;
        }

        if (itemSize() < 254) { // "-1", "virtual"
            stored_type = ims.tiger.system.Constants.BYTE_LIST_TYPE;
        }

        if (itemSize() == 0) {
            stored_type = ims.tiger.system.Constants.INT_LIST_TYPE;
        }
    }

    /** Automatische Zuweisung des Typs. */
    public final void setTypeOnNumberOfStoredSymbols(int num) {
        stored_type = ims.tiger.system.Constants.INT_LIST_TYPE;

        if (num < 65534) {
            stored_type = ims.tiger.system.Constants.SHORT_LIST_TYPE;
        }

        if (num < 254) {
            stored_type = ims.tiger.system.Constants.BYTE_LIST_TYPE;
        }

        if (num == 0) {
            stored_type = ims.tiger.system.Constants.INT_LIST_TYPE;
        }
    }

    /** Ist es ein Aufzaehlungstyp? */
    public final boolean isListed() {
        return itemSize() > 0;
    }

    /** Abfragen des genauen Typs. */
    public final byte getStoredType() {
        return stored_type;
    }

    /** Hinzufuegen eines Items. */
    public final void addItem(String item) {
        items.add(item);
    }

    /** Hinzufuegen eines Items mit zugehoeriger Erklaerung. */
    public final void addItem(String item, String description) {
        items.add(item);
        descriptions.add(description);
        itemHash.put(item, description);
    }

    /** Anzahl der Items. */
    public final int itemSize() {
        return items.size();
    }

    /** Auslesen aller Items. */
    public final List getItems() {
        return items;
    }

    /** Gehoert Item zur Liste? */
    public boolean isItem(String query) {
        return items.contains(query);
    }

    /** Hinzufuegen einer Erklaerung. */
    public final void addDescription(String description) {
        descriptions.add(description);
    }

    /** Anzahl der Items. */
    public final int getDescriptionsSize() {
        return items.size();
    }

    /** Beschreibung eines Items. */
    public String getDescription(String item) {
        return itemHash.containsKey(item) ? (String) itemHash.get(item) : null;
    }

    /** Auslesen aller Items. */
    public final List getDescriptions() {
        return descriptions;
    }

    /** Ueberschreiben der equals-Methode: Features mit demselben Namen sind gleich! */
    public boolean equals(Object o) {
        return (o instanceof Feature)
        ? ((Feature) o).getName().equals(getName()) : false;
    }

    /** hashcode-Methode; vgl. equals */
    public int hashCode() {
        return getName().hashCode();
    }

    /* Debugging-Methode */
    public void print() /* !!! KONTROLLE !!! */ {
        System.out.println("----Feature----");
        System.out.println(name);
        System.out.println(stored_type);

        String items = "";
        String desc;
        List hilf = getItems();

        for (int i = 0; i < hilf.size(); i++) {
            items += ((String) hilf.get(i) + " ");
            desc = (String) descriptions.get(i);

            if (desc.length() > 0) {
                items += ("(" + desc + ") ");
            }
        }

        System.out.println(items);
    }
}
