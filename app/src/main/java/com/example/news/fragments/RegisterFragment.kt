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
import com.example.news.utils.UserInputValidation
import com.example.news.utils.validateFullName
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

            btnRegister.setOnClickListener {
                handleSubmit()
            }
            tvRegister.setOnClickListener {
                val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                findNavController().navigate(action)
            }
        }

        fAuth.currentUser?.let {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }


    private fun handleSubmit() {


        val isFullNameValid =
            binding.fullNameTil.validateFullName("Full name must be at least 3 characters")

        if (UserInputValidation.validateForm(binding.credentials) &&
            isFullNameValid
        ) {
            binding.progressBar.visibility = View.VISIBLE
            registerViewModel.registerUser(
                binding.credentials.emailEt.text.toString().trim(),
                binding.credentials.passwordEt.text.toString().trim(), fAuth
            )
        }

        registerViewModel.registerResult.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.progressBar.visibility = View.INVISIBLE
                val action = RegisterFragmentDirections.actionRegisterFragmentToHomeFragment()
                findNavController().navigate(action)
            }

        }

        registerViewModel.errorResult.observe(viewLifecycleOwner) {
            if (it?.isNotEmpty() == true) {
                binding.progressBar.visibility = View.GONE
                binding.credentials.emailEt.apply {
                    error = it
                    requestFocus()
                }
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }


    }

}

