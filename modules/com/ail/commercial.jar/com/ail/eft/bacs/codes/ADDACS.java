package com.ail.eft.bacs.codes;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum ADDACS implements TypeEnum {
    ZERO("0", "Instruction Cancelled - Refer To Payer"),
    ONE("1", "Instruction Cancelled By Payer"),
    TWO("2", "Payer Deceased"),
    THREE("3", "Instruction Cancelled Account Transferred To Another Bank / Building Society"),
    B("B", "Account Closed"),
    C("C", "Account Transferred To Another Bank/Building Society"),
    D("D", "Advance Notice Disputed"),
    E("E", "Instruction Amended"),
    R("R", "Instruction Re-Instated");

    private final String longName;
    private final String description;

    ADDACS() {
        this.longName = name();
        this.description = name();
    }

    ADDACS(String longName) {
        this.longName = longName;
        this.description = longName;
    }

    ADDACS(String longName, String description) {
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
    public static ADDACS forName(String name) {
        return (ADDACS) Functions.enumForName(name, values());
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
