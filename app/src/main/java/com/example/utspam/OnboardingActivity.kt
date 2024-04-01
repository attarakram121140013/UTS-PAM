package com.example.utspam

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        // Timer untuk penundaan sebelum beralih ke halaman login
        Handler(Looper.getMainLooper()).postDelayed({
            // Cek apakah pengguna sudah masuk sebelumnya menggunakan DataStore Preference
            val preferences = PreferenceManager.getDefaultSharedPreferences(this)
            val isLoggedIn = preferences.getBoolean("isLoggedIn", false)

            if (!isLoggedIn) {
                // Pengguna belum masuk sebelumnya, arahkan ke halaman login
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // Pengguna sudah masuk sebelumnya, arahkan ke halaman Beranda
                startActivity(Intent(this, MainActivity::class.java))
            }

            // Tutup halaman Onboarding setelah navigasi
            finish()
        }, 3000) // Timer 3000ms (3 detik)
    }
}
