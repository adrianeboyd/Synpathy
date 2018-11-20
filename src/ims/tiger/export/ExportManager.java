/*
 * File:     ExportManager.java
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

import ims.tiger.corpus.*;

import ims.tiger.gui.shared.progress.ProgressContainerInterface;

import ims.tiger.query.api.MatchResult;
import ims.tiger.query.api.QueryIndexException;

import ims.tiger.query.internalapi.InternalCorpusQueryManager;

import org.apache.log4j.Logger;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;

import org.jdom.output.XMLOutputter;

import java.io.*;

import java.util.HashMap;
import java.util.List;


/** Diese zentrale Klasse steuert den XML-/XSLT-Export. */
public class ExportManager {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(ExportManager.class);
    private static String IDSIGN = ""; // vormals: s
    private static String VARSIGN = "#";
    private static String SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";

    /** Holds value of property DOCUMENT ME! */
    public static String ENCODING = "UTF-8";
    private boolean exportHeader;
    private boolean exportStructure;
    private boolean exportMatch;
    private int referSchema;
    private InternalCorpusQueryManager manager;
    private Header header;
    private List t_features;
    private List nt_features;
    private String install_dir;
    private ProgressContainerInterface container;

    /**
     * Creates a new ExportManager instance
     *
     * @param manager DOCUMENT ME!
     * @param install_dir DOCUMENT ME!
     */
    public ExportManager(InternalCorpusQueryManager manager, String install_dir) {
        this.manager = manager;
        this.install_dir = install_dir;

        exportHeader = true;
        exportStructure = true;
        exportMatch = true;

        header = manager.getHeader();
        t_features = header.getAllTFeatureNames();
        nt_features = header.getAllNTFeatureNames();
    }

    /** Stellt die Verbindung zum umgebenden Fenster her. */
    public void setContainer(ProgressContainerInterface container) {
        this.container = container;
    }

