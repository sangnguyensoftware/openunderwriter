package com.ail.financial;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum PaymentMethodStatus implements TypeEnum {
    PENDING("i18n_pending"),
    APPROVED("i18n_approved"),
    DECLINED("i18n_declined"),
    REVOKED("i18n_revoked");

    private final String longName;

    PaymentMethodStatus() {
        this.longName=name();
    }

    PaymentMethodStatus(String longName) {
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
    public static PaymentMethodStatus forName(String name) {
        return (PaymentMethodStatus)Functions.enumForName(name, values());
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
