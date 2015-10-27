/*
 * File:     ColorButtonListener.java
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
package ims.tiger.gui.tigergraphviewer.options;

import java.awt.Color;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JColorChooser;


/** Spezieller Listener, der die Farbauswahl-Buttons steuert. */
public class ColorButtonListener extends AbstractAction {
    private JButton button;
    private ColorOptions parent;

    /**
     * Creates a new ColorButtonListener instance
     *
     * @param button DOCUMENT ME!
     * @param parent DOCUMENT ME!
     */
    public ColorButtonListener(JButton button, ColorOptions parent) {
        this.button = button;
        this.parent = parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parm1 DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent parm1) {
        Color c = JColorChooser.showDialog(parent, "Color Selection",
                button.getBackground());

        if (c != null) {
            button.setBackground(c);

            if (((c.getRed() > 200) && (c.getGreen() > 200)) ||
                    ((c.getRed() > 200) && (c.getBlue() > 200)) ||
                    ((c.getGreen() > 200) && (c.getGreen() > 200))) {
                button.setForeground(Color.black);
            } else {
                button.setForeground(Color.white);
            }
        }
    }
}
