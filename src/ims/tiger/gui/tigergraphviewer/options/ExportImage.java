/*
 * File:     ExportImage.java
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
package ims.tiger.gui.tigergraphviewer.options;

import ims.tiger.corpus.Header;

import ims.tiger.gui.shared.progress.ProgressContainerInterface;

import ims.tiger.gui.tigergraphviewer.TIGERGraphViewerConfiguration;

import ims.tiger.gui.tigergraphviewer.draw.DisplaySentence;
import ims.tiger.gui.tigergraphviewer.draw.GraphCalculator;
import ims.tiger.gui.tigergraphviewer.draw.GraphConstants;
import ims.tiger.gui.tigergraphviewer.draw.GraphPanelUtil;

import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.svg.SVGOMDocument;

import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGSyntax;
import org.apache.batik.svggen.StyleHandler;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;

import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.JPEGTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.transcoder.image.TIFFTranscoder;

import org.w3c.dom.CDATASection;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;

import org.w3c.dom.svg.SVGSVGElement;

import java.awt.Color;
import java.awt.Dimension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;


/**
 * $Id: ExportImage.java,v 1.8 2007/01/04 16:55:52 klasal Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.8 $
 */
public class ExportImage {
    /** DOCUMENT ME! */
    public static final String[] formatS = { "SVG", "TIF", "PNG" };

    /** DOCUMENT ME! */
    public static final int SVG = 0;

    /** DOCUMENT ME! */
    public static final int TIFF = 1;

    /** DOCUMENT ME! */
    public static final int PNG = 2;

    /** DOCUMENT ME! */
    public static final int JPG = 3;

