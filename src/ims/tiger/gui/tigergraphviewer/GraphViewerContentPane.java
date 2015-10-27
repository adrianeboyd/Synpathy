/*
 * File:     GraphViewerContentPane.java
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
package ims.tiger.gui.tigergraphviewer;

import ims.tiger.corpus.Header;

import ims.tiger.export.ExportManager;

import ims.tiger.gui.shared.ImageLoader;
import ims.tiger.gui.shared.SimpleFileFilter;

import ims.tiger.gui.tigergraphviewer.draw.DisplayNode;
import ims.tiger.gui.tigergraphviewer.draw.DisplaySentence;
import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;
import ims.tiger.gui.tigergraphviewer.draw.GraphPanel;

import ims.tiger.gui.tigergraphviewer.forest.CorpusForest;
import ims.tiger.gui.tigergraphviewer.forest.Forest;
import ims.tiger.gui.tigergraphviewer.forest.ForestException;
import ims.tiger.gui.tigergraphviewer.forest.ResultForest;

import ims.tiger.gui.tigergraphviewer.options.ColorOptions;
import ims.tiger.gui.tigergraphviewer.options.DisplayOptions;
import ims.tiger.gui.tigergraphviewer.options.ExportForestDialog;
import ims.tiger.gui.tigergraphviewer.options.ExportImageDialog;

import ims.tiger.parse.DefaultCorpus;

import org.apache.log4j.Logger;

import org.xml.sax.SAXException;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.filechooser.FileFilter;


/**
 * $Id: GraphViewerContentPane.java,v 1.37 2007/03/23 14:48:07 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.37 $
 */
public class GraphViewerContentPane extends JPanel {
    /** Holds value of property DOCUMENT ME! */
    private static final FileFilter TIGER_FILE_FILTER = new SimpleFileFilter(new String[] {
                "tig", "tig.gz"
            }, "Tiger file");

    /** Holds value of property DOCUMENT ME! */
    protected static final FileFilter FEATURE_FILE_FILTER = new SimpleFileFilter(
            "xml");

    /** Holds value of property DOCUMENT ME! */
    private static final FileFilter RESULT_FILE_FILTER = new SimpleFileFilter(
            "res");

    /** Holds value of property DOCUMENT ME! */
    protected static final String PREFERENCES_DIR = ".synpathy";

    /** Holds value of property DOCUMENT ME! */
    private static final Dimension DEFAULT_SIZE = new Dimension(800, 400);

    /** Holds value of property DOCUMENT ME! */
    protected final JFileChooser fc = new JFileChooser(System.getProperty(
                "user.home"));

    /** Holds value of property DOCUMENT ME! */
    protected final Logger logger = Logger.getLogger(GraphViewerContentPane.class.getName());

    /** Collection of sentences */
    protected Forest forest;

    /** Panel that shows the tree */
    protected GraphPanel graphPanel;

    /** Holds value of property DOCUMENT ME! */
    protected String install_dir;

    /** Holds value of property DOCUMENT ME! */
    protected String user_dir;

    /** Holds value of property DOCUMENT ME! */
    protected String working_dir;

    /** url of last used corpus file */
    protected URL lastUsedCorpusURL;

    /** url of last used feature file */
    protected URL lastUsedFeatureURL;
    private Action closeAction;
    private Action exitAction;
    private Action exportAction;
    private Action firstMatchAction;
    private Action focusAction;
    private Action gotoMatchAction;
    private Action lastMatchAction;
    private Action nextMatchAction;
    private Action nextSubMatchAction;
    private Action openAction;
    private Action pageSetupAction;
    private Action previousMatchAction;
    private Action previousSubMatchAction;
    private Action printAction;
    private Action readFeaturesAction;
    private Action refreshAction;
    private Action saveAction;
    private Action saveForestAction;
    private Action showColorOptionsAction;
    private Action showDisplayOptionsAction;
    private Action showTextPanelAction;
    private ButtonPanel buttonPanel;
    private ColorOptions colop;
    private DisplayOptions displop;

