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

import static com.ail.core.Functions.configurationNamespaceToProductName;
import static com.ail.core.Functions.loadUrlContentAsString;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.lang.StringEscapeUtils;

import com.ail.core.CoreProxy;
import com.ail.core.Type;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.render.RenderService.RenderCommand;

/**
 * A PageElement which contains content read from an arbitrary URL. The content is parsed
 * for embedded JXPath expressions and the expressions evaluated against the {@com.ail.openquote.Policy Policy}
 * and the results of the evaluation inserted into the content.</br><br/>
 * For example, if the content returned included:<br/><br/>
 * &nbsp;&nbsp;"The proposer's name is: &lt;b&gt;${proposer/forname}&lt;/b&gt;"<br/><br/>
 * Then the rendered output might read: <br/><br/>
 * &nbsp;&nbsp;"The proposer's name is: <b>Jimbo</b>".
 * <p>Note: This element takes no special steps to ensure that the content read is suitable for use within
 * the PageFlow or in a portal context.</p>
 */
public class ParsedUrlContent extends PageElement {
    private static final long serialVersionUID = -4810599045554021748L;

    /** URL to read content from. */
    private String url;

    /**
     * Default constructor
     */
    public ParsedUrlContent() {
        super();
	}

    /**
     * URL to read raw content from.
     * @return URL to read from
     */
    public String getUrl() {
        return url;
    }

    /**
     * @see #getUrl()
     * @param url URL to read content from.
     */
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Type applyRequestValues(Type model) {
    	return model;
    }

    public String escapeForJson(String html) {
        return StringEscapeUtils.escapeJavaScript(html).replaceAll("\\\\'", "'");
    }

	@Override
    public boolean processValidations(Type model) {
        return false;
    }

	@Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
        String productName=null;
        boolean success=false;
        String content=null;

    	if (conditionIsMet(model)) {
        	Policy policy=PageFlowContext.getPolicy();

            CoreProxy cp=PageFlowContext.getCoreProxy();
            Collection<String> namespaces=cp.getConfigurationNamespaceParent();

            for(String namespace: namespaces) {
                try {
                    if (namespace.endsWith("Registry")) {
                        productName=configurationNamespaceToProductName(namespace);
                        content=expand(loadUrlContentAsString(new URL(expandRelativeUrlToProductUrl(getUrl(), productName))), policy, model);
                        success=true;
                        break;
                    }
                }
                catch(IOException e) {
                    // next! This just indicates that we didn't find it in this namespace
                }
            }

            if (!success) {
                content="ERROR: Content not found for:"+getUrl();
            }

            RenderCommand command = buildRenderCommand("ParsedUrlContent", model);
            command.setContentArg(content);
            model = invokeRenderCommand(command);
    	}

    	return model;
	}
}
