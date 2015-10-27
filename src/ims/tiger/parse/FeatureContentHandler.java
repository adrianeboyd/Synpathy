/*
 * File:     FeatureContentHandler.java
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

package ims.tiger.parse;

import ims.tiger.corpus.Feature;
import ims.tiger.corpus.Header;

import ims.tiger.system.Constants;

import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;


/**
 * Reads features and their values/descriptions ("closed Vocabulary")
 *
 * @author Alexander Klassmann
 * @version 30/10/02
 */
public class FeatureContentHandler implements ContentHandler {
    private static Logger logger = Logger.getLogger(FeatureContentHandler.class.getName());

    /** Holds value of property DOCUMENT ME! */
    boolean inAnnotation;
    private Feature currentFeature;

    /** Holds value of property DOCUMENT ME! */
    private final Header header;
    private Locator locator;
    private String currentValueContent = "";
    private String currentValueName = "";

    /**
     * DOCUMENT ME!
     *
     * @param header header for storing the metadata of syntax the file
     */
    public FeatureContentHandler(Header header) {
        this.header = header;
    }

    // implementation of org.xml.sax.ContentHandler interface

    /**
     * DOCUMENT ME!
     *
     * @param param1 locator
     */
    public void setDocumentLocator(Locator param1) {
        this.locator = locator;
    }

    /**
         *
         */
    public void characters(char[] ch, int start, int end)
        throws SAXException {
        currentValueContent += new String(ch, start, end);
    }

    /**
     *
     * @exception SAXException
     */
    public void endDocument() throws SAXException {
        logger.debug("End parsing feature file.");
    }

    /**
     *
     * @param namespaceURI
     * @param elementName
     * @param rawName
     *
     * @exception SAXException
     */
    public void endElement(String namespaceURI, String elementName,
        String rawName) throws SAXException {
        String content = currentValueContent.trim();

        if (inAnnotation) {
            if (elementName.equals("value")) {
                if (currentFeature != null) {
                    currentFeature.addItem(currentValueName, content);
                }
            } else if (elementName.equals("feature")) {
                currentFeature = null;
            } else if (elementName.equals("annotation")) {
                inAnnotation = false;
            }

            //in 'meta' 
        } else if (elementName.equals("name")) {
            header.setCorpus_Name(content);
        } else if (elementName.equals("author")) {
            header.setCorpus_Author(content);
        } else if (elementName.equals("date")) {
            header.setCorpus_Date(content);
        } else if (elementName.equals("description")) {
            header.setCorpus_Description(content);
        } else if (elementName.equals("format")) {
            header.setCorpus_Format(content);
        } else if (elementName.equals("history")) {
            header.setCorpus_History(content);
        }

        currentValueContent = "";
    }

    /**
     *
     * @param param1
     *
     * @exception SAXException
     */
    public void endPrefixMapping(String param1) throws SAXException {
    }

    /**
     * don't ignore whitespace
     *
     * @param param1
     * @param param2
     * @param param3
     *
     * @exception SAXException
     */
    public void ignorableWhitespace(char[] param1, int param2, int param3)
        throws SAXException {
    }

    /**
     * DOCUMENT ME!
     *
     * @param param1
     * @param param2
     *
     * @exception SAXException
     */
    public void processingInstruction(String param1, String param2)
        throws SAXException {
        // TODO: implement this org.xml.sax.ContentHandler method
    }

    /**
     * DOCUMENT ME!
     *
     * @param param1
     *
     * @exception SAXException
     */
    public void skippedEntity(String param1) throws SAXException {
        // TODO: implement this org.xml.sax.ContentHandler method
    }

    /**
     * DOCUMENT ME!
     *
     * @exception SAXException
     */
    public void startDocument() throws SAXException {
        logger.debug("Start parsing feature file.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param namespaceURI
     * @param elementName
     * @param rawName
     * @param atts
     *
     * @exception SAXException
     */
    public void startElement(String namespaceURI, String elementName,
        String rawName, Attributes atts) throws SAXException {
        if (inAnnotation) {
            if (elementName.equals("feature")) {
                String name = atts.getValue("name");

                if ("T".equals(atts.getValue("domain"))) {
                    if (header.isTerminalFeature(name)) {
                        currentFeature = header.getTFeature(name);
                    } else {
                        currentFeature = new Feature(name);
                        header.addTerminalFeature(currentFeature);
                        logger.info("Set terminal feature '" + name + "'.");
                    }
                }

                if ("NT".equals(atts.getValue("domain"))) {
                    if (header.isNonterminalFeature(name)) {
                        currentFeature = header.getNTFeature(name);
                    } else {
                        currentFeature = new Feature(name);
                        header.addNonterminalFeature(currentFeature);
                        logger.info("Set non-terminal Feature '" + name + "'.");
                    }
                }
            } else if (elementName.equals("edgelabel")) {
                if (header.getEdgeFeature() != null) {
                    currentFeature = header.getEdgeFeature();
                } else {
                    header.setEdgesLabeled();
                    currentFeature = new Feature(Constants.EDGE);
                    header.setEdgeFeature(currentFeature);
                    logger.info("Set feature 'edges labelled'.");
                }
            } else if (elementName.equals("secedgelabel")) {
                if (header.getSecEdgeFeature() != null) {
                    currentFeature = header.getSecEdgeFeature();
                } else {
                    header.setSecondaryEdges();
                    currentFeature = new Feature(Constants.SECEDGE);
                    header.setSecEdgeFeature(currentFeature);
                    logger.info("Set feature 'secondary edges'.");
                }
            } else if (elementName.equals("value")) {
                currentValueName = atts.getValue("name");
            }
        } else if (elementName.equals("annotation")) {
            inAnnotation = true;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param param1
     * @param param2
     *
     * @exception SAXException
     */
    public void startPrefixMapping(String param1, String param2)
        throws SAXException {
    }
}
