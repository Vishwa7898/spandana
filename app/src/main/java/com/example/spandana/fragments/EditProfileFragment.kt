package com.example.spandana.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.spandana.databinding.FragmentEditProfileBinding
import java.io.File
import java.io.FileOutputStream

class EditProfileFragment : Fragment() {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var selectedImageUri: Uri? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                binding.ivProfilePic.setImageURI(uri)
            }
        }
    }

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

        // Set up click listeners
        setupClickListeners()
    }

    private fun loadUserData() {
        val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        binding.etName.setText(prefs.getString("user_name", ""))
        binding.etEmail.setText(prefs.getString("user_email", ""))

        // Load profile picture if exists
        val profilePicPath = prefs.getString("profile_pic_path", null)
        profilePicPath?.let { path ->
            val file = File(path)
            if (file.exists()) {
                binding.ivProfilePic.setImageURI(Uri.fromFile(file))
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnChangePhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickImage.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            saveUserData()
        }

        binding.btnCancel.setOnClickListener {
            // Go back to profile fragment
            parentFragmentManager.popBackStack()
        }

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun saveUserData() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return
        }

        // Save profile picture if selected
        var profilePicPath: String? = null
        selectedImageUri?.let { uri ->
            profilePicPath = saveProfilePicture(uri)
        }

        // Save to SharedPreferences
        requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
            .edit()
            .putString("user_name", name)
            .putString("user_email", email)
            .putString("profile_pic_path", profilePicPath)
            .apply()

        Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
        parentFragmentManager.popBackStack()
    }

    private fun saveProfilePicture(uri: Uri): String? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            val file = File(requireContext().filesDir, "profile_pic.jpg")
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
            inputStream?.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
