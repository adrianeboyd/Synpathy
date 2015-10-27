/*
 * File:     ListEntryRenderer.java
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

import java.awt.Component;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;


/** Ein Renderer fuer das Modell der TreeCombo-Klasse. Dies kann ein Blatt oder
 *  ein Gruppen-Knoten sein. */
public class ListEntryRenderer extends JLabel implements ListCellRenderer {
    /** Holds value of property DOCUMENT ME! */
    static final int OFFSET = 16;
    static Border emptyBorder = new EmptyBorder(0, 0, 0, 0);

    /** Holds value of property DOCUMENT ME! */
    ImageLoader loader = new ImageLoader();

    /** Holds value of property DOCUMENT ME! */
    Image nodeImage = loader.loadImage(ims.tiger.system.Images.FILTER_GROUP);

    /** Holds value of property DOCUMENT ME! */
    Image leafImage = loader.loadImage(ims.tiger.system.Images.FILTER_FILE);

    /** Holds value of property DOCUMENT ME! */
    ImageIcon leafIcon = new ImageIcon(leafImage.getScaledInstance(17, 17,
                Image.SCALE_AREA_AVERAGING));

    /** Holds value of property DOCUMENT ME! */
    ImageIcon nodeIcon = new ImageIcon(nodeImage.getScaledInstance(17, 17,
                Image.SCALE_AREA_AVERAGING));

    /**
     * Creates a new ListEntryRenderer instance
     */
    public ListEntryRenderer() {
        setOpaque(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param listbox DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param index DOCUMENT ME!
     * @param isSelected DOCUMENT ME!
     * @param cellHasFocus DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Component getListCellRendererComponent(JList listbox, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
        ListEntry listEntry = (ListEntry) value;

        if (listEntry != null) {
            Border border;
            setText(listEntry.object().toString());
            setIcon(listEntry.isNode() ? nodeIcon : leafIcon);

            if (index != -1) {
                border = new EmptyBorder(0, OFFSET * listEntry.level(), 0, 0);
            } else {
                border = emptyBorder;
            }

            if (UIManager.getLookAndFeel().getName().equals("CDE/Motif")) {
                if (index == -1) {
                    setOpaque(false);
                } else {
                    setOpaque(true);
                }
            } else {
                setOpaque(true);
                setBorder(border);
            }

            if (isSelected) {
                setBackground(UIManager.getColor("ComboBox.selectionBackground"));
                setForeground(UIManager.getColor("ComboBox.selectionForeground"));
            } else {
                setBackground(UIManager.getColor("ComboBox.background"));
                setForeground(UIManager.getColor("ComboBox.foreground"));
            }
        } else {
            setText("");
        }

        return this;
    }
}