    /* ======================================================== */
    /* Export als XML-Datei                                     */
    /* ======================================================== */
    public void setConfiguration(boolean exportHeader, boolean exportStructure,
        boolean exportMatch, boolean refineSchema, int referSchema) {
        // Parameter TIGER-XML
        this.exportHeader = exportHeader;
        this.exportStructure = exportStructure;
        this.exportMatch = exportMatch;

        // this.refineSchema = refineSchema; // zur Zeit noch nicht benutzt
        this.referSchema = referSchema;
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param file DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void saveMatchAsXML(MatchResult result, File file)
        throws ExportException, ExportStopException {
        saveMatchAsXML(result, file, null, false, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param file DOCUMENT ME!
     * @param include_nonmatches DOCUMENT ME!
     * @param include_matches DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void saveMatchAsXML(MatchResult result, File file,
        boolean include_nonmatches, boolean include_matches)
        throws ExportException, ExportStopException {
        saveMatchAsXML(result, file, null, include_nonmatches, include_matches);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param file DOCUMENT ME!
     * @param matchlist DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void saveMatchAsXML(MatchResult result, File file, int[] matchlist)
        throws ExportException, ExportStopException {
        for (int i = 0; i < matchlist.length; i++) {
            matchlist[i]--; // 1 -> 0 usw.

            if ((matchlist[i] < 0) || (matchlist[i] >= result.size())) {
                throw new ExportException(
                    "Match corpus graph number out of range.");
            }
        }

        saveMatchAsXML(result, file, matchlist, false, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param file DOCUMENT ME!
     * @param current_match DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void saveMatchAsXML(MatchResult result, File file, int current_match)
        throws ExportException, ExportStopException {
        int[] matchlist = new int[1];
        matchlist[0] = current_match;
        saveMatchAsXML(result, file, matchlist);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param file DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param to DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void saveMatchAsXML(MatchResult result, File file, int from, int to)
        throws ExportException, ExportStopException {
        // Konsistenz-Check
        if ((to < from) || (from < 1) || (to > result.size())) {
            throw new ExportException("Invalid range selected.");
        }

        // Erzeugen der Liste
        int dim = to - from + 1;
        int[] matchlist = new int[dim];

        for (int i = 0; i < dim; i++) {
            matchlist[i] = (i + from) - 1; // 1 -> 0 usw.
        }

        saveMatchAsXML(result, file, matchlist, false, true);
    }

    private void saveMatchAsXML(MatchResult match, File file, int[] matchlist,
        boolean include_nonmatches, boolean include_matches)
        throws ExportException, ExportStopException {
        try {
            if (logger.isInfoEnabled() == true) {
                logger.info("Export has been started.");
            }

            int sentence_no;
            int match_no;
            String sentence_id;
            Sentence sentence;
            org.jdom.Document doc;

            XMLOutputter out = new XMLOutputter("  ", true);
            out.setEncoding(ENCODING);
            out.setOmitDeclaration(true);

            /* 1. XML-Header ? */
            DataOutputStream f = new DataOutputStream(new FileOutputStream(file));
            f.writeBytes("<?xml version=\"1.0\" encoding=\"" + ENCODING +
                "\" standalone=\"");

            if (referSchema == 0) { // kein Schema
                f.writeBytes("yes\"?>\n\n");
            } else {
                f.writeBytes("no\"?>\n\n");
            }

            String id = header.getCorpus_ID();

            if ((id == null) || (id.length() == 0)) {
                id = "corpus_id";
            }

            if (referSchema == 0) { // kein Schema
                f.writeBytes("<corpus id=\"");
                f.writeBytes(id);
                f.writeBytes("\">\n\n");
            } else { // Schema

                String schema_location = ims.tiger.system.Constants.PublicTigerXMLSchema;

                if (referSchema == 1) {
                    String help = install_dir + File.separator + "schema" +
                        File.separator +
                        ims.tiger.system.Constants.TigerXMLSchema;
                    schema_location = ims.tiger.util.UtilitiesCollection.createURI(help);
                }

                f.writeBytes("<corpus xmlns:xsi=\"" + SCHEMA_INSTANCE + "\"\n");
                f.writeBytes("        " + "xsi:noNamespaceSchemaLocation=\"" +
                    schema_location + "\"\n");
                f.writeBytes("        id=\"" + id + "\">\n\n");
            }

            /* 2. Korpus-Header */
            if (exportHeader) {
                if (logger.isInfoEnabled() == true) {
                    logger.info("Exporting header.");
                }

                if (container != null) {
                    container.setMessage("Exporting header");
                }

                doc = generateHeader(header);
                out.output(doc, f);
                f.writeBytes("\n");
            }

            if (container != null) {
                if (container.isAborted()) {
                    throw new ExportStopException("Export stopped");
                }
            }

            /* 3. Korpus-Body */
            if (logger.isInfoEnabled() == true) {
                logger.info("Exporting body.");
            }

            f.writeBytes("<body>\n\n");

            if (matchlist != null) { // Fall a) Matchlist ist bereits angegeben

                int n = matchlist.length;

                for (int i = 0; i < n; i++) {
                    match_no = matchlist[i];
                    sentence_no = match.getSentenceNumberAt(match_no);
                    sentence = manager.getOriginalSentence(sentence_no);
                    doc = generateXMLSentence(sentence, match_no, match, false);

                    if (doc != null) {
                        out.output(doc, f);
                    }

                    if (container != null) {
                        if (container.isAborted()) {
                            throw new ExportStopException("Export stopped");
                        }

                        container.setMessage("Exporting corpus graph " +
                            sentence.getSentenceID());
                        container.setProgressValue((i * 100) / n);
                    }
                }
            } else { // Fall b) Matchlist nicht angegeben

                if (!include_nonmatches) { // b1) nur die Matches

                    int n = match.size();

                    for (int i = 0; i < n; i++) {
                        match_no = i;
                        sentence_no = match.getSentenceNumberAt(match_no);
                        sentence = manager.getOriginalSentence(sentence_no);
                        doc = generateXMLSentence(sentence, match_no, match,
                                false);

                        if (doc != null) {
                            out.output(doc, f);
                        }

                        if (container != null) {
                            if (container.isAborted()) {
                                throw new ExportStopException("Export stopped");
                            }

                            container.setMessage("Exporting corpus graph " +
                                sentence.getSentenceID());
                            container.setProgressValue((i * 100) / n);
                        }
                    }
                } else { // b2) komplettes Korpus ODER Nicht-Matches

                    int n = header.getNumberOfSentences();
                    int match_count = 0;

                    for (int i = 0; i < n; i++) {
                        sentence_no = i;
                        sentence = manager.getOriginalSentence(sentence_no);
                        match_no = -1;

                        if (include_matches && (match == null)) { // KORPUS
                            doc = generateXMLSentence(sentence, match_no,
                                    match, false);

                            if (doc != null) {
                                out.output(doc, f);
                            }
                        } else if (match.isMatchingSentence(sentence_no)) { //MATCHT

                            if (include_matches) {
                                match_no = match_count++;
                                doc = generateXMLSentence(sentence, match_no,
                                        match, false);

                                if (doc != null) {
                                    out.output(doc, f);
                                }
                            }
                        } else { // MATCHT NICHT
                            doc = generateXMLSentence(sentence, match_no,
                                    match, false);

                            if (doc != null) {
                                out.output(doc, f);
                            }
                        }

                        if (container != null) {
                            if (container.isAborted()) {
                                throw new ExportStopException("Export stopped");
                            }

                            container.setMessage("Exporting corpus graph " +
                                sentence.getSentenceID());
                            container.setProgressValue((i * 100) / n);
                        }
                    }
                }
            }

            /* 4. XML-Ende ? */
            f.writeBytes("</body>\n\n");
            f.writeBytes("</corpus>\n");
            f.close();
        } catch (ExportStopException e) {
            throw e;
        } catch (Exception e) {
            throw new ExportException(e.getMessage());
        }
    }

    /* ======================================================== */
    /* Export durch Pipen ueber ein XSLT-Stylesheet             */
    /* ======================================================== */
    public void pipeMatchIntoStylesheets(MatchResult result, File header_xslt,
        File sentence_xslt, HashMap parameter, File target_file)
        throws ExportException, ExportStopException {
        pipeMatchIntoStylesheets(result, null, false, true, header_xslt,
            sentence_xslt, parameter, target_file);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param include_nonmatches DOCUMENT ME!
     * @param include_matches DOCUMENT ME!
     * @param header_xslt DOCUMENT ME!
     * @param sentence_xslt DOCUMENT ME!
     * @param parameter DOCUMENT ME!
     * @param target_file DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void pipeMatchIntoStylesheets(MatchResult result,
        boolean include_nonmatches, boolean include_matches, File header_xslt,
        File sentence_xslt, HashMap parameter, File target_file)
        throws ExportException, ExportStopException {
        pipeMatchIntoStylesheets(result, null, include_nonmatches,
            include_matches, header_xslt, sentence_xslt, parameter, target_file);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param matchlist DOCUMENT ME!
     * @param header_xslt DOCUMENT ME!
     * @param sentence_xslt DOCUMENT ME!
     * @param parameter DOCUMENT ME!
     * @param target_file DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void pipeMatchIntoStylesheets(MatchResult result, int[] matchlist,
        File header_xslt, File sentence_xslt, HashMap parameter,
        File target_file) throws ExportException, ExportStopException {
        for (int i = 0; i < matchlist.length; i++) {
            matchlist[i]--; // 1 -> 0 usw.

            if ((matchlist[i] < 0) || (matchlist[i] >= result.size())) {
                throw new ExportException(
                    "Match corpus graph number out of range.");
            }
        }

        pipeMatchIntoStylesheets(result, matchlist, false, true, header_xslt,
            sentence_xslt, parameter, target_file);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param current_match DOCUMENT ME!
     * @param header_xslt DOCUMENT ME!
     * @param sentence_xslt DOCUMENT ME!
     * @param parameter DOCUMENT ME!
     * @param target_file DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void pipeMatchIntoStylesheets(MatchResult result, int current_match,
        File header_xslt, File sentence_xslt, HashMap parameter,
        File target_file) throws ExportException, ExportStopException {
        int[] matchlist = new int[1];
        matchlist[0] = current_match;
        pipeMatchIntoStylesheets(result, matchlist, header_xslt, sentence_xslt,
            parameter, target_file);
    }

    /**
     * DOCUMENT ME!
     *
     * @param result DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param to DOCUMENT ME!
     * @param header_xslt DOCUMENT ME!
     * @param sentence_xslt DOCUMENT ME!
     * @param parameter DOCUMENT ME!
     * @param target_file DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void pipeMatchIntoStylesheets(MatchResult result, int from, int to,
        File header_xslt, File sentence_xslt, HashMap parameter,
        File target_file) throws ExportException, ExportStopException {
        // Konsistenz-Check
        if ((to < from) || (from < 1) || (to > result.size())) {
            throw new ExportException("Invalid range selected.");
        }

        // Erzeugen der Liste
        int dim = to - from + 1;
        int[] matchlist = new int[dim];

        for (int i = 0; i < dim; i++) {
            matchlist[i] = (i + from) - 1; // 1 -> 0 usw.
        }

        pipeMatchIntoStylesheets(result, matchlist, false, true, header_xslt,
            sentence_xslt, parameter, target_file);
    }

    /**
     * DOCUMENT ME!
     *
     * @param match DOCUMENT ME!
     * @param matchlist DOCUMENT ME!
     * @param include_nonmatches DOCUMENT ME!
     * @param include_matches DOCUMENT ME!
     * @param header_xslt DOCUMENT ME!
     * @param sentence_xslt DOCUMENT ME!
     * @param parameter DOCUMENT ME!
     * @param target_file DOCUMENT ME!
     *
     * @throws ExportException DOCUMENT ME!
     * @throws ExportStopException DOCUMENT ME!
     */
    public void pipeMatchIntoStylesheets(MatchResult match, int[] matchlist,
        boolean include_nonmatches, boolean include_matches, File header_xslt,
        File sentence_xslt, HashMap parameter, File target_file)
        throws ExportException, ExportStopException {
        try {
            int sentence_no;
            int match_no;
            String sentence_id;
            Sentence sentence;
            org.jdom.Document doc;

            /* 1. Vorbereitungen */
            if (logger.isInfoEnabled() == true) {
                logger.info("Starting XSLT export");
            }

            if (container != null) {
                container.setMessage("Preparing the export");
            }

            // Stylesheet-Prozessor(en)
            BufferedWriter f = new BufferedWriter(new FileWriter(target_file));
            XSLTProcessor sentence_processor = new XSLTProcessor(sentence_xslt);

            boolean head = (header_xslt != null);
            XSLTProcessor header_processor = null;

            if (head) {
                header_processor = new XSLTProcessor(header_xslt);
            }

            // Einstellungen fuer die XML-Satz-Generatoren
            exportStructure = true;
            exportMatch = true;

            /* 2. Korpus-Header */
            if (head) {
                if (logger.isInfoEnabled() == true) {
                    logger.info("Exporting header");
                }

                if (container != null) {
                    container.setMessage("Exporting header");
                }

                doc = generateHeader(header);

                XMLOutputter out = new XMLOutputter(); // Dokument in String umlenken
                out.setEncoding(ENCODING);

                StringWriter s_out = new StringWriter();
                out.output(doc, s_out);

                StringReader s_in = new StringReader(s_out.toString());
                header_processor.process(s_in, f, parameter); // Sentence-Stylesheet !!!!
            }

            /* 3. Korpus-Body */
            if (logger.isInfoEnabled() == true) {
                logger.info("Exporting body");
            }

            if (matchlist != null) { // Fall a) Matchlist ist bereits angegeben

                int n = matchlist.length;

                for (int i = 0; i < n; i++) {
                    match_no = matchlist[i];
                    sentence_no = match.getSentenceNumberAt(match_no);
                    sentence = manager.getOriginalSentence(sentence_no);
                    doc = generateXMLSentence(sentence, match_no, match, true);

                    if (doc != null) {
                        doc.setDocType(new DocType("corpus")); // Dokument erzeugen

                        XMLOutputter out = new XMLOutputter(); // Dokument in String umlenken
                        out.setEncoding(ENCODING);

                        StringWriter s_out = new StringWriter();
                        out.output(doc, s_out);

                        StringReader s_in = new StringReader(s_out.toString());
                        sentence_processor.process(s_in, f, parameter); // Sentence-Stylesheet !!!!

                        if (container != null) {
                            if (container.isAborted()) {
                                throw new ExportStopException("Export stopped");
                            }

                            container.setMessage("Exporting corpus graph " +
                                sentence.getSentenceID());
                            container.setProgressValue((i * 100) / n);
                        }
                    }
                }
            } else { // Fall b) Matchlist nicht angegeben

                if (!include_nonmatches) { // b1) nur die Matches

                    int n = match.size();

                    for (int i = 0; i < match.size(); i++) {
                        match_no = i;
                        sentence_no = match.getSentenceNumberAt(match_no);
                        sentence = manager.getOriginalSentence(sentence_no);
                        doc = generateXMLSentence(sentence, match_no, match,
                                true);

                        if (doc != null) {
                            doc.setDocType(new DocType("corpus")); // Dokument erzeugen

                            XMLOutputter out = new XMLOutputter(); // Dokument in String umlenken
                            out.setEncoding(ENCODING);

                            StringWriter s_out = new StringWriter();
                            out.output(doc, s_out);

                            StringReader s_in = new StringReader(s_out.toString());
                            sentence_processor.process(s_in, f, parameter); // Sentence-Stylesheet !!!!

                            if (container != null) {
                                if (container.isAborted()) {
                                    throw new ExportStopException(
                                        "Export stopped");
                                }

                                container.setMessage("Exporting corpus graph " +
                                    sentence.getSentenceID());
                                container.setProgressValue((i * 100) / n);
                            }
                        }
                    }
                } // b1
                else { // b2) komplettes Korpus ODER Nicht-Matches

                    int n = header.getNumberOfSentences();
                    int match_count = 0;

                    for (int i = 0; i < n; i++) {
                        sentence_no = i;
                        sentence = manager.getOriginalSentence(sentence_no);
                        match_no = -1;
                        doc = null;

                        if (include_matches && (match == null)) { // KORPUS
                            doc = generateXMLSentence(sentence, match_no,
                                    match, false);
                        } else if (match.isMatchingSentence(sentence_no)) { // MATCHT

                            if (include_matches) {
                                match_no = match_count++;
                                doc = generateXMLSentence(sentence, match_no,
                                        match, true);
                            }
                        } else { // MATCHT NICHT
                            doc = generateXMLSentence(sentence, match_no,
                                    match, true);
                        }

                        if (doc != null) {
                            doc.setDocType(new DocType("corpus")); // Dokument erzeugen

                            XMLOutputter out = new XMLOutputter(); // Dokument in String umlenken
                            out.setEncoding(ENCODING);

                            StringWriter s_out = new StringWriter();
                            out.output(doc, s_out);

                            StringReader s_in = new StringReader(s_out.toString());
                            sentence_processor.process(s_in, f, parameter); // Sentence-Stylesheet !!!!

                            if (container != null) {
                                if (container.isAborted()) {
                                    throw new ExportStopException(
                                        "Export stopped");
                                }

                                container.setMessage("Exporting corpus graph " +
                                    sentence.getSentenceID());
                                container.setProgressValue((i * 100) / n);
                            }
                        }
                    }
                }
                 // b2
            }
             // b

            f.close();
        } catch (ExportStopException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Unerwarteter Fehler", e);
            throw new ExportException(e.getMessage());
        }
    }

    /* ======================================================== */
    /* XML-Generatoren fuer den Header / die Saetze             */
    /* ======================================================== */
    private org.jdom.Document generateXMLSentence(Sentence sentence,
        int match_no, MatchResult match, boolean corpus_element)
        throws QueryIndexException {
        /* 0. ===== Vorbereitungen ===== */

        // Kanten nach linker Ecke anordnen (bei Baeumen natuerliche Ordnung)
        sentence.orderSentenceByPrecedence();

        // Wurzelelement
        Element s = new Element("s");
        String sentence_id = sentence.getSentenceID();
        s.setAttribute("id", IDSIGN + sentence_id);

        org.jdom.Document doc;

        if (corpus_element) {
            Element corpus = new Element("corpus");
            Element body = new Element("body");
            corpus.addContent(body);
            body.addContent(s);
            doc = new org.jdom.Document(corpus);
        } else {
            doc = new org.jdom.Document(s);
        }

        /* ===== 1. Satzstruktur ===== */
        if (exportStructure) {
            // <graph>
            Element struct = new Element("graph");
            s.addContent(struct);
            
            // the root node is not properly tracked during editing,
            // so this makes sure that the root node is included
            // in the export only when there is exactly one root node
            // in the tree
            List<Node> nts = sentence.getNonterminals();
            int rootCount = 0;
            String rootNodeId = "";
            for (Node n : nts) {
            	if (n.getParent() == -1) {
            		rootCount++;
            		rootNodeId = n.getID();
            	}
            }

            if (rootCount == 1) {
            	struct.setAttribute("root", IDSIGN + rootNodeId);
            }

            if (sentence.crossingEdges()) {
                struct.setAttribute("discontinuous", "true");
            }

            //   <terminals>
            Element token = new Element("terminals");
            struct.addContent(token);

            //     <t>
            List terminals = sentence.getTerminals();
            String fname;
            String fvalue;
            T_Node tnode;

            for (int i = 0; i < terminals.size(); i++) {
                tnode = (T_Node) terminals.get(i);

                Element w = new Element("t");
                token.addContent(w);
                w.setAttribute("id", IDSIGN + tnode.getID());

                // Features
                for (int j = 0; j < t_features.size(); j++) {
                    fname = (String) t_features.get(j);
                    fvalue = tnode.getFeature(fname);

                    if (fvalue != null) {
                        w.setAttribute(fname, fvalue);
                    }
                }

                //     <edge type="secondary">
                if (header.secondaryEdges()) { // Sekundaere Kanten

                    for (int j = 0; j < sentence.getCoreferenceSize(); j++) {
                        if (sentence.getCoreferenceNode1(j) == i) {
                            Element edge = new Element("secedge");
                            w.addContent(edge);

                            Node son = sentence.getNode(sentence.getCoreferenceNode2(
                                        j));
                            String label = sentence.getCoreferenceLabel(j);
                            edge.setAttribute("label", label);
                            edge.setAttribute("idref", IDSIGN + son.getID());
                        }
                    }
                }
            }

            //   <nonterminals>
            Element inner = new Element("nonterminals");
            struct.addContent(inner);

            //     <nt>
            List nonterminals = sentence.getNonterminals();
            NT_Node ntnode;

            for (int i = 0; i < nonterminals.size(); i++) {
                ntnode = (NT_Node) nonterminals.get(i);

                Element n = new Element("nt");
                inner.addContent(n);
                n.setAttribute("id", IDSIGN + ntnode.getID());

                for (int j = 0; j < nt_features.size(); j++) { // Features
                    fname = (String) nt_features.get(j);
                    fvalue = ntnode.getFeature(fname);

                    if (fvalue != null) {
                        n.setAttribute(fname, fvalue);
                    }
                }

                //     <edge>
                List children = ntnode.getChilds(); // Kanten

                for (int j = 0; j < children.size(); j++) {
                    Element edge = new Element("edge");
                    n.addContent(edge);

                    Node son = sentence.getNode(((Integer) children.get(j)).intValue());

                    if (header.edgesLabeled()) { // Kantenlabel?

                        String label = son.getIncomingEdgeLabel();
                        edge.setAttribute("label", label);
                    }

                    edge.setAttribute("idref", IDSIGN + son.getID());
                }

                //     <edge type="secondary">
                if (header.secondaryEdges()) { // Sekundaere Kanten

                    for (int j = 0; j < sentence.getCoreferenceSize(); j++) {
                        if (sentence.getCoreferenceNode1(j) == (i +
                                ims.tiger.system.Constants.CUT)) {
                            Element edge = new Element("secedge");
                            n.addContent(edge);

                            Node son = sentence.getNode(sentence.getCoreferenceNode2(
                                        j));
                            String label = sentence.getCoreferenceLabel(j);
                            edge.setAttribute("label", label);
                            edge.setAttribute("idref", IDSIGN + son.getID());
                        }
                    }
                }
            }
        }
         // Satzstruktur

        /* ===== 2. Matches ===== */
        if ((exportMatch) && (match_no > -1)) {
            Element matches = new Element("matches");
            s.addContent(matches);

            int sentno = match.getSentenceNumberAt(match_no);
            match.orderSentenceSubmatches(sentno); // doppelte entfernen usw.

            int n = match.getSentenceSubmatchSize(sentno);

            for (int i = 0; i < n; i++) { // Durchwandere Matches des Satzes

                Element submatch = new Element("match");
                matches.addContent(submatch);

                int[] var_values = match.getSentenceSubmatchAt(sentno, i);
                int subgraph = manager.subgraph(sentno, var_values);
                Node subnode = sentence.getNode(subgraph);
                submatch.setAttribute("subgraph", IDSIGN + subnode.getID());

                int value;

                for (int j = 0; j < var_values.length; j++) { // Variablen
                    value = var_values[j];

                    if (value >= 0) {
                        Element variable = new Element("variable");
                        submatch.addContent(variable);
                        variable.setAttribute("name",
                            VARSIGN + match.getVariableName(j));

                        Node referred = sentence.getNode(value);
                        variable.setAttribute("idref", IDSIGN +
                            referred.getID());
                    }
                }
            }
        }

        return doc;
    }

    private static Element generateFeature(Header header, String fname) {
        Feature f = header.getFeature(fname);

        Element feature = new Element("feature");
        feature.setAttribute("name", fname);

        if (header.isNonterminalFeature(fname)) {
            feature.setAttribute("domain", "NT");
        } else if (header.isTerminalFeature(fname)) {
            feature.setAttribute("domain", "T");
        } else {
            feature.setAttribute("domain", "FREC");
        }

        // Feature-Werte
        if (f.isListed()) {
            List fvalues = f.getItems();
            List fdesc = f.getDescriptions();
            String fv;
            String fd;

            for (int j = 0; j < fvalues.size(); j++) {
                fv = (String) fvalues.get(j);

                Element value = new Element("value");
                feature.addContent(value);
                value.setAttribute("name", fv); // Name

                if ((fdesc != null) && (fdesc.size() > j)) { // evtl. Beschreibung
                    fd = (String) fdesc.get(j);

                    if (fd.length() > 0) {
                        value.addContent(fd);
                    }
                }
            }
        }

        return feature;
    }

    private static org.jdom.Document generateHeader(Header header) {
        /* 0. ===== Vorbereitungen ===== */
        Element head = new Element("head");
        org.jdom.Document doc = new org.jdom.Document(head);

        /* 1. ===== Meta-Information ===== */
        Element meta = new Element("meta");
        head.addContent(meta);

        String inf;
        inf = header.getCorpus_Name();

        if ((inf != null) && (inf.length() > 0)) {
            Element infel = new Element("name");
            meta.addContent(infel);
            infel.addContent(inf);
        }

        inf = header.getCorpus_Author();

        if ((inf != null) && (inf.length() > 0)) {
            Element infel = new Element("author");
            meta.addContent(infel);
            infel.addContent(inf);
        }

        inf = header.getCorpus_Date();

        if ((inf != null) && (inf.length() > 0)) {
            Element infel = new Element("date");
            meta.addContent(infel);
            infel.addContent(inf);
        }

        inf = header.getCorpus_Description();

        if ((inf != null) && (inf.length() > 0)) {
            Element infel = new Element("description");
            meta.addContent(infel);
            infel.addContent(inf);
        }

        inf = header.getCorpus_Format();

        if ((inf != null) && (inf.length() > 0)) {
            Element infel = new Element("format");
            meta.addContent(infel);
            infel.addContent(inf);
        }

        inf = header.getCorpus_History();

        if ((inf != null) && (inf.length() > 0)) {
            Element infel = new Element("history");
            meta.addContent(infel);
            infel.addContent(inf);
        }

        /* 2. ===== Annotations-Information ===== */
        Element annotation = new Element("annotation");
        head.addContent(annotation);

        /* a) Features */

        // T
        List fnames_t = header.getAllTFeatureNames();
        String fname;

        for (int i = 0; i < fnames_t.size(); i++) {
            fname = (String) fnames_t.get(i);

            if (!header.isGeneralFeature(fname)) {
                annotation.addContent(generateFeature(header, fname));
            }
        }

        // NT
        List fnames_nt = header.getAllNTFeatureNames();

        for (int i = 0; i < fnames_nt.size(); i++) {
            fname = (String) fnames_nt.get(i);

            if (!header.isGeneralFeature(fname)) {
                annotation.addContent(generateFeature(header, fname));
            }
        }

        // FREC
        List fnames_frec = header.getAllGeneralFeatures();

        for (int i = 0; i < fnames_frec.size(); i++) {
            fname = (String) fnames_frec.get(i);
            annotation.addContent(generateFeature(header, fname));
        }

        /* b) Kantenlabel ? */
        if (header.edgesLabeled()) {
            Element edgelabel = new Element("edgelabel");
            annotation.addContent(edgelabel);

            Feature f = header.getEdgeFeature();

            if (f.isListed()) {
                List fvalues = f.getItems();
                List fdesc = f.getDescriptions();
                String fv;
                String fd;

                for (int j = 0; j < fvalues.size(); j++) {
                    fv = (String) fvalues.get(j);

                    Element value = new Element("value");
                    edgelabel.addContent(value);
                    value.setAttribute("name", fv); // Name

                    if ((fdesc != null) && (fdesc.size() > j)) { // evtl. Beschreibung
                        fd = (String) fdesc.get(j);

                        if (fd != null) {
                            value.addContent(fd);
                        }
                    }
                }
            }
        }

        /* c) Sekundaere Kanten ? */
        if (header.secondaryEdges()) {
            Element secedgelabel = new Element("secedgelabel");
            annotation.addContent(secedgelabel);

            Feature f = header.getSecEdgeFeature();

            if (f.isListed()) {
                List fvalues = f.getItems();
                List fdesc = f.getDescriptions();
                String fv;
                String fd;

                for (int j = 0; j < fvalues.size(); j++) {
                    fv = (String) fvalues.get(j);

                    Element value = new Element("value");
                    secedgelabel.addContent(value);
                    value.setAttribute("name", fv); // Name

                    if ((fdesc != null) && (fdesc.size() > j)) { // evtl. Beschreibung
                        fd = (String) fdesc.get(j);

                        if (fd != null) {
                            value.addContent(fd);
                        }
                    }
                }
            }
        }

        return doc;
    }

    /**
     * save only header
     * @param header
     * @param fileName
     * @throws IOException
     */
    public static void saveHeader(Header header, String fileName)
        throws IOException {
        Document doc = ExportManager.generateHeader(header);
        XMLOutputter out = new XMLOutputter();
        out.setEncoding(ExportManager.ENCODING);
        out.setIndent(true);
        out.setIndentSize(4);
        out.setNewlines(true);
        out.output(doc, new FileOutputStream(fileName));
    }
}
