/*
 * File:     XSLTProcessor.java
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
package ims.tiger.export;

import org.jdom.output.*;

import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;

import java.util.HashMap;

import javax.xml.transform.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


/** Diese Klasse stellt Methoden zum Prozessieren von Stylesheets auf
 *  XML-Dateien zur Verfuegung. Als Stylesheet-Prozessor wird XALAN verwendet.<BR>
 *
 *  Um die Austauschbarkeit des Prozessors zu gewaehrleisten,
 *  wird die aktuelle Xalan-Implementation der TRAX-Schnittstelle verwendet.
 */
public class XSLTProcessor {
    private Transformer processor;

    /** Konstruktor instantiiert den XSLT-Erzeuger. */
    public XSLTProcessor(File xslt_file) throws SAXException {
        TransformerFactory tFactory = TransformerFactory.newInstance();
        StreamSource source = new StreamSource(xslt_file);

        try {
            processor = tFactory.newTransformer(source);
        } catch (TransformerConfigurationException e) {
            throw new SAXException(e.getMessage());
        }
    }

    /** Prozessiert das Stylesheet auf dem als String vorliegenden XML-Fragment
     *  unter Beruecksichtigung der uebergebenen Parameter. */
    public void process(StringReader doc_string, Writer f, HashMap parameter)
        throws SAXException, IOException {
        /* IO-Objekt */
        StreamSource source = new StreamSource(doc_string);
        StreamResult result = new StreamResult(f);

        process(source, result, parameter);
    }

    /** Prozessiert das Stylesheet auf dem als DOM-Objekt vorliegenden XML-Fragment
     *  unter Beruecksichtigung der uebergebenen Parameter. */
    public void process(org.w3c.dom.Document document, Writer f,
        HashMap parameter) throws SAXException, IOException {
        /* DOM-Objekt */
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(f);

        process(source, result, parameter);
    }

    /** Prozessiert das Stylesheet auf dem als DOM-Objekt vorliegenden XML-Fragment
     *  unter Beruecksichtigung der uebergebenen Parameter. */
    public void process(org.jdom.Document jdom_document, Writer f,
        HashMap parameter) throws SAXException, IOException {
        /* JDOM-Objekt -> DOM-Objekt */
        org.w3c.dom.Document dom_document = null;
        DOMOutputter output = new DOMOutputter();

        try {
            dom_document = output.output(jdom_document);
        } catch (Exception e) {
            throw new SAXException(e);
        }

        DOMSource source = new DOMSource(dom_document);
        StreamResult result = new StreamResult(f);

        process(source, result, parameter);
    }

    /* Interne Methode startet die Prozessierung. */
    private void process(Source source, Result result, HashMap parameter)
        throws SAXException, IOException {
        /* 1. Parameter uebergeben */
        if ((parameter != null) && (parameter.size() > 0)) {
            Object[] keys = parameter.keySet().toArray();
            String key;
            String value;

            for (int i = 0; i < keys.length; i++) {
                key = (String) keys[i];
                value = (String) parameter.get(key); // werden intern automatisch als String 'wert' gesetzt
                processor.setParameter(key, value);
            }
        }

        /* 2. Prozessierung */
        try {
            processor.transform(source, result);
        } catch (TransformerException e) {
            throw new SAXException(e.getMessage());
        }
    }
}
