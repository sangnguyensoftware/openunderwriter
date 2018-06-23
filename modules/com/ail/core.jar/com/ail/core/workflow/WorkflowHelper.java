/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.core.workflow;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;

import com.ail.core.FieldChange;
import com.ail.core.Type;
import com.ail.core.persistence.OutsideTransactionContext;
/**
 * Utility methods for workflow services.
 *
 */
public class WorkflowHelper {

    /**
     * Checks to see if in the current request a field has changed that exists in a list of fields that we are interested in changes on.
     * If there are changes to a field we are interested in then we want to return true, or if we actually do not care if there are any
     * changes then return true, otherwise go through the 2 lists and look for changes.
     * @param model
     * @param onChange  list of field names that we want to check for changes
     * @return
     */
    public static boolean isExecuteOnChangeField(Type model, List<String> onChangeField) {
        if (onChangeField == null || onChangeField.isEmpty()) {
            // no changes to check for so return true
            return true;
        }

        for (String onChangeFieldBinding : onChangeField) {
            String xpath = onChangeFieldBinding;
            FieldChange changedField = OutsideTransactionContext.getOnChangeFields().get(xpath);
            if (changedField == null) {
                /** If not match for the name it might be in there as an attribiute with /value on the end, so try that */
                xpath += "/value";
                changedField = OutsideTransactionContext.getOnChangeFields().get(xpath);
            }
            if (changedField != null) {
                // If it is null then there's something quite wrong
                Object newValue = model.xpathGet(xpath);
                if (!ObjectUtils.equals(changedField.getOldValue(), newValue)) {
                    return true;
                }
            }
        }

        return false;
    }
}
