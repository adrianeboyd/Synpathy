/*
 * File:     Sentence.java
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

import ims.tiger.system.*;

import java.util.*;


/** Diese Klasse definiert einen Korpusgraphen, d.h. einen Korpussatz
 *  mit seiner syntaktischen Annotation. Die Definition greift
 *  auf die Definitionen der Knotentypen zurueck.
 */
public class Sentence {
    /** Holds value of property DOCUMENT ME! */
    protected ArrayList terminals;

    /** Holds value of property DOCUMENT ME! */
    protected ArrayList nonterminals;

    /** Holds value of property DOCUMENT ME! */
    protected int root;

    /** Holds value of property DOCUMENT ME! */
    protected ArrayList coreference;

    /** Holds value of property DOCUMENT ME! */
    protected String id;

    /** Konstruktor. Initialisiert die Liste der terminalen und nicht-terminalen Knoten. */
    public Sentence() {
        terminals = new ArrayList();
        nonterminals = new ArrayList();
        coreference = new ArrayList();
        root = -1;
    }

    /** Dublizierungsmethode. */
    public Object clone() throws CloneNotSupportedException {
        Sentence result = new Sentence();

        result.terminals = new ArrayList();

        for (int i = 0; i < terminals.size(); i++) {
            result.terminals.add(((T_Node) terminals.get(i)).clone());
        }

        result.nonterminals = new ArrayList();

        for (int i = 0; i < nonterminals.size(); i++) {
            result.nonterminals.add(((NT_Node) nonterminals.get(i)).clone());
        }

        result.coreference = (ArrayList) coreference.clone(); // !!! BEI SPAETERER BENUTZUNG CLONEN IMPLEMENTIEREN

        result.root = root;
        result.id = id;

        return result;
    }

    /** Setzt den Satz zurueck, d.h. bereitet ihn fuer die Wiederverwendung vor. */
    public void reset() {
        terminals.clear();
        nonterminals.clear();
        coreference.clear();
        root = -1;
    }

    /** Besetzt die Terminal-Liste. */
    public final void setTerminals(ArrayList terminals) {
        this.terminals = terminals;
    }

    /** Besetzt die Nicht-Terminal-Liste. */
    public final void setNonterminals(ArrayList nonterminals) {
        this.nonterminals = nonterminals;
    }

    /** Liefert die Terminal-Liste. */
    public final ArrayList getTerminals() {
        return terminals;
    }

    /** Liefert die Terminal-Liste als Array. */
    public final Object[] getTerminalsArray() {
        return terminals.toArray();
    }

    /** Liefert die Nicht-Terminal-Liste. */
    public final ArrayList getNonterminals() {
        return nonterminals;
    }

    /** Liefert die Nicht-Terminal-Liste als Array. */
    public final Object[] getNonterminalsArray() {
        return nonterminals.toArray();
    }

    /** Fuegt einen terminalen Knoten hinzu. */
    public final void addTerminal(T_Node tnode) {
        terminals.add(tnode);
    }

    /** Fuegt einen nicht-terminalen Knoten hinzu. */
    public final void addNonterminal(NT_Node ntnode) {
        nonterminals.add(ntnode);
    }

    /** Besetzt den Wurzelknoten. */
    public final void setRoot(int root) {
        this.root = root;
    }

    /** Liefert die Anzahl der terminalen Knioten. */
    public final int getTerminalsSize() {
        return terminals.size();
    }

    /** Liefert die Anzahl der nicht-terminalen Knoten. */
    public final int getNonterminalsSize() {
        return nonterminals.size();
    }

    /** Liefert den Wurzelknoten. */
    public final int getRoot() {
        return root;
    }

    /** Liefert den terminalen Knoten mit der Nummer index. */
    public final Object getTerminalAt(int index) {
        if (index >= Constants.CUT) {
            index -= Constants.CUT;
        }

        return terminals.get(index);
    }

    /** Liefert den nicht-terminalen Knoten mit der Nummer index. */
    public final Object getNonterminalAt(int index) {
        return nonterminals.get(index);
    }

