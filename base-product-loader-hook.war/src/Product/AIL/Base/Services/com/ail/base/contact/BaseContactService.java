package com.ail.base.contact;
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
import com.ail.core.PreconditionException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.party.Party;

/**
 * The base service intended to handle contact calls - i.e. email, post etc.
 */
public abstract class BaseContactService extends RestfulServiceInvoker {

    protected Party getParty(Type model, String userToContact) throws PreconditionException {
        try {
            Object data = model.xpathGet(userToContact);
            if (Party.class.isInstance(data)) {
                return (Party) data;
            } else {
                throw new PreconditionException(
                        "Could not find Party type on Type: " + model.getClass().getSimpleName() + ":" + model.getExternalSystemId() + " for xpath: " + userToContact);
            }
        } catch (TypeXPathException e) {
            throw new PreconditionException(
                    "Error invoking xpathGet on Type: " + model.getClass().getSimpleName() + ":" + model.getExternalSystemId() + " for xpath: " + userToContact, e);
        }
    }

    public static class MaximalArgument {

        public String caseType;
        public String caseId;
        public String userToContact;
        public String subject;
        public String message;
        public String templateName;
        public String documentsToAttach;

        @Override
        public String toString() {
            return "MaximalArgument [caseType=" + caseType + ", caseId=" + caseId + ", userToContact=" + userToContact + ", subject=" + subject + ", message=" + message
                    + ", templateName=" + templateName + ", documentsToAttach=" + documentsToAttach + "]";
        }
    }

    public static class MinimalArgument {

        public String caseType;
        public String caseId;
        public String userToContact;
        public String message;

        @Override
        public String toString() {
            return "MinimalArgument [caseType=" + caseType + ", caseId=" + caseId + ", userToContact=" + userToContact + ", message=" + message + "]";
        }
    }
}
