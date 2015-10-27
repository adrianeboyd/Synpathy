/*
 * File:     Constants.java
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
package ims.tiger.system;


/**
 * TIGERSEARCH-Systemkonstanten
 */
public class Constants {
    /** Konstanten fuer die Version */
    public final static String VERSION = "Version 2.1.1";

    /** Festlegung des XML-Parser */
    public final static String SAXREADER = "org.apache.xerces.parsers.SAXParser";

    /** Projekt-Homepage */
    public final static String TIGERHomepage = "http://www.tigersearch.de";

    /** Holds value of property DOCUMENT ME! */
    public final static String TIGERScriptNS = "http://www.tigersearch.de/TIGERScript";

    /** Pfade zum TigerXML.xsd Schema */
    public final static String TigerXMLSchema = "TigerXML.xsd";

    /** Holds value of property DOCUMENT ME! */
    public final static String PublicTigerXMLSchema = "http://www.ims.uni-stuttgart.de/projekte/TIGER/TIGERSearch/public/TigerXML.xsd";

    /** Globale Fehlermeldungen */
    public final static String OUT_OF_MEMORY_CORPUS = "Your system has run out of memory. Please choose a smaller corpus or\n change the memory configuration in the TIGERRegistry tool.\n";

    /** Holds value of property DOCUMENT ME! */
    public final static String OUT_OF_MEMORY = "Your system has run out of memory.\n Please change the memory configuration in the TIGERRegistry tool.\n";

    /** Holds value of property DOCUMENT ME! */
    public final static String OUT_OF_MEMORY_CONTINUE = "\nDo you want to close the application or try to continue?\n";

    /** Holds value of property DOCUMENT ME! */
    public final static String OUT_OF_MEMORY_CLOSED = "\nThe application will be closed now.\n";

    /** Konstante zur Unterscheidung T-/NT-Knoten */
    public final static int CUT = 150;

    /** Konstante maximale Anzahl von Tochterknoten eines Knoten */
    public final static int MAX_DAUGHTER = 250;

    /** Holds value of property DOCUMENT ME! */
    public final static int MAX_DEPTH = 250;

    /** Bis zu welcher Schranke bringt ein ReversedCorpus-Eintrag einen Gewinn? */
    public final static double REVCORPUS_BOUND = 0.5;

    /** Stufen fuer Knotenfilter */
    public final static double[] NODEFILTER_BOUNDS = { 0.1, 0.25, 0.5 };

    /** Holds value of property DOCUMENT ME! */
    public final static int NODEFILTER_DISJUNCTS = 3;

    /** Holds value of property DOCUMENT ME! */
    public final static String EDGE = "edge";

    /** Holds value of property DOCUMENT ME! */
    public final static String SECEDGE = "secedge";

    /** Holds value of property DOCUMENT ME! */
    public final static String TIGERS = "VROOT";

    /** Holds value of property DOCUMENT ME! */
    public final static String TIGER_UNDEF = "tiger_undef"; // TYP!

    /** Holds value of property DOCUMENT ME! */
    public final static String INTNODE = "my";

    /** Holds value of property DOCUMENT ME! */
    public final static String UNDEF = "--";

    /** Holds value of property DOCUMENT ME! */
    public final static String CORPUS_DETECTOR = "corpus_config.xml";

    /** Holds value of property DOCUMENT ME! */
    public final static String HEADER_DETECTOR = "corpus.header";

    /** Konstanten fuer Feature-Typen */
    public final static byte BYTE_LIST_TYPE = 1;

    /** Holds value of property DOCUMENT ME! */
    public final static byte SHORT_LIST_TYPE = 2;

    /** Holds value of property DOCUMENT ME! */
    public final static byte INT_LIST_TYPE = 4;

    /** Konstanten fuer vordefinierte Typen */
    public final static String CONSTANT = "Constant";

    /** Holds value of property DOCUMENT ME! */
    public final static String USERDEFCONSTANT = "UserDefConstant";

    /** Holds value of property DOCUMENT ME! */
    public final static String STRING = "String";

    /** Holds value of property DOCUMENT ME! */
    public final static String T = "T";

