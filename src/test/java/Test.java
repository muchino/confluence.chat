/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import confluence.chat.actions.ChatMessageParser;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import junit.framework.TestCase;

/**
 *
 * @author Dev
 */
public class Test extends TestCase {

    public Test(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // TODO add test methods here. The name must begin with 'test'. For example:

    public void testHello() {
        ChatMessageParser parser = new ChatMessageParser();
        
        System.out.println(parser.parseString(":-)"));
    }
}
