package com.ail.base.data;
/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.factory.UndefinedTypeError;
import com.ail.core.product.ProductServiceCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.util.Choice;

/**
 * Service to retrieve a list of options for a dropdown or typeahead field from
 * a product Choice List. Takes query parameter "name" which is the name of the
 * Choice List and, optionally, "masterValue" which is the value of the selected
 * master list option if the list to be retrieved is a slave of that list.
 */
@ProductServiceCommand(serviceName = "GetChoiceOptionsService", commandName = "GetChoiceOptions")
public class GetChoiceOptionsService extends RestfulServiceInvoker {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        new GetChoiceOptionsService().invoke();
    }

    public RestfulServiceReturn service() throws PostconditionException {
        String name = CoreContext.getRequestWrapper().getServletRequest().getParameter("name");
        String masterValue = CoreContext.getRequestWrapper().getServletRequest().getParameter("masterValue");

        Return ret = new Return(HTTP_OK);

        Choice choice;
        List<Choice> choices = null;
        try {
            choice = (Choice) CoreContext.getCoreProxy().newProductType(CoreContext.getProductName(), name, Choice.class);
            if (StringUtils.isBlank(masterValue)) {
                choices = (List<Choice>) choice.getChoice();
            } else {
                for (Choice m : choice.getChoice()) {
                    if (masterValue.equalsIgnoreCase(m.getName())) {
                        choices = m.getChoice();
                        break;
                    }
                }
                if (choices == null) {
                    return new ClientError(HTTP_BAD_REQUEST, "Invalid value for parameter masterValue: " + masterValue);
                }
            }
        } catch (UndefinedTypeError e) {
            return new ClientError(HTTP_BAD_REQUEST, "Invalid value for parameter name: " + name);
        } catch (Exception e) {
            CoreContext.getCoreProxy().logError("Unable to load choice", e);
            return ret;
        }

        if (choices != null) {
            ret.options = new String[choices.size()];
            for (int i = 0; i < choices.size(); i++) {
                ret.options[i] = ((Choice) choices.get(i)).getName();
            }
        }

        return ret;
    }

    public static class Return extends RestfulServiceReturn {

        String[] options;

        public Return(int status) {
            super(status);
        }
    }
}