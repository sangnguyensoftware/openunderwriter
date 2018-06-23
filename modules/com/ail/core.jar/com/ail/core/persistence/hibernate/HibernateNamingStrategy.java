package com.ail.core.persistence.hibernate;

import static com.ail.core.Functions.classForName;
import static org.apache.commons.lang.StringUtils.capitalize;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.cfg.EJB3NamingStrategy;

import com.ail.annotation.TypeDefinition;

public class HibernateNamingStrategy extends EJB3NamingStrategy {
    private static final String DISCRIMINATOR_COLUMN_SUFFIX = "DSC";
    private static final int PREFIX_LENGTH = 3;
    private static final String JOIN_TABLE_PREFIX = "j";
    private static final String COLLETION_TABLE_PREFIX = "c";
    private String currentTablePrefix = null;
    private static Map<String,String> tablePrefixCache = new HashMap<>();

    @Override
    public String collectionTableName(final String ownerEntity, final String ownerEntityTable, final String associatedEntity, final String associatedEntityTable, final String propertyName) {
        if (associatedEntity!=null) {
            return joinTableNameFor(ownerEntityTable, associatedEntityTable, propertyName);
        }
        else {
            return collectionTableNameFor(ownerEntityTable, propertyName);
        }
    }

    private String collectionTableNameFor(final String ownerEntityTable, final String propertyName) {
        currentTablePrefix = null;
        return COLLETION_TABLE_PREFIX + capitalize(tableNamePrefixFor(ownerEntityTable)) + capitalize(tableNamePrefixFor(propertyName));
    }

    private String joinTableNameFor(final String ownerEntityTable, final String associatedEntityTable, final String propertyName) {
        currentTablePrefix = null;
        return JOIN_TABLE_PREFIX + capitalize(tableNamePrefixFor(ownerEntityTable)) + capitalize(tableNamePrefixFor(propertyName)) + capitalize(tableNamePrefixFor(associatedEntityTable));
    }

    @Override
    public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
        return (propertyName == null ? "" : propertyName) + referencedColumnName + tableNamePrefixFor(propertyTableName);
    }

    @Override
    public String columnName(String columnName) {
        return prefixedColumnName(columnName);
    }

    @Override
    public String propertyToColumnName(String propertyName) {
        return prefixedColumnName(propertyName);
    }

    /**
     * The className argument which Hibernate passes in may be either the simple
     * name of the class or the fully qualified name due to a gap in the JPA spec.
     */
    @Override
    public String classToTableName(String className) {
        currentTablePrefix = tableNamePrefixFor(className);

        if (className.contains(".")) {
            return currentTablePrefix + className.substring(className.lastIndexOf('.') + 1);
        }
        else {
            return currentTablePrefix + className;
        }
    }

    private String prefixedColumnName(String name) {
        if (columnNameCanBeCalculated(name)) {
            return currentTablePrefix + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        } else {
            return super.columnName(name);
        }
    }

    private boolean columnNameCanBeCalculated(String name) {
        if (currentTablePrefix == null) {
            return false;
        }
        if (name.contains("_")) {
            return false;
        }
        if (name.endsWith(DISCRIMINATOR_COLUMN_SUFFIX)) {
            return false;
        }
        if (name.length() > 4 && Character.isUpperCase(name.charAt(3))) {
            return false;
        }
        if (name.equals("REV")) {
            return false;
        }

        return true;
    }

    private String tableNamePrefixFor(String className) {
        String prefix = null;

        if (tablePrefixCache.containsKey(className)) {
            prefix = tablePrefixCache.get(className);
        }

        if (prefix == null && className.contains(".")) {
            try {
                prefix = derivePrefixFromAnnotation(classForName(className));
            } catch (ClassNotFoundException e) {
                // do nothing - fall back to using the class name approach.
            }
        }

        if (prefix == null) {
            prefix = derivePrefixFromClassName(className);
        }

        tablePrefixCache.put(className, prefix);

        if (className.contains(".")) {
            tablePrefixCache.put(className.substring(className.lastIndexOf('.')+1), prefix);
        }

        return prefix;
    }

    /**
     * If the class has a TypeDefinition annotation, and that annotation defines a 'prefix', return it.
     */
    private String derivePrefixFromAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(TypeDefinition.class)) {
            TypeDefinition td = clazz.getAnnotation(TypeDefinition.class);
            if (td != null && !TypeDefinition.UNDEFINED_PREFIX.equals(td.prefix())) {
                return td.prefix();
            }
        }
        return null;
    }

    /**
     * Generate a table name based on the class name. Initially, this attempts
     * to make a prefix based on the upper-case characters in the class name. If
     * there aren't a sufficient number of them, it will use the lower-case
     * chars following the last upper-case character to make up the full
     * PREFIX_LENGTH. For example, if the class name is "MyClassName", then the
     * prefix will be "mcn"; if the class name is "MyClass" the prefix will be
     * "mcl".
     *
     * @param className
     * @return table name, or null if there weren't enough upper-case chars to
     *         generate the required prefix length.
     */
    private String derivePrefixFromClassName(String className) {
        int prefixIdx = 0;
        int lastUpperIndex = 0;
        char[] prefix = new char[PREFIX_LENGTH];
        char[] src = className.toCharArray();

        prefix[prefixIdx++] = Character.toLowerCase(src[0]);

        for (int i = 1; i < src.length; i++) {
            char c = src[i];
            if (Character.isUpperCase(c)) {
                prefix[prefixIdx++] = Character.toLowerCase(c);
                if (prefixIdx == PREFIX_LENGTH) {
                    return new String(prefix);
                }
                lastUpperIndex = i;
            }
        }

        while (prefixIdx < PREFIX_LENGTH) {
            prefix[prefixIdx++] = Character.toLowerCase(src[++lastUpperIndex]);
        }

        return new String(prefix);
    }

}
