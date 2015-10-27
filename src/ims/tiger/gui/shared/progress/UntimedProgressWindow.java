/*
 * File:     UntimedProgressWindow.java
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
package ims.tiger.gui.shared.progress;

import ims.tiger.gui.shared.ImageLoader;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


/** Definiert das einfachste Progress-Fenster.*/
public class UntimedProgressWindow extends ProgressWindow {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(UntimedProgressWindow.class);

    /* GUI-Attribute */
    private JPanel contentPane;
    private JLabel imgLabel;
    private JLabel textLabel;
    private ImageLoader loader;
    private ImageIcon runningTiger;

    /** Erzeugen des Fensters */
    public UntimedProgressWindow(Frame parent, String window_title,
        ProgressTaskInterface task, byte mode) {
        super(parent, window_title, task, mode);
        enableInputMethods(false);
    }

    /** Erzeugen des Fensters */
    public UntimedProgressWindow(Dialog parent, String window_title,
        ProgressTaskInterface task, byte mode) {
        super(parent, window_title, task, mode);
        enableInputMethods(false);
    }

    /** Fensteraufbau */
    protected void init() {
        getContentPane().setLayout(new BorderLayout());

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));

        loader = new ImageLoader();
        runningTiger = new ImageIcon(loader.loadImage(
                    ims.tiger.system.Images.TIGERRUN));

        imgLabel = new JLabel("     Task in Progress...     ", runningTiger,
                SwingConstants.CENTER);
        imgLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        imgLabel.setVerticalTextPosition(SwingConstants.BOTTOM);
        imgLabel.setPreferredSize(imgLabel.getPreferredSize());

        contentPane.add(imgLabel, BorderLayout.CENTER);

        setContentPane(contentPane);
        setResizable(false); //Groesse nicht veraenderbar!
        pack();
    }

    /** Stoesst die Erledigung der Aufgabe an. */
    public void start() {
        thread = new ProgressThread(this, task);
        thread.start();
    }

    /** Uebeschreibt die Ereignisbehandlung: Das Schliessen des Fensters wird
     *  abgeblockt, stattdessen die Evaluation gestoppt. */
    protected void processWindowEvent(WindowEvent e) {
    }

    /** Ereignis-Behandlung: z.Zt. nur Cancel-Button. */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Cancel") {
            stop = true;
        }
    }

    /** Die Statusmeldung wird aktualisiert. */
    public void setMessage(String message) {
        imgLabel.setText(message);
    }

    /** Die Statusmeldung wird aktualisiert. */
    public void setMessage1(String message) {
        imgLabel.setText(message);
    }

    /** Die Statusmeldung wird aktualisiert. */
    public void setMessage2(String message) {
    }

    /** Der Fortschrittsbalken wird aktualisiert. */
    public void setProgress(int progress) {
    }

    /** Ist der Cancen-Button aktiviert worden? */
    public boolean isStopped() {
        return false;
    }

    /** Die Verarbeitung ist beendet. */
    public void finished() {
        this.setVisible(false);
    }

    /** Raeumt den Thread und sich selbst sauber auf. */
    public void dispose() {
        try {
            thread.join();
        } catch (Exception e) {
            logger.error("Unexpected exception", e);
        }

        super.dispose();
    }
}
