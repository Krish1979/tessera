
package com.quorum.tessera.data.migration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;


public class MainTest {
    @Rule
  public final ExpectedSystemExit expectedSystemExit = ExpectedSystemExit.none();
    
    @Test
    public void doStuff() throws Exception {
        expectedSystemExit.expectSystemExitWithStatus(0);
        Main.main("help");
    }
    
    @Test
    public void doStuffAndBreak() throws Exception {
        expectedSystemExit.expectSystemExitWithStatus(1);
        Main.main();
    }
    
}
