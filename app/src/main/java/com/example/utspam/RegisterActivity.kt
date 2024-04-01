package com.example.utspam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.utspam.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Inisialisasi FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Set onClickListener for register button
        binding.registerButton.setOnClickListener {
            val username =  binding.usernameEditText.text.toString().trim()
            val password =  binding.passwordEditText.text.toString()
            val githubUsername =  binding.githubUsernameEditText.text.toString().trim()
            val nik =  binding.nikEditText.text.toString().trim()
            val email =  binding.emailEditText.text.toString().trim()

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
                    // Update informasi pengguna (opsional)
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }
                    user?.updateProfile(profileUpdates)
                        ?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Update profil berhasil
                                Toast.makeText(
                                    this,
                                    "Registrasi berhasil.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                // Redirect ke halaman selanjutnya atau halaman login
                                // Misalnya, jika ingin langsung login setelah registrasi
                                finish()
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
