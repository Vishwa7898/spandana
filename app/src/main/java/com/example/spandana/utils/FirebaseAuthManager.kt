package com.example.spandana.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthManager private constructor(private val context: Context) {
    
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    
    companion object {
        @Volatile
        private var INSTANCE: FirebaseAuthManager? = null
        
        fun getInstance(context: Context): FirebaseAuthManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: FirebaseAuthManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
    
    // Check if user is currently logged in
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }
    
    // Get current user
    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
    
    // Login with email and password
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Save user data to SharedPreferences
                saveUserDataToPrefs(user)
                
                // Get additional user data from Firestore
                getUserDataFromFirestore(user.uid)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Sign up with email and password
    suspend fun signUpWithEmail(name: String, email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Update user profile with display name
                val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .build()
                user.updateProfile(profileUpdates).await()
                
                // Save user data to Firestore
                saveUserDataToFirestore(user.uid, name, email)
                
                // Save user data to SharedPreferences
                saveUserDataToPrefs(user)
                
                Result.success(user)
            } else {
                Result.failure(Exception("Sign up failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Logout user
    fun logout() {
        auth.signOut()
        clearUserDataFromPrefs()
    }
    
    // Save user data to SharedPreferences
    private fun saveUserDataToPrefs(user: FirebaseUser) {
        prefs.edit().apply {
            putBoolean("is_logged_in", true)
            putString("user_type", "registered")
            putString("user_name", user.displayName ?: "User")
            putString("user_email", user.email ?: "")
            putString("user_uid", user.uid)
            apply()
        }
    }
    
    // Clear user data from SharedPreferences
    private fun clearUserDataFromPrefs() {
        prefs.edit().clear().apply()
    }
    
    // Save user data to Firestore
    private suspend fun saveUserDataToFirestore(uid: String, name: String, email: String) {
        try {
            val userData = hashMapOf(
                "name" to name,
                "email" to email,
                "createdAt" to com.google.firebase.Timestamp.now(),
                "lastLogin" to com.google.firebase.Timestamp.now()
            )
            
            firestore.collection("users").document(uid).set(userData).await()
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }
    
    // Get user data from Firestore
    private suspend fun getUserDataFromFirestore(uid: String) {
        try {
            val document = firestore.collection("users").document(uid).get().await()
            if (document.exists()) {
                val data = document.data
                data?.let {
                    prefs.edit().apply {
                        putString("user_name", it["name"] as? String ?: "User")
                        apply()
                    }
                }
            }
        } catch (e: Exception) {
            // Handle error silently for now
        }
    }
    
    // Update user data in Firestore
    suspend fun updateUserData(uid: String, data: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("users").document(uid).update(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Send password reset email
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
