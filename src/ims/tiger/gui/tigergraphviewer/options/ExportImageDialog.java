/*
 * File:     ExportImageDialog.java
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
package ims.tiger.gui.tigergraphviewer.options;

import ims.tiger.gui.shared.*;

import ims.tiger.gui.shared.progress.*;

import ims.tiger.gui.tigergraphviewer.draw.*;

import ims.tiger.util.UtilitiesCollection;

import org.apache.log4j.*;

import org.jdom.*;

import org.jdom.input.*;

import java.awt.*;
import java.awt.event.*;

import java.io.*;

import java.net.URL;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;


/**
 * Fenster zum Exportieren einer einzelnen Grafik.
 */
public class ExportImageDialog extends JDialog implements ActionListener,
    ProgressTaskInterface {
    /** DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(ExportImageDialog.class.getName());
    private static java.util.List css_keys;
    private static java.util.List css_values;
    private Container contentPane;
    private EtchedBorder eBorder = new EtchedBorder(EtchedBorder.LOWERED);
    private File export_dir;
    private GraphPanel panel;
    private GridBagConstraints c;
    private GridBagLayout gbl;
    private JButton cancel;
    private JButton search;
    private JButton submit;
    private JComboBox filterCombo;
    private JComboBox formatCombo;
    private JTextField tofileField;
    private ProgressContainerInterface container;
    private StatusBar statBar; // Status bar
    private String errorMessage;
    private String install_dir;
    private String working_dir;
    private boolean error;

    /* --------------------------------------------------------- */
    /* Fenster-Aufbau                                            */
    /* --------------------------------------------------------- */
    public ExportImageDialog(String install_dir, String working_dir,
        URL lastUsedCorpusURL, GraphPanel panel, JFrame parent)
        throws IOException {
        super(parent, "Export Options", true);

        this.install_dir = install_dir;
        this.working_dir = working_dir;
        export_dir = new File(working_dir);
        this.panel = panel;

        /* a) Allgemeine Eigenschaften */
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.setSize(new Dimension(300, 300));

        EmptyBorder emptyBorder = new EmptyBorder(new Insets(5, 5, 5, 5));
        JPanel interPane = new JPanel();

        /* b) Top Panel  */
        JPanel tpContentPanel = new JPanel(new BorderLayout());
        tpContentPanel.setBorder(eBorder);

        JPanel topPanel = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        topPanel.setLayout(gbl);

        JLabel exportLabel = new JLabel("Export to file: ");
        tofileField = new JTextField(40);

        String fileName;

        if (lastUsedCorpusURL != null) {
            fileName = lastUsedCorpusURL.getPath();

            if (fileName.indexOf('.') > 0) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }

            fileName += ".svg";
        } else {
            fileName = "New.svg";
        }

        tofileField.setText(fileName);
        search = new JButton("Search");
        search.addActionListener(this);

        JLabel formatLabel = new JLabel("Export format: ");
        formatCombo = new JComboBox(ExportImage.formatS);
        formatCombo.setEditable(false);
        formatCombo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    int selection = formatCombo.getSelectedIndex();

                    switch (selection) {
                    case ExportImage.SVG: {
                        if (tofileField.getText().endsWith(".svg") ||
                                tofileField.getText().endsWith(".jpg") ||
                                tofileField.getText().endsWith(".png") ||
                                tofileField.getText().endsWith(".tif")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 4) +
                                ".svg");
                        } else {
                            tofileField.setText(tofileField.getText() + ".svg");
                        }

                        break;
                    }

                    case ExportImage.JPG: {
                        if (tofileField.getText().endsWith(".svg") ||
                                tofileField.getText().endsWith(".jpg") ||
                                tofileField.getText().endsWith(".png") ||
                                tofileField.getText().endsWith(".tif")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 4) +
                                ".jpg");
                        } else {
                            tofileField.setText(tofileField.getText() + ".jpg");
                        }

                        break;
                    }

                    case ExportImage.PNG: {
                        if (tofileField.getText().endsWith(".svg") ||
                                tofileField.getText().endsWith(".jpg") ||
                                tofileField.getText().endsWith(".png") ||
                                tofileField.getText().endsWith(".tif")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 4) +
                                ".png");
                        } else {
                            tofileField.setText(tofileField.getText() + ".png");
                        }

                        break;
                    }

                    case ExportImage.TIFF: {
                        if (tofileField.getText().endsWith(".svg") ||
                                tofileField.getText().endsWith(".jpg") ||
                                tofileField.getText().endsWith(".png") ||
                                tofileField.getText().endsWith(".tif")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 4) +
                                ".tif");
                        } else {
                            tofileField.setText(tofileField.getText() + ".tif");
                        }

                        break;
                    }
                    }
                }
            });

        readCascadingStylesheets(install_dir); // XML-Datei lesen

        JLabel filterLabel = new JLabel("Image filter: ");

        filterCombo = new JComboBox(css_keys.toArray());
        filterCombo.setEditable(false);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(exportLabel, c);
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        gbl.setConstraints(tofileField, c);
        c.gridwidth = 1;
        c.gridx = 2;
        gbl.setConstraints(search, c);
        c.gridx = 0;
        c.gridy = 2;
        gbl.setConstraints(formatLabel, c);
        c.gridx = 1;
        gbl.setConstraints(formatCombo, c);
        c.gridx = 0;
        c.gridy = 3;
        gbl.setConstraints(filterLabel, c);
        c.gridx = 1;
        gbl.setConstraints(filterCombo, c);

        topPanel.add(exportLabel);
        topPanel.add(tofileField);
        topPanel.add(search);

        topPanel.add(formatLabel);
        topPanel.add(formatCombo);

        topPanel.add(filterLabel);
        topPanel.add(filterCombo);

        tpContentPanel.add(topPanel);

        /* g) interPane */
        interPane.setBorder(emptyBorder);
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        interPane.setLayout(gbl);

        submit = new JButton("Submit");
        submit.addActionListener(this);

        Dimension subD = new Dimension(70, 30);
        submit.setMaximumSize(subD);
        submit.setMinimumSize(subD);
        submit.setPreferredSize(subD);
        submit.setSize(subD);
        submit.setBorder(BorderFactory.createEtchedBorder());

        cancel = new JButton("Cancel");
        cancel.addActionListener(this);
        cancel.setMaximumSize(subD);
        cancel.setMinimumSize(subD);
        cancel.setPreferredSize(subD);
        cancel.setSize(subD);
        cancel.setBorder(BorderFactory.createEtchedBorder());

        c.gridwidth = 4;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(tpContentPanel, c);
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 2;
        c.gridy = 1;
        gbl.setConstraints(submit, c);
        c.weightx = 0.0;
        c.gridx = 3;
        gbl.setConstraints(cancel, c);

        interPane.add(tpContentPanel);
        interPane.add(submit);
        interPane.add(cancel);

        statBar = new StatusBar();

        contentPane.add(interPane, BorderLayout.NORTH);
        contentPane.add(statBar, BorderLayout.SOUTH);
        this.setContentPane(contentPane);
        pack();

        /* Direktes Verlassen */
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            });
    }

    /* --------------------------------------------------------- */
    /* Implementation des ProgressTaskInterface                  */
    /* --------------------------------------------------------- */

    /**
     * Stellt den Kontakt zum umgebenden Container her.
     *
     * @param container DOCUMENT ME!
     */
    public void setContainer(ProgressContainerInterface container) {
        this.container = container;
    }

    /* --------------------------------------------------------- */
    /* Fenster-Programmlogik                                     */
    /* --------------------------------------------------------- */
    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();

            if (e.getActionCommand() == "Submit") {
                String name = getToFile();
                File file = new File(name);

                // Dateiname OK?
                if ((name.length() == 0) || name.startsWith("*") ||
                        name.startsWith(".")) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a filename.", "Parameter error",
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }

                // Pfad absolut?
                if (!file.isAbsolute()) {
                    if (!working_dir.endsWith(File.separator)) {
                        working_dir += File.separator;
                    }

                    name = working_dir + name;

                    setToFile(name);
                    file = new File(name);
                }

                // Existiert Datei bereits?
                if (file.exists()) {
                    Object[] options = { "Overwrite", "Cancel" };
                    int n = JOptionPane.showOptionDialog(this,
                            "File exists: " + name, "Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);

                    // -1 wenn WINDOW_CLOSED_EVENT ausgeloest wird
                    if ((n == JOptionPane.NO_OPTION) || (n == -1)) {
                        return;
                    }
                }

                // Fenster mit Aufgabe initialisieren
                UntimedProgressWindow win = new UntimedProgressWindow(this,
                        "Working...", this, ProgressWindow.WITH_ONE_MESSAGE);

                // Aufgabe starten
                win.start();

                // Fenster sichtbar machen
                win.setLocationRelativeTo(this);
                win.setVisible(true);

                // Fenster zerstoeren = Ressourcen freigeben
                win.dispose();

                if (error) {
                    JOptionPane.showMessageDialog(this,
                        "Error during the export:\n" + errorMessage,
                        "Export error", JOptionPane.ERROR_MESSAGE);
                    statBar.setContent(" ");
                    this.update(this.getGraphics());

                    return;
                }

                statBar.setContent("Done.");
                this.update(this.getGraphics());

                this.setVisible(false);
            }

            if (e.getActionCommand() == "Cancel") {
                this.setVisible(false);
            }

            if (e.getActionCommand() == "Search") {
                JFileChooser chooser = new JFileChooser();
                chooser.setApproveButtonText("Select");

                if (export_dir != null) {
                    chooser.setCurrentDirectory(export_dir);
                }

                javax.swing.filechooser.FileFilter svg = new SimpleFileFilter(ExportImage.formatS,
                        "Images (*.svg, *.jpg, *.png, *.tif)");
                chooser.addChoosableFileFilter(svg);

                chooser.setFileFilter(svg);

                int returnVal = chooser.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    tofileField.setText(chooser.getSelectedFile()
                                               .getAbsolutePath());
                    export_dir = chooser.getCurrentDirectory();
                }
            }

            // Event-Handling ENDE
        } catch (OutOfMemoryError err) {
            logger.error(
                "Out of memory error in ExportForestDialog action listener.");
            UtilitiesCollection.showOutOfMemoryMessage(this, false, true);
        } catch (Error err) {
            logger.error("Java error in ExportForestDialog action listener: " +
                err.getMessage(), err);
        } catch (Exception err) {
            logger.error(
                "Java exception in ExportForestDialog action listener: " +
                err.getMessage(), err);
        }
    }

    /**
     * Ist die Aufgabe durch einen Fehler zum Ende gekommen?
     *
     * @return DOCUMENT ME!
     */
    public boolean endedWithError() {
        return error;
    }

    /**
     * Ist die Aufgabe abgebrochen worden?
     *
     * @return DOCUMENT ME!
     */
    public boolean endedWithStop() {
        return false;
    }

    /**
     * Ist die Aufgabe ohne Zwischenfaelle beendet worden?
     *
     * @return DOCUMENT ME!
     */
    public boolean endedWithSuccess() {
        return !error;
    }

    /**
     * Startet die eigentliche Aufgabe.
     */
    public void startTask() {
        error = false;

        // Parameter einholen
        String name = getToFile();

        File f = new File(name);
        int format_selection = getSelectedFormat();
        int style_selection = getSelectedFilter();

        logger.info("Starting the export process");
        statBar.setContent("Exporting the image ...");
        this.update(this.getGraphics());

        try {
            if ((name.length() == 0) || name.startsWith("*") ||
                    name.startsWith(".")) {
                throw new IOException("Please enter a filename.");
            }

            String css = (style_selection == 0) ? null
                                                : (String) css_values.get(style_selection);
            ExportImage.exportImage(panel.getCurrentDisplaySentence(),
                panel.getHeader(), panel.getGraphViewerConfiguration(),
                format_selection, f, css, container);
        } // try
        catch (Exception exp) {
            logger.error("Error generating image output: " + exp.getMessage());
            error = true;
            errorMessage = exp.getMessage();
        }
    }

    /* Zielfilter */
    private int getSelectedFilter() {
        return filterCombo.getSelectedIndex();
    }

    /* --------------------------------------------------------- */
    /* Zugriffsmethoden Exportparameter                          */
    /* --------------------------------------------------------- */
    /* Zielformat */
    private int getSelectedFormat() {
        return formatCombo.getSelectedIndex();
    }

    private void setToFile(String name) {
        tofileField.setText(name);
    }

    /* Zieldatei */
    private String getToFile() {
        return tofileField.getText();
    }

    /* Liest die vordefinierten CSS ein. */
    private static void readCascadingStylesheets(String install_dir)
        throws IOException {
        /* SAX-Parser einbinden */
        SAXBuilder builder = new SAXBuilder(false);

        /* Dokument parsen */
        String uri = null;

        try {
            uri = UtilitiesCollection.createURI(install_dir + File.separator +
                    "config" + File.separator + "svgfilter.xml");

            if ((install_dir.indexOf("!") > -1) &&
                    !install_dir.startsWith("jar:")) {
                uri = "jar:" + install_dir + "/config/svgfilter.xml";
            }
        } catch (Exception e) {
            throw new IOException(e.getMessage());
        }

        org.jdom.Document doc = null;

        try {
            doc = builder.build(new java.net.URL(uri));
        } catch (JDOMException e) {
            throw new IOException(e.getMessage());
        }

        Element root = doc.getRootElement();
        java.util.List filters = root.getChildren();

        String name;
        String css;
        Element filter;
        css_keys = new LinkedList();
        css_values = new LinkedList();

        for (int i = 0; i < filters.size(); i++) {
            filter = (Element) filters.get(i);
            name = filter.getAttributeValue("name");
            css = filter.getText();
            css_keys.add(name);
            css_values.add(css);
        }

        css_keys.add(0, "none");
        css_values.add(0, "");
    }
}
