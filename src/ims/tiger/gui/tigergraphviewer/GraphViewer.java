/*
 * File:     GraphViewer.java
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

package ims.tiger.gui.tigergraphviewer;

import ims.tiger.gui.tigergraphviewer.forest.Forest;
import ims.tiger.gui.tigergraphviewer.forest.ForestException;


/**
 * $Id: GraphViewer.java,v 1.4 2006/08/23 09:49:59 klasal Exp $
 *
 * @author klasal
 */
public interface GraphViewer {
    /**
     * starts visualization
     *
     * @param s forest to be shown
     *
     * @throws ForestException
     */
    void visualizeForest(Forest s) throws ForestException;
}
