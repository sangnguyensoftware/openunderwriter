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
package com.ail.insurance.actuarial;

import static com.ail.core.Functions.classForName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class DataGenerator {
    private enum Reasons {
        NEW_BUSINESS, CLAIM, ENDORSEMENT, CANCELLATION, RENEWAL
    };

    // Records will be generated between START_DATE and END_DATE
    private static final Long ONE_DAY = (long) (24 * 60 * 60 * 1000); // Milliseconds
                                                                      // in a
                                                                      // day
    private static final Long ONE_YEAR = (long) (365 * ONE_DAY);
    private static final Long START_DATE = System.currentTimeMillis() - (10 * ONE_YEAR); // 10
                                                                                         // years
                                                                                         // ago
                                                                                         // today
    private static final Long END_DATE = System.currentTimeMillis();

    private static final Long NUMBER_OF_RECORDS = 1000L;

    // Enum definitions
    private static final String[] COVER_TYPES = { "COMPREHENSIVE", "THIRD PARTY FIRE AND THEFT", "THIRD PARTY ONLY" };
    private static final String[] INSURED_TYPES = { "CORPORATE", "PERSONAL" };
    private static final String[] INSURANCE_RATES = { "17.5", "15", "12.5", "10" };
    private static final String[] BODY_TYPES = { "4X4", "CONVERTIBLE", "COUPE", "ESTATE", "HATCHBACK", "SALOON", "SPORTS" };
    private static final String[] PARKING_TYPES = { "GARAGE", "OFF ROAD", "ON ROAD" };
    private static final String[] RISK_CODES = { "AMBULANCE / HEARSE-Z405", "ART/TANKER Z301", "CLASS II", "CLASS III", "GWI- CLASS I", "HIRING CARS-Z405", "MAXI BUS-Z405", "MINI BUS-Z405",
            "TAXI-Z405", "X.1", "X.4", "Y.3", "Z.300 ABOVE 3000 CC", "Z.300 BELOW 3000 CC", "Z.301 ABOVE 3000 CC", "Z.301 BELOW 3000 CC", "Z.802 ROAD", "Z.802 SITE" };
    private static final String[] COMMERCIAL_VEHICLE = { "true", "false", "false", "false", "false" };
    private static final String[] AIRBAG = { "true", "false", "false", "false", "false" };
    private static final String[] ENGINE_SIZE = { "900", "1000", "1100", "1300", "1500", "1800", "2000", "2200", "2500", "2600", "3000", "3500", "4000", "4500", "5000" };
    private static final String[] CLAIM_TYPES = { "DAMAGE TO VEHICLE BY FIRE", "ECOWAS BROWN CARD", "MOTOR FIRE AND THEFT", "MOTOR OWN DAMAGE", "THIRD PARTY INJURY", "THIRD PARTY PROPERTY DAMAGE" };

    private static final double RENEWAL_CHANCE = 0.25;
    private static final double CLAIM_CHANCE = 0.75;

    // Policy numbers will start from BASE_POLICY_NUMBER
    private static final double BASE_POLICY_NUMBER = 10000;
    private static final long MINIMUM_MILLAGE = 2000;

    private PolicyNumberGenerator policyNumberGenerator = new PolicyNumberGenerator();

    private Context context = null;

    private String selectFrom(String[] options) {
        return options[(int) (context.policyNumber % options.length)];
    }

    /**
     * Select a random reason and return it. A "reason" is any that modifies a
     * policy - in effect all operations except NEW_BUSINESS are delta
     * operations.
     */
    private Reasons selectReason() {
        int opIndex = (int) (Reasons.values().length * Math.random());
        Reasons operation = Reasons.values()[opIndex];
        return (Reasons.NEW_BUSINESS.equals(operation)) ? selectReason() : operation;
    }

    private String sqlDate(long date) {
        return new SimpleDateFormat("yy-MM-dd").format(new Date(date));
    }

    private String sqlYear(long date) {
        return new SimpleDateFormat("yyyy").format(new Date(date));
    }

    private void outputPolicy() throws SQLException {
        executeSQL("update polPolicy set polReplaced='" + sqlDate(context.eventDate) + "' where polReplaced is null and polExternalSystemID='" + context.policyNumber + "';");

        executeSQL("insert into polPolicy (" + "polCoverTypeIDcov," + "polInsuredTypeIDins," + "polReasonIDrea," + "polExternalSystemID," + "polInception," + "polExpiry," + "polLeadInsurer,"
                + "polTax," + "polUWYear," + "polGrossPremiumAmount," + "polGrossPremiumIDccy" + ")" + "values (" + "'"
                + selectFrom(COVER_TYPES)
                + "', "
                + "'"
                + selectFrom(INSURED_TYPES)
                + "', "
                + "'"
                + context.reason.toString().replace('_', ' ')
                + "', "
                + "'"
                + context.policyNumber
                + "', "
                + "'"
                + sqlDate(context.inceptionDate)
                + "', "
                + "'"
                + sqlDate(context.expiryDate)
                + "', "
                + "true, "
                + ""
                + selectFrom(INSURANCE_RATES)
                + ", "
                + ""
                + sqlYear(context.inceptionDate)
                + ", "
                + "" + (int) (300 + (context.policyNumber % 2000)) + ", " + "'GBP'" + ");");
    }

    private void outputVehicle() throws SQLException {
        long yearOfManufacture;
        yearOfManufacture = Long.parseLong(sqlYear(context.firstRecordDate));
        yearOfManufacture = yearOfManufacture - (context.policyNumber % 20);

        long annualMileage = MINIMUM_MILLAGE + (context.policyNumber % 30000);
        long mileage = (Long.parseLong(sqlYear(context.eventDate)) - yearOfManufacture) * annualMileage;

        long sumInsured = 700 + (context.policyNumber % 48000);

        executeSQL("insert into vehVehicle (" + "vehPolicyIDpol, " + "vehBodyIDbod, " + "vehParkedIDpar, " + "vehRiskCodeIDrsk, " + "vehYearOfManufacture, " + "vehCommercialVehicle, "
                + "vehDriverAirBagFitted, " + "vehEngineSizeCC, " + "vehMilage, " + "vehSumInsuredLocalAmount, " + "vehSumInsuredLocalIDccy " + ")" + "values ("
                + "(select polID from polPolicy where polExternalSystemID="
                + context.policyNumber
                + " and polReplaced is null),"
                + "'"
                + selectFrom(BODY_TYPES)
                + "',"
                + "'"
                + selectFrom(PARKING_TYPES)
                + "',"
                + "'"
                + selectFrom(RISK_CODES)
                + "',"
                + ""
                + yearOfManufacture
                + ","
                + ""
                + selectFrom(COMMERCIAL_VEHICLE)
                + ","
                + ""
                + selectFrom(AIRBAG)
                + ","
                + ""
                + selectFrom(ENGINE_SIZE)
                + ","
                + ""
                + mileage
                + ","
                + ""
                + sumInsured
                + ","
                + "'GBP'" + ");");
    }

    private void executeSQL(String sql) throws SQLException {
        PreparedStatement statement = context.connection.prepareStatement(sql);
        statement.execute();
        statement.close();
    }

    private void outputClaim() throws SQLException {
        String incidentOccured = sqlDate(context.eventDate - (ONE_DAY * (context.policyNumber % 30)));
        double claimAmountSelector = Math.random();
        long claimMax = 0;

        if (claimAmountSelector < .65) {
            claimMax = 2000;
        } else if (claimAmountSelector > .65 && claimAmountSelector < .99) {
            claimMax = 10000;
        } else if (claimAmountSelector > .99) {
            claimMax = 100000;
        }

        executeSQL("insert into clmClaim (" + " clmPolicyIDpol," + " clmVehicleIDveh," + " clmClaimTypeIDcla," + " clmAmountPaidAmount," + " clmAmountPaidIDccy," + " clmIncidentOccured" + " )"
                + " values (" + " (select polID from polPolicy where polExternalSystemID='" + context.policyNumber + "' and polReplaced is null),"
                + " (select vehID from vehVehicle join polPolicy on vehPolicyIDpol=polID where polExternalSystemID='" + context.policyNumber + "' and polReplaced is null)," + " '"
                + selectFrom(CLAIM_TYPES) + "'," + " " + (int) (200 + (Math.random() * claimMax)) + "," + " 'GBP'," + " '" + incidentOccured + "'" + " );");
    }

    private void outputPolicyRecord() throws SQLException {
        if (context.eventDate < END_DATE && context.eventDate < context.expiryDate) {
            outputPolicy();
            outputVehicle();
        }
    }

    private void outputClaimRecord() throws SQLException {
        if (context.eventDate < END_DATE && context.eventDate < context.expiryDate) {
            outputClaim();
            outputPolicy();
            outputVehicle();
        }
    }

    private void generateRenewal() throws SQLException {
        context.inceptionDate = context.expiryDate + ONE_DAY;
        context.expiryDate = context.expiryDate + ONE_YEAR;
        context.eventDate = context.inceptionDate;
        outputPolicyRecord();
    }

    private void generateEndorsement() throws SQLException {
        context.eventDate = (long) (context.eventDate + ((Math.random() * 31) * ONE_DAY));
        outputPolicyRecord();
    }

    private void generateCalcellation() throws SQLException {
        context.eventDate = (long) (context.eventDate + ((Math.random() * 31) * ONE_DAY));
        outputPolicyRecord();
    }

    private void generateClaim() throws SQLException {
        context.eventDate = (long) (context.eventDate + ((Math.random() * 31) * ONE_DAY));
        outputClaimRecord();
        outputPolicyRecord();
    }

    private void generateNewBusiness() throws SQLException {
        context.reason = Reasons.NEW_BUSINESS;
        outputPolicyRecord();
    }

    void setupContext() {
        context.policyNumber = policyNumberGenerator.next();
        context.inceptionDate = (long) (START_DATE + (Math.random() * (END_DATE - START_DATE)));
        context.expiryDate = context.inceptionDate + ONE_YEAR;
        context.reason = selectReason();
        context.firstRecordDate = context.inceptionDate;
        context.eventDate = context.inceptionDate;
    }

    private void run(String args[]) throws SQLException {
        context = new Context(args);

        System.out.println("Generating dummy Actuarial data...");

        for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
            boolean done = false;

            setupContext();

            if (!context.quiet) {
                System.out.print("generating record " + (i + 1) + " of " + NUMBER_OF_RECORDS + "\r");
            }

            if (context.inceptionDate > START_DATE + ONE_YEAR) {
                generateNewBusiness();
            }

            while (!done) {
                context.reason = selectReason();
                switch (context.reason) {
                case CLAIM:
                    if (Math.random() > CLAIM_CHANCE) {
                        generateClaim();
                    }
                    break;
                case CANCELLATION:
                    generateCalcellation();
                    done = true;
                    break;
                case ENDORSEMENT:
                    generateEndorsement();
                    break;
                case RENEWAL:
                    if (Math.random() > RENEWAL_CHANCE) {
                        generateRenewal();
                    } else {
                        done = true;
                    }
                    break;
                }
            }
        }

        if (!context.quiet) {
            System.out.println("Generation of dummy Actuarial data complete.");
        }
    }

    public static void main(String[] args) {
        try {
            new DataGenerator().run(args);
        } catch (Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /*
     * Generate policy numbers that are spread randomly over a range but never
     * recur.
     */
    class PolicyNumberGenerator {
        private List<Long> usedPolicyNumbers = new ArrayList<Long>();

        public Long next() {
            long policyNumber;

            do {
                policyNumber = (long) (BASE_POLICY_NUMBER + (Math.random() * (NUMBER_OF_RECORDS * 10)));
            } while (usedPolicyNumbers.contains(policyNumber));

            usedPolicyNumbers.add(policyNumber);

            return policyNumber;
        }
    }

    private class Context {
        private boolean quiet = false;
        private Connection connection;
        private long policyNumber;
        private long inceptionDate;
        private long expiryDate;
        private long firstRecordDate;
        private long eventDate;
        private Reasons reason;
        private String dbDriver;
        private String dbUrl;
        private String dbUsername;
        private String dbPassword;

        private Context(String[] args) {
            int arg = 0;
            
            if ("-q".equals(args[arg])) {
                quiet = true;
                arg++;
            }
            
            dbDriver = args[arg++];
            
            dbUrl = args[arg++];
            
            dbUsername = args[arg++];
            
            if (args.length != arg) {
                dbPassword=args[arg++];
            }
                        
            populateConnection();

            if (dbDriver == null || dbUrl == null || dbUsername == null) {
                System.err.println("Usage: DataGenerator [-q] <JDBC Driver Class Name> <JDBC Connection URL> <Database Username> [<Database Password>]");
                System.exit(1);
            }
        }

        private void populateConnection() {
            try {
                classForName(dbDriver);

                Properties properties = new Properties();
                properties.put("user", dbUsername);
                if (dbPassword != null && dbPassword.length() > 0) {
                    properties.put("password", dbPassword);
                }

                connection = DriverManager.getConnection(dbUrl, properties);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException("JDBC Driver Class (" + dbDriver + ") not found.");
            } catch (SQLException e) {
                throw new IllegalArgumentException("Database access error (driver:" + dbDriver + ", url:" + dbUrl + ") " + e);
            }
        }
    }
}
