package com.groupon.quotedemo.quote_list.network

import retrofit2.http.GET

interface QuotesApi{

    @GET("quotes/lang/en")
    suspend fun quotes() : List<Quote>

}