    /** Graphical representation of a the displayed sentence */
    private DisplaySentence sentence;
    private ExportForestDialog exfodi;
    private ExportImageDialog exdil;
    private Header header;
    private IconBar iconBar;
    private JCheckBoxMenuItem focus;
    private JCheckBoxMenuItem text;
    private JScrollPane jsp;
    private List highlightedNodeIDs;
    private TIGERGraphViewerConfiguration config;

    /** Panel at the bottom which show terminal nodes as sentence */
    private TextPanel textPanel;
    private Toolkit toolkit;
    private boolean focusOnMatch;
    private boolean textCorpusMode;

    /**
     * Creates a new GraphViewerContentPane object.
     *
     * @param install_dir DOCUMENT ME!
     * @param user_dir DOCUMENT ME!
     * @param working_dir DOCUMENT ME!
     * @param textCorpusMode DOCUMENT ME!
     */
    public GraphViewerContentPane(String install_dir, String user_dir,
        String working_dir, boolean textCorpusMode) {
        this.install_dir = install_dir;
        this.user_dir = user_dir;
        this.working_dir = working_dir;
        this.textCorpusMode = textCorpusMode;

        config = new TIGERGraphViewerConfiguration(PREFERENCES_DIR,
                install_dir, user_dir);

        try {
            logger.info(install_dir);
            config.readConfiguration();
        } catch (Exception e) {
            logger.error("Error reading the forest viewer configuration file: " +
                e.getMessage());
        }

        makeActions();

        iconBar = new IconBar(textCorpusMode);
        iconBar.setActionListener(openAction, "Open");
        iconBar.setActionListener(printAction, "Print");
        iconBar.setActionListener(exportAction, "save_svg");
        iconBar.setActionListener(saveForestAction, "save_forest");
        iconBar.setActionListener(refreshAction, "Refresh");
        iconBar.setActionListener(focusAction, "Focus");
        iconBar.setActionListener(showTextPanelAction, "Text");
        iconBar.setName("iconBar");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.ipadx = 0;
        gbc.ipady = 0;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        add(iconBar, gbc);

        // Malflaeche erzeugen und in ein JscrollPane einbauen
        jsp = new GraphViewerScrollPane();

        graphPanel = new GraphPanel(config);

        jsp.setViewportView(graphPanel);

        // Inhaltscontainer fuer JScrollPane und ButtonPane
        setMinimumSize(DEFAULT_SIZE);
        setPreferredSize(DEFAULT_SIZE);
        setSize(DEFAULT_SIZE);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createRaisedBevelBorder());

        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.SOUTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 1;
        gbc.gridwidth = 2;
        gbc.gridx = GridBagConstraints.REMAINDER;
        gbc.gridy = 1;
        add(jsp, gbc);

        // Navigationsleiste erzeugen
        buttonPanel = new ButtonPanel(firstMatchAction, lastMatchAction,
                previousMatchAction, nextMatchAction, gotoMatchAction,
                previousSubMatchAction, nextSubMatchAction);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.gridy = 2;
        add(buttonPanel, gbc);

        /* HTML-Panel fuer die textuelle Darstellung des Satzes */
        textPanel = new TextPanel(header);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridy = 3;
        add(textPanel, gbc);

        this.setSize(GraphConstants.windowWidth, GraphConstants.windowHeight);

        toolkit = Toolkit.getDefaultToolkit();

