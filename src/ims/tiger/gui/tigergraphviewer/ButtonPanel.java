/*
 * File:     ButtonPanel.java
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

/* This program is free software; you can redistribute it and/or modify
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
package ims.tiger.gui.tigergraphviewer;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.util.Hashtable;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * $Id: ButtonPanel.java,v 1.5 2007/01/04 17:48:49 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.5 $
 */
public class ButtonPanel extends JPanel {
    /** Holds value of property DOCUMENT ME! */
    private static final Dimension BIG_BUTTON_SIZE = new Dimension(100, 27);

    /** Holds value of property DOCUMENT ME! */
    private static final Dimension SMALL_BUTTON_SIZE = new Dimension(50, 27);

    /** Holds value of property DOCUMENT ME! */
    private static final Dimension NUMBER_FIELD_SIZE = new Dimension(50, 20);

    /** Holds value of property DOCUMENT ME! */
    private final CardLayout cardLayout;

    /** Holds value of property DOCUMENT ME! */
    private final JLabel graphCountLabel;

    /** Holds value of property DOCUMENT ME! */
    private final JLabel subgraphCountLabel;

    /** Holds value of property DOCUMENT ME! */
    private final JLabel subgraphsLabel;

    /** Holds value of property DOCUMENT ME! */
    private final JLabel submatchLabel;

    /** Holds value of property DOCUMENT ME! */
    private final JPanel submatchPanelShell;

    /** Holds value of property DOCUMENT ME! */
    private final JSlider cSlider;

    /** Holds value of property DOCUMENT ME! */
    private final JTextField numberField;
    private int forestSize = -1;
    private int submatchCount = -1;
    private int submatchValue = -1;

