package com.ail.base;
/* Copyright Applied Industrial Logic Limited. 2016. All rights Reserved */
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
import java.text.MessageFormat;
import java.util.Date;

import com.ail.core.product.ProductServiceCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.party.Party;

/**
 * Add party id to party
 */
@ProductServiceCommand(serviceName = "AddPartyIdToProposerService", commandName = "AddPartyIdToProposer")
public class AddPartyIdToProposerService {

    public static void invoke(ExecutePageActionArgument args) {

        Policy policy = (Policy) args.getModelArgRet();
        Party proposer = policy.getClient();
        if (proposer != null && (proposer.getPartyId() == null || "".equals(proposer.getPartyId().trim()))) {
            long days = new Date().getTime() / 86400000;
            proposer.setPartyId("C" + days + MessageFormat.format("{0,number,000000}", +proposer.getSystemId()));
        }
    }
}
