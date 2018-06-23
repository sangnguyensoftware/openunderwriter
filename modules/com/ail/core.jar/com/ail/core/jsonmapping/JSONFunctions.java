/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
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
package com.ail.core.jsonmapping;

import static java.lang.Character.isWhitespace;

public class JSONFunctions {

    /**
     * Clean a json string and return the cleaned result. Whitespace and
     * carriage return characters are removed.
     *
     * @param jsonIn
     *            JSON to be cleansed
     * @return Cleansed JSON (or null, or an empty string if they were passed
     *         in)
     */
    public static String compressJSON(CharSequence jsonIn) {
        try {
            return internalCompressJSON(jsonIn);
        }
        catch(Throwable e) {
            throw new JSONCompressionError(jsonIn, e);
        }
    }

    static String internalCompressJSON(CharSequence jsonIn) {
        int wPos = 0;

        if (jsonIn == null) {
            return null;
        }

        StringBuffer json=new StringBuffer(jsonIn);

        for(int rPos=0 ; rPos < json.length() ; rPos++) {
            if (isWhitespace(json.charAt(rPos)) || '\n' == json.charAt(rPos)) {
                continue;
            }

            if (json.charAt(rPos) == '"') {
                do {
                    json.setCharAt(wPos++, json.charAt(rPos++));
                    if (rPos < json.length() && json.charAt(rPos)=='\\') {
                        json.setCharAt(wPos++, json.charAt(rPos++));
                    }
                } while(rPos < json.length() && json.charAt(rPos)!='"');
            }

            if (rPos < json.length() && json.charAt(rPos) == '}') {
                json.setCharAt(wPos++, json.charAt(rPos++));
                if (rPos < json.length() && json.charAt(rPos) == ',' && json.charAt(rPos+1) == ']') {
                    rPos++;
                }
            }

            if (rPos < json.length()) {
                json.setCharAt(wPos++, json.charAt(rPos));
            }
        }

        return json.substring(0, wPos).toString();
    }

    /**
     * Tidy a JSON string by removing unnecessary (non-whitespace) characters. Trailing commas
     * in arrays are removed (e.g. [{"one","1"},{"two","2"},] becomes  [{"one","1"},{"two","2"}]
     * @param jsonIn String to be cleaned
     * @return Cleansed JSON (or null, or an empty string if they were passed in)
     */
    public static String tidyJSON(CharSequence jsonIn) {
        int wPos = 0;

        if (jsonIn == null) {
            return null;
        }

        StringBuffer json=new StringBuffer(jsonIn);

        for(int rPos=0 ; rPos < json.length() ; rPos++) {
            if (json.charAt(rPos) == '}') {
                json.setCharAt(wPos++, json.charAt(rPos++));
                if (rPos < json.length() && json.charAt(rPos) == ',' && json.charAt(rPos+1) == ']') {
                    rPos++;
                }
            }

            if (rPos < json.length() && json.charAt(rPos) == ',' && json.charAt(rPos+1) == '}') {
                rPos++;
                json.setCharAt(wPos++, json.charAt(rPos++));
            }

            if (rPos < json.length() && json.charAt(rPos) == '[' && json.charAt(rPos+1) == ',') {
                json.setCharAt(wPos++, json.charAt(rPos++));
                rPos++;
            }

            if (rPos < json.length()) {
                json.setCharAt(wPos++, json.charAt(rPos));
            }
        }

        return json.substring(0, wPos).toString();
    }
}
