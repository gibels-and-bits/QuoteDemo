package com.groupon.quotedemo.quote_list.domain.use_cases

import com.groupon.quotedemo.mvi.UseCase
import com.groupon.quotedemo.quote_list.domain.QuoteListRoute
import com.groupon.quotedemo.quote_list.domain.QuoteListUpdate
import com.groupon.quotedemo.quote_list.domain.QuotesRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
@FlowPreview
class LoadQuoteDataUseCase(private val repository: QuotesRepository) : UseCase<QuoteListUpdate, QuoteListRoute>() {

    companion object {
        fun create(repository: QuotesRepository) = LoadQuoteDataUseCase(repository)
    }

    override fun execute(): Flow<QuoteListUpdate> {
        return flow{
            emit(QuoteListUpdate.Progress(true))
            val result = repository.getAllQuotes()
            emit(QuoteListUpdate.Progress(false))

            if(result.isError){
                emit(QuoteListUpdate.Error(result.errorMsg))
            } else {
                emit(QuoteListUpdate.ContentLoaded(result.quotes))
            }
        }
    }
}