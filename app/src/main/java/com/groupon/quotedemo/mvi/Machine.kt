package com.groupon.quotedemo.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
abstract class Machine<EVENT, UPDATE, STATE, ROUTE>(initialState: STATE) : ViewModel() {

    private val _events = Channel<EVENT>(Channel.CONFLATED)
    val events: SendChannel<EVENT> = _events

    private val _states = ConflatedBroadcastChannel(initialState)
    val states = _states.asFlow()

    private val _routes = BroadcastChannel<ROUTE>(1)
    val routes = _routes.asFlow()

    val state get() = _states.value

    init {
        _events.consumeAsFlow()
            .flatMapConcat { it.toUpdateFlow() }
            .map { it.toState() }
            .onEach { _states.send(it) }
            .launchIn(viewModelScope)
    }

    protected inner class DefaultNav : Navigator<ROUTE>{
        override fun navigate(route: ROUTE) {
            viewModelScope.launch {
                _routes.send(route)
            }
        }
    }

    protected fun navigate(route: ROUTE){
        DefaultNav().navigate(route)
    }

    protected abstract fun EVENT.toUpdateFlow(): Flow<UPDATE>

    protected abstract fun UPDATE.toState(): STATE
}

interface Navigator<ROUTE> {
    fun navigate(route: ROUTE)
}

abstract class UseCase<UPDATE, ROUTE>(private val navigator: Navigator<ROUTE>? = null ) {
    abstract fun execute(): Flow<UPDATE>
    fun navigate(route: ROUTE){
        navigator?.navigate(route)
    }
}