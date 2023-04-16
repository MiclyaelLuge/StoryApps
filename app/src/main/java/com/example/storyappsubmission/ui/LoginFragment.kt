package com.example.storyappsubmission.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.storyappsubmission.data.remote.result.Result
import androidx.navigation.findNavController
import com.example.storyappsubmission.R
import com.example.storyappsubmission.ViewModel.LoginViewModel
import com.example.storyappsubmission.ViewModel.LoginViewModelFactory
import com.example.storyappsubmission.ViewModel.TokenViewModel
import com.example.storyappsubmission.ViewModel.TokenViewModelFactory
import com.example.storyappsubmission.customView.MyEmail
import com.example.storyappsubmission.customView.MyPassword
import com.example.storyappsubmission.databinding.FragmentLoginBinding
import com.example.storyappsubmission.utils.LoginPreferences

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "token")

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var pref: LoginPreferences
    private lateinit var tokenViewModel: TokenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        pref = LoginPreferences.getInstance(requireContext().dataStore)
        tokenViewModel =
            ViewModelProvider(this, TokenViewModelFactory(pref))[TokenViewModel::class.java]
        tokenViewModel.getToken().observe(this) {
            if (it != "null") {
                Token = it
                view?.findNavController()
                    ?.navigate(R.id.action_loginFragment2_to_storyListFragment)
            }

        }

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.hide()
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val factory: LoginViewModelFactory = LoginViewModelFactory.getInstance(requireContext())
        val viewModel: LoginViewModel by viewModels {
            factory
        }
        binding.registerButton.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment2_to_registerFragment)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.myLoginEmail.text.toString()
            val password = binding.myLoginPassword.text.toString()
            if (MyEmail.isEmailError) {
                binding.myLoginEmail.requestFocus()
                Toast.makeText(context, getString(R.string.check_email), Toast.LENGTH_SHORT).show()
//
            } else if (MyPassword.isPasswordError) {
                binding.myLoginPassword.requestFocus()
                Toast.makeText(context, getString(R.string.check_password), Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.postLoginUser(email, password).observe(viewLifecycleOwner) { result ->
                    if (result != null) {
                        when (result) {
                            is Result.Loading -> {
                                binding.progressBar.visibility = View.VISIBLE
                                Toast.makeText(
                                    context,
                                    getString(R.string.loading),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            is Result.Error -> {
                                binding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    context,
                                    result.error,
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            is Result.Success -> {
                                binding.progressBar.visibility = View.GONE
                                val data = result.data
                                data.map {
                                    tokenViewModel.saveToken(it.token)
                                    Token = it.token
                                }
                                view?.findNavController()
                                    ?.navigate(R.id.action_loginFragment2_to_storyListFragment)

                            }
                        }
                    }
                }
            }
        }
        playAnimation()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finishAffinity()
                }


            })
        return binding.root
    }


    private fun playAnimation() {
        val welcomeLabel =
            ObjectAnimator.ofFloat(binding.welcomeLabel, View.ALPHA, 1f).setDuration(500)
        val welcomeText =
            ObjectAnimator.ofFloat(binding.welcomeText, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.myLoginEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.myLoginPassword, View.ALPHA, 1f).setDuration(500)
        val loginButton =
            ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)
        val registerLabel =
            ObjectAnimator.ofFloat(binding.RegisterLabel, View.ALPHA, 1f).setDuration(500)
        val registerButton =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(
                welcomeLabel,
                welcomeText,
                email,
                password,
                loginButton,
                registerLabel,
                registerButton
            )
            start()
        }
    }

    companion object {
        var Token = ""
    }


}




