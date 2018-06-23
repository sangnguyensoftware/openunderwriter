/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.pageflow.render;

import java.io.Writer;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.Core;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.context.RequestWrapper;
import com.ail.core.context.ResponseWrapper;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.PageElement;

/**
 * Generic service argument defining the interface to all render services. The actual requirements
 * of render services for each type of widget vary in the arguments that they need. Having this
 * one Arg for all render services has the advantage that we have to maintain far fewer classes,
 * but the disadvantage that it isn't clear which arguments have to be populated for a given
 * widget and which do not. However, all render services will require a that the {@link #getModelArgRet() Model}
 * argument is populated; and all will populate the {@link #getRenderedOutputRet RenderedOutputRet} with
 * the rendered results.
 */
@ServiceInterface
public interface RenderService {
    @ServiceArgument
    public interface RenderArgument extends Argument {
        /**
         * String returning the rendered output.
         * Typically, render services generate some output whether in the form of
         * HTML or XForms. The services should write this output into this
         * String property.
         * @return String to which output should be written.
         */
        String getRenderedOutputRet();

        /**
         * @see #getRenderedOutputRet()
         * @param renderedOutputRet
         */
        void setRenderedOutputRet(String renderedOutputRet);

        /**
         * The portal request associated with this invocation. Render services are invoked
         * as a part of a portal request/response process. This service may refer to this
         * property to fetch request specific information.
         * @return The request that this service is being invoked for.
         */
        RequestWrapper getRequestArg();

        /**
         * @see #getRequestArg()
         * @param requestArg
         */
        void setRequestArg(RequestWrapper requestArg);

        /**
         * The portal response associated with this invocation. Render services are invoked
         * as a part of a portal request/response process. This service may refer to this
         * property to fetch response specific information.
         * @return The response that this service is being invoked for.
         */
        ResponseWrapper getResponseArgRet();

        /**
         * @see #getResponseArgRet()
         * @param responseArgRet
         */
        void setResponseArgRet(ResponseWrapper responseRet);

        /**
         * The model represents the data which the request to render is being made. It is the
         * M in MVC.
         * @return The model being rendered
         */
        Type getModelArgRet();

        /**
         * @see #getModelArgRet()
         * @param modelArg
         */
        void setModelArgRet(Type modelArgRet);

        /**
         * Get the instance of the policy which is being processed.
         * @return Policy instance
         */
        Policy getPolicyArg();

        /**
         * @see #getPolicyArg()
         * @param policyArg
         */
        void setPolicyArg(Policy policyArg);

        /**
         * The page element or widget being rendered. This represents the V in MVC and defines
         * how the model needs to be rendered.
         * @return page element being rendered
         */
        PageElement getPageElementArg();

        /**
         * @see #getPageElementArg()
         * @param pageElementArg
         */
        void setPageElementArg(PageElement pageElementArg);

        /**
         * When the element being rendered is being rendered within a table, the
         * rowContext uniquely identifies the row within that table.
         * @return row context
         */
        String getRowContextArg();

        /**
         * @see #getRowContextArg()
         */
        void setRowContextArg(String rowContext);

        /**
         * The onChangeArg defines an optional piece of javascript that should be
         * attached to the generated HTML form element's onchange event.
         */
        String getOnChangeArg();

        /**
         * @see #setOnChangeArg(String)
         * @param onChange
         */
        void setOnChangeArg(String onChange);

        /**
         * The onLoadArg defines an optional piece of javascript that should be
         * attached to the generated HTML form element's onload event.
         */
        String getOnLoadArg();

        /**
         * @see #setOnLoadArg(String)
         * @param onChange
         */
        void setOnLoadArg(String onLoad);

        /**
         * The name of the style class (css) to be associated with the element when it is rendered.
         * @return
         */
        String getStyleClassArg();

        /**
         * @see #getStyleClassArg()
         * @param styleClass
         */
        void setStyleClassArg(String styleClass);

        String getRefArg();

        void setRefArg(String ref);

        /**
         * Instance of Core which the render templates may use to access other core services.
         * @return
         */
        Core getCoreArg();

        /**
         * @see #getCoreArg()
         * @param core
         */
        void setCoreArg(Core core);

        /**
         * Unique ID within the page for use in the identification of page elements
         * @return
         */
        String getRenderIdArg();

        /**
         * @see #getRenderIdArg()
         * @param renderIdArg
         */
        void setRenderIdArg(String renderIdArg);

        Writer getWriterArg();

        void setWriterArg(Writer writer);

        /**
         * Id for use on the "detail" elements of widget where appropriate - for example the
         * questionWithDetails widget.
         * @return
         */
        String getDetailIdArg();

        /**
         * @see #getDetailIdArg()
         * @param detailIdArg
         */
        void setDetailIdArg(String detailIdArg);

        /**
         * Hint to be passed to the renderer to help guide it in the way that the attribute is rendered
         * @return render hint
         */
        String getRenderHintArg();

        /**
         * @see #getRenderHint()
         * @param renderHint
         */
        void setRenderHintArg(String renderHint);

        /**
         * Optional argument used by page elements (e.g. ParseUrlContent) which render content directly on the UI.
         * @return Content to be rendered.
         */
        String getContentArg();

        /**
         * @see #setContentArg(String)
         */
        void setContentArg(String content);

        Type getPageFlowSessionTypeArg();

        void setPageFlowSessionTypeArg(Type pageFlowSessionTypeArg);
    }

    @ServiceCommand
    public interface RenderCommand extends Command, RenderArgument {
    }
}


