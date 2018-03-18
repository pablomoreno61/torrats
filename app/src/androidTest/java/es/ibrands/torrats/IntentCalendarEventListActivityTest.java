package es.ibrands.torrats;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import es.ibrands.torrats.activity.CalendarDetailActivity;
import es.ibrands.torrats.activity.CalendarListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.not;

@RunWith(AndroidJUnit4.class)
public class IntentCalendarEventListActivityTest
{
    @Rule
    public IntentsTestRule<CalendarListActivity> mIntentsTestRule = new IntentsTestRule<>(
        CalendarListActivity.class
    );

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResources()
    {
        mIdlingResource = mIntentsTestRule.getActivity().getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    public void stubAllExternalIntents()
    {
        intending(not(isInternal())).respondWith(
            new Instrumentation.ActivityResult(Activity.RESULT_OK, null)
        );
    }

    @Test
    public void startRecipeDetailActivityIntent()
    {
        onView(withId(R.id.calendar_recycler_view)).perform(RecyclerViewActions.actionOnItem(
            hasDescendant(withText("Cheesecake")), click())
        );

        Context context = InstrumentationRegistry.getTargetContext();
        context.getResources().getBoolean(R.bool.is_tablet);
        Boolean isTabletUsed = context.getResources().getBoolean(R.bool.is_tablet);

        if (isTabletUsed) {
            onView(withId(R.id.video_player_tablet)).check(matches(isDisplayed()));
        } else {
            intended(hasComponent(CalendarDetailActivity.class.getName()));
        }
    }

    @After
    public void unregisterIdlingResource()
    {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
