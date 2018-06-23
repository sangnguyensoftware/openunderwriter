package com.ail.eft.bacs.codes;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum AWACS implements TypeEnum {
    ZERO("0", "Invalid Details"),
    THREE("3", "Account transferred");

    private final String longName;
    private final String description;

    AWACS() {
        this.longName = name();
        this.description = name();
    }

    AWACS(String longName) {
        this.longName = longName;
        this.description = longName;
    }

    AWACS(String longName, String description) {
        this.longName = longName;
        this.description = description;
    }

    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    @Override
    public String longName() {
        return longName;
    }

    /**
     * This method is similar to the valueOf() method offered by Java's Enum type,
     * but in this case it will match either the Enum's name or the longName.
     *
     * @param name
     *            The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a
     *         match.
     */
    public static AWACS forName(String name) {
        return (AWACS) Functions.enumForName(name, values());
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getLongName() {
        return longName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }
}
