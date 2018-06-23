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
package com.ail.core.language;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;

/**
 * Implements a wrapper to the Core's language translation service to simplify common
 * types of message lookup.
 */
public class I18N {

    /**
     * Return the translation matching a specific message (aka key). The translation is
     * based on the message passed in and the thread's current locale. The argument passed
     * in is not assumed to be an i18n key itself (i.e. it does not have to have the
     * conventional "i18n_" prefix). If the prefix is not present, it will be added and
     * the resulting key looked up.
     * @param message key identifying the string to be returned.
     * @return ThreadLocale specific string, or the value of <i>message</i> if a match cannot be found.
     */
	public static String i18n(String message) {
		if (message==null) {
			return null;
		}

		if (message.indexOf("i18n_")==0) {
			return(i18n(message, message));
		}
		else {
			return(i18n("i18n_"+message, message));
		}
	}

	/**
     * Return the translation matching a specific message (aka key). The translation is
     * based on the message passed in and the thread's current locale.
     * @param message key identifying the string to be returned.
     * @return ThreadLocale specific string, or the value of <i>alternative</i> if a match cannot be found.
     */
    public static String i18n(String message, String alternative) {
        if (message != null) {
            try {
                String product = null;
                CoreProxy coreProxy = null;

                if (CoreContext.getCoreProxy() != null) {
                    coreProxy = CoreContext.getCoreProxy();
                    product = CoreContext.getProductName();
                }
                else {
                    coreProxy = new CoreProxy();
                }

                if (product == null || product.length() == 0) {
                    product = "AIL.Base";
                }

                Translations trans = coreProxy.newProductType(product, "Translations", Translations.class);

                return trans.translate(message, alternative);
            } catch (Throwable e) {
                // ignore this - let the default return handle it
            }
        }
        return alternative;
    }
}
