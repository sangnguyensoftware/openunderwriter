package com.ail.core.logging;

import com.ail.core.Core;
import com.ail.core.CoreProxy;

/**
 * A custom security manager that exposes the getClassContext() information so
 * that we can derive the current stack. This is considerably quicker than using
 * the alternative method of generating an exception and querying it for the 
 * stack. See this post: 
 * http://stackoverflow.com/questions/421280/in-java-how-do-i-find-the-caller-of-a-method-using-stacktrace-or-reflection
 */
class IdentifyCallingClass extends SecurityManager {
    IdentifyCallingClass() {
    }

    Class<?>[] getStack() {
        return getClassContext();
    }
    
    Class<?> callingClass() {
        boolean takeNext = false;

        for (Class<?> clazz : getStack()) {
            if (takeNext && !clazz.equals(CoreProxy.class) && !clazz.equals(Core.class)) {
                return clazz;
            }
            
            if (clazz.equals(Core.class)) {
                takeNext = true;
            }
        }
        
        throw new IllegalStateException("Could not determin invoking class for logging.");
    }
}