    /** Liefert die interne Nummer des Terminals mit dem angegebenen Attribut-Wert-Paar. */
    public final int getTerminalPositionOf(String feature, String value) {
        for (int i = 0; i < terminals.size(); i++) {
            if (value.equals(((T_Node) terminals.get(i)).getFeature(feature))) {
                return i;
            }
        }

        return -1;
    }

    /** Liefert die interne Nummer des Terminals mit der angegebenen ID. */
    public final int getTerminalPositionOf(String id) {
        for (int i = 0; i < terminals.size(); i++) {
            if (id.equals(((T_Node) terminals.get(i)).getID())) {
                return i;
            }
        }

        return -1;
    }

    /** Liefert die interne Nummer des Nonterminals mit dem angegebenen Attribut-Wert-Paar. */
    public final int getNonterminalPositionOf(String feature, String value) {
        for (int i = 0; i < nonterminals.size(); i++) {
            if (value.equals(((NT_Node) nonterminals.get(i)).getFeature(feature))) {
                return i;
            }
        }

        return -1;
    }

    /** Liefert die interne Nummer des Nonterminals mit der angegebenen ID. */
    public final int getNonterminalPositionOf(String id) {
        for (int i = 0; i < nonterminals.size(); i++) {
            if (id.equals(((NT_Node) nonterminals.get(i)).getID())) {
                return i;
            }
        }

        return -1;
    }

    /** Liefert den Knoten mit der angegebenen Nummer. */
    public final Node getNode(int nummer) {
        if (nummer >= Constants.CUT) { /* NT-Knoten */
            nummer -= Constants.CUT;

            return (NT_Node) nonterminals.get(nummer);
        }
        else { /* T-Knoten */

            return (T_Node) terminals.get(nummer);
        }
    }

    /** Ermittelt die Anzahl der Token-Kinder des Knotens. */
    public final int getTokenChildren(int nummer) {
        Node n = getNode(nummer);

        if (n.isTerminal()) {
            return 1;
        }
        else {
            int summe = 0;
            int son;
            NT_Node nt = (NT_Node) n;

            for (int i = 0; i < nt.getChildsSize(); i++) {
                son = ((Integer) nt.getChildAt(i)).intValue();
                summe += getTokenChildren(son);
            }

            return summe;
        }
    }

    /** Besetzt die ID des Graphen. */
    public final void setSentenceID(String id) {
        this.id = id;
    }

    /** Liefert die ID des Graphen. */
    public final String getSentenceID() {
        return id;
    }

    /** Fuegt eine neue sekundaere Kante hinzu. */
    public final void addCoreference(int nr1, String label, int nr2) {
        coreference.add(new Integer(nr1));
        coreference.add(label);
        coreference.add(new Integer(nr2));
    }

    /** Gibt die Anzahl der abgelegten sekundaeren Kanten an. */
    public final int getCoreferenceSize() {
        return coreference.size() / 3;
    }

    /** Gibt die Knotennummer des ersten Knoten zurueck. Numerierung beginnt bei 0! */
    public final int getCoreferenceNode1(int pos) {
        Integer i = (Integer) coreference.get(3 * pos);

        return i.intValue();
    }

    /** Gibt die Knotennummer des zweiten Knoten zurueck. Numerierung beginnt bei 0! */
    public final int getCoreferenceNode2(int pos) {
        Integer i = (Integer) coreference.get((3 * pos) + 2);

        return i.intValue();
    }

    /** Uebergibt die interne Verwaltung der sekundaeren Kanten. */
    public final ArrayList getCoreference() {
        return coreference;
    }

    /** Gibt die Kantenbeschriftung zurueck. Numerierung beginnt bei 0! */
    public final String getCoreferenceLabel(int pos) {
        return (String) coreference.get((3 * pos) + 1);
    }