    /**
     * DOCUMENT ME!
     *
     * @param format_selection DOCUMENT ME!
     * @param out DOCUMENT ME!
     * @param document DOCUMENT ME!
     *
     * @throws TranscoderException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public static void convertSVG2Image(int format_selection, OutputStream out,
        SVGOMDocument document) throws TranscoderException, IOException {
        ImageTranscoder t = null;

        if (format_selection == JPG) { // JPG
            t = new JPEGTranscoder();
            t.addTranscodingHint(JPEGTranscoder.KEY_QUALITY, new Float(1.0));
        } else if (format_selection == PNG) { // PNG
            t = new PNGTranscoder();
        } else if (format_selection == TIFF) { // TIFF
            t = new TIFFTranscoder();
        } else {
            return;
        }

        TranscoderInput input = new TranscoderInput(document);
        TranscoderOutput output = new TranscoderOutput(out);
        t.transcode(input, output);
        out.flush();
        out.close();
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param format_selection DOCUMENT ME!
     * @param f DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void exportImage(DisplaySentence sentence, Header header,
        TIGERGraphViewerConfiguration config, int format_selection, File f)
        throws Exception {
        exportImage(sentence, header, config, format_selection, f, null, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param format_selection DOCUMENT ME!
     * @param out DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void exportImage(DisplaySentence sentence, Header header,
        TIGERGraphViewerConfiguration config, int format_selection,
        OutputStream out) throws Exception {
        exportImage(sentence, header, config, format_selection, out, null, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param format_selection DOCUMENT ME!
     * @param f DOCUMENT ME!
     * @param CSSstyle DOCUMENT ME!
     * @param container DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void exportImage(DisplaySentence sentence, Header header,
        TIGERGraphViewerConfiguration config, int format_selection, File f,
        String CSSstyle, ProgressContainerInterface container)
        throws Exception {
        exportImage(sentence, header, config, format_selection,
            new FileOutputStream(f), CSSstyle, container);
    }

    /**
     * DOCUMENT ME!
     *
     * @param sentence DOCUMENT ME!
     * @param header DOCUMENT ME!
     * @param config DOCUMENT ME!
     * @param format_selection DOCUMENT ME!
     * @param out DOCUMENT ME!
     * @param CSSstyle DOCUMENT ME!
     * @param container DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public static void exportImage(DisplaySentence sentence, Header header,
        TIGERGraphViewerConfiguration config, int format_selection,
        OutputStream out, String CSSstyle, ProgressContainerInterface container)
        throws Exception {
        DOMImplementation domImpl = SVGDOMImplementation.getDOMImplementation();
        SVGOMDocument document = (SVGOMDocument) domImpl.createDocument(SVGDOMImplementation.SVG_NAMESPACE_URI,
                "svg", null);

        SVGGeneratorContext ctx = SVGGeneratorContext.createDefault(document);
        ctx.setPrecision(1);
        ctx.setComment("Syntax tree for sentence " +
            sentence.getSentence().getSentenceID() +
            " generated by TIGER Syntaxviewer on " + new Date());
        ctx.setStyleHandler(new MyStyleHandler());

        CDATASection styleSheet = null;

        if (CSSstyle != null) {
            styleSheet = document.createCDATASection("");
            styleSheet.appendData(CSSstyle);
        }

        SVGGraphics2D generator = new SVGGraphics2D(ctx, false);

        Dimension graphDimension = GraphCalculator.calculate(sentence,
                config.getDefaultDisplayedNTFeature(header),
                config.getDisplayedTFeatures(header), generator);
        generator.setSVGCanvasSize(graphDimension);

        generator.setColor((CSSstyle == null)
            ? GraphConstants.panelBackgroundColor : Color.WHITE);
        generator.fillRect(0, 0, graphDimension.width, graphDimension.height);

        GraphPanelUtil.paintSentence(sentence, header, config, generator);

        // a) Erzeuge SVG -> direkter Export
        if (format_selection == SVG) {
            if (container != null) {
                container.setMessage("Generating SVG...");
            }

            boolean useCSS = true; // we want to use CSS style attribute
            Writer writer = new OutputStreamWriter(out, "UTF-8");

            // ggf. Style-Element einfuegen
            if (CSSstyle != null) {
                if (container != null) {
                    container.setMessage("Inserting CSS stylesheet...");
                }

                SVGSVGElement root = (SVGSVGElement) generator.getRoot();
                Element defs = root.getElementById(SVGSyntax.ID_PREFIX_GENERIC_DEFS);
                Element style = document.createElementNS(SVGSyntax.SVG_NAMESPACE_URI,
                        SVGSyntax.SVG_STYLE_TAG);
                style.setAttributeNS(null, SVGSyntax.SVG_TYPE_ATTRIBUTE,
                    "text/css");

                style.appendChild(styleSheet);
                defs.appendChild(style);
                generator.stream(root, writer, useCSS);
            } else {
                generator.stream(writer, useCSS);
                out.close();
            }
        } else { // Erzeuge JPG, PNG, TIFF -> Konvertierung starten

            if (container != null) {
                container.setMessage("Converting to " +
                    formatS[format_selection] + "...");
            }

            Element root = document.getDocumentElement();
            generator.getRoot(root);

            convertSVG2Image(format_selection, out, document);
        }
    }
}


/**
 * DOCUMENT ME!
 * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
 * @author $Author: hasloe $
 * @version $Revision: 1.3 $
 */
class MyStyleHandler implements StyleHandler {
    /**
     * simplifies svg output
     *
     * @param element element to be changed
     * @param styleMap styles belonging to an element
     * @param generatorContext generator context
     */
    public void setStyle(Element element, Map styleMap,
        SVGGeneratorContext generatorContext) {
        Iterator it = styleMap.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();

            if ((("text-rendering".equals(key)) ||
                    ("shape-rendering".equals(key))) &&
                    !element.getNodeName().equals("svg")) {
                //omit local settings
            } else if ("font-family".equals(key)) {
                //overwrite global font, omit local settings
                if (element.getNodeName().equals("svg")) {
                    element.setAttribute(key, "sans-serif");
                } else {
                    //	omit
                }
            } else if ("stroke-dasharray".equals(key) ||
                    "stroke-dashoffset".equals(key)) {
                //omit every setting since never used
            } else if ("xml:space".equals(key)) {
                //omit
            } else {
                element.setAttribute(key, (String) styleMap.get(key));
            }
        }
    }
}
