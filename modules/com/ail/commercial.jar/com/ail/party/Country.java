package com.ail.party;

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

/**
 * An enum of countries as defined by ISO-3166
 */
public enum Country implements TypeEnum {
    ALA("AALAND ISLANDS","AX","248"),
    AFG("AFGHANISTAN","AF","004"),
    ALB("ALBANIA","AL","008"),
    DZA("ALGERIA","DZ","012"),
    ASM("AMERICAN SAMOA","AS","016"),
    AND("ANDORRA","AD","020"),
    AGO("ANGOLA","AO","024"),
    AIA("ANGUILLA","AI","660"),
    ATA("ANTARCTICA","AQ","010"),
    ATG("ANTIGUA AND BARBUDA","AG","028"),
    ARG("ARGENTINA","AR","032"),
    ARM("ARMENIA","AM","051"),
    ABW("ARUBA","AW","533"),
    AUS("AUSTRALIA","AU","036"),
    AUT("AUSTRIA","AT","040"),
    AZE("AZERBAIJAN","AZ","031"),
    BHS("BAHAMAS","BS","044"),
    BHR("BAHRAIN","BH","048"),
    BGD("BANGLADESH","BD","050"),
    BRB("BARBADOS","BB","052"),
    BLR("BELARUS","BY","112"),
    BEL("BELGIUM","BE","056"),
    BLZ("BELIZE","BZ","084"),
    BEN("BENIN","BJ","204"),
    BMU("BERMUDA","BM","060"),
    BTN("BHUTAN","BT","064"),
    BOL("BOLIVIA","BO","068"),
    BIH("BOSNIA AND HERZEGOWINA","BA","070"),
    BWA("BOTSWANA","BW","072"),
    BVT("BOUVET ISLAND","BV","074"),
    BRA("BRAZIL","BR","076"),
    IOT("BRITISH INDIAN OCEAN TERRITORY","IO","086"),
    BRN("BRUNEI DARUSSALAM","BN","096"),
    BGR("BULGARIA","BG","100"),
    BFA("BURKINA FASO","BF","854"),
    BDI("BURUNDI","BI","108"),
    KHM("CAMBODIA","KH","116"),
    CMR("CAMEROON","CM","120"),
    CAN("CANADA","CA","124"),
    CPV("CAPE VERDE","CV","132"),
    CYM("CAYMAN ISLANDS","KY","136"),
    CAF("CENTRAL AFRICAN REPUBLIC","CF","140"),
    TCD("CHAD","TD","148"),
    CHL("CHILE","CL","152"),
    CHN("CHINA","CN","156"),
    CXR("CHRISTMAS ISLAND","CX","162"),
    CCK("COCOS (KEELING) ISLANDS","CC","166"),
    COL("COLOMBIA","CO","170"),
    COM("COMOROS","KM","174"),
    COD("CONGO, Democratic Republic of (was Zaire)","CD","180"),
    COG("CONGO, Republic of","CG","178"),
    COK("COOK ISLANDS","CK","184"),
    CRI("COSTA RICA","CR","188"),
    CIV("COTE D'IVOIRE","CI","384"),
    HRV("CROATIA (local name: Hrvatska)","HR","191"),
    CUB("CUBA","CU","192"),
    CYP("CYPRUS","CY","196"),
    CZE("CZECH REPUBLIC","CZ","203"),
    DNK("DENMARK","DK","208"),
    DJI("DJIBOUTI","DJ","262"),
    DMA("DOMINICA","DM","212"),
    DOM("DOMINICAN REPUBLIC","DO","214"),
    ECU("ECUADOR","EC","218"),
    EGY("EGYPT","EG","818"),
    SLV("EL SALVADOR","SV","222"),
    GNQ("EQUATORIAL GUINEA","GQ","226"),
    ERI("ERITREA","ER","232"),
    EST("ESTONIA","EE","233"),
    ETH("ETHIOPIA","ET","231"),
    FLK("FALKLAND ISLANDS (MALVINAS)","FK","238"),
    FRO("FAROE ISLANDS","FO","234"),
    FJI("FIJI","FJ","242"),
    FIN("FINLAND","FI","246"),
    FRA("FRANCE","FR","250"),
    GUF("FRENCH GUIANA","GF","254"),
    PYF("FRENCH POLYNESIA","PF","258"),
    ATF("FRENCH SOUTHERN TERRITORIES","TF","260"),
    GAB("GABON","GA","266"),
    GMB("GAMBIA","GM","270"),
    GEO("GEORGIA","GE","268"),
    DEU("GERMANY","DE","276"),
    GHA("GHANA","GH","288"),
    GIB("GIBRALTAR","GI","292"),
    GRC("GREECE","GR","300"),
    GRL("GREENLAND","GL","304"),
    GRD("GRENADA","GD","308"),
    GLP("GUADELOUPE","GP","312"),
    GUM("GUAM","GU","316"),
    GTM("GUATEMALA","GT","320"),
    GIN("GUINEA","GN","324"),
    GNB("GUINEA-BISSAU","GW","624"),
    GUY("GUYANA","GY","328"),
    HTI("HAITI","HT","332"),
    HMD("HEARD AND MC DONALD ISLANDS","HM","334"),
    HND("HONDURAS","HN","340"),
    HKG("HONG KONG","HK","344"),
    HUN("HUNGARY","HU","348"),
    ISL("ICELAND","IS","352"),
    IND("INDIA","IN","356"),
    IDN("INDONESIA","ID","360"),
    IRN("IRAN (ISLAMIC REPUBLIC OF)","IR","364"),
    IRQ("IRAQ","IQ","368"),
    IRL("IRELAND","IE","372"),
    ISR("ISRAEL","IL","376"),
    ITA("ITALY","IT","380"),
    JAM("JAMAICA","JM","388"),
    JPN("JAPAN","JP","392"),
    JOR("JORDAN","JO","400"),
    KAZ("KAZAKHSTAN","KZ","398"),
    KEN("KENYA","KE","404"),
    KIR("KIRIBATI","KI","296"),
    PRK("KOREA, DEMOCRATIC PEOPLE'S REPUBLIC OF","KP","408"),
    KOR("KOREA, REPUBLIC OF","KR","410"),
    KWT("KUWAIT","KW","414"),
    KGZ("KYRGYZSTAN","KG","417"),
    LAO("LAO PEOPLE'S DEMOCRATIC REPUBLIC","LA","418"),
    LVA("LATVIA","LV","428"),
    LBN("LEBANON","LB","422"),
    LSO("LESOTHO","LS","426"),
    LBR("LIBERIA","LR","430"),
    LBY("LIBYAN ARAB JAMAHIRIYA","LY","434"),
    LIE("LIECHTENSTEIN","LI","438"),
    LTU("LITHUANIA","LT","440"),
    LUX("LUXEMBOURG","LU","442"),
    MAC("MACAU","MO","446"),
    MKD("MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF","MK","807 "),
    MDG("MADAGASCAR","MG","450"),
    MWI("MALAWI","MW","454"),
    MYS("MALAYSIA","MY","458"),
    MDV("MALDIVES","MV","462"),
    MLI("MALI","ML","466"),
    MLT("MALTA","MT","470"),
    MHL("MARSHALL ISLANDS","MH","584"),
    MTQ("MARTINIQUE","MQ","474"),
    MRT("MAURITANIA","MR","478"),
    MUS("MAURITIUS","MU","480"),
    MYT("MAYOTTE","YT","175"),
    MEX("MEXICO","MX","484"),
    FSM("MICRONESIA, FEDERATED STATES OF","FM","583"),
    MDA("MOLDOVA, REPUBLIC OF","MD","498"),
    MCO("MONACO","MC","492"),
    MNG("MONGOLIA","MN","496"),
    MSR("MONTSERRAT","MS","500"),
    MAR("MOROCCO","MA","504"),
    MOZ("MOZAMBIQUE","MZ","508"),
    MMR("MYANMAR","MM","104"),
    NAM("NAMIBIA","NA","516"),
    NRU("NAURU","NR","520"),
    NPL("NEPAL","NP","524"),
    NLD("NETHERLANDS","NL","528"),
    ANT("NETHERLANDS ANTILLES","AN","530"),
    NCL("NEW CALEDONIA","NC","540"),
    NZL("NEW ZEALAND","NZ","554"),
    NIC("NICARAGUA","NI","558"),
    NER("NIGER","NE","562"),
    NGA("NIGERIA","NG","566"),
    NIU("NIUE","NU","570"),
    NFK("NORFOLK ISLAND","NF","574"),
    MNP("NORTHERN MARIANA ISLANDS","MP","580"),
    NOR("NORWAY","NO","578"),
    OMN("OMAN","OM","512"),
    PAK("PAKISTAN","PK","586"),
    PLW("PALAU","PW","585"),
    PSE("PALESTINIAN TERRITORY, Occupied","PS","275"),
    PAN("PANAMA","PA","591"),
    PNG("PAPUA NEW GUINEA","PG","598"),
    PRY("PARAGUAY","PY","600"),
    PER("PERU","PE","604"),
    PHL("PHILIPPINES","PH","608"),
    PCN("PITCAIRN","PN","612"),
    POL("POLAND","PL","616"),
    PRT("PORTUGAL","PT","620"),
    PRI("PUERTO RICO","PR","630"),
    QAT("QATAR","QA","634"),
    REU("REUNION","RE","638"),
    ROU("ROMANIA","RO","642"),
    RUS("RUSSIAN FEDERATION","RU","643"),
    RWA("RWANDA","RW","646"),
    SHN("SAINT HELENA","SH","654"),
    KNA("SAINT KITTS AND NEVIS","KN","659"),
    LCA("SAINT LUCIA","LC","662"),
    SPM("SAINT PIERRE AND MIQUELON","PM","666"),
    VCT("SAINT VINCENT AND THE GRENADINES","VC","670"),
    WSM("SAMOA","WS","882"),
    SMR("SAN MARINO","SM","674"),
    STP("SAO TOME AND PRINCIPE","ST","678"),
    SAU("SAUDI ARABIA","SA","682"),
    SEN("SENEGAL","SN","686"),
    SCG("SERBIA AND MONTENEGRO","CS","891"),
    SYC("SEYCHELLES","SC","690"),
    SLE("SIERRA LEONE","SL","694"),
    SGP("SINGAPORE","SG","702"),
    SVK("SLOVAKIA","SK","703"),
    SVN("SLOVENIA","SI","705"),
    SLB("SOLOMON ISLANDS","SB","090"),
    SOM("SOMALIA","SO","706"),
    ZAF("SOUTH AFRICA","ZA","710"),
    SGS("SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS","GS","239"),
    ESP("SPAIN","ES","724"),
    LKA("SRI LANKA","LK","144"),
    SDN("SUDAN","SD","736"),
    SUR("SURINAME","SR","740"),
    SJM("SVALBARD AND JAN MAYEN ISLANDS","SJ","744"),
    SWZ("SWAZILAND","SZ","748"),
    SWE("SWEDEN","SE","752"),
    CHE("SWITZERLAND","CH","756"),
    SYR("SYRIAN ARAB REPUBLIC","SY","760"),
    TWN("TAIWAN","TW","158"),
    TJK("TAJIKISTAN","TJ","762"),
    TZA("TANZANIA, UNITED REPUBLIC OF","TZ","834"),
    THA("THAILAND","TH","764"),
    TLS("TIMOR-LESTE","TL","626"),
    TGO("TOGO","TG","768"),
    TKL("TOKELAU","TK","772"),
    TON("TONGA","TO","776"),
    TTO("TRINIDAD AND TOBAGO","TT","780"),
    TUN("TUNISIA","TN","788"),
    TUR("TURKEY","TR","792"),
    TKM("TURKMENISTAN","TM","795"),
    TCA("TURKS AND CAICOS ISLANDS","TC","796"),
    TUV("TUVALU","TV","798"),
    UGA("UGANDA","UG","800"),
    UKR("UKRAINE","UA","804"),
    ARE("UNITED ARAB EMIRATES","AE","784"),
    GBR("UNITED KINGDOM","GB","826"),
    USA("UNITED STATES","US","840"),
    UMI("UNITED STATES MINOR OUTLYING ISLANDS","UM","581"),
    URY("URUGUAY","UY","858"),
    UZB("UZBEKISTAN","UZ","860"),
    VUT("VANUATU","VU","548"),
    VAT("VATICAN CITY STATE (HOLY SEE)","VA","336"),
    VEN("VENEZUELA","VE","862"),
    VNM("VIET NAM","VN","704"),
    VGB("VIRGIN ISLANDS (BRITISH)","VG","092"),
    VIR("VIRGIN ISLANDS (U.S.)","VI","850"),
    WLF("WALLIS AND FUTUNA ISLANDS","WF","876"),
    ESH("WESTERN SAHARA","EH","732"),
    YEM("YEMEN","YE","887"),
    ZMB("ZAMBIA","ZM","894"),
    ZWE("ZIMBABWE","ZW","716");

    private final String longName;
    private final String twoLetterCode;
    private final String numericCode;

    Country(String longName, String twoLetterCode, String numericCode) {
        this.longName=longName;
        this.twoLetterCode=twoLetterCode;
        this.numericCode=numericCode;
    }

    @Override
    public String getLongName() {
        return longName;
    }

    @Override
    public String getName() {
        return name();
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
     * in this case it will match either the Countries name, longName or two letter code.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static Country forName(String name) {
        for(Country c: values()) {
            if (c.longName().equalsIgnoreCase(name) || c.name().equalsIgnoreCase(name) || c.getTwoLetterCode().equalsIgnoreCase(name)) {
                return c;
            }
        }

        throw new IllegalArgumentException("'"+name+"' is not a valid value in enum "+Country.class.getName());
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    /**
     * Return the country's ISO two letter code
     * @return ISO two letter code
     */
    public String getTwoLetterCode() {
        return twoLetterCode;
    }

    /**
     * Return the country's ISO numeric code number
     * @return ISO code number
     */
    public String getNumericCode() {
        return numericCode;
    }
}
