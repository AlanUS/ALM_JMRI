package jmri.jmrit.beantable;

import jmri.util.JUnitUtil;
import org.junit.*;

/**
 *
 * @author Paul Bender Copyright (C) 2017	
 */
public class SignalMastLogicTableActionTest extends AbstractTableActionBase {

    @Test
    public void testCTor() {
        Assert.assertNotNull("exists",a);
    }

    @Override
    public String getTableFrameName(){
       return Bundle.getMessage("TitleSignalMastLogicTable");
    }

    /**
     * Check the return value of includeAddButton.  The table generated by 
     * this action includes an Add Button.
     */
    @Override
    @Test
    public void testIncludeAddButton(){
         Assert.assertTrue("Default include add button", a.includeAddButton());
    }

    @Override
    public String getAddFrameName(){
        return Bundle.getMessage("TitleAddSignalMastLogic");
    }

    @Test
    @Override
    @Ignore("no add button on signal mast logic table")
    public void testAddButton() {
    }

    @Test
    @Override
    @Ignore("no add button on signal mast logic table")
    public void testAddThroughDialog() {
    }

    // The minimal setup for log4J
    @Override
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        jmri.util.JUnitUtil.resetProfileManager();
        helpTarget = "package.jmri.jmrit.beantable.SignalMastLogicTable"; 
        a = new SignalMastLogicTableAction();
    }

    @Override
    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(SignalMastLogicTableActionTest.class);

}
