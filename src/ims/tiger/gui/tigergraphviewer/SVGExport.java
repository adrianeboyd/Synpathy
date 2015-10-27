/*
 * File:     SVGExport.java
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

/* This program is free software; you can redistribute it and/or modify
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

import ims.tiger.gui.tigergraphviewer.draw.DisplaySentence;
import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;
import ims.tiger.gui.tigergraphviewer.draw.GraphPanel;

import ims.tiger.gui.tigergraphviewer.forest.Forest;

import ims.tiger.util.UtilitiesCollection;

import org.jdom.Comment;
import org.jdom.Namespace;

import org.jdom.output.XMLOutputter;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.util.zip.GZIPOutputStream;


/**
 * $Id: SVGExport.java,v 1.4 2007/01/03 11:46:05 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.4 $
 */
public class SVGExport {
    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     * @param forest DOCUMENT ME!
     * @param panel DOCUMENT ME!
     * @param match_sent DOCUMENT ME!
     * @param include_submatches DOCUMENT ME!
     * @param match_highlighting DOCUMENT ME!
     * @param display_matchinfo DOCUMENT ME!
     * @param compress DOCUMENT ME!
     * @param background DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    protected static void saveSVGForest(String filename, Forest forest,
        GraphPanel panel, int[] match_sent, boolean include_submatches,
        boolean match_highlighting, boolean display_matchinfo,
        boolean compress, boolean background) throws Exception {
        // Pruefe Wohldefiniertheit der Parameter (fuer Korpusansicht)
        if (include_submatches && (!forest.submatchesPossible())) {
            include_submatches = false;
        }

        if (match_highlighting && (!forest.isForestWithMatches())) {
            match_highlighting = false;
        }

        if (display_matchinfo && (!forest.isForestWithMatches())) {
            display_matchinfo = false;
        }

        // Sichere den aktuellen Satz
        DisplaySentence backup_sentence = panel.getCurrentDisplaySentence();
        int backup_position = forest.getCurrentMatchNumber();
        DisplaySentence display;

        // Zieldatei
        File f = new File(filename);
        XMLOutputter out = new XMLOutputter("  ", true);
        DataOutputStream output;

        if (compress) {
            output = new DataOutputStream(new GZIPOutputStream(
                        new FileOutputStream(f)));
        } else {
            output = new DataOutputStream(new FileOutputStream(f));
        }

        // Neue Wurzel
        org.jdom.Element root = new org.jdom.Element("svg");

        // Breite und Hoehe
        int max_breite = 0;
        int max_hoehe = 0;

        // HOME und END
        boolean first = true;
        String first_id = null;
        String last_id = null;
        DisplaySentence last = forest.gotoMatchNumber(match_sent[match_sent.length -
                1]);
        last_id = last.getSentence().getSentenceID();

        int last_sub_n = forest.getSubMatchesSize();

        if ((last_sub_n > 1) && include_submatches) {
            last_id += ("_" + (new Integer(last_sub_n)));
        }

        // <- und ->
        String previous_id = null;
        String current_id = null;
        String next_id = null;

        String current_match = null;
        String next_match = null;
        String match_count = (new Integer(forest.getForestSize())).toString();

        String current_submatch = null;
        String next_submatch = null;
        String submatch_count = null;

        org.jdom.Element previous_group = null;

        for (int i = 0; i < match_sent.length; i++) {
            // Wieviele Submatches?
            display = forest.gotoMatchNumber(match_sent[i]);

            int sub_n = forest.getSubMatchesSize();

            // Match-Info
            String match = (new Integer(match_sent[i])).toString() + "/" +
                match_count;
            submatch_count = (new Integer(sub_n)).toString();

            // Submatches anzeigen?
            if (!include_submatches) {
                sub_n = 1;
            }

            for (int j = 1; j <= sub_n; j++) {
                String submatch = (new Integer(j)).toString() + "/" +
                    submatch_count;

                // ID bestimmen
                String id = display.getSentence().getSentenceID();
                String anhaengsel = "";

                if (sub_n > 1) {
                    // Anhaengsel an ID
                    anhaengsel += ("_" + (new Integer(j)));
                    id += anhaengsel;
                }

                if (first_id == null) {
                    first_id = id;
                }

                // Graph zeichnen
                if (!match_highlighting) {
                    display.turnOffHighlighting();
                }

                panel.setSentence(display);
                panel.repaint();

                // SVG zeichnen; ggf. runterdruecken; ggf. ID-Anhaengsel
                org.jdom.Element image_root = panel.paintSVG(50, anhaengsel);

                // <svg> auswerten
                int breite = image_root.getAttribute("width").getIntValue();
                int hoehe = image_root.getAttribute("height").getIntValue();

                if (breite > max_breite) {
                    max_breite = breite;
                }

                if (hoehe > max_hoehe) {
                    max_hoehe = hoehe;
                }

                // <g><rect> loeschen
                org.jdom.Element image_group = (org.jdom.Element) image_root.getChildren()
                                                                            .get(0);
                image_group.removeChild("g"); // <g>
                image_root.removeChildren();

                // Im haengenden Speicher weiterblaettern
                previous_id = current_id;
                current_id = next_id;
                next_id = id;

                current_match = next_match;
                next_match = match;

                current_submatch = next_submatch;
                next_submatch = submatch;

                // Haenge VORAUSGEGANGENEN Match ein!
                if (current_id != null) {
                    insertMatch(root, previous_group, previous_id, current_id,
                        next_id, current_match, current_submatch, first_id,
                        last_id, display_matchinfo, first);

                    if (first) {
                        first = false;
                    }
                }

                previous_group = image_group;

                // ggf. weiterblaettern
                if (include_submatches && (forest.isNextSubMatch())) {
                    panel.normalizeMatchHighlights();
                    display = forest.nextSubMatch();
                }
            }
             // j
        }
         // i

        // ALLERLETZTEN Match nachliefern
        previous_id = current_id;
        current_id = next_id;
        next_id = null;

        current_match = next_match;
        current_submatch = next_submatch;

        insertMatch(root, previous_group, previous_id, current_id, next_id,
            current_match, current_submatch, first_id, last_id,
            display_matchinfo, first);

        // Breite und Hoehe besetzen
        if (max_breite < 625) {
            max_breite = 625;
        }
         // mindestens NaviPanel-Breite

        root.setAttribute("width", (new Integer(max_breite)).toString());
        root.setAttribute("height", (new Integer(max_hoehe)).toString());

        // Hintergrundfarbe setzen?
        if (background) {
            // <g type="bgcolor">
            org.jdom.Element bgcolor = new org.jdom.Element("g");
            bgcolor.setAttribute("type", "bgcolor");

            //   <!-- create background color -->
            bgcolor.addContent(new Comment(" create background color "));

            //   <rect ... />
            org.jdom.Element rect = new org.jdom.Element("rect");
            rect.setAttribute("x", "0");
            rect.setAttribute("y", "0");
            rect.setAttribute("width", (new Integer(max_breite)).toString());
            rect.setAttribute("height", (new Integer(max_hoehe)).toString());
            rect.setAttribute("fill",
                UtilitiesCollection.getRGBCode(
                    GraphConstants.panelBackgroundColor));

            // </g>
            bgcolor.addContent(rect);

            java.util.List childs = root.getChildren();
            childs.add(0, bgcolor);
        }

        org.jdom.Document doc = new org.jdom.Document(root);
        out.output(doc, output);

        output.flush();
        output.close();

        forest.gotoMatchNumber(backup_position);
        panel.setSentence(backup_sentence);
    }

