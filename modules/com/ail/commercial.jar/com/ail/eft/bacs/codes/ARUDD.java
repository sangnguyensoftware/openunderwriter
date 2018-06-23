package com.ail.eft.bacs.codes;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum ARUDD implements TypeEnum {
    ZERO("0", "Refer To Payer"),
    ONE("1", "Instruction Cancelled"),
    TWO("2", "Payer Deceased"),
    THREE("3", "Account transferred"),
    FOUR("4", "Advance Notice Disputed"),
    FIVE("5", "No Account (OR Wrong Account Type)"),
    SIX("6", "No Instruction"),
    SEVEN("7", "Amount Differs"),
    EIGHT("8", "Amount Not Yet Due"),
    NINE("9", "Presentation Overdue"),
    A("A", "Service User Differs"),
    B("B", "Account Closed");

    private final String longName;
    private final String description;

    ARUDD() {
        this.longName = name();
        this.description = name();
    }

    ARUDD(String longName) {
        this.longName = longName;
        this.description = longName;
    }

    ARUDD(String longName, String description) {
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
    public static ARUDD forName(String name) {
        return (ARUDD) Functions.enumForName(name, values());
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
