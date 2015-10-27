/*
 * File:     EditCVPanel.java
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

package mpi.util.gui;

import mpi.util.BasicControlledVocabulary;
import mpi.util.CVEntry;
import mpi.util.ControlledVocabulary;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;


/**
 * DOCUMENT ME!
 * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
 * @author $Author: hasloe $
 * @version $Revision: 1.3 $
 */
public class EditCVPanel extends JPanel implements ActionListener,
    ListSelectionListener {
    /** Empty string to fill UI elements when values/description are empty. */
    private static final String EMPTY = "";

    /** Holds value of property DOCUMENT ME! */
    private static final int MOVE_BUTTON_SIZE = 24;

    /** Holds value of property DOCUMENT ME! */
    private static final int MINIMAL_ENTRY_PANEL_WIDTH = 240;

    /** Holds value of property DOCUMENT ME! */
    protected BasicControlledVocabulary cv;

    // internal caching fields

    /** Holds value of property DOCUMENT ME! */
    protected CVEntry currentEntry;

    /** Holds value of property DOCUMENT ME! */
    protected JButton addEntryButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton changeEntryButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton deleteEntryButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton moveDownButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton moveToBottomButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton moveToTopButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton moveUpButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton redoButton;

    /** Holds value of property DOCUMENT ME! */
    protected JButton undoButton;

    /** Holds value of property DOCUMENT ME! */
    protected JLabel entryDescLabel;

    /** Holds value of property DOCUMENT ME! */
    protected JLabel entryValueLabel;

    /** Holds value of property DOCUMENT ME! */
    protected JLabel titleLabel;

    /** Holds value of property DOCUMENT ME! */
    protected JList entryList;

    /** Holds value of property DOCUMENT ME! */
    protected JTextField entryDescTextField;

    /** Holds value of property DOCUMENT ME! */
    protected JTextField entryValueTextField;

    /** Holds value of property DOCUMENT ME! */
    protected String invalidValueMessage = "Invalid value";

    /** Holds value of property DOCUMENT ME! */
    protected String valueExistsMessage = "Value exists";
    private DefaultListModel entryListModel;

    // ui elements
    private UndoManager undoManager;

    /**
     * opens panel with no cv
     *
     */
    public EditCVPanel() {
        this(null);
    }

    /**
     * opens panel with cv
     * @param cv Controlled Vocabulary
     */
    public EditCVPanel(BasicControlledVocabulary cv) {
        undoManager = new UndoManager() {
                    public void undoableEditHappened(UndoableEditEvent e) {
                        super.undoableEditHappened(e);
                        updateUndoRedoButtons();
                    }
                };
        makeLayout();
        entryList.addListSelectionListener(this);
        setControlledVocabulary(cv);
    }

    /**
     * sets (new) cv
     * @param cv Controlled Vocabulary
     */
    public void setControlledVocabulary(BasicControlledVocabulary cv) {
        this.cv = cv;
        undoManager.discardAllEdits();
        updateLabels();
        resetViewer();
        entryValueTextField.setEnabled(cv != null);
        entryDescTextField.setEnabled(cv != null);

        if (cv instanceof ControlledVocabulary) {
            ((ControlledVocabulary) cv).addUndoableEditListener(undoManager);
            undoButton.setVisible(true);
            redoButton.setVisible(true);
        } else {
            undoButton.setVisible(false);
            redoButton.setVisible(false);
        }
    }

    /**
     * The button actions.
     *
     * @param actionEvent the actionEvent
     */
    public void actionPerformed(ActionEvent actionEvent) {
        Object source = actionEvent.getSource();

        // check source equality
        if (source == entryValueTextField) {
            entryDescTextField.requestFocus();
        } else if ((source == addEntryButton) ||
                (source == entryDescTextField)) {
            addEntry();
        } else if (source == changeEntryButton) {
            changeEntry();
        } else if (source == deleteEntryButton) {
            deleteEntries();
        } else if (source == moveToTopButton) {
            moveEntries(BasicControlledVocabulary.MOVE_TO_TOP);
        } else if (source == moveUpButton) {
            moveEntries(BasicControlledVocabulary.MOVE_UP);
        } else if (source == moveDownButton) {
            moveEntries(BasicControlledVocabulary.MOVE_DOWN);
        } else if (source == moveToBottomButton) {
            moveEntries(BasicControlledVocabulary.MOVE_TO_BOTTOM);
        } else if (source == undoButton) {
            undo();
        } else if (source == redoButton) {
            redo();
        }
    }

    /**
     * for test purposes. opens frame with this panel and a test controlled vocabulary
     * @param args no arguments
     */
    public static void main(String[] args) {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        ControlledVocabulary cv = new ControlledVocabulary("Name 1",
                "Description 1");
        cv.addEntry(new CVEntry("Entry 1", "Entry description 1"));
        cv.addEntry(new CVEntry("Entry 2", "Entry description 2"));
        cv.addEntry(new CVEntry("Entry 3", "Entry description 3"));

        JPanel p = new EditCVPanel(cv);
        frame.getContentPane().add(p);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * Handles a change in the selection in the entry list.
     *
     * @param lse the list selection event
     */
    public void valueChanged(ListSelectionEvent lse) {
        if (lse.getSource() == entryList) {
            updateEntryButtons();
            updateTextFields();
        }
    }

    /**
     * Adds an entry to the current CV. When checking the uniqueness of the
     * entry  value, values are compared case sensitive.
     */
    protected void addEntry() {
        if (cv == null) {
            return;
        }

        String entry = entryValueTextField.getText();

        entry = entry.trim();

        if (entry.length() == 0) {
            showWarningDialog(invalidValueMessage);

            return;
        }

        if (cv.containsValue(entry)) {
            showWarningDialog(valueExistsMessage);
        } else {
            String desc = entryDescTextField.getText();

            if (desc != null) {
                desc = desc.trim();
            }

            CVEntry newEntry = new CVEntry(entry, desc);
            cv.addEntry(newEntry);
            updateList();

            //make text fields free for next input!
            setSelectedEntry(null);
        }
    }

    /**
     * Changes the value and/or description of an existing entry. Checks
     * whether  the specified value is unique within the current
     * ControlledVocabulary.
     */
    protected void changeEntry() {
        if (cv == null) {
            return;
        }

        String newValue = entryValueTextField.getText().trim();

        if (newValue.length() == 0) {
            showWarningDialog(invalidValueMessage);
            entryValueTextField.setText((currentEntry != null)
                ? currentEntry.getValue() : "");

            return;
        }

        String newDescription = entryDescTextField.getText().trim();

        if (newValue.equals(currentEntry.getValue())) {
            if ((newDescription != null) &&
                    !newDescription.equals(currentEntry.getDescription())) {
                CVEntry newEntry = new CVEntry(newValue, newDescription);
                cv.replaceEntry(currentEntry, newEntry);
                updateList();
                setSelectedEntry(newEntry);
            }

            return;
        }

        // entry value has changed...
        if (cv.containsValue(newValue)) {
            showWarningDialog(valueExistsMessage);
        } else {
            CVEntry newEntry = new CVEntry(newValue, newDescription);
            cv.replaceEntry(currentEntry, new CVEntry(newValue, newDescription));
            updateList();
            setSelectedEntry(newEntry);
        }
    }

    /**
     * Deletes the selected entry/entries from the current
     * ControlledVocabulary.
     */
    protected void deleteEntries() {
        Object[] selEntries = entryList.getSelectedValues();

        if (selEntries.length == 0) {
            return;
        }

        CVEntry[] entries = new CVEntry[selEntries.length];

        for (int i = 0; i < entries.length; i++) {
            entries[i] = (CVEntry) selEntries[i];
        }

        cv.removeEntries(entries);
        updateList();
        setSelectedEntry(null);
    }

    /**
    * This method is called from within the constructor to initialize the
    * dialog's components.
    */
    protected void makeLayout() {
        JPanel moveEntriesPanel;

        GridBagConstraints gridBagConstraints;

        ImageIcon topIcon = new ImageIcon(this.getClass().getResource("/mpi/util/gui/resources/Top16.gif"));
        ImageIcon bottomIcon = new ImageIcon(this.getClass().getResource("/mpi/util/gui/resources/Bottom16.gif"));
        ImageIcon upIcon = new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/navigation/Up16.gif"));
        ImageIcon downIcon = new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/navigation/Down16.gif"));
        ImageIcon redoIcon = new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/general/Redo16.gif"));
        ImageIcon undoIcon = new ImageIcon(this.getClass().getResource("/toolbarButtonGraphics/general/Undo16.gif"));

        entryListModel = new DefaultListModel();
        entryList = new JList(entryListModel);
        entryValueLabel = new JLabel();
        entryValueTextField = new JTextField();
        addEntryButton = new JButton();
        addEntryButton.setEnabled(false);
        changeEntryButton = new JButton();
        changeEntryButton.setEnabled(false);
        deleteEntryButton = new JButton();
        deleteEntryButton.setEnabled(false);

        titleLabel = new JLabel();
        entryDescLabel = new JLabel();
        entryDescTextField = new JTextField();
        moveEntriesPanel = new JPanel();
        moveUpButton = new JButton(upIcon);
        moveUpButton.setEnabled(false);
        moveToTopButton = new JButton(topIcon);
        moveToTopButton.setEnabled(false);
        moveDownButton = new JButton(downIcon);
        moveDownButton.setEnabled(false);
        moveToBottomButton = new JButton(bottomIcon);
        moveToBottomButton.setEnabled(false);
        undoButton = new JButton(undoIcon);
        undoButton.setEnabled(false);
        redoButton = new JButton(redoIcon);
        redoButton.setEnabled(false);

        Dimension prefDim = new Dimension(MINIMAL_ENTRY_PANEL_WIDTH,
                MOVE_BUTTON_SIZE);
        Dimension buttonDimension = new Dimension(MOVE_BUTTON_SIZE,
                MOVE_BUTTON_SIZE);

        Insets insets = new Insets(2, 6, 2, 6);

        // entry sorting buttons
        moveEntriesPanel.setLayout(new GridBagLayout());

        moveToTopButton.addActionListener(this);
        moveToTopButton.setPreferredSize(buttonDimension);
        moveToTopButton.setMaximumSize(buttonDimension);
        moveToTopButton.setMinimumSize(buttonDimension);
        moveUpButton.addActionListener(this);
        moveUpButton.setPreferredSize(buttonDimension);
        moveUpButton.setMaximumSize(buttonDimension);
        moveUpButton.setMinimumSize(buttonDimension);
        moveDownButton.addActionListener(this);
        moveDownButton.setPreferredSize(buttonDimension);
        moveDownButton.setMaximumSize(buttonDimension);
        moveDownButton.setMinimumSize(buttonDimension);
        moveToBottomButton.addActionListener(this);
        moveToBottomButton.setPreferredSize(buttonDimension);
        moveToBottomButton.setMaximumSize(buttonDimension);
        moveToBottomButton.setMinimumSize(buttonDimension);
        undoButton.addActionListener(this);
        undoButton.setPreferredSize(buttonDimension);
        undoButton.setMaximumSize(buttonDimension);
        undoButton.setMinimumSize(buttonDimension);
        redoButton.addActionListener(this);
        redoButton.setPreferredSize(buttonDimension);
        redoButton.setMaximumSize(buttonDimension);
        redoButton.setMinimumSize(buttonDimension);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.insets = insets;
        moveEntriesPanel.add(moveToTopButton, gridBagConstraints);
        moveEntriesPanel.add(moveUpButton, gridBagConstraints);
        moveEntriesPanel.add(moveDownButton, gridBagConstraints);
        moveEntriesPanel.add(moveToBottomButton, gridBagConstraints);
        moveEntriesPanel.add(undoButton, gridBagConstraints);
        moveEntriesPanel.add(redoButton, gridBagConstraints);

        //other subcomponents        
        JScrollPane entryPane = new JScrollPane(entryList);
        entryPane.setPreferredSize(prefDim);
        entryPane.setMinimumSize(prefDim);

        entryValueTextField.setPreferredSize(prefDim);
        entryValueTextField.setMinimumSize(prefDim);

        entryDescTextField.setPreferredSize(prefDim);
        entryDescTextField.setMinimumSize(prefDim);

        //main layout
        setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = insets;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        add(entryPane, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = insets;
        add(entryValueLabel, gridBagConstraints);

        gridBagConstraints.gridy = GridBagConstraints.RELATIVE;
        add(entryValueTextField, gridBagConstraints);
        add(entryDescLabel, gridBagConstraints);
        add(entryDescTextField, gridBagConstraints);
        add(addEntryButton, gridBagConstraints);
        add(changeEntryButton, gridBagConstraints);
        add(deleteEntryButton, gridBagConstraints);
        add(moveEntriesPanel, gridBagConstraints);

        undoButton.setToolTipText(undoManager.getUndoPresentationName());

        entryValueTextField.addActionListener(this);
        entryDescTextField.addActionListener(this);
        addEntryButton.addActionListener(this);
        changeEntryButton.addActionListener(this);
        deleteEntryButton.addActionListener(this);
    }

    /**
     * Updates some UI fields after a change in the selected CV.
     *
     */
    protected void resetViewer() {
        // reset some fields
        entryListModel.clear();

        if (cv != null) {
            CVEntry[] entries = cv.getEntries();
            currentEntry = null;

            for (int i = 0; i < entries.length; i++) {
                entryListModel.addElement(entries[i]);

                if (i == 0) {
                    entryList.setSelectedIndex(0);
                    currentEntry = entries[0];
                }
            }

            addEntryButton.setEnabled(true);
        } else {
            cv = null;
            addEntryButton.setEnabled(false);
        }

        updateEntryButtons();
        updateTextFields();
    }

    /**
     * Since this dialog is meant to be modal a Locale change while this dialog
     * is open  is not supposed to happen. This will set the labels etc. using
     * the current locale  strings.
     */
    protected void updateLabels() {
        moveToTopButton.setToolTipText("Top");
        moveUpButton.setToolTipText("Up");
        moveDownButton.setToolTipText("Down");
        moveToBottomButton.setToolTipText("Bottom");
        deleteEntryButton.setText("Delete");
        changeEntryButton.setText("Change");
        addEntryButton.setText("Add");
        entryDescLabel.setText("Description");
        entryValueLabel.setText("Value");
        setBorder(new TitledBorder("Entries"));
        undoButton.setToolTipText(undoManager.getUndoPresentationName());
        redoButton.setToolTipText(undoManager.getRedoPresentationName());
    }

    /**
     * Reextracts the entries from the current CV after an add, change or
     * delete entry operation on the CV.
     *
     */
    protected void updateList() {
        if (cv != null) {
            entryList.removeListSelectionListener(this);
            entryListModel.clear();

            CVEntry[] entries = cv.getEntries();

            for (int i = 0; i < entries.length; i++) {
                entryListModel.addElement(entries[i]);
            }

            entryList.addListSelectionListener(this);
        }
    }

    private void setSelectedEntries(CVEntry[] entries) {
        currentEntry = null;

        if ((entries != null) && (entries.length > 0)) {
            entryList.removeListSelectionListener(this);

            for (int i = 0; i < entryListModel.getSize(); i++) {
                for (int j = 0; j < entries.length; j++) {
                    if (entryListModel.getElementAt(i).equals(entries[j])) {
                        entryList.addSelectionInterval(i, i);
                    }
                }
            }

            entryList.addListSelectionListener(this);
        }

        updateEntryButtons();
        updateTextFields();
    }

    private void setSelectedEntry(CVEntry entry) {
        if (entry != null) {
            setSelectedEntries(new CVEntry[] { entry });
        } else {
            setSelectedEntries(null);
        }
    }

    /**
     * Moves the selected entries to the bottom of the entry list.
     * @param the type of the move (up, down, etc.) as defined in BasicControlledVocabulary
     */
    private void moveEntries(int moveType) {
        if (cv == null) {
            return;
        }

        Object[] selEntries = entryList.getSelectedValues();

        if (selEntries.length == 0) {
            return;
        }

        CVEntry[] entriesToBeMoved = new CVEntry[selEntries.length];

        for (int i = 0; i < selEntries.length; i++) {
            entriesToBeMoved[i] = (CVEntry) selEntries[i];
        }

        cv.moveEntries(entriesToBeMoved, moveType);
        updateList();
        setSelectedEntries(entriesToBeMoved);
    }

    /**
     * Invokes the redo method of the <code>UndoManager</code>.
     */
    private void redo() {
        try {
            undoManager.redo();

            updateList();
            setSelectedEntry(null);
        } catch (CannotRedoException cre) {
            //LOG.warning(LogUtil.formatStackTrace(cre));
        }

        updateUndoRedoButtons();
    }

    /**
     * Shows a warning/error dialog with the specified message string.
     *
     * @param message the message to display
     */
    private void showWarningDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Warning",
            JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Invokes the undo method of the <code>UndoManager</code>.
     */
    private void undo() {
        try {
            undoManager.undo();

            updateList();
            setSelectedEntry(null);
        } catch (CannotUndoException cue) {
            // LOG.warning(LogUtil.formatStackTrace(cue));
        }

        updateUndoRedoButtons();
    }

    /**
    * Enables or disables buttons depending on the selected entries.
    */
    private void updateEntryButtons() {
        if ((entryList == null) || (entryList.getSelectedIndex() == -1)) {
            changeEntryButton.setEnabled(false);
            deleteEntryButton.setEnabled(false);
            moveToTopButton.setEnabled(false);
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
            moveToBottomButton.setEnabled(false);
            currentEntry = null;
        } else {
            int firstIndex = entryList.getSelectedIndices()[0];
            int numSelected = entryList.getSelectedIndices().length;
            int lastIndex = entryList.getSelectedIndices()[numSelected - 1];
            changeEntryButton.setEnabled(true);
            deleteEntryButton.setEnabled(true);

            if (firstIndex > 0) {
                moveToTopButton.setEnabled(true);
                moveUpButton.setEnabled(true);
            } else {
                moveToTopButton.setEnabled(false);
                moveUpButton.setEnabled(false);
            }

            if (lastIndex < (entryList.getModel().getSize() - 1)) {
                moveDownButton.setEnabled(true);
                moveToBottomButton.setEnabled(true);
            } else {
                moveDownButton.setEnabled(false);
                moveToBottomButton.setEnabled(false);
            }

            currentEntry = (CVEntry) entryList.getSelectedValue();
        }
    }

    private void updateTextFields() {
        if (entryList.getSelectedIndex() == -1) {
            entryValueTextField.setText(EMPTY);
            entryDescTextField.setText(EMPTY);
        } else {
            //put the first selected entry into text fields
            CVEntry selEntry = (CVEntry) entryList.getSelectedValue();
            entryValueTextField.setText(selEntry.getValue());
            entryDescTextField.setText(selEntry.getDescription());
        }

        if (entryValueTextField.isEnabled()) {
            entryValueTextField.requestFocus();
        }
    }

    /**
     * Enables or disables the undo/redo buttons.
     */
    private void updateUndoRedoButtons() {
        undoButton.setEnabled(undoManager.canUndo());
        redoButton.setEnabled(undoManager.canRedo());
    }
}