        setVisible(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getCurrentDisplayedMatch() {
        return forest.getCurrentMatchNumber();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public GraphPanel getGraphPanel() {
        return graphPanel;
    }

    /**
     * DOCUMENT ME!
     *
     * @param ids DOCUMENT ME!
     */
    public void setHighlightedNodeIDs(List ids) {
        highlightedNodeIDs = ids;
        updateSentence();
        graphPanel.setScroll();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();

        JMenu File = new JMenu("File");
        File.add(new JMenuItem(openAction));
        File.add(new JMenuItem(saveAction));
        File.addSeparator();
        File.add(new JMenuItem(readFeaturesAction));
        File.addSeparator();

        if (textCorpusMode) {
            File.add(new JMenuItem(exitAction));
        } else {
            File.add(new JMenuItem(closeAction));
        }

        JMenu Graph = new JMenu("Graph");
        Graph.add(new JMenuItem(exportAction));

        //Graph.add(new JMenuItem(saveForestAction));
        //Temporarily disabled as export as forest not works properly; 
        //see also IconBar!
        Graph.addSeparator();
        Graph.add(new JMenuItem(pageSetupAction));
        Graph.add(new JMenuItem(printAction));

        JMenu View = new JMenu("View");
        focus = new JCheckBoxMenuItem(focusAction);
        focus.setVerticalTextPosition(JCheckBoxMenuItem.CENTER);
        focus.setHorizontalTextPosition(JCheckBoxMenuItem.LEFT);
        View.add(focus);

        if (textCorpusMode) {
            focus.setEnabled(false);
        } else {
            focus.setEnabled(true);
        }

        text = new JCheckBoxMenuItem(showTextPanelAction);
        text.setVerticalTextPosition(JCheckBoxMenuItem.CENTER);
        text.setHorizontalTextPosition(JCheckBoxMenuItem.LEFT);

        View.add(text);
        View.addSeparator();
        View.add(new JMenuItem(refreshAction));

        JMenu Options = new JMenu("Options");
        Options.add(new JMenuItem(showColorOptionsAction));
        Options.add(new JMenuItem(showDisplayOptionsAction));

        mb.add(File);
        mb.add(Graph);
        mb.add(View);
        mb.add(Options);

        //inialize "boolean" actions
        showTextPanelAction.actionPerformed(null);
        focusAction.actionPerformed(null);

        return mb;
    }

    /**
     * DOCUMENT ME!
     *
     * @param newMatchNumber DOCUMENT ME!
     */
    public void gotoMatchNumber(int newMatchNumber) {
        if ((newMatchNumber < 1) || (newMatchNumber > forest.getForestSize())) {
            toolkit.beep();

            //reset
            buttonPanel.setCurrentGraph(forest.getCurrentMatchNumber());
        } else if (newMatchNumber != forest.getCurrentMatchNumber()) {
            sentence = forest.gotoMatchNumber(newMatchNumber);
            updateMatchPanel();
            updateSentence();
            textPanel.updateText(sentence);
            buttonPanel.setCurrentGraph(newMatchNumber);
        }
    }

    /**
     * Speichert die User-Einstellungen in der Datei.
     */
    public void saveConfiguration() {
        Rectangle window = getAncestorFrame().getBounds();

        // Windows-Einstellungen sichern
        config.setWindowHeight(window.height);
        config.setWindowWidth(window.width);
        config.setWindowX(window.x);
        config.setWindowY(window.y);

        // Einstellungen aus GraphConstants holen
        config.setPanelBackgroundColor(GraphConstants.panelBackgroundColor);
        config.setPanelColor(GraphConstants.panelColor);
        config.setNodeBackgroundColor(GraphConstants.nodeBackgroundColor);
        config.setNodeColor(GraphConstants.nodeColor);
        config.setNodeMatchColor(GraphConstants.nodeMatchColor);
        config.setNodeMatchSubgraphColor(GraphConstants.nodeMatchSubgraphColor);
        config.setNodeHighlightedColor(GraphConstants.nodeHighlightedColor);
        config.setNodeImplodedColor(GraphConstants.nodeImplodedColor);
        config.setSecondaryEdgeColor(GraphConstants.secondaryEdgeColor);
        config.setLabelBackgroundColor(GraphConstants.labelBackgroundColor);
        config.setVirtualRootNodeColor(GraphConstants.virtualRootNodeColor);

        config.setLeafMaxWidth(GraphConstants.leafMaxWidth);

        config.setSecondaryEdgeDisplay(GraphConstants.secondaryEdgeDisplay);
        config.setVirtualRootNodeDisplay(GraphConstants.virtualRootNodeDisplay);

        config.setSuppressEdgeLabel(GraphConstants.suppressEdgeLabel);
        config.setSuppressFeatureValue(GraphConstants.suppressFeatureValue);

        // Abspeichern
        try {
            config.saveConfiguration(user_dir);
        } catch (Exception e) {
            logger.error("Error writing the forest viewer configuartion file: " +
                e.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void updateSentence() {
        if ((sentence == null) || sentence.isGraphCorrect()) {
            if (highlightedNodeIDs != null) {
                for (int i = 0; i < sentence.getDisplayLeafNodeSize(); i++) {
                    DisplayNode localNode = sentence.getDisplayLeafNode(i);
                    localNode.setHighlightedNode(highlightedNodeIDs.contains(
                            localNode.getNode().getID()));
                }
            }

            graphPanel.setSentence(sentence);
        } else {
            graphPanel.setSentence((DisplaySentence) null);
            JOptionPane.showMessageDialog(this,
                "The tree structure of this sentence is corrupted.\nNo tree will be drawn.",
                "Internal error", JOptionPane.ERROR_MESSAGE);
        }

        // Satz zeichnen
        graphPanel.repaint();
        updateSubMatchPanel();
    }

    /**
     * Uebergibt den neuen darzustellenden Wald. Gehe initial zu Satz 0.
     *
     * @param forest DOCUMENT ME!
     */
    public synchronized void visualizeForest(Forest forest) {
        this.forest = forest;

        // Focus on match
        iconBar.setActivateFocusOnMatch(forest.isForestWithMatches());
        focus.setEnabled(forest.isForestWithMatches());

        if (!forest.isForestWithMatches()) {
            iconBar.setFocusOnMatch(false);
            focus.setSelected(false);
            saveAction.putValue(Action.NAME, "Save");
        } else {
            saveAction.putValue(Action.NAME, "Save matches");
        }

        // Slider
        buttonPanel.setSliderEnabled(forest.useSliderForNavigation());

        // Anzahl aller Saetze
        if (forest.isForestSizeAccessible()) {
            buttonPanel.setForestSize(forest.getForestSize());
        }

        sentence = null;

        if (forest.getForestSize() > 0) {
            updateHeader(forest.getHeader());
            sentence = forest.getInitialMatch();
        }

        updateSentence();
        textPanel.updateText(sentence);

        updateMatchPanel();
        updateSubMatchPanel();
        buttonPanel.setCurrentGraph(forest.getCurrentMatchNumber());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected final JFrame getAncestorFrame() {
        Container parent = this;

        while (!((parent = parent.getParent()) instanceof JFrame))
            ;

        return (JFrame) parent;
    }

    /**
     * DOCUMENT ME!
     */
    protected void exit() {
        saveConfiguration();
        getAncestorFrame().dispose();
        System.exit(0);
    }

    /**
     * DOCUMENT ME!
     */
    protected void makeActions() {
        ImageLoader loader = new ImageLoader();
        Icon previousMatchIcon = new ImageIcon(loader.loadImage("Back16.gif"));
        Icon previousSubMatchIcon = new ImageIcon(loader.loadImage(
                    "StepBackGreen16.gif"));
        Icon nextMatchIcon = new ImageIcon(loader.loadImage("Forward16.gif"));
        Icon nextSubMatchIcon = new ImageIcon(loader.loadImage(
                    "StepForwardGreen16.gif"));

        firstMatchAction = new AbstractAction("First") {
                    public void actionPerformed(ActionEvent e) {
                        gotoFirstMatch();
                    }
                };
        firstMatchAction.putValue(Action.SHORT_DESCRIPTION,
            "First matching sentence");

        KeyStroke ks = KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0);
        firstMatchAction.putValue(Action.ACCELERATOR_KEY, ks);
        firstMatchAction.setEnabled(false);

        lastMatchAction = new AbstractAction("Last") {
                    public void actionPerformed(ActionEvent e) {
                        gotoLastMatch();
                    }
                };
        lastMatchAction.putValue(Action.SHORT_DESCRIPTION,
            "Last matching sentence");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_END, 0);
        lastMatchAction.putValue(Action.ACCELERATOR_KEY, ks);
        lastMatchAction.setEnabled(false);

        previousMatchAction = new AbstractAction("Previous", previousMatchIcon) {
                    public void actionPerformed(ActionEvent e) {
                        gotoPreviousMatch();
                    }
                };
        previousMatchAction.putValue(Action.SHORT_DESCRIPTION,
            "Previous matching sentence");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0);
        previousMatchAction.putValue(Action.ACCELERATOR_KEY, ks);
        previousMatchAction.setEnabled(false);

        nextMatchAction = new AbstractAction("Next", nextMatchIcon) {
                    public void actionPerformed(ActionEvent e) {
                        gotoNextMatch();
                    }
                };
        nextMatchAction.putValue(Action.SHORT_DESCRIPTION,
            "Next matching sentence");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0);
        nextMatchAction.putValue(Action.ACCELERATOR_KEY, ks);
        nextMatchAction.setEnabled(false);

        previousSubMatchAction = new AbstractAction("Previous",
                previousSubMatchIcon) {
                    public void actionPerformed(ActionEvent e) {
                        gotoPreviousSubMatch();
                    }
                };
        previousSubMatchAction.putValue(Action.SHORT_DESCRIPTION,
            "Previous subgraph");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_P, 0);
        previousSubMatchAction.putValue(Action.ACCELERATOR_KEY, ks);
        previousSubMatchAction.setEnabled(false);

        nextSubMatchAction = new AbstractAction("Next", nextSubMatchIcon) {
                    public void actionPerformed(ActionEvent e) {
                        gotoNextSubMatch();
                    }
                };
        nextSubMatchAction.putValue(Action.SHORT_DESCRIPTION, "Next subgraph");
        ks = KeyStroke.getKeyStroke(KeyEvent.VK_N, 0);
        nextSubMatchAction.putValue(Action.ACCELERATOR_KEY, ks);
        nextSubMatchAction.setEnabled(false);

        openAction = new AbstractAction("Open") {
                    public void actionPerformed(ActionEvent e) {
                        if (forest instanceof CorpusForest &&
                                ((CorpusForest) forest).isModified()) {
                            int option = JOptionPane.showConfirmDialog(GraphViewerContentPane.this,
                                    "The corpus has been modified. Do you want to open another one anyway?");

                            if (option == JOptionPane.NO_OPTION) {
                                return;
                            }
                        }

                        open();
                    }
                };

        closeAction = new AbstractAction("Close") {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);

                        //exit();
                    }
                };

