
/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.AccountType.CASH;
import static com.ail.pageflow.PageFlowContext.getPolicy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.financial.service.CreateAccountService.CreateAccountCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class CreateAccountService {
    public static void invoke(ExecutePageActionArgument args) throws BaseException, ParseException {
        Date openingDate = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000");
        CreateAccountCommand cac = (CreateAccountCommand) CoreContext.getCoreProxy().newCommand(CreateAccountCommand.class);

        cac.setPartyArg(getPolicy().getClient());
        cac.setCurrencyArg(GBP);
        cac.setTypeArg(CASH);
        cac.setOpeningDateArg(openingDate);
        cac.invoke();

        cac.setPartyArg(getPolicy().getBroker());
        cac.setCurrencyArg(GBP);
        cac.setTypeArg(CASH);
        cac.setOpeningDateArg(openingDate);
        cac.invoke();
    }
}