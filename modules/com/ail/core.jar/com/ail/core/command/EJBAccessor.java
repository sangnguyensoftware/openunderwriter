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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ail.core.BaseException;
import com.ail.core.BaseServerException;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.CoreUserImpl;
import com.ail.core.NotImplementedError;
import com.ail.core.configure.Configuration;

/**
 * Provide access to EJB entry points. The entry point is accessed by its remote
 * interface.
 */
public class EJBAccessor extends Accessor {
    private static Hashtable<EJBAccessorSettings,CacheElement> cache=new Hashtable<EJBAccessorSettings,CacheElement>();
    private EJBAccessorSettings settings=new EJBAccessorSettings();
    private Argument args=null;

    /**
     * Get hold of the EJBHome associated with this accessor
	 * Each instance of this class (EJBAccessor) has an EJB associated with it.
     * This method finds the EJBHome associated with this instance.
     * @return The home interface of the EJB associated with this accessor.
     */
	private CacheElement getEJBDetails(String methodName, Object[] args) {
        Class<?> remoteInterfaceClass=null;
        Object remoteInterface=null;

        CoreProxy cp=new CoreProxy(getArgs().getCallersCore());
        
        Properties props=cp.getGroup("JEEAccessorContext").getParameterAsProperties();

        // Get the home interface
        try {
            InitialContext context=new InitialContext(props);
            remoteInterface=context.lookup(getJndiName());
        }
        catch(NamingException e) {
            throw new AccessorError("EJBAccessor: failed to find EJB: "+e);
        }

        // get the create and invoke methods details.
        int idx=0;

        remoteInterfaceClass=remoteInterface.getClass();

        // loop through the methods on the remote interface looking for the method by
        // name if one was specified, or returning the type of our argument (by convention
        // EJB components accept one arg and return an object of the same type).
        Method methods[]=remoteInterfaceClass.getDeclaredMethods();
        for(idx=0 ; idx<methods.length ; idx++) {
            if (methodName==null) {
                if (methods[idx].getReturnType().isAssignableFrom(args[0].getClass())) {
                    break;
                }
            }
            else {
                if (methodName.equals(methods[idx].getName())) {
                    break;
                }
            }
        }

        // if we dropped out of the above loop early, then we found a match,
        // create a cache element and add it to the cache.
        if (idx!=methods.length) {
            return new CacheElement(remoteInterface, methods[idx]);
        }
        else {
            throw new AccessorError("EJBAccessor: "+getJndiName()+"."+getRemoteMethod()+"() NoSuchMethodException");
        }
    }

    /**
     * Invoke a method on an EJB. This can be quite an expensive process, i.e.<ol>
     * <li>Get the EJBHome for the EJB specified in our 'settings'.</li>
     * <li>Create an instance of the EJB.</li>
     * <li>Get the EJBs meta data, and from there get its remote interface</li>
     * <li>Loop through the methods on the remote interface looking for one to match
     * our settings.</li>
     * <li>Use the reflection API to invoke the method.</li></ol>
     * Pretty expensive, so we'll try to avoid the first four steps by using a cache.
     * @param methodName This is normally null, except when we're being called to invoke
     * getVersion or get/setConfiguration.
     * @param args Args to be passed to the method
     * @return Whatever the method returned
     * @throws AccessorError
     */
	private Object invokeRemoteMethod(String methodName, Object[] args) throws AccessorError, BaseException {
        CacheElement element=null;

        // args is optional (may be null)
        if (args!=null) {
            settings.setArgType(args[0].getClass().getName());
        }

        // invoke the method on the EJB.
        try {
            synchronized(cache) {
                // check the cache for a CacheElement that matches our settings
                element=(CacheElement)cache.get(settings);

                // if there wasn't one there, we'll have to create it.
                if (element==null || methodName!=null) {
                    element=getEJBDetails(methodName, args);
                    if (methodName==null) {
                        cache.put(settings, element);
                    }
                }
            }

		    return element.getRemoteMethod().invoke(element.getRemoteInterface(), args);
        }
        catch(IllegalAccessException e) {
            throw new AccessorError("EJBAccessor: "+getJndiName()+"."+getRemoteMethod()+"() IllegalAccessException");
        }
        catch(InvocationTargetException e) {
        	Throwable exception = e;
        	while(exception.getCause()!=null){
				exception = exception.getCause();
        		if(exception instanceof BaseException){
        			throw (BaseException)exception;
        		}
        	}
        	e.printStackTrace();
            throw new AccessorError("EJBAccessor: "+getJndiName()+"."+getRemoteMethod()+"() threw :"+e.getTargetException());
        }
        catch(BaseServerException e) {
            throw e.getCauseException();
        }
    }

