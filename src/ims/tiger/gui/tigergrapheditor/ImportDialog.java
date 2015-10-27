/*
 * File:     ImportDialog.java
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

package ims.tiger.gui.tigergrapheditor;

import ims.tiger.corpus.Sentence;

import ims.tiger.gui.tigergraphviewer.GraphViewer;

import ims.tiger.gui.tigergraphviewer.forest.CorpusForest;
import ims.tiger.gui.tigergraphviewer.forest.ForestException;

import ims.tiger.parse.DefaultCorpus;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


/**
 * This dialog is meant to specify precise import conditions
 *
 * @author klasal
 * @version $version$
 */
public class ImportDialog extends JDialog implements ItemListener {
    //maximal line count for first import block in file

    /** Holds value of property DOCUMENT ME! */
    private static final int MAX_FIRST_BLOCK_SIZE = 1024;

    /** Holds value of property DOCUMENT ME! */
    private static final int DEFAULT_TEXT_FIELD_LENGTH = 55;

    /** Holds value of property DOCUMENT ME! */
    private final CorpusForest forest;

    /** Holds value of property DOCUMENT ME! */
    private final File file;

    /** Holds value of property DOCUMENT ME! */
    private final JTextField startLineTextField = new JTextField(3);

    /** Holds value of property DOCUMENT ME! */
    private final Logger logger = Logger.getLogger(ImportDialog.class.getName());
    private String deselectedFeature;
    private JComboBox[] comboBoxes;
    private JTextField[] textFields;
    private int blockLineCount;

    /**
     * Constructor
     *
     * @param frame parent frame
     * @param forest forest
     * @param file import file
     * @param featureNames available terminal features
     *
     * @throws IOException error with reading import file
     */
    public ImportDialog(JFrame frame, CorpusForest forest, final File file,
        String[] featureNames) throws IOException {
        super(frame, true);
        this.file = file;
        this.forest = forest;
        setTitle("Importing " + file);

        Action cancelAction = new AbstractAction("Cancel") {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            };

        Action okAction = new AbstractAction("OK") {
                public void actionPerformed(ActionEvent e) {
                    try {
                        if (blockLineCount > textFields.length) {
                            int confirm = JOptionPane.showConfirmDialog(ImportDialog.this,
                                    "More lines in first block than features. Extra lines will be ignored!",
                                    "Warning", JOptionPane.WARNING_MESSAGE);

                            if (confirm == JOptionPane.CANCEL_OPTION) {
                                return;
                            }
                        }

                        addSentences(getSelectedFeatureNames(), getStartLine());
                        dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(ImportDialog.this,
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        dispose();
                    }
                }
            };

        getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.add(new JLabel("Ignoring first "));
        startLineTextField.setHorizontalAlignment(JTextField.CENTER);
        startLineTextField.setText("0");
        startLineTextField.getDocument().addDocumentListener(new DocumentListener() {
                public void changedUpdate(DocumentEvent e) {
                }

                public void insertUpdate(DocumentEvent e) {
                    try {
                        updateTextFields();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(ImportDialog.this,
                            "Error: " + ex.getMessage(), "IO Error",
                            JOptionPane.ERROR_MESSAGE);
                        logger.error(ex.getMessage());
                        dispose();
                    }
                }

                public void removeUpdate(DocumentEvent e) {
                    try {
                        updateTextFields();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(ImportDialog.this,
                            "Error: " + ex.getMessage(), "IO Error",
                            JOptionPane.ERROR_MESSAGE);
                        logger.error(ex.getMessage());
                        dispose();
                    }
                }
            });
        panel.add(startLineTextField);
        panel.add(new JLabel(" lines of file"));
        getContentPane().add(panel, BorderLayout.NORTH);

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        comboBoxes = new JComboBox[featureNames.length];
        textFields = new JTextField[featureNames.length];

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < featureNames.length; i++) {
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = 1;
            textFields[i] = new JTextField(DEFAULT_TEXT_FIELD_LENGTH);
            textFields[i].setEditable(false);
            textFields[i].setBackground(Color.WHITE);
            panel.add(textFields[i], c);

            c.weightx = 0;
            c.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(" -> "), c);

            c.gridwidth = GridBagConstraints.REMAINDER;

            JComboBox comboBox = new JComboBox(featureNames);
            comboBoxes[i] = comboBox;
            comboBox.setSelectedItem(featureNames[i]);
            panel.add(comboBox, c);

            comboBox.addItemListener(this);
        }

