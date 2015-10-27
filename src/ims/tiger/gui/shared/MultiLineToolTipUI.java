/*
 * File:     MultiLineToolTipUI.java
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

import org.apache.log4j.Logger;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.plaf.metal.MetalToolTipUI;


/** Mehrzeiliges Tooltip. */
public class MultiLineToolTipUI extends MetalToolTipUI {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(MultiLineToolTipUI.class);
    private String[] strs;
    private int maxWidth = 0;
    private Font font;

    /**
     * Creates a new MultiLineToolTipUI instance
     *
     * @param font DOCUMENT ME!
     */
    public MultiLineToolTipUI(Font font) {
        super();
        this.font = font;
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param c DOCUMENT ME!
     */
    public void paint(Graphics g, JComponent c) {
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        Dimension size = c.getSize();

        g.setColor(c.getBackground());

        g.fillRect(0, 0, size.width, size.height);
        g.setColor(c.getForeground());

        if (strs != null) {
            for (int i = 0; i < strs.length; i++) {
                g.drawString(strs[i], 3,
                    ((metrics.getHeight()) * (i + 1)) -
                    (metrics.getAscent() / 2) + (metrics.getDescent() / 2) + 1);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param c DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getPreferredSize(JComponent c) {
        FontMetrics metrics = c.getFontMetrics(font);
        String tipText = ((JToolTip) c).getTipText();

        if (tipText == null) {
            tipText = "";
        }

        BufferedReader br = new BufferedReader(new StringReader(tipText));
        String line;
        int maxWidth = 0;
        Vector v = new Vector();

        try {
            while ((line = br.readLine()) != null) {
                int width = SwingUtilities.computeStringWidth(metrics, line);

                maxWidth = (maxWidth < width) ? width : maxWidth;

                v.addElement(line);
            }
        } catch (IOException ex) {
            logger.error("Error during calculation of tooltip", ex);
        }

        int lines = v.size();

        if (lines < 1) {
            strs = null;
            lines = 1;
        } else {
            strs = new String[lines];

            int i = 0;

            for (Enumeration e = v.elements(); e.hasMoreElements(); i++) {
                strs[i] = (String) e.nextElement();
            }
        }

        int height = metrics.getHeight() * lines;

        this.maxWidth = maxWidth;

        return new Dimension(maxWidth + 6, height + 4);
    }
}
