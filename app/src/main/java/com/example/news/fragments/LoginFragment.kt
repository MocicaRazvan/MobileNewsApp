package com.example.news.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.news.R
import com.example.news.databinding.FragmentLoginBinding
import com.example.news.viewModels.LoginViewModel
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fAuth = FirebaseAuth.getInstance()

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.apply {
            btnRegister.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }
            home.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            }

            btnLogin.setOnClickListener {
                onSubmit()
            }

        }

        fAuth.currentUser?.let {
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }

    private fun onSubmit() {
        val email = binding.email.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty()) {
            binding.email.apply {
                error = "Email is required"
                requestFocus()
            }
            return
        }

        if (password.isEmpty()) {
            binding.password.apply {
                error = "Password is required"
                requestFocus()
            }
            return
        }

        if (password.length < 6) {
            binding.password.apply {
                error = "Password must be at least 6 characters"
                requestFocus()
            }
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        loginViewModel.loginUser(email, password, fAuth)

        loginViewModel.getAuthResult().observe(viewLifecycleOwner) {
            if (it != null) {
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            }
        }

        loginViewModel.getErrorResult().observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                binding.progressBar.visibility = View.GONE
                binding.email.apply {
                    error = it
                    requestFocus()
                }
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }


    }


}