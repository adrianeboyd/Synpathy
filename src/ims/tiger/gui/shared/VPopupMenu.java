/*
 * File:     VPopupMenu.java
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
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;


/** Standard-PopupMenu (vertikale Ausrichtung). */
public class VPopupMenu extends JPopupMenu {
    private int iwidth;

    /**
     * DOCUMENT ME!
     *
     * @param invoker DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     */
    public void show(Component invoker, int x, int y) {
        Point p = getPopupMenuOrigin(invoker, x, y);
        iwidth = 0;
        super.show(invoker, p.x, p.y);
    }

    /**
     * DOCUMENT ME!
     *
     * @param invoker DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     * @param width DOCUMENT ME!
     */
    public void show(Component invoker, int x, int y, int width) {
        Point p = getPopupMenuOrigin(invoker, x, y);
        iwidth = width;
        super.show(invoker, p.x, p.y);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Dimension getPreferredSize() {
        if (iwidth > 0) {
            Dimension dim = super.getPreferredSize();

            return new Dimension(iwidth, (int) dim.getHeight());
        } else {
            return super.getPreferredSize();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param invoker DOCUMENT ME!
     * @param x DOCUMENT ME!
     * @param y DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Point getPopupMenuOrigin(Component invoker, int x, int y) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension pmSize = this.getSize();

        if (pmSize.width == 0) {
            pmSize = this.getPreferredSize();
        }

        Point absp = new Point(x, y);
        SwingUtilities.convertPointToScreen(absp, invoker);

        int aleft = absp.x + pmSize.width;
        int abottom = absp.y + pmSize.height;

        if (aleft > screenSize.width) {
            x -= (aleft - screenSize.width);
        }

        if (abottom > screenSize.height) {
            y -= (abottom - screenSize.height);
        }

        return new Point(x, y);
    }
}
