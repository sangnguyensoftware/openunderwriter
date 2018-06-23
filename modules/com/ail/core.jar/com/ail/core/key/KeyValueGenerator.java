/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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
package com.ail.core.key;

import com.ail.core.CoreContext;

class KeyValueGenerator {
    public Long fetchMinValue(String keyId) {
        String range = CoreContext.getCoreProxy().getParameterValue("KeyGenerators." + keyId + ".Range", "0");
        return minFromRange(rangeAt(range, 0));
    }

    public Long fetchNextValue(String keyId, Long currentValue) {
        String range = CoreContext.getCoreProxy().getParameterValue("KeyGenerators." + keyId + ".Range", "0");

        for (String r : ranges(range)) {
            if (currentValue >= minFromRange(r) && currentValue < maxFromRange(r)) {
                return currentValue+1;
            }
            else if (currentValue < minFromRange(r)) {
                return new Long(minFromRange(r));
            }
        }

        throw new KeyGenerationError("Unique key generation failed for keyId: '"+keyId+"' (range definition:'"+range+"') currentValue: "+currentValue);
    }

    private String[] ranges(String range) {
        return range.split(",");
    }

    private String rangeAt(String range, int index) {
        return range.split(",")[index];
    }

    private Long minFromRange(String range) {
        return new Long(range.split("-")[0]);
    }

    private Long maxFromRange(String range) {
        String[] ra = range.split("-");

        if (ra.length == 1) {
            return new Long(ra[0]);
        } else if (ra[1].charAt(0) == '*') {
            return Long.MAX_VALUE;
        } else {
            return new Long(ra[1]);
        }
    }
}
