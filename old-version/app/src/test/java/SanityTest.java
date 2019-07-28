import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

public class SanityTest {
    @Test
    public void testBasicAddition() {
        int x = 1;
        assertThat(1+x, is(equalTo(2)));
    }
}