/*
 * File:     ExportForestDialog.java
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

import ims.tiger.gui.shared.SimpleFileFilter;

import ims.tiger.gui.tigergraphviewer.forest.Forest;

import ims.tiger.util.RegExpException;
import ims.tiger.util.RegExpToolbox;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.*;

import java.io.File;
import java.io.IOException;

import java.util.LinkedList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;


/** Dialog-Fenster zum Export eines SVG-Waldes. */
public class ExportForestDialog extends JDialog implements ActionListener,
    ItemListener {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(ExportForestDialog.class);
    private String[] typeS = { "normal (.svg)", "compressed (.svgz)" };
    private String[] extS = { "svg", "svgz" };
    private Container contentPane;
    private GridBagLayout gbl;
    private GridBagConstraints c;
    private GridLayout gl;
    private JPanel interrtpcp;
    private JButton search;
    private JButton submit;
    private JButton cancel;

    /** Holds value of property DOCUMENT ME! */
    EtchedBorder eBorder = new EtchedBorder(EtchedBorder.LOWERED);
    private JComboBox typeCombo;
    private JComboBox nameCombo;
    private ButtonGroup rbg01;
    private ButtonGroup rbg02;
    private JTextField fromF;
    private JTextField toF;
    private JTextField selectField;
    private JTextField tofileField;
    private JCheckBox matchWithinSentCB;
    private JCheckBox matchHighlightingCB;
    private JCheckBox matchInfoCB;
    private JRadioButton selectedBgRB;
    private JRadioButton neutralBgRB;
    private JRadioButton allRB;
    private JRadioButton fromtoRB;
    private JRadioButton selectRB;
    private File export_dir;
    private String install_dir;
    private String working_dir;
    private Forest forest;
    private int[] matching_sentences;
    private boolean submitted;

    /* --------------------------------------------------------- */
    /* Fenster-Aufbau                                            */
    /* --------------------------------------------------------- */
    public ExportForestDialog(String install_dir, String working_dir,
        Frame parent) throws IOException {
        super(parent, "SVG match forest export", true);

        this.install_dir = install_dir;
        this.working_dir = working_dir;
        export_dir = new File(working_dir);

        /* a) Allgemeine Eigenschaften */
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.setSize(new Dimension(300, 500));

        EmptyBorder emptyBorder = new EmptyBorder(new Insets(5, 5, 5, 5));
        JPanel interPane = new JPanel();

        /* b) Top Panel  */
        JPanel tpContentPanel = new JPanel(new BorderLayout());
        tpContentPanel.setBorder(eBorder);

        JPanel topPanel = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        topPanel.setLayout(gbl);

        JLabel exportLabel = new JLabel("SVG file name: ");
        tofileField = new JTextField("*.svg", 40);
        search = new JButton("Search");
        search.addActionListener(this);

        JLabel typeLabel = new JLabel("SVG type: ");
        typeCombo = new JComboBox(typeS);
        typeCombo.setEditable(false);
        typeCombo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    int selection = typeCombo.getSelectedIndex();

                    switch (selection) {
                    case 0: {
                        if (tofileField.getText().endsWith(".svg")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 4) +
                                ".svg");
                        } else if (tofileField.getText().endsWith(".svgz")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 5) +
                                ".svg");
                        } else {
                            tofileField.setText(tofileField.getText() + ".svg");
                        }

                        break;
                    }

                    case 1: {
                        if (tofileField.getText().endsWith(".svg")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 4) +
                                ".svgz");
                        } else if (tofileField.getText().endsWith(".svgz")) {
                            tofileField.setText(tofileField.getText().substring(0,
                                    tofileField.getText().length() - 5) +
                                ".svgz");
                        } else {
                            tofileField.setText(tofileField.getText() +
                                ".svgz");
                        }

                        break;
                    }
                    }
                }
            });

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
        c.gridx = 2;
        c.gridwidth = 1;
        gbl.setConstraints(search, c);
        c.gridx = 0;
        c.gridy = 2;
        gbl.setConstraints(typeLabel, c);
        c.gridx = 1;
        gbl.setConstraints(typeCombo, c);

        topPanel.add(exportLabel);
        topPanel.add(tofileField);
        topPanel.add(search);
        topPanel.add(typeLabel);
        topPanel.add(typeCombo);
        tpContentPanel.add(topPanel);

        /* c) Middle Panel  */
        JPanel mpContentPanel = new JPanel(new BorderLayout());
        mpContentPanel.setBorder(eBorder);

        JPanel middlePanel = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        middlePanel.setLayout(gbl);

        JLabel includeLabel = new JLabel("Match selection");
        rbg01 = new ButtonGroup();
        allRB = new JRadioButton("All matching graphs", true);
        allRB.addActionListener(this);
        allRB.setActionCommand("all");
        allRB.addItemListener(this);
        rbg01.add(allRB);
        fromtoRB = new JRadioButton("From matching graph", false);
        fromtoRB.addActionListener(this);
        fromtoRB.setActionCommand("fromto");
        fromtoRB.addItemListener(this);
        rbg01.add(fromtoRB);

        Dimension mD = new Dimension(75, 24);

        fromF = new JTextField("");
        fromF.setEditable(true);
        fromF.setMaximumSize(mD);
        fromF.setMinimumSize(mD);
        fromF.setPreferredSize(mD);
        fromF.setSize(mD);

        JLabel toLabel = new JLabel(" to ");
        toLabel.setForeground(Color.black);

        toF = new JTextField("");
        toF.setEditable(true);
        toF.setMaximumSize(mD);
        toF.setMinimumSize(mD);
        toF.setPreferredSize(mD);
        toF.setSize(mD);

        selectRB = new JRadioButton("Select matching graphs: ", false);
        selectRB.addActionListener(this);
        selectRB.setActionCommand("select");
        selectRB.addItemListener(this);
        rbg01.add(selectRB);
        selectField = new JTextField("", 20);

        matchWithinSentCB = new JCheckBox("Include all matches within a graph",
                false);
        matchWithinSentCB.addItemListener(this);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 1, 1);
        gbl.setConstraints(includeLabel, c);
        c.gridy = 1;
        c.insets = new Insets(1, 15, 1, 1);
        gbl.setConstraints(allRB, c);
        c.gridy = 2;
        gbl.setConstraints(fromtoRB, c);
        c.gridx = 1;
        c.insets = new Insets(1, 1, 5, 1);
        gbl.setConstraints(fromF, c);
        c.gridx = 2;
        c.insets = new Insets(1, 12, 1, 1);
        gbl.setConstraints(toLabel, c);
        c.gridx = 3;
        c.insets = new Insets(1, 1, 5, 5);
        gbl.setConstraints(toF, c);
        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(1, 15, 1, 1);
        gbl.setConstraints(selectRB, c);
        c.gridx = 1;
        c.insets = new Insets(1, 1, 1, 5);
        c.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(selectField, c);
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 4;
        gbl.setConstraints(matchWithinSentCB, c);

        middlePanel.add(includeLabel);
        middlePanel.add(allRB);
        middlePanel.add(fromtoRB);
        middlePanel.add(fromF);
        middlePanel.add(toLabel);
        middlePanel.add(toF);
        middlePanel.add(selectRB);
        middlePanel.add(selectField);
        middlePanel.add(matchWithinSentCB);

        mpContentPanel.add(middlePanel);

        /* c) Bottom Panel  */
        JPanel bpContentPanel = new JPanel(new BorderLayout());
        bpContentPanel.setBorder(eBorder);

        JPanel bottomPanel = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        bottomPanel.setLayout(gbl);

        JLabel imgFormatLabel = new JLabel("Image includes...");

        matchHighlightingCB = new JCheckBox("match highlighting.", true);
        matchHighlightingCB.addItemListener(this);

        matchInfoCB = new JCheckBox("match information in navigation bar.", true);
        matchInfoCB.addItemListener(this);

        rbg02 = new ButtonGroup();
        neutralBgRB = new JRadioButton("transparent (recommended) or", true);
        neutralBgRB.addItemListener(this);
        rbg02.add(neutralBgRB);
        selectedBgRB = new JRadioButton("selected background color.", false);
        selectedBgRB.addItemListener(this);
        rbg02.add(selectedBgRB);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(imgFormatLabel, c);
        c.gridy = 1;
        gbl.setConstraints(matchHighlightingCB, c);
        c.gridy = 2;
        gbl.setConstraints(matchInfoCB, c);
        c.gridy = 3;
        gbl.setConstraints(neutralBgRB, c);
        c.gridy = 4;
        gbl.setConstraints(selectedBgRB, c);

        bottomPanel.add(imgFormatLabel);
        bottomPanel.add(matchHighlightingCB);
        bottomPanel.add(matchInfoCB);
        bottomPanel.add(neutralBgRB);
        bottomPanel.add(selectedBgRB);

        bpContentPanel.add(bottomPanel);

        /* e) interPane */
        interPane.setBorder(emptyBorder);
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        interPane.setLayout(gbl);

        submit = new JButton("Submit");
        submit.addActionListener(this);

        Dimension subD = new Dimension(100, 30);
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

        c.gridwidth = 2;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(tpContentPanel, c);
        c.gridy = 1;
        gbl.setConstraints(mpContentPanel, c);
        c.gridy = 2;
        gbl.setConstraints(bpContentPanel, c);
        c.fill = GridBagConstraints.NONE;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.EAST;
        c.weightx = 1.0;
        c.gridy = 3;
        gbl.setConstraints(submit, c);
        c.weightx = 0.0;
        c.gridx = 1;
        gbl.setConstraints(cancel, c);

        interPane.add(tpContentPanel);
        interPane.add(mpContentPanel);
        interPane.add(bpContentPanel);
        interPane.add(submit);
        interPane.add(cancel);

        contentPane.add(interPane, BorderLayout.NORTH);
        this.setContentPane(contentPane);
        pack();

        /* Direktes Verlassen */
        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    setVisible(false);
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();

            if (e.getActionCommand() == "Search") {
                JFileChooser chooser = new JFileChooser();
                chooser.setApproveButtonText("Select");

                if (export_dir != null) {
                    chooser.setCurrentDirectory(export_dir);
                }

                javax.swing.filechooser.FileFilter svg = new SimpleFileFilter(extS,
                        "Images (*.svg, *.svgz)");
                chooser.addChoosableFileFilter(svg);
                chooser.setFileFilter(svg);

                int returnVal = chooser.showOpenDialog(this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    tofileField.setText(chooser.getSelectedFile()
                                               .getAbsolutePath());
                    export_dir = chooser.getCurrentDirectory();
                }
            }

            if (e.getActionCommand() == "Submit") {
                String name = getFilename();
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

                    setFilename(name);
                    file = new File(name);
                }

                // Existiert Dateiname?
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

                // Eingaben OK?
                try {
                    matching_sentences = calculateMatchingSentences();
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this,
                        "Your input is not correct:\n" + exp.getMessage(),
                        "Parameter error", JOptionPane.ERROR_MESSAGE);

                    return;
                }

                // Arg viele Saetze?
                if (matching_sentences.length > 100) {
                    Object[] options = { "Start export", "Cancel" };
                    int n = JOptionPane.showOptionDialog(this,
                            "You have selected quite a lot of sentences.\nThis may take a while. Are you sure?",
                            "Warning", JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options,
                            options[0]);

                    // -1 wenn WINDOW_CLOSED_EVENT ausgeloest wird
                    if ((n == JOptionPane.NO_OPTION) || (n == -1)) {
                        return;
                    }
                }

                // OK => Let's go!
                submitted = true;
                this.setVisible(false);
            }

            if (e.getActionCommand() == "Cancel") {
                submitted = false;
                this.setVisible(false);
            }

            // Event-Handling ENDE
        } catch (OutOfMemoryError err) {
            logger.error(
                "Out of memory error in ExportForestDialog action listener.");
            ims.tiger.util.UtilitiesCollection.showOutOfMemoryMessage(this,
                false, true);
        } catch (Error err) {
            logger.error("Java error in ExportForestDialog action listener", err);
        } catch (Exception err) {
            logger.error("Java exception in ExportForestDialog action listener",
                err);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void itemStateChanged(ItemEvent e) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSubmitted() {
        return submitted;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFilename() {
        return tofileField.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     */
    public void setFilename(String filename) {
        tofileField.setText(filename);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean compressFile() {
        return typeCombo.getSelectedIndex() > 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean includeSubmatches() {
        return matchWithinSentCB.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean highlighMatches() {
        return matchHighlightingCB.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean useBackground() {
        return selectedBgRB.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean displayMatchInformation() {
        return matchInfoCB.isSelected();
    }

    /**
     * DOCUMENT ME!
     *
     * @param forest DOCUMENT ME!
     */
    public void setForest(Forest forest) {
        this.forest = forest;

        // Fenstertitel
        if (forest.isForestWithMatches()) {
            setTitle("SVG match forest export");
        } else {
            setTitle("SVG corpus forest export");
        }

        // Match-Optionen
        matchWithinSentCB.setEnabled(forest.submatchesPossible());
        matchHighlightingCB.setEnabled(forest.isForestWithMatches());
        matchInfoCB.setEnabled(forest.isForestWithMatches());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int[] getMatchingSentences() {
        return matching_sentences;
    }

    private int[] calculateMatchingSentences() throws Exception {
        if (allRB.isSelected()) { // all

            int sent_n = forest.getForestSize();
            int[] match_sent = new int[sent_n];

            for (int i = 0; i < sent_n; i++) {
                match_sent[i] = i + 1;
            }

            return match_sent;
        } else if (fromtoRB.isSelected()) { // from-to

            String from_t = fromF.getText();
            int from = -1;
            from = (new Integer(from_t)).intValue();

            String to_t = toF.getText();
            int to = -1;
            to = (new Integer(to_t)).intValue();

            if ((to < from) || (from < 1) || (to > forest.getForestSize())) {
                throw new Exception("Invalid range selected.");
            }

            // Erzeugen der Liste
            int dim = to - from + 1;
            int[] matchlist = new int[dim];

            for (int i = 0; i < dim; i++) {
                matchlist[i] = i + from; // 1 -> 1 usw.
            }

            return matchlist;
        } else { // freie Eingabe

            return getTextSelection();
        }
    }

    private int[] getTextSelection()
        throws NumberFormatException, RegExpException {
        RegExpToolbox reg = new RegExpToolbox();

        String selection = selectField.getText() + ",";
        java.util.List areas = reg.split("/[,;]/", selection);
        java.util.List sentences = new LinkedList();

        String area;

        for (int i = 0; i < areas.size(); i++) {
            area = (String) areas.get(i);

            if (area.length() == 0) {
                continue;
            }

            if (reg.matches("/-/", area)) {
                String start_t = reg.getPreMatch();
                String end_t = reg.getPostMatch();
                int start = (new Integer(start_t)).intValue();
                int end = (new Integer(end_t)).intValue();

                for (int j = start; j <= end; j++) {
                    sentences.add(new Integer(j));
                }
            } else { // Nummer extrahieren
                sentences.add(new Integer(area));
            }
        }

        if (sentences.size() == 0) {
            throw new NumberFormatException("No match selected.");
        }

        int[] result = new int[sentences.size()];

        for (int i = 0; i < sentences.size(); i++) {
            result[i] = ((Integer) sentences.get(i)).intValue();

            if ((result[i] < 1) || (result[i] > forest.getForestSize())) {
                throw new NumberFormatException("Invalid range selected.");
            }
        }

        return result;
    }
}
