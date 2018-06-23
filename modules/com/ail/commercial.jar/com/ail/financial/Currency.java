/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.financial;

import com.ail.core.Functions;
import com.ail.core.ThreadLocale;
import com.ail.core.TypeEnum;

/**
 * Enum of valid currency codes. Source is: http://www.iso.org/iso/support/currency_codes_list-1.htm
 */
public enum Currency implements TypeEnum {
    AED("UNITED ARAB EMIRATES", "UAE Dirham", "784"),
    AFN("AFGHANISTAN", "Afghani", "971"),
    ALL("ALBANIA", "Lek", "008"),
    AMD("ARMENIA", "Armenian Dram", "051"),
    ANG("NETHERLANDS ANTILLES", "Netherlands Antillian Guilder", "532"),
    AOA("ANGOLA", "Kwanza", "973"),
    ARS("ARGENTINA", "Argentine Peso", "032"),
    AUD("AUSTRALIA", "Australian Dollar", "036"),
    AWG("ARUBA", "Aruban Guilder", "533"),
    AZN("AZERBAIJAN", "Azerbaijanian Manat", "944"),
    BAM("BOSNIA AND HERZEGOVINA", "Convertible Marks", "977"),
    BBD("BARBADOS", "Barbados Dollar", "052"),
    BDT("BANGLADESH", "Taka", "050"),
    BGN("BULGARIA", "Bulgarian Lev", "975"),
    BHD("BAHRAIN", "Bahraini Dinar", "048"),
    BIF("BURUNDI", "Burundi Franc", "108"),
    BMD("BERMUDA", "Bermuda Dollar", "060"),
    BND("BRUNEI DARUSSALAM", "Brunei Dollar", "096"),
    BOB("BOLIVIA", "Boliviano", "068"),
    BOV("BOLIVIA", "Mvdol", "984" ),
    BRL("BRAZIL", "Brazilian Real", "986"),
    BSD("BAHAMAS", "Bahamian Dollar", "044"),
    BTN("BHUTAN", "Ngultrum", "064"),
    BWP("BOTSWANA", "Pula", "072"),
    BYR("BELARUS", "Belarussian Ruble", "974"),
    BZD("BELIZE", "Belize Dollar", "084"),
    CAD("CANADA", "Canadian Dollar", "124"),
    CDF("CONGO, THE DEMOCRATIC REPUBLIC OF", "Congolese Franc", "976"),
    CHE("SWITZERLAND", "WIR Euro", "947"),
    CHF("SWITZERLAND", "Swiss Franc", "756"),
    CHW("SWITZERLAND", "WIR Franc", "948"),
    CLF("CHILE", "Unidades de fomento", "990"),
    CLP("CHILE", "Chilean Peso", "152"),
    CNY("CHINA", "Yuan Renminbi", "156"),
    COP("COLOMBIA", "Colombian Peso", "170"),
    COU("COLOMBIA", "Unidad de Valor Real", "970"),
    CRC("COSTA RICA", "Costa Rican Colon", "188"),
    CUC("CUBA", "Peso Convertible", "931"),
    CUP("CUBA", "Cuban Peso", "192"),
    CVE("CAPE VERDE", "Cape Verde Escudo", "132"),
    CZK("CZECH REPUBLIC", "Czech Koruna", "203"),
    DJF("DJIBOUTI", "Djibouti Franc", "262"),
    DKK("DENMARK", "Danish Krone", "208"),
    DOP("DOMINICAN REPUBLIC", "Dominican Peso", "214"),
    DZD("ALGERIA", "Algerian Dinar", "012"),
    EEK("ESTONIA", "Kroon", "233"),
    EGP("EGYPT", "Egyptian Pound", "818"),
    ERN("ERITREA", "Nakfa", "232"),
    ETB("ETHIOPIA", "Ethiopian Birr", "230"),
    EUR("EUROPEAN UNION", "Euro", "978"),
    FJD("FIJI", "Fiji Dollar", "242"),
    FKP("FALKLAND ISLANDS (MALVINAS)", "Falkland Islands Pound", "238"),
    GBP("UNITED KINGDOM", "Pound Sterling", "826"),
    GEL("GEORGIA", "Lari", "981"),
    GHS("GHANA", "Cedi", "936"),
    GIP("GIBRALTAR", "Gibraltar Pound", "292"),
    GMD("GAMBIA", "Dalasi", "270"),
    GNF("GUINEA", "Guinea Franc", "324"),
    GTQ("GUATEMALA", "Quetzal", "320"),
    GYD("GUYANA", "Guyana Dollar", "328"),
    HKD("HONG KONG", "Hong Kong Dollar", "344"),
    HNL("HONDURAS", "Lempira", "340"),
    HRK("CROATIA", "Croatian Kuna", "191"),
    HTG("HAITI", "Gourde", "332"),
    HUF("HUNGARY", "Forint", "348"),
    IDR("INDONESIA", "Rupiah", "360"),
    ILS("ISRAEL", "New Israeli Sheqel", "376"),
    INR("INDIA", "Indian Rupee", "356"),
    IQD("IRAQ", "Iraqi Dinar", "368"),
    IRR("IRAN, ISLAMIC REPUBLIC OF", "Iranian Rial", "364"),
    ISK("ICELAND", "Iceland Krona", "352"),
    JMD("JAMAICA", "Jamaican Dollar", "388"),
    JOD("JORDAN", "Jordanian Dinar", "400"),
    JPY("JAPAN", "Yen", "392"),
    KES("KENYA", "Kenyan Shilling", "404"),
    KGS("KYRGYZSTAN", "Som", "417"),
    KHR("CAMBODIA", "Riel", "116"),
    KMF("COMOROS", "Comoro Franc", "174"),
    KPW("KOREA", "North Korean Won", "408"),
    KWD("KUWAIT", "Kuwaiti Dinar", "414"),
    KYD("CAYMAN ISLANDS", "Cayman Islands Dollar", "136"),
    KZT("KAZAKHSTAN", "Tenge", "398"),
    LAK("LAO PEOPLE'S DEMOCRATIC REPUBLIC", "Kip", "418"),
    LBP("LEBANON", "Lebanese Pound", "422"),
    LKR("SRI LANKA", "Sri Lanka Rupee", "144"),
    LRD("LIBERIA", "Liberian Dollar", "430"),
    LSL("LESOTHO", "Loti", "426"),
    LTL("LITHUANIA", "Lithuanian Litas", "440"),
    LVL("LATVIA", "Latvian Lats", "428"),
    LYD("LIBYAN ARAB JAMAHIRIYA", "Libyan Dinar", "434"),
    MAD("MOROCCO", "Moroccan Dirham", "504"),
    MDL("MOLDOVA, REPUBLIC OF", "Moldovan Leu", "498"),
    MGA("MADAGASCAR", "Malagasy Ariary", "969"),
    MKD("MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF", "Denar", "807"),
    MMK("MYANMAR", "Kyat", "104"),
    MNT("MONGOLIA", "Tugrik", "496"),
    MOP("MACAO", "Pataca", "446"),
    MRO("MAURITANIA", "Ouguiya", "478"),
    MUR("MAURITIUS", "Mauritius Rupee", "480"),
    MVR("MALDIVES", "Rufiyaa", "462"),
    MWK("MALAWI", "Kwacha", "454"),
    MXN("MEXICO", "Mexican Peso", "484"),
    MXV("MEXICO ", "Mexican Unidad de Inversion (UDI)", "979"),
    MYR("MALAYSIA", "Malaysian Ringgit", "458"),
    MZN("MOZAMBIQUE", "Metical", "943"),
    NAD("NAMIBIA", "Namibia Dollar", "516"),
    NGN("NIGERIA", "Naira", "566"),
    NIO("NICARAGUA", "Cordoba Oro", "558"),
    NOK("NORWAY", "Norwegian Krone", "578"),
    NPR("NEPAL", "Nepalese Rupee", "524"),
    NZD("NEW ZEALAND", "New Zealand Dollar", "554"),
    OMR("OMAN", "Rial Omani", "512"),
    PAB("PANAMA", "Balboa", "590"),
    PEN("PERU", "Nuevo Sol", "604"),
    PGK("PAPUA NEW GUINEA", "Kina", "598"),
    PHP("PHILIPPINES", "Philippine Peso", "608"),
    PKR("PAKISTAN", "Pakistan Rupee", "586"),
    PLN("POLAND", "Zloty", "985"),
    PYG("PARAGUAY", "Guarani", "600"),
    QAR("QATAR", "Qatari Rial", "634"),
    RON("ROMANIA", "New Leu", "946"),
    RSD("SERBIA", "Serbian Dinar", "941"),
    RUB("RUSSIAN FEDERATION", "Russian Ruble", "643"),
    RWF("RWANDA", "Rwanda Franc", "646"),
    SAR("SAUDI ARABIA", "Saudi Riyal", "682"),
    SBD("SOLOMON ISLANDS", "Solomon Islands Dollar", "090"),
    SCR("SEYCHELLES", "Seychelles Rupee", "690"),
    SDG("SUDAN", "Sudanese Pound", "938"),
    SEK("SWEDEN", "Swedish Krona", "752"),
    SGD("SINGAPORE", "Singapore Dollar", "702"),
    SHP("SAINT HELENA, ASCENSION AND TRISTAN DA CUNHA", "Saint Helena Pound", "654"),
    SLL("SIERRA LEONE", "Leone", "694"),
    SOS("SOMALIA", "Somali Shilling", "706"),
    SRD("SURINAME", "Surinam Dollar", "968"),
    STD("SO TOME AND PRINCIPE", "Dobra", "678"),
    SVC("EL SALVADOR", "El Salvador Colon", "222"),
    SYP("SYRIAN ARAB REPUBLIC", "Syrian Pound", "760"),
    SZL("SWAZILAND", "Lilangeni", "748"),
    THB("THAILAND", "Baht", "764"),
    TJS("TAJIKISTAN", "Somoni", "972"),
    TMT("TURKMENISTAN", "Manat", "934"),
    TND("TUNISIA", "Tunisian Dinar", "788"),
    TOP("TONGA", "Pa'anga", "776"),
    TRY("TURKEY", "Turkish Lira", "949"),
    TTD("TRINIDAD AND TOBAGO", "Trinidad and Tobago Dollar", "780"),
    TWD("TAIWAN, PROVINCE OF CHINA", "New Taiwan Dollar", "901"),
    TZS("TANZANIA, UNITED REPUBLIC OF", "Tanzanian Shilling", "834"),
    UAH("UKRAINE", "Hryvnia", "980"),
    UGX("UGANDA", "Uganda Shilling", "800"),
    USD("UNITED STATES", "US Dollar", "840"),
    USN("UNITED STATES", "US Dollar (Next day)", "997"),
    USS("UNITED STATES", "US Dollar (Same day)", "998"),
    UYI("URUGUAY", "Uruguay Peso en Unidades Indexadas", "940"),
    UYU("URUGUAY", "Peso Uruguayo", "858"),
    UZS("UZBEKISTAN", "Uzbekistan Sum", "860"),
    VEF("VENEZUELA", "Bolivar Fuerte", "937"),
    VND("VIET NAM", "Dong", "704"),
    VUV("VANUATU", "Vatu", "548"),
    XAF("CENTRAL AFRICAN REPUBLIC", "CFA Franc BEAC", "950"),
    XCD("EAST CARIBBEAN", "East Caribbean Dollar", "951"),
    XOF("CFA", "CFA Franc BCEAO", "952"),
    XPF("FRENCH POLYNESIA", "CFP Franc", "953"),
    YER("YEMEN", "Yemeni Rial", "886"),
    ZAR("SOUTH AFRICA", "Rand", "710"),
    ZMK("ZAMBIA", "Zambian Kwacha", "894"),
    ZWL("ZIMBABWE", "Zimbabwe Dollar", "932");
    
