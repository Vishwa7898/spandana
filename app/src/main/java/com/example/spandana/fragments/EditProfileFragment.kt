package com.example.spandana.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.spandana.R
import com.example.spandana.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load current user data
        loadUserData()

        // Set up back button
        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        // Save button click
        binding.btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun loadUserData() {
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        binding.etName.setText(prefs.getString("user_name", ""))
        binding.etEmail.setText(prefs.getString("user_email", ""))
    }

    private fun saveUserData() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return
        }

        // Save to SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString("user_name", name)
            putString("user_email", email)
            apply()
        }

        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
