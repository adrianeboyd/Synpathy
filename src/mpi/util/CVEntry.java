/*
 * File:     CVEntry.java
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

import java.io.Serializable;


/**
 * $Id: CVEntry.java,v 1.6 2006/08/29 08:49:55 klasal Exp $
 * An entry in a ContolledVocabulary.<br>
 * An entry has a value and an optinal description.  Pending: the entries'
 * value in a controlled vocabulary should be unique. We could override the
 * equals(o) method of <code>Object</code> to return
 * this.value.equals(((CVEntry)o).getValue()). This however would not be
 * consistent with hashCode().
 *
 */
public class CVEntry implements Comparable, Serializable {
    /** Holds value of property DOCUMENT ME! */
    private final String value;
    private String description;

    /**
     * Creates a new entry with the specified value.
     *
     * @param value the value
     *
     * @see #CVEntry(String,String)
     */
    public CVEntry(String value) {
        this(value, null);
    }

    /**
     * Creates a new entry with the specified value and the specified
     * description.
     *
     * @param value the value
     * @param description the description
     *
     */
    public CVEntry(String value, String description) {
        if (value == null) {
            throw new IllegalArgumentException("The value can not be null.");
        }

        this.value = value;
        this.description = description;
    }

    /**
     * Sets the description of this entry.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the description.
     *
     * @return the description or null
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * implementation of the comparable interface.
     *
     * @param o the object this class is compared to
     * @return compareTo of 'value's, or, if they are equal, compareTo of 'description's
     */
    public int compareTo(Object o) {
        CVEntry other = (CVEntry) o;

        int compare = this.getValue().compareTo(other.getValue());

        if (compare == 0) {
            compare = this.getDescription().compareTo(other.getDescription());
        }

        return compare;
    }

    /**
     * Overrides <code>Object</code>'s equals method by checking if value and
     * description of the two objects are equal.
     *
     * Note, that also subclasses of this class might be equal to this class!!!
     *
     * @param obj the reference object with which to compare
     *
     * @return true if this object is the same as the obj argument; false
     *         otherwise
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof CVEntry)) {
            return false;
        }

        // check the fields
        CVEntry other = (CVEntry) obj;

        if (value.equals(other.getValue())) {
            if ((description == null) && (other.getDescription() == null)) {
                return true;
            }

            //note that still one of the descriptions may be null!
            return (description != null) &&
            description.equals(other.getDescription());
        }

        return false;
    }

    /**
     * returns hashCode of 'value'.
     *
     * (note that it is not necessary to return different hashcodes if objects are not equal;
     * including the field 'description' would cause problems since it is mutable)
     *
     * @return hashCode
     */
    public int hashCode() {
        return value.hashCode();
    }

    /**
     * Overrides <code>Object</code>'s toString() method to just return  the
     * value of this entry.<br>
     * This way this object can easily be used directly in Lists, ComboBoxes
     * etc.
     *
     * @return the value
     */
    public String toString() {
        return value;
    }
}
