package com.example.utspam

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        // Set timer untuk Splash Screen
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            // Periksa apakah pengguna sudah masuk sebelumnya menggunakan DataStore Preference
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isLoggedIn = preferences.getBoolean("isLoggedIn", false)

            if (isLoggedIn) {
                // Pengguna sudah masuk sebelumnya, arahkan ke halaman Beranda
                startActivity(Intent(this, HomeActivity::class.java))
            } else {
                // Pengguna belum masuk sebelumnya, arahkan ke halaman Onboarding Motion Layout
                startActivity(Intent(this, OnboardingActivity::class.java))
            }

            // Tutup Splash Screen setelah navigasi
            finish()
        }, 3000) // Timer 3000ms (3 detik)
    }
}
