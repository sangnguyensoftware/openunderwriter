/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Reference;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.quotation.RefreshAssessmentSheetCache;

/**
 * An expression line defines a mathematical expression that will be evaluated to generate a result and,
 * optionally, contribute that result to the value of another line. Expressions may refer to the values
 * of other assessment sheet lines using the notation [line name]. Java's built-in JavaScript engine
 * is used to evaluate the expressions allowing all of JavaScript's functions and operations to be used.
 */
@TypeDefinition
public class ExpressionLine extends CalculationLine {
    private static final long serialVersionUID = 1048699218660294623L;
    private static final String TOKEN_END = "]";
    private static final String TOKEN_START = "[";
    private static final Pattern pattern = Pattern.compile("\\[([^\\]]*)\\]");

    private transient Set<String> referencedLineNames;
    private transient Currency computedCurrency;

    private String expression=null;

    public ExpressionLine() {
    }

    /**
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param expression A comma separated list of the IDs of the lines that this one depends on (will sum).
     */
    public ExpressionLine(String id, String reason, Reference relatesTo, String contributesTo, String expression) {
        super(id, reason, relatesTo, contributesTo, null);
        this.expression=expression;
    }

    /**
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param dependsOn A comma separated list of the IDs of the lines that this one depends on (will sum).
     * @param priority The priority of this line wrt other lines in the same sheet (lines with higher priority values are processed first)
     */
    public ExpressionLine(String id, String reason, Reference relatesTo, String contributesTo, String expression, int priority) {
      super(id, reason, relatesTo, contributesTo, null, priority);
      this.expression=expression;
    }

    /**
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param dependsOn A comma separated list of the IDs of the lines that this one depends on (will sum).
     */
    public ExpressionLine(String id, String reason, String expression) {
        super(id, reason, null, null, null);
        this.expression=expression;
    }

    /**
     * The expression to be evaluated. The expression must evaluate to a
     * numerical amount. All of the JavaScript operators defined <a href=
     * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Expressions_and_Operators">
     * here</a> are available, as are the mathematical functions described
     * <a href=
     * "https://developer.mozilla.org/en-US/docs/Web/JavaScript/Guide/Numbers_and_dates#Math_object">
     * here</a>.
     *
     * @return Expression to be evaluated by this line.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @see #getExpression()
     * @param expression Expression to be evaluated by this line
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    private void setComputedCurrency(Currency currency) {
        computedCurrency = currency;
    }

    private Currency getComputedCurrency() {
        return computedCurrency;
    }

    @Override
    public boolean calculate(RefreshAssessmentSheetCache sheets, AssessmentSheet sheet) {
        String rawExpression = buildRawExpression(sheets, sheet);

        if (rawExpression == null) {
            return false;
        }

        // set the amount base on the result of evaluation
        setAmount(new CurrencyAmount(evaluateRawExpression(rawExpression), getComputedCurrency()));

        // mark the line with an order index so that the order lines were processed in is known
        setProcessedOrder(sheet.getNextProcessOrderIndex());

        // if this line contributes to another...
        if (getContributesTo() != null) {
            // try to get the line that this on contributes to.
            CalculationLine conTo = (CalculationLine) sheets.findAssessmentLine(getContributesTo(), sheet);

            // if it doesn't exist yet, create it.
            if (conTo == null) {
                conTo = new FixedSum(getContributesTo(), "calculated", null, null, new CurrencyAmount(getAmount()));
                sheets.addAssessmentLine(conTo, sheet);
                conTo.setAssessmentSheet(sheet);
            }
            else {
                conTo.setAmount(conTo.getAmount().add(getAmount()));
            }
        }

        return true;
    }


    BigDecimal evaluateRawExpression(String rawExpression) {
        try {
            ScriptEngine engine = new ScriptEngineManager(null).getEngineByName("nashorn");
            engine.eval("result=" + rawExpression);
            return new BigDecimal((Double)engine.get("result"));

        } catch (ScriptException e) {
            throw new ExpressionLineError("Evaluation of the expression line (id="+getId()+") failed. Raw expression is: '"+rawExpression+"'", e);
        }
    }

    /**
     * Expand all line value references in the lines expression replacing the
     * references with the values of the referenced lines. If any referenced line
     * cannot be found, null is returned.
     * @param sheets All sheets in the policy (used to find referenced lines).
     * @param current Sheet "owning" this line (used to find referenced lines).
     * @return raw, expanded, expression; or null if a referenced line cannot be found.
     */
    String buildRawExpression(RefreshAssessmentSheetCache sheets, AssessmentSheet current) {
        StringBuffer expanded = new StringBuffer(expression);
        CalculationLine cl = null;
        Currency currency = null;

        // loop through all the lines that our expression refers to.
        for(String lineName: referencedLineNames() ) {
            cl=(CalculationLine)sheets.findAssessmentLine(lineName, current);

            if (cl == null || cl.getAmount() == null) {
                return null;
            }

            if (currency == null) {
                currency = cl.getAmount().getCurrency();
            }
            else {
                if (!currency.equals(cl.getAmount().getCurrency())) {
                    throw new ExpressionLineError("Expression line (id='" + getId() + "') referes to lines with mismatching currencies");
                }
            }

            String token = TOKEN_START+lineName+TOKEN_END;

            for( int start = expanded.indexOf(token) ; start != -1 ; start = expanded.indexOf(token)) {
                int end = start + token.length();
                expanded.replace(start, end, cl.getAmount().getAmount().toString());
            };
        }

        setComputedCurrency(currency);

        return expanded.toString();
    }

    Set<String> referencedLineNames() {
        if (referencedLineNames==null) {
            referencedLineNames = new HashSet<>();
            for(Matcher m = pattern.matcher(expression) ; m.find() ; referencedLineNames.add(m.group(1)));

        }
        return referencedLineNames;
    }
}
