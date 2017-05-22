import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.runner.AndroidJUnit4;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class SanityAndroidTest {
    @Test
    public void testBasicAddition() {
        int x = 1;
        assertThat(1+x, is(equalTo(2)));
    }
}