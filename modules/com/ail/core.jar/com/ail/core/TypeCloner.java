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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ail.core.command.Argument;

/**
 * This clone service is used by all Type subclasses to handle deep cloning. For
 * the factory to operate correctly it is essential that Types can be deep
 * cloned, as it hangs onto prototyped instances by name and simply clones them
 * when a request is made for an instance of a named type.
 */
class TypeCloner {

    private Object source;
    private Object destination;

    TypeCloner(Object source, Object destination) {
        this.source = source;
        this.destination = destination;
    }

    Object invoke() throws CloneNotSupportedException {
        ArrayList<Field> fields = getAllDeclaredFields(source.getClass());

        Field field = null;
        String fieldName = null;
        Class<?> fieldType = null;
        Method method = null;
        String methodBaseName = null;

        // JXPath's context includes a cache, if we don't clear it the
        // clone's JXPath will point into the thing we cloned!
        ((Type) destination).jXPathContext = null;

        for (int i = fields.size() - 1; i >= 0; i--) {

            try {
                // the field we're cloning
                field = fields.get(i);

                // the class (or type) of the field we're cloning
                fieldType = field.getType();

                // If the field is a primitive, a String, a static, transient,
                // or a core...
                if (cloneWasHandledByJava(field, fieldType)) {
                    // ...ignore it - super.clone() will have handled
                    // primitives, and cores and statics can be ignored altogether.
                    continue;
                }

                // the name of the field we're cloning
                fieldName = field.getName();

                // If the field name was myValue, methodBaseName will be MyValue
                // (used in setMyValue, getMyValue).
                methodBaseName = Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

                // if the field is a Map, or implements Map, then deep clone it
                if (Map.class.isAssignableFrom(fieldType)) {
                    method = null;

                    // Get the Map we're going to clone
                    method = source.getClass().getMethod("get" + methodBaseName);
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) method.invoke(source);

                    // We'll clone into this clonedMap
                    Map<Object, Object> clonedMap = new HashMap<>();

                    Object value = null;

                    // Loop through the map cloning each element into cloneMap.
                    for (Object key : map.keySet()) {
                        value = map.get(key);
                        value = (value instanceof Type) ? ((Type) value).clone() : value;
                        clonedMap.put(key, value);
                    }

                    // Set the cloned map into the clone
                    method = source.getClass().getMethod("set" + methodBaseName, method.getReturnType());
                    method.invoke(destination, clonedMap);
                }
                // if the field is a Set, or implements Set, then deep clone it
                else if (Set.class.isAssignableFrom(fieldType)) {
                    method = null;

                    // Get the Set we're going to clone
                    method = source.getClass().getMethod("get" + methodBaseName);
                    @SuppressWarnings("unchecked")
                    Set<Type> set = (Set<Type>) method.invoke(source);

                    // We'll clone into this clonedSet
                    Set<Object> clonedSet = new HashSet<>();

                    // Loop through the set cloning each element into cloneSet.
                    for (Type value : set) {
                        clonedSet.add(value.clone());
                    }

                    // Set the cloned set into the clone
                    method = source.getClass().getMethod("set" + methodBaseName, method.getReturnType());
                    method.invoke(destination, clonedSet);
                }
                // For types assignable from List, or (as a back stop) fields defined as Collection.
                else if (List.class.isAssignableFrom(fieldType) || fieldType == Collection.class) {
                    method = null;

                    // Get the List we're going to clone
                    method = source.getClass().getMethod("get" + methodBaseName);
                    @SuppressWarnings("unchecked")
                    Collection<Object> list = (Collection<Object>) method.invoke(source);

                    // We'll clone into this List
                    List<Object> clonedList = new ArrayList<>();

                    // Loop through the ArrayList cloning each element into
                    // cloneList. Note: ArrayLists of Strings are quite common,
                    // so we'll handle them too, but generally only classes
                    // based on Type can be cloned in this way.
                    for (Object type : list) {
                        if (type instanceof Type) {
                            clonedList.add(((Type) type).clone());
                        } else {
                            clonedList.add(type);
                        }
                    }

                    // Set the cloned ArrayList into the clone. The assumption
                    // here is that the setter
                    // we're going to call takes only one argument, and that
                    // argument is the same
                    // as that returned by the getter. Safe enough.
                    method = source.getClass().getMethod("set" + methodBaseName, method.getReturnType());
                    method.invoke(destination, clonedList);
                }
                // If the field is a Type, clone it.
                else if (Cloneable.class.isAssignableFrom(fieldType) || Argument.class.isAssignableFrom(fieldType)) {
                    method = null;

                    method = source.getClass().getMethod("get" + methodBaseName);
                    Cloneable fieldValue = (Cloneable) method.invoke(source);

                    Object arg = (fieldValue == null) ? null : fieldValue.clone();

                    method = source.getClass().getMethod("set" + methodBaseName, method.getReturnType());
                    method.invoke(destination, arg);
                }
                // If the field is a Date...
                else if (Date.class.isAssignableFrom(fieldType)) {
                    method = null;

                    method = source.getClass().getMethod("get" + methodBaseName);
                    Date fieldValue = (Date) method.invoke(source);

                    Object arg = (fieldValue == null) ? null : fieldValue.clone();

                    method = source.getClass().getMethod("set" + methodBaseName, fieldType);
                    method.invoke(destination, arg);
                } else if (fieldType.isEnum()) {
                    method = source.getClass().getMethod("get" + methodBaseName);
                    Enum<?> fieldValue = (Enum<?>) method.invoke(source);

                    method = source.getClass().getMethod("set" + methodBaseName, fieldType);
                    method.invoke(destination, fieldValue);
                }
                // If all else fails, set the clone field's value to null.
                else {
                    method = source.getClass().getMethod("set" + methodBaseName, fieldType);
                    method.invoke(destination, new Object[] { null });
                }
            } catch (InvocationTargetException e) {
                CloneNotSupportedException cnse = new CloneNotSupportedException("Attempt to clone " + source.getClass().getName() + "." + fieldName + " threw: "+e.getTargetException());
                cnse.initCause(e.getTargetException());
                throw cnse;
            } catch (Exception e) {
                CloneNotSupportedException cnse = new CloneNotSupportedException("Attempt to clone " + source.getClass().getName() + "." + fieldName + " failed: "+e);
                cnse.initCause(e);
                throw cnse;
            }
        }

        return destination;
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

    boolean cloneWasHandledByJava(Field field, Class<?> fieldType) {
        return fieldType.isPrimitive()
        ||  fieldType==String.class
        ||  fieldType==Object.class
        ||  Modifier.isStatic(field.getModifiers())
        ||  Modifier.isTransient(field.getModifiers())
        ||  Core.class.isAssignableFrom(fieldType)
        ||  Number.class.isAssignableFrom(fieldType);
    }
}
