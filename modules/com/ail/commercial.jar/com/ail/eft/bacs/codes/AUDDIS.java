package com.ail.eft.bacs.codes;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum AUDDIS implements TypeEnum {
    ONE("1", "Instruction Cancelled By Payer"),
    TWO("2", "Payer Deceased"),
    THREE("3", "Account transferred"),
    FIVE("5", "No account"),
    SIX("6", "No Instruction"),
    B("B", "Account Closed"),
    C("C", "Account Transferred To Another Bank/Building Society"),
    F("F", "Invalid account type"),
    G("G", "Bank will not accept Direct Debits on account"),
    H("H", "Instruction has expired"),
    I("I", "Payer Reference Number is not unique"),
    K("K", "Instruction cancelled by Paying Bank"),
    L("L", "Incorrect Payer’s Account Details"),
    M("M", "Transaction Code/User Status Incompatible"),
    N("N", "Transaction Disallowed At Payer’s Branch"),
    O("O", "Invalid Reference"),
    P("P", "Payer’s Name Not Present"),
    Q("Q", "Service Users Name Is Blank");

    private final String longName;
    private final String description;

    AUDDIS() {
        this.longName = name();
        this.description = name();
    }

    AUDDIS(String longName) {
        this.longName = longName;
        this.description = longName;
    }

    AUDDIS(String longName, String description) {
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
    public static AUDDIS forName(String name) {
        return (AUDDIS) Functions.enumForName(name, values());
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
