package jmri.util.usb;


import org.junit.Assert;
import org.junit.jupiter.api.*;

/**
 * Tests for the Bundle class
 *
 * @author Bob Jacobsen Copyright (C) 2012
 */
public class BundleTest  {

    @Test public void testGoodKeysMessage() {
        Assert.assertEquals("Help", Bundle.getMessage("ButtonHelp"));
        Assert.assertEquals("Turnout", Bundle.getMessage("BeanNameTurnout"));
    }

    @Test
    public void testBadKeyMessage() {
        Assert.assertThrows(java.util.MissingResourceException.class, () -> Bundle.getMessage("FFFFFTTTTTTT"));
    }

    @Test public void testGoodKeysMessageArg() {
        Assert.assertEquals("Help", Bundle.getMessage("ButtonHelp", "foo"));
        Assert.assertEquals("Turnout", Bundle.getMessage("BeanNameTurnout", "foo"));
        Assert.assertEquals("About Test", Bundle.getMessage("TitleAbout", "Test"));
    }

    @Test
    public void testBadKeyMessageArg() {
        Assert.assertThrows(java.util.MissingResourceException.class, () -> Bundle.getMessage("FFFFFTTTTTTT", new Object[]{}));
    }

}
