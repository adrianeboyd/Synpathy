/*
 * File:     HTMLViewer.java
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

import org.apache.log4j.Logger;

import java.awt.BorderLayout;

import java.net.URL;

import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;


/** Ein kleiner Browser zur Darstellung von HTML-Seiten. */
public class HTMLViewer extends JEditorPane implements HyperlinkListener {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(HTMLViewer.class);
    private JPanel m_Panel;
    private HTMLEditorKit htmlKit = new HTMLEditorKit();
    private JButton m_back;
    private JButton m_forward;
    private ArrayList m_backForward = new ArrayList();
    private int m_pos = 0;

    /**
     * Creates a new HTMLViewer instance
     *
     * @param TheUrl DOCUMENT ME!
     */
    public HTMLViewer(String TheUrl) {
        m_Panel = new JPanel();
        m_Panel.setLayout(new BorderLayout());
        this.setEditorKit(htmlKit);
        this.setEditable(false);

        try {
            URL HtmlFile = new URL(TheUrl);
            setPage(HtmlFile);
        } catch (Exception e) {
            logger.error("Error in HTML viewer", e);
        }

        addHyperlinkListener(this);

        JScrollPane editorScrollPane = new JScrollPane(this);
        m_Panel.add(editorScrollPane, BorderLayout.CENTER);
    }

    /**
     * Creates a new HTMLViewer instance
     */
    public HTMLViewer() {
        m_Panel = new JPanel();
        m_Panel.setLayout(new BorderLayout());
        setEditorKit(htmlKit);
        setEditable(false);

        addHyperlinkListener(this);

        JScrollPane editorScrollPane = new JScrollPane(this);
        m_Panel.add(editorScrollPane, BorderLayout.CENTER);
    }

    /**
     * DOCUMENT ME!
     *
     * @param TheUrl DOCUMENT ME!
     */
    public void loadUrl(String TheUrl) {
        try {
            URL HtmlFile = new URL(TheUrl);
            setPage(HtmlFile);
            m_backForward.add(HtmlFile);
            m_pos++;
        } catch (Exception e) {
            logger.error("Error in HTML viewer", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JPanel GetViewerPanel() {
        return m_Panel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            JEditorPane pane = (JEditorPane) e.getSource();

            if (e instanceof HTMLFrameHyperlinkEvent) {
                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                HTMLDocument doc = (HTMLDocument) pane.getDocument();
                doc.processHTMLFrameHyperlinkEvent(evt);
            } else {
                URL url = e.getURL();

                try {
                    setPage(url);

                    if ((m_backForward.size() - 1) > m_pos) {
                        m_backForward = new ArrayList(m_backForward.subList(0,
                                    m_pos + 1));
                    }

                    m_backForward.add(url);
                    m_pos = m_backForward.size() - 1;
                    checkButtons();
                } catch (Exception ex) {
                    logger.error("Error during calculation of tooltip: " +
                        ex.getMessage());
                    logger.error("Error in HTML viewer", ex);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void back() {
        try {
            if (m_pos > 0) {
                m_pos--;

                URL HtmlFile = (URL) m_backForward.get(m_pos);
                setPage(HtmlFile);
            }
        } catch (Exception e) {
            logger.error("Error in HTML viewer", e);
        }

        checkButtons();
    }

    /**
     * DOCUMENT ME!
     */
    public void forward() {
        try {
            if (m_pos < (m_backForward.size() - 1)) {
                m_pos++;

                URL HtmlFile = (URL) m_backForward.get(m_pos);
                setPage(HtmlFile);
            }
        } catch (Exception e) {
            logger.error("Error in HTML viewer", e);
        }

        checkButtons();
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setForwardButton(JButton b) {
        m_forward = b;
        checkButtons();
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setBackButton(JButton b) {
        m_back = b;
        checkButtons();
    }

    /**
     * DOCUMENT ME!
     */
    public void checkButtons() {
        if (m_back != null) {
            m_back.setEnabled((m_pos > 0));
            m_back.repaint();
        }

        if (m_forward != null) {
            m_forward.setEnabled((m_pos < (m_backForward.size() - 1)));
        }
    }
}
