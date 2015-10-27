/*
 * File:     TIGERGraphEditor.java
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
package ims.tiger.gui.tigergrapheditor;

import ims.tiger.gui.shared.AboutWindow;
import ims.tiger.gui.shared.ImageLoader;

import ims.tiger.gui.tigergraphviewer.GraphViewerContentPane;
import ims.tiger.gui.tigergraphviewer.TIGERGraphViewer;


/**
 * $Id: TIGERGraphEditor.java,v 1.13 2006/12/14 13:32:25 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.13 $
 */
public class TIGERGraphEditor extends TIGERGraphViewer {
    private static String version = "1.0";

    /**
     * Creates a new TIGERGraphEditor object.
     *
     * @param tigersearch_dir DOCUMENT ME!
     * @param install_dir DOCUMENT ME!
     * @param working_dir DOCUMENT ME!
     * @param textCorpusMode DOCUMENT ME!
     * @param local DOCUMENT ME!
     * @param user_dir DOCUMENT ME!
     */
    public TIGERGraphEditor(String tigersearch_dir, String install_dir,
        String working_dir, final boolean textCorpusMode, boolean local,
        String user_dir) {
        super(tigersearch_dir, install_dir, working_dir, textCorpusMode, local,
            user_dir);
        setTitle("Synpathy");
    }

    /**
     * DOCUMENT ME!
     *
     * @param install_dir DOCUMENT ME!
     * @param user_dir DOCUMENT ME!
     * @param working_dir DOCUMENT ME!
     * @param textCorpusMode DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected GraphViewerContentPane getContentPane(String install_dir,
        String user_dir, String working_dir, boolean textCorpusMode) {
        return new GraphEditorContentPane(install_dir, user_dir, working_dir,
            textCorpusMode);
    }

    /**
     * DOCUMENT ME!
     */
    protected void setLogo() {
        ImageLoader loader = new ImageLoader();
        setIconImage(loader.loadImage("synpathy_logo_16.gif"));
    }

    /**
     * DOCUMENT ME!
     */
    protected void showAboutWindow() {
        String[] names = new String[2];
        names[0] = "IMS, University of Stuttgart (TIGER Project)";
        names[1] = "Max Planck Institute for Psycholinguistics";

        String title = "Synpathy " + version;
        new AboutWindow(this, title, true, names);
    }
}
