/*
 * File:     RegExpToolbox.java
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
package ims.tiger.util;

import org.apache.oro.text.perl.MalformedPerl5PatternException;
import org.apache.oro.text.perl.Perl5Util;

import java.util.ArrayList;
import java.util.List;


/**
 * Sammlung von RegularExpression-Tools: Match, Substitute, Split. Die Verwendung erfolgt analog Perl.
 * Die Objekt-Schnittstelle bindet externe Bibliotheken ein und wurde lediglich aus Gruenden der
 * Austauschbarkeit realisiert.<P>
 *
 * Zur Zeit wird die Bibliothek Perl5Util (Version 1.2) der Firma <A HREF="http://www.oroinc.com">Oro</A> verwendet.
 * Diese Bibliothek ist fuer nicht-kommerzielle Zwecke frei verfuegbar.
 */
public class RegExpToolbox {
    private Perl5Util perl;

    /** Konstruktor initialisiert das Perl5Util-Objekt. */
    public RegExpToolbox() {
        perl = new Perl5Util();
    }

    /** Substitute-Funktion analog Perl. Liefert den ersetzten String zurueck. */
    public final String substitute(String pattern, String input)
        throws RegExpException {
        try {
            return perl.substitute(pattern, input);
        } catch (MalformedPerl5PatternException e) {
            throw new RegExpException("Regular expression " + pattern +
                " is malformed.");
        }
    }

    /** Split-Funktion analog Perl. Liefert eine Liste der Bestandteile zurueck. */
    public final List split(String pattern, String input)
        throws RegExpException {
        try {
            List result = new ArrayList();
            perl.split(result, pattern, input);

            return result;
        }
         catch (MalformedPerl5PatternException e) {
            throw new RegExpException("Regular expression " + pattern +
                " is malformed.");
        }
    }

    /** Bestimmt lediglich, ob ein Match vorliegt oder nicht. */
    public final boolean matches(String pattern, String input)
        throws RegExpException {
        try {
            return perl.match(pattern, input);
        } catch (MalformedPerl5PatternException e) {
            throw new RegExpException("Regular expression " + pattern +
                " is malformed.");
        }
    }

    /** Bestimmt den Match unabhaengig davon, ob ein Match vorliegt oder nicht. */
    public final void match(String pattern, String input)
        throws RegExpException {
        try {
            boolean dummy = perl.match(pattern, input);
        } catch (MalformedPerl5PatternException e) {
            throw new RegExpException("Regular expression " + pattern +
                " is malformed.");
        }
    }

    /** Liefert den Match zurueck. */
    public final String getMatch() {
        return perl.toString();
    }

    /** Liefert String links vom Match zurueck. */
    public final String getPreMatch() {
        return perl.preMatch();
    }

    /** Liefert String rechts vom Match zurueck. */
    public final String getPostMatch() {
        return perl.postMatch();
    }
}
