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
package com.ail.core.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class Transformers<I, O> {

    private static final int NO_MAX_SIZE = -1;

    public List<O> transform(Collection<I> input, Transformer<I, O> transformer) throws TransformerException {
        return doTransform(input, transformer, NO_MAX_SIZE);
    }

    public List<O> transform(Collection<I> input, Transformer<I, O> transformer, int maxSize) throws TransformerException {
        return doTransform(input, transformer, maxSize);
    }

    private List<O> doTransform(Collection<I> input, Transformer<I, O> transformer, int maxSize) throws TransformerException {
        List<O> results =  new ArrayList<>();

        if (input != null) {
            for (I item : input) {
                results.add(transformer.apply(item));
                if (maxSize != NO_MAX_SIZE && results.size() == maxSize) {
                    break;
                }
            }
        }
        return results;
    }

}
