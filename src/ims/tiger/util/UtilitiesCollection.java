/*
 * File:     UtilitiesCollection.java
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

import org.apache.log4j.Logger;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import java.io.*;

import java.net.URL;


/**
 * DOCUMENT ME!
 * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
 * @author $Author: hasloe $
 * @version $Revision: 1.3 $
 */
public class UtilitiesCollection {
    /** Holds value of property DOCUMENT ME! */
    public static Logger logger = Logger.getLogger(UtilitiesCollection.class);

    /** This method makes life a bit easier when generating HTML content.
      * It converts special characters to HTML encoding. */
    public static String trimHTMLContent(String str) {
        if (str == null) {
            return null;
        }

        StringBuffer retval = new StringBuffer();
        char ch;

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
            case '\"':
                retval.append("&quot;");

                continue;

            /*           case '\'':
                          retval.append("&apos;");
                          continue;
            */
            case '<':
                retval.append("&lt;");

                continue;

            case '>':
                retval.append("&gt;");

                continue;

            case '&':
                retval.append("&amp;");

                continue;

            default:
                retval.append(str.charAt(i));

                continue;
            }
        }

        return retval.toString();
    }

    /** This method makes life a bit easier when generating XML elements.
      * It converts special characters to XML unicode encoding. */
    public static String trimContent(String str) {
        if (str == null) {
            return null;
        }

        StringBuffer retval = new StringBuffer();
        char ch;

        for (int i = 0; i < str.length(); i++) {
            switch (str.charAt(i)) {
            case '\"':
                retval.append("&quot;");

                continue;

            case '\'':
                retval.append("&apos;");

                continue;

            case '<':
                retval.append("&lt;");

                continue;

            case '>':
                retval.append("&gt;");

                continue;

            case '&':
                retval.append("&amp;");

                continue;

            default:

                if (((ch = str.charAt(i)) < 0x20) || (ch > 0x7e)) {
                    String s = "0000" + Integer.toString(ch, 16);
                    retval.append("&#x" +
                        s.substring(s.length() - 4, s.length()) + ";");
                } else {
                    retval.append(ch);
                }

                continue;
            }
        }

        return retval.toString();
    }

    /** This method makes life a bit easier when generating XML comments.
      * It converts special characters to XML unicode encoding. */
    public static String trimComment(String comment) {
        // -- ersetzen
        while (comment.indexOf("--") >= 0) {
            int pos = comment.indexOf("--");
            comment = comment.substring(0, pos) + "-\\-" +
                comment.substring(pos + 2, comment.length());
        }

        return comment;
    }

    /** Konvertierung byte -> integer ohne Datenverlust */
    public static final int byte2int(byte b) {
        int wert = (int) b;

        if (wert < -1) {
            wert += 256;
        }

        return wert;
    }

    /** Konvertierung short -> integer ohne Datenverlust */
    public static final int short2int(short s) {
        int wert = (int) s;

        if (wert < -1) {
            wert += 65536;
        }

        return wert;
    }

    /** Konvertierung: Ausloeschen von Quotes \ */
    public static final String deleteQuotes(String input) {
        StringBuffer buf = new StringBuffer(input);

        int i = 0;

        while (i < buf.length()) {
            if (buf.charAt(i) == '\\') {
                buf.deleteCharAt(i);
            } else {
                i++;
            }
        }

        return buf.toString();
    }

    /** Herstellen einer URI */
    public static String createURI(String filename) throws Exception {
        File file = new File(filename);
        String path = file.getAbsolutePath();
        String fSep = System.getProperty("file.separator");

        if ((fSep != null) && (fSep.length() == 1)) {
            path = path.replace(fSep.charAt(0), '/');
        }

        if ((path.length() > 0) && (path.charAt(0) != '/')) {
            path = '/' + path;
        }

        try {
            return new URL("file", null, path).toString();
        } catch (java.net.MalformedURLException e) {
            throw new Exception("unexpected MalformedURLException");
        }
    }

    /** Wandelt Color in Color-String. */
    public static String getRGBCode(Color color) {
        return "rgb(" + color.getRed() + "," + color.getGreen() + "," +
        color.getBlue() + ")";
    }

    /** Wandelt Color in HTML-Color-String. */
    public static String getHTMLRGBCode(Color color) {
        String red = Integer.toHexString(color.getRed());

        if (red.length() == 1) {
            red = "0" + red;
        }

        String green = Integer.toHexString(color.getGreen());

        if (green.length() == 1) {
            green = "0" + green;
        }

        String blue = Integer.toHexString(color.getBlue());

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + red + green + blue;
    }

    /** Wandelt Fehler-Srack in String um. */
    public static String getStackAsString(Throwable e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            pw.close();
            sw.close();

            return sw.toString();
        } catch (Exception exp) {
            exp.printStackTrace();

            return "No stack trace avialable.";
        }
    }

    /** Zeigt OutOfMemory-Errorbox an. */
    public static void showOutOfMemoryMessage(Component parent, boolean corpus,
        boolean continues) {
        String message;

        if (corpus) {
            message = ims.tiger.system.Constants.OUT_OF_MEMORY_CORPUS;
        } else {
            message = ims.tiger.system.Constants.OUT_OF_MEMORY;
        }

        if (continues) {
            message += ims.tiger.system.Constants.OUT_OF_MEMORY_CONTINUE;

            Object[] options = { "Close", "Continue" };

            int answer = javax.swing.JOptionPane.showOptionDialog(parent,
                    message, "Out of memory error",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE, null, options,
                    options[0]);

            // -1 wenn WINDOW_CLOSED_EVENT ausgeloest wird
            if ((answer == javax.swing.JOptionPane.NO_OPTION) ||
                    (answer == -1)) {
                return;
            } else {
                logger.error("Stopping the tool." +
                    "\n\n==============================\n");
                System.exit(0);
            }
        }
        else {
            message += ims.tiger.system.Constants.OUT_OF_MEMORY_CLOSED;

            javax.swing.JOptionPane.showMessageDialog(parent, message,
                "Out of memory error", javax.swing.JOptionPane.ERROR_MESSAGE);

            logger.error("Stopping the tool." +
                "\n\n==============================\n");
            System.exit(0);
        }
    }

    /** Waehle den Zeichensatz aus, der den String darstellen kann. */
    public static java.awt.Font chooseFont(java.awt.Font[] fonts, String text) {
        if (fonts.length == 0) {
            return new Font("Monospaced", java.awt.Font.PLAIN, 14);
        }

        StringBuffer test = new StringBuffer(text);

        while (test.indexOf("\n") > -1) {
            test.deleteCharAt(test.indexOf("\n"));
        }

        String testemich = test.toString();

        for (int i = 0; i < fonts.length; i++) {
            if (fonts[i].canDisplayUpTo(testemich) < 0) {
                return fonts[i];
            }
        }

        return fonts[0];
    }

    /** Waehle die Nummer des Zeichensatzes aus, der den String darstellen kann. */
    public static int chooseFontNumber(java.awt.Font[] fonts, String text) {
        if (fonts.length == 0) {
            return -1;
        }

        StringBuffer test = new StringBuffer(text);

        while (test.indexOf("\n") > -1) {
            test.deleteCharAt(test.indexOf("\n"));
        }

        String testemich = test.toString();

        for (int i = 0; i < fonts.length; i++) {
            if (fonts[i].canDisplayUpTo(testemich) < 0) {
                return i;
            }
        }

        return 0;
    }

    /** Wandle 4-stellige Hexadezimalkodierung in Unicode-Zeichen um (z.B. 000a -> 10) */
    public static char convertHexToChar(String hex) {
        return (char) convertHexToInt(hex);
    }

    /** Wandle 4-stellige Hexadezimalkodierung in Nummer um (z.B. 000a -> 10) */
    public static int convertHexToInt(String hex) {
        hex = hex.toLowerCase();

        char[] chex = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'
        };
        int[] c = { 0, 0, 0, 0 };

        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 16; i++) {
                if (hex.charAt(j) == chex[i]) {
                    c[j] = i;
                }
            }
        }

        int no = (c[3] + (16 * c[2]) + (16 * 16 * c[1]) +
            (16 * 16 * 16 * c[0]));

        return no;
    }

    /*  Liest eine Textdatei und wandelt den Text in ein String-Objekt um.
    */
    public static String getContent(String path) throws IOException {
        StringBuffer result = new StringBuffer();

        BufferedReader f = new BufferedReader(new FileReader(path));

        String line;

        while ((line = f.readLine()) != null) {
            result.append(line + "\n");
        }

        f.close();

        return result.toString();
    }

    /** Triggert einen Attributwert in der TIGERSearch-Anfragesprache. */
    public static final String triggerFeatureValue(String value) {
        StringBuffer result = new StringBuffer();

        char c;

        for (int i = 0; i < value.length(); i++) {
            c = value.charAt(i);

            // " am Anfang oder Ende (pos="NN")?
            if (((i == 0) && (c == '\"')) ||
                    ((i == (value.length() - 1)) && (c == '\"'))) {
                result.append(c);

                continue;
            }

            // zu quotendes Symbol?
            for (int j = 0; j < ims.tiger.system.Constants.TO_BE_QUOTED.length;
                    j++) {
                if (c == ims.tiger.system.Constants.TO_BE_QUOTED[j]) {
                    result.append("\\");

                    break;
                }
            }

            result.append(c);
        }

        return result.toString();
    }

    /** Prueft, ob die VM geeignet ist. */
    public static boolean isMatchingVM(String vm) {
        return (vm.startsWith(ims.tiger.system.Constants.VM_VERSION));
    }

    /** Konvertierung Relationsnummer -> Relationsname (zum Debuggen) */
    public static final String getRelationSymbol(int number) {
        if ((number <= 0) ||
                (number > ims.tiger.system.Constants.relsymbols.length)) {
            return "unknown";
        } else {
            return ims.tiger.system.Constants.relsymbols[number - 1];
        }
    }
}
