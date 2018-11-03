package me.thuongle.googlebookssearch.ui.search

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import kotlinx.android.synthetic.main.activity_search.*
import me.thuongle.googlebookssearch.R
import me.thuongle.googlebookssearch.databinding.ActivitySearchBinding

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivitySearchBinding>(this, R.layout.activity_search)
        setupViews()
    }

    private fun setupViews() {
        ed_query.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn_search.isClickable = s != null && s.isNotEmpty()
            }
        })
    }
}
