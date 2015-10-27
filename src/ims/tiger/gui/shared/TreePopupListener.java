/*
 * File:     TreePopupListener.java
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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTree;
import javax.swing.tree.TreePath;


/** Standard-TreePopUpListener. */
public class TreePopupListener extends MouseAdapter {
    private VPopupMenu popup;
    private JTree tree;
    private Object obj1 = null;
    private Object obj2 = null;
    private TreePath baumPfad;

    /**
     * Creates a new TreePopupListener instance
     *
     * @param popup DOCUMENT ME!
     * @param tree DOCUMENT ME!
     */
    public TreePopupListener(VPopupMenu popup, JTree tree) {
        this.popup = popup;
        this.tree = tree;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseClicked(MouseEvent e) {
        TreePath baumPfad = tree.getPathForLocation(e.getX(), e.getY());

        if (baumPfad == null) {
            return;
        }

        if ((baumPfad != null) && tree.isSelectionEmpty()) {
            tree.setSelectionPath(baumPfad);
        }

        if ((baumPfad != null) && !tree.isSelectionEmpty()) {
            if (tree.getLastSelectedPathComponent() != null) {
                obj1 = baumPfad.getLastPathComponent();
                obj2 = tree.getLastSelectedPathComponent();

                if (obj1 != obj2) {
                    tree.setSelectionPath(baumPfad);
                }

                if (e.getModifiers() == MouseEvent.META_MASK) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        }
    }
}
