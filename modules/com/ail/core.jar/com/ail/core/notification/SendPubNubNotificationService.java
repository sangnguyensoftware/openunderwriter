package com.ail.core.notification;

import static com.ail.core.Functions.productNameToConfigurationNamespace;

import org.json.JSONObject;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.notification.SendPubNubNotificationService.SendPubNubNotificationArgument;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;

/**
 * Low level email transport service
 */
@ServiceImplementation
public class SendPubNubNotificationService extends Service<SendPubNubNotificationArgument> {
    private String configurationNamespace;

    @ServiceArgument
    public interface SendPubNubNotificationArgument extends Argument {
        JSONObject getMessageArg();

        void setMessageArg(JSONObject  messageArg);

        String getChannelArg();

        void setChannelArg(String channelArg);
    }

    @ServiceCommand(defaultServiceClass=SendPubNubNotificationService.class)
    public interface SendPubNubNotificationCommand extends Command, SendPubNubNotificationArgument {
    }

    @Override
    public String getConfigurationNamespace() {
        if (configurationNamespace == null) {
            return super.getConfigurationNamespace();
        } else {
            return configurationNamespace;
        }
    }

    public void setConfigurationNamespace() {
        this.configurationNamespace = productNameToConfigurationNamespace(CoreContext.getProductName());
    }

    @Override
    public void invoke() throws BaseException {
        if (args.getMessageArg() == null) {
            throw new PreconditionException("args.getMessageArg() == null || args.getMessageArg().length() == 0");
        }

        if (args.getChannelArg() == null || args.getChannelArg().length() == 0) {
            throw new PreconditionException("args.getChannelArg() == null || args.getChannelArg().length() == 0");
        }

        setConfigurationNamespace();

        String subscribeKey = getCore().getParameterValue("PubNubProperties.SubscribeKey");

        if (subscribeKey == null) {
            throw new PreconditionException("getCore().getParameterValue('PubNubProperties.SubscribeKey') == null");
        }

        String publishKey = getCore().getParameterValue("PubNubProperties.PublishKey");

        if (publishKey == null) {
            throw new PreconditionException("getCore().getParameterValue('PubNubProperties.PublishKey') == null");
        }

        invokePubNub(subscribeKey, publishKey, args.getChannelArg(), args.getMessageArg());
    }

    void invokePubNub(String subscribeKey, String publishKey, String channel, JSONObject jsonObject) throws PreconditionException {
        new Pubnub(publishKey, subscribeKey).publish(channel, jsonObject, new Callback() {
            @Override
            public void errorCallback(String channel, PubnubError error) {
                throw new PubNubIntegrationError("Failed to send PubNub notification: " + error.toString());
            }
        });
    }}