    private static void insertMatch(org.jdom.Element root,
        org.jdom.Element group, String previous_id, String current_id,
        String next_id, String current_match, String current_submatch,
        String first_id, String last_id, boolean display_matchinfo,
        boolean visible) {
        // Sichtbarkeit
        if (!visible) {
            group.setAttribute("style", "display:none");
        }

        // 1. Block: Panel
        // <!-- animation: sentence information panel -->
        Comment c = new Comment(" animation: sentence information panel ");
        group.addContent("\n    ");
        group.addContent(c);

        // <g>
        org.jdom.Element panel = new org.jdom.Element("g");

        //   <!-- navigation panel -->
        c = new Comment(" navigation panel ");
        panel.addContent(c);

        //   <rect>
        org.jdom.Element rect = new org.jdom.Element("rect");
        rect.setAttribute("x", "25");
        rect.setAttribute("y", "10");
        rect.setAttribute("width", "575");
        rect.setAttribute("height", "25");
        rect.setAttribute("rx", "2");
        rect.setAttribute("ry", "2");
        rect.setAttribute("fill", "lightyellow");
        rect.setAttribute("stroke", "black");
        panel.addContent(rect);

        //   <!-- match information -->
        c = new Comment(" match information ");
        panel.addContent(c);

        //   <text>SentID: ...</text>
        org.jdom.Element text1 = new org.jdom.Element("text");
        text1.setAttribute("x", "30");
        text1.setAttribute("y", "27.5");
        text1.setText("SentID: " + current_id);
        panel.addContent(text1);

        //   <text>MatchSent: ...</text>
        if (display_matchinfo) {
            org.jdom.Element text2 = new org.jdom.Element("text");
            text2.setAttribute("x", "150");
            text2.setAttribute("y", "27.5");
            text2.setText("MatchSent: " + current_match);
            panel.addContent(text2);

            org.jdom.Element text3 = new org.jdom.Element("text");
            text3.setAttribute("x", "300");
            text3.setAttribute("y", "27.5");
            text3.setText("Match: " + current_submatch);
            panel.addContent(text3);
        }

        //   <!-- backward -->
        c = new Comment(" backward ");
        panel.addContent(c);

        //   <g id="XXXXX_backward">
        String p_color;

        if (previous_id != null) {
            p_color = "black";
        } else {
            p_color = "grey";
        }

        org.jdom.Element previous = new org.jdom.Element("g");
        previous.setAttribute("id", current_id + "_backward");

        //      <rect>
        org.jdom.Element p_rect = new org.jdom.Element("rect");
        p_rect.setAttribute("x", "400");
        p_rect.setAttribute("y", "15");
        p_rect.setAttribute("rx", "2");
        p_rect.setAttribute("ry", "2");
        p_rect.setAttribute("width", "40");
        p_rect.setAttribute("height", "15");
        p_rect.setAttribute("fill", "rgb(177,180,200)");
        p_rect.setAttribute("stroke", p_color);
        previous.addContent(p_rect);

        //      <line>
        org.jdom.Element p_line1 = new org.jdom.Element("line");
        p_line1.setAttribute("x1", "408");
        p_line1.setAttribute("y1", "22.5");
        p_line1.setAttribute("x2", "432");
        p_line1.setAttribute("y2", "22.5");
        p_line1.setAttribute("fill", p_color);
        p_line1.setAttribute("stroke", p_color);
        p_line1.setAttribute("style", "stroke-width:2");
        previous.addContent(p_line1);

        //      <line>
        org.jdom.Element p_line2 = new org.jdom.Element("line");
        p_line2.setAttribute("x1", "413");
        p_line2.setAttribute("y1", "17.5");
        p_line2.setAttribute("x2", "407.25");
        p_line2.setAttribute("y2", "23.25");
        p_line2.setAttribute("fill", p_color);
        p_line2.setAttribute("stroke", p_color);
        p_line2.setAttribute("style", "stroke-width:2");
        previous.addContent(p_line2);

        //      <line>
        org.jdom.Element p_line3 = new org.jdom.Element("line");
        p_line3.setAttribute("x1", "413");
        p_line3.setAttribute("y1", "27.5");
        p_line3.setAttribute("x2", "408");
        p_line3.setAttribute("y2", "22.5");
        p_line3.setAttribute("fill", p_color);
        p_line3.setAttribute("stroke", p_color);
        p_line3.setAttribute("style", "stroke-width:2");
        previous.addContent(p_line3);

        //   </g>    (id="XXXXX_backward")
        panel.addContent(previous);

        //   <!-- forward -->
        c = new Comment(" forward ");
        panel.addContent(c);

        //   <g id="XXXXX_forward">
        String f_color;

        if (next_id != null) {
            f_color = "black";
        } else {
            f_color = "grey";
        }

        org.jdom.Element forward = new org.jdom.Element("g");
        forward.setAttribute("id", current_id + "_forward");

        //      <rect>
        org.jdom.Element f_rect = new org.jdom.Element("rect");
        f_rect.setAttribute("x", "450");
        f_rect.setAttribute("y", "15");
        f_rect.setAttribute("rx", "2");
        f_rect.setAttribute("ry", "2");
        f_rect.setAttribute("width", "40");
        f_rect.setAttribute("height", "15");
        f_rect.setAttribute("fill", "rgb(177,180,200)");
        f_rect.setAttribute("stroke", f_color);
        forward.addContent(f_rect);

        //      <line>
        org.jdom.Element f_line1 = new org.jdom.Element("line");
        f_line1.setAttribute("x1", "458");
        f_line1.setAttribute("y1", "22.5");
        f_line1.setAttribute("x2", "482");
        f_line1.setAttribute("y2", "22.5");
        f_line1.setAttribute("fill", f_color);
        f_line1.setAttribute("stroke", f_color);
        f_line1.setAttribute("style", "stroke-width:2");
        forward.addContent(f_line1);

        //      <line>
        org.jdom.Element f_line2 = new org.jdom.Element("line");
        f_line2.setAttribute("x1", "477");
        f_line2.setAttribute("y1", "17.5");
        f_line2.setAttribute("x2", "482.75");
        f_line2.setAttribute("y2", "23.25");
        f_line2.setAttribute("fill", f_color);
        f_line2.setAttribute("stroke", f_color);
        f_line2.setAttribute("style", "stroke-width:2");
        forward.addContent(f_line2);

        //      <line>
        org.jdom.Element f_line3 = new org.jdom.Element("line");
        f_line3.setAttribute("x1", "477");
        f_line3.setAttribute("y1", "27.5");
        f_line3.setAttribute("x2", "482");
        f_line3.setAttribute("y2", "22.5");
        f_line3.setAttribute("fill", f_color);
        f_line3.setAttribute("stroke", f_color);
        f_line3.setAttribute("style", "stroke-width:2");
        forward.addContent(f_line3);

        //   </g>    (id="XXXXX_forward")
        panel.addContent(forward);

        //   <!-- Home -->
        c = new Comment(" First ");
        panel.addContent(c);

        //   <g id="XXXXX_home">
        org.jdom.Element home = new org.jdom.Element("g");
        home.setAttribute("id", current_id + "_first");

        //     <rect>
        org.jdom.Element h_rect = new org.jdom.Element("rect");
        h_rect.setAttribute("x", "520");
        h_rect.setAttribute("y", "15");
        h_rect.setAttribute("rx", "2");
        h_rect.setAttribute("ry", "2");
        h_rect.setAttribute("width", "34");
        h_rect.setAttribute("height", "15");
        h_rect.setAttribute("fill", "rgb(177,180,200)");
        h_rect.setAttribute("stroke", "black");
        home.addContent(h_rect);

        //      <text>
        org.jdom.Element text_h = new org.jdom.Element("text");
        text_h.setAttribute("x", "521");
        text_h.setAttribute("y", "27.5");
        text_h.setText("First");
        home.addContent(text_h);

        //   </g>
        panel.addContent(home);

        //   <!-- Last -->
        c = new Comment(" Last ");
        panel.addContent(c);

        //   <g id="XXXXX_last">
        org.jdom.Element end = new org.jdom.Element("g");
        end.setAttribute("id", current_id + "_last");

        //     <rect>
        org.jdom.Element e_rect = new org.jdom.Element("rect");
        e_rect.setAttribute("x", "560");
        e_rect.setAttribute("y", "15");
        e_rect.setAttribute("rx", "2");
        e_rect.setAttribute("ry", "2");
        e_rect.setAttribute("width", "34");
        e_rect.setAttribute("height", "15");
        e_rect.setAttribute("fill", "rgb(177,180,200)");
        e_rect.setAttribute("stroke", "black");
        end.addContent(e_rect);

        //      <text>
        org.jdom.Element text_e = new org.jdom.Element("text");
        text_e.setAttribute("x", "566");
        text_e.setAttribute("y", "27.5");
        text_e.setText("Last");
        end.addContent(text_e);

        //   </g>
        panel.addContent(end);

        // </g>
        group.addContent(panel);

        // 2. Block: Animation
        Namespace xlink = Namespace.getNamespace("xlink",
                "http://www.w3.org/1999/xlink");

        // <!-- animation: event handling -->
        c = new Comment(" animation: event handling ");
        group.addContent("\n    ");
        group.addContent(c);

        // <g>
        org.jdom.Element event = new org.jdom.Element("g");

        //   <!-- backward -->
        c = new Comment(" backward ");
        event.addContent(c);

        if (previous_id != null) {
            // <animate xlink:href="#XXX(current)" attributeName="display"
            //          from="block" to="none" begin="XXX(current)_backward.click"
            //          dur="0.1s" fill="freeze" />
            org.jdom.Element b_anim1 = new org.jdom.Element("animate");
            b_anim1.setAttribute("href", "#" + current_id, xlink);
            b_anim1.setAttribute("attributeName", "display");
            b_anim1.setAttribute("from", "block");
            b_anim1.setAttribute("to", "none");
            b_anim1.setAttribute("begin", current_id + "_backward.click");
            b_anim1.setAttribute("dur", "0.1s");
            b_anim1.setAttribute("fill", "freeze");
            event.addContent(b_anim1);

            // <animate xlink:href="#XXX(previous)" attributeName="display"
            //          from="none" to="block" begin="XXX(current)_backward.click"
            //          dur="0.1s" fill="freeze" />
            org.jdom.Element b_anim2 = new org.jdom.Element("animate");
            b_anim2.setAttribute("href", "#" + previous_id, xlink);
            b_anim2.setAttribute("attributeName", "display");
            b_anim2.setAttribute("from", "none");
            b_anim2.setAttribute("to", "block");
            b_anim2.setAttribute("begin", current_id + "_backward.click");
            b_anim2.setAttribute("dur", "0.1s");
            b_anim2.setAttribute("fill", "freeze");
            event.addContent(b_anim2);
        }

        //   <!-- forward -->
        c = new Comment(" forward ");
        event.addContent(c);

        if (next_id != null) {
            // <animate xlink:href="#XXX(current)" attributeName="display"
            //          from="block" to="none" begin="XXX(current)_forward.click"
            //          dur="0.1s" fill="freeze" />
            org.jdom.Element f_anim1 = new org.jdom.Element("animate");
            f_anim1.setAttribute("href", "#" + current_id, xlink);
            f_anim1.setAttribute("attributeName", "display");
            f_anim1.setAttribute("from", "block");
            f_anim1.setAttribute("to", "none");
            f_anim1.setAttribute("begin", current_id + "_forward.click");
            f_anim1.setAttribute("dur", "0.1s");
            f_anim1.setAttribute("fill", "freeze");
            event.addContent(f_anim1);

            // <animate xlink:href="#XXX(next)" attributeName="display"
            //          from="none" to="block" begin="XXX(current)_forward.click"
            //          dur="0.1s" fill="freeze" />
            org.jdom.Element f_anim2 = new org.jdom.Element("animate");
            f_anim2.setAttribute("href", "#" + next_id, xlink);
            f_anim2.setAttribute("attributeName", "display");
            f_anim2.setAttribute("from", "none");
            f_anim2.setAttribute("to", "block");
            f_anim2.setAttribute("begin", current_id + "_forward.click");
            f_anim2.setAttribute("dur", "0.1s");
            f_anim2.setAttribute("fill", "freeze");
            event.addContent(f_anim2);
        }

        //   <!-- home -->
        c = new Comment(" first ");
        event.addContent(c);

        // <animate xlink:href="#XXX(current)" attributeName="display"
        //          from="block" to="none" begin="XXX(current)_first.click"
        //          dur="0.1s" fill="freeze" />
        org.jdom.Element h_anim1 = new org.jdom.Element("animate");
        h_anim1.setAttribute("href", "#" + current_id, xlink);
        h_anim1.setAttribute("attributeName", "display");
        h_anim1.setAttribute("from", "block");
        h_anim1.setAttribute("to", "none");
        h_anim1.setAttribute("begin", current_id + "_first.click");
        h_anim1.setAttribute("dur", "0.1s");
        h_anim1.setAttribute("fill", "freeze");
        event.addContent(h_anim1);

        // <animate xlink:href="#XXX(first)" attributeName="display"
        //          from="none" to="block" begin="XXX(current)_first.click"
        //          dur="0.1s" fill="freeze" />
        org.jdom.Element f_anim2 = new org.jdom.Element("animate");
        f_anim2.setAttribute("href", "#" + first_id, xlink);
        f_anim2.setAttribute("attributeName", "display");
        f_anim2.setAttribute("from", "none");
        f_anim2.setAttribute("to", "block");
        f_anim2.setAttribute("begin", current_id + "_first.click");
        f_anim2.setAttribute("dur", "0.1s");
        f_anim2.setAttribute("fill", "freeze");
        event.addContent(f_anim2);

        //   <!-- last -->
        c = new Comment(" last ");
        event.addContent(c);

        // <animate xlink:href="#XXX(current)" attributeName="display"
        //          from="block" to="none" begin="XXX(current)_last.click"
        //          dur="0.1s" fill="freeze" />
        org.jdom.Element e_anim1 = new org.jdom.Element("animate");
        e_anim1.setAttribute("href", "#" + current_id, xlink);
        e_anim1.setAttribute("attributeName", "display");
        e_anim1.setAttribute("from", "block");
        e_anim1.setAttribute("to", "none");
        e_anim1.setAttribute("begin", current_id + "_last.click");
        e_anim1.setAttribute("dur", "0.1s");
        e_anim1.setAttribute("fill", "freeze");
        event.addContent(e_anim1);

        // <animate xlink:href="#XXX(last)" attributeName="display"
        //          from="none" to="block" begin="XXX(current)_last.click"
        //          dur="0.1s" fill="freeze" />
        org.jdom.Element e_anim2 = new org.jdom.Element("animate");
        e_anim2.setAttribute("href", "#" + last_id, xlink);
        e_anim2.setAttribute("attributeName", "display");
        e_anim2.setAttribute("from", "none");
        e_anim2.setAttribute("to", "block");
        e_anim2.setAttribute("begin", current_id + "_last.click");
        e_anim2.setAttribute("dur", "0.1s");
        e_anim2.setAttribute("fill", "freeze");
        event.addContent(e_anim2);

        // </g>
        group.addContent(event);

        // 3. Satz fertig -> in Sammlung einfuegen!
        root.addContent(group);
    }
}
