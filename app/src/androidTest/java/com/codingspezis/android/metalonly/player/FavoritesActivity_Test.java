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
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FavoritesActivity_Test {

    @Rule
    public ActivityTestRule<FavoritesActivity_> mActivityTestRule = new ActivityTestRule<>(FavoritesActivity_.class);

    @Test
    public void favoritesActivity_Test() {
        ViewInteraction actionMenuItemView = onView(allOf(withId(R.id.mnu_add_manually), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction appCompatEditText = onView(withId(R.id.edit_artist));
        appCompatEditText.perform(scrollTo(), click());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.edit_artist));
        appCompatEditText2.perform(scrollTo(), replaceText("Blue Crown"), closeSoftKeyboard());

        ViewInteraction appCompatEditText7 = onView(withId(R.id.edit_title));
        appCompatEditText7.perform(scrollTo(), replaceText("Dunes"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txtArtist), withText("Blue Crown"),
                        childAtPosition(
                                allOf(withId(R.id.LinearLayout1),
                                        childAtPosition(
                                                withId(android.R.id.list),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Blue Crown")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txtTitle), withText("Dunes"),
                        childAtPosition(
                                allOf(withId(R.id.LinearLayout1),
                                        childAtPosition(
                                                withId(android.R.id.list),
                                                0)),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText("Dunes")));

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.LinearLayout1),
                        childAtPosition(
                                withId(android.R.id.list),
                                0),
                        isDisplayed()));
        relativeLayout.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.text1), withText("Wünschen"),
                        childAtPosition(
                                allOf(withClassName(is("com.android.internal.app.AlertController$RecycleListView")),
                                        withParent(withClassName(is("android.widget.LinearLayout")))),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        // Continue to check that wish activity loaded
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
