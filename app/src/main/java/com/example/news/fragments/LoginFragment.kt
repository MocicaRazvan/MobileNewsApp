package com.example.news.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
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
import com.example.news.R
import com.example.news.databinding.FragmentLoginBinding
import com.example.news.viewModels.LoginViewModel
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
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
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

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

            btnGoogle.setOnClickListener {
                Log.d("GOOGLE", "Google sign in clicked")
                signGoogle()
            }

        }

        fAuth.currentUser?.let {
            val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
            findNavController().navigate(action)
        }






        loginViewModel.getIntentSender().observe(viewLifecycleOwner) { intentSenderRequest ->
            intentSenderRequest?.let {
                activityResultLauncher.launch(it)
            }
        }

        loginViewModel.getGoogleErrorResult().observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }

        loginViewModel.getIntentSender().observe(viewLifecycleOwner) {
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

        loginViewModel.getCredentialsAuthResult().observe(viewLifecycleOwner) {
            if (it != null) {
                val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment()
                findNavController().navigate(action)
            }
        }

        loginViewModel.getCredentialsErrorResult().observe(viewLifecycleOwner) {
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