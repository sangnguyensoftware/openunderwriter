/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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
package com.ail.core.persistence;

import java.util.HashMap;
import java.util.Map;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.FieldChange;
import com.ail.core.command.Command;

/**
 * This class wraps a ThreadLocal object that contains a Set of Commands. This class should be initialised before
 * a persistence session is created so that any code executed within the persistence session may call
 * {@link #addPostCommitCommand(Command)} to add an arbitrary Command that for execution only after the persistence session is committed.
 * Additionally, there is a Map of {@link FieldChange} objects where interested parties can store and later retrieve a list
 * of fields that have been changed in this request.
 * Once the persistence session is committed the persistence transaction should call {@link #executePostCommitCommands()}
 * to execute all the commands outside of the persistence transaction.
 * Finally {@link #destroy()} should be called.
 */
public class OutsideTransactionContext {

    private static ThreadLocal<Map<String, FieldChange>> onChangeFields = new ThreadLocal<>();
    private static ThreadLocal<Map<String, Command>> postCommitCommands = new ThreadLocal<>();

    public static void initialise() {
        onChangeFields.set(new HashMap<String, FieldChange>());
        postCommitCommands.set(new HashMap<String, Command>());
    }

    public static void destroy() {
        onChangeFields.remove();
        postCommitCommands.remove();
    }

    public static Map<String, FieldChange> getOnChangeFields() {
        return onChangeFields.get();
    }

    public static void addOnChangeField(String fieldId, FieldChange fieldChange) {
        onChangeFields.get().put(fieldId, fieldChange);
    }

    public static void addPostCommitCommand(Command command) {
        postCommitCommands.get().put(command.toString(), command);
    }

    public static void executePostCommitCommands() {
        for (Command command : postCommitCommands.get().values()) {
            try {
                command.invoke();
            } catch (BaseException e) {
                new CoreProxy().logError("Failed to invoke postCommitCommand " + command.getClass(), e);
            }
        }
    }
}
