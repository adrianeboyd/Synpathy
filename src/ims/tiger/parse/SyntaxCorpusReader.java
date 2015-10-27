/*
 * File:     SyntaxCorpusReader.java
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.net.URL;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.zip.GZIPInputStream;


/**
 * This class reads an ASCII syntax file in Negra 3 or CGN format
 *
 * @author Alexander Klassmann
 * @version 1.1 28/11/02
 */
public class SyntaxCorpusReader {
    private static Logger logger = Logger.getLogger(SyntaxCorpusReader.class.getName());
    private Header header;
    private Vector sentenceVector;

    /**
     * DOCUMENT ME!
     *
     * @param header for storing the metadata (like features, corpus name, etc)
     * @param sentenceVector contains the Sentences of the corpus
     */
    public SyntaxCorpusReader(Header header, Vector sentenceVector) {
        this.header = header;
        this.sentenceVector = sentenceVector;
    }

    /**
     * Default function call is reading a file
     *
     * @param corpusURL file URL
     */
    public void readSyntaxCorpus(URL corpusURL) {
        readSyntaxCorpus(corpusURL, false);
    }

    /**
     * Reads the syntax file from a file or from the console
     *
     * @param corpusURL file URL
     * @param console if true read not from file, but from console
     */
    public void readSyntaxCorpus(URL corpusURL, boolean console) {
        StringTokenizer tokenizer;
        int tNodeNumber = 0;
        int ntNodeNumber = 0;
        int tNodeNumberInSentence = 0;
        int ntNodeNumberInSentence = 0;
        int edgeNumber = 0;
        boolean lemmaInFile = false;
        String line;
        Feature currentFeature = null;
        Sentence currentSentence = null;
        T_Node tNode = null;
        NT_Node ntNode = null;
        Hashtable primEdgeHash = new Hashtable();
        Hashtable secEdgeHash = new Hashtable();
        Hashtable secEdgeLabelHash = new Hashtable();

        // if no feature predefined, set the 'word' feature as obligatory first feature
        if (header.getAllTerminalFeatures().size() == 0) {
            header.addTerminalFeature(new Feature("word"));
            logger.info("Set terminal feature 'word'.");
        }

        try {
            boolean zipfile = corpusURL.getFile().toLowerCase().endsWith(".syn.gz");
            BufferedReader in = console
                ? (zipfile
                ? new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(System.in)))
                : new BufferedReader(new InputStreamReader(System.in)))
                : (zipfile
                ? new BufferedReader(new InputStreamReader(
                        new GZIPInputStream(corpusURL.openStream())))
                : new BufferedReader(new InputStreamReader(
                        corpusURL.openStream())));
            logger.debug("Start reading syntax file " +
                (console ? "from console." : ("`" + corpusURL + "' .")));

