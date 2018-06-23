package com.ail.insurance.payment;

import com.ail.financial.ledger.JournalLineType;
import com.ail.financial.ledger.JournalType;
import com.ail.party.Party;
/**
 * Represents the two sides of a journal entry, the debtor and the creditor.
 * The Debtor and Creditor can either be supplied as a Party, or as an xpath relative to the Policy.
 */
public class DebtorCreditorJournalEntry {

    private Party debtor;

    private String debtorPolicyXpath;

    private Party creditor;

    private String creditorPolicyXpath;

    private JournalType journalType;

    private JournalLineType journalLineType;

    public DebtorCreditorJournalEntry() {
    }

    public DebtorCreditorJournalEntry(Party debtor, Party creditor, JournalType journalType, JournalLineType journalLineType) {
        super();
        this.debtor = debtor;
        this.creditor = creditor;
        this.journalType = journalType;
        this.journalLineType = journalLineType;
    }

    public DebtorCreditorJournalEntry(String debtorPolicyXpath, Party creditor, JournalType journalType, JournalLineType journalLineType) {
        super();
        this.debtorPolicyXpath = debtorPolicyXpath;
        this.creditor = creditor;
        this.journalType = journalType;
        this.journalLineType = journalLineType;
    }

    public DebtorCreditorJournalEntry(Party debtor, String creditorPolicyXpath, JournalType journalType, JournalLineType journalLineType) {
        super();
        this.debtor = debtor;
        this.creditorPolicyXpath = creditorPolicyXpath;
        this.journalType = journalType;
        this.journalLineType = journalLineType;
    }

    public DebtorCreditorJournalEntry(String debtorPolicyXpath, String creditorPolicyXpath, JournalType journalType, JournalLineType journalLineType) {
        super();
        this.debtorPolicyXpath = debtorPolicyXpath;
        this.creditorPolicyXpath = creditorPolicyXpath;
        this.journalType = journalType;
        this.journalLineType = journalLineType;
    }

    public Party getDebtor() {
        return debtor;
    }

    public void setDebtor(Party debtor) {
        this.debtor = debtor;
    }

    public String getDebtorPolicyXpath() {
        return debtorPolicyXpath;
    }

    public void setDebtorPolicyXpath(String debtorPolicyXpath) {
        this.debtorPolicyXpath = debtorPolicyXpath;
    }

    public Party getCreditor() {
        return creditor;
    }

    public void setCreditor(Party creditor) {
        this.creditor = creditor;
    }

    public String getCreditorPolicyXpath() {
        return creditorPolicyXpath;
    }

    public void setCreditorPolicyXpath(String creditorPolicyXpath) {
        this.creditorPolicyXpath = creditorPolicyXpath;
    }

    public JournalType getJournalType() {
        return journalType;
    }

    public void setJournalType(JournalType journalType) {
        this.journalType = journalType;
    }

    public JournalLineType getJournalLineType() {
        return journalLineType;
    }

    public void setJournalLineType(JournalLineType journalLineType) {
        this.journalLineType = journalLineType;
    }

    @Override
    public String toString() {
        return "DebtorCreditorJournalEntry [debtor=" + debtor + ", debtorPolicyXpath=" + debtorPolicyXpath + ", creditor=" + creditor + ", creditorPolicyXpath=" + creditorPolicyXpath
                + ", journalType=" + journalType + ", journalLineType=" + journalLineType + "]";
    }

}
