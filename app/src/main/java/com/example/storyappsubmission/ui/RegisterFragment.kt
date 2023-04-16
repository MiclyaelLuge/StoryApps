package com.example.storyappsubmission.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.example.storyappsubmission.R
import com.example.storyappsubmission.ViewModel.LoginViewModel
import com.example.storyappsubmission.ViewModel.LoginViewModelFactory
import com.example.storyappsubmission.customView.MyEmail
import com.example.storyappsubmission.customView.MyPassword
import com.example.storyappsubmission.data.remote.result.Result
import com.example.storyappsubmission.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        binding.progressBar.visibility = View.GONE
        val factory: LoginViewModelFactory = LoginViewModelFactory.getInstance(requireContext())
        val viewModel: LoginViewModel by viewModels {
            factory
        }
        binding.registerButton.setOnClickListener {
            val name = binding.myName.text.toString()
            val email = binding.myRegisterEmail.text.toString()
            val password = binding.myRegisterPassword.text.toString()
            if (name.isBlank()) {
                binding.myName.requestFocus()
                Toast.makeText(context, getString(R.string.check_name), Toast.LENGTH_SHORT).show()
            } else if (MyEmail.isEmailError) {
                binding.myRegisterEmail.requestFocus()
                Toast.makeText(context, getString(R.string.check_email), Toast.LENGTH_SHORT).show()
            } else if (MyPassword.isPasswordError) {
                binding.myRegisterPassword.requestFocus()
                Toast.makeText(context, getString(R.string.check_password), Toast.LENGTH_SHORT)
                    .show()
            } else {
                viewModel.postRegisterUser(name, email, password)
                    .observe(viewLifecycleOwner) { result ->
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
                                    Toast.makeText(context, result.error, Toast.LENGTH_SHORT).show()
                                }
                                is Result.Success -> {
                                    binding.progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        context,
                                        getString(R.string.register_berhasil),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    view?.findNavController()
                                        ?.navigate(R.id.action_registerFragment_to_loginFragment2)
                                }
                            }
                        }
                    }
            }

        }
        playAnimation()
        return binding.root
    }

    private fun playAnimation() {
        val welcomeImage =
            ObjectAnimator.ofFloat(binding.mainImage, View.ALPHA, 1f).setDuration(500)
        val welcomeText =
            ObjectAnimator.ofFloat(binding.registerlabel, View.ALPHA, 1f).setDuration(500)
        val name = ObjectAnimator.ofFloat(binding.myName, View.ALPHA, 1f).setDuration(500)
        val email = ObjectAnimator.ofFloat(binding.myRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val password =
            ObjectAnimator.ofFloat(binding.myRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val registerButton =
            ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(welcomeImage, welcomeText, name, email, password, registerButton)
            start()
        }
    }


}