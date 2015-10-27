/*
 * File:     ExportXSLTConfiguration.java
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
package ims.tiger.export;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import org.jdom.input.SAXBuilder;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/** Klasse verwaltet die Stylesheets zum XSLT-Export. Die Stylesheets liegen
 *  im export/-Unterverzeichnis der TIGERSearch-Installation.
 */
public class ExportXSLTConfiguration {
    private HashMap stylesheet_map;
    private List stylesheet_names;
    private List stylesheet_headerfiles;
    private List stylesheet_sentencefiles;

    /**
     * Creates a new ExportXSLTConfiguration instance
     *
     * @param install_dir DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public ExportXSLTConfiguration(String install_dir)
        throws IOException {
        String config_xmlfile = install_dir +
            "/export/TIGERExportRegistry.xml";

        stylesheet_map = new HashMap();
        stylesheet_names = new ArrayList();
        stylesheet_headerfiles = new ArrayList();
        stylesheet_sentencefiles = new ArrayList();

        /* SAX-Parser einbinden */
        SAXBuilder builder = new SAXBuilder(false); // non-validating

        /* Dokument parsen */
        Document doc = null;

        try {
            doc = builder.build(config_xmlfile);
        } catch (JDOMException e) {
            throw new IOException(e.getMessage());
        }

        /* Stylesheet-Eintraege */
        Element root = doc.getRootElement();
        java.util.List config_file = root.getChildren();

        Element xslt;

        for (int i = 0; i < config_file.size(); i++) {
            xslt = (Element) config_file.get(i);

            String id = xslt.getAttributeValue("id");
            String name = xslt.getAttributeValue("name");
            String header = xslt.getAttributeValue("header_xslt");
            String sentence = xslt.getAttributeValue("sentence_xslt");
            stylesheet_map.put(id, sentence);
            stylesheet_names.add(name);
            stylesheet_headerfiles.add(header);
            stylesheet_sentencefiles.add(sentence);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int size() {
        return stylesheet_names.size();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object[] getStylesheetNames() {
        return stylesheet_names.toArray();
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getStylesheetName(int index) {
        return stylesheet_names.get(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getHeaderStylesheet(int index) {
        return stylesheet_headerfiles.get(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getSentenceStylesheet(int index) {
        return stylesheet_sentencefiles.get(index);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public HashMap getStylesheetMap() {
        return stylesheet_map;
    }
}
