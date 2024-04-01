package com.example.utspam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.utspam.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: UserAdapter
    private var userList = mutableListOf<User>()
    private var originalUserList = mutableListOf<User>()
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter(userList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadUsersFromFirestore()
        loadUsersFromRetrofit()

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

    private fun loadUsersFromFirestore() {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                userList.clear()
                originalUserList.clear()
                for (document in documents) {
                    val user = document.toObject(User::class.java)
                    userList.add(user)
                    originalUserList.add(user)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->

            }
    }

    private fun loadUsersFromRetrofit() {
        val apiService = ApiClient.apiClient
        val call = apiService.getUsers()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        userList.addAll(it.data)
                        originalUserList.addAll(it.data)
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {

            }
        })
    }

    private fun searchUsers(query: String) {
        val filteredList = originalUserList.filter { user ->
            user.username.contains(query, ignoreCase = true) ||
                    user.email.contains(query, ignoreCase = true)
        }
        userList.clear()
        userList.addAll(filteredList)
        adapter.notifyDataSetChanged()
    }
}
