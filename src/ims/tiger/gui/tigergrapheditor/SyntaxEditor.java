/*
 * File:     SyntaxEditor.java
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

package ims.tiger.gui.tigergrapheditor;

import ims.tiger.gui.tigergraphviewer.GraphViewer;

import ims.tiger.gui.tigergraphviewer.forest.CorpusForest;
import ims.tiger.gui.tigergraphviewer.forest.ForestException;

import ims.tiger.parse.DefaultCorpus;

import org.apache.log4j.Logger;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


/**
 * $Id: SyntaxEditor.java,v 1.14 2006/08/23 09:24:33 klasal Exp $
 * Starting class for Synpathy
 *
 * @author Alexander Klassmann
 * @version August 2006
 */
public class SyntaxEditor {
    private static Logger logger = Logger.getLogger(SyntaxEditor.class.getName());

    /** Holds value of property DOCUMENT ME! */
    private static final String RESOURCE_PATH = "/ims/tiger/resources";

    /** Holds value of property DOCUMENT ME! */
    private static final String DEFAULT_FEATURE_FILE = null;

    /** Holds value of property DOCUMENT ME! */
    protected static boolean standalone = true;

    /** Holds value of property DOCUMENT ME! */
    private final GraphViewer fv;

    /**
     * Empty Constructor
     * starts the syntaxviewer with an empty panel
     */
    public SyntaxEditor() {
        this(null);
    }

    /**
     * Reads corpus
     * @param corpusURL syntax file (with ending .xml or .syn or .syn.gz)
     */
    public SyntaxEditor(URL corpusURL) {
        this(corpusURL, null, (String) null);
    }

    /**
     * same as above plus additional parameters for time synchronization
     * @param corpusURL url of corpus file
     * @param featureURL url o feature file
     * @param configPath path to look for configuration files
     */
    public SyntaxEditor(URL corpusURL, URL featureURL, String configPath) {
        if (configPath == null) {
            try {
                configPath = this.getClass().getResource(RESOURCE_PATH).getPath();
            } catch (NullPointerException e) {
                logger.error("No resources in classpath.");
            }
        }

        fv = getGraphViewer(configPath);
        setForest(corpusURL, featureURL);
    }

    /**
     * Entry method
     *
     * @param args arguments: -fd featureDescriptionURL -config configPath corpusURL
     * if no protocol is specified in corpusURL, a `file:'-protocol is added
    */
    public static void main(String[] args) {
        URL corpusURL = null;
        URL featureURL = null;
        String configPath = null;

        standalone = true;

        try {
            for (int i = 0; i < args.length; i++) {
                if ("-fd".equals(args[i])) {
                    try {
                        featureURL = new URL(args[++i]);
                    } catch (MalformedURLException e) {
                        if (e.getMessage().startsWith("no protocol")) {
                            try {
                                featureURL = new URL("file://" +
                                        new File(args[i]).getCanonicalPath());
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        } else {
                            throw new MalformedURLException(e.getMessage());
                        }
                    }
                } else if ("-config".equals(args[i])) {
                    configPath = args[++i];
                } else if (args[i].startsWith("-") || (corpusURL != null)) {
                    throw new IllegalArgumentException();
                } else {
                    try {
                        corpusURL = new URL(args[i]);
                    } catch (MalformedURLException e) {
                        if (e.getMessage().startsWith("no protocol")) {
                            try {
                                corpusURL = new URL("file://" +
                                        new File(args[i]).getCanonicalPath());
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        } else {
                            throw new MalformedURLException(e.getMessage());
                        }
                    }
                }
            }

            new SyntaxEditor(corpusURL, featureURL, configPath);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            System.out.println("Syntax Error: Option '" +
                args[args.length - 1] + "' must be followed by a file name!");
            System.exit(0);
        } catch (IllegalArgumentException e) {
            System.out.println(
                "Usage: syntaxviewer [URL of syntax file] [-fd featureDescription file] [-config GraphViewer configuration file] [-console]");
            System.exit(0);
        } catch (MalformedURLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * precedence is as follows: file at command line > lastUsedFile > defaultFile
     * if features are defined within the corpus file, they have priority over external feature file
     * @param corpusURL
     * @param featureURL
     */
    protected void setForest(URL corpusURL, URL featureURL) {
        DefaultCorpus corpus = new DefaultCorpus();
        GraphEditorContentPane editorPane = (GraphEditorContentPane) ((JFrame) fv).getContentPane();

        //no feature URL as parameter, than try last used
        if (featureURL == null) {
            featureURL = editorPane.getLastUsedFeatureURL();
        }

        //also no last used, than try default
        if ((featureURL == null) && (DEFAULT_FEATURE_FILE != null)) {
            if (new File(DEFAULT_FEATURE_FILE).exists()) {
                try {
                    featureURL = new URL("file:" + DEFAULT_FEATURE_FILE);
                } catch (MalformedURLException e) {
                    logger.error(e.getMessage());
                    featureURL = null;
                }
            } else {
                featureURL = this.getClass().getResource(DEFAULT_FEATURE_FILE);
            }
        }

        if (featureURL != null) {
            try {
                corpus.readFeatureFile(featureURL);
                editorPane.setLastUsedFeatureURL(featureURL);
            } catch (Exception e) {
                handleException(e);

                //if error with last used feature, delete reference
                if (featureURL == editorPane.getLastUsedFeatureURL()) {
                    editorPane.setLastUsedFeatureURL(null);
                }
            }
        }

        //if no corpus URL as parameter, than try last used 
        if (corpusURL == null) {
            corpusURL = editorPane.getLastUsedCorpusURL();
        }

        if (corpusURL != null) {
            try {
                corpus.readSyntaxCorpusFile(corpusURL);
                editorPane.setLastUsedCorpusURL(corpusURL);
            } catch (Exception e) {
                handleException(e);

                //if error with last used corpus, delete reference
                if (corpusURL == editorPane.getLastUsedCorpusURL()) {
                    editorPane.setLastUsedCorpusURL(null);
                }
            }
        }

        try {
            CorpusForest forest = new CorpusForest(corpus);
            fv.visualizeForest(forest);
        } catch (ForestException fe) {
            logger.error(fe.getMessage());
            fe.printStackTrace();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param configPath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected GraphViewer getGraphViewer(String configPath) {
        return new TIGERGraphEditor("", configPath,
            System.getProperty("user.dir"), standalone, true,
            System.getProperty("user.home"));
    }

    private void handleException(Exception e) {
        if (e instanceof IOException) {
            logger.error(e.getMessage());

            JOptionPane.showMessageDialog((JFrame) fv,
                "Error with reading corpus:  " + e.getMessage(),
                "Error message", JOptionPane.ERROR_MESSAGE);
        } else if (e instanceof SAXException) {
            logger.error(e.getMessage() + ": " +
                e.getStackTrace()[0].toString());
            JOptionPane.showMessageDialog((JFrame) fv,
                "Error with parsing corpus.\n File probably corrupt",
                "Error message", JOptionPane.ERROR_MESSAGE);
        } else {
            e.printStackTrace();
            JOptionPane.showMessageDialog((JFrame) fv,
                "An error occurred: " + e.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
