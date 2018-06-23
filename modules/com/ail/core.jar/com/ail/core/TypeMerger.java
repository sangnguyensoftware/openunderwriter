/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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

package com.ail.core;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Merge properties from 'donor' into 'subject'. The rules go like this:
 * <pre>
 *  For each property on the donor that isn't transient
 *    If the subject has the same property (or Field in reflection terminology)
 *      If the property is a primitive, a Date or an Enum
 *        If the subject field has <i>no value</i>
 *          Copy the value from donor into subject.
 *      If the property is based on List, Map, Set, or Collection
 *        If the subject's property is null
 *          Create a new List, Map, Set as appropriate
 *          Add the new collection to the subject
 *        For each element in the donor instance which extends Type
 *          If the element has an 'id' field and the subject doesn't have a matching element
 *            Clone the donor's element and add it to the subject
 *          If the subject doesn't contain (!x.contain(y)) the element
 *            Clone the donor's element and add it to the subject
 *        For each element in the donor instance which doesn't extend Type
 *          If the subject doesn't contain (!x.contain(y)) the element
 *            Recurse
 *      If the property is an Object
 *        If it extends Type and subject has <i>no value</i>
 *          Clone the donor's element and add it to the subject
 *        If it doesn't extend Type and the subject has no value
 *          Ignore it - we only merge Types or primitives
 *
 * Where <i>no value</i> means:
 *   boolean: false
 *   int: 0
 *   double: 0
 *   float: 0
 *   char: 0
 *   byte: 0
 *   long: 0
 *   String null or "" or "?" or "i18n_?"
 *   Object: null
 *   Date: null
 *   Enum: null
 *
 * </pre>
 * @param donor
 * @param subject
 * @param core
 */
class TypeMerger {

    private Type source;
    private Type destination;
    private Core core;

    TypeMerger(Type source, Type destination, Core core) {
        this.source = source;
        this.destination = destination;
        this.core = core;
    }

    public void invoke() {
        merge(source, destination);
    }

