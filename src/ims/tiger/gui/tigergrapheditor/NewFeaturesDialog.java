/*
 * File:     NewFeaturesDialog.java
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

import ims.tiger.corpus.Feature;
import ims.tiger.corpus.Header;

import ims.tiger.system.Constants;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.SimpleDateFormat;

import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


/**
 * $Id: NewFeaturesDialog.java,v 1.1 2007/03/23 14:51:23 klasal Exp $
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class NewFeaturesDialog extends JDialog {
    /** Return value from class method if YES is chosen. */
    public static final int OK_OPTION = 0;

    /** Return value from class method if CANCEL is chosen. */
    public static final int CANCEL_OPTION = 2;

    /** Holds value of property DOCUMENT ME! */
    private final Header header;

    /** Holds value of property DOCUMENT ME! */
    private final JTextArea descriptionArea = new JTextArea(3, 25);

    /** Holds value of property DOCUMENT ME! */
    private final JTextField authorField = new JTextField(25);

    /** Holds value of property DOCUMENT ME! */
    private final JTextField nameField = new JTextField(25);

    /** Holds value of property DOCUMENT ME! */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'H:mm:ssz");

    /** Holds value of property DOCUMENT ME! */
    private final String defaultFileFormat = "NeGra format, version 3";

    /** stores value of option */
    private int option = CANCEL_OPTION;

    /**
     * Creates a new NewFeatureFileDialog object.
     *
     * @param parent parent frame
     * @param header header to be reset
     */
    private NewFeaturesDialog(JFrame parent, Header header) {
        super(parent, true);
        this.header = header;
        makeLayout();
        pack();
    }

    /**
     * shows dialog and returns option (OK or CANCEL)
     *
     * @param parent parent frame
     * @param header header to be reset
     *
     * @return OK_OPTION or CANCEL_OPTION
     */
    public static int showNewFeaturesDialog(JFrame parent, Header header) {
        NewFeaturesDialog dialog = new NewFeaturesDialog(parent, header);
        dialog.show();

        return dialog.option;
    }

    private void close() {
        setVisible(false);
        dispose();
    }

    private void makeLayout() {
        setTitle("New feature set dialog");
        getContentPane().setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Create new set of feature descriptions");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setFont(label.getFont().deriveFont(16f));
        getContentPane().add(label, c);

        label = new JLabel("Name:");
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        getContentPane().add(label, c);
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        getContentPane().add(nameField, c);

        label = new JLabel("Author:");
        c.weightx = 0;
        c.gridwidth = GridBagConstraints.RELATIVE;
        getContentPane().add(label, c);
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        getContentPane().add(authorField, c);

        c.insets = new Insets(5, 5, 0, 5);
        label = new JLabel("Description:");
        getContentPane().add(label, c);

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        c.insets = new Insets(5, 5, 5, 5);
        descriptionArea.setBorder(new LineBorder(Color.GRAY));
        getContentPane().add(descriptionArea, c);

        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    resetFeatures();
                    option = OK_OPTION;
                    close();
                }
            });

        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 5, 5));
        buttonPanel.add(button);
        button = new JButton("Cancel");
        button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            });
        buttonPanel.add(button);
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        getContentPane().add(buttonPanel, c);

        nameField.setText("new");
        authorField.setText(System.getProperty("user.name"));
    }

    private void resetFeatures() {
        header.reset();
        header.setEdgeFeature(new Feature(Constants.EDGE));
        header.setSecEdgeFeature(new Feature(Constants.SECEDGE));
        header.addNonterminalFeature(new Feature("cat"));
        header.addTerminalFeature(new Feature("word"));
        header.setCorpus_Name(nameField.getText());
        header.setCorpus_Author(authorField.getText());
        header.setCorpus_Date(dateFormat.format(new Date()));
        header.setCorpus_Description(descriptionArea.getText());
        header.setCorpus_Format(defaultFileFormat);
        header.setCorpus_History("");
    }
}