    /** Holds value of property DOCUMENT ME! */
    public final static String NT = "NT";

    /** Holds value of property DOCUMENT ME! */
    public final static String FREC = "FREC";

    /** Konstanten fuer Variablentypen */
    public final static byte NODE_VARIABLE = 1;

    /** Holds value of property DOCUMENT ME! */
    public final static byte FEATURE_CONSTRAINT_VARIABLE = 2;

    /** Holds value of property DOCUMENT ME! */
    public final static byte FEATURE_VALUE_VARIABLE = 3;

    /** Konstante fuer Dateiende */
    public final static String EOF = "#EOF#";

    /** Konstanten fuer die Speicherverwaltung */
    public final static byte MEMORY = 2;

    /** Holds value of property DOCUMENT ME! */
    public final static byte MEMORY_RESTRICTED = 1;

    /** Holds value of property DOCUMENT ME! */
    public final static byte HARDDISC = 0;

    /** Konstanten fuer die Importfilter */
    public final static int MAX_FVALUE_COLLECT = 250;

    /** Konstanten fuer die Praedikate */
    public final static byte CONTINUOUS = 1;

    /** Holds value of property DOCUMENT ME! */
    public final static byte DISCONTINUOUS = 2;

    /** Holds value of property DOCUMENT ME! */
    public final static byte ARITY = 3;

    /** Holds value of property DOCUMENT ME! */
    public final static byte TOKEN_ARITY = 4;

    /** Holds value of property DOCUMENT ME! */
    public final static byte ROOT = 5;

    /** Holds value of property DOCUMENT ME! */
    public final static byte REGEXP = 6;

    /** Konstanten fuer die Relationen */
    public final static byte DOMINATES_M_N = 1;

    /** Holds value of property DOCUMENT ME! */
    public final static byte DOMINATES_N = 2;

    /** Holds value of property DOCUMENT ME! */
    public final static byte DOMINATES_DIR = 3;

    /** Holds value of property DOCUMENT ME! */
    public final static byte DOMINATES = 4;

    /** Holds value of property DOCUMENT ME! */
    public final static byte DOMINATES_LEFT = 5;

    /** Holds value of property DOCUMENT ME! */
    public final static byte DOMINATES_RIGHT = 6;

    /** Holds value of property DOCUMENT ME! */
    public final static byte DOMINATES_LABEL = 7;

    /** Holds value of property DOCUMENT ME! */
    public final static byte REFERENCES = 8;

    /** Holds value of property DOCUMENT ME! */
    public final static byte REFERENCES_LABEL = 9;

    /** Holds value of property DOCUMENT ME! */
    public final static byte PRECEDES_M_N = 10;

    /** Holds value of property DOCUMENT ME! */
    public final static byte PRECEDES_N = 11;

    /** Holds value of property DOCUMENT ME! */
    public final static byte PRECEDES_DIR = 12;

    /** Holds value of property DOCUMENT ME! */
    public final static byte PRECEDES = 13;

    /** Holds value of property DOCUMENT ME! */
    public final static byte SISTERS = 14;

    /** Holds value of property DOCUMENT ME! */
    public final static byte SISTERS_PRECEDES = 15;

    /** Holds value of property DOCUMENT ME! */
    public final static byte EMPTY_RELATION = 16;

    /** Holds value of property DOCUMENT ME! */
    public final static String[] relsymbols = {
        ">", ">", ">", ">*", ">@l", ">@r", ">", ">~", ">~", ".", ".", ".", ".*",
        "$", "$.*", "Error"
    };

    /** Holds value of property DOCUMENT ME! */
    public final static char[] TO_BE_QUOTED = {
        '\"', '.', '*', '@', '(', ')', '+', '-', '*', ' ', '!', '=', '#', ':',
        ';', '>', '|', '&', '$', '/', '?', ',', '{', '}'
    };

    /** Holds value of property DOCUMENT ME! */
    public final static String VM_VERSION = "1.4";

    /** Relationsfilter bei TIGERRegistry und bei TIGERSearch verwenden */

    //public static boolean USE_RELATIONFILTER = false;
}
