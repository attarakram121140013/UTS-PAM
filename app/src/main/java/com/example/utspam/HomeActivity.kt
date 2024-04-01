package com.example.utspam

import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.utspam.databinding.ActivityHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: UserAdapter
    private var userList = mutableListOf<User>()
    private var originalUserList = mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = UserAdapter(userList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        loadUsers()

        // Implement search functionality
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchUsers(it) }
                return true
            }
        })
    }

    private fun loadUsers() {
        val apiService = ApiClient.apiClient
        val call = apiService.getUsers()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        userList.clear()
                        userList.addAll(it.data)
                        originalUserList.addAll(it.data)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                // Handle failure
            }
        })
    }

    private fun searchUsers(query: String) {
        val filteredList = originalUserList.filter { user ->
            user.first_name.contains(query, ignoreCase = true) ||
                    user.last_name.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)
        }
        userList.clear()
        userList.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }
}
