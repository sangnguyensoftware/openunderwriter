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

package com.ail.core.factory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.ail.core.Core;
import com.ail.core.CoreProxy;
import com.ail.core.configure.Parameter;
import com.ail.core.configure.Type;

public abstract class AbstractFactory {

    /**
     * invokeMethod - use reflection to invoke a method
     * We're given an object, the name of the method, and a String representing
     * the argument to pass to it - this will always represent a
     * primitive.<p>
     * We use reflection to get all the methods, then search through them to
     * find one of the right name, that accepts only one argument.
     * If that argument's type is primitive, then we'll convert the string arg passed to us into the
     * type needed by the method, and invoke it. If the argument's type is not primitive, then we'll
     * assume that it is the name of another type which should be instantiated and then passed into
     * the method.
	 * @param obj Object to invoke methods upon.
     * @param propertyName propertyName of the method to invoke.
     * @param arg String representing the argument to be passed to the method.
	 * @param core Core class - for logging etc.
     **/
    private void invokeSetter(Object obj, String propertyName, String arg, Core core) {
		Class<?> c=obj.getClass();
		Method[] m=c.getMethods();
		String methodName="set"+propertyName;

		try {
			for(int i=0 ; i<m.length ; i++) {
				if (m[i].getName().equals(methodName)) {
					Class<?> p[]=m[i].getParameterTypes();

					if (p.length==1) {
						Object[] args=new Object[1];

						// Create the argument to be passed into the setter. This is
						// an instance of p[0] created by invoking p[0]'s string constructor.
                        if (p[0].isPrimitive() || p[0]==String.class) {
							Class<?>[] cargs={String.class};
							Constructor<?> constructor=p[0].getConstructor(cargs);
		    			    Object[] oargs={arg};

							// invoke the setter - passing in the new type as the arg
							args[0]=constructor.newInstance(oargs);
						}
                        else {
							args[0]=core.newType(arg);
                        }

						m[i].invoke(obj, args);

						return;
					}
				}
			}
		}
		catch(InvocationTargetException e) {
			core.logError("Exception encoundered setting property:"+e.getTargetException());
        }
		catch(Exception e) {
			core.logError("Exception encountered when applying property: "+propertyName+" value:"+arg+" to:"+obj.getClass()+": "+e);
		}

		core.logError("Bad argument type encoundered: "+propertyName+" on: "+obj.getClass()+" argument: "+arg);
    }

	/**
     * Create an instance of an object.
     * All concrete factories are required to implement this method. The
     * implementation should simply create an instance of the type specified
     * in the typeSpec argument. They should not attempt to process the
     * arguments (parameters) in the spec.
     * @param typeSpec Details of the type to be instantiated
     * @return The instance of the created object
     */
	abstract protected Object instantiateType(Type typeSpec);

	/**
     * Initialise an object with the properties defined in a typeSpec.
     * This method may be overridden by concrete factories, or used as it is.
     * Its purpose is to initialise the object (o) with the property values
     * defined in the typespec.
     * @param o The object to be initialized
     * @param typeSpec What to initialise 'o' with.
     * @return The initialized object
     **/
	protected Object initialiseType(Object o, Type typeSpec, Core core) {
        for(Parameter param: typeSpec.getParameter()) {
			invokeSetter(o, param.getName(), param.getValue(), core);
        }

		return o;
    }

	/**
     * Create an initialized instance of an object.
     * The details defining which object to create and how to initialise it
     * are defined in the typespec argument.
     * @param typeSpec Details of type to be created
     * @return The newly created type
     */
	public final Object createType(Type typeSpec, Core core) {
        Object obj;
        obj=instantiateType(typeSpec);
		obj=initialiseType(obj, typeSpec, core);
        obj=activateType(obj, core, typeSpec);
        return obj;
    }

	/**
     * Should the factory handler attempt to cache the "prototypes" of instances
     * generated by this factory? The FactoryHandler offers a facility where by
     * it hangs onto an instance of the classes generated by a factory so that
     * future requests for instances of the same type can simply clone this
     * prototype rather than calling out to the factory (Builder) again. This
     * can produce a significant performance gain if the factory's instantiation
     * process is slow or resource intensive. So, if you're coding a Factory and
     * you know it will be slow, return true otherwise return false.
     *
     * @return true if prototypes should be cached, false otherwise.
     */
    abstract protected boolean cachePrototype();

    /**
     * If <i>typeSpec</i> defines an 'extends' parameter instantiate the type it names, and
     * merge it's values into <i>subject</i>
     * @param subject The type we're going to merge into
     * @param typeSpec The typespec associated with the subject
     * @param core Core to be used during the merge.
     * @return The merged type (subject with the extended type's values merged in)
     */
    protected com.ail.core.Type mergeWithBase(com.ail.core.Type subject, Type typeSpec, Core core) {
        String extendType=null;

        try {
            extendType=typeSpec.xpathGet("parameter[nameLowerCase='extends']/value", String.class, false);
        }
        catch(Throwable e) {
            // ignore this, the 'if' below will catch it.
        }

        // if this type doesn't extend another, then return it as it is.
        if (extendType==null) {
            return subject;
        }

        // detect a recursive type definition. An attempt to instantiate a type which extends itself
        // would leave the call to ore.newType() (below) looping until we run out of stack.
        if (extendType.equals(typeSpec.getName())) {
            throw new RecursiveTypeError("The type '"+typeSpec.getName()+"' extends itself.");
        }

        CoreProxy cp=new CoreProxy(typeSpec.getNamespace(), core.getCoreUser());

        com.ail.core.Type from=(com.ail.core.Type)cp.newType(extendType);

        subject.mergeWithDataFrom(from, cp.getCore());

        return subject;
    }

    /**
     * Activate a newly create instance. If the object defines a method called 'activate' which accepts a Core.class,
     * the method is called. The intention is that the object carries out any setup it needs to within the activate
     * method. Significantly, this activation is done before the object is placed into the prototype cache. So later
     * requests for instances of the same type will benefit from the activation.
     * @param obj Object to be activated
     * @param core Instance of the Core to pass into the activate method.
     * @return Activated object.
     */
    protected Object activateType(Object obj, Core core, Type typeSpec) {
        Class<?> clazz=obj.getClass();

        try {
            clazz.getMethod("activate", Core.class, Type.class).invoke(obj, core, typeSpec);
        }
        catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (NoSuchMethodException e) {
            // ignore this - if there's no method on the object we just won't call it.
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return obj;
    }
}
