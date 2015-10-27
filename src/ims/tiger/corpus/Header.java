/*
 * File:     Header.java
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
package ims.tiger.corpus;

import java.io.*;

import java.util.*;
import java.util.zip.*;


/**
 * Diese Klasse definiert den Header, in dem alle Meta-Informationen sowie Informationen ueber die Features
 * abgelegt sind.

 * ACHTUNG! Da diese Klasse bei der Indexierung serialisiert wird, darf sie NICHT mehr veraendert werden.
 */
public class Header implements Serializable {
    // ACHTUNG! Verhalten der Methoden wurde veraendert, aber NICHT die 
    // Methoden ergaenzt oder geloescht.	

    /** Holds value of property DOCUMENT ME! */
    static final private long serialVersionUID = -2922096217960686129L;
    private String corpus_name; // Meta
    private String corpus_id;
    private String corpus_description;
    private String corpus_format;
    private int corpus_size;
    private String corpus_author;
    private String corpus_date;
    private String corpus_history;
    private int sent_count; // Statistik
    private int t_count;
    private int nt_count;
    private long edge_count;
    private boolean edgesLabeled; // Struktur
    private boolean crossingEdges;
    private boolean secondaryEdges;
    private List tfeature_list = new ArrayList(); //features
    private List ntfeature_list = new ArrayList();
    private Feature edge; // Edges
    private Feature secedge; // Edges

    /** Konstruktor besetzt die Attribute initial. */
    public Header() {
        sent_count = 0;
        t_count = 0;
        nt_count = 0;
        edge_count = 0;
        reset();
    }

    /** Setzt die Metadaten eines Korpus zurueck. */
    public final void reset() {
        corpus_name = "";
        corpus_id = "";
        corpus_description = "";
        corpus_format = "";
        corpus_author = "";
        corpus_date = "";
        corpus_history = "";

        crossingEdges = false;
        edgesLabeled = false;
        secondaryEdges = false;

        tfeature_list.clear();
        ntfeature_list.clear();
        edge = null;
        secedge = null;
    }

    /** Zuweisung des Korpusnamens. */
    public final void setCorpus_Name(String corpus_name) {
        this.corpus_name = corpus_name;
    }

    /** Abfragen des Korpusnamens. */
    public final String getCorpus_Name() {
        return corpus_name;
    }

    /** Zuweisung der Korpus-ID. */
    public final void setCorpus_ID(String corpus_id) {
        this.corpus_id = corpus_id;
    }

    /** Abfragen der Korpus-ID. */
    public final String getCorpus_ID() {
        return corpus_id;
    }

    /** Zuweisung der Korpus-Kurzbeschreibung. */
    public final void setCorpus_Description(String corpus_description) {
        this.corpus_description = corpus_description;
    }

    /** Abfragen der Korpus-Kurzbeschreibung. */
    public final String getCorpus_Description() {
        return corpus_description;
    }

    /** Zuweisung des Ausgangs-Formats (Negra usw.). */
    public final void setCorpus_Format(String corpus_format) {
        this.corpus_format = corpus_format;
    }

    /** Abfragen des Ausgangs-Formats. */
    public final String getCorpus_Format() {
        return corpus_format;
    }

    /** Zuweisung des Autors. */
    public final void setCorpus_Author(String corpus_author) {
        this.corpus_author = corpus_author;
    }

    /** Abfragen des Autors. */
    public final String getCorpus_Author() {
        return corpus_author;
    }

    /** Zuweisung des Datums. */
    public final void setCorpus_Date(String corpus_date) {
        this.corpus_date = corpus_date;
    }

    /** Abfragen des Datums. */
    public final String getCorpus_Date() {
        return corpus_date;
    }

    /** Zuweisung der History-Angaben. */
    public final void setCorpus_History(String corpus_history) {
        this.corpus_history = corpus_history;
    }

    /** Abfragen der History-Angaben. */
    public final String getCorpus_History() {
        return corpus_history;
    }

    /** Ermittelt die Anzahl der verwalteten Korpusgraphen. */
    public int getNumberOfSentences() {
        return sent_count;
    }

    /** Ermittelt die Anzahl der verwalteten T-Knoten. */
    public int getNumberOfTNodes() {
        return t_count;
    }

    /** Ermittelt die Anzahl der verwalteten NT-Knoten. */
    public int getNumberOfNTNodes() {
        return nt_count;
    }

    /** Ermittelt die Anzahl der verwalteten Kanten. */
    public long getNumberOfEdges() {
        return edge_count;
    }

    /** Besetzt die Anzahl der verwalteten Saetze. */
    public void setNumberOfSentences(int sent_count) {
        this.sent_count = sent_count;
    }

