/*
 * File:     IndexLoadProgressHandler.java
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
package ims.tiger.index.reader;


/** Ein Interface fuer eine Handler, der auf Meldungen des Index-Ladens
 *  reagiert.
 */
public interface IndexLoadProgressHandler {
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setProgressValue(int value);

    /**
     * DOCUMENT ME!
     *
     * @param message DOCUMENT ME!
     */
    public void setProgressMessage(String message);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isAborted();
}
