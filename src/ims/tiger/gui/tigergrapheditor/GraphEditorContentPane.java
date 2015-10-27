/*
 * File:     GraphEditorContentPane.java
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

import ims.tiger.corpus.Header;

import ims.tiger.export.ExportManager;

import ims.tiger.gui.shared.HTMLViewer;
import ims.tiger.gui.shared.SimpleFileFilter;

import ims.tiger.gui.tigergraphviewer.GraphViewerContentPane;

import ims.tiger.gui.tigergraphviewer.forest.CorpusForest;
import ims.tiger.gui.tigergraphviewer.forest.Forest;
import ims.tiger.gui.tigergraphviewer.forest.ForestException;

import ims.tiger.parse.DefaultCorpus;

import org.apache.log4j.Level;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.URL;

import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;


/**
 * $Id: GraphEditorContentPane.java,v 1.23 2007/03/29 12:14:09 klasal Exp $
 * adds editing functionality to super class
 *
 * @author klasal
 */
public class GraphEditorContentPane extends GraphViewerContentPane {
    /** Holds value of property DOCUMENT ME! */
    private static final String SYNTAXEDITOR_DAT = "synpathy.pfs";

    /** Holds value of property DOCUMENT ME! */
    private static final String LAST_FEATURE_URL = "LastFeatureURL";

    /** Holds value of property DOCUMENT ME! */
    private static final String LAST_CORPUS_URL = "LastCorpusURL";

    /** Holds value of property DOCUMENT ME! */
    private static final Dimension HELP_DIALOG_SIZE = new Dimension(600, 400);
    private Action editEdgeFeatureAction;
    private Action editNTFeatureListAction;
    private Action editSecedgeFeatureAction;
    private Action editTFeatureListAction;
    private Action helpAction;
    private Action importAction;
    private Action newAction;
    private Action newFeaturesAction;
    private Action saveFeaturesAction;

