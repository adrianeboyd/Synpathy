/*
 * File:     AboutWindow.java
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
package ims.tiger.gui.shared;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;


/** Kleines Fenster zur Darstellung der TIGERSearch-About-Box. */
public class AboutWindow extends JDialog implements ActionListener {
    private JLabel imgLabel;
    private JLabel devLabel;
    private JLabel nameLabel;
    private JLabel nLabel;
    private JEditorPane webLabel;
    private JButton closeButton;
    private String[] names;
    private JPanel sdpanel;
    private JPanel contentPane;

    /**
     * Creates a new AboutWindow instance
     *
     * @param parent DOCUMENT ME!
     * @param title DOCUMENT ME!
     * @param modal DOCUMENT ME!
     * @param names DOCUMENT ME!
     */
    public AboutWindow(Frame parent, String title, boolean modal, String[] names) {
        super(parent, title, modal);

        enableInputMethods(false);

        this.names = names;

        // Bilder einladen
        ImageLoader loader = new ImageLoader();
        Image logoImg = loader.loadImage(ims.tiger.system.Images.TS_LOGO_TRANS);
        ImageIcon logoIcon = new ImageIcon(logoImg);

        imgLabel = new JLabel(title, JLabel.CENTER);
        imgLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        imgLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        Font ilFont = imgLabel.getFont();
        imgLabel.setFont(new Font("dialog", ilFont.getStyle(),
                ilFont.getSize() + 5));
        imgLabel.setForeground(Color.black);
        imgLabel.setPreferredSize(imgLabel.getPreferredSize());

        devLabel = new JLabel("developed by ");
        devLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        devLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        Font dlFont = devLabel.getFont();
        devLabel.setFont(new Font("dialog", dlFont.getStyle(),
                dlFont.getSize() + 4));
        devLabel.setForeground(Color.black);
        devLabel.setPreferredSize(devLabel.getPreferredSize());

        webLabel = new JEditorPane("text/html",
                "<HTML><FONT FACE=\"Dialog\"><A HREF=\"\">" +
                "www.mpi.nl/tools" + "</FONT></A></HTML>");
        webLabel.setEditable(false);
        webLabel.setPreferredSize(webLabel.getPreferredSize());

        closeButton = new JButton("Close");

        Dimension cdim = closeButton.getPreferredSize();
        closeButton.setPreferredSize(cdim);
        closeButton.setVerticalTextPosition(AbstractButton.CENTER); // default text position
        closeButton.setHorizontalTextPosition(AbstractButton.LEFT);
        closeButton.addActionListener(this);

        sdpanel = new JPanel();

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        sdpanel.setLayout(gbl);
        sdpanel.setBackground(Color.WHITE);

        c.fill = GridBagConstraints.NONE;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = c.weighty = 1.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        gbl.setConstraints(imgLabel, c);
        c.gridy = 1;
        gbl.setConstraints(devLabel, c);
        c.insets = new Insets(1, 1, 1, 1);

        int counter = c.gridy;

        for (int i = 0; i < names.length; i++) {
            nLabel = this.makeNameLabel(names[i]);
            counter++;
            c.gridy = counter;
            gbl.setConstraints(nLabel, c);
            sdpanel.add(nLabel);
        }

        c.insets = new Insets(5, 5, 5, 5);
        c.gridy = counter + 1;
        gbl.setConstraints(webLabel, c);
        c.gridy = c.gridy + 1;
        c.weightx = c.weighty = 0.0;
        gbl.setConstraints(closeButton, c);

        sdpanel.add(imgLabel);
        sdpanel.add(devLabel);
        sdpanel.add(webLabel);
        sdpanel.add(closeButton);

        // Inhaltscontainer
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());

        EmptyBorder border = new EmptyBorder(new Insets(10, 10, 10, 10));
        contentPane.add(sdpanel, BorderLayout.CENTER);

        setContentPane(contentPane);

        addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent evt) {
                    dispose();
                }
            });
        pack();
        setResizable(false);
        this.setLocationRelativeTo(this.getParent());
        setVisible(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param evt DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent evt) {
        Object obj = evt.getSource();

        if (obj == closeButton) {
            setVisible(false);

            return;
        }
    }

    private JLabel makeNameLabel(String name) {
        nameLabel = new JLabel(name);
        nameLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        nameLabel.setVerticalTextPosition(SwingConstants.BOTTOM);

        Font nlFont = nameLabel.getFont();
        nameLabel.setFont(new Font("dialog", nlFont.getStyle(), nlFont.getSize()));
        nameLabel.setForeground(Color.black);
        nameLabel.setPreferredSize(nameLabel.getPreferredSize());

        return nameLabel;
    }
}
