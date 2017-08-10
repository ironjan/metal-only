package com.codingspezis.android.metalonly.player;


import android.content.ComponentName;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FavoritesActivity_Test {
    public static final String BLUE_CROWN = "Blue Crown";
    public static final String DUNES = "Dunes";

    @Rule
    public ActivityTestRule<FavoritesActivity_> testRule = new ActivityTestRule<>(FavoritesActivity_.class);

    /**
     * Creates a new favorite and prepares a wish with it.
     */
    @Test
    @Ignore("Does not work on travis")
    public void favoritesActivity_Test() {
        Intents.init();

        ViewInteraction actionMenuItemView = onView(allOf(withId(R.id.mnu_add_manually), isDisplayed()));
        actionMenuItemView.perform(click());

        onView(withId(R.id.edit_artist))
                .perform(scrollTo(), click());

        onView(withId(R.id.edit_artist))
                .perform(scrollTo(), replaceText("Blue Crown"), closeSoftKeyboard());

        onView(withId(R.id.edit_title))
                .perform(scrollTo(), replaceText("Dunes"), closeSoftKeyboard());

        onView(allOf(withId(android.R.id.button1), withText("OK"), isDisplayed()))
                .perform(click());

        onData(anything()).inAdapterView(withId(android.R.id.list)).atPosition(0).perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.txtArtist), withText(BLUE_CROWN),
                        childAtPosition(
                                allOf(withId(R.id.LinearLayout1),
                                        childAtPosition(
                                                withId(android.R.id.list),
                                                0)),
                                0),
                        isDisplayed()));
        textView.check(matches(withText("Blue Crown")));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.txtTitle), withText(DUNES),
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


        onView(
                allOf(withId(android.R.id.text1), withText("Wünschen"),
                        isDisplayed()))
                .perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), WishActivity_.class)));

        Intents.release();
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