        exitAction = new AbstractAction("Exit") {
                    public void actionPerformed(ActionEvent e) {
                        exit();
                    }
                };

        gotoMatchAction = new AbstractAction("Go to match") {
                    public void actionPerformed(ActionEvent e) {
                        String numberString = e.getActionCommand();
                        int number = -1;

                        if (numberString != null) {
                            number = Integer.parseInt(numberString);
                        }

                        gotoMatchNumber(number);
                    }
                };

        pageSetupAction = new AbstractAction("Page setup") {
                    public void actionPerformed(ActionEvent e) {
                        graphPanel.setPage();
                    }
                };

        printAction = new AbstractAction("Print") {
                    public void actionPerformed(ActionEvent e) {
                        graphPanel.print();
                        graphPanel.requestFocus();
                    }
                };

        readFeaturesAction = new AbstractAction("Open feature file") {
                    public void actionPerformed(ActionEvent e) {
                        openFeatureFile();
                    }
                };
        readFeaturesAction.putValue(Action.SHORT_DESCRIPTION,
            "Load features from file");

        exportAction = new AbstractAction("Export as image") {
                    public void actionPerformed(ActionEvent e) {
                        saveSVG();
                    }
                };

        saveAction = new AbstractAction("Save matches") {
                    public void actionPerformed(ActionEvent e) {
                        if (getValue(Action.NAME).equals("Save Matches")) {
                            saveMatches();
                        } else {
                            saveSentences();
                        }
                    }
                };

