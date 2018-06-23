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
package com.ail.insurance.policy;

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
import java.util.Random;

public class DataGenerator {
    
    
    private static final String APPLICATION = "APPLICATION";
    private static final String REFERRAL = "REFERRAL";
    private static final String QUOTE = "QUOTATION";
    private static final String RENEWAL = "RENEWAL";
    private static final String CANCEL = "CANCELLATION";
    private static final String CLAIM = "CLAIM";
    private static final String NEW = "NEW_BUSINESS";
    
    // Records will be generated between START_DATE and END_DATE
    private static final Long ONE_DAY = (long) (24 * 60 * 60 * 1000); // Milliseconds in a day
    private static final Long ONE_YEAR = (long) (365 * ONE_DAY); 
    private static final Long START_DATE = System.currentTimeMillis() - (1 * ONE_YEAR); // 1 years ago today
    private static final Long END_DATE = System.currentTimeMillis();
    
    private static final Long NUMBER_OF_RECORDS = 5000L;

    // Enum definitions
    private static final String[] BORDEREAU_REASONS =  {NEW, NEW, CLAIM, CANCEL, RENEWAL, RENEWAL, RENEWAL}; 
    private static final String[] SUMMARY_REASONS =  {NEW, CLAIM, CANCEL, RENEWAL, RENEWAL, QUOTE, QUOTE, QUOTE, QUOTE, QUOTE, QUOTE, QUOTE}; 
    private static final String[] UNCOMPLETED_REASONS =  {REFERRAL, APPLICATION, QUOTE, QUOTE}; 
    
    private static final String[] PAGE =  {"QuestionPage1", "QuestionPage2", "QuestionPage3", "Quotation", "Conditions", "Proposer", "History"};

    private static final String[] PRODUCTS = { "MotorPlus", "MotorPlus", "LifePlus", "WaterSports", "PetPlus" };
    private static final String[] BROKERS = { "Harvey Monceux Ltd", "Berkley Thames-Valley", "James Hewel & Co", "Shark, Graham & Timpson", "Salvador Rush" };
    private static final String[] FIRST_NAMES = { "John", "Tim", "Sarah", "Hillary", "Bill", "Samuel", "Daisy", "Harvey", "Jane", "Morris", "Claire" };
    private static final String[] LAST_NAMES = { "Johnson", "Baites", "Smith", "Jones", "Hall", "Robson", "Bryson", "Britten", "Hilliard", "Garvey", "Brompton", "Palmer", "Plant", "Selbey", "Dawson"};
        
    

    // Policy numbers will start from BASE_POLICY_NUMBER
    private static final double BASE_POLICY_NUMBER = 10000;

    private PolicyNumberGenerator policyNumberGenerator = new PolicyNumberGenerator();

    private Context context=null;
    
    private String selectFrom(String[] options) {
        return options[(int) (context.policyNumber % options.length)];
    }
        
    private String sqlDate(long date) {
        return new SimpleDateFormat("yy-MM-dd").format(new Date(date));
    }
    
    private void outputSummaryRecord() throws SQLException {
        
        String reason = selectFrom(SUMMARY_REASONS);
        int premium = getPremium(reason);
    
        String sql = "insert into report_summary ("+
                "broker,"+
                "quote_date,"+
                "accepted_date,"+
                "status,"+
                "premium_amount,"+
                "premium_currency,"+
                "product"+
            ")"+
            " values ("+
                "'"+selectFrom(BROKERS)+"', "+
                "'"+sqlDate(context.quoteDate)+"', "+
                "'"+sqlDate(context.inceptionDate)+"', "+
                "'"+reason.replace('_', ' ')+"', "+
                ""+premium+", "+
                "'GBP', "+
                "'"+selectFrom(PRODUCTS)+"'"+
            ");";
                
        executeSQL(sql);
    }

