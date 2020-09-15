package com.groupon.quotedemo.login.network

import kotlinx.coroutines.delay

class LoginService {

    enum class HttpCode{
        CODE_200,
        CODE_403
    }
    data class Result(val code: HttpCode)

    companion object {
       val instance by lazy {
           LoginService()
       }
    }

    suspend fun login(username: String, password: String) : Result {
        delay(5000)
        return if(username.isNotEmpty() && password.isNotEmpty()){
            Result(HttpCode.CODE_200)
        } else {
            Result(HttpCode.CODE_403)
        }
    }
}