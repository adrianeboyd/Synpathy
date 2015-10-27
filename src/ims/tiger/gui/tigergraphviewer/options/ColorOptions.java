/*
 * File:     ColorOptions.java
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

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;


/** Fenster zur Einstellung der Farboptionen des GraphViewers. */
public class ColorOptions extends JDialog implements ActionListener {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(ColorOptions.class);
    private Container contentPane;
    private GridBagLayout gbl;
    private GridBagConstraints c;
    private GridLayout gl;
    private JButton ok;
    private JButton cancel;
    private JButton reset;
    private JButton defaultColors;
    private JButton colorButton;
    private JButton cobu;
    private JLabel nameLabel;
    private JLabel nala;
    private JPanel topPanel;
    private JPanel bottomPanel;
    private String[] names;

    //Sichern der Einstellungen fuer Reset
    private Object[] backItUp;
    private Color[] colors;
    private TIGERGraphViewerConfiguration config;

    /** Holds value of property DOCUMENT ME! */
    Dimension colorButtonDim;

    /**
     * Creates a new ColorOptions instance
     *
     * @param config DOCUMENT ME!
     * @param parent DOCUMENT ME!
     */
    public ColorOptions(TIGERGraphViewerConfiguration config, Frame parent) {
        super(parent, "Color Options", true);

        enableInputMethods(false);

        this.config = config;

        colors = ColorOptions.makeColors();

        /* a) Allgemeine Eigenschaften */
        contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.setSize(new Dimension(300, 300));

        EmptyBorder emptyBorderTop = new EmptyBorder(new Insets(25, 20, 5, 20));
        EmptyBorder emptyBorderBottom = new EmptyBorder(new Insets(5, 20, 20, 20));
        colorButtonDim = new Dimension(100, 30);

        //Parameter sichern
        colors = ColorOptions.makeColors();

        //Display-Parameter nicht ueberschreiben!!! (Verbindung mit DisplayOptions)
        backItUp = DisplayOptions.getParameters();

        names = this.makeNames();

        /* b) Top Panel  */
        topPanel = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        topPanel.setLayout(gbl);
        topPanel.setBorder(emptyBorderTop);

        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 10, 5, 10);

        int countery = c.gridy;

        for (int i = 0; i < names.length; i++) {
            nala = this.makeNameLabel(names[i]);
            cobu = this.makeColorButton(colors[i]);
            cobu.setName(names[i]);

            ColorButtonListener cobuli = new ColorButtonListener(cobu, this);
            cobu.addActionListener(cobuli);
            gbl.setConstraints(nala, c);
            topPanel.add(nala);
            c.gridx = 1;
            gbl.setConstraints(cobu, c);
            topPanel.add(cobu);
            countery++;
            c.gridy = countery;
            c.gridx = 0;
        }

        /* c) Bottom Panel  */
        bottomPanel = new JPanel();
        gbl = new GridBagLayout();
        c = new GridBagConstraints();
        bottomPanel.setLayout(gbl);
        bottomPanel.setBorder(emptyBorderBottom);

        JSeparator josep = new JSeparator(SwingConstants.HORIZONTAL);

        Dimension subD = new Dimension(50, 30);

        ok = new JButton("OK");
        ok.setToolTipText("Change colors");
        ok.addActionListener(this);
        ok.setMaximumSize(subD);
        ok.setMinimumSize(subD);
        ok.setPreferredSize(subD);
        ok.setSize(subD);
        ok.setBorder(BorderFactory.createEtchedBorder());

        cancel = new JButton("Cancel");
        cancel.setToolTipText("No color changed");
        cancel.addActionListener(this);
        cancel.setMaximumSize(subD);
        cancel.setMinimumSize(subD);
        cancel.setPreferredSize(subD);
        cancel.setSize(subD);
        cancel.setBorder(BorderFactory.createEtchedBorder());

        reset = new JButton("Reset");
        reset.setToolTipText("Reset all colors");
        reset.addActionListener(this);
        reset.setMaximumSize(subD);
        reset.setMinimumSize(subD);
        reset.setPreferredSize(subD);
        reset.setSize(subD);
        reset.setBorder(BorderFactory.createEtchedBorder());

