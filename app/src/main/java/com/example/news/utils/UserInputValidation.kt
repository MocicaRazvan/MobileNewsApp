package com.example.news.utils

import android.util.Patterns
import com.example.news.databinding.ReusableFormBinding
import com.google.android.material.textfield.TextInputLayout

object UserInputValidation {


    fun validateForm(binding: ReusableFormBinding): Boolean {

        binding.apply {
            emailTil.clearError()
            passwordTil.clearError()
            confirmPasswordTil.clearError()
            emailEt.apply {
                clearFocus()
                error = null
            }
        }

        val isEmailValid = binding.emailTil.validateEmail("Invalid email format")
        val isPasswordValid =
            binding.passwordTil.validatePasswordLength(errorMessage = "Password must be at least 6 characters")
        val doPasswordsMatch =
            binding.confirmPasswordTil.matchPasswords(
                binding.passwordTil,
                "Passwords do not match"
            )
        return isEmailValid && isPasswordValid && doPasswordsMatch;
    }


}

fun TextInputLayout.validateNonEmpty(errorMessage: String): Boolean {
    this.error = if (editText?.text.toString().trim().isEmpty()) errorMessage else null
    return this.error == null
}

fun TextInputLayout.validateEmail(errorMessage: String): Boolean {
    val emailPattern = Patterns.EMAIL_ADDRESS.toRegex()
    val isEmailValid = editText?.text.toString().trim().matches(emailPattern)

    this.error = if (!isEmailValid) errorMessage else null
    return isEmailValid
}

fun TextInputLayout.validatePasswordLength(minLength: Int = 6, errorMessage: String): Boolean {
    val isPasswordValid = editText?.text.toString().trim().length >= minLength
    this.error = if (!isPasswordValid) errorMessage else null
    return isPasswordValid
}

fun TextInputLayout.matchPasswords(other: TextInputLayout, errorMessage: String): Boolean {
    val match = editText?.text.toString() == other.editText?.text.toString()
    if (!match) this.error = errorMessage
    return match
}

fun TextInputLayout.validateFullName(errorMessage: String): Boolean {
    val isFullNameValid = editText?.text.toString().trim().length >= 3
    this.error = if (!isFullNameValid) errorMessage else null
    return isFullNameValid
}

fun TextInputLayout.clearError() {
    this.error = null
}

