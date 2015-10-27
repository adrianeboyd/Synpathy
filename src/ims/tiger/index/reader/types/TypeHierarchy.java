/*
 * File:     TypeHierarchy.java
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
package ims.tiger.index.reader.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;


/**
 * Diese Klasse stellt die interne Repraesentation einer Typhierarchie dar.
 * Die Datenstruktur verwaltet pro NT die Liste aller Ts sowie einen Bitvektor,
 * der die Zugehoerigkeit aller im Universum definierten Ts zum Typ definiert.
 * Dadurch werden Mengenoperationen von zwei Typen vereinfacht.
 *
 * @version 21.8.2000
 * @author Wolfgang Lezius
 */
public class TypeHierarchy {
    private String featurename;
    private String root;
    private HashMap type_bitsets;
    private HashMap type_values;
    private ArrayList values;
    private HashMap displayrules;
    private HashMap rules;
    private HashMap comments;

    /** Konstruktor der Typhierarchie. */
    public TypeHierarchy(String featurename, String root, HashMap type_bitsets,
        HashMap type_values, ArrayList values, HashMap rules,
        HashMap displayrules, HashMap comments) {
        this.featurename = featurename;
        this.root = root;
        this.type_bitsets = type_bitsets;
        this.type_values = type_values;
        this.values = values;
        this.rules = rules;
        this.displayrules = displayrules;
        this.comments = comments;
    }

    /** Gibt den Featurenamen aus. */
    public String getFeatureName() {
        return featurename;
    }

    /** Gibt das Wurzelelement aus. */
    public String getRoot() {
        return root;
    }

    /** Gibt die Belegungen des Universums an. */
    public ArrayList getUniverse() {
        return getTypeValues(getRoot());
    }

    /** Liste der Namen aller definierten Typen. */
    public List getTypeNames() {
        Object[] names = type_bitsets.keySet().toArray();
        Arrays.sort(names);

        List l = new ArrayList();

        for (int i = 0; i < names.length; i++) {
            l.add((String) names[i]);
        }

        return l;
    }

    /** Erzeugt HTML-Dokumentation der Typhierarchie fuer GUI-Zwecke. */
    public String getHTMLTypeDocumentation() {
        StringBuffer doku = new StringBuffer();
        doku.append("<TABLE BORDER=\"0\">\n");

        Object[] names = type_bitsets.keySet().toArray();
        Arrays.sort(names);

        List l = new ArrayList();
        String head;

        for (int i = 0; i < names.length; i++) {
            head = (String) names[i];
            doku.append(" <TR><TD>");

            if (head.equals(root)) {
                head = "<FONT FACE=\"red\">" + head + "</FONT>";
            }

            doku.append(head);
            doku.append("</TD><TD>:=</TD><TD>");
            doku.append((String) displayrules.get(head));
            doku.append("</TD></TR>\n");
        }

        doku.append("</TABLE>\n");

        return doku.toString();
    }

    /** Erzeugt Text-Dokumentation der Typhierarchie fuer GUI-Zwecke. */
    public List getTextTypeDocumentation() {
        List doku = new ArrayList();

        Object[] names = type_bitsets.keySet().toArray();
        Arrays.sort(names);

        List l = new ArrayList();
        String head;
        String rule;

        for (int i = 0; i < names.length; i++) {
            head = (String) names[i];
            rule = (String) displayrules.get(head);

            if (head.equals(root)) {
                rule += "   (UserDefConstant)";
            }

            doku.add(head + "  :=  " + rule);
        }

        return doku;
    }

    /** Erzeugt eine JTree-Dokumentation der Typhierarchie fuer GUI-Zwecke. */
    /** Delivers all available corpora represented as a corpus tree. */
    public DefaultMutableTreeNode getJTreeTypeDocumentation() {
        DefaultMutableTreeNode rootnode = new DefaultMutableTreeNode(root);
        generateTreeNode(rootnode, root);

        return rootnode;
    }

