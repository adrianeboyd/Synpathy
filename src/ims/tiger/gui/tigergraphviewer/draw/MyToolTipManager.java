/*
 * File:     MyToolTipManager.java
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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JToolTip;
import javax.swing.Timer;


/** Definiert einen eigenen ToolTipManager fuer den GraphViewer (Bewegung
 *  der Maus ueber innere oder aeussere Knoten auf der Zeichenflaeche. */
public class MyToolTipManager extends MouseMotionAdapter
    implements ActionListener {
    /** Holds value of property DOCUMENT ME! */
    protected Timer m_timer;

    /** Holds value of property DOCUMENT ME! */
    protected int m_lastX = -1;

    /** Holds value of property DOCUMENT ME! */
    protected int m_lastY = -1;

    /** Holds value of property DOCUMENT ME! */
    protected boolean m_moved = false;

    /** Holds value of property DOCUMENT ME! */
    protected int m_counter = 0;

    /** Holds value of property DOCUMENT ME! */
    public JToolTip m_toolTip = new JToolTip();

    /**
     * Creates a new MyToolTipManager instance
     *
     * @param parent DOCUMENT ME!
     */
    MyToolTipManager(GraphPanel parent) {
        parent.addMouseMotionListener(this);

        m_toolTip.setTipText(null);

        parent.add(m_toolTip);

        m_toolTip.setVisible(false);

        m_timer = new Timer(200, this);

        m_timer.start();
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseMoved(MouseEvent e) {
        m_moved = true;

        m_counter = -1;

        m_lastX = e.getX();

        m_lastY = e.getY();

        if (m_toolTip.isVisible()) {
            m_toolTip.setVisible(false);

            m_toolTip.getParent().repaint();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        if (m_moved || (m_counter == 0) || (m_toolTip.getTipText() == null)) {
            if (m_toolTip.isVisible()) {
                m_toolTip.setVisible(true);
            }

            m_moved = false;

            return;
        }

        if (m_counter < 0) {
            m_counter = -4;

            m_toolTip.setVisible(true);

            Dimension d = m_toolTip.getPreferredSize();

            m_toolTip.setBounds(m_lastX, m_lastY + 20, d.width, d.height);
        }

        m_counter--;
    }
}
