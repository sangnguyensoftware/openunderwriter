/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.ui.client.common.component;

import java.util.ArrayList;
import java.util.List;

public class AilSimpleMultiSelectListBox extends AilSimpleListBox {

    public AilSimpleMultiSelectListBox() {
        super(true);
    }

    public AilSimpleMultiSelectListBox(List<String> model) {
        super(model, true);
    }
    
    public List<String> getSelectedItems() {
        List<String> selectedItems = new ArrayList<String>();
        for (int i = 0; i < getItemCount(); i++) {
            if (isItemSelected(i)) {
                selectedItems.add(getItemText(i));
            }
        }
        return selectedItems;
    }

}
