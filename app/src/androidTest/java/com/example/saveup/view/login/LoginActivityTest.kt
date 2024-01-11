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
class LoginActivityTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun loginActivityTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(5964)

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
                withId(R.id.mnItmProfile), withContentDescription("Perfil"),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val editText = onView(
            allOf(
                withId(R.id.etEmail),
                isDisplayed()
            )
        )
        editText.check(matches(withText("espresso@gmail.com")))

        val materialButton2 = onView(
            allOf(
                withId(R.id.btCloseSession), withText("Cerrar Sesión"),
                isDisplayed()
            )
        )
        materialButton2.perform(click())
    }
}
