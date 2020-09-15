package com.groupon.quotedemo.quote_list.domain

import com.groupon.quotedemo.quote_list.network.Quote
import com.groupon.quotedemo.quote_list.network.QuoteService
import com.groupon.quotedemo.quote_list.network.QuotesApi

object QuotesRepository {
    private val service: QuotesApi = QuoteService.get()

    data class Result(
        val isError: Boolean = false,
        val errorMsg: String = "",
        val quotes: List<Quote> = emptyList()
    )

    suspend fun getAllQuotes() =
        try {
            val allQuotes = service.quotes()
            Result(quotes = allQuotes)
        } catch (e: Exception) {
            Result(isError = true, errorMsg = e.localizedMessage.orEmpty())
        }
}