    @SuppressWarnings("unchecked")
    private void merge(Type donor, Type subject) {
        // If there's nothing to merge (from or into) then give up right away.
        if (donor==null || subject==null) {
            return;
        }

        // If the subject handles its own merge process, then let it.
        if (subject instanceof CanMerge) {
            ((CanMerge)subject).mergeWithDataFrom(donor, core);
            return;
        }

        // get a list of all the fields that the donor has to offer
        ArrayList<Field> donorFields=getAllDeclaredFields(donor.getClass());
        ArrayList<Field> subjectFields=getAllDeclaredFields(subject.getClass());

        Class<?> fieldType=null;
        String fieldName=null;

        for(Field field: donorFields) {
            fieldType=field.getType();
            fieldName=field.getName();

            try {
                if (subjectFields.contains(field)) {
                    if (Modifier.isStatic(field.getModifiers())
                    ||  Modifier.isTransient(field.getModifiers())
                    ||  Core.class.isAssignableFrom(fieldType)) {
                        continue;
                    }

                    if (fieldType.isPrimitive()) {
                        // get the subject's value for this field
                        Object sv=callGetter(fieldName, subject);

                        // if the field is a number...
                        if (sv instanceof Number) {
                            // ...and the subject has the value of zero, then override it.
                            if (Number.class.cast(sv).doubleValue()==0) {
                                callSetter(fieldName, fieldType, subject, callGetter(fieldName, donor));
                            }
                        }
                        // else if the field is a boolean and the subject's value is 'false' then override it.
                        else if (Boolean.class.cast(sv).booleanValue()==false){
                            callSetter(fieldName, fieldType, subject, callGetter(fieldName, donor));
                        }
                    }
                    else if (fieldType==Date.class || fieldType.isEnum()) {
                        // if the subject's value for this field is null, override it with the donor's value
                        Object subjectValue = callGetter(fieldName, subject);
                        if (subjectValue == null) {
                            callSetter(fieldName, fieldType, subject, callGetter(fieldName, donor));
                        }
                    }
                    else if (fieldType==String.class || fieldType==Long.class) {
                        // if the subject's value for this field is null or "?" or "", override it.
                        Object subjectValue = callGetter(fieldName, subject);
                        if (subjectValue == null || "?".equals(subjectValue) || "".equals(subjectValue) || "i18n_?".equals(subjectValue)) {
                            Object donorValue = callGetter(fieldName, donor);
                            if (donorValue!=null) {
                                callSetter(fieldName, fieldType, subject, donorValue);
                            }
                        }
                    }
                    else if (Map.class.isAssignableFrom(fieldType)) {
                        Map<Object,Object> donorMap=(Map<Object,Object>)callGetter(fieldName, donor);
                        Map<Object,Object> subjectMap=(Map<Object,Object>)callGetter(fieldName, subject);

                        // if the donor has a map, but the subject's corresponding map is null...
                        if (donorMap==null && subjectMap==null) {
                            // output a warning. Well behaved classes should always initialise maps.
                            core.logWarning("Subject class "+subject.getClass().getName()+" has null collection for field: "+fieldName+". Class constructors should initialise collections. Collection not merged.");
                            continue;
                        }

                        // if there is no donor map, then ignore it - we can't merge nothing!
                        if (donorMap!=null) {
                            // for each key that the donor map defines...
                            for(Object key: donorMap.keySet()) {
                                Object obj=donorMap.get(key);

                                // if the subject already contains this key, merge it's object with the donor's
                                // object for the same key.
                                if (subjectMap.containsKey(key)) {
                                    if (obj instanceof Type) {
                                        Type tObj=(Type)obj;
                                        merge((Type)tObj.clone(), (Type)subjectMap.get(key));
                                    }
                                }
                                else {
                                    // the subject's map doesn't contain the key, so add a clone of the donor's object.
                                    key=(key instanceof Type) ? ((Type)key).clone() : key;
                                    obj=(key instanceof Type) ? ((Type)obj).clone() : obj;
                                    subjectMap.put(key, obj);
                                }
                            }
                        }
                    }
                    else if (Collection.class.isAssignableFrom(fieldType)) {
                        // if the donor has a collection, but the subject's corresponding collection is null...
                        if (callGetter(fieldName, donor)!=null && callGetter(fieldName, subject)==null) {
                            // output a warning. Well behaved classes should always initialise collections.
                            core.logWarning("Subject class "+subject.getClass().getName()+" has null collection for field: "+fieldName+". Class constructors should initialise collections. Collection not merged.");
                            continue;
                        }

                        Collection<?> c=(Collection<?>)callGetter(fieldName, donor);

                        // if the collection is null, ignore it - we cant't merge nothing!
                        if (c!=null) {
                            // for each object in the donor's collection
                            for(Object dObj: c) {
                                boolean merged=false;
                                // if the subject's collection has an object with the same id
                                for(Object sObj: (Collection<?>)callGetter(fieldName, subject)) {
                                    // merge the objects if:
                                    //  dObj and sObj both implement Identified and are equal by their identifier, or
                                    //  neither dObj or sObj implement Identifier but they are equal by 'equals()'.
                                    if (dObj instanceof Type && sObj instanceof Type &&
                                            ((dObj instanceof Identified && sObj instanceof Identified && ((Identified)sObj).compareById((Identified)dObj))
                                            ||  (!(dObj instanceof Identified || sObj instanceof Identified) && dObj.equals(sObj)))) {
                                        // merge them
                                        merge((Type)dObj, (Type)sObj);
                                        merged=true;
                                        break;
                                    }
                                }

                                // if a match wasn't found in the subject, then add a clone of the donor's
                                if (!merged) {
                                    dObj = (dObj instanceof Type) ? ((Type)dObj).clone() : dObj;
                                    ((Collection<Object>)callGetter(fieldName, subject)).add(dObj);
                                }
                            }
                        }
                    }
                    else if (Type.class.isAssignableFrom(fieldType)) {
                        // if the subject doesn't have a matching value, clone the donor's and us it.
                        if (callGetter(fieldName, subject)==null) {
                            Type t=(Type)callGetter(fieldName, donor);
                            if (t!=null) {
                                callSetter(fieldName, fieldType, subject, t.clone());
                            }
                        }
                        // if the subject does have an instance, then merge the donor's values into it.
                        else {
                            merge((Type)callGetter(fieldName, donor), (Type)callGetter(fieldName, subject));
                        }
                    }
                    else if (Mergable.class.isAssignableFrom(fieldType)) {
                        // if the subject doesn't have a matching value, clone the donor's and us it.
                        if (callGetter(fieldName, subject)==null) {
                            Cloneable t=(Cloneable)callGetter(fieldName, donor);
                            if (t!=null) {
                                callSetter(fieldName, fieldType, subject, t.clone());
                            }
                        }

                        Mergable sd=(Mergable)callGetter(fieldName, subject);
                        Mergable md=(Mergable)callGetter(fieldName, donor);

                        if (sd == null) {
                            if (md != null) {
                                if (md instanceof Cloneable) {
                                    sd = (Mergable) ((Cloneable) md).clone();
                                }
                                else {
                                    core.logWarning("'Mergable' field: "+fieldName+" (of type: +"+fieldType.getName()+") was ignored during merge");
                                }
                            }
                        } else {
                            sd.mergeFrom(md);
                            callSetter(fieldName, fieldType, subject, sd);
                        }
                    }
                    else {
                        core.logWarning("field: "+fieldName+" (of type: +"+fieldType.getName()+") was ignored during merge");
                    }
                }
            }
            catch(Exception e) {
                core.logWarning("Failed to merge: "+fieldName, e);
            }
        }
    }

