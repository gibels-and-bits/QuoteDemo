package com.groupon.quotedemo.login.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.brewin.mvicoroutines.presentation.common.contents
import com.github.brewin.mvicoroutines.presentation.common.provideMachine
import com.google.android.material.snackbar.Snackbar
import com.groupon.quotedemo.R
import com.groupon.quotedemo.databinding.FragmentLoginBinding
import com.groupon.quotedemo.login.domain.LoginEvent
import com.groupon.quotedemo.login.domain.LoginMachine
import com.groupon.quotedemo.login.domain.LoginRoute
import com.groupon.quotedemo.login.domain.LoginState
import com.groupon.quotedemo.quote_list.view.QuoteListFragmentDirections
import com.groupon.quotedemo.viewBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks
import reactivecircus.flowbinding.material.dismissEvents

@FlowPreview
@ExperimentalCoroutinesApi
class LoginFragment : Fragment(R.layout.fragment_login) {

    private val binding by viewBinding(FragmentLoginBinding::bind)
    private lateinit var machine: LoginMachine

    private val errorSnackbar by lazy {
        Snackbar.make(requireView(), "", Snackbar.LENGTH_LONG)
            .apply {
                dismissEvents()
                    .onEach { machine.events.send(LoginEvent.ErrorMessageDismissed) }
                    .launchIn(viewLifecycleOwner.lifecycleScope)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        machine = provideMachine {
            val initial = LoginState.default()
            LoginMachine(initial)
        }

        machine.states
            .onEach { it.render() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        machine.routes
            .onEach { it.goto() }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        binding.setup()
    }

    private fun LoginRoute.goto(){
        findNavController().navigate(LoginFragmentDirections.actionLoginToQuoteList())
    }

    private fun LoginState.render(){

        if(isInProgress){
            binding.loading.visibility = View.VISIBLE
        } else {
            binding.loading.visibility = View.INVISIBLE
        }

        if (shouldShowError && !errorSnackbar.isShownOrQueued) {
            errorSnackbar.setText(errorMessage).show()
        }
    }

    private fun FragmentLoginBinding.setup() {
        login
            .clicks()
            .onEach {
                machine.events.send(
                    LoginEvent.LoginClick(username.contents(), password.contents())
                )
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }
}