    private final String longName;
    private final String entity;
    private final String code;
    
    Currency(String entity, String longName, String code) {
        this.longName=longName;
        this.entity=entity;
        this.code=code;
    }
    
    public String getLongName() {
        return longName;
    }
    
    public String getName() {
        return name();
    }
    
    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    public String longName() {
        return longName;
    }
    
    /** 
     * Get the number of fractional digits typically used for this currency.
     * @return number of digits
     */
    public int getFractionDigits() {
        return java.util.Currency.getInstance(name()).getDefaultFractionDigits();
    }
    
    /**
     * Get the symbol appropriate to this currency in the current locale.
     * @see com.ail.core.ThreadLocale#getThreadLocale()
     * @return Currency symbol.
     */
    public String getSymbol() {
        return java.util.Currency.getInstance(name()).getSymbol(ThreadLocale.getThreadLocale());
    }
    
    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static Currency forName(String name) {
        return (Currency)Functions.enumForName(name, values());
    }

    public int getOrdinal() {
        return ordinal();
    }

    /** 
     * Return the currency's entity or geographical description
     * @return currency entity 
     */
    public String getEntity() {
        return entity;
    }

    /**
     * Return the currency's ISO code number
     * @return ISO code number
     */
    public String getCode() {
        return code;
    }
}
