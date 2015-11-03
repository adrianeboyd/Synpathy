/*
 * File:     GraphPanelEditorMouseListener.java
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
package ims.tiger.gui.tigergraphviewer.draw;

import ims.tiger.corpus.NT_Node;
import ims.tiger.corpus.Node;
import ims.tiger.corpus.Sentence;
import ims.tiger.corpus.T_Node;

import ims.tiger.gui.tigergraphviewer.forest.CorpusForest;

import ims.tiger.system.Constants;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;


/**
 * $Id: GraphPanelEditorMouseListener.java,v 1.16 2006/08/28 10:22:54 klasal
 * Exp $
 *
 * @author $Author: klasal $
 * @version $Revision: 1.22 $
 */
public class GraphPanelEditorMouseListener extends GraphPanelMouseListener {
    /** Holds value of property DOCUMENT ME! */
    private final EdgePopupActionListener edgePopupActionListener = new EdgePopupActionListener();

    /** Holds value of property DOCUMENT ME! */
    private final JPopupMenu edgeFeaturePopupMenu = new JPopupMenu();

    /** Holds value of property DOCUMENT ME! */
    private final JPopupMenu edgeOptionPopupMenu = new JPopupMenu();

    /** Holds value of property DOCUMENT ME! */
    private final JPopupMenu nodeOptionPopupMenu = new JPopupMenu();

    /** Holds value of property DOCUMENT ME! */
    private final JPopupMenu ntNodeFeaturePopupMenu = new JPopupMenu();

    /** Holds value of property DOCUMENT ME! */
    private final JPopupMenu secEdgeFeaturePopupMenu = new JPopupMenu();

    /** Holds value of property DOCUMENT ME! */
    private final NodePopupActionListener nodePopupActionListener = new NodePopupActionListener();

    /** Holds value of property DOCUMENT ME! */
    private final SecEdgePopupActionListener secEdgePopupActionListener = new SecEdgePopupActionListener();
    private Action addEdgeAction;
    private Action addParentAction;
    private Action addSecEdgeAction;
    private Action deleteEdgesAction;
    private Action deleteNodesAction;
    private CorpusForest forest;
    private List singleClickedEdges = new ArrayList();
    private List singleClickedNodes = new ArrayList();
    private int clickedSecEdge = -1;

