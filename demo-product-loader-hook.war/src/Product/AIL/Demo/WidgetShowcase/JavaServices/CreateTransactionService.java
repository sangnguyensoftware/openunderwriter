
/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.JournalLineType.PREMIUM;
import static com.ail.financial.ledger.JournalType.PREMIUM_RECEIVED;
import static com.ail.pageflow.PageFlowContext.getPolicy;

import java.util.Date;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Reference;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.Journal.JournalBuilder;
import com.ail.financial.ledger.JournalLine.JournalLineBuilder;
import com.ail.financial.ledger.Ledger;
import com.ail.financial.service.PostJournalService.PostJournalCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class CreateTransactionService {
    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        
        CurrencyAmount amount = determineAmount();
        Date date = determineDate();
        String description = determineDescription();
        Ledger source = determineSourceLedger();
        Ledger target = determineTargetLedger();
        
        Journal journal = new JournalBuilder().
                    subject(new Reference(Policy.class, getPolicy().getExternalSystemId())).
                    with(new JournalLineBuilder().debit().ofType(PREMIUM).ledger(source).by(amount).description(description).build()).
                    with(new JournalLineBuilder().credit().ofType(PREMIUM).ledger(target).by(amount).description(description).build()).
                    ofType(PREMIUM_RECEIVED).
                    withTransactionDate(date).
                    build();
        
        PostJournalCommand pjc = (PostJournalCommand) CoreContext.getCoreProxy().newCommand(PostJournalCommand.class);
        pjc.setJournalArgRet(journal);
        pjc.invoke();
    }

    private static String determineDescription() {
       return (String)getPolicy().xpathGet("asset[id='accountDetails']/attribute[id='description']/value");
    }

    private static Date determineDate() {
        return (Date)getPolicy().xpathGet("asset[id='accountDetails']/attribute[id='date']/object");
    }

    private static Ledger determineSourceLedger() {
        String direction = (String) getPolicy().xpathGet("asset[id='accountDetails']/attribute[id='direction']/value");
        
        if ("Client to Broker".equals(direction)) {
            return (Ledger)CoreContext.getCoreProxy().queryUnique("get.ledger.for.party.and.currency", getPolicy().getClient(), GBP);
        }
        else { 
            return (Ledger)CoreContext.getCoreProxy().queryUnique("get.ledger.for.party.and.currency", getPolicy().getBroker(), GBP);
        }
    }

    private static Ledger determineTargetLedger() {
        String direction = (String) getPolicy().xpathGet("asset[id='accountDetails']/attribute[id='direction']/value");
        
        if ("Client to Broker".equals(direction)) {
            return (Ledger)CoreContext.getCoreProxy().queryUnique("get.ledger.for.party.and.currency", getPolicy().getBroker(), GBP);
        }
        else { 
            return (Ledger)CoreContext.getCoreProxy().queryUnique("get.ledger.for.party.and.currency", getPolicy().getClient(), GBP);
        }
    }

    private static CurrencyAmount determineAmount() {
        return new CurrencyAmount(
                (String)getPolicy().xpathGet("asset[id='accountDetails']/attribute[id='amount']/value"),
                (String)getPolicy().xpathGet("asset[id='accountDetails']/attribute[id='amount']/unit")
        );
    }
}