package com.groupon.quotedemo.quote_list.domain

import com.groupon.quotedemo.mvi.Machine
import com.groupon.quotedemo.quote_list.domain.use_cases.LoadQuoteDataUseCase
import com.groupon.quotedemo.quote_list.network.Quote
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach

sealed class QuoteListEvent {
    object ScreenLoaded : QuoteListEvent()
    data class QuoteItemClicked(val author: String, val quote: String) : QuoteListEvent()
    object ErrorMessageDismissed : QuoteListEvent()
}

sealed class QuoteListUpdate {
    data class Progress(val isInProgress: Boolean) : QuoteListUpdate()
    data class Error(val errorMessage: String) : QuoteListUpdate()
    data class ContentLoaded(val quotes: List<Quote>) : QuoteListUpdate()
    object HideError : QuoteListUpdate()
    object NoChange : QuoteListUpdate()
}

sealed class QuoteListRoute {
    data class QuoteDetail(val author: String, val quote: String) : QuoteListRoute()
}

data class QuoteListState(
    val isLoading: Boolean,
    val quotes: List<Quote>,
    val errorMessage: String,
    val shouldShowError: Boolean
) {

    companion object {
        fun default() = QuoteListState(
            isLoading = false,
            quotes = emptyList(),
            errorMessage = "",
            shouldShowError = false
        )
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
class QuoteListMachine(
    initialState: QuoteListState,
    private val repository: QuotesRepository
) : Machine<QuoteListEvent, QuoteListUpdate, QuoteListState, QuoteListRoute>(initialState) {

    override fun QuoteListEvent.toUpdateFlow(): Flow<QuoteListUpdate> =
        when (this) {
            QuoteListEvent.ScreenLoaded -> LoadQuoteDataUseCase.create(repository).execute()
            is QuoteListEvent.QuoteItemClicked -> flowOf(QuoteListUpdate.NoChange).onEach {
                navigate(QuoteListRoute.QuoteDetail(author, quote))
            }
            is QuoteListEvent.ErrorMessageDismissed -> flowOf(QuoteListUpdate.HideError)
        }

    override fun QuoteListUpdate.toState(): QuoteListState =
        when (this) {
            is QuoteListUpdate.Progress -> state.copy(isLoading = this.isInProgress)
            is QuoteListUpdate.Error -> state.copy(shouldShowError = true, errorMessage = this.errorMessage)
            is QuoteListUpdate.ContentLoaded -> state.copy(quotes = this.quotes)
            QuoteListUpdate.HideError -> state.copy(shouldShowError = false)
            else -> state
        }
}