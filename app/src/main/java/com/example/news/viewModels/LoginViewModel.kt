package com.example.news.viewModels

import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    var credentialsAuthResult = MutableLiveData<AuthResult>()
        private set
    var credentialsErrorResult = MutableLiveData<String?>()
        private set

    var intentSender = MutableLiveData<IntentSenderRequest>()
        private set
    var googleErrorResult = MutableLiveData<String>()
        private set

    fun loginUser(email: String, password: String, fAuth: FirebaseAuth) {
        viewModelScope.launch {
            try {
                credentialsErrorResult.value = null
                val result = fAuth.signInWithEmailAndPassword(email, password).await()
                credentialsAuthResult.value = result
            } catch (e: Exception) {
                credentialsErrorResult.value = e.message
            }
        }
    }

    fun signInGoogle(oneTapClient: SignInClient?, signInRequest: BeginSignInRequest) {
        viewModelScope.launch {
            try {
                val result = oneTapClient?.beginSignIn(signInRequest)?.await()
                result?.pendingIntent?.let {
                    val intentSenderRequest = IntentSenderRequest.Builder(it).build()
                    intentSender.value = intentSenderRequest
                } ?: run {
                    googleErrorResult.value = "Google Sign-In failed: No pending intent."
                }
            } catch (e: ApiException) {
                googleErrorResult.value = "Google Sign-In failed: ${e.message}"
            } catch (e: Exception) {
                googleErrorResult.value = "An unexpected error occurred: ${e.message}"
            }
        }
    }

//    fun getCredentialsAuthResult() = credentialsAuthResult
//    fun getCredentialsErrorResult() = credentialsErrorResult
//
//    fun getIntentSender() = intentSender
//    fun getGoogleErrorResult() = googleErrorResult

}