    /** Ermittelt die Anzahl der verwalteten T-Knoten. */
    public void setNumberOfTNodes(int t_count) {
        this.t_count = t_count;
    }

    /** Ermittelt die Anzahl der verwalteten NT-Knoten. */
    public void setNumberOfNTNodes(int nt_count) {
        this.nt_count = nt_count;
    }

    /** Ermittelt die Anzahl der verwalteten Kanten. */
    public void setNumberOfEdges(long edge_count) {
        this.edge_count = edge_count;
    }

    /** Besitzt Korpus gelabelte Kanten? */
    public final boolean edgesLabeled() {
        return edgesLabeled;
    }

    /** Besetzt die Labeleigenschaft des Korpus. */
    public final void setEdgesLabeled() {
        edgesLabeled = true;
    }

    /** Besetzt die SecondaryEdges-Eigenschaft des Korpus. */
    public final void setSecondaryEdges() {
        secondaryEdges = true;
    }

    /** Besitzt das Korpus secondary edges ? */
    public final boolean secondaryEdges() {
        return secondaryEdges;
    }

    /** Besitzt Korpus kreuzende Kanten? */
    public final boolean crossingEdges() {
        return crossingEdges;
    }

    /** Besetzt die Kreuzende-Kanten-Eigenschaft des Korpus. */
    public final void setCrossingEdges() {
        crossingEdges = true;
    }

    /** Hinzufuegen eines T-Features. */
    public final void addTerminalFeature(Feature feature) {
        int index = tfeature_list.indexOf(feature);

        if (index == -1) {
            tfeature_list.add(feature);
        } else {
            tfeature_list.set(index, feature);
        }
    }

    /** Hinzufuegen eines NT-Features. */
    public final void addNonterminalFeature(Feature feature) {
        int index = ntfeature_list.indexOf(feature);

        if (index == -1) {
            ntfeature_list.add(feature);
        } else {
            ntfeature_list.set(index, feature);
        }
    }

    /** Hinzufuegen eines T-/NT-Features. */
    public final void addGeneralFeature(Feature feature) {
        addTerminalFeature(feature);
        addNonterminalFeature(feature);
    }

    /** Fuegt ein neues Label hinzu. */
    public final void setEdgeFeature(Feature edge) {
        this.edge = edge;
    }

    /** Fragt das Kantenlanbel-Feature ab. */
    public final Feature getEdgeFeature() {
        return edge;
    }

    /** Ist ein Label korrekt? (Ist auch der Fall, wenn die Liste nicht vorbesetzt ist.) */
    public final boolean isEdgeLabel(String label) {
        return edge.isItem(label);
    }

    /** Gib alle Label als Array zurueck. */
    public final List getAllEdgeLabels() {
        return edge.getItems();
    }

    /** Fuegt ein neues Label hinzu. */
    public final void setSecEdgeFeature(Feature secedge) {
        this.secedge = secedge;
    }

    /** Fuegt ein neues Label hinzu. */
    public final Feature getSecEdgeFeature() {
        return secedge;
    }

    /** Ist ein Label korrekt? (Ist auch der Fall, wenn die Liste nicht vorbesetzt ist.) */
    public final boolean isSecondaryEdgeLabel(String label) {
        return secedge.isItem(label);
    }

    /** Gib alle Label als Array zurueck. */
    public final List getAllSecondaryEdgeLabels() {
        return secedge.getItems();
    }

    /** Zulaessiger FeatureName? */
    public final boolean isFeature(String feature) {
        Feature dummy = getFeature(feature);

        if (dummy != null) {
            return true;
        } else {
            return false;
        }
    }

    /** Hole alle Items des List-Features. */
    public final List getFeatureItems(String feature) {
        Feature test = getFeature(feature);

        return test.getItems();
    }

    /** Gueltiges Feature-Item? */
    public final boolean isFeatureItem(String feature, String item) {
        Feature test = getFeature(feature);

        return test.isItem(item);
    }

    /** Einholen eines T-Features. */
    public final Feature getTFeature(String name) {
        int size = tfeature_list.size();

        for (int i = 0; i < size; i++) {
            if (name.equals((String) ((Feature) tfeature_list.get(i)).getName())) {
                return ((Feature) tfeature_list.get(i));
            }
        }

        return null;
    }

    /** Einholen eines NT-Features. */
    public final Feature getNTFeature(String name) {
        int size = ntfeature_list.size();

        for (int i = 0; i < size; i++) {
            if (name.equals(
                        (String) ((Feature) ntfeature_list.get(i)).getName())) {
                return ((Feature) ntfeature_list.get(i));
            }
        }

        return null;
    }

