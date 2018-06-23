/**
 * 
 */
package org.apache.velocity;

import java.io.StringWriter;

import org.apache.velocity.app.Velocity;
import org.junit.Test;

/**
 * @author mark
 * 
 */
public class PolymorphArgumentTest {

    @Test
    public void testIncluded() {
        Velocity.setProperty("runtime.references.strict", true);
        Velocity.init();

        VelocityContext context = new VelocityContext();

        /*
         * Task 1. create ShapeFunctions class an interface (Shape) and and
         * extension class (Triangle): update the vm to call a method on
         * ShapeFunctions's driver's name that calls a method on the interface;
         * create instance of ShapeFunctions and Triangle in test case; run
         * test.
         */
        context.put("shapeFunctions", new ShapeFunctions());
        context.put("shape", new Triangle());

        Template template = null;

        template = Velocity.getTemplate("target/test/integration-test.jar/org/apache/velocity/templateInclude.vm");

        StringWriter sw = new StringWriter();

        template.merge(context, sw);

        System.out.println(sw);
    }
}
