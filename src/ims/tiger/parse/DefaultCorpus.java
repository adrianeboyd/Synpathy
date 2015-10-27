/*
 * File:     DefaultCorpus.java
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
import ims.tiger.corpus.Sentence;

import ims.tiger.index.reader.IndexLoadProgressHandler;

import ims.tiger.index.reader.types.TypeHierarchy;

import ims.tiger.query.api.*;

import ims.tiger.query.internalapi.*;

import ims.tiger.system.Constants;

import org.apache.log4j.Logger;

import org.apache.xerces.parsers.SAXParser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * A class representing a default corpus
 * it manages reading and storing the feature descriptions and
 * the syntax file itself.
 * @author Alexander Klassmann
 * @version 1.1 28/11/02
 */
public class DefaultCorpus implements InternalCorpusQueryManager {
    private static Logger logger = Logger.getLogger(DefaultCorpus.class.getName());
    private static XMLReader parser;

    /** Holds value of property DOCUMENT ME! */
    private final Header header = new Header();

    /** Holds value of property DOCUMENT ME! */
    private final Vector sentenceVector = new Vector();
    private boolean corpusRead = false;

    /**
     * creates an empty corpus with minimal header
     *
     */
    public DefaultCorpus() {
        assureMinimalFeatures(header);
        header.setNumberOfTNodes(0);
        header.setNumberOfSentences(0);
        header.setNumberOfEdges(0L);

        //default: allow everything
        header.setEdgesLabeled();
        header.setCrossingEdges();
        header.setSecondaryEdges();
    }

    /**
     * Minimal constructor
     * @param corpusURL URL refering to the syntax file
     * (if null, an empty corpus will be created)
     */
    public DefaultCorpus(URL corpusURL) throws IOException, SAXException {
        this(corpusURL, null);
    }

    /**
     * @param corpusURL URI refering to the syntax file
     * @param featureURL refering to the feature descirption file
     */
    public DefaultCorpus(URL corpusURL, URL featureURL)
        throws IOException, SAXException {
        this(corpusURL, featureURL, false);
    }

