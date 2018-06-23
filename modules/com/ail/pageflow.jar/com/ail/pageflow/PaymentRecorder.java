package com.ail.pageflow;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.JournalLineType.PREMIUM;
import static com.ail.financial.ledger.JournalType.PREMIUM_RECEIVED;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.Reference;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.ledger.Journal;
import com.ail.financial.ledger.Journal.JournalBuilder;
import com.ail.financial.ledger.JournalLine.JournalLineBuilder;
import com.ail.financial.ledger.Ledger;
import com.ail.financial.service.PostJournalService.PostJournalCommand;
import com.ail.insurance.policy.Policy;

public class PaymentRecorder extends PageElement {

    /** Select an account record the payment from */
    private String sourceBinding;
    /** Select an account record the payment to */
    private String targetBinding;

    public String getSourceBinding() {
        return sourceBinding;
    }

    public void setSourceBinding(String sourceBinding) {
        this.sourceBinding = sourceBinding;
    }

    public String getTargetBinding() {
        return targetBinding;
    }

    public void setTargetBinding(String targetBinding) {
        this.targetBinding = targetBinding;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PaymentRecorder", model);
    }

    @Override
    public Type applyRequestValues(Type model) {
        Type target = (Type)fetchBoundObject(model, model);

        if (!conditionIsMet(target)) {
            return model;
        }

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        return super.processValidations(model);
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (!conditionIsMet(model)) {
            return model;
        }

        RequestWrapper request = CoreContext.getRequestWrapper();

        CurrencyAmount amount = new CurrencyAmount(request.getParameter(amountId()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date paymentDate = null;
        try {
            paymentDate = dateFormat.parse(request.getParameter(paymentDateId()));
        } catch (ParseException e) {
            return model;
        }
        String description = request.getParameter(descriptionId());

        Type sourceModel = (Type)fetchBoundObject(sourceBinding, model);
        Type targetModel = (Type)fetchBoundObject(targetBinding, model);

        Ledger source = (Ledger)CoreContext.getCoreProxy().queryUnique("get.ledger.for.party.and.currency", sourceModel, GBP);
        Ledger target = (Ledger)CoreContext.getCoreProxy().queryUnique("get.ledger.for.party.and.currency", targetModel, GBP);

        Journal journal = new JournalBuilder().
                subject(new Reference(Policy.class, model.getExternalSystemId())).
                with(new JournalLineBuilder().debit().ofType(PREMIUM).ledger(source).by(amount).description(description).build()).
                with(new JournalLineBuilder().credit().ofType(PREMIUM).ledger(target).by(amount).description(description).build()).
                ofType(PREMIUM_RECEIVED).
                withTransactionDate(paymentDate).
                build();

        PostJournalCommand pjc = (PostJournalCommand) CoreContext.getCoreProxy().newCommand(PostJournalCommand.class);
        pjc.setJournalArgRet(journal);
        pjc.invoke();

        return model;
    }

    public String amountId() {
        return encodeId(getId()+"-amount");
    }

    public String paymentDateId() {
        return encodeId(getId()+"-paymentDate");
    }

    public String descriptionId() {
        return encodeId(getId()+"-description");
    }
}
