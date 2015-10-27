/*
 * File:     ToolBar.java
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.*;


/** Erweiterte Toolbar-Leiste mit einem "Rollover"-Effekt. */
public class ToolBar extends JPanel implements MouseListener {
    /** Holds value of property DOCUMENT ME! */
    protected static String IMAGEPATH = "images/";

    /** Holds value of property DOCUMENT ME! */
    protected JToolBar toolBar;

    /** Holds value of property DOCUMENT ME! */
    protected JButton b;

    /** Holds value of property DOCUMENT ME! */
    protected Dimension bdim = new Dimension(26, 26);

    /** Holds value of property DOCUMENT ME! */
    protected Dimension widedim = new Dimension(250, 26);

    /** Holds value of property DOCUMENT ME! */
    public Vector setOfEventlistener = new Vector();

    // Konstruktor
    public ToolBar() {
        setLayout(new BorderLayout());
    }

    /**
     * DOCUMENT ME!
     *
     * @param pListener DOCUMENT ME!
     * @param pname DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean setActionListener(ActionListener pListener, String pname) {
        boolean status = false;
        Eventeintrag ee = new Eventeintrag();

        for (Enumeration el = setOfEventlistener.elements();
                el.hasMoreElements();) {
            ee = (Eventeintrag) el.nextElement();

            if ((ee.name == pname) && (pListener != null)) {
                ee.lauscher = pListener;
                ee.belauschter.setEnabled(true);
                ee.belauschter.addActionListener(pListener);
            }
        }

        return status;
    }

    /**
     * DOCUMENT ME!
     *
     * @param button DOCUMENT ME!
     */
    public void setMouseListener(JButton button) {
        button.addMouseListener(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @param button DOCUMENT ME!
     */
    public void initButton(JButton button) {
        button.setMinimumSize(bdim);
        button.setMaximumSize(bdim);
        button.setSize(bdim);
        button.setPreferredSize(bdim);
        button.setBorder(BorderFactory.createBevelBorder(2));
        button.setBorderPainted(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param button DOCUMENT ME!
     */
    public void initComboButton(JButton button) {
        button.setMinimumSize(widedim);
        button.setMaximumSize(widedim);
        button.setSize(widedim);
        button.setPreferredSize(widedim);
        button.setBorder(BorderFactory.createBevelBorder(2));
        button.setBorderPainted(false);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JButton getButton() {
        return b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JToolBar getToolBar() {
        return toolBar;
    }

    /**
     * DOCUMENT ME!
     *
     * @param item DOCUMENT ME!
     * @param pname DOCUMENT ME!
     */
    public void addActionItem(JButton item, String pname) {
        Eventeintrag ee = new Eventeintrag();

        ee.name = pname;
        ee.belauschter = item;
        ee.belauschter.setEnabled(false);
        setOfEventlistener.addElement(ee);
    }

    /**
     * DOCUMENT ME!
     *
     * @param item DOCUMENT ME!
     * @param pname DOCUMENT ME!
     */
    public void addActionItem(JToggleButton item, String pname) {
        Eventeintrag ee = new Eventeintrag();

        ee.name = pname;
        ee.belauschter = item;
        ee.belauschter.setEnabled(false);
        setOfEventlistener.addElement(ee);
    }

    /** Mausbewegung */
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseExited(MouseEvent e) {
        if ((e.getComponent() instanceof JButton) &&
                e.getComponent().isEnabled()) {
            ((JButton) e.getComponent()).setBorder(BorderFactory.createBevelBorder(
                    2));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseEntered(MouseEvent e) {
        if ((e.getComponent() instanceof JButton) &&
                e.getComponent().isEnabled()) {
            ((JButton) e.getComponent()).setBorderPainted(true);
            ((JButton) e.getComponent()).setBorder(BorderFactory.createBevelBorder(
                    0));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mousePressed(MouseEvent e) {
        if ((e.getComponent() instanceof JButton) &&
                e.getComponent().isEnabled()) {
            ((JButton) e.getComponent()).setBorder(BorderFactory.createBevelBorder(
                    1));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void mouseReleased(MouseEvent e) {
        if ((e.getComponent() instanceof JButton) &&
                e.getComponent().isEnabled()) {
            ((JButton) e.getComponent()).setBorder(BorderFactory.createBevelBorder(
                    2));
        }
    }

    /**
     * DOCUMENT ME!
     * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
     * @author $Author: hasloe $
     * @version $Revision: 1.3 $
     */
    public class Eventeintrag {
        /** Holds value of property DOCUMENT ME! */
        public String name;

        /** Holds value of property DOCUMENT ME! */
        public AbstractButton belauschter;

        /** Holds value of property DOCUMENT ME! */
        public Object lauscher;
    }
}