        defaultColors = new JButton("Default");
        defaultColors.setToolTipText("Set all colors to default");
        defaultColors.addActionListener(this);
        defaultColors.setMaximumSize(subD);
        defaultColors.setMinimumSize(subD);
        defaultColors.setPreferredSize(subD);
        defaultColors.setSize(subD);
        defaultColors.setBorder(BorderFactory.createEtchedBorder());

        c.gridwidth = 5;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 15, 5);
        gbl.setConstraints(josep, c);
        c.gridwidth = 1;
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        gbl.setConstraints(ok, c);
        c.gridx = 2;
        gbl.setConstraints(cancel, c);
        c.gridx = 3;
        gbl.setConstraints(reset, c);
        c.gridx = 4;
        gbl.setConstraints(defaultColors, c);

        bottomPanel.add(josep);
        bottomPanel.add(ok);
        bottomPanel.add(cancel);
        bottomPanel.add(reset);
        bottomPanel.add(defaultColors);

        contentPane.add(topPanel, BorderLayout.NORTH);
        contentPane.add(bottomPanel, BorderLayout.SOUTH);
        this.setContentPane(contentPane);
        pack();

        /* Direktes Verlassen */
        this.addWindowListener(new ColorOptionsWindowListener());
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
                Component[] topcomp = topPanel.getComponents();

                for (int i = 0; i < topcomp.length; i++) {
                    if (topcomp[i] instanceof JButton) {
                        for (int j = 0; j < names.length; j++) {
                            if (topcomp[i].getName().equals(names[j])) {
                                if (topcomp[i].getName().equals("Background Color")) {
                                    GraphConstants.panelBackgroundColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Foreground Color")) {
                                    GraphConstants.panelColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Node Background Color")) {
                                    GraphConstants.nodeBackgroundColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Node Color")) {
                                    GraphConstants.nodeColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Node Match Color")) {
                                    GraphConstants.nodeMatchColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Node Match Subgraph Color")) {
                                    GraphConstants.nodeMatchSubgraphColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Node Highlighted Color")) {
                                    GraphConstants.nodeHighlightedColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Node Imploded Color")) {
                                    GraphConstants.nodeImplodedColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Secondary Edge Color")) {
                                    GraphConstants.secondaryEdgeColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Label Background Color")) {
                                    GraphConstants.labelBackgroundColor = topcomp[i].getBackground();
                                }

                                if (topcomp[i].getName().equals("Virtual Root Node Color")) {
                                    GraphConstants.virtualRootNodeColor = topcomp[i].getBackground();
                                }
                            }
                        }
                    }
                }

                DisplayOptions.resetConfigParameters(backItUp);
                this.setVisible(false);
            }

            if (e.getActionCommand() == "Cancel") {
                this.setButtonColor(colors);
                ColorOptions.resetConfigColors(colors);
                DisplayOptions.resetConfigParameters(backItUp);
                this.setVisible(false);
            }

            if (e.getActionCommand() == "Reset") {
                this.setButtonColor(colors);
                ColorOptions.resetConfigColors(colors);
            }

            if (e.getActionCommand() == "Default") {
                // 1. System-Defaults lesen
                try {
                    config.resetConfiguration();
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this,
                        "Could not reset configuration:\n" + exp.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }

                // 2. Default-Werte wieder in Buttons uebernehmen
                this.setButtonColor(ColorOptions.makeColors());
            }

            // Event-Handling ENDE
        } catch (OutOfMemoryError err) {
            logger.error("Out of memory error in ColorOptions action listener.");
            ims.tiger.util.UtilitiesCollection.showOutOfMemoryMessage(this,
                false, true);
        } catch (Error err) {
            logger.error("Java error in ColorOptions action listener", err);
        } catch (Exception err) {
            logger.error("Java exception in ColorOptions action listener", err);
        }
    }

    private JLabel makeNameLabel(String name) {
        nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.black);

        return nameLabel;
    }

    private JButton makeColorButton(Color color) {
        colorButton = new JButton("Choose");
        colorButton.setToolTipText("Choose color");
        colorButton.setVerticalAlignment(SwingConstants.CENTER);
        colorButton.setHorizontalAlignment(SwingConstants.CENTER);
        colorButton.setMaximumSize(colorButtonDim);
        colorButton.setMinimumSize(colorButtonDim);
        colorButton.setPreferredSize(colorButtonDim);
        colorButton.setSize(colorButtonDim);
        colorButton.setBorder(BorderFactory.createEtchedBorder());
        colorButton.setOpaque(true);
        colorButton.setBackground(color);

        if (((color.getRed() > 200) && (color.getGreen() > 200)) ||
                ((color.getRed() > 200) && (color.getBlue() > 200)) ||
                ((color.getGreen() > 200) && (color.getGreen() > 200))) {
            colorButton.setForeground(Color.black);
        } else {
            colorButton.setForeground(Color.white);
        }

        return colorButton;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Color[] makeColors() {
        Color[] farben = new Color[11];
        farben[0] = GraphConstants.panelBackgroundColor;
        farben[1] = GraphConstants.panelColor;
        farben[2] = GraphConstants.nodeBackgroundColor;
        farben[3] = GraphConstants.nodeColor;
        farben[4] = GraphConstants.nodeMatchColor;
        farben[5] = GraphConstants.nodeMatchSubgraphColor;
        farben[6] = GraphConstants.nodeHighlightedColor;
        farben[7] = GraphConstants.nodeImplodedColor;
        farben[8] = GraphConstants.secondaryEdgeColor;
        farben[9] = GraphConstants.labelBackgroundColor;
        farben[10] = GraphConstants.virtualRootNodeColor;

        return farben;
    }

    private String[] makeNames() {
        String[] names = new String[11];
        names[0] = "Background Color";
        names[1] = "Foreground Color";
        names[2] = "Node Background Color";
        names[3] = "Node Color";
        names[4] = "Node Match Color";
        names[5] = "Node Match Subgraph Color";
        names[6] = "Node Highlighted Color";
        names[7] = "Node Imploded Color";
        names[8] = "Secondary Edge Color";
        names[9] = "Label Background Color";
        names[10] = "Virtual Root Node Color";

        return names;
    }

    private void setButtonColor(Color[] colcol) {
        Component[] topcomp = topPanel.getComponents();

        for (int i = 0; i < topcomp.length; i++) {
            if (topcomp[i] instanceof JButton) {
                for (int j = 0; j < names.length; j++) {
                    if (topcomp[i].getName().equals(names[j])) {
                        topcomp[i].setBackground(colcol[j]);

                        if (((colcol[j].getRed() > 200) &&
                                (colcol[j].getGreen() > 200)) ||
                                ((colcol[j].getRed() > 200) &&
                                (colcol[j].getBlue() > 200)) ||
                                ((colcol[j].getGreen() > 200) &&
                                (colcol[j].getGreen() > 200))) {
                            topcomp[i].setForeground(Color.black);
                        } else {
                            topcomp[i].setForeground(Color.white);
                        }
                    }
                     //if
                }
                 //for
            }
             //if
        }
         //for
    }
     //setButtonColor

    /**
     * DOCUMENT ME!
     *
     * @param farben DOCUMENT ME!
     */
    public static void resetConfigColors(Color[] farben) {
        GraphConstants.panelBackgroundColor = farben[0];
        GraphConstants.panelColor = farben[1];
        GraphConstants.nodeBackgroundColor = farben[2];
        GraphConstants.nodeColor = farben[3];
        GraphConstants.nodeMatchColor = farben[4];
        GraphConstants.nodeMatchSubgraphColor = farben[5];
        GraphConstants.nodeHighlightedColor = farben[6];
        GraphConstants.nodeImplodedColor = farben[7];
        GraphConstants.secondaryEdgeColor = farben[8];
        GraphConstants.labelBackgroundColor = farben[9];
        GraphConstants.virtualRootNodeColor = farben[10];
    }

    /**
     * DOCUMENT ME!
     * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
     * @author $Author: hasloe $
     * @version $Revision: 1.3 $
     */
    public class ColorOptionsWindowListener extends WindowAdapter {
        /**
         * DOCUMENT ME!
         *
         * @param e DOCUMENT ME!
         */
        public void windowClosing(WindowEvent e) {
            setButtonColor(colors);
            resetConfigColors(colors);
            DisplayOptions.resetConfigParameters(backItUp);
            setVisible(false);
        }
    }
}
