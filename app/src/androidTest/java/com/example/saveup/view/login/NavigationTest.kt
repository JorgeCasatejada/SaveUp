package com.example.saveup.view.login


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.saveup.R
import org.hamcrest.Matchers.allOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun navigationTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(5745)

        val textInputEditText = onView(
            allOf(
                withId(R.id.etEmail),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("espresso@gmail.com"), closeSoftKeyboard())

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.etPassword),
                isDisplayed()
            )
        )
        textInputEditText2.perform(replaceText("123456"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.btLogin), withText("Login"),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(7000)

        val bottomNavigationItemView = onView(
            allOf(
                withId(R.id.mnItmGroups),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.textViewTitleGroups),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Mis grupos")))

        val bottomNavigationItemView2 = onView(
            allOf(
                withId(R.id.mnItmBalance), withContentDescription("Balance"),
                isDisplayed()
            )
        )
        bottomNavigationItemView2.perform(click())

        val editText = onView(
            allOf(
                withId(R.id.etBalance),
                isDisplayed()
            )
        )
        editText.check(matches(withText("0.00")))

        val bottomNavigationItemView3 = onView(
            allOf(
                withId(R.id.mnItmStatistics),
                isDisplayed()
            )
        )
        bottomNavigationItemView3.perform(click())

        val frameLayout = onView(
            allOf(
                withId(R.id.mnItmGraphs), withContentDescription("Gráficos"),
                isDisplayed()
            )
        )
        frameLayout.check(matches(isDisplayed()))

        val frameLayout2 = onView(
            allOf(
                withId(R.id.mnItmLimits), withContentDescription("Límites & Metas"),
                isDisplayed()
            )
        )
        frameLayout2.check(matches(isDisplayed()))

        val bottomNavigationItemView4 = onView(
            allOf(
                withId(R.id.mnItmLimits),
                isDisplayed()
            )
        )
        bottomNavigationItemView4.perform(click())

        val textView3 = onView(
            allOf(
                withId(R.id.monthlyLimitsTitle),
                isDisplayed()
            )
        )
        textView3.check(matches(withText("Límites Mensuales")))

        val textView4 = onView(
            allOf(
                withId(R.id.savingGoalsTitle),
                isDisplayed()
            )
        )
        textView4.check(matches(withText("Metas de Ahorro")))

        val bottomNavigationItemView5 = onView(
            allOf(
                withId(R.id.mnItmProfile),
                isDisplayed()
            )
        )
        bottomNavigationItemView5.perform(click())

        val editText2 = onView(
            allOf(
                withId(R.id.etEmail),
                isDisplayed()
            )
        )
        editText2.check(matches(withText("espresso@gmail.com")))

        val materialButton2 = onView(
            allOf(
                withId(R.id.btCloseSession),
                isDisplayed()
            )
        )
        materialButton2.perform(click())
    }
}
