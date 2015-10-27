/*
 * File:     DisplayOptions.java
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

import ims.tiger.corpus.Header;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;

import org.apache.log4j.Logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;


/** Fenster zum Einstellen genereller Parameter des GraphViewers. */
public class DisplayOptions extends JDialog implements ActionListener {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(DisplayOptions.class);
    private Container contentPane;
    private GridBagLayout gbl;
    private GridBagConstraints c;
    private JButton ok;
    private JButton cancel;
    private JButton reset;
    private JButton defaultProperties;
    private JLabel featureNumberLabel;
    private JLabel valueSuppressedLabel;
    private JLabel maxWidthLabel;
    private JLabel contextLabel;
    private JComboBox maxWidthCBOx;
    private JComboBox contextCBOx;
    private JCheckBox featureValueCheckBox;
    private JCheckBox edgeLabelCheckBox;
    private JCheckBox virtualRootCheckBox;
    private JCheckBox secondaryEdgeCheckBox;
    private JTextField featureValueTextField;
    private JTextField edgeLabelTextField;
    private JPanel topPanelL;
    private JPanel topPanelR;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private JLabel labelNT;
    private JLabel labelT;
    private JComboBox featureNTCBOx;
    private JCheckBox tFeatureCheckBox;
    private boolean suppresFeatureValue;
    private boolean suppresEdgeLabel;
    private boolean displaySecEdges;
    private boolean displayVRootNode;

    //Sichern der Einstellungen fuer Reset
    private Object[] backItUp;
    private Color[] colors;
    private String[] leafMaxWidth = {
        "30", "40", "50", "60", "70", "80", "90", "100", "125", "150", "175",
        "200", "250", "300", "350", "400"
    };
    private String[] contextWidth = { "0", "1", "2", "3" };
    private JCheckBox[] tboxes;
    private TIGERGraphViewerConfiguration config;
    private Header header;
    private boolean corpusDependentInfos = true;