    /**
     * Creates a new ButtonPanel object.
     *
     * @param firstMatchAction DOCUMENT ME!
     * @param lastMatchAction DOCUMENT ME!
     * @param previousMatchAction DOCUMENT ME!
     * @param nextMatchAction DOCUMENT ME!
     * @param gotoMatchAction DOCUMENT ME!
     * @param previousSubMatchAction DOCUMENT ME!
     * @param nextSubMatchAction DOCUMENT ME!
     */
    public ButtonPanel(Action firstMatchAction, Action lastMatchAction,
        Action previousMatchAction, Action nextMatchAction,
        final Action gotoMatchAction, Action previousSubMatchAction,
        Action nextSubMatchAction) {
        JLabel sentencesLabel = new JLabel("Sentences:");

        Font labelFont = new Font(sentencesLabel.getFont().getName(),
                sentencesLabel.getFont().getStyle(), 12);
        sentencesLabel.setFont(labelFont);

        graphCountLabel = new JLabel();
        graphCountLabel.setFont(labelFont);
        graphCountLabel.setForeground(Color.black);

        subgraphsLabel = new JLabel("Subgraphs:");
        subgraphsLabel.setFont(labelFont);

        subgraphCountLabel = new JLabel();
        subgraphCountLabel.setFont(labelFont);
        subgraphCountLabel.setForeground(Color.black);

        JPanel mpContentPanel = new JPanel();
        mpContentPanel.setLayout(new GridLayout(2, 2, 10, 0));

        sentencesLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
        subgraphsLabel.setBorder(new EmptyBorder(5, 10, 5, 0));
        mpContentPanel.add(sentencesLabel);
        mpContentPanel.add(graphCountLabel);
        mpContentPanel.add(subgraphsLabel);
        mpContentPanel.add(subgraphCountLabel);
        subgraphsLabel.setVisible(false);
        subgraphCountLabel.setVisible(false);

        EtchedBorder eBorder = new EtchedBorder(EtchedBorder.LOWERED);
        mpContentPanel.setBorder(eBorder);

        GridBagLayout matchglB = new GridBagLayout();
        JPanel matchPanel = new JPanel();
        matchPanel.setLayout(matchglB);

        JButton previous = new JButton(previousMatchAction);
        previous.setMinimumSize(BIG_BUTTON_SIZE);
        previous.setMaximumSize(BIG_BUTTON_SIZE);
        previous.setPreferredSize(BIG_BUTTON_SIZE);
        previous.setSize(BIG_BUTTON_SIZE);
        previous.setVerticalTextPosition(AbstractButton.CENTER); // default text position
        previous.setHorizontalTextPosition(AbstractButton.RIGHT);
        previous.setBorder(BorderFactory.createRaisedBevelBorder());

        numberField = new JTextField("");
        numberField.setMinimumSize(NUMBER_FIELD_SIZE);
        numberField.setMaximumSize(NUMBER_FIELD_SIZE);
        numberField.setPreferredSize(NUMBER_FIELD_SIZE);
        numberField.setSize(NUMBER_FIELD_SIZE);
        numberField.setHorizontalAlignment(JTextField.CENTER);
        numberField.requestFocus();

        JButton next = new JButton(nextMatchAction);
        next.setMinimumSize(BIG_BUTTON_SIZE);
        next.setMaximumSize(BIG_BUTTON_SIZE);
        next.setPreferredSize(BIG_BUTTON_SIZE);
        next.setSize(BIG_BUTTON_SIZE);
        next.setBorder(BorderFactory.createRaisedBevelBorder());
        next.setVerticalTextPosition(AbstractButton.CENTER);
        next.setHorizontalTextPosition(AbstractButton.LEFT);

        GridBagConstraints cC = new GridBagConstraints();

        cC.fill = GridBagConstraints.BOTH;
        cC.insets = new Insets(1, 1, 1, 10);
        cC.weightx = 1.0;
        cC.gridwidth = 1;
        cC.gridheight = 1;
        cC.gridx = 0;
        cC.gridy = 0;
        matchglB.setConstraints(previous, cC);
        cC.weightx = 0.0;
        cC.gridx = 1;
        cC.fill = GridBagConstraints.NONE;
        cC.insets = new Insets(1, 1, 1, 1);
        matchglB.setConstraints(numberField, cC);
        cC.weightx = 1.0;
        cC.gridx = 2;
        cC.fill = GridBagConstraints.BOTH;
        cC.insets = new Insets(1, 10, 1, 1);
        matchglB.setConstraints(next, cC);

        matchPanel.add(previous);
        matchPanel.add(numberField);
        matchPanel.add(next);

        GridBagLayout matchglC = new GridBagLayout();
        JPanel matchPanelBig = new JPanel();
        matchPanelBig.setLayout(matchglC);

        cSlider = new JSlider(JSlider.HORIZONTAL, 0, 0, 0);
        cSlider.setEnabled(false);

        Dimension sdim = new Dimension(200, 16);
        cSlider.setMinimumSize(sdim);
        cSlider.setMaximumSize(sdim);
        cSlider.setPreferredSize(sdim);
        cSlider.setSize(sdim);
        cSlider.setMinorTickSpacing(1);
        cSlider.setPaintTicks(false);
        cSlider.setPaintLabels(true);

        JButton home = new JButton(firstMatchAction);
        home.setMinimumSize(SMALL_BUTTON_SIZE);
        home.setMaximumSize(SMALL_BUTTON_SIZE);
        home.setPreferredSize(SMALL_BUTTON_SIZE);
        home.setSize(SMALL_BUTTON_SIZE);
        home.setBorder(BorderFactory.createRaisedBevelBorder());

        JButton end = new JButton(lastMatchAction);
        end.setMinimumSize(SMALL_BUTTON_SIZE);
        end.setMaximumSize(SMALL_BUTTON_SIZE);
        end.setPreferredSize(SMALL_BUTTON_SIZE);
        end.setSize(SMALL_BUTTON_SIZE);
        end.setBorder(BorderFactory.createRaisedBevelBorder());

        GridBagConstraints cD = new GridBagConstraints();

        cD.fill = GridBagConstraints.BOTH;
        cD.insets = new Insets(1, 1, 1, 1);
        cD.weightx = 1.0;
        cD.gridwidth = 3;
        cD.gridheight = 1;
        cD.gridx = 0;
        cD.gridy = 0;
        matchglC.setConstraints(matchPanel, cD);
        cD.gridwidth = 1;
        cD.gridy = 1;
        matchglC.setConstraints(home, cD);
        cD.gridx = 1;
        matchglC.setConstraints(cSlider, cD);
        cD.gridx = 2;
        matchglC.setConstraints(end, cD);

        matchPanelBig.add(matchPanel);
        matchPanelBig.add(home);
        matchPanelBig.add(cSlider);
        matchPanelBig.add(end);

        //Submatch-Information in einem extra Panel
        Dimension smd01 = new Dimension(60, 18);
        Dimension smd02 = new Dimension(60, 18);

        JLabel smatch = new JLabel("Subgraph:");
        smatch.setHorizontalAlignment(SwingConstants.CENTER);
        smatch.setVerticalAlignment(SwingConstants.CENTER);

        Font smatchFont = new Font(smatch.getFont().getName(),
                smatch.getFont().getStyle(), 11);
        smatch.setFont(smatchFont);
        smatch.setForeground(new Color(74, 156, 74));
        smatch.setMinimumSize(smd02);
        smatch.setMaximumSize(smd02);
        smatch.setPreferredSize(smd02);
        smatch.setSize(smd02);

        submatchLabel = new JLabel();
        submatchLabel.setVerticalAlignment(SwingConstants.CENTER);
        submatchLabel.setFont(smatchFont);
        submatchLabel.setForeground(Color.black);
        submatchLabel.setMinimumSize(smd01);
        submatchLabel.setMaximumSize(smd01);
        submatchLabel.setPreferredSize(smd01);
        submatchLabel.setSize(smd01);

        Dimension matchBDim = new Dimension(75, 24);

        JButton previousm = new JButton(previousSubMatchAction);
        previousm.setMinimumSize(matchBDim);
        previousm.setMaximumSize(matchBDim);
        previousm.setPreferredSize(matchBDim);
        previousm.setSize(matchBDim);
        previousm.setBorder(BorderFactory.createRaisedBevelBorder());
        previousm.setFont(smatchFont);

        JButton nextm = new JButton(nextSubMatchAction);
        nextm.setMinimumSize(matchBDim);
        nextm.setMaximumSize(matchBDim);
        nextm.setPreferredSize(matchBDim);
        nextm.setSize(matchBDim);
        nextm.setBorder(BorderFactory.createRaisedBevelBorder());
        nextm.setFont(smatchFont);
        nextm.setHorizontalTextPosition(AbstractButton.LEFT);

        GridLayout glo = new GridLayout(1, 2, 5, 0);
        JPanel submatchNavPanel = new JPanel(glo);
        submatchNavPanel.add(previousm);
        submatchNavPanel.add(nextm);

        JPanel submatchPanel = new JPanel();

        GridBagLayout gblmpC = new GridBagLayout();
        GridBagConstraints cmpC = new GridBagConstraints();
        submatchPanel.setLayout(gblmpC);

        cmpC.fill = GridBagConstraints.NONE;
        cmpC.insets = new Insets(5, 5, 5, 5);
        cmpC.gridwidth = 1;
        cmpC.gridheight = 1;
        cmpC.gridx = 0;
        cmpC.gridy = 0;
        gblmpC.setConstraints(smatch, cmpC);
        cmpC.gridx = 1;
        gblmpC.setConstraints(submatchLabel, cmpC);
        cmpC.gridx = 0;
        cmpC.gridy = 1;
        cmpC.gridwidth = 2;
        gblmpC.setConstraints(submatchNavPanel, cmpC);

        submatchPanel.add(smatch);
        submatchPanel.add(submatchLabel);
        submatchPanel.add(submatchNavPanel);

        submatchPanelShell = new JPanel();
        cardLayout = new CardLayout();
        submatchPanelShell.setLayout(cardLayout);
        submatchPanel.setBorder(eBorder);
        submatchPanelShell.add("submatchPanel", submatchPanel);
        submatchPanelShell.add("blank", new JPanel());
        cardLayout.show(submatchPanelShell, "blank");

        GridBagLayout gbl = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        setLayout(gbl);

        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(8, 5, 8, 5);
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.gridx = 0;
        c.gridy = 0;
        gbl.setConstraints(mpContentPanel, c);
        c.gridx = 1;
        gbl.setConstraints(matchPanelBig, c);
        c.gridx = 2;
        gbl.setConstraints(submatchPanelShell, c);

        add(mpContentPanel);
        add(matchPanelBig);
        add(submatchPanelShell);

        numberField.addKeyListener(new KeyAdapter() {
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                        gotoMatchAction.actionPerformed(new ActionEvent(this,
                                0, numberField.getText()));
                    }
                }
            });

        cSlider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    if (!cSlider.getValueIsAdjusting()) {
                        gotoMatchAction.actionPerformed(new ActionEvent(this,
                                0, "" + cSlider.getValue()));
                    }
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     */
    public void setCurrentGraph(int n) {
        if (n > 0) {
            numberField.setText("" + n);
        } else {
            numberField.setText("");
        }

        if (cSlider.getValue() != n) {
            cSlider.setValue(n);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     */
    public void setCurrentSubmatch(int n) {
        submatchValue = n;
        updateSubmatchLabel();
    }

    /**
     * DOCUMENT ME!
     *
     * @param size DOCUMENT ME!
     */
    public void setForestSize(int size) {
        graphCountLabel.setText((size > 0) ? ("" + size) : " ?");
        forestSize = size;
        configureSlider();
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setSliderEnabled(boolean b) {
        cSlider.setEnabled(b);
    }

    /**
     * DOCUMENT ME!
     *
     * @param subgraphCount number of subgraphs in forest
     */
    public void setSubgraphCount(int subgraphCount) {
        subgraphsLabel.setVisible(subgraphCount >= 0);
        subgraphCountLabel.setVisible(subgraphCount >= 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param submatchCount number of subgraphs for current Graph
     */
    public void setSubmatchCount(int submatchCount) {
        this.submatchCount = submatchCount;
        updateSubmatchLabel();
        cardLayout.show(submatchPanelShell,
            (submatchCount == -1) ? "blank" : "submatchPanel");
    }

    private void configureSlider() {
        if (cSlider.isEnabled()) {
            cSlider.setValueIsAdjusting(true);
            cSlider.setMinimum(1);
            cSlider.setMaximum(forestSize);
            cSlider.setValue(1);

            //Tabelle fuer Slider-Werte erzeugen
            Hashtable labelTable = new Hashtable();
            JLabel homeL = new JLabel();
            JLabel endL = new JLabel();
            homeL.setText("1");
            endL.setText("" + forestSize);

            Font newFont = new Font("Arial", Font.PLAIN, 10);
            homeL.setFont(newFont);
            homeL.setHorizontalAlignment(JLabel.CENTER);
            endL.setFont(newFont);
            endL.setHorizontalAlignment(JLabel.CENTER);

            labelTable.put(new Integer(1), homeL);
            labelTable.put(new Integer(forestSize), endL);
            cSlider.setLabelTable(labelTable);
        }
    }

    private void updateSubmatchLabel() {
        String s = "--";

        if (submatchCount > -1) {
            s = "" + submatchCount;

            if (submatchValue > -1) {
                s += (" / " + submatchValue);
            }
        }

        submatchLabel.setText(s);
    }
}
