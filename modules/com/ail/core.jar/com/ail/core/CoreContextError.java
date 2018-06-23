package com.ail.core;


/**
 * This error is thrown in response to the PageFlowContext encountering a configuration issue 
 * from which it cannot recover. The situation is such that even redirecting to the error 
 * page is not possible.
 */
public class CoreContextError extends BaseError {

    public CoreContextError(String description, Throwable target) {
        super(description, target);
    }

    public CoreContextError(String description) {
        super(description);
    }

    public CoreContextError(BaseException e) {
        super(e);
    }

}
