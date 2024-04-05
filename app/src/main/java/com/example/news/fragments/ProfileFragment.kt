package com.example.news.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.navigation.fragment.findNavController
import com.example.news.R
import com.example.news.databinding.FragmentProfileBinding
import com.example.news.utils.ThemePreferences
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var fAuth: FirebaseAuth
    private lateinit var themePreferences: ThemePreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fAuth = FirebaseAuth.getInstance()

        val user = fAuth.currentUser

        themePreferences = ThemePreferences(requireContext())



        binding.apply {
            tvEmail.text = user?.email
            btnLogOut.setOnClickListener { logout() }
            updateSwitchDrawable(themePreferences.isDarkTheme(), themeSwitch)
            themeSwitch.setOnCheckedChangeListener { _, isChecked ->
                handleThemeSwitch(themeSwitch, isChecked)
            }

        }

    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val action = ProfileFragmentDirections.actionProfileFragmentToLoginFragment()
        findNavController().navigate(action)
    }

    private fun handleThemeSwitch(switch: SwitchMaterial, isChecked: Boolean) {
        themePreferences.saveTheme(isChecked)
        updateSwitchDrawable(isChecked, switch)
    }

    private fun updateSwitchDrawable(isChecked: Boolean, switch: SwitchMaterial) {
        switch.apply {
            this.isChecked = isChecked
            setButtonDrawable(
                when (isChecked) {
                    true -> R.drawable.ic_dark
                    false -> R.drawable.ic_light
                }
            )

        }
    }

}