    /** Gibt alle sekundaeren Kanten an, an denen die angegebene Knotennummer UNABHAENGIG VON DER POSITION beteiligt ist.
        Die sek. Kanten werden in einer Liste angeordnet. In der ersten Zeile befindet sich jeweils
        der Label (String), in der zweiten Zeile der Partnerknoten (int).

        Diese Methode ist symmetrisch.
      */
    public final ArrayList getSymmCoreferences(int nr) {
        ArrayList result = new ArrayList();

        for (int i = 0; i < getCoreferenceSize(); i++) {
            if (nr == getCoreferenceNode1(i)) {
                result.add(getCoreferenceLabel(i));
                result.add(new Integer(getCoreferenceNode2(i)));
            }
            else if (nr == getCoreferenceNode2(i)) {
                result.add(getCoreferenceLabel(i));
                result.add(new Integer(getCoreferenceNode1(i)));
            }
        }

        return result;
    }

    /** Gibt alle sekundaeren Kanten an, an denen die angegebene Knotennummer ALS LINKER KNOTEN beteiligt ist.
        Die sek. Kanten werden in einer Liste angeordnet. In der ersten Zeile befindet sich jeweils
        der Label (String), in der zweiten Zeile der Partnerknoten (int).

        Diese Methode ist asymmetrisch.
      */
    public final ArrayList getAsymmCoreferences(int nr) {
        ArrayList result = new ArrayList();

        for (int i = 0; i < getCoreferenceSize(); i++) {
            if (nr == getCoreferenceNode1(i)) {
                result.add(getCoreferenceLabel(i));
                result.add(new Integer(getCoreferenceNode2(i)));
            }
        }

        return result;
    }

    /* Debugging-Methode: Visualisierung des Baums auf dem Bildschirm. */
    public final void printTree() {
        printSubTree(getRoot(), 0);

        for (int i = 0; i < getCoreferenceSize(); i++) {
            System.out.println(getCoreferenceNode1(i) + " >~" +
                getCoreferenceLabel(i) + " " + getCoreferenceNode2(i));
        }
    }

    /* Rekursive Hilfsroutine dazu */
    private void printSubTree(int nodenr, int gaplength) {
        /* 1. Knoten ausgeben */
        Node node = null;
        NT_Node ntnode = null;

        if (nodenr >= Constants.CUT) {
            ntnode = (NT_Node) getNonterminalAt(nodenr - Constants.CUT);
            node = (Node) ntnode;
        }
        else {
            node = (Node) getTerminalAt(nodenr);
        }

        String gap = "";
        int gapende = gaplength;

        if (nodenr < Constants.CUT) {
            gapende = 65;
        }

        for (int i = 0; i < gapende; i++) {
            gap += " ";
        }

        Object[] valuearray = node.getAllFeatureValues();
        String knoten = "";

        for (int i = 0; i < valuearray.length; i++) {
            knoten += (((String) valuearray[i]) + " ");
        }

        System.out.println(gap + knoten);

        /* 2. Soehne verfolgen */
        int abstand = 15;

        if (nodenr >= Constants.CUT) {
            ArrayList childs = (ArrayList) ntnode.getChilds();

            for (int i = 0; i < childs.size(); i++) {
                int nextnode = ((Integer) childs.get(i)).intValue();

                /* Pfeil zeichnen */
                String pfeil = gap + "     ";

                if (nextnode >= Constants.CUT) {
                    for (int j = gaplength + 5;
                            j < ((gaplength + abstand) - 1); j++) {
                        pfeil += "-";
                    }

                    pfeil += ">";
                }
                else {
                    for (int j = gaplength + 5; j < 64; j++) {
                        pfeil += "-";
                    }

                    pfeil += ">";
                }

                System.out.println(pfeil);

                /* Knoten verfolgen */
                printSubTree(nextnode, gaplength + abstand);
            }
        }
    }

    /* Debugging-Methode: Visualisiserung der Knoten auf dem Bildschirm. */
    public final void printNodes() {
        System.out.println("Mutterknoten: ");
        System.out.println(getRoot());

        System.out.println("=== TERMINAL-Nodes ===");

        Object[] test = terminals.toArray();

        for (int i = 0; i < test.length; i++) {
            ((Node) test[i]).printNode();
        }

        System.out.println("=== NON-TERMINAL-Nodes ===");
        test = nonterminals.toArray();

        for (int i = 0; i < test.length; i++) {
            ((Node) test[i]).printNode();
        }
    }

