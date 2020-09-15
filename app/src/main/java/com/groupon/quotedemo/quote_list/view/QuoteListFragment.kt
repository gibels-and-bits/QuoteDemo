package com.groupon.quotedemo.quote_list.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.brewin.mvicoroutines.presentation.common.provideMachine
import com.google.android.material.snackbar.Snackbar
import com.groupon.quotedemo.R
import com.groupon.quotedemo.databinding.FragmentQuoteListBinding
import com.groupon.quotedemo.login.domain.LoginEvent
import com.groupon.quotedemo.quote_list.domain.QuoteListEvent
import com.groupon.quotedemo.quote_list.domain.QuoteListMachine
import com.groupon.quotedemo.quote_list.domain.QuoteListRoute
import com.groupon.quotedemo.quote_list.domain.QuoteListState
import com.groupon.quotedemo.quote_list.domain.QuotesRepository
import com.groupon.quotedemo.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.material.dismissEvents

@ExperimentalCoroutinesApi
@FlowPreview
class QuoteListFragment : Fragment(R.layout.fragment_quote_list) {
    private val binding by viewBinding(FragmentQuoteListBinding::bind)
    private lateinit var machine: QuoteListMachine

    private val errorSnackbar by lazy {
        Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)
            .apply {
                dismissEvents()
                    .onEach { machine.events.send(QuoteListEvent.ErrorMessageDismissed) }
                    .launchIn(lifecycleScope)
            }
    }

    private val quoteListAdapter = QuoteListAdapter { quote ->
        flowOf(quote)
            .onEach {
                machine.events.send(QuoteListEvent.QuoteItemClicked(it.author, it.content))
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        machine = provideMachine {
            val initial = QuoteListState.default()
            QuoteListMachine(initial, QuotesRepository)
        }

        machine.states
            .onEach { it.render() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        machine.routes
            .onEach { it.goto() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        flowOf(QuoteListEvent.ScreenLoaded)
            .onEach {
                machine.events.send(it)
            }
            .launchIn(lifecycleScope)

        binding.setup()
    }

    private fun QuoteListState.render() {
        if (shouldShowError && !errorSnackbar.isShownOrQueued) {
            errorSnackbar.setText(errorMessage).show()
        }

        quoteListAdapter.submitList(quotes)
    }

    private fun QuoteListRoute.goto() {
        when(this){
            is QuoteListRoute.QuoteDetail -> findNavController().navigate(QuoteListFragmentDirections.actionQuoteListToDetail(author, quote))
        }
    }

    private fun FragmentQuoteListBinding.setup() {
        quoteList.adapter = quoteListAdapter
        quoteList.layoutManager = LinearLayoutManager(context)
    }
}
