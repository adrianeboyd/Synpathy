/*
 * File:     SplashWindow.java
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

import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;


/** Ein kleines SplashWindow mit dem Tiger-Logo fuer den Programmstart. */
public class SplashWindow extends JWindow {
    /**
     * Creates a new SplashWindow instance
     *
     * @param message DOCUMENT ME!
     */
    public SplashWindow(String message) {
        super();

        // Content-Objekt holen
        JPanel content = (JPanel) this.getContentPane();

        // Position
        int width = 329;
        int height = 190;
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - width) / 2;
        int y = (screen.height - height) / 2;
        this.setBounds(x, y, width, height);

        // Content besetzen
        ImageLoader loader = new ImageLoader();
        Image icon = loader.loadImage(ims.tiger.system.Images.TS_LOGO);

        JLabel label = new JLabel(new ImageIcon(icon));
        label.setBackground(Color.white);
        label.setForeground(Color.white);

        JLabel mlabel = new JLabel(message, JLabel.CENTER);
        mlabel.setFont(new Font("Dialog", Font.BOLD, 14));
        content.add(label, BorderLayout.CENTER);
        content.add(mlabel, BorderLayout.SOUTH);
        content.setBorder(BorderFactory.createLineBorder(Color.black, 1));
    }
}