    /** Ueberprueft den Korpusgraphen auf kreuzende Kanten. */
    public boolean crossingEdges() {
        for (int i = 0; i < this.getNonterminalsSize(); i++) {
            if (discontinuous(i + ims.tiger.system.Constants.CUT)) {
                return true;
            }
        }

        return false;
    }

    /** Ist der angegebene Knoten diskontinuierlich? */
    public boolean discontinuous(int node) {
        HashSet result = calculateProjection(node);

        /* Ist die Projektion aufgebrochen? */
        Object[] projection = result.toArray();
        int min = 99999;
        int max = -1;

        int aktuell;

        for (int i = 0; i < projection.length; i++) { // max und min
            aktuell = ((Integer) projection[i]).intValue();

            if (aktuell > max) {
                max = aktuell;
            }

            if (aktuell < min) {
                min = aktuell;
            }
        }

        if ((min > -1) && (max < 99999)) {
            for (int i = min; i <= max; i++) { // diskret?

                int parent = ((T_Node) getNode(i)).getParent();

                if ((!(result.contains(new Integer(i)))) && // Knoten nicht in der Projektion UND
                        (parent > -1) && // Knoten eingehaengt
                        (!((NT_Node) getNode(parent)).isVirtualRoot()) // haengt nicht an virtueller Wurzel
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    /* Hilfsmethode: Berechnet rekursiv die Projektionen der NT-Knoten. */
    public final HashSet calculateProjection(int root) {
        HashSet result = new HashSet();

        Node node = getNode(root);

        // if REKENDE return;
        if (node.isTerminal()) { /* Terminal */
            result.add(new Integer(root));

            return result;
        }
        else { /* Nicht-Terminal */

            NT_Node ntnode = (NT_Node) node;

            int son;
            HashSet sons;

            /* alle Soehne absuchen */
            for (int i = 0; i < ntnode.getChildsSize(); i++) {
                son = ((Integer) ntnode.getChildAt(i)).intValue();

                sons = calculateProjection(son);

                Object[] sonsa = sons.toArray();

                for (int j = 0; j < sonsa.length; j++) {
                    result.add(sonsa[j]);
                }
            }

            return result;
        }
    }

    /** Ordnet fuer den gesamten Graphen die Kinder aller Knoten nach ihrer Praezedenz an. */
    public final void orderSentenceByPrecedence() {
        NT_Node nt;
        ArrayList childs;
        ArrayList newchilds;

        for (int i = 0; i < nonterminals.size(); i++) {
            nt = (NT_Node) nonterminals.get(i);
            childs = nt.getChilds();
            newchilds = (ArrayList) orderNodesByPrecedence(childs);
            nt.setChilds(newchilds);
        }
    }

    /** Ordnet die angegebenen Knoten nach ihrer Praezedenz. */
    public final List orderNodesByPrecedence(List nodes) {
        List leftsons = new ArrayList();

        for (int i = 0; i < nodes.size(); i++) {
            int n_nr = ((Integer) nodes.get(i)).intValue();
            Node n = getNode(n_nr);

            /* linke Ecke aus der Projektion herausholen */
            HashSet proj = calculateProjection(n_nr);
            Object[] projection = proj.toArray();

            int min = 9999;
            int aktuell;

            for (int j = 0; j < projection.length; j++) {
                aktuell = ((Integer) projection[j]).intValue();

                if (aktuell < min) {
                    min = aktuell;
                }
            }

            leftsons.add(new Integer(min));
        }

        List ordered = new ArrayList();

        while (leftsons.size() > 0) {
            /* linkeste Kante herausholen */
            int min = 9999;
            int pos = 0;
            int aktuell;

            for (int i = 0; i < leftsons.size(); i++) {
                aktuell = ((Integer) leftsons.get(i)).intValue();

                if (aktuell < min) {
                    min = aktuell;
                    pos = i;
                }
            }

            ordered.add(nodes.get(pos));

            leftsons.remove(pos);
            nodes.remove(pos);
        }

        return ordered;
    }
}
