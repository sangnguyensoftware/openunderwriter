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
package com.ail.core.jsonmapping.jackson;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.NONE;
import static org.codehaus.jackson.annotate.JsonMethod.ALL;
import static org.codehaus.jackson.annotate.JsonMethod.FIELD;
import static org.codehaus.jackson.map.SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS;
import static org.codehaus.jackson.map.SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS;
import static org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion.NON_NULL;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectReader;
import org.codehaus.jackson.map.ObjectWriter;
import org.codehaus.jackson.map.SerializationConfig.Feature;

class Mapper {
    private static final String DEFAULT_MAPPER = null;
    private static Map<String, ObjectMapper> mappers = new HashMap<>();;

    static {
        mappers.put(DEFAULT_MAPPER, new ObjectMapper());
        // Don't use Class getters to derive JSON, use fields instead.
        mappers.get(DEFAULT_MAPPER).setVisibility(ALL, NONE);
        mappers.get(DEFAULT_MAPPER).setVisibility(FIELD, ANY);
        // Exclude empty values
        mappers.get(DEFAULT_MAPPER).setSerializationInclusion(NON_NULL);
        // Exclude empty lists
        mappers.get(DEFAULT_MAPPER).configure(WRITE_EMPTY_JSON_ARRAYS, false);
        mappers.get(DEFAULT_MAPPER).configure(WRITE_DATES_AS_TIMESTAMPS, false);
    };

    private void addMapperForOptions(String options) {
        mappers.put(options, new ObjectMapper());
        // Don't use Class getters to derive JSON, use fields instead.
        mappers.get(options).setVisibility(ALL, NONE);
        mappers.get(options).setVisibility(FIELD, ANY);
        // Exclude empty values
        mappers.get(options).setSerializationInclusion(NON_NULL);

        for (String opt : options.split(",")) {
            String[] keyVal = opt.split("=");
            mappers.get(options).configure(Feature.valueOf(keyVal[0]), new Boolean(keyVal[1]));
        }
    }

    private ObjectMapper mapperFor(String options) {
        if (!mappers.containsKey(options)) {
            addMapperForOptions(options);
        }

        return mappers.get(options);
    }

    ObjectWriter buildWriter(String options) {
        return mapperFor(options).writer();
    }

    ObjectReader buildReader(Class<? extends Object> clazz, String options) {
        return mapperFor(options).reader(clazz);
    }
}
