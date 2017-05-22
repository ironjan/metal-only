import junit.framework.TestSuite;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.RunWith;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.runner.AndroidJUnitRunner;
import android.test.ActivityInstrumentationTestCase2;

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