    private void outputBordereauRecord() throws SQLException {
        
        String reason = selectFrom(BORDEREAU_REASONS);
        int premium = getPremium(reason);
    
        String sql = "insert into bordereau ("+
                "broker,"+
                "quote_number,"+
                "policy_number,"+
                "quote_date,"+
                "accepted_date,"+
                "inception_date,"+
                "status,"+
                "policy_holder_name,"+
                "premium_amount,"+
                "premium_currency,"+
                "commission,"+
                "tax,"+
                "product"+
            ")"+
            " values ("+
                "'"+selectFrom(BROKERS)+"', "+
                "'QF"+context.policyNumber+"', "+
                "'POL"+context.policyNumber+"', "+
                "'"+sqlDate(context.quoteDate)+"', "+
                "'"+sqlDate(context.inceptionDate)+"', "+
                "'"+sqlDate(context.inceptionDate)+"', "+
                "'"+reason.replace('_', ' ')+"', "+
                "'"+selectFrom(FIRST_NAMES) + " " + selectFrom(LAST_NAMES) + "', "+
                ""+premium+", "+
                "'GBP', "+
                ""+((premium<0?0:premium*0.1))+", "+
                ""+((premium<0?0:premium*0.06))+", "+
                "'"+selectFrom(PRODUCTS)+"'"+
            ");";
                
        executeSQL(sql);
    }
    
private void outputUncompletedRecord() throws SQLException {
        
        String reason = selectFrom(UNCOMPLETED_REASONS);
        int premium = getPremium(reason);
        String first = selectFrom(FIRST_NAMES);
        String last = selectFrom(LAST_NAMES);
    
        String sql = "insert into pre_policy ("+
                "policy_system_id,"+
                "broker,"+
                "quote_number,"+
                "quote_date,"+
                "status,"+
                "page,"+
                "policy_holder_name,"+
                "policy_holder_number,"+
                "policy_holder_email,"+
                "premium_amount,"+
                "premium_currency,"+
                "product,"+
                "test"+
            ")"+
            " values ("+
                "0, "+
                "'"+selectFrom(BROKERS)+"', "+
                "'QF"+context.policyNumber+"', "+
                "'"+sqlDate(context.quoteDate)+"', "+
                "'"+reason.replace('_', ' ')+"', "+
                "'"+selectFrom(PAGE)+"', "+
                "'"+ first + " " + last + "', "+
                "'0" + new Random().nextInt(9999) + " " + new Random().nextInt(999999) + "', " +
                "'"+ first.toLowerCase() + "@" + last.toLowerCase() + ".com', "+
                ""+premium+", "+
                "'GBP', "+
                "'"+selectFrom(PRODUCTS)+"', "+
                "'false' "+
            ");";
                
        executeSQL(sql);
    }
    
    private int getPremium(String reason) {
        
        int premium = 0;
        
        if (!REFERRAL.equals(reason) && !APPLICATION.equals(reason)) {
            
            premium = (int)(300 + (context.policyNumber % 2000));
                    
            if (CLAIM.equals(reason) || CANCEL.equals(reason)) {
                premium = premium * -1;
            } 
        }
        return premium;
    }

    private void executeSQL(String sql) throws SQLException {
        PreparedStatement statement=context.connection.prepareStatement(sql);
        statement.execute();
        statement.close();
    }
    
    private void generateBordereau() throws SQLException {
        outputBordereauRecord();
    }
    
    private void generateSummary() throws SQLException {
        outputSummaryRecord();
    }
    
    private void generateUncompleted() throws SQLException {
        outputUncompletedRecord();
    }

    void setupContext() {
        context.policyNumber = policyNumberGenerator.next();
        context.quoteDate = (long) (START_DATE + (Math.random() * (END_DATE - START_DATE)));
        context.inceptionDate = (long) (context.quoteDate + ((Math.random() * 7) * ONE_DAY));
    }
    
    private void run(String args[]) throws SQLException {
        context=new Context(args);

        System.out.println("Generating dummy Bordereau, Summary and Uncompleted Quote data...");
        
        for (int i = 0; i < NUMBER_OF_RECORDS; i++) {
            setupContext();

            if (!context.quiet) {
                System.out.print("generating record "+(i+1)+" of "+NUMBER_OF_RECORDS+"\r");
            }
            generateBordereau();
            generateSummary();
            generateUncompleted();
            
        }
        
        if (!context.quiet) {
            System.out.println("Generation of dummy data complete.");
        }
    }

    public static void main(String[] args) {
        try {
            new DataGenerator().run(args);
        }
        catch(Throwable e) {
            e.printStackTrace(System.err);
            System.exit(1);
        }
    }

    /*
     * Generate policy numbers that are spread randomly over a range but never recur.
     */
    class PolicyNumberGenerator {
        private List<Long> usedPolicyNumbers = new ArrayList<Long>();

        public Long next() {
            long policyNumber;

            do {
                policyNumber = (long) (BASE_POLICY_NUMBER + (Math.random() * (NUMBER_OF_RECORDS*10)));
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
        private long quoteDate;
        private String dbDriver;
        private String dbUrl;
        private String dbUsername;
        private String dbPassword = "";;
        
        private Context(String[] args) {
            int arg=0;
            
            if ("-q".equals(args[arg])) {
                quiet=true;
                arg++;
            }
            
            dbDriver=args[arg++];
            
            dbUrl=args[arg++];
            
            dbUsername=args[arg++];
            
            if (args.length != arg) {
                dbPassword=args[arg++];
            }
            
            populateConnection();
            
            if (dbDriver==null || dbUrl==null || dbUsername==null) {
                System.err.println("Usage: DataGenerator [-q] <JDBC Driver Class Name> <JDBC Connection URL> <Database Username> [<Database Password>]");
                System.exit(1);
            }
        }
        
        private void populateConnection() {
            try {
                classForName(dbDriver);

                Properties properties = new Properties();
                properties.put("user", dbUsername);
                if (dbPassword!=null && dbPassword.length() > 0) {
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
