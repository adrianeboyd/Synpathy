/*
 * File:     InternalCorpusQueryManager.java
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
package ims.tiger.query.internalapi;

import ims.tiger.index.reader.IndexException;
import ims.tiger.index.reader.IndexLoadProgressHandler;
import ims.tiger.index.reader.IndexLoadStopException;

import ims.tiger.query.api.*;


/** Interface der internen Version der Query-API. Erweitert die offizielle API um
 *  einige Methoden, die vom TIGERSearch-GUI benoetigt werden.
 */
public interface InternalCorpusQueryManager extends CorpusQueryManager {
    /* ---------------------------------- */
    /* 1. Methods used for corpus loading */
    /* ---------------------------------- */

    /** Loads a new corpus which is represented by its ID.
     *  Deletes the current corpus - if present.
     *  Makes use of the IndexLoadProgressHandler.
     */
    public void loadCorpus(String new_corpusid, IndexLoadProgressHandler handler)
        throws QueryIndexException, IndexLoadStopException;

    /* ------------------------------------------ */
    /* 2. Methods used to access corpus corpus graphs */
    /* ------------------------------------------ */

    /** This method is used to calculate the subgraph node for a set
     *  of corpus graph nodes.
     */
    public int subgraph(int sentence, int[] nodes) throws QueryIndexException;

    /* ------------------------------------------ */
    /* 3. Methods used to process corpus queries  */
    /* ------------------------------------------ */

    /** Processes a query on the current corpus.
     *  Makes use of the QueryHandler object to send
     *  status information during query processing.
     */
    public MatchResult processQuery(String input, QueryHandler qh)
        throws QueryParseException, QueryIndexException;

    /** Processes a query on the current corpus.
     *  The query is processed on every corpus graph i,
     *  sent_min <= i <= sent_max. Query processing
     *  is stopped if at least match_max matching
     *  corpus graphs have been found.
     *  Makes use of the QueryHandler object to send
     *  status information during query processing.
     */
    public MatchResult processQuery(String input, int sent_min, int sent_max,
        int match_max, QueryHandler qh)
        throws QueryParseException, QueryIndexException;

    /* ------------------------------------------ */
    /* 4. Methods used to access the index        */
    /* ------------------------------------------ */

    /** Provides access to a feature value of a selected node of a selected sentence.
     *  The index is accessed by this method!
     */
    public String getFeatureValueOf(int sentence, int node, String fname)
        throws IndexException;
}
