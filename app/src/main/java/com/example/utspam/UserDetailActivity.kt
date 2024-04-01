package com.example.utspam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.utspam.databinding.ActivityUserDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get user ID from Intent
        val userId = intent.getIntExtra("userId", -1)

        // Make API call to get user details
        val apiService = ApiClient.apiClient
        val call = apiService.getUser(userId)

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    val user = userResponse?.data // Assuming data contains a single user
                    user?.let {
                        displayUserDetails(it) // Pass a single user to the function
                    }
                } else {
                    Toast.makeText(
                        this@UserDetailActivity,
                        "Failed to get user details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }


            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                Toast.makeText(
                    this@UserDetailActivity,
                    "Failed to get user details: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayUserDetails(users: List<User>) {
        for (user in users) {
            binding.textViewName.append("Name: ${user.username}\n")
            binding.textViewEmail.append("Email: ${user.email}\n")
        }
    }
}
