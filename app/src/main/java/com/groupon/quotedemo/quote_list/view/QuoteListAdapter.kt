package com.groupon.quotedemo.quote_list.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.groupon.quotedemo.databinding.QuoteListItemBinding
import com.groupon.quotedemo.quote_list.network.Quote

class QuoteListAdapter(
    private val onClick: (item: Quote) -> Unit = {}
) : ListAdapter<Quote, QuoteListAdapter.ViewHolder>(ItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(QuoteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: QuoteListItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Quote) {
            binding.root.setOnClickListener { onClick(item) }
            binding.authorName.text = item.author
        }
    }

    private object ItemCallback : DiffUtil.ItemCallback<Quote>() {

        override fun areItemsTheSame(oldItem: Quote, newItem: Quote): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Quote, newItem: Quote): Boolean =
            oldItem.author == newItem.author

        override fun getChangePayload(oldItem: Quote, newItem: Quote): Any? =
            if (oldItem.id == newItem.id) null
            else Unit // Dummy value to prevent item change animation.
    }
}