    /**
     * Creates a new GraphEditorContentPane object.
     *
     * @param install_dir DOCUMENT ME!
     * @param user_dir DOCUMENT ME!
     * @param working_dir DOCUMENT ME!
     * @param textCorpusMode DOCUMENT ME!
     */
    public GraphEditorContentPane(String install_dir, String user_dir,
        String working_dir, boolean textCorpusMode) {
        super(install_dir, user_dir, working_dir, textCorpusMode);
        readEditorPreferences();
        graphPanel.setEditable(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     */
    public void setLastUsedCorpusURL(URL url) {
        lastUsedCorpusURL = url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public URL getLastUsedCorpusURL() {
        return lastUsedCorpusURL;
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     */
    public void setLastUsedFeatureURL(URL url) {
        lastUsedFeatureURL = url;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public URL getLastUsedFeatureURL() {
        return lastUsedFeatureURL;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JMenuBar createMenuBar() {
        JMenuBar mb = super.createMenuBar();

        JMenu fileMenu = mb.getMenu(0);
        fileMenu.add(new JMenuItem(newAction), 0);
        fileMenu.add(new JMenuItem(importAction), 3);
        fileMenu.add(new JMenuItem(newFeaturesAction), 5);
        fileMenu.add(new JMenuItem(saveFeaturesAction), 7);

        JMenu editMenu = new JMenu("Edit");
        editMenu.add(new JMenuItem(editTFeatureListAction));
        editMenu.add(new JMenuItem(editNTFeatureListAction));
        editMenu.add(new JMenuItem(editEdgeFeatureAction));
        editMenu.add(new JMenuItem(editSecedgeFeatureAction));
        mb.add(editMenu, 1);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.add(new JMenuItem(helpAction));
        mb.add(helpMenu, mb.getComponentCount());

        return mb;
    }

    /**
         *
         */
    public void saveConfiguration() {
        super.saveConfiguration();
        saveEditorPreferences();
    }

    /**
     * DOCUMENT ME!
     *
     * @param forest DOCUMENT ME!
     */
    public synchronized void visualizeForest(Forest forest) {
        super.visualizeForest(forest);
        graphPanel.setForest((CorpusForest) forest);
    }

    /**
     * DOCUMENT ME!
     */
    protected void exit() {
        if (((CorpusForest) forest).isModified()) {
            int option = JOptionPane.showConfirmDialog(GraphEditorContentPane.this,
                    "The corpus has been modified. Do you want to save it?");

            if (option == JOptionPane.CANCEL_OPTION) {
                return;
            }

            if (option == JOptionPane.YES_OPTION) {
                saveSentences();

                //something went wrong with save -> don't exit
                if (((CorpusForest) forest).isModified()) {
                    return;
                }
            }
        }

        super.exit();
    }

    /**
     * DOCUMENT ME!
     */
    protected void makeActions() {
        super.makeActions();
        newAction = new AbstractAction("New") {
                    public void actionPerformed(ActionEvent e) {
                        if (((CorpusForest) forest).isModified()) {
                            int option = JOptionPane.showConfirmDialog(GraphEditorContentPane.this,
                                    "The corpus has been modified. Do you want to open a new one anyway?");

                            if (option == JOptionPane.NO_OPTION) {
                                return;
                            }
                        }

                        newFile();
                    }
                };

        importAction = new AbstractAction("Import terminal nodes") {
                    public void actionPerformed(ActionEvent e) {
                        importSentences();
                    }
                };

        editTFeatureListAction = new AbstractAction(
                "Edit terminal Node Features") {
                    public void actionPerformed(ActionEvent e) {
                        editFeatureList(EditFeatureListDialog.TERMINAL_NODE_FEATURE);
                    }
                };

        editNTFeatureListAction = new AbstractAction(
                "Edit non-terminal Node Feature") {
                    public void actionPerformed(ActionEvent e) {
                        editFeatureList(EditFeatureListDialog.NONTERMINAL_NODE_FEATURE);
                    }
                };

        editEdgeFeatureAction = new AbstractAction("Edit edge labels") {
                    public void actionPerformed(ActionEvent e) {
                        editFeatureList(EditFeatureListDialog.EDGE_FEATURE);
                    }
                };

        editSecedgeFeatureAction = new AbstractAction(
                "Edit secondary edge labels") {
                    public void actionPerformed(ActionEvent e) {
                        editFeatureList(EditFeatureListDialog.SECEDGE_FEATURE);
                    }
                };

        helpAction = new AbstractAction("Help for Editing") {
                    public void actionPerformed(ActionEvent e) {
                        openHelpDialog();
                    }
                };

        newFeaturesAction = new AbstractAction("New Feature file") {
                    public void actionPerformed(ActionEvent e) {
                        newFeatureFile();
                    }
                };

        saveFeaturesAction = new AbstractAction("Save feature file") {
                    public void actionPerformed(ActionEvent e) {
                        saveFeatures();
                    }
                };
    }

    private void editFeatureList(int featureCategory) {
        JDialog dialog = new EditFeatureListDialog(getAncestorFrame(),
                forest.getHeader(), featureCategory);
        dialog.setVisible(true);
    }

    private void importSentences() {
        fc.setFileFilter(new SimpleFileFilter("txt"));

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            String[] featureNames = (String[]) forest.getHeader()
                                                     .getAllTFeatureNames()
                                                     .toArray(new String[0]);

            try {
                ImportDialog importDialog = new ImportDialog(getAncestorFrame(),
                        (CorpusForest) forest, file, featureNames);
                importDialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(),
                    "IO Error", JOptionPane.ERROR_MESSAGE);
                logger.error(e.getMessage());
            }
        }
    }

    private void newFeatureFile() {
        if (forest instanceof CorpusForest) {
            Header header = forest.getHeader();
            int option = NewFeaturesDialog.showNewFeaturesDialog(getAncestorFrame(),
                    header);

            if (option == NewFeaturesDialog.OK_OPTION) {
                lastUsedFeatureURL = null;
            }
        }
    }

    private void newFile() {
        Header oldHeader = null;

        if (forest != null) {
            oldHeader = forest.getHeader();
        }

        DefaultCorpus corpus = new DefaultCorpus();

        if (oldHeader != null) {
            Header newHeader = corpus.getHeader();
            newHeader.getAllNonterminalFeatures().clear();
            newHeader.getAllNonterminalFeatures().addAll(oldHeader.getAllNonterminalFeatures());
            newHeader.getAllTerminalFeatures().clear();
            newHeader.getAllTerminalFeatures().addAll(oldHeader.getAllTerminalFeatures());
            newHeader.setEdgeFeature(oldHeader.getEdgeFeature());
            newHeader.setSecEdgeFeature(oldHeader.getSecEdgeFeature());

            if (oldHeader.secondaryEdges()) {
                newHeader.setSecondaryEdges();
            }

            if (oldHeader.edgesLabeled()) {
                newHeader.setEdgesLabeled();
            }

            if (oldHeader.crossingEdges()) {
                newHeader.setCrossingEdges();
            }
        }

        try {
            forest = new CorpusForest(corpus);
            visualizeForest(forest);
            lastUsedCorpusURL = null;
            ((CorpusForest) forest).setModified(false);
        } catch (ForestException e) {
            e.printStackTrace();
        }
    }

    private void openHelpDialog() {
        JDialog helpDialog = new JDialog(getAncestorFrame(), "Help");
        HTMLViewer viewer = new HTMLViewer(this.getClass()
                                               .getResource("/ims/tiger/gui/tigergrapheditor/resources/EditHelp.html")
                                               .toString());
        helpDialog.getContentPane().add(viewer.GetViewerPanel());
        helpDialog.pack();
        helpDialog.setSize(HELP_DIALOG_SIZE);
        helpDialog.setVisible(true);
    }

    private void readEditorPreferences() {
        String file = user_dir + File.separator + PREFERENCES_DIR +
            File.separator + SYNTAXEDITOR_DAT;
        File file_test = new File(file);

        if (!file_test.exists()) {
            return;
        }
         // Datei noch nicht angelegt

        Object help;

        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(
                        file));
            help = in.readObject();
            in.close();
        } catch (Exception e) { // Objektstruktur durch Update veraendert! Beim Speichern aktuell.

            if (logger.isEnabledFor(Level.WARN)) {
                logger.warn(
                    "Structure of serialized object ForestViewerConfiguration has changed.");
            }

            file_test.delete();

            return;
        }

        try {
            lastUsedFeatureURL = (URL) ((HashMap) help).get(LAST_FEATURE_URL);
            lastUsedCorpusURL = (URL) ((HashMap) help).get(LAST_CORPUS_URL);
        } catch (ClassCastException e) {
            logger.error("Error casting the ForestViewerConfiguration object.");

            return;
        }
    }

    private void saveEditorPreferences() {
        String path = user_dir + File.separator + PREFERENCES_DIR +
            File.separator;
        File path_test = new File(path);

        if (!path_test.exists()) {
            path_test.mkdir();
        }

        // Datei erzeugen
        String fileName = path + SYNTAXEDITOR_DAT;

        File file = new File(fileName);

        if (file.exists()) {
            file.delete();
        }

        HashMap map = new HashMap();
        map.put(LAST_FEATURE_URL, lastUsedFeatureURL);
        map.put(LAST_CORPUS_URL, lastUsedCorpusURL);

        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(
                        file));
            out.writeObject(map);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFeatures() {
        fc.setFileFilter(FEATURE_FILE_FILTER);

        if (lastUsedFeatureURL != null) {
            fc.setSelectedFile(new File(lastUsedFeatureURL.getPath()));
        } else {
            fc.setCurrentDirectory(new File(user_dir));
        }

        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            try {
                String fileName = file.getCanonicalPath();

                if (fileName.indexOf('.') == -1) {
                    fileName += ".xml";
                }

                if (new File(fileName).exists()) {
                    if (JOptionPane.showConfirmDialog(this,
                                "File " + fileName +
                                " exists. Do you want to overwrite it?", "",
                                JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return;
                    }
                }

                ExportManager.saveHeader(forest.getHeader(), fileName);
                lastUsedFeatureURL = new URL("file:" + fileName);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
