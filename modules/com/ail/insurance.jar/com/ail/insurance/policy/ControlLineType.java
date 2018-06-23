package com.ail.insurance.policy;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

/**
 * Define how a limit's minimum and maximum values should be applied to the watched line.
 * Possible values are:<ul>
 * <li>INSIDE - Fire the line's if value > minimum && value < maximum</li>
 * <li>OUTSIDE - Fire the line's it value is < minimum || value > maximum</li>
 * </ul>
 */
public enum ControlLineType implements TypeEnum {
    INSIDE("Inside"),
    OUTSIDE("Outside");
    
    private final String longName;
    
    ControlLineType() {
        this.longName=name();
    }
    
    ControlLineType(String longName) {
        this.longName=longName;
    }
   
    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    public String longName() {
        return longName;
    }
    
    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static BehaviourType forName(String name) {
        return (BehaviourType)Functions.enumForName(name, values());
    }
    
    public String getName() {
        return name();
    }
    
    public String getLongName() {
        return longName;
    }

    public int getOrdinal() {
        return ordinal();
    }
}
