package com.ail.core;

import org.codehaus.jackson.annotate.JsonIgnore;

public class RestfulServiceReturn {

    @JsonIgnore
    public static final int NO_CACHE = -1;
    @JsonIgnore
    public int returnStatus;

    private int cacheMaxAge = NO_CACHE;

    public RestfulServiceReturn(int status) {
        this.returnStatus = status;
    }

    public RestfulServiceReturn(int status, int cacheMaxAge) {
        this.returnStatus = status;
        this.cacheMaxAge = cacheMaxAge;
    }

    @JsonIgnore
    public int getCacheMaxAge() {
        if (returnStatus == 200) {
            return cacheMaxAge;
        }  else {
            return NO_CACHE;
        }
    }

}