    /**
     * Invoke the getter for a named field on a given object and return whatever comes back. Chances are
     * the method will be get&lt;fieldName&gt;, but in the case of a boolean it may be is&lt;fieldName&gt;;
     * if the 'get' fails, we'll try the 'is'.
     * @param fieldName field to call the method for.
     * @param subject Object to invoke the method on.
     * @return Whatever the getter returns
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private Object callGetter(String fieldName, Object subject) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        // if fieldname is 'forename', methodTail will be 'Forename'.
        String methodTail=Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);

        try {
            String methodName="get"+methodTail;
            Method method=subject.getClass().getMethod(methodName);
            return method.invoke(subject);
        }
        catch(NoSuchMethodException e) {
            try {
                String methodName="is"+methodTail;
                Method method=subject.getClass().getMethod(methodName);
                return method.invoke(subject);
            }
            catch(NoSuchMethodException x) {
                throw new NoSuchMethodError(subject.getClass().getName()+".[is|get]"+methodTail);
            }
        }
    }

    private void callSetter(String fieldName, Class<?> fieldType, Object on, Object value) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
        String methodName="set"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
        Method method=on.getClass().getMethod(methodName, fieldType);
        method.invoke(on, value);
    }

    /**
     * Build a list of all the fields on the specified class that should
     * be considered for cloning in the {@link #clone() clone()} method below.
     * We'll only return methods from super types which implement Type.
     * @param clazz Class to collect fields for
     * @return An ArrayList of instances of {@link java.lang.reflect.Field Field}
     */
    private ArrayList<Field> getAllDeclaredFields(Class<?> clazz) {
        ArrayList<Field> al=new ArrayList<>(20);
        ArrayList<String> names=new ArrayList<>();

        // Go up the class tree as far as Type, but DONT include Type itself.
        // Also, take care not to add the same field twice.
        for(Class<?> c=clazz ; c!=Type.class ; c=c.getSuperclass()) {
            for(Field f: c.getDeclaredFields()) {
                if (!names.contains(f.getName())) {
                    al.add(f);
                    names.add(f.getName());
                }
            }
        }

        // The only field we want from Type is attribute, so if clazz is a
        // sub-type of Type, then add the attribute field.
        if (Type.class.isAssignableFrom(clazz)) {
          try {
            al.add(Type.class.getDeclaredField("attribute"));
          }
          catch(NoSuchFieldException e) {
            e.printStackTrace();
            throw new NotImplementedError("Cannot find the attribute field on Type");
          }
        }

        return al;
    }
}
