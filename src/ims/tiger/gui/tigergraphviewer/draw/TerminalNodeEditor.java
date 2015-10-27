/*
 * File:     TerminalNodeEditor.java
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

package ims.tiger.gui.tigergraphviewer.draw;

import ims.tiger.corpus.Header;
import ims.tiger.corpus.T_Node;

import ims.tiger.gui.tigergraphviewer.forest.CorpusForest;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


/**
 * DOCUMENT ME!
 * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
 * @author $Author: hasloe $
 * @version $Revision: 1.3 $
 */
public class TerminalNodeEditor extends JDialog {
    /** Holds value of property DOCUMENT ME! */
    private final T_Node node;

    /** Holds value of property DOCUMENT ME! */
    private final String[] featureNames;
    private JComponent[] inputComponents;

    /**
     * Creates a new TerminalNodeEditor instance
     *
     * @param node DOCUMENT ME!
     * @param header DOCUMENT ME!
     * @param graphPanel DOCUMENT ME!
     * @param forest DOCUMENT ME!
     */
    public TerminalNodeEditor(T_Node node, final Header header,
        final GraphPanel graphPanel, final CorpusForest forest) {
        this.node = node;
        featureNames = (String[]) header.getAllTFeatureNames().toArray(new String[0]);

        Action okAction = new AbstractAction("OK") {
                public void actionPerformed(ActionEvent e) {
                    for (int i = 0; i < inputComponents.length; i++) {
                        TerminalNodeEditor.this.node.setFeature(featureNames[i],
                            TerminalNodeEditor.this.getValue(i));
                    }

                    graphPanel.setSentence(graphPanel.getCurrentDisplaySentence());
                    graphPanel.repaint();
                    forest.setModified(true);

                    dispose();
                }
            };

        Action cancelAction = new AbstractAction("Cancel") {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            };

        setTitle("Edit node");
        getContentPane().setLayout(new BorderLayout());

        JLabel headLabel = new JLabel("Terminal node features");
        headLabel.setHorizontalAlignment(JLabel.CENTER);
        headLabel.setBorder(new javax.swing.border.EmptyBorder(5, 5, 5, 5));
        getContentPane().add(headLabel, BorderLayout.NORTH);
        inputComponents = new JComponent[featureNames.length];

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        for (int i = 0; i < featureNames.length; i++) {
            List features = header.getTFeature(featureNames[i]).getItems();

            if (features.size() > 0) {
                JComboBox comboBox = new JComboBox(features.toArray());
                comboBox.setEditable(true);
                comboBox.setSelectedItem(node.getFeature(featureNames[i]));
                comboBox.setFont(getFont());
                inputComponents[i] = comboBox;
            } else {
                JTextField textField = new JTextField(15);
                textField.setPreferredSize(new Dimension(
                        textField.getPreferredSize().width,
                        (new JComboBox()).getPreferredSize().height));
                textField.setText(node.getFeature(featureNames[i]));
                inputComponents[i] = textField;
            }

            c.gridwidth = GridBagConstraints.RELATIVE;
            c.anchor = GridBagConstraints.EAST;
            c.fill = GridBagConstraints.NONE;
            panel.add(new JLabel(featureNames[i]), c);
            c.anchor = GridBagConstraints.WEST;
            c.gridwidth = GridBagConstraints.REMAINDER;
            c.fill = GridBagConstraints.BOTH;
            panel.add(inputComponents[i], c);
        }

        getContentPane().add(panel, BorderLayout.CENTER);

        panel = new JPanel();

        JPanel insetPanel = new JPanel(new GridLayout(1, 0));
        insetPanel.add(new JButton(okAction));
        insetPanel.add(new JButton(cancelAction));
        panel.add(insetPanel);
        getContentPane().add(panel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getValue(int i) {
        if (inputComponents[i] instanceof JTextField) {
            return ((JTextField) inputComponents[i]).getText();
        }

        return (String) ((JComboBox) inputComponents[i]).getSelectedItem();
    }
}
