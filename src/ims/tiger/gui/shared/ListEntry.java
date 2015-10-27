/*
 * File:     ListEntry.java
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
package ims.tiger.gui.shared;


/** Ein Eintrag im Modell der TreeCombo-Klasse. Dies kann ein Blatt oder
 *  ein Gruppen-Knoten sein. */
public class ListEntry {
    /** Holds value of property DOCUMENT ME! */
    Object object;

    /** Holds value of property DOCUMENT ME! */
    int level;

    /** Holds value of property DOCUMENT ME! */
    boolean isNode;

    /**
     * Creates a new ListEntry instance
     *
     * @param anObject DOCUMENT ME!
     * @param aLevel DOCUMENT ME!
     * @param isNode DOCUMENT ME!
     */
    public ListEntry(Object anObject, int aLevel, boolean isNode) {
        object = anObject;
        level = aLevel;
        this.isNode = isNode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object object() {
        return object;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int level() {
        return level;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isNode() {
        return isNode;
    }
}
