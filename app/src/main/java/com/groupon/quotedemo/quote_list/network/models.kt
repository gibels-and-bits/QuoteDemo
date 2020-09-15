package com.groupon.quotedemo.quote_list.network

import com.google.gson.annotations.SerializedName

data class Quote(
    val id: String,
    val author: String,
    @SerializedName("en") val content: String
)