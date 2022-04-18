package com.artworkspace.storyapp.ui.main

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.artworkspace.storyapp.R
import com.artworkspace.storyapp.ui.detail.DetailStoryActivity
import com.artworkspace.storyapp.ui.detail.DetailStoryActivity.Companion.EXTRA_DETAIL
import com.artworkspace.storyapp.ui.splash.SplashActivity
import com.artworkspace.storyapp.utils.EspressoIdlingResource
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Testing in MainActivityTest
 * Initial condition: Must already logged in
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val activity = ActivityScenarioRule(SplashActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        Intents.release()
    }


    @Test
    fun loadStoryDetailInformation() {
        intended(hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_stories)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                0,
                click()
            )
        )

        intended(hasComponent(DetailStoryActivity::class.java.name))
        intended(hasExtraWithKey(EXTRA_DETAIL))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_story_username)).check(matches(isDisplayed()))
        onView(withId(R.id.tv_story_date)).check(matches(isDisplayed()))
        onView(withId(R.id.iv_story_image)).check(matches(isDisplayed()))
        onView(withId(R.id.scroll_view)).perform(swipeUp())
        onView(withId(R.id.tv_story_description)).check(matches(isDisplayed()))

        pressBack()

    }
}