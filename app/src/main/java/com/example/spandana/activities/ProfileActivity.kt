package com.example.spandana.activities

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import com.example.spandana.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.util.Log
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPref: SharedPreferences
    private lateinit var storageRef: StorageReference
    private var selectedImageUri: Uri? = null

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                selectedImageUri = uri
                binding.profileImageView.setImageURI(uri)
                uploadProfilePhoto(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        auth = FirebaseAuth.getInstance()
        storageRef = FirebaseStorage.getInstance().reference
        sharedPref = getSharedPreferences("auth_prefs", MODE_PRIVATE)

        setupClickListeners()
        loadUserProfile()
    }

    private fun setupClickListeners() {
        // Back Button
        binding.backButton.setOnClickListener {
            onBackPressed()
        }

        // Profile Photo Click
        binding.profileImageView.setOnClickListener {
            openImagePicker()
        }

        // Edit Profile Button
        binding.editProfileButton.setOnClickListener {
            showEditProfileDialog()
        }

        // Logout Button
        binding.logoutButton.setOnClickListener {
            showLogoutDialog()
        }
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            // Load user details
            binding.nameTextView.text = user.displayName ?: "No Name"
            binding.emailTextView.text = user.email ?: "No Email"
            
            // Load profile photo
            user.photoUrl?.let { photoUrl ->
                // You can use Glide or Picasso here for better image loading
                Log.d("ProfileActivity", "Profile photo URL: $photoUrl")
            }
            
            Log.d("ProfileActivity", "User loaded: ${user.displayName}, ${user.email}")
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    private fun uploadProfilePhoto(imageUri: Uri) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Show loading
        binding.profileImageView.isEnabled = false
        Toast.makeText(this, "Uploading photo...", Toast.LENGTH_SHORT).show()

        // Create reference to user's profile photo
        val photoRef = storageRef.child("profile_photos/${user.uid}.jpg")

        // Upload file
        photoRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get download URL
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Update user profile with photo URL
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setPhotoUri(downloadUri)
                        .build()

                    user.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            binding.profileImageView.isEnabled = true
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Profile photo updated!", Toast.LENGTH_SHORT).show()
                                Log.d("ProfileActivity", "Profile photo updated: $downloadUri")
                            } else {
                                Toast.makeText(this, "Failed to update profile photo", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }
            .addOnFailureListener { exception ->
                binding.profileImageView.isEnabled = true
                Toast.makeText(this, "Failed to upload photo: ${exception.message}", Toast.LENGTH_LONG).show()
                Log.e("ProfileActivity", "Upload failed", exception)
            }
    }

    private fun showEditProfileDialog() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val currentName = user.displayName ?: ""
        
        // Create input dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Profile")
        
        val input = android.widget.EditText(this)
        input.setText(currentName)
        input.hint = "Enter your name"
        
        builder.setView(input)
        builder.setPositiveButton("Save") { _, _ ->
            val newName = input.text.toString().trim()
            if (newName.isNotEmpty()) {
                updateUserProfile(newName)
            } else {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateUserProfile(newName: String) {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(newName)
            .build()

        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()
                    binding.nameTextView.text = newName
                    Log.d("ProfileActivity", "Profile updated: $newName")
                } else {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                    Log.e("ProfileActivity", "Profile update failed", task.exception)
                }
            }
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                performLogout()
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performLogout() {
        // Sign out from Firebase
        auth.signOut()
        
        // Clear shared preferences including guest mode
        sharedPref.edit().clear().apply()
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        
        // Navigate to AuthActivity
        val intent = Intent(this, AuthActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
