import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Just a working test.
 */
@Config(emulateSdk = 18)
@RunWith(RobolectricTestRunner.class)
public class SanityTest {
    @Test
    public void testAddition() {
        final int a = 1;
        int b = 1;
        Assert.assertEquals("Addition 1+1 != 2", 2, a + b);
    }
}