        saveForestAction = new AbstractAction("Save forest") {
                    public void actionPerformed(ActionEvent e) {
                        saveForest();
                    }
                };

        refreshAction = new AbstractAction("Refresh") {
                    public void actionPerformed(ActionEvent e) {
                        refresh();
                    }
                };

        showColorOptionsAction = new AbstractAction("Color options") {
                    public void actionPerformed(ActionEvent e) {
                        showColorOptions();
                    }
                };

        showDisplayOptionsAction = new AbstractAction("Display options") {
                    public void actionPerformed(ActionEvent e) {
                        showDisplayOptions();
                    }
                };

        focusAction = new AbstractAction("Focus on match") {
                    public void actionPerformed(ActionEvent e) {
                        boolean newState = (getValue("state") == null) ? false
                                                                       : (!((Boolean) getValue(
                                "state")).booleanValue());

                        if (sentence != null) {
                            iconBar.setFocusOnMatch(focusOnMatch);
                            graphPanel.normalizeHighlights();
                            graphPanel.setFocusOnMatch(focusOnMatch);

                            if (newState == true) {
                                sentence.focusOnMatch();
                            } else {
                                sentence.reverseFocusOnMatch();
                            }

                            sentence.init();
                            updateSentence();
                        }

                        focus.setSelected(newState);
                        iconBar.setFocusOnMatch(newState);
                        putValue("state", new Boolean(newState));
                    }
                };

