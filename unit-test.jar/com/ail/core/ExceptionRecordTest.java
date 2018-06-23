package com.ail.core;

import static org.junit.Assert.*;

import org.junit.Test;

public class ExceptionRecordTest {
    @Test
    public void testStackTruncation() {
        // Create an exception and make sure it has more frames than ExceptionRecord will take.
        Exception e=new Exception();
        
        StackTraceElement[]  ste=new StackTraceElement[ExceptionRecord.MAX_FRAMES_TO_CAPTURE+10];
        for(int i=0 ; i<ste.length ; i++) {
            ste[i]=new StackTraceElement("dummy.Dummy", "method", "file", 1);
        }
        e.setStackTrace(ste);
        assertTrue(e.getStackTrace().length > ExceptionRecord.MAX_FRAMES_TO_CAPTURE);

        ExceptionRecord er=new ExceptionRecord(e);
        assertEquals(er.getStack().size()-1, ExceptionRecord.MAX_FRAMES_TO_CAPTURE);
    }
}
