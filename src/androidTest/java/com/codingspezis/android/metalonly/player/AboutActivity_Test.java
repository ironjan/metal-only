package com.codingspezis.android.metalonly.player;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class AboutActivity_Test {

    @Rule
    public ActivityTestRule<AboutActivity_> mActivityTestRule = new ActivityTestRule<>(AboutActivity_.class);

    @Test
    public void aboutActivity_Test() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.textAppName), withText("Metal Only"),
                        childAtPosition(
                                allOf(withId(R.id.RelativeLayout1),
                                        childAtPosition(
                                                withId(R.id.fragmentAbout),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Metal Only")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.textAppVersion), withText("0.6.3 (60300)"),
                        childAtPosition(
                                allOf(withId(R.id.RelativeLayout1),
                                        childAtPosition(
                                                withId(R.id.fragmentAbout),
                                                0)),
                                1),
                        isDisplayed()));
        String expectedVersionInformation = BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")";
        textView2.check(matches(withText(expectedVersionInformation)));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
