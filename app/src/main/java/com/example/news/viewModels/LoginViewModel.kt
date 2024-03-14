package com.example.news.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val authResult = MutableLiveData<AuthResult>()
    private val errorResult = MutableLiveData<String>()

    fun loginUser(email: String, password: String, fAuth: FirebaseAuth) {
        viewModelScope.launch {
            try {
                val result = fAuth.signInWithEmailAndPassword(email, password).await()
                authResult.value = result
            } catch (e: Exception) {
                errorResult.value = e.message
            }
        }
    }

    fun getAuthResult() = authResult
    fun getErrorResult() = errorResult

}