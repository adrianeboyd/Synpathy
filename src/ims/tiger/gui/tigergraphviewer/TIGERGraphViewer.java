/*
 * File:     TIGERGraphViewer.java
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
/*
 * IMS, University of Stuttgart
 * TIGER Treebank Project
 * Copyright 1999-2003, all rights reserved
 */
package ims.tiger.gui.tigergraphviewer;

import ims.tiger.gui.shared.AboutWindow;
import ims.tiger.gui.shared.ImageLoader;

import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;

import ims.tiger.gui.tigergraphviewer.forest.Forest;
import ims.tiger.gui.tigergraphviewer.forest.ForestException;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;


/**
 * Diese Klasse zeigt einen Wald von Baeumen an. Sie ist die Hauptklasse des GraphViewer-Pakets.
 */
public class TIGERGraphViewer extends JFrame implements GraphViewer {
    /** DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(TIGERGraphViewer.class.getName());

    /**DOCUMENT ME! */
    protected final GraphViewerContentPane contentPane;
    private TIGERGraphViewerConfiguration config;
    private Action showAboutWindowAction;

    /**
     * Konstruktor gestaltet die Oberflaeche und weist eine leere Flaeche aus.
     *
     * @param tigersearch_dir DOCUMENT ME!
     * @param install_dir DOCUMENT ME!
     * @param working_dir DOCUMENT ME!
     * @param textCorpusMode DOCUMENT ME!
     * @param local DOCUMENT ME!
     * @param user_dir DOCUMENT ME!
     */
    public TIGERGraphViewer(String tigersearch_dir, String install_dir,
        String working_dir, final boolean textCorpusMode, boolean local,
        String user_dir) {
        setTitle("TIGERGraphViewer");

        enableInputMethods(false);

        /* 1. Konfiguration festlegen */
        if (local) { // lokaler Zugriff moeglich
            config = new TIGERGraphViewerConfiguration(tigersearch_dir,
                    install_dir, user_dir);

            try {
                config.readConfiguration();
            } catch (Exception e) {
                logger.error(
                    "Error reading the forest viewer configuration file: " +
                    e.getMessage());
            }
        }

        setLogo();

        setBounds(GraphConstants.windowX, GraphConstants.windowY,
            GraphConstants.windowWidth, GraphConstants.windowHeight);

        contentPane = getContentPane(install_dir, user_dir, working_dir,
                textCorpusMode);

        JMenuBar menuBar = contentPane.createMenuBar();

        showAboutWindowAction = new AbstractAction("About this tool") {
                    public void actionPerformed(ActionEvent e) {
                        showAboutWindow();
                    }
                };

        JMenu about = new JMenu("About");
        about.add(new JMenuItem(showAboutWindowAction));
        menuBar.add(about);
        setJMenuBar(menuBar);

        setContentPane(contentPane);
        pack();
        setSize(GraphConstants.windowWidth, GraphConstants.windowHeight);

        setVisible(true);

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    setVisible(false);

                    if (textCorpusMode) {
                        System.exit(0);
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     */
    protected void setLogo() {
        ImageLoader loader = new ImageLoader();
        setIconImage(loader.loadImage("tree.gif"));
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
        return new GraphViewerContentPane(install_dir, user_dir, working_dir,
            textCorpusMode);
    }

    /**
     * DOCUMENT ME!
     */
    protected void showAboutWindow() {
        String[] names = new String[2];
        names[0] = "IMS, University of Stuttgart (TIGER Project)";
        names[1] = "Max Planck Institute for Psycholinguistics";

        String title = "TIGERGraphViewer " +
            ims.tiger.system.Constants.VERSION;
        new AboutWindow(this, title, true, names);
    }

    /**
     * DOCUMENT ME!
     *
     * @param forest DOCUMENT ME!
     *
     * @throws ForestException DOCUMENT ME!
     */
    public void visualizeForest(Forest forest) throws ForestException {
        contentPane.visualizeForest(forest);
    }
}
