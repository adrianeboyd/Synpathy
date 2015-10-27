/*
 * File:     IconBar.java
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
package ims.tiger.gui.tigergraphviewer;

import ims.tiger.gui.shared.ButtonSeparator;
import ims.tiger.gui.shared.ImageLoader;
import ims.tiger.gui.shared.ToolBar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ActionListener;

import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;


/** Button-Leiste am oberen Bildrand des GraphViewers zum direkten Aufruf der Optionen. */
public class IconBar extends ToolBar {
    /** Holds value of property DOCUMENT ME! */
    public boolean tcm = true;

    /** Holds value of property DOCUMENT ME! */
    public ButtonSeparator bse;
    private JToggleButton focus;
    private ImageIcon focusOnIcon;
    private ImageIcon focusOffIcon;
    private JToggleButton text;
    private ImageIcon textOnIcon;
    private ImageIcon textOffIcon;

    // Konstruktor
    public IconBar(boolean textCorpusMode) {
        // Bilder laden
        ImageLoader loader = new ImageLoader();

        Image focusOnImage = loader.loadImage(ims.tiger.system.Images.FOCUS_ON);
        focusOnIcon = new ImageIcon(focusOnImage.getScaledInstance(17, 17,
                    Image.SCALE_AREA_AVERAGING));

        Image focusOffImage = loader.loadImage(ims.tiger.system.Images.FOCUS_OFF);
        focusOffIcon = new ImageIcon(focusOffImage.getScaledInstance(17, 17,
                    Image.SCALE_AREA_AVERAGING));

        Image textOnImage = loader.loadImage(ims.tiger.system.Images.TEXT_ON);
        textOnIcon = new ImageIcon(textOnImage.getScaledInstance(17, 17,
                    Image.SCALE_AREA_AVERAGING));

        Image textOffImage = loader.loadImage(ims.tiger.system.Images.TEXT_OFF);
        textOffIcon = new ImageIcon(textOffImage.getScaledInstance(17, 17,
                    Image.SCALE_AREA_AVERAGING));

        Image img_open = loader.loadImage(ims.tiger.system.Images.IMG_OPEN);
        Image img_print = loader.loadImage(ims.tiger.system.Images.IMG_PRINT);
        Image img_save_svg = loader.loadImage(ims.tiger.system.Images.IMG_SAVE);
        Image img_save_svg_forest = loader.loadImage(ims.tiger.system.Images.IMG_SAVESVG);
        Image img_refresh = loader.loadImage(ims.tiger.system.Images.IMG_REFRESH);

        Image img_helpbook = loader.loadImage(ims.tiger.system.Images.HELP);

        tcm = textCorpusMode;
        setLayout(new BorderLayout());

        // Die Werkzeugleiste
        toolBar = new JToolBar();

        b = new JButton(new ImageIcon(img_open));
        this.initButton(b);
        b.setActionCommand("Open");
        b.setToolTipText("Open corpus file");

        if (textCorpusMode) {
            toolBar.add(b);
            addActionItem(b, "Open");
            this.setMouseListener(b);

            bse = new ButtonSeparator();
            toolBar.add(bse);
        }

        b = new JButton(new ImageIcon(img_print));
        this.initButton(b);
        b.setActionCommand("Print");
        b.setToolTipText("Print current graph");
        toolBar.add(b);
        addActionItem(b, "Print");
        this.setMouseListener(b);

        b = new JButton(new ImageIcon(img_save_svg));
        this.initButton(b);
        b.setActionCommand("save_svg");
        b.setToolTipText("Export current graph as image");
        toolBar.add(b);
        addActionItem(b, "save_svg");
        this.setMouseListener(b);

        // b = new JButton(new ImageIcon(img_save_svg_forest));
        // this.initButton(b);
        // b.setActionCommand("save_forest");
        // b.setToolTipText("Export forest as SVG");
        // toolBar.add(b);
        // addActionItem(b, "save_forest");
        // this.setMouseListener(b);
        bse = new ButtonSeparator();
        toolBar.add(bse);

        b = new JButton(new ImageIcon(img_refresh));
        this.initButton(b);
        b.setActionCommand("Refresh");
        b.setToolTipText("Refresh graph");
        toolBar.add(b);
        addActionItem(b, "Refresh");
        this.setMouseListener(b);

        Dimension tbdim = new Dimension(26, 26);

        focus = new JToggleButton(focusOffIcon, false);
        focus.setMinimumSize(tbdim);
        focus.setMaximumSize(tbdim);
        focus.setSize(tbdim);
        focus.setPreferredSize(tbdim);
        focus.setBorder(BorderFactory.createBevelBorder(2));
        focus.setBorderPainted(true);
        focus.setActionCommand("Focus");
        focus.setToolTipText("Focus on match");
        toolBar.add(focus);
        addActionItem(focus, "Focus");

        text = new JToggleButton(textOnIcon, true);
        text.setMinimumSize(tbdim);
        text.setMaximumSize(tbdim);
        text.setSize(tbdim);
        text.setPreferredSize(tbdim);
        text.setBorder(BorderFactory.createBevelBorder(2));
        text.setBorderPainted(true);
        text.setActionCommand("Text");
        text.setToolTipText("Display tokens as text");
        toolBar.add(text);
        addActionItem(text, "Text");

        bse = new ButtonSeparator();
        toolBar.add(bse);

        b = new JButton(new ImageIcon(img_helpbook));
        this.initButton(b);
        b.setToolTipText("Help");
        toolBar.add(b);
        addActionItem(b, "helpset");
        this.setMouseListener(b);

        add(toolBar, BorderLayout.NORTH);
    }

    /* Status des Focus-Buttons abfragen. */
    public boolean isFocusOnMatch() {
        return focus.isSelected();
    }

    /* Status des Focus-Buttons setzen. */
    public void setFocusOnMatch(boolean setFocus) {
        if (setFocus) {
            focus.setIcon(focusOnIcon);
        }

        if (!setFocus) {
            focus.setIcon(focusOffIcon);
        }

        focus.setSelected(setFocus);
    }

    /* Status des Text-Buttons abfragen. */
    public boolean isShowText() {
        return text.isSelected();
    }

    /* Status des Text-Buttons setzen. */
    public void setShowText(boolean setText) {
        if (setText) {
            text.setIcon(textOnIcon);
        }

        if (!setText) {
            text.setIcon(textOffIcon);
        }

        text.setSelected(setText);
    }

    /**
     * Setzt den Listener fuer ein Event.
     */
    public boolean setActionListener(ActionListener pListener, String pname) {
        boolean status = false;
        Eventeintrag ee = new Eventeintrag();

        for (Enumeration el = setOfEventlistener.elements();
                el.hasMoreElements();) {
            ee = (Eventeintrag) el.nextElement();

            if (ee.name == pname) {
                if (pname.equals("Focus") | pname.equals("Implode") |
                        pname.equals("Explode")) {
                    ee.lauscher = pListener;
                    ee.belauschter.setEnabled(!tcm);
                    ee.belauschter.addActionListener(pListener);
                } else {
                    ee.lauscher = pListener;
                    ee.belauschter.setEnabled(true);
                    ee.belauschter.addActionListener(pListener);
                }
            }
        }

        return status;
    }

    /** Aktiviert Fokussierung auf den Match. */
    public void setActivateFocusOnMatch(boolean focusOnMatch) {
        focus.setEnabled(focusOnMatch);
    }
}