    /**
     * Creates a new DisplayOptions instance
     *
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param parent DOCUMENT ME!
     */
    public DisplayOptions(Header header, TIGERGraphViewerConfiguration config,
        Frame parent) {
        super(parent, "Display Options", true);

        enableInputMethods(false);

        this.config = config;
        this.header = header;

        /* a) Allgemeine Eigenschaften */
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.setSize(new Dimension(300, 300));

        EmptyBorder emptyBorderTop = new EmptyBorder(new Insets(15, 10, 5, 10));
        EmptyBorder emptyBorderBottom = new EmptyBorder(new Insets(5, 20, 20, 20));

        //Parameter sichern
        backItUp = DisplayOptions.getParameters();

        //Farben nicht ueberschreiben (Verbindung mit ColorOptions)!!!
        colors = ColorOptions.makeColors();

        /* b) Top Panel links */
        topPanelL = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        topPanelL.setLayout(gbl);

        LineBorder lineBorderL = new LineBorder(Color.black, 1);
        TitledBorder titledBorderL = new TitledBorder(lineBorderL,
                "General settings", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("dialog", Font.BOLD, 12), Color.darkGray);

        topPanelL.setBorder(titledBorderL);

        Dimension nD = new Dimension(95, 24);

        maxWidthLabel = new JLabel("Maximum width of terminal node: ");
        maxWidthLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        maxWidthCBOx = new JComboBox(leafMaxWidth);
        maxWidthCBOx.setAlignmentX(Component.LEFT_ALIGNMENT);
        maxWidthCBOx.setEditable(false);
        maxWidthCBOx.setSelectedIndex(getGraphConstantValues(leafMaxWidth,
                GraphConstants.leafMaxWidth));

        maxWidthCBOx.setMaximumSize(nD);
        maxWidthCBOx.setMinimumSize(nD);
        maxWidthCBOx.setPreferredSize(nD);
        maxWidthCBOx.setSize(nD);

        virtualRootCheckBox = new JCheckBox("Display virtual root node",
                GraphConstants.virtualRootNodeDisplay);
        virtualRootCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (virtualRootCheckBox.isSelected()) {
                        displayVRootNode = true;
                    }

                    if (!virtualRootCheckBox.isSelected()) {
                        displayVRootNode = false;
                    }
                }
            });

        secondaryEdgeCheckBox = new JCheckBox("Display secondary edges",
                GraphConstants.secondaryEdgeDisplay);
        secondaryEdgeCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (secondaryEdgeCheckBox.isSelected()) {
                        displaySecEdges = true;
                    }

                    if (!secondaryEdgeCheckBox.isSelected()) {
                        displaySecEdges = false;
                    }
                }
            });

        valueSuppressedLabel = new JLabel("Hide...");
        valueSuppressedLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        featureValueCheckBox = new JCheckBox("feature value");

        //Achtung, hier wird auf leeres Wort geprueft!
        if (GraphConstants.suppressFeatureValue.equals("")) {
            featureValueCheckBox.setSelected(false);
        } else {
            featureValueCheckBox.setSelected(true);
        }

        featureValueCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (featureValueCheckBox.isSelected()) {
                        featureValueTextField.setEnabled(true);
                        suppresFeatureValue = true;
                    }

                    if (!featureValueCheckBox.isSelected()) {
                        featureValueTextField.setEnabled(false);
                        suppresFeatureValue = false;
                    }
                }
            });

        featureValueTextField = new JTextField(GraphConstants.suppressFeatureValue);
        featureValueTextField.setHorizontalAlignment(SwingConstants.CENTER);

        edgeLabelCheckBox = new JCheckBox("edge label");

        //Achtung, hier wird auf leeres Wort geprueft!
        if (GraphConstants.suppressEdgeLabel.equals("")) {
            edgeLabelCheckBox.setSelected(false);
        } else {
            edgeLabelCheckBox.setSelected(true);
        }

        edgeLabelCheckBox.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (edgeLabelCheckBox.isSelected()) {
                        edgeLabelTextField.setEnabled(true);
                        suppresEdgeLabel = true;
                    }

                    if (!edgeLabelCheckBox.isSelected()) {
                        edgeLabelTextField.setEnabled(false);
                        suppresEdgeLabel = false;
                    }
                }
            });

        edgeLabelTextField = new JTextField(GraphConstants.suppressEdgeLabel);
        edgeLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);

        contextLabel = new JLabel("Number of context sentences: ");
        contextLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contextCBOx = new JComboBox(contextWidth);
        contextCBOx.setAlignmentX(Component.LEFT_ALIGNMENT);
        contextCBOx.setEditable(false);
        contextCBOx.setSelectedIndex(GraphConstants.textContext);

        contextCBOx.setMaximumSize(nD);
        contextCBOx.setMinimumSize(nD);
        contextCBOx.setPreferredSize(nD);
        contextCBOx.setSize(nD);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 1;
        gbl.setConstraints(maxWidthLabel, c);
        c.gridx = 1;
        gbl.setConstraints(maxWidthCBOx, c);
        c.gridx = 0;
        c.gridy = 2;
        gbl.setConstraints(secondaryEdgeCheckBox, c);
        c.gridy = 3;
        gbl.setConstraints(virtualRootCheckBox, c);
        c.gridy = 4;
        gbl.setConstraints(valueSuppressedLabel, c);
        c.gridy = 5;
        gbl.setConstraints(featureValueCheckBox, c);
        c.gridx = 1;
        gbl.setConstraints(featureValueTextField, c);
        c.gridx = 0;
        c.gridy = 6;
        gbl.setConstraints(edgeLabelCheckBox, c);
        c.gridx = 1;
        gbl.setConstraints(edgeLabelTextField, c);
        c.gridx = 0;
        c.gridy = 7;
        gbl.setConstraints(contextLabel, c);
        c.gridx = 1;
        gbl.setConstraints(contextCBOx, c);

        topPanelL.add(maxWidthLabel);
        topPanelL.add(maxWidthCBOx);
        topPanelL.add(secondaryEdgeCheckBox);
        topPanelL.add(virtualRootCheckBox);
        topPanelL.add(valueSuppressedLabel);
        topPanelL.add(featureValueCheckBox);
        topPanelL.add(featureValueTextField);
        topPanelL.add(edgeLabelCheckBox);
        topPanelL.add(edgeLabelTextField);
        topPanelL.add(contextLabel);
        topPanelL.add(contextCBOx);

        /* c) Top Panel rechts */
        topPanelR = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        topPanelR.setLayout(gbl);

        LineBorder lineBorderR = new LineBorder(Color.black, 1);
        TitledBorder titledBorderR = new TitledBorder(lineBorderR,
                "Corpus dependent settings", TitledBorder.LEFT,
                TitledBorder.TOP, new Font("dialog", Font.BOLD, 12),
                Color.darkGray);

        topPanelR.setBorder(titledBorderR);

        labelNT = new JLabel("Displayed non-terminal feature");
        labelNT.setAlignmentX(Component.LEFT_ALIGNMENT);

        Object[] ntFeatures = header.getAllNTFeatureNames().toArray();
        Object[] tFeatures = header.getAllTFeatureNames().toArray();

        featureNTCBOx = new JComboBox(makeComboBaby(ntFeatures));
        featureNTCBOx.setAlignmentX(Component.LEFT_ALIGNMENT);
        featureNTCBOx.setEditable(false);
        featureNTCBOx.setSelectedIndex(0);

        featureNTCBOx.setMaximumSize(nD);
        featureNTCBOx.setMinimumSize(nD);
        featureNTCBOx.setPreferredSize(nD);
        featureNTCBOx.setSize(nD);

        labelT = new JLabel("Displayed terminal features");
        labelT.setAlignmentX(Component.LEFT_ALIGNMENT);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.BOTH;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(labelNT, c);
        topPanelR.add(labelNT);

        c.gridy = 1;
        gbl.setConstraints(featureNTCBOx, c);
        topPanelR.add(featureNTCBOx);

        c.gridy = 2;
        gbl.setConstraints(labelT, c);
        topPanelR.add(labelT);

        tboxes = new JCheckBox[tFeatures.length];

        JPanel tpanel = new JPanel();
        GridBagLayout gbl2 = new GridBagLayout();
        tpanel.setLayout(gbl2);

        for (int i = 0; i < tFeatures.length; i++) {
            tFeatureCheckBox = new JCheckBox((String) tFeatures[i]);
            tFeatureCheckBox.setName((String) tFeatures[i]);

            tboxes[i] = tFeatureCheckBox;

            GridBagConstraints gc = new GridBagConstraints();

            gc.gridwidth = 1;
            gc.gridheight = 1;
            gc.gridx = 0;
            gc.gridy = 0;
            gc.weightx = 1.0;
            gc.weighty = 1.0;

            gc.anchor = GridBagConstraints.WEST;
            gc.insets = new Insets(0, 0, 0, 0);
            gc.gridx = 0;
            gc.gridy = i;

            gbl2.setConstraints(tFeatureCheckBox, gc);
            tpanel.add(tFeatureCheckBox);
        }

        setCurrentCorpusChecks(); // Was ist beim Korpus voreingestellt?

        JScrollPane tscrolled = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        tscrolled.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        tscrolled.setViewportView(tpanel);
        tscrolled.setPreferredSize(new Dimension(150, 150));
        tscrolled.setSize(new Dimension(150, 150));

        c.gridx = 0;
        c.gridy = 3;
        gbl.setConstraints(tscrolled, c);
        topPanelR.add(tscrolled);

        /* d) Top Panel ganz */
        topPanelR.setVisible(corpusDependentInfos);

        topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(emptyBorderTop);
        topPanel.add(this.topPanelL, BorderLayout.WEST);
        topPanel.add(this.topPanelR, BorderLayout.EAST);

        /* e) Bottom Panel  */
        bottomPanel = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        bottomPanel.setLayout(gbl);
        bottomPanel.setBorder(emptyBorderBottom);

        Dimension subD = new Dimension(50, 30);

        ok = new JButton("OK");
        ok.setToolTipText("Change options");
        ok.addActionListener(this);
        ok.setMaximumSize(subD);
        ok.setMinimumSize(subD);
        ok.setPreferredSize(subD);
        ok.setSize(subD);
        ok.setBorder(BorderFactory.createEtchedBorder());

        cancel = new JButton("Cancel");
        cancel.setToolTipText("No option changed");
        cancel.addActionListener(this);
        cancel.setMaximumSize(subD);
        cancel.setMinimumSize(subD);
        cancel.setPreferredSize(subD);
        cancel.setSize(subD);
        cancel.setBorder(BorderFactory.createEtchedBorder());

        reset = new JButton("Reset");
        reset.setToolTipText("Reset all options");
        reset.addActionListener(this);
        reset.setMaximumSize(subD);
        reset.setMinimumSize(subD);
        reset.setPreferredSize(subD);
        reset.setSize(subD);
        reset.setBorder(BorderFactory.createEtchedBorder());

        defaultProperties = new JButton("Default");
        defaultProperties.setToolTipText("Set all options to default");
        defaultProperties.addActionListener(this);
        defaultProperties.setMaximumSize(subD);
        defaultProperties.setMinimumSize(subD);
        defaultProperties.setPreferredSize(subD);
        defaultProperties.setSize(subD);
        defaultProperties.setBorder(BorderFactory.createEtchedBorder());

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(ok, c);
        c.gridx = 1;
        gbl.setConstraints(cancel, c);
        c.gridx = 2;
        gbl.setConstraints(reset, c);
        c.gridx = 3;
        gbl.setConstraints(defaultProperties, c);

        bottomPanel.add(ok);
        bottomPanel.add(cancel);
        bottomPanel.add(reset);
        bottomPanel.add(defaultProperties);

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        this.setContentPane(contentPane);
        pack();

        /* Direktes Verlassen */
        addWindowListener(new DisplayOptionsWindowListener());
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        try {
            Object src = e.getSource();

            if (e.getActionCommand() == "OK") {
                int new_tf_max;
                int new_t_width;
                String new_f_suppress;
                String new_l_suppress;
                boolean new_secedge;

                // 1. Ueberpruefe die neuen Einstellungen
                try {
                    // MINDESTENS EIN T ANGEWAEHLT?
                    int tcount = 0;

                    for (int i = 0; i < tboxes.length; i++) {
                        if (tboxes[i].isSelected()) {
                            tcount++;
                        }
                    }

                    if (tcount == 0) {
                        JOptionPane.showMessageDialog(this,
                            "Please choose at least one terminal feature!",
                            "Parameter error", JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    // MAXIMALE T-BREITE
                    String new_t_width_string = (String) maxWidthCBOx.getSelectedItem();
                    new_t_width = (new Integer(new_t_width_string)).intValue();

                    // Bereichspruefung
                    if ((new_t_width < 30) || (new_t_width > 400)) {
                        JOptionPane.showMessageDialog(this,
                            "Maximum width of terminal node is out of range.",
                            "Parameter error", JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    // UNTERDRUECKE FEATURE-WERTE
                    new_f_suppress = featureValueTextField.getText();

                    boolean new_f_suppress_check = featureValueCheckBox.isSelected();

                    // suppressed, aber leer?
                    if ((new_f_suppress_check) &&
                            (new_f_suppress.length() == 0)) {
                        JOptionPane.showMessageDialog(this,
                            "Please specify the suppressed feature value.",
                            "Parameter error", JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    // !suppressed => leer!
                    if (!new_f_suppress_check) {
                        new_f_suppress = "";
                    }

                    // UNTERDRUECKE KANTENLABEL
                    new_l_suppress = edgeLabelTextField.getText();

                    boolean new_l_suppress_check = edgeLabelCheckBox.isSelected();

                    // suppressed, aber leer?
                    if ((new_l_suppress_check) &&
                            (new_l_suppress.length() == 0)) {
                        JOptionPane.showMessageDialog(this,
                            "Please specify the suppressed edge label.",
                            "Parameter error", JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    // !suppressed => leer!
                    if (!new_l_suppress_check) {
                        new_l_suppress = "";
                    }

                    // ZEIGE SEKUNDAERE KANTEN
                    new_secedge = secondaryEdgeCheckBox.isSelected();
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this,
                        "One of the parameters is not correct:\n" +
                        exp.getMessage(), "Parameter error",
                        JOptionPane.ERROR_MESSAGE);

                    return;
                }

                // 2. Uebernehme die neuen Einstellungen
                GraphConstants.leafMaxWidth = new_t_width;
                GraphConstants.suppressFeatureValue = new_f_suppress;
                GraphConstants.suppressEdgeLabel = new_l_suppress;

                GraphConstants.secondaryEdgeDisplay = new_secedge;
                GraphConstants.virtualRootNodeDisplay = virtualRootCheckBox.isSelected();

                GraphConstants.textContext = contextCBOx.getSelectedIndex(); // Index ist hier zugleich Wert! 

                ColorOptions.resetConfigColors(colors);

                List tFeatures = (List) ((ArrayList) header.getAllTFeatureNames()).clone(); // auf Kopie arbeiten!
                List tselected = new ArrayList();

                for (int i = 0; i < tboxes.length; i++) {
                    if (tboxes[i].isSelected()) {
                        tselected.add(tFeatures.get(i));
                    }
                }

                config.setDisplayedTFeatures(header, tselected);
                config.setDisplayedNTFeature(header,
                    (String) featureNTCBOx.getSelectedItem());

                this.setVisible(false);
            }

            if (e.getActionCommand() == "Cancel") {
                this.restoreGUISettings(backItUp);
                DisplayOptions.resetConfigParameters(backItUp);
                ColorOptions.resetConfigColors(colors);
                this.setVisible(false);
            }

            if (e.getActionCommand() == "Reset") {
                this.restoreGUISettings(backItUp);
                DisplayOptions.resetConfigParameters(backItUp);
                setCurrentCorpusChecks();
            }

            if (e.getActionCommand() == "Default") {
                // System-Defaults lesen
                try {
                    config.resetConfiguration();
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this,
                        "Could not reset configuration:\n" + exp.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }

                this.restoreGUISettings(DisplayOptions.getParameters());

                // Default bei Features
                featureNTCBOx.setSelectedIndex(0);

                for (int i = 0; i < tboxes.length; i++) {
                    if (i < 5) {
                        tboxes[i].setSelected(true);
                    } else {
                        tboxes[i].setSelected(false);
                    }
                }
            }

            // Event-Handling ENDE
        } catch (OutOfMemoryError err) {
            logger.error(
                "Out of memory error in DisplayOptions action listener.");
            ims.tiger.util.UtilitiesCollection.showOutOfMemoryMessage(this,
                false, true);
        } catch (Error err) {
            logger.error("Java error in DisplayOptionso action listener", err);
        } catch (Exception err) {
            logger.error("Java exception in DisplayOptions action listener", err);
        }
    }

    private void cleanCorpusChecks() {
        // clean T
        for (int i = 0; i < tboxes.length; i++) {
            tboxes[i].setSelected(false);
        }
    }

    private void setTCheck(String fname) {
        for (int i = 0; i < tboxes.length; i++) {
            if (tboxes[i].getName().equals(fname)) {
                tboxes[i].setSelected(true);

                break;
            }
        }
    }

    private void setCurrentCorpusChecks() {
        cleanCorpusChecks();

        // NT
        String ntFeature = config.getDisplayedNTFeature(header);
        featureNTCBOx.setSelectedItem(ntFeature);

        // T
        List tFeatures = config.getDisplayedTFeatures(header);

        for (int i = 0; i < tFeatures.size(); i++) {
            setTCheck((String) tFeatures.get(i));
        }
    }

    private int getGraphConstantValues(String[] values, int val) {
        for (int i = 0; i < values.length; i++) {
            if (Integer.parseInt(values[i]) == val) {
                return i;
            }
        }

        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Object[] getParameters() {
        Object[] zeugls = new Object[6];

        int leafMaxWOriginal = GraphConstants.leafMaxWidth;
        String featureValueOriginal = GraphConstants.suppressFeatureValue;
        String edgeLabelOriginal = GraphConstants.suppressEdgeLabel;
        boolean secEdgeDisplayStateOriginal = GraphConstants.secondaryEdgeDisplay;
        boolean virtualRootNodeDisplayStateOriginal = GraphConstants.virtualRootNodeDisplay;
        int textContextOriginal = GraphConstants.textContext;

        zeugls[0] = new Integer(leafMaxWOriginal);
        zeugls[1] = featureValueOriginal;
        zeugls[2] = edgeLabelOriginal;
        zeugls[3] = new Boolean(secEdgeDisplayStateOriginal);
        zeugls[4] = new Boolean(virtualRootNodeDisplayStateOriginal);
        zeugls[5] = new Integer(textContextOriginal);

        return zeugls;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sachen DOCUMENT ME!
     */
    public static void resetConfigParameters(Object[] sachen) {
        GraphConstants.leafMaxWidth = ((Integer) sachen[0]).intValue();
        GraphConstants.suppressFeatureValue = (String) sachen[1];
        GraphConstants.suppressEdgeLabel = (String) sachen[2];
        GraphConstants.secondaryEdgeDisplay = ((Boolean) sachen[3]).booleanValue();
        GraphConstants.virtualRootNodeDisplay = ((Boolean) sachen[4]).booleanValue();
        GraphConstants.textContext = ((Integer) sachen[5]).intValue();
    }

    private void restoreGUISettings(Object[] stuff) {
        maxWidthCBOx.setSelectedIndex(getGraphConstantValues(leafMaxWidth,
                ((Integer) stuff[0]).intValue()));

        featureValueTextField.setText((String) stuff[1]);

        if (((String) stuff[1]).equals("")) {
            featureValueCheckBox.setSelected(false);
        } else {
            featureValueCheckBox.setSelected(true);
        }

        edgeLabelTextField.setText((String) stuff[2]);

        if (((String) stuff[2]).equals("")) {
            edgeLabelCheckBox.setSelected(false);
        } else {
            edgeLabelCheckBox.setSelected(true);
        }

        secondaryEdgeCheckBox.setSelected(((Boolean) stuff[3]).booleanValue());
        virtualRootCheckBox.setSelected(((Boolean) stuff[4]).booleanValue());

        contextCBOx.setSelectedIndex(((Integer) stuff[5]).intValue());
    }
     //restoreGUISettings

    private DefaultComboBoxModel makeComboBaby(Object[] dieDingens) {
        return new DefaultComboBoxModel(dieDingens);
    }

    /**
     * DOCUMENT ME!
     * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
     * @author $Author: hasloe $
     * @version $Revision: 1.3 $
     */
    public class DisplayOptionsWindowListener extends WindowAdapter {
        /**
         * DOCUMENT ME!
         *
         * @param e DOCUMENT ME!
         */
        public void windowClosing(WindowEvent e) {
            restoreGUISettings(backItUp);
            resetConfigParameters(backItUp);
            ColorOptions.resetConfigColors(colors);
            setVisible(false);
        }
    }
}
