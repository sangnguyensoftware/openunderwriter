package com.ail.core.audit;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum RevisionType implements TypeEnum {
    INSERT("i18n_revison_type_insert"),
    UPDATE("i18n_revison_type_update"),
    DELETE("i18n_revison_type_delete");

    private final String longName;

    RevisionType(String longName) {
        this.longName=longName;
    }

    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    @Override
    public String longName() {
        return longName;
    }

    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static RevisionType forName(String name) {
        return (RevisionType)Functions.enumForName(name, values());
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getLongName() {
        return longName;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }
}
