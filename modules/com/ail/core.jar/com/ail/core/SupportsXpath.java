package com.ail.core;

import java.util.Iterator;

import org.apache.commons.jxpath.JXPathContext;

public interface SupportsXpath {
    JXPathContext fetchJXPathContext();
    /**
     * Execute the given XPath expression on <i>this</i> and return the single result.
     * @param xpath Expression to evaluate
     * @exception TypeXPathException If evaluation of the expression fails.
     * @return Result of evaluation.
     */
    Object xpathGet(String xpath);

    /**
     * Execute the given XPath expression on <i>this</i> and return the single
     * result; or, if the xpath expression fails to evaluate for any reason,
     * return <code>alternative</code>
     * @param xpath Expression to evaluate
     * @param alternative Returned if xpath is null or fails to evaluate.
     * @return Result of evaluation; or, alternative.
     */
    Object xpathGet(String xpath, Object alternative);

    /**
     * Evaluate the given xpath expression on <i>this</i> and return the result as an instance
     * of the class <i>clazz</i>.
     * @param xpath Expression to evaluate
     * @param clazz Class to return an instance of
     * @return Result.
     */
    <T extends Object> T xpathGet(String xpath, Class<T> clazz);

    /**
     * Evaluate the given xpath expression on <i>this</i> and return the result as an instance
     * of the class <i>clazz</i>.
     * @param xpath Expression to evaluate
     * @param clazz Class to return an instance of
     * @param dictionaryLookup whether to check the dictionary for the xpath
     * @return Result.
     */
    <T extends Object> T xpathGet(String xpath, Class<T> clazz, boolean dictionaryLookup);

    /**
     * Evaluate the given xpath expression on <i>this</i> and return the result as an instance
     * of the class <i>clazz</i>.
     * @param xpath Expression to evaluate
     * @param alternative Returned if xpath is null or fails to evaluate.
     * @param clazz Class to return an instance of
     * @return Result.
     */
    <T extends Object> T xpathGet(String xpath, T alternative, Class<T> clazz);

    /**
     * Execute the given XPath expression on <i>this</i>. The expectation is that the XPath expression
     * evaluates to more than one node. This method returns the matching nodes as an iteration.
     * @param xpath Expression to evaluate
     * @exception TypeXPathException If evaluation of the expression fails.
     * @return Result of evaluation.
     */
    Iterator<? extends Object> xpathIterate(String xpath);

    /**
     * Execute the given xpath expression on <i>this</i>. The expectation is that the xpath expression
     * evaluates to more than one node. This method returns the matching nodes as an iteration.
     * @param xpath Expression to evaluate
     * @exception TypeXPathException If evaluation of the expression fails.
     * @return Result of evaluation.
     */
    <T extends Object> Iterator<T> xpathIterate(String xpath, Class<T> clazz);

    /**
     * Execute the given xpath expression on <i>this</i>. The expectation is that the xpath expression
     * evaluates to more than one node. This method returns the matching nodes as an iteration.
     * @param xpath Expression to evaluate
     * @param alternative Value to be returned if the evaluation of <code>xpath</code> fails.
     * @param clazz Return instance of this class in an {@link Iterator}
     * @return Result of evaluation.
     */
    <T extends Object> Iterator<T> xpathIterate(String xpath, Iterator<T> alternative, Class<T> clazz);

    /**
     * Set the value of a property within <i>this</i> identified by an xpath expression to a the value <i>obj</i>.
     * @param xpath Expression identifying the property to set.
     * @param obj value to set the property to.
     */
    void xpathSet(String xpath, Object obj);
}