        showTextPanelAction = new AbstractAction("Sentence as text") {
                    public void actionPerformed(ActionEvent e) {
                        boolean newState = (getValue("state") == null) ? true
                                                                       : (!((Boolean) getValue(
                                "state")).booleanValue());
                        text.setSelected(newState);
                        iconBar.setShowText(newState);
                        textPanel.setVisible(newState);
                        putValue("state", new Boolean(newState));
                    }
                };
    }

    /**
     * DOCUMENT ME!
     */
    protected void openFeatureFile() {
        fc.setDialogTitle("Read feature description file");
        fc.setFileFilter(FEATURE_FILE_FILTER);

        if (lastUsedFeatureURL != null) {
            fc.setSelectedFile(new File(lastUsedFeatureURL.getPath()));
        } else {
            fc.setCurrentDirectory(new File(user_dir));
            fc.setSelectedFile(new File(""));
        }

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = fc.getSelectedFile().getAbsolutePath();
            URL featureURL;

            if (forest instanceof CorpusForest) {
                Object corpus = ((CorpusForest) forest).getCorpus();

                if (corpus instanceof DefaultCorpus) {
                    try {
                        featureURL = new URL("file:" + fileName);
                        ((DefaultCorpus) corpus).readFeatureFile(featureURL);
                        graphPanel.repaint();
                        lastUsedFeatureURL = featureURL;
                    } catch (Exception e) {
                        logger.error("Could not read feature descriptions: " +
                            e.getMessage());
                        lastUsedFeatureURL = null;
                    }
                }
            } else {
                try {
                    featureURL = new URL("file:" + fileName);

                    if (readFeatureFile(featureURL, forest.getHeader())) {
                        lastUsedFeatureURL = featureURL;
                    } else {
                        lastUsedFeatureURL = null;
                    }
                } catch (MalformedURLException e) {
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected void saveSentences() {
        fc.setDialogTitle("Save corpus file");
        fc.setFileFilter(TIGER_FILE_FILTER);

        if (lastUsedCorpusURL != null) {
            fc.setSelectedFile(new File(lastUsedCorpusURL.getPath()));
        } else {
            fc.setCurrentDirectory(new File(user_dir));
        }

        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fc.getSelectedFile();

            if (file.getName().indexOf(".") == -1) {
                file = new File(file.toString() + ".tig");
            }

            if (file.exists()) {
                if (JOptionPane.showConfirmDialog(this,
                            "File " + file +
                            " exists. Do you want to overwrite it?", "",
                            JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                    return;
                }
            }

            try {
                ExportManager export = new ExportManager(((CorpusForest) forest).getCorpus(),
                        install_dir);
                export.setConfiguration(false, true, false, false, 2);
                export.saveMatchAsXML(null, file, true, true);
                ((CorpusForest) forest).setModified(false);

                try {
                    lastUsedCorpusURL = new URL("file:" +
                            file.getCanonicalPath());
                } catch (Exception e) {
                }

                /*        ExportDialog exportDialog = new ExportDialog(install_dir,
                                working_dir, getAncestorFrame());
                        exportDialog.update(((CorpusForest) forest).getCorpus(), null,
                            forest.getCurrentMatchNumber());
                        exportDialog.setVisible(true);*/
            } catch (Exception exp) {
                logger.error("Error saving sentences: " + exp.getMessage());
                JOptionPane.showMessageDialog(this,
                    "Error saving sentences:\n" + exp.getMessage(),
                    "Save error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void gotoFirstMatch() {
        gotoMatchNumber(1);
    }

    private void gotoLastMatch() {
        gotoMatchNumber(forest.getForestSize());
    }

    private void gotoNextMatch() {
        gotoMatchNumber(forest.getCurrentMatchNumber() + 1);
    }

    private void gotoNextSubMatch() {
        if (forest.isNextSubMatch()) {
            graphPanel.normalizeMatchHighlights();
            sentence = forest.nextSubMatch();
            updateSentence();
            textPanel.updateText(sentence);
        } else {
            toolkit.beep();
        }
    }

    private void gotoPreviousMatch() {
        gotoMatchNumber(forest.getCurrentMatchNumber() - 1);
    }

    private void gotoPreviousSubMatch() {
        if (forest.isPreviousSubMatch()) {
            graphPanel.normalizeMatchHighlights();
            sentence = forest.previousSubMatch();
            updateSentence();
            textPanel.updateText(sentence);
        } else {
            toolkit.beep();
        }
    }

    private void open() {
        fc.setDialogTitle("Open corpus file");
        fc.setFileFilter(TIGER_FILE_FILTER);

        if (lastUsedCorpusURL != null) {
            fc.setCurrentDirectory(new File(lastUsedCorpusURL.getPath()).getParentFile());
        } else {
            fc.setCurrentDirectory(new File(user_dir));
        }

        fc.setSelectedFile(new File(""));

        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String uri = fc.getSelectedFile().getAbsolutePath();

            try {
                if (forest instanceof CorpusForest) {
                    Object corpus = ((CorpusForest) forest).getCorpus();

                    if (corpus instanceof DefaultCorpus) {
                        try {
                            ((DefaultCorpus) corpus).readSyntaxCorpusFile(uri);

                            CorpusForest localForest = new CorpusForest((DefaultCorpus) corpus);
                            visualizeForest(localForest);
                            localForest.setModified(false);

                            try {
                                lastUsedCorpusURL = new URL("file:" + uri);
                            } catch (MalformedURLException e) {
                            }
                        } catch (IOException e) {
                            logger.error(e.getMessage());
                            JOptionPane.showMessageDialog(this,
                                "Could not read file " + uri + ". " +
                                e.getMessage(), "Error",
                                JOptionPane.ERROR_MESSAGE);
                        } catch (SAXException e) {
                            logger.error(e.getMessage());
                            JOptionPane.showMessageDialog(this,
                                "Error with parsing file " + uri +
                                ".\nFile probably corrupt. ", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else {
                    logger.error(
                        "Unknown implementation of Corpus interface used. No import possible.");
                }
            } catch (ForestException fe) {
                fe.printStackTrace();
            }
        }
    }

    private boolean readFeatureFile(URL url, Header localHeader) {
        try {
            DefaultCorpus.readFeatureFile(url, localHeader);
        } catch (Exception e) {
            logger.error(e.getMessage());
            JOptionPane.showMessageDialog(this,
                "Could not read feature descriptions: " + e.getMessage(),
                "Error", JOptionPane.WARNING_MESSAGE);

            return false;
        }

        return true;
    }

    private void refresh() {
        if (sentence != null) {
            graphPanel.normalizeHighlights();
            sentence.init();
        }

        updateSentence();
        textPanel.updateText(sentence);
        graphPanel.requestFocus();
    }

    private void saveForest() {
        if (forest.getForestSize() == 0) {
            JOptionPane.showMessageDialog(this, "Nothing to export!",
                "Semantic error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        try {
            if (exfodi == null) {
                exfodi = new ExportForestDialog(install_dir, working_dir,
                        getAncestorFrame());
            }

            exfodi.setForest(forest);
            exfodi.setLocationRelativeTo(this);
            exfodi.setVisible(true);
        } catch (IOException exp) {
            logger.error("Error initialising SVG export: " + exp.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error initialising SVG export:\n" + exp.getMessage(),
                "Export error", JOptionPane.ERROR_MESSAGE);
        }

        if (!exfodi.isSubmitted()) {
            return;
        }

        try {
            SVGExport.saveSVGForest(exfodi.getFilename(), forest, graphPanel,
                exfodi.getMatchingSentences(), exfodi.includeSubmatches(),
                exfodi.highlighMatches(), exfodi.displayMatchInformation(),
                exfodi.compressFile(), exfodi.useBackground());
        } catch (Exception exp) {
            logger.error("Error during SVG forest export: " + exp.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error during SVG forest export:\n" + exp.getMessage(),
                "Export error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveMatches() {
        if (forest.getForestSize() == 0) {
            return;
        }

        fc.setDialogTitle("Save matches");
        fc.setFileFilter(RESULT_FILE_FILTER);

        int returnVal = fc.showSaveDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String uri = fc.getSelectedFile().getAbsolutePath();

            if (!uri.endsWith("res")) {
                uri += ".res";
            }

            ((ResultForest) forest).exportMatchingTerminals(uri);
        }
    }

    private void saveSVG() {
        if (forest.getForestSize() == 0) {
            JOptionPane.showMessageDialog(this, "Nothing to export!",
                "Semantic error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        try {
            if (exdil == null) {
                exdil = new ExportImageDialog(install_dir, working_dir,
                        lastUsedCorpusURL, graphPanel, getAncestorFrame());
            }
        } catch (IOException exp) {
            logger.error("Error reading CSS configuration file: " +
                exp.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error reading CSS configuration file: \n" + exp.getMessage(),
                "Configuration error", JOptionPane.ERROR_MESSAGE);

            return;
        }

        try {
            exdil.setLocationRelativeTo(this);
            exdil.setVisible(true);
            refresh();
        } catch (Exception exp) {
            logger.error("Error initialising image export: " +
                exp.getMessage());
            JOptionPane.showMessageDialog(this,
                "Error during the export:\n" + exp.getMessage(),
                "Export error", JOptionPane.ERROR_MESSAGE);

            return;
        }
    }

    private void showColorOptions() {
        colop = new ColorOptions(config, getAncestorFrame());
        colop.setLocationRelativeTo(this);
        colop.setVisible(true);

        jsp.getViewport().setBackground(GraphConstants.panelBackgroundColor);
        graphPanel.setBackground(GraphConstants.panelBackgroundColor);
        graphPanel.setForeground(GraphConstants.panelColor);

        graphPanel.repaint();
        graphPanel.requestFocus();
    }

    private void showDisplayOptions() {
        displop = new DisplayOptions(header, config, getAncestorFrame());
        displop.setLocationRelativeTo(this);
        displop.setVisible(true);

        sentence = graphPanel.getCurrentDisplaySentence();
        updateSentence();
        textPanel.updateText(sentence);
        graphPanel.requestFocus();
    }

    /* Aktualisiert den Header-Zugriff im Panel. */
    private void updateHeader(Header header) {
        graphPanel.setHeader(header);
        textPanel.setHeader(header);
        this.header = header;
    }

    private void updateMatchPanel() {
        previousMatchAction.setEnabled(forest.getCurrentMatchNumber() > 1);
        nextMatchAction.setEnabled((forest.getCurrentMatchNumber() > 0) &&
            (forest.getCurrentMatchNumber() < forest.getForestSize()));

        firstMatchAction.setEnabled(forest.getCurrentMatchNumber() > 1);
        lastMatchAction.setEnabled((forest.getCurrentMatchNumber() > 0) &&
            (forest.getCurrentMatchNumber() < forest.getForestSize()));
    }

    private void updateSubMatchPanel() {
        // Aktueller Submatch
        if (forest.submatchesPossible()) {
            buttonPanel.setCurrentSubmatch(forest.getCurrentSubMatchNumber());
            buttonPanel.setSubmatchCount(forest.getSubMatchesSize());
        } else {
            buttonPanel.setSubmatchCount(-1);
        }

        if (forest.submatchesSumPossible()) {
            buttonPanel.setSubgraphCount(forest.getSubMatchesSum());
        } else {
            buttonPanel.setSubgraphCount(-1);
        }

        if (forest.submatchesPossible()) {
            if (forest.getSubMatchesSize() > 1) {
                nextSubMatchAction.setEnabled(true);
                previousSubMatchAction.setEnabled(true);
            } else {
                nextSubMatchAction.setEnabled(false);
                previousSubMatchAction.setEnabled(false);
            }
        }
    }
}