            while ((line = in.readLine()) != null) {
                if (line.indexOf('#') == 0) {
                    line = line.substring(1);

                    if (line.startsWith("BOS") || line.startsWith("EOS") ||
                            line.startsWith("BOT") || line.startsWith("EOT")) {
                        tokenizer = new StringTokenizer(line, " ");

                        String begin = tokenizer.nextToken();

                        if ("BOS".equals(begin)) {
                            currentSentence = new Sentence();
                            tNodeNumber += tNodeNumberInSentence;
                            ntNodeNumber += ntNodeNumberInSentence;
                            tNodeNumberInSentence = 0;
                            ntNodeNumberInSentence = 0;
                            primEdgeHash.clear();
                            secEdgeHash.clear();
                            secEdgeLabelHash.clear();
                            currentSentence.setSentenceID(tokenizer.nextToken());
                            sentenceVector.add(currentSentence);
                        } else if ("EOS".equals(begin)) {
                            Enumeration e = primEdgeHash.keys();

                            while (e.hasMoreElements()) {
                                Node node = (Node) e.nextElement();
                                int childNodeNumber = (node instanceof T_Node)
                                    ? currentSentence.getTerminals().indexOf((T_Node) node)
                                    : (currentSentence.getNonterminals()
                                                      .indexOf((NT_Node) node) +
                                    Constants.CUT);
                                int parentNodeNumber = currentSentence.getNonterminalPositionOf((String) primEdgeHash.get(
                                            node));
                                node.setParent(Constants.CUT +
                                    parentNodeNumber);
                                ((NT_Node) currentSentence.getNonterminalAt(parentNodeNumber)).addChild(childNodeNumber);
                            }

                            e = secEdgeHash.keys();

                            while (e.hasMoreElements()) {
                                Node node = (Node) e.nextElement();
                                String startNodeId = (String) secEdgeHash.get(node);
                                int startNodeNumber = (node instanceof T_Node)
                                    ? currentSentence.getTerminalPositionOf(startNodeId)
                                    : (currentSentence.getNonterminalPositionOf(startNodeId) +
                                    Constants.CUT);
                                int endNodeNumber = (node instanceof T_Node)
                                    ? currentSentence.getTerminals().indexOf((T_Node) node)
                                    : (currentSentence.getNonterminals()
                                                      .indexOf((NT_Node) node) +
                                    Constants.CUT);
                                currentSentence.addCoreference(startNodeNumber,
                                    (String) secEdgeLabelHash.get(node),
                                    endNodeNumber);
                            }

                            currentSentence = null;
                        } else if ("BOT".equals(begin)) {
                            String featureTag = tokenizer.nextToken();

                            if ("WORDTAG".equals(featureTag)) {
                                currentFeature = new Feature("pos");
                                header.addTerminalFeature(currentFeature);
                                logger.info("Set terminal Feature 'pos'.");
                            } else if ("MORPHTAG".equals(featureTag)) {
                                currentFeature = new Feature("morph");
                                header.addTerminalFeature(currentFeature);
                                logger.info("Set terminal Feature 'morph'.");
                            } else if ("NODETAG".equals(featureTag)) {
                                currentFeature = new Feature("cat");
                                header.addNonterminalFeature(currentFeature);
                                logger.info("Set non-terminal Feature 'cat'.");
                            } else if ("EDGETAG".equals(featureTag)) {
                                currentFeature = new Feature(Constants.EDGE);
                                header.setEdgeFeature(currentFeature);
                                logger.info("Set feature 'edges labelled'.");
                            } else if ("SECEDGETAG".equals(featureTag)) {
                                currentFeature = new Feature(Constants.SECEDGE);
                                header.setSecEdgeFeature(currentFeature);
                                logger.info("Set feature 'secondary edges'.");
                            }
                        } else if ("EOT".equals(begin)) {
                            currentFeature = null;
                        }
                    } else if (line.startsWith("FORMAT")) {
                    } else {
                        if (currentSentence != null) {
                            //Nonterminals
                            ntNode = new NT_Node();
                            currentSentence.addNonterminal(ntNode);
                            tokenizer = new StringTokenizer(line, "\t ");
                            ntNode.setID(tokenizer.nextToken());
                            ntNode.setFeature("cat", tokenizer.nextToken());
                            tokenizer.nextToken(); // for empty morph field: "--"
                            ntNode.setIncomingEdgeLabel(tokenizer.nextToken());

                            String edge = tokenizer.nextToken();

                            if (!"0".equals(edge)) {
                                primEdgeHash.put(ntNode, edge);
                            }

                            if (tokenizer.hasMoreTokens()) {
                                secEdgeLabelHash.put(ntNode,
                                    tokenizer.nextToken());
                                secEdgeHash.put(ntNode, tokenizer.nextToken());
                            }
                        }
                    }
                } else if (line.indexOf('%') == 0) {
                } else {
                    if (currentSentence == null) {
                        if (currentFeature != null) {
                            tokenizer = new StringTokenizer(line, "\t"); // no space separator !

                            // Skip number of item
                            tokenizer.nextToken();

                            String item = tokenizer.nextToken();

                            // Skip 'Y' and 'N' in pos table
                            if ("pos".equals(currentFeature.getName())) {
                                tokenizer.nextToken();
                            }

                            String description = tokenizer.nextToken();
                            currentFeature.addItem(item, description);
                        }
                    } else {
                        tokenizer = new StringTokenizer(line, "\t ");

                        if (tokenizer.hasMoreTokens()) {
                            tNode = new T_Node();
                            currentSentence.addTerminal(tNode);
                            tNodeNumberInSentence++;
                            tNode.setID(new Integer(sentenceVector.size()).toString() +
                                "." +
                                new Integer(tNodeNumberInSentence).toString());
                            tNode.setFeature("word", tokenizer.nextToken());
                            tNode.setFeature("pos", tokenizer.nextToken());
                            tNode.setFeature("morph", tokenizer.nextToken());
                            tNode.setIncomingEdgeLabel(tokenizer.nextToken());

                            String edge = tokenizer.nextToken();

                            if (!"0".equals(edge)) {
                                primEdgeHash.put(tNode, edge);
                            }

                            if (tokenizer.hasMoreTokens()) {
                                edge = tokenizer.nextToken();

                                if ("%%".equals(edge)) {
                                    tNode.setFeature("lemma",
                                        tokenizer.nextToken());
                                    lemmaInFile = true;
                                } else {
                                    secEdgeHash.put(tNode, tokenizer.nextToken());
                                    secEdgeLabelHash.put(tNode, edge);
                                }

                                if (tokenizer.hasMoreTokens()) {
                                    tokenizer.nextToken(); // for "%%"
                                    tNode.setFeature("lemma",
                                        tokenizer.nextToken());
                                    lemmaInFile = true;
                                }
                            }
                        }
                    }
                }
            }

            in.close();
            logger.debug("End reading syntax file");
        } catch (IOException e) {
            e.printStackTrace();
        }

        header.setNumberOfSentences(sentenceVector.size());
        header.setNumberOfTNodes(tNodeNumber);
        header.setNumberOfNTNodes(ntNodeNumber);
        header.setNumberOfEdges(edgeNumber);

        if (lemmaInFile && !header.isFeature("lemma")) {
            header.addTerminalFeature(new Feature("lemma"));
            logger.info("Set terminal feature 'lemma'.");
        }

        //if only 'word' defined, then setting two more default terminal features
        if (header.getAllTerminalFeaturesSize() == 1) {
            header.addTerminalFeature(new Feature("pos"));
            logger.info(
                "No user defined terminal features - set terminal feature 'pos'.");
            header.addTerminalFeature(new Feature("morph"));
            logger.info(
                "No user defined terminal features - set terminal feature 'morph'.");
        }

        //setting at least one non-terminal feature
        if (header.getAllNonterminalFeaturesSize() == 0) {
            header.addNonterminalFeature(new Feature("cat"));
            logger.info(
                "No user defined non-terminal features - set non-terminal feature 'cat'.");
        }
    }
}
