package com.example.news.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {

    var registerResult = MutableLiveData<AuthResult>()
        private set
    var errorResult = MutableLiveData<String?>()
        private set


    fun registerUser(email: String, password: String, fAuth: FirebaseAuth) {
        viewModelScope.launch {
            try {
                errorResult.value = null
                val result = fAuth.createUserWithEmailAndPassword(email, password).await()
                registerResult.value = result
            } catch (e: Exception) {
                errorResult.value = e.message
            }

        }

    }

//    fun getRegisterResult() = registerResult
//    fun getErrorResult() = errorResult
}