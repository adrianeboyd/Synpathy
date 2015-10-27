/*
 * File:     Result2XML.java
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
package ims.tiger.gui.tigergraphviewer.forest;

import ims.tiger.corpus.NT_Node;
import ims.tiger.corpus.Node;
import ims.tiger.corpus.Sentence;
import ims.tiger.corpus.T_Node;

import ims.tiger.query.api.MatchResult;
import ims.tiger.query.api.QueryIndexException;

import ims.tiger.query.internalapi.InternalCorpusQueryManager;

import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;

import org.jdom.output.XMLOutputter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import java.util.ArrayList;
import java.util.Date;
import java.util.zip.GZIPOutputStream;


/**
 * $Id: Result2XML.java,v 1.3 2006/07/20 10:37:48 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.3 $
 */
public class Result2XML {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(Result2XML.class.getName());

    /**
      * DOCUMENT ME!
      *
      * @param sentence DOCUMENT ME!
      * @param node DOCUMENT ME!
      *
      * @return DOCUMENT ME!
      */
    public static ArrayList getTerminalDescendants(Sentence sentence,
        NT_Node node) {
        ArrayList descendants = new ArrayList();

        Object[] o = node.getChildsArray();

        for (int j = 0; j < o.length; j++) {
            Node child = sentence.getNode(((Integer) o[j]).intValue());

            if (child instanceof NT_Node) {
                descendants.addAll(getTerminalDescendants(sentence,
                        (NT_Node) child));
            } else {
                descendants.add(child);
            }
        }

        return descendants;
    }

    /**
     * DOCUMENT ME!
     *
     * @param fileName DOCUMENT ME!
     * @param manager DOCUMENT ME!
     * @param result DOCUMENT ME!
     */
    public static void exportMatchingTerminals(String fileName,
        InternalCorpusQueryManager manager, MatchResult result) {
        File outputFile = new File(fileName);

        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(
                        outputFile));
            Sentence sentence;
            String fragmentID;
            String sentenceID;

            out.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");
            out.write("<searchResults date=\"" +
                new Date(System.currentTimeMillis()).toString() +
                "\" originator=\"TIGERSearch\">\n");

            out.write("<description>");
            out.write("");
            out.write("</description>\n");

            int sentno = result.getSentenceNumberAt(0);
            sentenceID = manager.getSentenceID(sentno);
            fragmentID = sentenceID.substring(0, sentenceID.indexOf('.'));
            out.write("<file id=\"" + fragmentID + "\" url=\"" + "\">\n");

            for (int matchno = 0; matchno < result.size(); matchno++) {
                sentno = result.getSentenceNumberAt(matchno);
                sentence = manager.getSentence(sentno);
                sentenceID = sentence.getSentenceID();

                if (!sentenceID.startsWith(fragmentID + ".")) {
                    out.write("</file>\n");
                    fragmentID = sentenceID.substring(0, sentenceID.indexOf('.'));
                    out.write("<file id=\"" + fragmentID + "\" url=\"" +
                        "\">\n");
                }

                for (int submatchno = 0;
                        submatchno < result.getSentenceSubmatchSize(sentno);
                        submatchno++) {
                    out.write("\t<s id=\"" + sentenceID + "\">\n");

                    int[] match_nodes = result.getSentenceSubmatchAt(sentno,
                            submatchno);
                    out.write("\t\t<match tierName=\"ORT\">\n");

                    int nodeNr = manager.subgraph(sentno, match_nodes);

                    Node node = sentence.getNode(nodeNr);

                    ArrayList terminals;

                    if (node instanceof NT_Node) {
                        terminals = getTerminalDescendants(sentence,
                                (NT_Node) node);
                    } else {
                        terminals = new ArrayList();
                        terminals.add(node);
                    }

                    for (int t = 0; t < terminals.size(); t++) {
                        T_Node t_node = (T_Node) terminals.get(t);
                        out.write("\t\t\t<token idref=\"" + t_node.getID() +
                            "\" nr=\"" +
                            (sentence.getTerminals().indexOf(t_node) + 1) +
                            "\"/>\n");
                    }

                    out.write("\t\t</match>\n");
                    out.write("\t</s>\n");
                }
            }

            out.write("</file>\n");
            out.write("</searchResults>");
            out.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (QueryIndexException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param fileName DOCUMENT ME!
     * @param manager DOCUMENT ME!
     * @param result DOCUMENT ME!
     */
    public void exportMatchingTerminalsUsingDOM(String fileName,
        InternalCorpusQueryManager manager, MatchResult result) {
        Sentence sentence;
        String fragmentID;
        String sentenceID;
        Element root;
        Element description;
        Element phrase;
        Element matches;
        Element token;

        try {
            boolean compress = false;
            File f = new File(fileName);
            XMLOutputter out = new XMLOutputter("  ", true);
            DataOutputStream output;

            if (compress) {
                output = new DataOutputStream(new GZIPOutputStream(
                            new FileOutputStream(f)));
            } else {
                output = new DataOutputStream(new FileOutputStream(f));
            }

            //Namespace ns = Namespace.getNamespace("Blabla","http://www.mpi.nl");
            root = new Element("SearchResults");
            root.setAttribute("Date",
                new Date(System.currentTimeMillis()).toString());
            root.setAttribute("Originator", "TigerSearch");

            Document doc = new Document(root);
            description = new Element("Description");
            description.addContent("Description of query");
            root.addContent(description);

            Element file = new Element("File");

            int sentno = result.getSentenceNumberAt(0);
            sentenceID = manager.getSentenceID(sentno);
            fragmentID = sentenceID.substring(0, sentenceID.indexOf('.'));
            file.setAttribute("ID", fragmentID);

            //file.setAttribute("URL", "file:///home/klasal/workdir/src/fn000008");
            for (int matchno = 0; matchno < result.size(); matchno++) {
                sentno = result.getSentenceNumberAt(matchno);
                sentence = manager.getSentence(sentno);
                sentenceID = sentence.getSentenceID();

                if (!sentenceID.startsWith(fragmentID + ".")) {
                    fragmentID = sentenceID.substring(0, sentenceID.indexOf('.'));
                    root.addContent(file);
                    file = new Element("File");
                    file.setAttribute("ID", fragmentID);
                }

                phrase = new Element("s");

                phrase.setAttribute("ID", sentenceID);

                for (int submatchno = 0;
                        submatchno < result.getSentenceSubmatchSize(sentno);
                        submatchno++) {
                    int[] match_nodes = result.getSentenceSubmatchAt(sentno,
                            submatchno);
                    matches = new Element("Match");
                    matches.setAttribute("TierName", "ORT");
                    phrase.addContent(matches);

                    int nodeNr = manager.subgraph(sentno, match_nodes);

                    Node node = sentence.getNode(nodeNr);

                    ArrayList terminals = getTerminalDescendants(sentence,
                            (NT_Node) node);

                    for (int t = 0; t < terminals.size(); t++) {
                        token = new Element("Token");
                        token.setAttribute("Idref",
                            ((Node) terminals.get(t)).getID());
                        matches.addContent(token);
                    }
                }

                file.addContent(phrase);
            }

            root.addContent(file);

            out.output(doc, output);
        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (QueryIndexException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
