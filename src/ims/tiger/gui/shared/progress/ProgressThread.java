/*
 * File:     ProgressThread.java
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
package ims.tiger.gui.shared.progress;


/** Diese Implementation setzt die Architektur in Form eines Threads
 *  um, d.h. sie stoesst einen Thread an, der die Aufgabe verwaltet.
 */
public class ProgressThread extends Thread implements ProgressContainerInterface {
    private ProgressWindow user;
    private ProgressTaskInterface task;

    /** Der Konstruktor verkettet das innere Objekt (Aufgabe), den umgebende
     *  Container (dieser Thread) und die aeussere Zugriffsschicht (User-GUI).
     */
    public ProgressThread(ProgressWindow user, ProgressTaskInterface task) {
        this.user = user;
        this.task = task;
        task.setContainer(this);
    }

    /** Startet die Aufgabe. Meldet an die GUI Beendigung des Prozesses. */
    public void run() {
        // Aufgabe starten
        task.startTask();

        // Warte, bis Fenster auch wirklich sichtbar ist
        while (!user.isVisible()) {
            try {
                Thread.sleep(250);
            } catch (Exception e) {
            }
        }

        try {
            Thread.sleep(250);
        } catch (Exception e) {
        }

        // Teile Ende dem Fenster mit
        user.finished();
    }

    /** Gibt nach innen die Information, ob aussen Abbruch angeordnet worden ist. */
    public boolean isAborted() {
        return user.isStopped();
    }

    /** Besetzte Statusmeldung 1. */
    public void setMessage(String message) {
        setMessage1(message);
    }

    /** Besetzte Statusmeldung 1. */
    public void setMessage1(String message) {
        user.setMessage1(message);
    }

    /** Besetzte Statusmeldung 2. */
    public void setMessage2(String message) {
        user.setMessage2(message);
    }

    /** Besetzte den ProgressBar (0-100). */
    public void setProgressValue(int value) {
        user.setProgress(value);
    }
}
