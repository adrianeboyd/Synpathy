/*
 * File:     GraphViewerScrollPane.java
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

/* This program is free software; you can redistribute it and/or modify
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
package ims.tiger.gui.tigergraphviewer;

import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JScrollPane;
import javax.swing.JViewport;


/**
 * $Id: GraphViewerScrollPane.java,v 1.3 2006/08/23 08:54:56 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.3 $
 */
public class GraphViewerScrollPane extends JScrollPane implements KeyListener {
    /**
     * Creates a new GraphViewerScrollPane object.
     */
    public GraphViewerScrollPane() {
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);

        getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        getViewport().setBackground(GraphConstants.panelBackgroundColor);

        verticalScrollBar.setUnitIncrement(GraphConstants.unitIncrement);
        horizontalScrollBar.setUnitIncrement(GraphConstants.unitIncrement);
        verticalScrollBar.setValue(verticalScrollBar.getMaximum() -
            verticalScrollBar.getVisibleAmount());
        horizontalScrollBar.setValue(0);

        addKeyListener(this);
    }

    /**
     *
     *
     * @param e DOCUMENT ME!
     */
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (0 < horizontalScrollBar.getValue()) {
                horizontalScrollBar.setValue(horizontalScrollBar.getValue() -
                    GraphConstants.unitIncrement);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            if (0 < verticalScrollBar.getValue()) {
                verticalScrollBar.setValue(verticalScrollBar.getValue() -
                    GraphConstants.unitIncrement);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (horizontalScrollBar.getValue() < (horizontalScrollBar.getMaximum() -
                    horizontalScrollBar.getVisibleAmount())) {
                horizontalScrollBar.setValue(horizontalScrollBar.getValue() +
                    GraphConstants.unitIncrement);
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            if (verticalScrollBar.getValue() < (verticalScrollBar.getMaximum() -
                    verticalScrollBar.getVisibleAmount())) {
                verticalScrollBar.setValue(verticalScrollBar.getValue() +
                    GraphConstants.unitIncrement);
            }
        }
    }

    /**
     *
     * @param e DOCUMENT ME!
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     *
     * @param e DOCUMENT ME!
     */
    public void keyTyped(KeyEvent e) {
    }
}
