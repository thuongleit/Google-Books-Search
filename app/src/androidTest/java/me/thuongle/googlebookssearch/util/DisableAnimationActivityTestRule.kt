package me.thuongle.googlebookssearch.util

import android.app.Activity
import android.app.Application
import android.databinding.ViewDataBinding
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.rule.ActivityTestRule
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import org.junit.Assert.fail


class DisableAnimationActivityTestRule<A : Activity>(
    activityClass: Class<A>
) : ActivityTestRule<A>(activityClass) {

    override fun beforeActivityLaunched() {
        val application = getInstrumentation().targetContext.applicationContext as Application
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityPaused(activity: Activity?) {
            }

            override fun onActivityResumed(activity: Activity?) {
            }

            override fun onActivityStarted(activity: Activity?) {
                // Disables progress bar animations for the views of the given activity
                traverseViews(activity!!.findViewById(android.R.id.content))
            }

            override fun onActivityDestroyed(activity: Activity?) {
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
            }

            override fun onActivityStopped(activity: Activity?) {
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
            }
        })

        // need to disable USE_CHOREOGRAPHER when perform espresso tests with data binding
        // see more: https://issuetracker.google.com/issues/37116383
        try {
            setFinalStatic(
                ViewDataBinding::class.java.getDeclaredField("USE_CHOREOGRAPHER"), false
            )
        } catch (e: Exception) {
            fail(e.message)
        }

        super.beforeActivityLaunched()
    }
}

fun traverseViews(view: View?) {
    if (view is ViewGroup) {
        traverseViewGroup(view)
    } else if (view is ProgressBar) {
        disableProgressBarAnimation(view)
    }
}

private fun traverseViewGroup(view: ViewGroup) {
    val count = view.childCount
    (0 until count).forEach {
        traverseViews(view.getChildAt(it))
    }
}

/**
 * necessary to run tests on older API levels where progress bar uses handler loop to animate.
 *
 * @param progressBar The progress bar whose animation will be swapped with a drawable
 */
private fun disableProgressBarAnimation(progressBar: ProgressBar) {
    progressBar.indeterminateDrawable = ColorDrawable(Color.BLUE)
}