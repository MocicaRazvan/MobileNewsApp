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
import com.example.news.databinding.FragmentRegisterBinding
import com.example.news.viewModels.RegisterViewModel
import com.google.firebase.auth.FirebaseAuth


class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        registerViewModel = ViewModelProvider(this)[RegisterViewModel::class.java]
        fAuth = FirebaseAuth.getInstance()

        binding.apply {
            home.setOnClickListener {
                val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                findNavController().navigate(action)
            }

            btnRegister.setOnClickListener {
                handleSubmit()
            }
        }

        fAuth.currentUser?.let {
            val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
            findNavController().navigate(action)
        }
    }


    private fun handleSubmit() {
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

        registerViewModel.registerUser(email, password, fAuth)

        registerViewModel.getRegisterResult().observe(viewLifecycleOwner) {
            if (it != null) {
                binding.progressBar.visibility = View.INVISIBLE
                val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                findNavController().navigate(action)
            }

        }

        registerViewModel.getErrorResult().observe(viewLifecycleOwner) {
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