    private void generateTreeNode(DefaultMutableTreeNode rootnode, String type) {
        // Teminal?
        if (type.startsWith("\"")) {
            rootnode.setUserObject(type);

            return;
        }

        // Subtypen erzeugen und einhaengen
        List children = (List) rules.get(type);
        String child;

        for (int i = 0; i < children.size(); i++) {
            child = (String) children.get(i);

            DefaultMutableTreeNode childnode = new DefaultMutableTreeNode(child);
            rootnode.add(childnode);
            generateTreeNode(childnode, child);
        }
    }

    /** Ist der angegebene Typ bekannt? */
    public boolean isType(String type) {
        return type_values.containsKey(type);
    }

    /** Gibt die Belegungen des angegebenen Typs an. */
    public ArrayList getTypeValues(String type) {
        return (ArrayList) type_values.get(type);
    }

    /** Gibt die Anzahl der Belegungen des Typs an. */
    public int getTypeSize(String type) {
        return ((ArrayList) type_values.get(type)).size();
    }

    /** Gibt die inversen Belegungen des angegebenen Typs an. */
    public ArrayList getInverseTypeValues(String type) {
        ArrayList universe = getUniverse();
        ArrayList result = new ArrayList();
        BitSet typesets = getTypeVector(type);

        for (int i = 0; i < universe.size(); i++) {
            if (!typesets.get(i)) {
                result.add(universe.get(i));
            }
        }

        return result;
    }

    /** Gibt den Bitvektor des angegebenen Typs an. */
    public BitSet getTypeVector(String type) {
        return (BitSet) type_bitsets.get(type);
    }

    /** Ist die Konstante im Typ enthalten? */
    public boolean isConstMemberOfType(String constant, String type) {
        ArrayList list = getTypeValues(type);

        for (int i = 0; i < list.size(); i++) {
            if (constant.equals(list.get(i))) {
                return true;
            }
        }

        return false;
    }

    /** Macht die Konstante den gesamten Type aus? */
    public boolean isConstHoleType(String constant, String type) {
        if ((isConstMemberOfType(constant, type)) && (getTypeSize(type) == 1)) {
            return true;
        }

        return false;
    }

    /** Ist der TypA im TypB enthalten? */
    public boolean isSubType(String type_a, String type_b) {
        BitSet bita = getTypeVector(type_a);
        BitSet bitb = getTypeVector(type_b);

        BitSet test = (BitSet) bita.clone(); // t = a AND b
        test.and(bitb);

        if (test.equals(bita)) {
            return true;
        } // t = a ?
        else {
            return false;
        }
    }

    /** Sind TypA im TypB disjunkt? */
    public boolean areDisjunct(String type_a, String type_b) {
        BitSet bita = getTypeVector(type_a);
        BitSet bitb = getTypeVector(type_b);

        BitSet empty = new BitSet();
        BitSet test = (BitSet) bita.clone(); // t = a AND b
        test.and(bitb);

        if (test.equals(empty)) {
            return true;
        } // t = empty ?
        else {
            return false;
        }
    }

    /** Uebergibt - falls verfuegbar - einen Kommentartext fuer
     *  ein Typsymbol oder fuer eine Konstante.
     */
    public String getTypeOrConstantComment(String symbol) {
        if (comments.containsKey(symbol)) {
            return (String) comments.get(symbol);
        } else {
            return null;
        }
    }

    /** Debug-Methode: Ausdrucken der internen Repraesentation. */
    public void print() {
        for (int i = 0; i < values.size(); i++) {
            System.out.print(i + "-" + (String) values.get(i) + " ");
        }

        System.out.println();

        String key;
        Object[] keys = (type_bitsets.keySet()).toArray();

        for (int i = 0; i < keys.length; i++) {
            key = (String) keys[i];

            BitSet bit = (BitSet) type_bitsets.get(key);
            System.out.println(key + ": " + bit);
            System.out.println(key + ": " + (ArrayList) type_values.get(key));
        }
    }
}
