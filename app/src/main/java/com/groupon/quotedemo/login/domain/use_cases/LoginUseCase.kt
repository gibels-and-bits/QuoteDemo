package com.groupon.quotedemo.login.domain.use_cases

import com.groupon.quotedemo.login.domain.LoginRoute
import com.groupon.quotedemo.login.network.LoginService
import com.groupon.quotedemo.login.domain.LoginUpdate
import com.groupon.quotedemo.mvi.Navigator
import com.groupon.quotedemo.mvi.UseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@ExperimentalCoroutinesApi
@FlowPreview
class LoginUseCase(private val service: LoginService, navigator: Navigator<LoginRoute>) : UseCase<LoginUpdate, LoginRoute>(navigator) {
    private lateinit var name: String
    private lateinit var pass: String

    companion object {
        fun create(name : String, password: String, navigator: Navigator<LoginRoute>) =
            LoginUseCase(LoginService.instance, navigator).apply {
                this.name = name
                this.pass = password
            }
    }

    override fun execute(): Flow<LoginUpdate> {
        return flow{
            emit(LoginUpdate.Progress(true))
            val result = service.login(name, pass)
            emit(LoginUpdate.Progress(false))

            when(result.code){
                LoginService.HttpCode.CODE_200 -> navigate(LoginRoute.QuoteList)
                LoginService.HttpCode.CODE_403 -> emit(LoginUpdate.Error("Invalid Credentials"))
            }
        }
    }
}