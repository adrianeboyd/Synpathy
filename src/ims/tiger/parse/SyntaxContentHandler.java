/*
 * File:     SyntaxContentHandler.java
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
import ims.tiger.corpus.NT_Node;
import ims.tiger.corpus.Node;
import ims.tiger.corpus.Sentence;
import ims.tiger.corpus.T_Node;

import ims.tiger.system.Constants;

import org.apache.log4j.Logger;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;


/**
 * Parses a TIGER or a CGN syntax file in xml-format. In case of a TIGER file
 * all features found within annotations will be added to the header. In case
 * of a CGN-file and no features are defined in header, then Features 'word',
 * 'pos', 'morph' and 'cat' are added.
 *
 * @author Alexander Klassmann
 * @version 1.0 30/10/02
 */
public class SyntaxContentHandler implements ContentHandler {
    private static Logger logger = Logger.getLogger(SyntaxContentHandler.class.getName());

    /** Holds value of property DOCUMENT ME! */
    private final Header header;

    /** Holds value of property DOCUMENT ME! */
    private final Vector sentenceVector;
    private HashMap primEdgeLabelHash = new HashMap();
    private HashMap secEdgeLabelHash = new HashMap();
    private Hashtable primEdgeHash = new Hashtable();
    private Hashtable secEdgeHash = new Hashtable();
    private Locator locator;
    private Node currentNode;
    private Sentence currentSentence;
    private String currentContent = "";
    private String root;
    private boolean tiger;
    private int edgeNumber;
    private int ntNodeNumber;
    private int ntNodeNumberInSentence;
    private int tNodeNumber;
    private int tNodeNumberInSentence;

    /**
     * DOCUMENT ME!
     *
     * @param header for storing the metadata (like features, corpus name, etc)
     * @param sentenceVector contains the Sentences of the corpus
     */
    public SyntaxContentHandler(Header header, Vector sentenceVector) {
        this.header = header;
        this.sentenceVector = sentenceVector;
    }

    // implementation of org.xml.sax.ContentHandler interface

