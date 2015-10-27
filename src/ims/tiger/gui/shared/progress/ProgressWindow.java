/*
 * File:     ProgressWindow.java
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

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.*;


/** Definiert das Fenster, das die Meldungen anzeigt und den
 *  Abbruch-Button zur Verfuegung stellt.
 */
public class ProgressWindow extends JDialog implements ActionListener {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(ProgressWindow.class);

    /* Modi */

    /** Holds value of property DOCUMENT ME! */
    public static byte WITH_NO_CANCEL = 0;

    /** Holds value of property DOCUMENT ME! */
    public static byte WITH_ONE_MESSAGE = 1;

    /** Holds value of property DOCUMENT ME! */
    public static byte WITH_TWO_MESSAGES = 2;
    private JPanel contentPane;

    /* GUI-Attribute */
    private JButton cancel_button;
    private JLabel message1;
    private JLabel message2;
    private JLabel pbl;
    private JProgressBar progress_bar;

    /* Kommunikations-Attribute */

    /** Holds value of property DOCUMENT ME! */
    protected boolean stop = false;

    /** Holds value of property DOCUMENT ME! */
    protected ProgressTaskInterface task;

    /** Holds value of property DOCUMENT ME! */
    protected ProgressThread thread;

    /** Holds value of property DOCUMENT ME! */
    protected byte mode;

    /** Erzeugen des Fensters */
    public ProgressWindow(Frame parent, String window_title,
        ProgressTaskInterface task, byte mode) {
        super(parent, window_title, true);
        enableInputMethods(false);
        this.task = task;
        this.mode = mode;
        init();
        this.setLocationRelativeTo(parent);
    }

    /** Erzeugen des Fensters */
    public ProgressWindow(Dialog parent, String window_title,
        ProgressTaskInterface task, byte mode) {
        super(parent, window_title, true);
        enableInputMethods(false);
        this.mode = mode;
        this.task = task;
        init();
        this.setLocationRelativeTo(parent);
    }

    /** Fensteraufbau */
    protected void init() {
        getContentPane().setLayout(new BorderLayout(3, 3));

        contentPane = new JPanel();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        contentPane.setLayout(gbl);

        message1 = new JLabel();
        message1.setText(
            "                                                          ");
        message1.setPreferredSize(message1.getPreferredSize());

        cancel_button = new JButton("Cancel");
        cancel_button.setSize(83, 29);
        cancel_button.setBorder(BorderFactory.createEtchedBorder());
        cancel_button.addActionListener(this);

        message2 = new JLabel();
        message2.setText(
            "                                                          ");
        message2.setPreferredSize(message2.getPreferredSize());

        pbl = new JLabel();
        pbl.setText("Progress");
        pbl.setPreferredSize(pbl.getPreferredSize());

        progress_bar = new JProgressBar();

        Dimension dimpb = progress_bar.getPreferredSize();
        progress_bar.setPreferredSize(dimpb);
        progress_bar.setMinimum(0);
        progress_bar.setMaximum(100);
        progress_bar.setValue(0);

        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(10, 10, 10, 10);
        c.gridwidth = 3;
        c.gridheight = 1;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.ipadx = 0;
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 0;
        gbl.setConstraints(message1, c);
        c.gridwidth = 1;
        c.gridheight = 2;
        c.ipadx = 16;
        c.ipady = 8;
        c.gridx = 3;
        c.insets = new Insets(10, 5, 5, 10);
        gbl.setConstraints(cancel_button, c);
        c.insets = new Insets(0, 10, 5, 10);
        c.gridwidth = 3;
        c.gridheight = 1;
        c.ipadx = 0;
        c.ipady = 0;
        c.gridx = 0;
        c.gridy = 1;
        gbl.setConstraints(message2, c);
        c.insets = new Insets(5, 10, 5, 5);
        c.gridwidth = 1;
        c.gridy = 2;
        gbl.setConstraints(pbl, c);
        c.insets = new Insets(5, 5, 5, 10);
        c.gridwidth = 3;
        c.gridx = 1;
        c.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(progress_bar, c);

        if (mode == WITH_NO_CANCEL) {
            contentPane.add(message1);
        }

        if (mode == WITH_ONE_MESSAGE) {
            contentPane.add(message1);
            contentPane.add(cancel_button);
            contentPane.add(pbl);
            contentPane.add(progress_bar);
        }

        if (mode == WITH_TWO_MESSAGES) {
            contentPane.add(message1);
            contentPane.add(cancel_button);
            contentPane.add(message2);
            contentPane.add(pbl);
            contentPane.add(progress_bar);
        }

        setContentPane(contentPane);
        setSize(600, 100);
        setResizable(false); //Groesse nicht veraenderbar!
        pack();
    }

    /** Uebeschreibt die Ereignisbehandlung: Das Schliessen des Fensters wird
     *  abgeblockt, stattdessen die Evaluation gestoppt. */
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            stop = true;
        } else {
            super.processWindowEvent(e);
        }
    }

    /** Stoesst die Erledigung der Aufgabe an. */
    public void start() {
        thread = new ProgressThread(this, task);
        thread.start();
    }

    /** Ereignis-Behandlung: z.Zt. nur Cancel-Button. */
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand() == "Cancel") {
            stop = true;
        }
    }

    /** Die Statusmeldung wird aktualisiert. */
    public void setMessage(String message) {
        message1.setText(message);
    }

    /** Die Statusmeldung wird aktualisiert. */
    public void setMessage1(String message) {
        message1.setText(message);
    }

    /** Die Statusmeldung wird aktualisiert. */
    public void setMessage2(String message) {
        message2.setText(message);
    }

    /** Der Fortschrittsbalken wird aktualisiert. */
    public void setProgress(int progress) {
        progress_bar.setValue(progress);
    }

    /** Ist der Cancen-Button aktiviert worden? */
    public boolean isStopped() {
        return stop;
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
