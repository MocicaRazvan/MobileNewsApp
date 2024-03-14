package com.example.news.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    private val registerResult = MutableLiveData<AuthResult>()
    private val errorResult = MutableLiveData<String>()


    fun registerUser(email: String, password: String, fAuth: FirebaseAuth) {
        viewModelScope.launch {
            try {
                val result = fAuth.createUserWithEmailAndPassword(email, password).await()
                registerResult.value = result
            } catch (e: Exception) {
                errorResult.value = e.message
            }

        }

    }

    fun getRegisterResult() = registerResult
    fun getErrorResult() = errorResult
}