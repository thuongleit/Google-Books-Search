package me.thuongle.googlebookssearch.ui.search

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.annotation.VisibleForTesting
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_search.*
import me.thuongle.googlebookssearch.BuildConfig
import me.thuongle.googlebookssearch.R
import me.thuongle.googlebookssearch.api.BookService
import me.thuongle.googlebookssearch.api.BookServiceImpl
import me.thuongle.googlebookssearch.databinding.ActivitySearchBinding
import me.thuongle.googlebookssearch.repository.BookRepository
import me.thuongle.googlebookssearch.testing.OpenForTesting
import me.thuongle.googlebookssearch.ui.common.BookListAdapter
import me.thuongle.googlebookssearch.ui.common.Callback
import me.thuongle.googlebookssearch.util.AppExecutors
import me.thuongle.googlebookssearch.util.dismissKeyboard
import me.thuongle.googlebookssearch.util.isConnected
import java.io.IOException

@OpenForTesting
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var searchViewModel: SearchViewModel
    private var adapter: BookListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)

        val bookService = BookServiceImpl.create(BuildConfig.NETWORK_EXECUTOR_TYPE)
        searchViewModel = getViewModel(bookService)

        initViews()
        initViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.findItem(R.id.swap_service)?.let { swapMenu ->
            val currentNetworkType = searchViewModel.repository.getService().getType()
            swapMenu.title =
                    getString(R.string.swap_service_hint, BookService.NetworkExecutorType.swap(currentNetworkType))
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.swap_service -> {
                val newType =
                    BookService.NetworkExecutorType.swap(searchViewModel.repository.getService().getType())
                searchViewModel.repository.swapService(BookServiceImpl.create(newType))
                Toast.makeText(this, getString(R.string.swap_service_hint, newType), Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getViewModel(bookService: BookServiceImpl): SearchViewModel {
        val viewModelFactory = ViewModelFactory(BookRepository.getInstance(bookService, AppExecutors.create()))
        return ViewModelProviders.of(this, viewModelFactory)
            .get(SearchViewModel::class.java)
    }

    @VisibleForTesting
    internal fun setViewModel(viewModel: SearchViewModel) {
        searchViewModel = viewModel
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

        ed_query.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(v)
                true
            } else {
                false
            }
        }
        ed_query.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                performSearch(v)
                true
            } else {
                false
            }
        }
        btn_search.setOnClickListener { performSearch(ed_query) }
        btn_search.isClickable = false
        binding.retryCallback = object : Callback {
            override fun invoke() {
                searchViewModel.retryQuery()
            }
        }

        val bookListAdapter = BookListAdapter(appExecutors = AppExecutors())
        rv_book_list.adapter = bookListAdapter
        adapter = bookListAdapter
    }

    private fun initViewModel() {
        searchViewModel.searchResult.observe(this, Observer { result ->
            result?.let {
                if (it.isError
                    && it.status?.error is IOException
                    && !this@SearchActivity.isConnected()
                ) {
                    binding.customErrorMsg = getString(R.string.no_internet_connection_msg)
                }
            }
            binding.result = result
            adapter?.submitList(result?.data)
        })
    }

    private fun performSearch(receiver: View) {
        dismissKeyboard(receiver.windowToken)
        val query = ed_query.text.toString()
        searchViewModel.searchBooks(query)
        binding.networkType = searchViewModel.repository.getService().getType().toString()
        binding.query = query
    }
}
