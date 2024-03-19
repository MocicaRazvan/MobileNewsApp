package com.example.news.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.news.BuildConfig
import com.example.news.R
import com.example.news.databinding.FragmentLoginBinding
import com.example.news.utils.UserInputValidation
import com.example.news.utils.clearError
import com.example.news.utils.matchPasswords
import com.example.news.utils.validateEmail
import com.example.news.utils.validatePasswordLength
import com.example.news.viewModels.LoginViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var loginViewModel: LoginViewModel
    private var oneTapClient: SignInClient? = null
    private lateinit var signInRequest: BeginSignInRequest


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

        oneTapClient = Identity.getSignInClient(requireContext())

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(BuildConfig.DEFAULT_WEB_CLIENT_ID)
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.apply {
            tvRegister.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
                findNavController().navigate(action)
            }
            btnLogin.setOnClickListener {
                onSubmit()
            }

            btnGoogle.setOnClickListener {
                Log.d("GOOGLE", "Google sign in clicked")
                signGoogle()
            }

        }

        fAuth.currentUser?.let {
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            findNavController().navigate(action)
        }






        loginViewModel.intentSender.observe(viewLifecycleOwner) { intentSenderRequest ->
            intentSenderRequest?.let {
                activityResultLauncher.launch(it)
            }
        }

        loginViewModel.googleErrorResult.observe(viewLifecycleOwner) { errorMessage ->
            binding.progressBar.visibility = View.INVISIBLE
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.intentSender.observe(viewLifecycleOwner) {
            Log.d("GOOGLE", "Intent sender received ${it}")
            activityResultLauncher.launch(it)
        }
    }

    private val activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    val credential = oneTapClient?.getSignInCredentialFromIntent(result.data)
                    val idToken = credential?.googleIdToken
                    when {
                        idToken != null -> {
                            val firebaseCredentials =
                                GoogleAuthProvider.getCredential(idToken, null)
                            fAuth.signInWithCredential(firebaseCredentials).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    binding.progressBar.visibility = View.INVISIBLE
                                    val action =
                                        LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                                    findNavController().navigate(action)
                                } else {
                                    Toast.makeText(
                                        requireContext(),
                                        it.exception?.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            Toast.makeText(requireContext(), "Sign in complete", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {
                            // Shouldn't happen.
                            Toast.makeText(requireContext(), "No ID token!", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                } catch (e: ApiException) {
                    // ...
                    Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()

                }
            }

        }

    private fun signGoogle() {
        loginViewModel.signInGoogle(oneTapClient, signInRequest)
    }

    private fun onSubmit() {

        if (UserInputValidation.validateForm(binding.credentials)) {
            binding.progressBar.visibility = View.VISIBLE
            loginViewModel.loginUser(
                binding.credentials.emailEt.text.toString().trim(),
                binding.credentials.passwordEt.text.toString().trim(), fAuth
            )
        }


        loginViewModel.credentialsAuthResult.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.progressBar.visibility = View.INVISIBLE
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            }
        }

        loginViewModel.credentialsErrorResult.observe(viewLifecycleOwner) {
            if (it?.isNotEmpty() == true) {
                binding.progressBar.visibility = View.INVISIBLE
                binding.credentials.emailEt.apply {
                    error = it
                    requestFocus()
                }
            }
        }


    }


}