    /**
     * @param corpusURL URL refering to the syntax file
     * @param featureURL refering to the feature descirption file
     * @param console if true, reading the syntax file via console
     * (in this case a corpusURL is necessary for identifying the format
     * and in case of an xml-file to find the corresponding dtd!)
     */
    public DefaultCorpus(URL corpusURL, URL featureURL, boolean console)
        throws IOException, SAXException {
        //try to read features; if fails, read only corpus and throw Exception later
        IOException featureIOException = null;
        SAXException featureSAXException = null;

        if (featureURL != null) {
            try {
                readFeatureFile(featureURL);
            } catch (IOException e) {
                featureIOException = e;
            } catch (SAXException e) {
                featureSAXException = e;
            } catch (Exception e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        }

        if (corpusURL != null) {
            if (corpusURL.getProtocol().startsWith("file")) {
                File file = new File(corpusURL.getFile());

                if (file.isDirectory()) {
                    readSyntaxCorpusFiles(findFiles(file));
                } else {
                    readSyntaxCorpusFile(corpusURL, console);
                }
            }
        } else {
            header.setNumberOfTNodes(0);
            header.setNumberOfSentences(0);
            header.setNumberOfEdges(0L);
        }

        assureMinimalFeatures(header);

        if (featureIOException != null) {
            throw featureIOException;
        }

        if (featureSAXException != null) {
            throw featureSAXException;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getAvailableCorporaList() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DefaultMutableTreeNode getAvailableCorporaTree() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCorpusBookmarkFileAvailable() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCorpusBookmarksAsString() {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCorpusDocumentation(String s) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ArrayList getCorpusLoadWarnings() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCorpusLoaded() {
        return corpusRead;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCurrentCorpusDocumentation() {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCurrentCorpusID() {
        return header.getCorpus_ID();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCurrentDetailedCorpusDocumentation() {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDetailedCorpusDocumentation(String s) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param i1 DOCUMENT ME!
     * @param i2 DOCUMENT ME!
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFeatureValueOf(int i1, int i2, String s) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHTMLTemplateDefinition(String s) {
        return "";
    }

    /**
    *
    * @return Header
    */
    public Header getHeader() {
        return header;
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws QueryIndexException DOCUMENT ME!
     */
    public Sentence getOriginalSentence(int i) throws QueryIndexException {
        return getSentence(i + 1);
    }

    /**
     *
     * @param number Sentence number
     * @return Sentence
     * @exception ims.tiger.query.manager.QueryIndexException <description>
     */
    public Sentence getSentence(int number) throws QueryIndexException {
        return (Sentence) sentenceVector.elementAt(number - 1);
    }

    /**
     *
     * @param number Sentence number
     * @return Sentence ID
     * @exception ims.tiger.query.manager.QueryIndexException <description>
     */
    public String getSentenceID(int number) throws QueryIndexException {
        return ((Sentence) sentenceVector.elementAt(number - 1)).getSentenceID();
    }

    /**
     *
     * @param id String ID
     * @return Sentence number
     */
    public int getSentenceNumber(String id) {
        if (id == null) {
            return -1;
        }

        for (int i = 0; i < sentenceVector.size(); i++) {
            if (id.equals(
                        ((Sentence) sentenceVector.elementAt(i)).getSentenceID())) {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTemplateDefinitionHead(String s) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getTemplateNames() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTemplatePath(String s) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getTemplateSignatures() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DefaultMutableTreeNode getTemplateTree() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isTemplates() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public TypeHierarchy getTypeHierarchy(String s) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isTypeHierarchy(String s) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param ID DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isValidCorpusID(String ID) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     */
    public void addSentence(Sentence sentence) {
        sentenceVector.add(sentence);
        header.setNumberOfTNodes(header.getNumberOfTNodes() +
            sentence.getTerminalsSize());
        header.setNumberOfSentences(header.getNumberOfSentences() + 1);
        header.setNumberOfEdges(header.getNumberOfEdges() +
            sentence.getCoreferenceSize());
    }

    /**
     * DOCUMENT ME!
     */
    public void closeCurrentCorpus() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    public void loadCorpus(String s) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     * @param handler DOCUMENT ME!
     */
    public void loadCorpus(String s, IndexLoadProgressHandler handler) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param q DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MatchResult processQuery(String q) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param q DOCUMENT ME!
     * @param i1 DOCUMENT ME!
     * @param i2 DOCUMENT ME!
     * @param i3 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MatchResult processQuery(String q, int i1, int i2, int i3) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param q DOCUMENT ME!
     * @param handler DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MatchResult processQuery(String q, QueryHandler handler) {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param q DOCUMENT ME!
     * @param i1 DOCUMENT ME!
     * @param i2 DOCUMENT ME!
     * @param i3 DOCUMENT ME!
     * @param handler DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public MatchResult processQuery(String q, int i1, int i2, int i3,
        QueryHandler handler) {
        return null;
    }

    /**
     * @param featureURI builds URL from URI (i.e. file selected via FileChooser)
     */
    public void readFeatureFile(String featureURI)
        throws IOException, SAXException {
        try {
            readFeatureFile(new URL("file:" + featureURI));
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * @param featureURI builds URL from URI (i.e. file selected via FileChooser)
     * @param header Header from corpus resp. forest other then this
     */
    public static void readFeatureFile(String featureURI, Header header)
        throws IOException, SAXException {
        try {
            readFeatureFile(new URL("file:" + featureURI), header);
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param featureURL DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     */
    public void readFeatureFile(URL featureURL)
        throws IOException, SAXException {
        readFeatureFile(featureURL, header);
    }

    /**
    * Invoke reading the feature from an XML file
    * reads first data into a dummy header; if no errors, transfer the features to the real header
    * @param featureURL URL of the feature description file
    * @param header from another corpus resp. forest
    */
    public static void readFeatureFile(URL featureURL, Header header)
        throws IOException, SAXException {
        if (featureURL == null) {
            throw new IOException("Feature URL is null");
        }

        logger.info("Feature file: " + featureURL);

        if (parser == null) {
            parser = new SAXParser();
        }

        Header localHeader = new Header();
        parser.setContentHandler(new FeatureContentHandler(localHeader));

        InputSource is = new InputSource(featureURL.openStream());
        is.setSystemId(featureURL.getFile());
        parser.parse(is);

        if ((localHeader.getAllNonterminalFeaturesSize() == 0) &&
                (localHeader.getAllTerminalFeaturesSize() == 0) &&
                (localHeader.getEdgeFeature() == null) &&
                (localHeader.getSecEdgeFeature() == null)) {
            throw new SAXException("Feature file contains no features.");
        }

        //if no Exception is thrown...
        resetHeader(header);

        //transfer data from localHeader to header
        List featureList = localHeader.getAllNonterminalFeatures();

        for (int i = 0; i < featureList.size(); i++) {
            header.addNonterminalFeature((Feature) featureList.get(i));
        }

        featureList = localHeader.getAllTerminalFeatures();

        for (int i = 0; i < featureList.size(); i++) {
            header.addTerminalFeature((Feature) featureList.get(i));
        }

        header.setEdgeFeature(localHeader.getEdgeFeature());
        header.setSecEdgeFeature(localHeader.getSecEdgeFeature());

        header.setCorpus_Author(localHeader.getCorpus_Author());
        header.setCorpus_Date(localHeader.getCorpus_Date());
        header.setCorpus_Description(localHeader.getCorpus_Description());
        header.setCorpus_Format(localHeader.getCorpus_Format());
        header.setCorpus_History(localHeader.getCorpus_History());
        header.setCorpus_Name(localHeader.getCorpus_Name());

        assureMinimalFeatures(header);
    }

    /**
     * @param corpusURI builds URL from URI (i.e. file selected via FileChooser)
     */
    public void readSyntaxCorpusFile(String corpusURI)
        throws IOException, SAXException {
        try {
            readSyntaxCorpusFile(new URL("file:" + corpusURI));
        } catch (MalformedURLException e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param corpusURL DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     */
    public void readSyntaxCorpusFile(URL corpusURL)
        throws IOException, SAXException {
        readSyntaxCorpusFile(corpusURL, false);
    }

    /**
     * reads the syntax Corpus from an .xml or a .syn file of from console
     * @param corpusURL corpus file URL
     * @param console true if reading from console, false if reading a file
     */
    public void readSyntaxCorpusFile(URL corpusURL, boolean console)
        throws IOException, SAXException {
        String fi = corpusURL.getFile().toLowerCase();
        Vector oldSentenceVector = new Vector();

        try {
            if (fi.endsWith(".xml") || fi.endsWith("tig") ||
                    fi.endsWith("tig.gz")) {
                try {
                    logger.info("Searching for features in content file.");
                    readFeatureFile(corpusURL, header);
                } catch (Exception e) {
                }

                if (parser == null) {
                    parser = new SAXParser();
                }

                for (int i = 0; i < sentenceVector.size(); i++) {
                    oldSentenceVector.add(sentenceVector.get(i));
                }

                sentenceVector.clear();

                logger.info("Reading content file: " + corpusURL);
                parser.setContentHandler(new SyntaxContentHandler(header,
                        sentenceVector));

                InputSource is = new InputSource(console ? System.in
                                                         : (fi.endsWith("gz")
                        ? new GZIPInputStream(corpusURL.openStream())
                        : corpusURL.openStream()));
                is.setSystemId(corpusURL.getFile());
                parser.parse(is);
                corpusRead = true;
            } else if (fi.endsWith(".syn") || fi.endsWith(".syn.gz")) {
                new SyntaxCorpusReader(header, sentenceVector).readSyntaxCorpus(corpusURL,
                    console);
                corpusRead = true;
            } else {
                logger.error("Format of " + corpusURL.getFile() +
                    " not known!");
            }

            //on error restore old values
        } catch (SAXException e) {
            sentenceVector.clear();

            for (int i = 0; i < oldSentenceVector.size(); i++) {
                sentenceVector.add(oldSentenceVector.get(i));
            }

            throw e;
        } catch (IOException e) {
            sentenceVector.clear();

            for (int i = 0; i < oldSentenceVector.size(); i++) {
                sentenceVector.add(oldSentenceVector.get(i));
            }

            throw e;
        } finally {
            assureMinimalFeatures(header);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param corpusFiles DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws SAXException DOCUMENT ME!
     */
    public void readSyntaxCorpusFiles(List corpusFiles)
        throws IOException, SAXException {
        sentenceVector.clear();
        logger.info(corpusFiles.size() + " files");

        for (int i = 0; i < corpusFiles.size(); i++) {
            try {
                readSyntaxCorpusFile(new URL("file:" + corpusFiles.get(i)),
                    false);
            } catch (MalformedURLException e) {
                logger.error(e.getMessage());
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void refreshAvailableCorpora() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     * @param ii DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int subgraph(int i, int[] ii) {
        return 0;
    }

    private static void assureMinimalFeatures(Header header) {
        if (header.getEdgeFeature() == null) {
            header.setEdgeFeature(new Feature(Constants.EDGE));
        }

        if (header.getSecEdgeFeature() == null) {
            header.setSecEdgeFeature(new Feature(Constants.SECEDGE));
        }

        if (header.getAllNonterminalFeaturesSize() == 0) {
            header.addNonterminalFeature(new Feature("cat"));
        }

        if (header.getAllTerminalFeaturesSize() == 0) {
            header.addTerminalFeature(new Feature("word"));
        }
    }

    /**
     * keep some structure information
     * (in case new feature file doesn't set them explicitly)
     * @param header
     */
    private static void resetHeader(Header header) {
        boolean edgesLabelled = header.edgesLabeled();
        boolean secEdges = header.secondaryEdges();
        boolean crossingEdges = header.crossingEdges();
        header.reset();

        if (edgesLabelled) {
            header.setEdgesLabeled();
        }

        if (secEdges) {
            header.setSecondaryEdges();
        }

        if (crossingEdges) {
            header.setCrossingEdges();
        }
    }

    private List findFiles(File dirFile) {
        List files = new ArrayList();
        String[] fileNames = dirFile.list();

        for (int i = 0; i < fileNames.length; i++) {
            File loopFile = new File(dirFile, fileNames[i]);

            if (loopFile.isDirectory()) {
                files.addAll(findFiles(loopFile));
            } else {
                if (loopFile.getName().endsWith("tig")) {
                    files.add(loopFile);
                }
            }
        }

        return files;
    }
}
