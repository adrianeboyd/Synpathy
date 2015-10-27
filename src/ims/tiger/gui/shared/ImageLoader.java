/*
 * File:     ImageLoader.java
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

import java.awt.Image;
import java.awt.Toolkit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


/** Eine Klasse zum Einladen von Bildern aus einem fest vorgegebenen Package.
 *  Auf diese Weise koennen Bilder zentral verwaltet werden. */
public class ImageLoader {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(ImageLoader.class);
    private String IMAGEPATH = "/ims/tiger/images/";

    /**
     * DOCUMENT ME!
     *
     * @param imgName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Image loadImage(String imgName) {
        // Falls Bild nicht vorhanden => WARNUNGS-Bild anzeigen!
        if (this.getClass().getResourceAsStream(IMAGEPATH + imgName) == null) {
            logger.error("Image " + imgName + " not found in classpath.");
            imgName = ims.tiger.system.Images.WARNING;
        }

        try {
            InputStream is = this.getClass().getResourceAsStream(IMAGEPATH +
                    imgName);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int c;

            try {
                while ((c = is.read()) >= 0) {
                    baos.write(c);
                }
            } catch (IOException e) {
                logger.error("Error loading image: " + e.getMessage());
            }

            Image img = Toolkit.getDefaultToolkit().createImage(baos.toByteArray());

            return img;
        } catch (Exception ex) {
            logger.error("Error loading image " + imgName, ex);

            return null;
        }
    }
}
