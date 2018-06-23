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
package com.ail.pageflow;

import java.io.IOException;

import com.ail.core.BaseException;
import com.ail.core.Type;

public class If extends PageContainer {
    private static final long serialVersionUID = 6794512728423045427L;

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        if (conditionIsMet(model)) {
            for (PageElement pe : getPageElement()) {
                model = pe.renderResponse(model);
            }

            model = super.renderResponse(model);
        }

        return model;
    }

    @Override
    public Type applyRequestValues(Type model) {
        return super.applyRequestValues(model);
    }

    @Override
    public boolean processValidations(Type model) {
        return super.processValidations(model);
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        return super.processActions(model);
    }
}
