/*
 * File:     ControlledVocabulary.java
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

package mpi.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;


/**
 * this class adds undo/redo functionality to the super class
 *
 * @author klasal $Id: ControlledVocabulary.java,v 1.5 2006/08/17 13:57:30 klasal Exp $
 *
 */
public class ControlledVocabulary extends BasicControlledVocabulary {
    /** Holds value of property DOCUMENT ME! */
    private final List undoableEditListeners = new ArrayList();

    /**
     *
     * @param name name of this cv
     */
    public ControlledVocabulary(String name) {
        super(name);
    }

    /**
     *
     * @param name name of this cv
     * @param description description of this cv
     */
    public ControlledVocabulary(String name, String description) {
        super(name, description);
    }

    /**
     * @param entry entry to be added
     * @return true if action was completed successfully
     */
    public boolean addEntry(CVEntry entry) {
        boolean b = super.addEntry(entry);

        if (b) {
            fireUndoableEditUpdate(new UndoableEditEvent(this,
                    new UndoableCVEntryAdd(entry)));
        }

        return b;
    }

    /**
     * Add listener
     * @param l listener
     */
    public void addUndoableEditListener(UndoableEditListener l) {
        if (!undoableEditListeners.contains(l)) {
            undoableEditListeners.add(l);
        }
    }

    /**
     * moves array of entries in a direction specified by moveType
     * @param entriesToBeMoved array of entries to be moved
     * @param moveType direction of the move
     */
    public void moveEntries(CVEntry[] entriesToBeMoved, int moveType) {
        CVEntry[] oldEntries = getEntries();
        super.moveEntries(entriesToBeMoved, moveType);
        fireUndoableEditUpdate(new UndoableEditEvent(this,
                new UndoableCVGlobalChange(oldEntries, "Move entries")));
    }

    /**
     * @param entriesToBeRemoved list of entries
     * @return true if action was completed successfully
     */
    public boolean removeEntries(CVEntry[] entriesToBeRemoved) {
        CVEntry[] oldEntries = getEntries();
        boolean b = super.removeEntries(entriesToBeRemoved);

        if (b) {
            fireUndoableEditUpdate(new UndoableEditEvent(this,
                    new UndoableCVGlobalChange(oldEntries, "Delete entries")));
        }

        return b;
    }

    /**
     * remove listener
     * @param l listener
     */
    public void removeUndoableEditListener(UndoableEditListener l) {
        if (undoableEditListeners.contains(l)) {
            undoableEditListeners.remove(l);
        }
    }

    /**
     * notifies listeners
     * @param e event
     */
    protected void fireUndoableEditUpdate(UndoableEditEvent e) {
        for (int i = undoableEditListeners.size() - 1; i >= 0; i--) {
            ((UndoableEditListener) undoableEditListeners.get(i)).undoableEditHappened(e);
        }
    }

    /**
     * class representing an addition of an event
     * @author klasal
     *
     */
    class UndoableCVEntryAdd extends AbstractUndoableEdit {
        /** Holds value of property DOCUMENT ME! */
        private final CVEntry entry;

        /**
         *
         * @param entry added CVentry
         */
        UndoableCVEntryAdd(CVEntry entry) {
            this.entry = entry;
        }

        /**
         *
         * @return name of the event
         */
        public String getRepresentationName() {
            return "add Entry";
        }

        /**
         * The actual redo action.
         *
         */
        public void redo() {
            super.redo();
            entries.add(entry);
        }

        /**
        * The actual undo action.
        *
        */
        public void undo() {
            super.undo();
            entries.remove(entry);
        }
    }

    /**
     *
     * @author klasal
     *
     */
    class UndoableCVEntryReplace extends AbstractUndoableEdit {
        /** Holds value of property DOCUMENT ME! */
        private final CVEntry oldEntry;
        private CVEntry newEntry;

        /**
         *
         * @param oldEntry entry to be replaced
         * @param newEntry new entry
         */
        UndoableCVEntryReplace(CVEntry oldEntry, CVEntry newEntry) {
            this.oldEntry = oldEntry;
            this.newEntry = newEntry;
        }

        /**
         *
         * @return name of the event
         */
        public String getRepresentationName() {
            return "change Entry";
        }

        /**
         * The actual redo action.
         *
         */
        public void redo() {
            super.redo();

            int index = entries.indexOf(oldEntry);
            entries.remove(index);
            entries.add(index, newEntry);
        }

        /**
        * The actual undo action.
        *
        */
        public void undo() {
            super.undo();

            int index = entries.indexOf(newEntry);
            entries.remove(index);
            entries.add(index, oldEntry);
        }
    }

    /**
     * unspecific change of more than one CVentry
     * @author klasal
     *
     */
    class UndoableCVGlobalChange extends AbstractUndoableEdit {
        /** Holds value of property DOCUMENT ME! */
        private final CVEntry[] oldEntries;
        private String representationName;
        private CVEntry[] newEntries;

        /**
         *
         * @param entries old entries
         * @param representationName name of the event
         */
        UndoableCVGlobalChange(CVEntry[] entries, String representationName) {
            this.oldEntries = entries;
            this.representationName = representationName;
        }

        /**
         *
         * @return name of the event
         */
        public String getRepresentationName() {
            return representationName;
        }

        /**
         * The actual redo action.
         *
         */
        public void redo() {
            super.redo();
            entries.clear();

            for (int i = 0; i < newEntries.length; i++) {
                entries.add(newEntries[i]);
            }
        }

        /**
        * The actual undo action.
        *
        */
        public void undo() {
            super.undo();
            newEntries = getEntries();
            entries.clear();

            for (int i = 0; i < oldEntries.length; i++) {
                entries.add(oldEntries[i]);
            }
        }
    }
}
