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
package com.ail.eft.bacs;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.StringUtils;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.eft.bacs.output.advices.BACSDocument;
/**
 * Service to parse BACS output advices from XML
 */
@ServiceImplementation
public class ParseAdvicesService extends Service<ParseAdvicesService.ParseAdvicesArgument> {
    private static final long serialVersionUID = 5529681194148500651L;

    /**
     * Interface defining the arguments and returns associated with the generate PaymentInstructions service.
     */
    @ServiceArgument
    public interface ParseAdvicesArgument extends Argument {

        String getAdvicesXMLArg();

        void setAdvicesXMLArg(String advicesXMLArg);

        BACSDocument getAdvicesRet();

        void setAdvicesRet(BACSDocument advicesRet);
    }

    @ServiceCommand(defaultServiceClass=ParseAdvicesService.class)
    public interface ParseAdvicesCommand extends Command, ParseAdvicesArgument {}

    @Override
    public void invoke() throws BaseException {
        try {
            if (StringUtils.isBlank(args.getAdvicesXMLArg())) {
                throw new PreconditionException("args.getAdvicesXMLArg() blank");
            }

            args.setAdvicesRet(getAdvices());
        } catch (Exception e) {
            throw new PostconditionException("Failed to parse advices", e);
        }
    }

    private BACSDocument getAdvices() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(BACSDocument.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(args.getAdvicesXMLArg());

        return (BACSDocument) jaxbUnmarshaller.unmarshal(reader);
    }
}
