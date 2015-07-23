import junit.framework.*;

import org.junit.*;
import org.junit.runner.*;
import org.robolectric.*;
import org.robolectric.annotation.*;

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