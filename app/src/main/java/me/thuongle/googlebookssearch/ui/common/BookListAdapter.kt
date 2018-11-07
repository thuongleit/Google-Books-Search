package me.thuongle.googlebookssearch.ui.common

import android.databinding.DataBindingUtil
import android.support.v7.util.DiffUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import me.thuongle.googlebookssearch.R
import me.thuongle.googlebookssearch.api.GoogleBook
import me.thuongle.googlebookssearch.databinding.RowBookItemBinding
import me.thuongle.googlebookssearch.util.AppExecutors

/**
 * A RecyclerView adapter for [GoogleBook] class.
 */
class BookListAdapter(
    appExecutors: AppExecutors
) : DataBoundListAdapter<GoogleBook, RowBookItemBinding>(
    appExecutors = appExecutors,
    diffCallback = object : DiffUtil.ItemCallback<GoogleBook>() {
        override fun areItemsTheSame(oldItem: GoogleBook, newItem: GoogleBook): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: GoogleBook, newItem: GoogleBook): Boolean {
            return oldItem == newItem
        }
    }
) {

    override fun createBinding(parent: ViewGroup): RowBookItemBinding {
        val binding = DataBindingUtil.inflate<RowBookItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_book_item,
            parent,
            false,
            null
        )
        return binding
    }

    override fun bind(binding: RowBookItemBinding, item: GoogleBook) {
        binding.item = item
    }
}