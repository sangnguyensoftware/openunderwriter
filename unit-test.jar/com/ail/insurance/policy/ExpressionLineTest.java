package com.ail.insurance.policy;

import static com.ail.financial.Currency.GBP;
import static com.ail.financial.Currency.USD;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.RefreshAssessmentSheetCache;

public class ExpressionLineTest {
    ExpressionLine sut;

    @Mock
    private RefreshAssessmentSheetCache assessmentSheetList;
    @Mock
    private AssessmentSheet assessmentSheet;
    @Mock
    private CalculationLine totalLine;
    @Mock
    private CalculationLine subTotalLine;
    @Mock
    private CalculationLine variableThreeLine;
    @Mock
    private CalculationLine variableFourLine;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new ExpressionLine();

        sut.setExpression("5+[total] + 15 - ([sub total] * 2) / [variable three]*[variable four]*2");

        doReturn(totalLine).when(assessmentSheetList).findAssessmentLine(eq("total"), eq(assessmentSheet));
        doReturn(subTotalLine).when(assessmentSheetList).findAssessmentLine(eq("sub total"), eq(assessmentSheet));
        doReturn(variableThreeLine).when(assessmentSheetList).findAssessmentLine(eq("variable three"), eq(assessmentSheet));
        doReturn(variableFourLine).when(assessmentSheetList).findAssessmentLine(eq("variable four"), eq(assessmentSheet));

        doReturn(new CurrencyAmount(0, GBP)).when(totalLine).getAmount();
        doReturn(new CurrencyAmount(0, GBP)).when(subTotalLine).getAmount();
        doReturn(new CurrencyAmount(0, GBP)).when(variableThreeLine).getAmount();
        doReturn(new CurrencyAmount(0, GBP)).when(variableFourLine).getAmount();
    }

    @Test
    public void testExtractionOfReferencedLineNames() {

        Set<String> vars = sut.referencedLineNames();

        assertTrue("vars.contains('total')", vars.contains("total"));
        assertTrue("vars.contains('sub total')", vars.contains("sub total"));
        assertTrue("vars.contains('variable three')", vars.contains("variable three"));
        assertTrue("vars.contains('variable four')", vars.contains("variable four"));

        assertThat(vars.size(), is(4));
    }

    @Test
    public void buildDereferencedExpressionReturnsTrueWhenAllLinesExist() {
        assertThat(sut.buildRawExpression(assessmentSheetList, assessmentSheet), is(notNullValue()));
    }

    @Test
    public void buildDereferencedExpressionReturnsFalseWhenAllLinesDoNotExist() {
        sut.setExpression("5+[total] + 15 + [non variable]");

        doReturn(totalLine).when(assessmentSheetList).findAssessmentLine(eq("total"), eq(assessmentSheet));

        doReturn(new CurrencyAmount(0, GBP)).when(totalLine).getAmount();

        assertThat(sut.buildRawExpression(assessmentSheetList, assessmentSheet), is((String)null));
    }

    @Test
    public void buildDereferencedExpressionReturnsFalseWhenReferencedLineHasNullAmount() {
        sut.setExpression("5+[total]");

        doReturn(totalLine).when(assessmentSheetList).findAssessmentLine(eq("total"), eq(assessmentSheet));

        doReturn(null).when(totalLine).getAmount();

        assertThat(sut.buildRawExpression(assessmentSheetList, assessmentSheet), is((String)null));
    }

    @Test
    public void buildDereferencedExpressionValuesCorrectly() {
        doReturn(new CurrencyAmount(2.5, GBP)).when(totalLine).getAmount();
        doReturn(new CurrencyAmount(4, GBP)).when(subTotalLine).getAmount();
        doReturn(new CurrencyAmount(6, GBP)).when(variableThreeLine).getAmount();
        doReturn(new CurrencyAmount(8, GBP)).when(variableFourLine).getAmount();

        String result = "5+2.50 + 15 - (4.00 * 2) / 6.00*8.00*2";

        assertThat(sut.buildRawExpression(assessmentSheetList, assessmentSheet), is(result));
    }

    @Test(expected=ExpressionLineError.class)
    public void allReferencedLinesMustBeTheSameCurrency() {
        sut.setExpression("5+[total] + 15 + [sub total]");

        doReturn(new CurrencyAmount(4, GBP)).when(totalLine).getAmount();
        doReturn(new CurrencyAmount(4, USD)).when(subTotalLine).getAmount();

        sut.buildRawExpression(assessmentSheetList, assessmentSheet);
    }

    @Test
    public void testEvaluate() {
        assertThat(sut.evaluateRawExpression("5+2.50").doubleValue(), is(7.5));
    }
}
