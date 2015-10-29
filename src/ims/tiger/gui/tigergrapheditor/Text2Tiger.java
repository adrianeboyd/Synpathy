/*
 * File:     Text2Tiger.java
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

package ims.tiger.gui.tigergrapheditor;

import ims.tiger.corpus.Sentence;
import ims.tiger.corpus.T_Node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;


/**
 * $Id: Text2Tiger.java,v 1.5 2006/08/23 09:49:58 klasal Exp $ Converts sentences from a plain text file into a list of
 * ims.tiger.corpus.Sentence's
 *
 * @author klasal
 */
public final class Text2Tiger {
    /**
     * disallow instantiating
     */
    private Text2Tiger() {
        super();
    }

    /**
     * Imports sentences from a plain text file
     *
     * @param file name of file
     * @param startNr sentence number
     * @param featureNames features to be imported
     * @param startLine line in file to start with
     *
     * @return list of sentences
     *
     * @throws IOException read error
     */
    public static List importSentences(File file, int startNr,
        String[] featureNames, int startLine) throws IOException {
        List sentences = new ArrayList();
        String filename = file.getName();

        if (filename.indexOf('.') > -1) {
            filename = filename.substring(0, filename.lastIndexOf('.'));
        }

        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line;
        int wordCount = 0;
        int blockLineCount = 0;
        Sentence sentence = null;
        int lineCount = 0;

        while ((lineCount < startLine) && ((line = reader.readLine()) != null)) {
            lineCount++;
        }

        while ((line = reader.readLine()) != null) {
            if (line.trim().equals("")) {
                blockLineCount = 0;
            } else {
                blockLineCount++;

                if (blockLineCount == 1) {
                    sentence = new Sentence();
                    sentence.setSentenceID(filename + "." +
                        (startNr + sentences.size() + 1));
                    sentences.add(sentence);
                }

                String[] tokens = line.split("\\s+");
                wordCount += tokens.length;

                if ((blockLineCount > 1) &&
                        (tokens.length != sentence.getTerminalsSize())) {
                    throw new IllegalArgumentException(
                        "Number of tokens isn't the same for all features in sentence nr " +
                        sentences.size() + " Import failed.");
                }

                for (int i = 0; i < tokens.length; i++) {
                    if (blockLineCount == 1) {
                        T_Node node = new T_Node();
                        node.setID(sentence.getSentenceID() + ".t." + (i+1));
                        node.addFeature(featureNames[0], tokens[i]);

                        for (int j = 1; j < featureNames.length; j++) {
                            node.addFeature(featureNames[j], "");
                        }

                        sentence.addTerminal(node);
                    } else {
                        if (blockLineCount < featureNames.length) {
                            T_Node node = (T_Node) sentence.getTerminalAt(i);
                            node.addFeature(featureNames[blockLineCount - 1],
                                tokens[i]);
                        }
                    }
                }
            }
        }

        return sentences;
    }
}
