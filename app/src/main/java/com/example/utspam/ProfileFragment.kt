package com.example.utspam

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.utspam.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

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

        // Get user information from Firebase Authentication
        val currentUser = auth.currentUser
        currentUser?.let {
            val username = it.displayName ?: "Unknown"
            val email = it.email ?: "Unknown"
            val githubUsername = "Your GitHub Username" // Replace with your GitHub username

            // Display user information
            binding.textViewUsername.text = username
            binding.textViewEmail.text = email
            binding.textViewGithubUsername.text = githubUsername
            // Display the first letter of the username as a placeholder profile picture
            val placeholderImage = createInitialsImage(username)
            binding.imageViewProfile.setImageBitmap(placeholderImage)
        }

        // Set onClickListener for the logout button
        binding.logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
