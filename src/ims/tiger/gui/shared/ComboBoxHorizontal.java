/*
 * File:     ComboBoxHorizontal.java
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
package ims.tiger.gui.shared;

import java.awt.*;

import javax.swing.*;
import javax.swing.plaf.basic.*;


/** Speziell angepasste ComboBox. */
public class ComboBoxHorizontal extends JComboBox {
    /**
     * Creates a new ComboBoxHorizontal instance
     *
     * @param object DOCUMENT ME!
     */
    public ComboBoxHorizontal(Object[] object) {
        super(object);
        setUI(new myComboUI());
        getComponent(0).setBackground(Color.lightGray);
        getComponent(1).setBackground(Color.white);

        setBorder(BorderFactory.createLineBorder(Color.gray));

        setEditable(true);
    }

    /**
     * DOCUMENT ME!
     * $Id: jalopy_gnu_src_dist.xml,v 1.3 2007/02/06 13:30:33 hasloe Exp $
     * @author $Author: hasloe $
     * @version $Revision: 1.3 $
     */
    public class myComboUI extends BasicComboBoxUI {
        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        protected ComboPopup createPopup() {
            BasicComboPopup popup = new BasicComboPopup(comboBox) {
                    protected JScrollPane createScroller() {
                        return new JScrollPane(list,
                            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                    }
                };

            return popup;
        }
    }
}
