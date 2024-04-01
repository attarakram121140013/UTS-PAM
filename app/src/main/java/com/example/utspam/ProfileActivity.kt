import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.utspam.LoginActivity
import com.example.utspam.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

    private fun logoutUser() {
        // Logout from Firebase Authentication
        auth.signOut()
        // Redirect to the login page
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
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
