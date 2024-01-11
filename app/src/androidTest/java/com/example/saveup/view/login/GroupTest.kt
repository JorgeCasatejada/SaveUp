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
class GroupTest {

    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun groupTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(5207)

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
                withId(R.id.mnItmGroups), withContentDescription("Grupos"),
                isDisplayed()
            )
        )
        bottomNavigationItemView.perform(click())

        val floatingActionButton = onView(
            allOf(
                withId(R.id.fabAddGroup), withContentDescription("addGroup"),
                isDisplayed()
            )
        )
        floatingActionButton.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val textInputEditText3 = onView(
            allOf(
                withId(R.id.etGroupTitle),
                isDisplayed()
            )
        )

        textInputEditText3.perform(replaceText("Grupo"), closeSoftKeyboard())

        val textInputEditText4 = onView(
            allOf(
                withId(R.id.etGroupBadget),
                isDisplayed()
            )
        )
        textInputEditText4.perform(replaceText("500"), closeSoftKeyboard())

        val textInputEditText5 = onView(
            allOf(
                withId(R.id.etDescription),
                isDisplayed()
            )
        )
        textInputEditText5.perform(replaceText("descripcion"), closeSoftKeyboard())

        val textInputEditText6 = onView(
            allOf(
                withId(R.id.etIdParticipant),
                isDisplayed()
            )
        )
        textInputEditText6.perform(replaceText("grabacion@gmail.com"), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.btAddParticipant), withText("Añadir"),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        val materialButton3 = onView(
            allOf(
                withId(R.id.btAdd), withText("Crear grupo"),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        Thread.sleep(700)

        val textView = onView(
            allOf(
                withId(R.id.titleGroup), withText("Grupo"),
                isDisplayed()
            )
        )
        textView.check(matches(withText("Grupo")))

        val bottomNavigationItemView2 = onView(
            allOf(
                withId(R.id.mnItmProfile), withContentDescription("Perfil"),
                isDisplayed()
            )
        )
        bottomNavigationItemView2.perform(click())

        val materialButton4 = onView(
            allOf(
                withId(R.id.btCloseSession), withText("Cerrar Sesión"),
                isDisplayed()
            )
        )
        materialButton4.perform(click())
    }
}
