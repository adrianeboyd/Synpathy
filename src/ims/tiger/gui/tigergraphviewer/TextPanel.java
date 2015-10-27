/*
 * File:     TextPanel.java
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

import ims.tiger.corpus.Header;

import ims.tiger.gui.tigergraphviewer.draw.DisplayNode;
import ims.tiger.gui.tigergraphviewer.draw.DisplaySentence;
import ims.tiger.gui.tigergraphviewer.draw.DisplayT_Node;
import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;

import ims.tiger.util.UtilitiesCollection;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


/**
 * $Id: TextPanel.java,v 1.4 2007/01/03 11:46:05 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.4 $
 */
public class TextPanel extends JPanel {
    /** Holds value of property DOCUMENT ME! */
    private final JEditorPane htmlPane;
    private Header header;

    /**
     * Creates a new TextPanel object.
     *
     * @param header DOCUMENT ME!
     */
    public TextPanel(Header header) {
        this.header = header;
        setLayout(new BorderLayout());

        htmlPane = new JEditorPane("text/html", "");
        htmlPane.setPreferredSize(new Dimension(300, 72));
        htmlPane.setEditable(false);
        htmlPane.setCaretPosition(0);

        JScrollPane sentencePane = new JScrollPane(htmlPane);
        sentencePane.setMinimumSize(new Dimension(300, 72));
        sentencePane.setPreferredSize(new Dimension(300, 72));

        setBorder(BorderFactory.createRaisedBevelBorder());
        add(sentencePane, BorderLayout.CENTER);
    }

    /**
     * DOCUMENT ME!
     *
     * @param header Header
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * update sentence
     *
     * @param sentence Sentence
     */
    protected void updateText(DisplaySentence sentence) {
        if (sentence == null) {
            htmlPane.setText("");

            return;
        }

        Color nMatchColor = GraphConstants.nodeMatchColor;
        String nmHTMLColor = UtilitiesCollection.getHTMLRGBCode(nMatchColor);

        Color nMatchSGColor = GraphConstants.nodeMatchSubgraphColor;
        String nmsgHTMLColor = UtilitiesCollection.getHTMLRGBCode(nMatchSGColor);

        String mainfeature = (String) header.getAllTFeatureNames().get(0);
        int n = sentence.getDisplayLeafNodeSize();

        StringBuffer senttext = new StringBuffer();
        StringBuffer testtext = new StringBuffer();

        for (int i = 0; i < n; i++) {
            DisplayNode t = sentence.getDisplayLeafNode(i);

            if (t.isMatchNode()) {
                senttext.append("<font color=\"" + nmHTMLColor + "\">" +
                    t.getNode().getFeature(mainfeature) + "</font> ");
                testtext.append(t.getNode().getFeature(mainfeature));
            } else if (t.isMatchHighlightedNode()) {
                senttext.append("<font color=\"" + nmsgHTMLColor + "\">" +
                    t.getNode().getFeature(mainfeature) + "</font> ");
                testtext.append(t.getNode().getFeature(mainfeature));
            } else {
                senttext.append("<font color=\"black\">" +
                    t.getNode().getFeature(mainfeature) + "</font> ");
                testtext.append(t.getNode().getFeature(mainfeature));
            }
        }

        // Suche passenden Font
        Font textFont = UtilitiesCollection.chooseFont(GraphConstants.leafFont,
                testtext.toString());

        // Suche passende Font-Stufe
        int size = textFont.getSize();
        String relsize = "+0";

        if (size >= 9) {
            relsize = "-3";
        }

        if (size >= 10) {
            relsize = "-2";
        }

        if (size >= 12) {
            relsize = "-1";
        }

        if (size >= 14) {
            relsize = "+0";
        }

        if (size >= 18) {
            relsize = "+1";
        }

        if (size >= 20) {
            relsize = "+2";
        }

        if (size >= 24) {
            relsize = "+3";
        }

        String sentIDtext = "<html><font face=\"" +
            GraphConstants.leafFont[0].getName() + "\" " + "size=\"" + relsize +
            "\"><b>" + sentence.getSentence().getSentenceID() +
            ": </b></font>";
        String fonttext = "<font face=\"" + textFont.getName() + "\" " +
            "size=\"" + relsize + "\">";

        htmlPane.setText(sentIDtext + fonttext + senttext + "</font></html>");
    }
}