    /**
     * DOCUMENT ME!
     *
     * @param locator
     */
    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    /**
     * DOCUMENT ME!
     *
     * @param ch
     * @param start
     * @param end
     *
     * @exception SAXException
     */
    public void characters(char[] ch, int start, int end)
        throws SAXException {
        if (!tiger) {
            currentContent += new String(ch, start, end);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @exception SAXException
     */
    public void endDocument() throws SAXException {
        header.setNumberOfSentences(sentenceVector.size());
        header.setNumberOfTNodes(tNodeNumber);
        header.setNumberOfNTNodes(ntNodeNumber);
        header.setNumberOfEdges(edgeNumber);

        //setting in any case at least three terminal and one non-terminal feature
        if (header.getAllTerminalFeaturesSize() == 0) {
            header.addTerminalFeature(new Feature("word"));
            logger.warn(
                "No user defined terminal features - set terminal feature 'word'.");
        }

        logger.debug("End parsing syntax file.");
    }

    /**
     * DOCUMENT ME!
     *
     * @param namespaceURI
     * @param elementName
     * @param rawName
     *
     * @exception SAXException
     */
    public void endElement(String namespaceURI, String elementName,
        String rawName) throws SAXException {
        if ((tiger && (elementName.equals("s"))) ||
                ((!tiger) && (elementName.equals("sentence")))) {
            Enumeration e = primEdgeHash.keys();

            while (e.hasMoreElements()) {
                String id = (String) e.nextElement();
                int childNodeNumber = currentSentence.getTerminalPositionOf(id);

                if (childNodeNumber == -1) {
                    childNodeNumber = currentSentence.getNonterminalPositionOf(id) +
                        Constants.CUT;
                }

                int parentNodeNumber = currentSentence.getNonterminalPositionOf((String) primEdgeHash.get(
                            id));
                currentSentence.getNode(childNodeNumber).setParent(Constants.CUT +
                    parentNodeNumber);

                String label = (String) primEdgeLabelHash.get(id);

                if (label != null) {
                    currentSentence.getNode(childNodeNumber)
                                   .setIncomingEdgeLabel(label);
                    header.setEdgesLabeled();
                }

                ((NT_Node) currentSentence.getNonterminalAt(parentNodeNumber)).addChild(childNodeNumber);
            }

            e = secEdgeHash.keys();

            while (e.hasMoreElements()) {
                String id = (String) e.nextElement();
                String startNodeId = (String) secEdgeHash.get(id);
                int startNodeNumber = currentSentence.getTerminalPositionOf(startNodeId);

                if (startNodeNumber == -1) {
                    startNodeNumber = currentSentence.getNonterminalPositionOf(startNodeId) +
                        Constants.CUT;
                }

                int endNodeNumber = currentSentence.getTerminalPositionOf(id);

                if (endNodeNumber == -1) {
                    endNodeNumber = currentSentence.getNonterminalPositionOf(id) +
                        Constants.CUT;
                }

                String label = (String) secEdgeLabelHash.get(id);

                if (label == null) {
                    label = "";
                }

                currentSentence.addCoreference(startNodeNumber, label,
                    endNodeNumber);
            }

            sentenceVector.add(currentSentence);
        }

        // CGN *****************************************************************************************
        if (tiger) {
            if (elementName.equals("graph")) {
                currentSentence.setRoot((root == null) ? (-1)
                                                       : currentSentence.getNonterminalPositionOf(
                        root));
            }

            if (elementName.equals("name")) {
                header.setCorpus_Name(currentContent);
            } else if (elementName.equals("author")) {
                header.setCorpus_Author(currentContent);
            } else if (elementName.equals("date")) {
                header.setCorpus_Date(currentContent);
            } else if (elementName.equals("description")) {
                header.setCorpus_Description(currentContent);
            } else if (elementName.equals("format")) {
                header.setCorpus_Format(currentContent);
            } else if (elementName.equals("history")) {
                header.setCorpus_History(currentContent);
            }
        } // end TIGER

        // CGN *****************************************************************************************
        else {
            //get rid of quotes in xml content
            if ((currentContent.startsWith("\"")) &&
                    (currentContent.endsWith("\""))) {
                currentContent = currentContent.substring(1,
                        currentContent.length() - 1);
            }

            if (elementName.equals("word")) {
                currentNode.setFeature("word", currentContent);
            } else if (elementName.equals("morph")) {
                currentNode.setFeature("morph", currentContent);
            }

            currentContent = "";
        }
         // end CGN
    }

    /**
     * DOCUMENT ME!
     *
     * @param param1
     *
     * @exception SAXException
     */
    public void endPrefixMapping(String param1) throws SAXException {
    }

    /**
     * DOCUMENT ME!
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
    }

    /**
     * DOCUMENT ME!
     *
     * @param param1
     *
     * @exception SAXException
     */
    public void skippedEntity(String param1) throws SAXException {
    }

    /**
     * DOCUMENT ME!
     *
     * @exception SAXException
     */
    public void startDocument() throws SAXException {
        logger.debug("Start parsing syntax file in tiger-xml-format.");
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
        if (elementName.equals("corpus") || elementName.equals("subcorpus")) {
            tiger = true;
        } else if (elementName.equals("syn")) {
            tiger = false;
        } else if ((tiger && (elementName.equals("s"))) ||
                ((!tiger) && (elementName.equals("sentence")))) {
            currentSentence = new Sentence();
            tNodeNumber += tNodeNumberInSentence;
            ntNodeNumber += ntNodeNumberInSentence;
            tNodeNumberInSentence = 0;
            ntNodeNumberInSentence = 0;
            primEdgeHash.clear();
            primEdgeLabelHash.clear();
            secEdgeHash.clear();
            secEdgeLabelHash.clear();
            currentSentence.setSentenceID(tiger ? atts.getValue("id")
                                                : atts.getValue("ref"));
        } else if (tiger) {
            //TIGER ***********************************************************************************
            if (elementName.equals("t")) {
                currentNode = new T_Node();

                for (int i = 0; i < atts.getLength(); i++) {
                    if ("id".equals(atts.getLocalName(i))) {
                        currentNode.setID(atts.getValue(i));

                        continue;
                    }

                    currentNode.setFeature(atts.getLocalName(i),
                        atts.getValue(i));

                    if (!header.isFeature(atts.getLocalName(i))) {
                        header.addTerminalFeature(new Feature(atts.getLocalName(
                                    i)));
                        logger.info("Set terminal feature '" +
                            atts.getLocalName(i) + "'.");
                    }
                }

                currentSentence.addTerminal((T_Node) currentNode);
                tNodeNumberInSentence++;
            } else if (elementName.equals("nt")) {
                currentNode = new NT_Node();

                for (int i = 0; i < atts.getLength(); i++) {
                    if ("id".equals(atts.getLocalName(i))) {
                        currentNode.setID(atts.getValue(i));

                        continue;
                    }

                    currentNode.setFeature(atts.getLocalName(i),
                        atts.getValue(i));

                    if (!header.isFeature(atts.getLocalName(i))) {
                        header.addNonterminalFeature(new Feature(
                                atts.getLocalName(i)));
                        logger.info("Set non-terminal feature '" +
                            atts.getLocalName(i) + "'.");
                    }
                }

                currentSentence.addNonterminal((NT_Node) currentNode);
                ntNodeNumberInSentence++;
            } else if (elementName.equals("edge")) {
                String refString = atts.getValue("idref");
                primEdgeHash.put(refString, currentNode.getID());
                primEdgeLabelHash.put(refString, atts.getValue("label"));
                edgeNumber++;
            } else if (elementName.equals("secedge")) {
                String refString = atts.getValue("idref");
                secEdgeHash.put(refString, currentNode.getID());
                secEdgeLabelHash.put(refString, atts.getValue("label"));
            } else if (elementName.equals("graph")) {
                root = atts.getValue("root");
            }
        } // end TIGER

        // CGN ***************************************************************************************
        else {
            if (elementName.equals("vertex")) {
                String idString = atts.getValue("ref");
                idString = idString.substring(idString.lastIndexOf(".") + 1);

                int idNumber = Integer.parseInt(idString);

                if (idNumber < 500) {
                    currentNode = new T_Node();
                    currentNode.setFeature("pos", atts.getValue("tag"));
                    currentSentence.addTerminal((T_Node) currentNode);
                    tNodeNumber++;
                } else {
                    currentNode = new NT_Node();
                    currentNode.setFeature("cat", atts.getValue("tag"));
                    currentSentence.addNonterminal((NT_Node) currentNode);
                    ntNodeNumber++;
                }

                currentNode.setID(idString);
            } else if (elementName.equals("edge")) {
                String refString = atts.getValue("ref");
                refString = refString.substring(refString.lastIndexOf(".") + 1);

                if (!primEdgeHash.containsKey(currentNode.getID())) {
                    primEdgeHash.put(currentNode.getID(), refString);
                    primEdgeLabelHash.put(currentNode.getID(),
                        atts.getValue("label"));
                    edgeNumber++;
                } else {
                    secEdgeHash.put(currentNode.getID(), refString);
                    secEdgeLabelHash.put(currentNode.getID(),
                        atts.getValue("label"));
                }
            }
        }
         // end CGN
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
