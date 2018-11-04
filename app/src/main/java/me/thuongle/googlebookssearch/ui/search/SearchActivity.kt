package me.thuongle.googlebookssearch.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_search.*
import me.thuongle.googlebookssearch.R
import me.thuongle.googlebookssearch.databinding.ActivitySearchBinding
import me.thuongle.googlebookssearch.repository.BookRepository
import me.thuongle.googlebookssearch.ui.common.BookListAdapter
import me.thuongle.googlebookssearch.ui.common.Callback
import me.thuongle.googlebookssearch.util.AppExecutors

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private var adapter: BookListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        val viewModelFactory = ViewModelFactory(BookRepository.getInstance())
        searchViewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(SearchViewModel::class.java)

        initViews()
        initViewModel()
    }

    private fun initViews() {
        ed_query.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btn_search.isClickable = s != null && s.isNotEmpty()
            }
        })

        ed_query.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
        ed_query.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch()
                true
            } else {
                false
            }
        }
        btn_search.setOnClickListener { performSearch() }
        binding.retryCallback = object : Callback {
            override fun invoke() {
                performSearch()
            }
        }

        val bookListAdapter = BookListAdapter(appExecutors = AppExecutors())
        rv_book_list.adapter = bookListAdapter
        adapter = bookListAdapter
    }

    private fun initViewModel() {
        searchViewModel.searchResult.observe(this, Observer { result ->
            binding.result = result
        })
        searchViewModel.booksResult.observe(this, Observer { result ->
            adapter?.submitList(result)
        })
    }

    private fun performSearch() {
        val query = ed_query.text.toString()
        searchViewModel.searchBooks(query)
        binding.query = query
    }
}