    /**
     * Creates a new GraphPanelEditorMouseListener object.
     *
     * @param panel DOCUMENT ME!
     */
    public GraphPanelEditorMouseListener(GraphPanel panel) {
        super(panel);

        addParentAction = new AbstractAction("Add parent node") {
                    public void actionPerformed(ActionEvent e) {
                        addParent();
                    }
                };
        addParentAction.putValue(Action.SHORT_DESCRIPTION,
            "Add a common parent node to marked nodes");

        addEdgeAction = new AbstractAction("Add edge") {
                    public void actionPerformed(ActionEvent e) {
                        addEdge();
                    }
                };
        addEdgeAction.putValue(Action.SHORT_DESCRIPTION,
            "Add edges from first marked node to other marked nodes");

        addSecEdgeAction = new AbstractAction("Add secondary edge") {
                    public void actionPerformed(ActionEvent e) {
                        addSecEdge();
                    }
                };
        addSecEdgeAction.putValue(Action.SHORT_DESCRIPTION,
            "Add secondary edge pointing to this node");

        deleteEdgesAction = new AbstractAction("Delete") {
                    public void actionPerformed(ActionEvent e) {
                        deleteEdges();
                    }
                };
        deleteEdgesAction.putValue(Action.SHORT_DESCRIPTION,
            "Delete marked edges");

        deleteNodesAction = new AbstractAction("Delete") {
                    public void actionPerformed(ActionEvent e) {
                        deleteNodes();
                    }
                };
        deleteNodesAction.putValue(Action.SHORT_DESCRIPTION,
            "Delete marked nodes");

        nodeOptionPopupMenu.add(new JMenuItem(addParentAction));
        nodeOptionPopupMenu.add(new JMenuItem(addEdgeAction));
        nodeOptionPopupMenu.add(new JMenuItem(addSecEdgeAction));
        nodeOptionPopupMenu.add(new JMenuItem(deleteNodesAction));

        edgeOptionPopupMenu.add(new JMenuItem(deleteEdgesAction));

        InputMap inputMap = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        Object actionKeyDelete = "Delete";
        Object actionKeyShiftReleased = "Add Parent";
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
            actionKeyDelete);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0, true),
            actionKeyShiftReleased);

        ActionMap actionMap = panel.getActionMap();
        actionMap.put(actionKeyDelete,
            new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    deleteNodes();
                    deleteEdges();
                }
            });
        actionMap.put(actionKeyShiftReleased,
            new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    addParent();
                }
            });
    }

    /**
     * DOCUMENT ME!
     *
     * @param forest DOCUMENT ME!
     */
    public void setForest(CorpusForest forest) {
        this.forest = forest;
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param clickedEdgeChild DOCUMENT ME!
     */
    protected void handleEdgeClick(MouseEvent e, DisplayNode clickedEdgeChild) {
        singleClickedNodes.clear();

        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 1)) {
            graphPanel.normalizeHighlights();
            clickedEdgeChild.setHighlightedEdge(true);
            singleClickedEdges.clear();
            singleClickedEdges.add(clickedEdgeChild);
            graphPanel.repaint();
            updateActions();
        } else if (SwingUtilities.isRightMouseButton(e) &&
                (e.getClickCount() == 1)) {
            if (clickedEdgeChild.isHighlightedEdge()) {
                edgeOptionPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        } else if (e.getClickCount() == 2) {
            edgePopupActionListener.clickedEdgeChild = clickedEdgeChild;
            fillMenu(edgeFeaturePopupMenu, Constants.EDGE,
                edgePopupActionListener);
            singleClickedEdges.clear();
            edgeFeaturePopupMenu.show(e.getComponent(), e.getX(), e.getY());
        } else {
            super.handleEdgeClick(e, clickedEdgeChild);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param clickedNodeNr DOCUMENT ME!
     */
    protected void handleNodeClick(MouseEvent e, int clickedNodeNr) {
        if (SwingUtilities.isMiddleMouseButton(e)) {
            super.handleNodeClick(e, clickedNodeNr);
        } else if (e.getClickCount() == 1) {
            DisplayNode node = graphPanel.getCurrentDisplaySentence()
                                         .getDisplayNode(clickedNodeNr);

            if (SwingUtilities.isLeftMouseButton(e)) {
                if (!e.isControlDown()) {
                    reset();

                    //only activate graphPanel if control is not pressed
                    graphPanel.setHighlightedNode(node);
                }

                if (node.isHighlightedNode()) {
                    singleClickedNodes.remove(new Integer(clickedNodeNr));
                    node.setHighlightedNode(false);
                } else {
                    node.setHighlightedNode(true);
                    addToClickedNodes(clickedNodeNr);
                }

                graphPanel.repaint();
                updateActions();
            } else if (SwingUtilities.isRightMouseButton(e)) {
                if (node.isHighlightedNode()) {
                    nodeOptionPopupMenu.show(e.getComponent(), e.getX(),
                        e.getY());
                }
            }
        } else if (SwingUtilities.isLeftMouseButton(e) &&
                (e.getClickCount() == 2)) {
            if (displaySentence.getSentence().getNode(clickedNodeNr) instanceof NT_Node) {
                nodePopupActionListener.nodeNr = clickedNodeNr;
                fillMenu(ntNodeFeaturePopupMenu,
                    (String) graphPanel.getHeader().getAllNTFeatureNames().get(0),
                    nodePopupActionListener);
                singleClickedNodes.clear();
                ntNodeFeaturePopupMenu.show(e.getComponent(), e.getX(), e.getY());
            } else {
                T_Node node = (T_Node) displaySentence.getSentence()
                                                      .getTerminalAt(clickedNodeNr);
                TerminalNodeEditor editor = new TerminalNodeEditor(node,
                        graphPanel.getHeader(), graphPanel, forest);
                editor.setVisible(true);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param i DOCUMENT ME!
     */
    protected void handleSecEdgeClick(MouseEvent e, int i) {
        singleClickedNodes.clear();

        if (SwingUtilities.isLeftMouseButton(e) && (e.getClickCount() == 1)) {
            if (displaySentence.isHighlightedSecEdge(i)) {
                displaySentence.setHighlightedSecEdge(i, false);
                clickedSecEdge = -1;
            } else {
                displaySentence.setHighlightedSecEdge(i, true);
                clickedSecEdge = i;
            }

            updateActions();
            graphPanel.repaint();
        } else if (SwingUtilities.isRightMouseButton(e) &&
                (e.getClickCount() == 1)) {
            if (displaySentence.isHighlightedSecEdge(i)) {
                edgeOptionPopupMenu.show(e.getComponent(), e.getX(), e.getY());
            }
        } else if (e.getClickCount() == 2) {
            secEdgePopupActionListener.secEdgeNr = i;
            fillMenu(secEdgeFeaturePopupMenu, Constants.SECEDGE,
                secEdgePopupActionListener);
            clickedSecEdge = -1;
            secEdgeFeaturePopupMenu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    /**
     * DOCUMENT ME!
     */
    protected void reset() {
        singleClickedNodes.clear();
        singleClickedEdges.clear();

        if (clickedSecEdge != -1) {
            displaySentence.setHighlightedSecEdge(clickedSecEdge, false);
            clickedSecEdge = -1;
        }

        updateActions();
        super.reset();
    }

    /**
     * note that this method only updates the model (ims.tiger.corpus), not the
     * graphic components (Display_Node, etc.) !
     *
     * @param sentence
     * @param parentNode
     * @param childNodes
     */
    private static void addChildren(Sentence sentence, NT_Node parentNode,
        List childNodes) {
        int parentNodeNr = sentence.getNonterminals().indexOf(parentNode);

        for (int i = 0; i < childNodes.size(); i++) {
            int childNodeNr = ((Integer) childNodes.get(i)).intValue();
            parentNode.addChild(childNodeNr);

            Node childNode = sentence.getNode(childNodeNr);
            childNode.setParent(parentNodeNr + Constants.CUT);
            childNode.setFeature(Constants.EDGE, "");
        }
    }

    private void addEdge() {
        Sentence sentence = displaySentence.getSentence();

        Node outgoingNode = sentence.getNode(((Integer) singleClickedNodes.get(
                    0)).intValue());

        if (!(outgoingNode instanceof NT_Node)) {
            return;
        }

        singleClickedNodes.remove(0);

        addChildren(sentence, (NT_Node) outgoingNode, singleClickedNodes);

        sentence.orderSentenceByPrecedence();
        displaySentence.init();
        graphPanel.update();
        graphPanel.repaint();
        forest.setModified(true);
        reset();
    }

    private void addParent() {
        Sentence sentence = displaySentence.getSentence();

        NT_Node node = new NT_Node();
        node.setID(proposeNodeId(sentence));

        List ntFeatureNames = graphPanel.getHeader().getAllNTFeatureNames();

        for (int i = 0; i < ntFeatureNames.size(); i++) {
            node.addFeature((String) ntFeatureNames.get(i), "");
        }

        sentence.addNonterminal(node);

        // handle joining of nodes with parent
        // all nodes have the same parent - otherwise addParentAction is disabled
        int parent_id = sentence.getNode(((Integer) singleClickedNodes.get(0)).intValue()).getParent();
        if (parent_id != -1) {
            // remove all edges between clickedNodes and parent
            for (int i = 0; i < singleClickedNodes.size(); i++) {
                deleteEdge(sentence,
                           sentence.getNode(((Integer) singleClickedNodes.get(i)).intValue()));
            }

            // add the created node as child of the parent node
            ArrayList list = new ArrayList();
            list.add(sentence.getNonterminalPositionOf(node.getID()) + ims.tiger.system.Constants.CUT);
            addChildren(sentence, (NT_Node) sentence.getNode(parent_id), list);
        }

        addChildren(sentence, node, singleClickedNodes);

        sentence.orderSentenceByPrecedence();
        displaySentence.init();
        graphPanel.update();
        graphPanel.repaint();
        forest.setModified(true);
        reset();
    }

    /**
     * note that in this case model and graphical components are identical, so
     * it's not necessary to create a new Display_Sentence
     */
    private void addSecEdge() {
        Sentence sentence = graphPanel.getCurrentSentence();
        int outNodeNr = ((Integer) singleClickedNodes.get(0)).intValue();
        int inNodeNr = ((Integer) singleClickedNodes.get(1)).intValue();
        sentence.addCoreference(outNodeNr, "", inNodeNr);
        graphPanel.repaint();
        forest.setModified(true);
        reset();
    }

    /**
     * adds a clicked node to the set of marked nodes, if there is no direct
     * relationship between the new node and the already marked nodes.
     *
     * @param newNodeNr
     */
    private void addToClickedNodes(int newNodeNr) {
        if (singleClickedNodes.contains(new Integer(newNodeNr))) {
            return;
        }

        for (int i = 0; i < singleClickedNodes.size(); i++) {
            int currentClickedNodeNr = ((Integer) singleClickedNodes.get(i)).intValue();
            DisplayNode currentNode = displaySentence.getDisplayNode(currentClickedNodeNr);

            //test if ancestor of new node already selected
            int parentNodeNr;
            DisplayNode newNode = displaySentence.getDisplayNode(newNodeNr);
            DisplayNode loopNode = newNode;

            while ((parentNodeNr = loopNode.getNode().getParent()) != -1) {
                if (parentNodeNr == currentClickedNodeNr) {
                    newNode.setHighlightedNode(false);

                    return;
                }

                loopNode = displaySentence.getDisplayNode(parentNodeNr);
            }

            loopNode = currentNode;

            //test if new node is ancestor of already selected one
            while ((parentNodeNr = loopNode.getNode().getParent()) != -1) {
                if (parentNodeNr == newNodeNr) {
                    currentNode.setHighlightedNode(false);
                    singleClickedNodes.remove(i);

                    break;
                }

                loopNode = displaySentence.getDisplayNode(parentNodeNr);
            }
        }

        singleClickedNodes.add(new Integer(newNodeNr));
        updateActions();
    }

    /**
     * deletes a single edge
     *
     * @param sentence
     * @param clickedEdgeChild
     */
    private void deleteEdge(Sentence sentence, Node clickedEdgeChild) {
        NT_Node parent = (NT_Node) sentence.getNode(clickedEdgeChild.getParent());
        int index = sentence.getTerminals().indexOf(clickedEdgeChild);

        if (index == -1) {
            index = sentence.getNonterminals().indexOf(clickedEdgeChild) +
                ims.tiger.system.Constants.CUT;
        }

        if (index != -1) {
            parent.getChilds().remove(new Integer(index));
            clickedEdgeChild.setParent(-1);
            forest.setModified(true);
        }

        singleClickedEdges.remove(clickedEdgeChild);
    }

    /**
     * deletes marked edges
     */
    private void deleteEdges() {
        Sentence sentence = displaySentence.getSentence();

        if (clickedSecEdge > -1) {
            deleteSecEdge(sentence, clickedSecEdge);
        } else {
            for (int i = 0; i < singleClickedEdges.size(); i++) {
                deleteEdge(sentence,
                    ((DisplayNode) singleClickedEdges.get(i)).getNode());

                // node.setHighlightedEdge(false);
            }
        }

        reset();

        sentence.orderSentenceByPrecedence();
        displaySentence.init();
        graphPanel.update();
        graphPanel.repaint();
        forest.setModified(true);
    }

    /**
     * deletes a single node
     *
     * @param sentence
     * @param nodeNr
     */
    private void deleteNode(Sentence sentence, int nodeNr) {
        Node node = sentence.getNode(nodeNr);

        if (node instanceof NT_Node) {
            //reset feature EDGE in child nodes
            for (int j = 0; j < ((NT_Node) node).getChildsSize(); j++) {
                Node childNode = sentence.getNode(((Integer) ((NT_Node) node).getChildAt(
                            j)).intValue());
                childNode.setParent(-1);
                childNode.getFeatures().remove(Constants.EDGE);
            }

            //delete node
            sentence.getNonterminals().remove(node);

            //update references of terminals to nonterminal nodes 'higher' than deleted one
            for (int j = 0; j < sentence.getTerminalsSize(); j++) {
                T_Node loopNode = (T_Node) sentence.getTerminalAt(j);
                int parent = loopNode.getParent();

                if (parent > nodeNr) {
                    loopNode.setParent(parent - 1);
                }
            }

            //same with non-terminals
            for (int j = 0; j < sentence.getNonterminalsSize(); j++) {
                NT_Node loopNode = (NT_Node) sentence.getNonterminalAt(j);
                int parent = loopNode.getParent();

                if (parent > nodeNr) {
                    loopNode.setParent(parent - 1);
                }

                ArrayList children = loopNode.getChilds();

                for (int i = 0; i < children.size(); i++) {
                    int childNr = ((Integer) children.get(i)).intValue();

                    if (childNr > nodeNr) {
                        children.set(i, new Integer(childNr - 1));
                    }
                }
            }
        }

        //delete secondary edges which contain deleted node
        for (int i = 0; i < sentence.getCoreferenceSize(); i++) {
            if ((nodeNr == sentence.getCoreferenceNode1(i)) ||
                    (nodeNr == sentence.getCoreferenceNode2(i))) {
                deleteSecEdge(sentence, i);

                //following edge is now at the same index!
                i--;
            }
        }

        //update secondary edges with 'higher' nodes than the deleted
        for (int i = 0; i < sentence.getCoreferenceSize(); i++) {
            if (nodeNr < sentence.getCoreferenceNode1(i)) {
                sentence.getCoreference().set(i * 3,
                    new Integer(sentence.getCoreferenceNode1(i) - 1));
            }

            if (nodeNr < sentence.getCoreferenceNode2(i)) {
                sentence.getCoreference().set((i * 3) + 2,
                    new Integer(sentence.getCoreferenceNode2(i) - 1));
            }
        }

        //update editing ui
        singleClickedNodes.remove(new Integer(nodeNr));

        //update rest of clicked nodes
        for (int i = 0; i < singleClickedNodes.size(); i++) {
            Integer nodeNrInteger = (Integer) singleClickedNodes.get(i);

            if (nodeNr < nodeNrInteger.intValue()) {
                singleClickedNodes.remove(i);
                singleClickedNodes.add(new Integer(nodeNrInteger.intValue() -
                        1));
            }
        }

        forest.setModified(true);
    }

    /**
     * deletes marked nodes
     */
    private void deleteNodes() {
        while (singleClickedNodes.size() > 0) {
            int currentClickedNodeNr = ((Integer) singleClickedNodes.get(0)).intValue();
            deleteNode(displaySentence.getSentence(), currentClickedNodeNr);
        }

        reset();

        displaySentence.getSentence().orderSentenceByPrecedence();

        displaySentence.init();
        graphPanel.update();
        graphPanel.repaint();
        forest.setModified(true);
    }

    /**
     * deletes secondary edge (stored in three subsequent indices of arraylist)
     *
     * @param sentence
     * @param secEdgeNr
     */
    private void deleteSecEdge(Sentence sentence, int secEdgeNr) {
        sentence.getCoreference().remove(secEdgeNr * 3);
        sentence.getCoreference().remove(secEdgeNr * 3);
        sentence.getCoreference().remove(secEdgeNr * 3);
        forest.setModified(true);
    }

    /**
     * puts feature list into a popup menu
     *
     * @param menu
     * @param featureName
     * @param actionListener DOCUMENT ME!
     */
    private void fillMenu(JPopupMenu menu, String featureName,
        ActionListener actionListener) {
        menu.removeAll();

        List features = graphPanel.getHeader().getFeature(featureName).getItems();

        for (int i = 0; i < features.size(); i++) {
            JMenuItem featureItem = new JMenuItem((String) features.get(i));
            featureItem.addActionListener(actionListener);
            menu.add(featureItem);
        }
    }

    private String proposeNodeId(Sentence sentence) {
        int highestNodeNumber = sentence.getNonterminalsSize();
        String proposal;

        do {
            highestNodeNumber++;
            proposal = sentence.getSentenceID() + ".nt." + highestNodeNumber;
        } while (sentence.getNonterminalPositionOf(proposal) > -1 || sentence.getTerminalPositionOf(proposal) > -1);

        return proposal;
    }

    /**
     * enabled, disable actions in correspondence to marked items
     */
    private void updateActions() {
        Sentence sentence = graphPanel.getCurrentSentence();

        //check edges
        if ((singleClickedEdges.size() == 0) && (clickedSecEdge == -1)) {
            deleteNodesAction.setEnabled(false);
        } else {
            deleteEdgesAction.setEnabled(true);

            for (int i = 0; i < singleClickedEdges.size(); i++) {
                Node node = ((DisplayNode) singleClickedEdges.get(i)).getNode();

                //each nt node has to have at least one child
                NT_Node parentNode = (NT_Node) sentence.getNode(node.getParent());

                if (parentNode.getChildsSize() < 2) {
                    deleteEdgesAction.setEnabled(false);
                }
            }
        }

        //check nodes
        if (singleClickedNodes.size() == 0) {
            addParentAction.setEnabled(false);
            addEdgeAction.setEnabled(false);
            addSecEdgeAction.setEnabled(false);
            deleteNodesAction.setEnabled(false);
        } else {
            deleteNodesAction.setEnabled(true);

            addEdgeAction.setEnabled(sentence.getNode(
                    ((Integer) singleClickedNodes.get(0)).intValue()) instanceof NT_Node &&
                (singleClickedNodes.size() > 1));

            addParentAction.setEnabled(singleClickedNodes.size() > 0);

            int clickedTNodeCount = 0;
            Set clickedNodesParents = new HashSet();

            for (int i = 0; i < singleClickedNodes.size(); i++) {
                Node node = sentence.getNode(((Integer) singleClickedNodes.get(
                            i)).intValue());

                //T nodes cannot be deleted
                if (node instanceof T_Node) {
                    deleteNodesAction.setEnabled(false);
                    clickedTNodeCount++;
                }

                //nodes with parent cannot be deleted; sec edge well possible!
                if (node.getParent() != -1) {

                    clickedNodesParents.add(node.getParent());

                    deleteNodesAction.setEnabled(false);

                    //outgoing node might have parents, incoming nodes not
                    if (i > 0) {
                        addEdgeAction.setEnabled(false);
                    }
                }
            }

            // nodes having different parents cannot be joined
            if (clickedNodesParents.size() > 1) {
                addParentAction.setEnabled(false);
            }

            //allow sec edge creation between two nodes
            if (singleClickedNodes.size() == 2) {
                addSecEdgeAction.setEnabled(true);

                //disable if already a sec edge between the two nodes
                for (int i = 0; i < sentence.getCoreferenceSize(); i++) {
                    if (singleClickedNodes.contains(
                                new Integer(sentence.getCoreferenceNode1(i))) &&
                            singleClickedNodes.contains(
                                new Integer(sentence.getCoreferenceNode2(i)))) {
                        addSecEdgeAction.setEnabled(false);
                    }
                }
            } else {
                addSecEdgeAction.setEnabled(false);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
     * @author $Author: hasloe $
     * @version $Revision: 1.3 $
     */
    class EdgePopupActionListener implements ActionListener {
        private DisplayNode clickedEdgeChild;

        /**
         * DOCUMENT ME!
         *
         * @param e DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent e) {
            clickedEdgeChild.getNode().setFeature(Constants.EDGE,
                e.getActionCommand());
            graphPanel.setSentence(graphPanel.getCurrentDisplaySentence());
            graphPanel.repaint();
            forest.setModified(true);
            reset();
        }
    }

    /**
     * DOCUMENT ME!
     * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
     * @author $Author: hasloe $
     * @version $Revision: 1.3 $
     */
    class NodePopupActionListener implements ActionListener {
        private int nodeNr;

        /**
         * DOCUMENT ME!
         *
         * @param e DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent e) {
            Node node = displaySentence.getSentence().getNode(nodeNr);

            node.setFeature((String) graphPanel.getHeader()
                                               .getAllNTFeatureNames().get(0),
                e.getActionCommand());
            graphPanel.setSentence(graphPanel.getCurrentDisplaySentence());
            graphPanel.repaint();
            forest.setModified(true);
            reset();
        }
    }

    /**
     * DOCUMENT ME!
     * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
     * @author $Author: hasloe $
     * @version $Revision: 1.3 $
     */
    class SecEdgePopupActionListener implements ActionListener {
        private int secEdgeNr;

        /**
         * DOCUMENT ME!
         *
         * @param e DOCUMENT ME!
         */
        public void actionPerformed(ActionEvent e) {
            ArrayList coreferences = displaySentence.getSentence()
                                                    .getCoreference();
            coreferences.set((3 * secEdgeNr) + 1, e.getActionCommand());
            forest.setModified(true);
            reset();
        }
    }
}
