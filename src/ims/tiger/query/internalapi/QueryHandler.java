/*
 * File:     QueryHandler.java
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

import ims.tiger.gui.shared.progress.ProgressContainerInterface;
import ims.tiger.gui.shared.progress.ProgressTaskInterface;

import ims.tiger.query.api.*;

import org.apache.log4j.Logger;


/** Dieser Handler steuert die kontrollierte Prozessierung von Korpusanfragen.
 *  Er ermoeglicht eine Kommunikation der GUI mit dem Auswertungsprozess.
 */
public class QueryHandler implements ProgressTaskInterface {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(QueryHandler.class);

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR_QUERY_PARSE = 0;

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR_QUERY_NORMALIZE = 1;

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR_QUERY_OPTIMIZE = 2;

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR_QUERY_EVALUATE = 3;

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR_QUERY_INDEXACCESS = 4;

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR_QUERY_FILTER = 5;

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR_OUT_OF_MEMORY = 6;

    /** Holds value of property DOCUMENT ME! */
    public static final int ERROR = 7;
    private InternalCorpusQueryManager manager;
    private String query;
    private int sent_min;
    private int sent_max;
    private int match_max;
    private MatchResult result;
    private boolean success;
    private boolean stop;
    private boolean error;
    private int errorType;
    private int errorRow;
    private int errorColumn;
    private String errorMessage;
    private ProgressContainerInterface container;

    /** Der Konstruktor erhaelt den QueryManager und die Query mit
     *  einigen spezifizierenden Parametern.
     */
    public QueryHandler(InternalCorpusQueryManager manager, String query,
        int sent_min, int sent_max, int match_max) {
        this.manager = manager;
        this.query = query;
        this.sent_min = sent_min;
        this.sent_max = sent_max;
        this.match_max = match_max;
    }

    /** Der Konstruktor erhaelt den QueryManager und die Query.
     */
    public QueryHandler(InternalCorpusQueryManager manager, String query) {
        this.manager = manager;
        this.query = query;
        this.sent_min = 0;
        this.sent_max = 0;
        this.match_max = 0;
    }

    /** Stellt den Kontakt zum umgebenden Container her. */
    public void setContainer(ProgressContainerInterface container) {
        this.container = container;
    }

    /** Starten die Evaluation der Query. Gelangt die Evaluation
     *  ohne Fehler/Abbruch zum Ende, ende die Methode automatisch.
     */
    public void startTask() {
        success = false;
        error = false;
        stop = false;

        errorMessage = "";
        errorType = 0;
        errorRow = -1;
        errorColumn = -1;

        result = null;

        try {
            result = manager.processQuery(query, sent_min, sent_max, match_max,
                    this);
        } catch (QueryParseException e) {
            if (logger.isInfoEnabled() == true) {
                logger.info("Query parse exception", e);
            }

            error = true;
            errorType = ERROR_QUERY_PARSE;
            errorRow = e.getErrorRow();
            errorColumn = e.getErrorColumn();
            errorMessage = e.getMessage();

            return;
        } catch (QueryIndexException e) {
            logger.error("Query index exception", e);
            error = true;
            errorType = ERROR_QUERY_INDEXACCESS;
            errorMessage = e.getMessage();

            return;
        } catch (OutOfMemoryError e) {
            logger.error("Out of memory exception during query evauation.");
            error = true;
            errorType = ERROR_OUT_OF_MEMORY;
            errorMessage = ims.tiger.system.Constants.OUT_OF_MEMORY;

            return;
        } catch (Exception e) {
            logger.error("Unexpected exception during query evaluation", e);
            error = true;
            errorType = ERROR;
            errorMessage = e.getMessage();

            return;
        }

        if (!stop) {
            success = true;
        }
    }

    /** Fenster fordert Abbruch? */
    public boolean isStopped() {
        return container.isAborted();
    }

    /** Evaluation intern gestoppt. */
    public void setStopped() {
        stop = true;
    }

    /** Die Statusmeldung wird aktualisiert. */
    public void setMessage(String message) {
        container.setMessage1(message);
    }

    /** Der Fortschrittsbalken wird aktualisiert. */
    public void setProgress(int progress) {
        container.setProgressValue(progress);
    }

    /** Die Anzahl der gefundenen Matches wird aktualisiert. */
    public void setNumberOfMatches(int matches) {
        container.setMessage2("Matching sentences: " +
            (new Integer(matches)).toString());
    }

    /** Ist die Aufgabe ohne Zwischenfaelle beendet worden? */
    public boolean endedWithSuccess() {
        return success;
    }

    /** Ist die Aufgabe abgebrochen worden? */
    public boolean endedWithStop() {
        return stop;
    }

    /** Ist die Aufgabe durch einen Fehler zum Ende gekommen? */
    public boolean endedWithError() {
        return error;
    }

    /** Uebergibt eine Fehlermeldung an das umgebende Fenster. */
    public String getErrorMessage() {
        return errorMessage;
    }

    /** Uebergibt eine Fehlermeldung an das umgebende Fenster. */
    public int getErrorType() {
        return errorType;
    }

    /** Uebergibt die Fehlerposition (falls moeglich) an das umgebende Fenster. */
    public int getErrorRow() {
        return errorRow;
    }

    /** Uebergibt die Fehlerposition (falls moeglich) an das umgebende Fenster. */
    public int getErrorColumn() {
        return errorColumn;
    }

    /** Uebergibt das Query-Ergebnis an das umgebende Fenster. */
    public MatchResult getResult() {
        return result;
    }
}