        getContentPane().add(panel, BorderLayout.CENTER);

        panel = new JPanel();

        JPanel insetPanel = new JPanel(new GridLayout(1, 0));
        insetPanel.add(new JButton(okAction));
        insetPanel.add(new JButton(cancelAction));
        panel.add(insetPanel);
        getContentPane().add(panel, BorderLayout.SOUTH);

        updateTextFields();
        pack();
        setLocationRelativeTo(frame);
    }

    /**
     * update other comboboxes in order to avoid features being selected twice
     *
     * @param e DOCUMENT ME!
     */
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED) {
            deselectedFeature = (String) e.getItem();
        } else if (e.getStateChange() == ItemEvent.SELECTED) {
            if (deselectedFeature != null) {
                for (int i = 0; i < comboBoxes.length; i++) {
                    if (comboBoxes[i] == e.getSource()) {
                        continue;
                    }

                    if (comboBoxes[i].getSelectedItem().equals(e.getItem())) {
                        comboBoxes[i].removeItemListener(this);
                        comboBoxes[i].setSelectedItem(deselectedFeature);
                        comboBoxes[i].addItemListener(this);

                        break;
                    }
                }
            }
        }
    }

    private String[] getSelectedFeatureNames() {
        String[] featureNames = new String[comboBoxes.length];

        for (int i = 0; i < comboBoxes.length; i++) {
            featureNames[i] = (String) comboBoxes[i].getSelectedItem();
        }

        return featureNames;
    }

    private int getStartLine() {
        int startLine = 0;

        try {
            startLine = Integer.parseInt(startLineTextField.getText());
        } catch (Exception e) {
        }

        return startLine;
    }

    private void addSentences(String[] featureNames, int startLine)
        throws Exception {
        int oldForestSize = forest.getForestSize();
        List sentences = Text2Tiger.importSentences(file, oldForestSize,
                featureNames, startLine);
        DefaultCorpus corpus = (DefaultCorpus) forest.getCorpus();

        for (int i = 0; i < sentences.size(); i++) {
            Sentence loopSentence = (Sentence) sentences.get(i);
            corpus.addSentence(loopSentence);
        }

        try {
            CorpusForest forest = new CorpusForest(corpus);
            JOptionPane.showMessageDialog(this,
                "Imported " + sentences.size() + " sentences.");
            forest.setModified(true);

            if (getOwner() instanceof GraphViewer) {
                ((GraphViewer) getOwner()).visualizeForest(forest);
            }
        } catch (ForestException e) {
            e.printStackTrace();
        }
    }

    /**
     * reads first block of file, omitting lines up to startline
     *
     * @param startLine
     *
     * @return
     *
     * @throws IOException
     */
    private String[] readLinesFromFile(int startLine) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String[] lines = new String[MAX_FIRST_BLOCK_SIZE];
        blockLineCount = 0;

        int lineCount = 0;
        String line;

        while ((lineCount < startLine) && ((line = reader.readLine()) != null)) {
            lineCount++;
        }

        lineCount = 0;

        while ((lineCount < MAX_FIRST_BLOCK_SIZE) &&
                ((line = reader.readLine()) != null)) {
            //ignore empty begin lines
            if (line.trim().equals("")) {
                //end after first block
                if (blockLineCount > 0) {
                    break;
                }
            } else {
                lines[blockLineCount] = line;
                blockLineCount++;
            }

            lineCount++;
        }

        return lines;
    }

    private void updateTextFields() throws IOException {
        String[] lines = readLinesFromFile(getStartLine());

        for (int i = 0; i < textFields.length; i++) {
            if (lines[i] != null) {
                String[] tokens = lines[i].split("\\s+");
                lines[i] = tokens[0];

                for (int j = 1; j < tokens.length; j++) {
                    lines[i] += (" " + tokens[j]);
                }

                textFields[i].setText(lines[i]);
            } else {
                textFields[i].setText("");
            }
        }
    }
}
