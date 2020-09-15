package com.groupon.quotedemo.quote_list.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object QuoteService {
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://programming-quotes-api.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun get()  = retrofit.create(QuotesApi::class.java)
}
