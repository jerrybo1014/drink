package app.jerry.drink

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//@RunWith(AndroidJUnit4::class)
//@LargeTest
//class DrinkTest {
//
//    @get:Rule
//    var activityRule: ActivityTestRule<MainActivity>
//            = ActivityTestRule(MainActivity::class.java)
//
//    @Test
//    fun test(){
//        Thread.sleep(5000)
//        onView(withId(R.id.home_recycler_high_score)).perform(
//            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
//        Thread.sleep(5000)
//    }
//
//}