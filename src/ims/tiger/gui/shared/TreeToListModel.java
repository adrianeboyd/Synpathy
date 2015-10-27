/*
 * File:     TreeToListModel.java
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

import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;


/** Ein einfaches TreeToList-Modell. */
public class TreeToListModel extends AbstractListModel implements ComboBoxModel,
    TreeModelListener {
    /** Holds value of property DOCUMENT ME! */
    TreeModel source;

    /** Holds value of property DOCUMENT ME! */
    boolean invalid = true;

    /** Holds value of property DOCUMENT ME! */
    Object currentValue;

    /** Holds value of property DOCUMENT ME! */
    Vector cache = new Vector();

    /**
     * Creates a new TreeToListModel instance
     *
     * @param aTreeModel DOCUMENT ME!
     */
    public TreeToListModel(TreeModel aTreeModel) {
        source = aTreeModel;
        aTreeModel.addTreeModelListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param anObject DOCUMENT ME!
     */
    public void setSelectedItem(Object anObject) {
        currentValue = anObject;
        fireContentsChanged(this, -1, -1);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getSelectedItem() {
        return currentValue;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getSize() {
        validate();

        return cache.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getElementAt(int index) {
        return cache.elementAt(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void treeNodesChanged(TreeModelEvent e) {
        invalid = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void treeNodesInserted(TreeModelEvent e) {
        invalid = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void treeNodesRemoved(TreeModelEvent e) {
        invalid = true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void treeStructureChanged(TreeModelEvent e) {
        invalid = true;
    }

    /**
     * DOCUMENT ME!
     */
    void validate() {
        if (invalid) {
            cache = new Vector();
            cacheTree(source.getRoot(), 0);

            if (cache.size() > 0) {
                currentValue = cache.elementAt(0);
            }

            invalid = false;
            fireContentsChanged(this, 0, 0);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param anObject DOCUMENT ME!
     * @param level DOCUMENT ME!
     */
    void cacheTree(Object anObject, int level) {
        if (source.isLeaf(anObject)) {
            addListEntry(anObject, level, false);
        } else {
            int c = source.getChildCount(anObject);
            int i;
            Object child;
            addListEntry(anObject, level, true);
            level++;

            for (i = 0; i < c; i++) {
                child = source.getChild(anObject, i);
                cacheTree(child, level);
            }

            level--;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param anObject DOCUMENT ME!
     * @param level DOCUMENT ME!
     * @param isNode DOCUMENT ME!
     */
    void addListEntry(Object anObject, int level, boolean isNode) {
        cache.addElement(new ListEntry(anObject, level, isNode));
    }
}
