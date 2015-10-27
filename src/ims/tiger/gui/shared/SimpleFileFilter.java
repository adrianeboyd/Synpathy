/*
 * File:     SimpleFileFilter.java
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

import java.io.File;

import javax.swing.filechooser.FileFilter;


/** Ein simpler Datei-Filter. */
public class SimpleFileFilter extends FileFilter {
    private String[] extensions;
    private String description;

    /**
     * Creates a new SimpleFileFilter instance
     *
     * @param ext DOCUMENT ME!
     */
    public SimpleFileFilter(String ext) {
        this(new String[] { ext }, null);
    }

    /**
     * Creates a new SimpleFileFilter instance
     *
     * @param exts DOCUMENT ME!
     * @param descr DOCUMENT ME!
     */
    public SimpleFileFilter(String[] exts, String descr) {
        extensions = new String[exts.length];

        for (int i = exts.length - 1; i >= 0; i--) {
            extensions[i] = exts[i].toLowerCase();
        }

        description = ((descr == null) ? (exts[0] + " file") : descr);
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String name = f.getName().toLowerCase();

        for (int i = extensions.length - 1; i >= 0; i--) {
            if (name.endsWith(extensions[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return description;
    }
}
