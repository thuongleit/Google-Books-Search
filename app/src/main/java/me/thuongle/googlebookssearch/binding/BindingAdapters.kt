package me.thuongle.googlebookssearch.binding

import android.databinding.BindingAdapter
import android.view.View

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("visible")
    fun setVisible(view: View, show: Boolean) {
        view.visibility = if (show) View.VISIBLE else View.GONE
    }

    @JvmStatic
    @BindingAdapter("invisible")
    fun setInvisible(view: View, invisible: Boolean) {
        view.visibility = if (invisible) View.INVISIBLE else View.VISIBLE
    }
}
