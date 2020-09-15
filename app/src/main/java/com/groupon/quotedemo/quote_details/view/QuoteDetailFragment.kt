package com.groupon.quotedemo.quote_details.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.github.brewin.mvicoroutines.presentation.common.provideMachine
import com.groupon.quotedemo.R
import com.groupon.quotedemo.databinding.FragmentQuoteDetailBinding
import com.groupon.quotedemo.databinding.FragmentQuoteListBinding
import com.groupon.quotedemo.quote_list.domain.QuoteListMachine
import com.groupon.quotedemo.quote_list.domain.QuoteListRoute
import com.groupon.quotedemo.quote_list.domain.QuoteListState
import com.groupon.quotedemo.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class QuoteDetailFragment : Fragment(R.layout.fragment_quote_detail) {
    private val binding by viewBinding(FragmentQuoteDetailBinding::bind)
    private val args: QuoteDetailFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.setup()
    }

    private fun FragmentQuoteDetailBinding.setup() {
        author.text = args.authorName
        quote.text = args.quote
    }
}