    /**
     * Invoke the service (via the EJB)
     * @throws BaseException
     */
    public void invoke() throws BaseException {
        super.logEntry();

        CoreUser cu=getArgs().getCallersCore();
        getArgs().setCallersCore(new CoreUserImpl(cu));
        Object[] argValues={getArgs()};
		setArgs((Argument)invokeRemoteMethod(getRemoteMethod(), argValues));
        
        super.logExit();
    }

    /**
     * Set the arguments to be passed to the EJB service
     * @param args
     */
    public void setArgs(Argument args) {
		this.args=args;
    }

    /**
     * Get the argument being passed into (or returned by) the EJB
     * service.
     * @return Arguments
     */
    public Argument getArgs() {
		return args;
    }

	public Configuration getConfiguration() {
        try {
    		return (Configuration)invokeRemoteMethod("getConfiguration", null);
        }
        catch(BaseException e) {
            throw new AccessorError(e);
        }
    }

	public void setConfiguration(Configuration properties) {
        throw new NotImplementedError("EJBCommand.setConfiguration");
    }

    public void setJndiName(String jndiName) {
        settings.setJndiName(jndiName);
    }

    public String getJndiName() {
        return settings.getJndiName();
    }

    public void setRemoteMethod(String remoteMethod) {
        settings.setRemoteMethod(remoteMethod);
    }

    public String getRemoteMethod() {
        return settings.getRemoteMethod();
    }

    public EJBAccessorSettings getSettings() {
        return this.settings;
    }

    public void setSettings(EJBAccessorSettings settings) {
        this.settings=settings;
    }

    public Object clone() throws CloneNotSupportedException {
        return this;
    }
}

/**
 * Private class to encapsulate the accessor's settings. We use these settings
 * as a key in a hashtable based cache, that's why they are split out here into
 * a separate class.
 */
class EJBAccessorSettings {
    private String jndiName=null;
    private String remoteMethod=null;
    private String argType=null;
    private int[] hashParts=new int[3];
    private int hashValue;

    /**
     * Set (update) the hash value that hashCode() will return. Each setter calls this
     * method, and it updates the hashValue. This value is simply the sum of the
     * character values of each property (factory, server, etc), added together.
     * @param indx Unique value for each property
     * @param s The string the setter was called with
     */
    private void setHashValue(int indx, String s) {
        hashParts[indx]=0;
        for(int i=s.length() ; i>0 ; hashParts[indx]+=s.charAt(--i));

        hashValue=0;
        for(int i=0 ; i<hashParts.length ; hashValue+=hashParts[i++]);
    }

    public String getJndiName() {
        return jndiName;
    }

    public void setJndiName(String jndiName) {
        this.jndiName = jndiName;
        setHashValue(0, jndiName);
    }

    public String getRemoteMethod() {
        return remoteMethod;
    }

    public void setRemoteMethod(String remoteMethod) {
        this.remoteMethod = remoteMethod;
        setHashValue(1, remoteMethod);
    }

    public String getArgType() {
        return argType;
    }

    public void setArgType(String argType) {
        this.argType=argType;
        setHashValue(2, argType);
    }

    public int hashCode() {
        return hashValue;
    }

    public boolean equals(Object that) {
        return that instanceof EJBAccessorSettings && that.hashCode() == hashValue;
    }
}

/**
 * This class encapsulates the information cached per EJBAccessor.
 * The intention is to hold onto as much information as possible to
 * speed up the creation of the required EJB, and the method the service
 * is wrapped in.<p>
 * Instances of this class are cached to speed up EJB service access.
 */
class CacheElement {
    private Object remoteInterface = null;
    private Method remoteMethod = null;

    public CacheElement(Object remoteInterface, Method remoteMethod) {
        setRemoteInterface(remoteInterface);
        setRemoteMethod(remoteMethod);
    }

    public Object getRemoteInterface() {
        return remoteInterface;
    }

    public void setRemoteInterface(Object remoteInterface) {
        this.remoteInterface = remoteInterface;
    }

    public Method getRemoteMethod() {
        return remoteMethod;
    }

    public void setRemoteMethod(Method remoteMethod) {
        this.remoteMethod = remoteMethod;
    }
}
