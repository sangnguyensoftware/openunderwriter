/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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

package com.ail.core.command;

import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.hornetq.api.core.Message;

import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.configure.Configuration;

/**
 * Deployment specific command for use with JMS. This accessor object acts as a
 * client to a point-to-point JMS queue. The accessor takes two parameters:
 * Factory and Queue which it uses to locate the ConnectionFacctory and Queue
 * via JNDI.</p> The following example shows how a JMSAccessor can be
 * configured:
 *
 * <pre>
 *   &lt;service name="NotifyPartyByEmailService" builder="ClassBuilder" key="com.ail.core.command.JMSAccessor"&gt;
 *     &lt;parameter name="Factory"&gt;ConnectionFactory&lt;/parameter&gt;
 *     &lt;parameter name="Queue"&gt;queue/AilCommandQueue&lt;/parameter&gt;
 *   &lt;/service&gt;
 * </pre>
 */
public class JMSAccessor extends Accessor {
    private String factory = null;
    private String queue = null;
    private Argument args = null;
    private transient QueueConnectionFactory connectionFactoryInstance = null;
    private transient Queue queueInstance = null;
    private String queueConnectionPrincipal;
    private String queueConnectionPassword;

    @Override
    public void invoke() throws JMSServiceException {
        TextMessage msg;
        QueueConnection conn = null;
        QueueSession session = null;
        QueueSender sender = null;

        try {
            conn = getQueueConnection();
            session = conn.createQueueSession(false, QueueSession.AUTO_ACKNOWLEDGE);
            conn.start();

            // Strip unnecessary stuff from the args
            Argument strippedArgs = (Argument) ((ArgumentImpl) args).clone();
            CoreProxy coreProxy = createCoreProxy(args.getCallersCore());

            strippedArgs.setCallersCore(coreProxy);

            // Create the message
            msg = session.createTextMessage(coreProxy.toXML(strippedArgs).toString());

            // The command server user ArgType to figure out how to 'fromXML' the body of the message
            msg.setStringProperty("ArgType", strippedArgs.getClass().getName());

            msg.setLongProperty("VersionEffectiveDate", strippedArgs.getCallersCore().getVersionEffectiveDate().getTime());

            msg.setStringProperty("ConfigurationNamespace", strippedArgs.getCallersCore().getConfigurationNamespace());

            // All messages have to wait before delivery is attempted.
            msg.setLongProperty(Message.HDR_SCHEDULED_DELIVERY_TIME.toString(), deliveryDelay());

            sender = session.createSender(getQueueInstance());
            sender.send(msg);
        } catch (Exception e) {
            throw new JMSServiceException(e);
        } finally {
            try {
                if (sender != null)
                    sender.close();
                if (conn != null)
                    conn.stop();
                if (session != null)
                    session.close();
                if (conn != null)
                    conn.close();
            } catch (Exception e) {
                throw new JMSServiceException(e);
            }
        }
    }

    CoreProxy createCoreProxy(CoreUser callersCore) {
        return new CoreProxy(callersCore);
    }

    long deliveryDelay() {
        return System.currentTimeMillis() + 5000;
    }

    @Override
    public void setArgs(Argument args) {
        this.args = args;
    }

    @Override
    public Argument getArgs() {
        return args;
    }

    @Override
    public Configuration getConfiguration() {
        throw new CommandInvocationError("Get configuration cannot be invoked on a JMSAccessor service");
    }

    @Override
    public void setConfiguration(Configuration properties) {
        throw new CommandInvocationError("Set configuration cannot be invoked on a JMSAccessor service");
    }

    public void setFactory(String factory) {
        this.factory = factory;
    }

    public String getFactory() {
        return factory;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    public String getQueue() {
        return queue;
    }

    QueueConnection getQueueConnection() throws NamingException, JMSException {
        CoreProxy cp = new CoreProxy(args.getCallersCore());

        if (connectionFactoryInstance == null) {
            Properties props = cp.getGroup("JEEAccessorContext").getParameterAsProperties();
            InitialContext iniCtx = new InitialContext(props);
            connectionFactoryInstance = (QueueConnectionFactory) iniCtx.lookup(getFactory());
            queueConnectionPrincipal = props.getProperty("java.naming.security.principal");
            queueConnectionPassword = props.getProperty("java.naming.security.credentials");
        }

        return connectionFactoryInstance.createQueueConnection(queueConnectionPrincipal, queueConnectionPassword);
    }

    Queue getQueueInstance() throws NamingException {
        CoreProxy cp = new CoreProxy(args.getCallersCore());

        if (queueInstance == null) {
            Properties props = cp.getGroup("JEEAccessorContext").getParameterAsProperties();
            InitialContext iniCtx = new InitialContext(props);
            queueInstance = (Queue) iniCtx.lookup(getQueue());
        }

        return queueInstance;
    }
}
