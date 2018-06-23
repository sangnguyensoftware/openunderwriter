package com.ail.insurance.quotation;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.Currency.USD;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.XMLException;
import com.ail.core.XMLString;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.ExpressionLine;
import com.ail.insurance.policy.FixedSum;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.RefreshAssessmentSheetsService.RefreshAssessmentSheetsArgument;

public class RefreshAssessmentSheetsServiceTest {
    private static final String DUMMY_ORIGIN = "DUMMY_ORIGIN";

    RefreshAssessmentSheetsService sut;

    @Mock
    RefreshAssessmentSheetsArgument args;
    @Mock
    Policy policy;

    AssessmentSheet sheet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new RefreshAssessmentSheetsService();
        sheet = new AssessmentSheet();

        sut.setArgs(args);

        doReturn(policy).when(args).getPolicyArgRet();
        doReturn(DUMMY_ORIGIN).when(args).getOriginArg();

        doReturn(false).when(policy).isAggregator();
        doReturn(sheet).when(policy).getAssessmentSheetFor(eq(AssessmentSheet.DEFAULT_SHEET_NAME));

        sheet.setLockingActor("test");
    }

    @Test
    public void testSimpleExpressionLine() throws PreconditionException {
        sheet.addFixedSum("line 1", "nothing", new CurrencyAmount(10, GBP));
        sheet.addFixedSum("line 2", "nothing", new CurrencyAmount(2, GBP));

        sheet.addExpressionLine("result", "nothing", "[line 1] + [line 2]");

        sut.invoke();

        ExpressionLine line=(ExpressionLine) sheet.findLineById("result");

        assertThat(line.getAmount(), is(new CurrencyAmount(12, GBP)));
    }

    @Test
    public void testMathExpressionLine() throws PreconditionException {
        sheet.addFixedSum("line 1", "nothing", new CurrencyAmount(10, GBP));
        sheet.addFixedSum("line 2", "nothing", new CurrencyAmount(2, GBP));

        sheet.addExpressionLine("result", "nothing", "Math.min([line 1] + [line 2], 5)");

        sut.invoke();

        ExpressionLine line=(ExpressionLine) sheet.findLineById("result");

        assertThat(line.getAmount(), is(new CurrencyAmount(5, GBP)));
    }

    @Test
    public void testConstraintLine() throws IOException, XMLException, PreconditionException {
        XMLString asXML = new XMLString(this.getClass().getResourceAsStream("AssessmentSheet.xml"));
        AssessmentSheet sheet = new CoreProxy().fromXML(AssessmentSheet.class, asXML);

        doReturn(sheet).when(policy).getAssessmentSheetFor(eq(AssessmentSheet.DEFAULT_SHEET_NAME));

        sheet.setLockingActor("CalculatePremium");

        sut.invoke();

        FixedSum totalPremium = sheet.findLineById("total premium", FixedSum.class);
        assertThat(totalPremium.getAmount(), is(new CurrencyAmount(500, USD)));
    }
}
