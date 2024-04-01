package com.example.utspam

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.utspam.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private var userListener: ListenerRegistration? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.logoutButton.setOnClickListener {
            logoutUser()
        }
        auth.addAuthStateListener { firebaseAuth ->
            val currentUser = firebaseAuth.currentUser
            if (currentUser != null) {
                // User is signed in, load user data
                loadUserData(currentUser)
            } else {
                // User is not signed in, clear UI
                clearUserData()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        auth.addAuthStateListener(authListener)
    }

    override fun onStop() {
        super.onStop()
        auth.removeAuthStateListener(authListener)
    }

    private val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is signed in, load user data
            loadUserData(currentUser)
        } else {
            // User is not signed in, clear UI
            clearUserData()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        // Remove the listener when the fragment is destroyed to avoid memory leaks
        userListener?.remove()
    }

    private fun loadUserData(user: FirebaseUser) {
        // Reference to the current user's document
        val userRef = firestore.collection("users").document(user.uid)

        // Retrieve user data from Firestore
        userListener = userRef.addSnapshotListener { document, error ->
            if (error != null) {
                // Handle errors
                return@addSnapshotListener
            }

            if (document != null && document.exists()) {
                val username = document.getString("username") ?: "Unknown"
                val email = document.getString("email") ?: "Unknown"
                val githubUsername = document.getString("githubUsername") ?: "Unknown"

                // Display user information
                binding.textViewUsername.text = username
                binding.textViewEmail.text = email
                binding.textViewGithubUsername.text = githubUsername
                // Display the first letter of the username as a placeholder profile picture
                val placeholderImage = createInitialsImage(username)
                binding.imageViewProfile.setImageBitmap(placeholderImage)
            } else {
                // Document doesn't exist
            }
        }
    }

    private fun clearUserData() {
        // Clear UI when user is not signed in
        binding.textViewUsername.text = ""
        binding.textViewEmail.text = ""
        binding.textViewGithubUsername.text = ""
        binding.imageViewProfile.setImageResource(R.drawable.ic_person_placeholder)
    }

    private fun logoutUser() {
        // Logout from Firebase Authentication
        auth.signOut()
        // Redirect to the login page
        startActivity(Intent(requireContext(), LoginActivity::class.java))
        requireActivity().finish()
    }

    private fun createInitialsImage(name: String): Bitmap {
        val width = 100 // Adjust size as needed
        val height = 100 // Adjust size as needed
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = Color.BLUE // Adjust color as needed
            isAntiAlias = true
            textSize = 40f // Adjust font size as needed
            typeface = Typeface.DEFAULT_BOLD
        }
        val initials = name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
        val textBounds = Rect()
        paint.getTextBounds(initials, 0, initials.length, textBounds)
        val textX = (width - textBounds.width()) / 2f
        val textY = (height + textBounds.height()) / 2f
        canvas.drawText(initials, textX, textY, paint)
        return bitmap
    }
}
