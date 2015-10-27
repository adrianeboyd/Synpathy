/*
 * File:     EditFeatureListDialog.java
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

import ims.tiger.corpus.Feature;
import ims.tiger.corpus.Header;

import mpi.util.BasicControlledVocabulary;
import mpi.util.CVEntry;
import mpi.util.ControlledVocabulary;

import mpi.util.gui.AbstractEditCVDialog;

import java.awt.Frame;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.border.TitledBorder;


/**
 * $Id: EditFeatureListDialog.java,v 1.9 2006/08/23 09:49:58 klasal Exp $ The
 * Edit Feature List dialog is a dialog for defining and changing features and
 * their entries.<br>
 *
 * @author klasal
 */
public class EditFeatureListDialog extends AbstractEditCVDialog {
    /**
     * terminal node feature
     */
    public static final int TERMINAL_NODE_FEATURE = 0;

    /**
     * nonterminal node feature
     */
    public static final int NONTERMINAL_NODE_FEATURE = 1;

    /**
     * edge feature
     */
    public static final int EDGE_FEATURE = 2;

    /**
     * secondary edge feature
     */
    public static final int SECEDGE_FEATURE = 3;

    /** Holds value of property DOCUMENT ME! */
    private static final int MINIMUM_HEIGHT_SINGLE_FEATURE = 300;

    /** Holds value of property DOCUMENT ME! */
    private static final String[] TITLES = new String[] {
            "Terminal node features", "Non-terminal node feature", "Edge labels",
            "Secondary edge labels"
        };

    /** Holds value of property DOCUMENT ME! */
    private final Header header;

    /** Holds value of property DOCUMENT ME! */
    private final int featureCategory;

    /**
     * Creates a new EditCVDialog.
     *
     * @param parent parent frame
     * @param header header
     * @param featureCategory one of terminal, nonterminal, edge, secondary edge
     */
    public EditFeatureListDialog(Frame parent, Header header,
        int featureCategory) {
        super(parent, true, featureCategory == TERMINAL_NODE_FEATURE);
        this.header = header;
        this.featureCategory = featureCategory;

        if (featureCategory != TERMINAL_NODE_FEATURE) {
            minimumHeight = MINIMUM_HEIGHT_SINGLE_FEATURE;
        }

        updateLabels();
        setPosition();
        updateComboBox();
        cvNameTextField.requestFocus();
    }

    /**
     * get features out of header and transform them into CVs
     *
     * @return DOCUMENT ME!
     */
    protected List getCVList() {
        List cvList = new ArrayList();

        if (featureCategory == EDGE_FEATURE) {
            cvList.add(feature2CV(header.getEdgeFeature()));
        } else if (featureCategory == SECEDGE_FEATURE) {
            cvList.add(feature2CV(header.getSecEdgeFeature()));
        } else {
            List featureNames;

            if (featureCategory == TERMINAL_NODE_FEATURE) {
                featureNames = header.getAllTFeatureNames();
                featureNames.remove("word");
            } else {
                featureNames = header.getAllNTFeatureNames();
            }

            for (int i = 0; i < featureNames.size(); i++) {
                cvList.add(feature2CV(header.getFeature(
                            (String) featureNames.get(i))));
            }
        }

        return cvList;
    }

    /**
     * update header after add
     *
     * @param name DOCUMENT ME!
     */
    protected void addCV(String name) {
        super.addCV(name);

        List features = (featureCategory == TERMINAL_NODE_FEATURE)
            ? header.getAllTerminalFeatures() : header.getAllNonterminalFeatures();
        features.add(new Feature(name));
    }

    /**
     * update header after change
     *
     * @param cv DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param description DOCUMENT ME!
     */
    protected void changeCV(ControlledVocabulary cv, String name,
        String description) {
        super.changeCV(cv, name, description);

        if (name != null) {
            List features = (featureCategory == TERMINAL_NODE_FEATURE)
                ? header.getAllTerminalFeatures()
                : header.getAllNonterminalFeatures();

            int i = -1;

            do {
                i++;
            } while (!((Feature) features.get(i)).getName().equals(cv.getName()));

            ((Feature) features.get(i)).setName(name);
        }
    }

    /**
     * update header after delete
     *
     * @param cv DOCUMENT ME!
     */
    protected void deleteCV(ControlledVocabulary cv) {
        super.deleteCV(cv);

        List features = (featureCategory == TERMINAL_NODE_FEATURE)
            ? header.getAllTerminalFeatures() : header.getAllNonterminalFeatures();

        int i = -1;

        do {
            i++;
        } while (!((Feature) features.get(i)).getName().equals(cv.getName()));

        features.remove(i);
    }

    /**
     * features have no descriptions, hide corresponding gui elements
     */
    protected void makeLayout() {
        super.makeLayout();
        cvDescLabel.setVisible(false);
        cvDescArea.setVisible(false);
    }

    /**
     * DOCUMENT ME!
     */
    protected void updateLabels() {
        super.updateLabels();
        cvPanel.setBorder(new TitledBorder(TITLES[featureCategory]));
        titleLabel.setText("Edit " + TITLES[featureCategory]);
        setTitle("Edit " + TITLES[featureCategory]);
    }

    /**
     * transforms a feature into CV
     *
     * @param feature
     *
     * @return
     */
    private ControlledVocabulary feature2CV(Feature feature) {
        ControlledVocabulary cv = new ControlledVocabulary(feature.getName(), "") {
                public void handleModified() {
                    updateHeader(this);
                }
            };

        cv.setInitMode(true);

        List items = feature.getItems();

        for (int i = 0; i < items.size(); i++) {
            String item = (String) items.get(i);
            cv.addEntry(new CVEntry(item, feature.getDescription(item)));
        }

        cv.setInitMode(false);

        return cv;
    }

    /**
     * transforms ControlledVocabulary into feature
     *
     * @param cv
     */
    private void updateHeader(BasicControlledVocabulary cv) {
        String cvName = cv.getName();

        Feature newFeature = new Feature(cvName);

        CVEntry[] entries = cv.getEntries();

        for (int j = 0; j < entries.length; j++) {
            newFeature.addItem(entries[j].getValue(),
                entries[j].getDescription());
        }

        if (featureCategory == EDGE_FEATURE) {
            header.setEdgeFeature(newFeature);

            return;
        }

        if (featureCategory == SECEDGE_FEATURE) {
            header.setSecEdgeFeature(newFeature);

            return;
        }

        List features = (featureCategory == TERMINAL_NODE_FEATURE)
            ? header.getAllTerminalFeatures() : header.getAllNonterminalFeatures();

        int featureNr = -1;

        do {
            featureNr++;
        } while (!((Feature) features.get(featureNr)).getName().equals(oldCVName));

        features.set(featureNr, newFeature);

        SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = dateFmt.format(Calendar.getInstance().getTime());

        header.setCorpus_Date(dateString);
    }
}
