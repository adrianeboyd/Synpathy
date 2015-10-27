/*
 * File:     StringArraySort.java
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

import java.text.CollationKey;
import java.text.Collator;

import java.util.Locale;


/** Kleines Utility-Objekt zur String-Sortierung. */
public class StringArraySort {
    /**
     * Creates a new StringArraySort instance
     */
    public StringArraySort() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param keys DOCUMENT ME!
     */
    public static void sortArray(CollationKey[] keys) {
        CollationKey tmp;

        for (int i = 0; i < keys.length; i++) {
            for (int j = i + 1; j < keys.length; j++) {
                if (keys[i].compareTo(keys[j]) > 0) {
                    tmp = keys[i];
                    keys[i] = keys[j];
                    keys[j] = tmp;
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param fileNames DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String[] getSortedArray(String[] fileNames) {
        String[] result = new String[fileNames.length];
        Collator enUSCollator = Collator.getInstance(new Locale("en", "US"));
        CollationKey[] keys = new CollationKey[fileNames.length];

        for (int k = 0; k < keys.length; k++) {
            keys[k] = enUSCollator.getCollationKey(fileNames[k]);
        }

        sortArray(keys);

        for (int i = 0; i < keys.length; i++) {
            result[i] = keys[i].getSourceString();
        }

        return result;
    }
}
