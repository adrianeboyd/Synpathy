/*
 * File:     CorpusQueryManager.java
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
package ims.tiger.query.api;

import ims.tiger.corpus.Header;
import ims.tiger.corpus.Sentence;

import ims.tiger.index.reader.types.TypeHierarchy;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;


/** This interface defines the corpus query manager API of the
 *  TIGERSearch tool. There is currently only one implementation (local),
 *  but a client/server version might follow.
 *
 *  This interface differs slightly from the original IMS, since processQuery
 *  throws less Exceptions.
 */
public interface CorpusQueryManager {
    /* ---------------------------------- */
    /* 1. Methods used for corpus loading */
    /* ---------------------------------- */

    /** Delivers a list of all available corpus IDs. */
    public List getAvailableCorporaList();

    /** Delivers all available corpora represented as a corpus tree. */
    public DefaultMutableTreeNode getAvailableCorporaTree();

    /** Refreshes the initialization of the available corpora.
     *  The list and tree representation could be cached, thus
     *  this method enforces reinitialization.
     */
    public void refreshAvailableCorpora() throws CorpusQueryManagerException;

    /** Checks whether the corpus id is valid. */
    public boolean isValidCorpusID(String corpusid);

    /** Checks whether a corpus has already been loaded. */
    public boolean isCorpusLoaded();

    /** Delivers the ID of the corpus currently loaded.
     *  Delivers a null pointer if no corpus is loaded!
     */
    public String getCurrentCorpusID();

    /** Loads a new corpus which is represented by its ID.
     *  Deletes the current corpus - if present.
     */
    public void loadCorpus(String new_corpusid) throws QueryIndexException;

    /** Delivers a list of Strings that describe any problems
     *  that might have occured during the corpus loading process.
     */
    public ArrayList getCorpusLoadWarnings();

    /** Closes a corpus. The memory used by the corpus index is reset. */
    public void closeCurrentCorpus();

    /* ---------------------------------------------- */
    /* 2. Methods used to access corpus documentation */
    /* ---------------------------------------------- */

    /** Delivers the Header object of the corpus that comprises meta
     *  information about the corpus and detailed feature declarations.
     */
    public Header getHeader();

    /** Generates a short HTML documentation file that comprises
     *  essential information about a corpus.
     */
    public String getCorpusDocumentation(String corpusid)
        throws IOException;

    /** Generates a short HTML documentation file that comprises
     *  essential information about the CURRENT corpus.
     */
    public String getCurrentCorpusDocumentation() throws IOException;

    /** Generates a more detailed HTML documentation file that comprises
     *  meta and feature information about a corpus.
     */
    public String getDetailedCorpusDocumentation(String corpusid)
        throws IOException;

    /** Generates a more detailed HTML documentation file that comprises
     *  meta and feature information about the CURRENT corpus.
     */
    public String getCurrentDetailedCorpusDocumentation()
        throws IOException;

    /** Is a corpus bookmark file available? */
    public boolean isCorpusBookmarkFileAvailable();

    /** Deliver the content of the corpus bookmark file as a String object. */
    public String getCorpusBookmarksAsString();

    /** Liegen Templates vor? */
    public boolean isTemplates();

    /** Lies die Templatenamen aus */
    public String[] getTemplateNames();

    /** Lies die Templatesignaturen aus */
    public String[] getTemplateSignatures();

    /** Uebergibt eine Baumstruktur, die alle definierten Templates repraesentiert. */
    public DefaultMutableTreeNode getTemplateTree();

    /** Uebergibt eine HTML-Expansion des Templates. */
    public String getHTMLTemplateDefinition(String templateSignature);

    /** Uebergibt die Definition des Template-Kopfes. */
    public String getTemplateDefinitionHead(String templateSignature);

    /** Uebergibt den relativen Dateipfad des Templates. */
    public String getTemplatePath(String templateSignature);

    /* ------------------------------------------------- */
    /* 3. Methods used to access corpus type hierarchies */
    /* ------------------------------------------------- */

    /** Checks whether a type hierarchy has been defined for a feature. */
    public boolean isTypeHierarchy(String fname);

    /** Returns the TypeHierarchy object for a feature. */
    public TypeHierarchy getTypeHierarchy(String fname);

    /* ------------------------------------------ */
    /* 4. Methods used to access corpus corpus graphs */
    /* ------------------------------------------ */

    /** Generates a Sentence object that represents the structure of
     *  the sentno-th corpus graph of the corpus.
     */
    public Sentence getSentence(int sentno) throws QueryIndexException;

    /** Generates a Sentence object that represents the structure of
     *  the sentno-th corpus graph of the corpus. Makes use of the original
     *  corpus graph and node IDs that have been used in the TIGER-XML
     *  corpus definition file.
     */
    public Sentence getOriginalSentence(int sentno) throws QueryIndexException;

    /** Generates the corpus graph ID of the sentno-th sentence. */
    public String getSentenceID(int sentno) throws QueryIndexException;

    /** Generates the number of the corpus graph identified by its ID.
     *  Results -1 if not available.
     */
    public int getSentenceNumber(String id);

    /* ------------------------------------------ */
    /* 5. Methods used to process corpus queries  */
    /* ------------------------------------------ */

    /** Processes a query on the current corpus. */
    public MatchResult processQuery(String input)
        throws QueryParseException, QueryIndexException;

    /** Processes a query on the current corpus.
     *  The query is processed on every corpus graph i,
     *  sent_min <= i <= sent_max. Query processing
     *  is stopped if at least match_max matching
     *  corpus graphs have been found.
     */
    public MatchResult processQuery(String input, int sent_min, int sent_max,
        int match_max) throws QueryParseException, QueryIndexException;
}
