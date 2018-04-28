package com.codingspezis.android.metalonly.player;

import android.content.ComponentName;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class StreamActivity_Navigation_Test {
    @Rule
    public ActivityTestRule<StreamControlActivity_> mActivityTestRule = new ActivityTestRule<>(StreamControlActivity_.class);

    @BeforeClass
    public static void initIntents(){
        Intents.init();
    }

    @AfterClass
    public static void releaseIntents(){
        Intents.release();
    }

    @Test
    public void aboutActivity_Navigation_Test() {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        ViewInteraction appCompatTextView = onView(
                allOf(withText(R.string.menu_info), isDisplayed()));
        appCompatTextView.perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), AboutActivity_.class)));
    }

    @Test
    public void wishActivity_Navigation_Test(){
        onView(allOf(withId(R.id.btnWish), isDisplayed()))
                .perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), WishActivity_.class)));
    }

    @Test
    public void favoritesActivity_Navigation_Test(){
        ViewInteraction actionMenuItemView = onView(
                allOf(withText(R.string.menu_favorites), isDisplayed()));
        actionMenuItemView.perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), FavoritesActivity_.class)));
    }

    @Test
    public void planActivity_Navigation_Test(){
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.btnCalendar), isDisplayed()));
        appCompatTextView.perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), PlanActivity_.class)));
    }

    @Test
    public void donationActivity_Navigation_Test(){
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());

        // TODO this test should work with withId(R.id.mnu_donation) but didn't
        ViewInteraction actionMenuItemView = onView(
                allOf(withText(R.string.menu_donation), isDisplayed()));
        actionMenuItemView.perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), PayPalDonationActivity_.class)));
    }

}
