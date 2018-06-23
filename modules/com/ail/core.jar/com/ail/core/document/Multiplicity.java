package com.ail.core.document;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum Multiplicity implements TypeEnum {
    NONE("None"),
    ONE("One"),
    MANY("Many");
    
    private final String longName;
    
    Multiplicity(String longName) {
        this.longName=longName;
    }
    
    public static Multiplicity forName(String name) {
        return (Multiplicity)Functions.enumForName(name, values());
    }

    @Override
    public String longName() {
        return longName;
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
