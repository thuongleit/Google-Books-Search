package me.thuongle.googlebookssearch.ui.search

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import me.thuongle.googlebookssearch.R
import me.thuongle.googlebookssearch.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySearchBinding>(this, R.layout.activity_search)
    }
}
