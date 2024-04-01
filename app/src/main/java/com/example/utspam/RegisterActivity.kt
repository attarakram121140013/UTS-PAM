package com.example.utspam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.utspam.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inisialisasi FirebaseFirestore
        db = FirebaseFirestore.getInstance()

        // Set onClickListener for register button
        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString()
            val githubUsername = binding.githubUsernameEditText.text.toString().trim()
            val nik = binding.nikEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()

            // Mendaftarkan pengguna baru
            registerUser(username, password, githubUsername, nik, email)
        }
    }

    private fun registerUser(username: String, password: String, githubUsername: String, nik: String, email: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registrasi berhasil
                    val user = auth.currentUser
                    // Update informasi pengguna
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Menambahkan data pengguna ke Firestore
                                val userData = hashMapOf(
                                    "username" to username,
                                    "password" to password,
                                    "githubUsername" to githubUsername,
                                    "nik" to nik,
                                    "email" to email
                                )

                                db.collection("users")
                                    .document(user?.uid ?: "")
                                    .set(userData)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this,
                                            "Registrasi berhasil.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        finish()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this,
                                            "Gagal menambahkan pengguna ke Firestore: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                } else {
                    // Registrasi gagal
                    Toast.makeText(
                        this,
                        "Registrasi gagal: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
