/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

package com.ail.core.language;

import static java.util.Locale.UK;
import static java.util.Locale.US;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.ail.core.ThreadLocale;

public class TestTranslations {

    @Test
    public void testLocaleConstruction() throws Exception {
        Translations translations=new Translations(UK.toString());

        Translation translation=new Translation(UK.toString());
        translation.getKey().put("key1", "String number one");
        translation.getKey().put("key2", "String number two");
        translation.getKey().put("key3", "String number three");
        translation.getKey().put("key4", "String number four");
        translations.getTranslation().add(translation);
        
        translation=new Translation(US.toString(), UK.toString());
        translation.getKey().put("key1", "Second String number one");
        translation.getKey().put("key2", "Second String number two");
        translation.getKey().put("key3", "Second String number three");
        translations.getTranslation().add(translation);

        java.util.Locale saved=ThreadLocale.getThreadLocale();
        ThreadLocale.setThreadLocale(US);

        assertEquals("Second String number one", translations.translate("key1", "two"));
        assertEquals("String number four", translations.translate("key4", "two"));
        assertEquals("String number four", translations.translate("key4", "three"));
        assertEquals("three", translations.translate("key5", "three"));
        assertEquals("two", translations.translate("key5", "two"));
        
        ThreadLocale.setThreadLocale(saved);
    }
}
