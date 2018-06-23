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
package com.ail.insurance.quotation;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.notification.SendPubNubNotificationService.SendPubNubNotificationCommand;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.NotifyClientByPubNubService.NotifyClientByPubNubArgument;
import com.ail.party.Party;

@ServiceImplementation
public class NotifyClientByPubNubService extends Service<NotifyClientByPubNubArgument> {
    private static final long serialVersionUID = -4915889686192216902L;
    static final String PARTY_PUBNUB_CHANNEL_XPATH = "/attribute[id='PubNubChannel']/value";

    private String configurationNamespace = null;

    @ServiceArgument
    public interface NotifyClientByPubNubArgument extends Argument {
        Long getPolicyIdArg();

        void setPolicyIdArg(Long policyIdArg);

        Policy getPolicyArg();

        void setPolicyArg(Policy policyArg);

        String getMessageArg();

        void setMessageArg(String messageArg);
    }

    @ServiceCommand(defaultServiceClass = NotifyClientByPubNubService.class)
    public interface NotifyClientByPubNubCommand extends Command, NotifyClientByPubNubArgument {
    }

    @Override
    public String getConfigurationNamespace() {
        if (configurationNamespace == null) {
            return super.getConfigurationNamespace();
        } else {
            return configurationNamespace;
        }
    }

    private void setConfigurationNamespace(String configurationNamespace) {
        this.configurationNamespace = configurationNamespace;
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
        setConfigurationNamespace(null);

        Policy policy = determinePolicy();

        Party client = determineClientParty(policy);

        sendNotification(policy, client);
    }

    Policy determinePolicy() throws PreconditionException {
        if (args.getPolicyIdArg() == null && args.getPolicyArg() == null) {
            throw new PreconditionException("args.getPolicyIdArg() == null && args.getPolicyArg() == null");
        }

        if (args.getPolicyArg() != null) {
           return args.getPolicyArg();
        }

        Policy policy = (Policy) getCore().queryUnique("get.policy.by.systemId", args.getPolicyIdArg());

        if (policy == null) {
            throw new PreconditionException("core.queryUnique(get.policy.by.systemId, " + args.getPolicyIdArg() + ")==null");
        }

        return policy;
    }

    Party determineClientParty(Policy policy) throws PreconditionException {
        if (policy.getClient() == null) {
        	throw new PreconditionException("policy.getClient() == null");
        }
        return policy.getClient();
    }

    void sendNotification(Policy policy, Party client) throws BaseException {
        String channel = (String) client.xpathGet(PARTY_PUBNUB_CHANNEL_XPATH);

        if (channel == null) {
            throw new PreconditionException("client.xpathGet('" + PARTY_PUBNUB_CHANNEL_XPATH + "') == null");
        }

        String message = args.getMessageArg();

        if (message == null || message.isEmpty()) {
            List<Attribute> messages = client.getAttribute();
            for (Iterator<Attribute> iterator = messages.iterator(); iterator.hasNext();) {
                Attribute attribute = (Attribute) iterator.next();
                if ("PubNubMessage".equals(attribute.getId())) {
                    message = attribute.getValue();
                    break;
                }
            }
            if (message == null || message.isEmpty()) {
                throw new PreconditionException("message == null || message.isEmpty()");
            }
        }

        SendPubNubNotificationCommand spnnc = getCore().newCommand(SendPubNubNotificationCommand.class);
        spnnc.setMessageArg(stringMessage2PushNotification(message));
        spnnc.setChannelArg(channel);
        spnnc.invoke();
    }

    JSONObject stringMessage2PushNotification(String message) throws PreconditionException {
        try {
            String escapedMessage = JSONObject.quote(message);

            return new JSONObject("{"+
                                      "'pn_gcm': {"+
                                          "'data': {"+
                                              "'message': " + escapedMessage +
                                          "}"+
                                      "},"+
                                      "'pn_apns': {"+
                                          "'aps': {"+
                                              "'alert': " + escapedMessage +
                                          "}"+
                                      "}"+
                                  "}");
        } catch (JSONException e) {
            throw new PreconditionException("Failed to build JSON push message.", e);
        }
    }
}
