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
import java.net.URL;

import com.ail.core.Type;
import com.ail.core.product.ProductUrlToExternalUrlService.ProductUrlToExternalUrlCommand;
/**
 * The PageScript element inserts JavaSript in the generated page. The JavaSript
 * to be inserted can be referenced by a URL (including product URLs), or picked
 * up directly from the PageScript properties. It may also be inserted into the
 * page header, or be written directly into the page relative to other elements
 * in the PageFlow's definition.
 * <p/>
 * <p>
 * For example, the following will include a reference to somesite.com's
 * somescript.js into the header of a page:<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * <code>&lt;pageScript url='http://somesite.com/somescript.js' pageHeader='true'/&gt;</code>
 * </p>
 * <p>
 * The following will include a reference to a script defined in the HTML sub
 * folder of the product. Again, the script reference will be place in the
 * header of the generated page:<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * <code>&lt;pageScript url='~/HTML/MyScript.js' pageHeader='true'/&gt;</code><br/>
 * Note that this would also search the product's parent and grandparent (etc)
 * if the script is not found locally.
 * </p>
 * <p>
 * It is also possible to insert code directly into the page as follows:<br/>
 * &nbsp;&nbsp;&nbsp;&nbsp;
 * <code>&lt;pageScript script='alert("Hello World!");'/&gt;</code><br/>
 * Here the <code>pageHeader</code> property has been left to it's default value
 * of 'false' so the script will be placed directly into the generated page's
 * body in a position relative to the other elements in the PageFlow's
 * definition of the page.
 * 
 * @since 1.2
 */
public class PageScript extends PageElement {
    private static final long serialVersionUID = -1320299722728499324L;

    /** @see #isPageHeader() */
    private boolean pageHeader = false;

    /** @see #getUrl() */
    private String url = null;

    /** @see #getScript() */
    private String script = null;

    /**
     * Internal representation of the URL. If the {@link PageScript#url} is a
     * product relative URL (i.e. starts with a "~"), this property will contain
     * the expanded form. Otherwise it will contain whatever the URL property
     * contains.
     */
    private String canonicalUrl = null;

    private boolean initialized = false;

    /**
     * Default constructor
     */
    public PageScript() {
        super();
    }

    /**
     * Set to true if the script should be included in the header of the page.
     * Otherwise the script is included in the location suggested by the
     * PageScript's relative position in the page. By default the script is
     * included in the relative position, and not in the header.
     * 
     * @return
     */
    public boolean isPageHeader() {
        return pageHeader;
    }

    /**
     * @see #isPageHeader()
     * @param pageHeader
     */
    public void setPageHeader(boolean pageHeader) {
        this.pageHeader = pageHeader;
    }

    /**
     * URL to JavasSript resource. This may be an absolute or product relative
     * URL. See also the {@link #getScript() script} property. If both script
     * and URL properties are non-null script will be used in preference.
     * 
     * @return JavaScript URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * @see #getUrl()
     * @param url
     *            JavasSript URL
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * In-line JavaSript - an alternative to using the {@link #getUrl() URL}
     * property. If a script is defined, it's contents is assumed to be raw
     * JavaSript and is simply written to the page. If both script and URL
     * properties are non-null script will be used in preference.
     * 
     * @return Script as a string
     */
    public String getScript() {
        return script;
    }

    /**
     * @see #getScript()
     * @param script
     *            Text of the JavaScript
     */
    public void setScript(String script) {
        this.script = script;
    }

    public String getCanonicalUrl() {
        return canonicalUrl;
    }

    public void setCanonicalUrl(String canonicalUrl) {
        this.canonicalUrl = canonicalUrl;
    }

    @Override
    public Type applyRequestValues(Type model) {
        // nothing to do here - PageScript doesn't have a value
        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        // nothing to do here - PageScript doesn't have validations
        return false;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        if (!pageHeader) {
            model = renderScript(model);
        }

        return model;
    }

    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        if (pageHeader) {
            renderScript(model);
        }
        return model;
    }

    private Type renderScript(Type model) throws IOException {
        initialise(model);

        return executeTemplateCommand("PageScript", model);
    }

    /**
     * Perform one time initializations.
     * 
     * @param request
     * @param model
     */
    synchronized void initialise(Type model) {
        if (!initialized) {
            String absoluteUrl = getUrl();

            if (url != null && url.charAt(0) == '~') {
                absoluteUrl = expandRelativeUrlToProductUrl(getUrl(), PageFlowContext.getProductName());
            }

            try {
                ProductUrlToExternalUrlCommand puteu = PageFlowContext.getCoreProxy().newCommand(ProductUrlToExternalUrlCommand.class);
                puteu.setProductUrlArg(new URL(absoluteUrl));
                puteu.invoke();
                canonicalUrl = new URL(puteu.getExternalUrlRet()).getPath();
            } catch (Exception e) {
                canonicalUrl = absoluteUrl;
            }
        }
    }
}