    /** Einholen eines beliebigen Features. */
    public final Feature getFeature(String name) {
        for (int i = 0; i < tfeature_list.size(); i++)
            if (name.equals(((Feature) tfeature_list.get(i)).getName())) {
                return ((Feature) tfeature_list.get(i));
            }

        for (int i = 0; i < ntfeature_list.size(); i++)
            if (name.equals(((Feature) ntfeature_list.get(i)).getName())) {
                return ((Feature) ntfeature_list.get(i));
            }

        if (name.equals(ims.tiger.system.Constants.EDGE)) {
            return edge;
        }

        if (name.equals(ims.tiger.system.Constants.SECEDGE)) {
            return secedge;
        }

        return null;
    }

    /** Prueft, ob dieses Feature auf terminalen Knoten gueltig ist. */
    public final boolean isTerminalFeature(String feature) {
        Feature f = getTFeature(feature);

        if (f == null) {
            return false;
        }

        return true;
    }

    /** Prueft, ob dieses Feature auf nicht-terminalen Knoten gueltig ist. */
    public final boolean isNonterminalFeature(String feature) {
        Feature f = getNTFeature(feature);

        if (f == null) {
            return false;
        }

        return true;
    }

    /** Prueft, ob dieses Feature auf terminalen UND nicht-terminalen Knoten gueltig ist. */
    public final boolean isGeneralFeature(String feature) {
        Feature f1 = getTFeature(feature);
        Feature f2 = getNTFeature(feature);

        if ((f1 != null) && (f2 != null)) {
            return true;
        } else {
            return false;
        }
    }

    /** Liefert alle auf terminalen Knoten gueltigen Features. */
    public final List getAllTerminalFeatures() {
        return tfeature_list;
    }

    /** Liefert die Anzahl aller auf terminalen Knoten gueltigen Features. */
    public final int getAllTerminalFeaturesSize() {
        return tfeature_list.size();
    }

    /** Liefert alle auf nicht-terminalen Knoten gueltigen Features. */
    public final List getAllNonterminalFeatures() {
        return ntfeature_list;
    }

    /** Liefert die Anzahl aller auf nicht-terminalen Knoten gueltigen Features. */
    public final int getAllNonterminalFeaturesSize() {
        return ntfeature_list.size();
    }

    /** Liefert alle NAMEN der terminalen UND nichtterminalen Knoten gueltigen Features.
     *  ACHTUNG! Methodenname ist NICHT SPRECHEND! */
    public final List getAllGeneralFeatures() {
        List result = new ArrayList();

        for (int i = 0; i < tfeature_list.size(); i++)
            for (int j = 0; j < ntfeature_list.size(); j++)
                if (tfeature_list.get(i).equals(ntfeature_list.get(j))) {
                    result.add((String) ((Feature) tfeature_list.get(i)).getName());
                }

        return result;
    }

    /** Abfragen aller Featurenamen, unabhaengig von T-/NT-Gueltigkeit */
    public final List getAllFeatureNames() {
        List result = new ArrayList();

        Set names = new HashSet();
        names.addAll(getAllTFeatureNames());
        names.addAll(getAllNTFeatureNames());

        Object[] mynames = names.toArray();

        for (int i = 0; i < mynames.length; i++) {
            result.add((String) mynames[i]);
        }

        return result;
    }

    /** Abfragen aller TFeaturenamen */
    public final List getAllTFeatureNames() {
        List result = new ArrayList();
        int size = tfeature_list.size();

        for (int i = 0; i < size; i++) {
            result.add((String) ((Feature) tfeature_list.get(i)).getName());
        }

        return result;
    }

    /** Abfragen aller NTFeaturenamen */
    public final List getAllNTFeatureNames() {
        List result = new ArrayList();
        int size = ntfeature_list.size();

        for (int i = 0; i < size; i++) {
            result.add((String) ((Feature) ntfeature_list.get(i)).getName());
        }

        return result;
    }

    /** Serialisieren des Headers. */
    public final void save(String directory) throws IOException {
        ObjectOutputStream p = new ObjectOutputStream(new BufferedOutputStream(
                    new GZIPOutputStream(
                        new FileOutputStream(directory + "corpus.header"))));
        p.writeObject(this);
        p.close();
    }

    /* Debugging-Methode: Ausdrucken der Informationen */
    public void print() {
        System.out.println(corpus_name);
        System.out.println(corpus_format);

        for (int i = 0; i < tfeature_list.size(); i++) {
            ((Feature) tfeature_list.get(i)).print();
        }

        for (int i = 0; i < ntfeature_list.size(); i++) {
            ((Feature) ntfeature_list.get(i)).print();
        }

        if (edge != null) {
            edge.print();
        }

        if (secedge != null) {
            secedge.print();
        }
    }
}
