package com.groupon.quotedemo.login.domain

import com.groupon.quotedemo.login.domain.use_cases.LoginUseCase
import com.groupon.quotedemo.mvi.Machine

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flowOf

sealed class LoginEvent {
    data class LoginClick(val name: String, val password: String) : LoginEvent()
    object ErrorMessageDismissed : LoginEvent()
}

sealed class LoginUpdate {
    data class Progress(val isInProgress: Boolean) : LoginUpdate()
    data class Error(val errorMessage: String) : LoginUpdate()
    object HideError : LoginUpdate()
}

sealed class LoginRoute {
    object QuoteList : LoginRoute()
}

data class LoginState(
    val isInProgress: Boolean,
    val errorMessage: String,
    val shouldShowError: Boolean
)  {

    companion object {
        fun default() = LoginState(
            isInProgress = false,
            errorMessage = "",
            shouldShowError = false
        )
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
class LoginMachine(
    initialState: LoginState
) : Machine<LoginEvent, LoginUpdate, LoginState, LoginRoute>(initialState) {

    override fun LoginEvent.toUpdateFlow() = when (this) {
        is LoginEvent.LoginClick -> LoginUseCase.create(name, password, DefaultNav()).execute()
        LoginEvent.ErrorMessageDismissed -> flowOf(LoginUpdate.HideError)
    }

    override fun LoginUpdate.toState() = when (this) {
        is LoginUpdate.Progress -> state.copy(
            isInProgress = isInProgress
        )
        is LoginUpdate.Error -> state.copy(
            errorMessage = errorMessage,
            shouldShowError = true
        )
        LoginUpdate.HideError -> state.copy(
            errorMessage = "",
            shouldShowError = false
        )
    }
}