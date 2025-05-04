package dam.paco.bibliotech.screens

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import dam.paco.bibliotech.R
import dam.paco.bibliotech.data.model.Constants
import dam.paco.bibliotech.data.model.User
import dam.paco.bibliotech.data.service.ApiService
import dam.paco.bibliotech.data.service.RetrofitClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvIncorrectCredentials: TextView
    private lateinit var btnLogin: Button
    private lateinit var ibGithub: ImageButton
    private lateinit var ibGitlab: ImageButton
    private lateinit var ibLinkedin: ImageButton

    private val apiService = RetrofitClient.createService(ApiService::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initListeners()
    }

    private fun initComponents() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        tvIncorrectCredentials = findViewById(R.id.tvIncorrectCredentials)
        btnLogin = findViewById(R.id.btnLogin)
        ibGithub = findViewById(R.id.ibGithub)
        ibGitlab = findViewById(R.id.ibGitlab)
        ibLinkedin = findViewById(R.id.ibLinkedin)
    }

    private fun initListeners() {
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            println("Username: $username -> Password: $password")

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            tvIncorrectCredentials.visibility = View.GONE
            btnLogin.isEnabled = false

            lifecycleScope.launch {
                try {
                    val user = loginUser(username, password)
                    if (user != null) {
                        println("User logged in: $user")
                        Toast.makeText(this@LoginActivity, "Welcome, ${user.name}!", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                        intent.putExtra(Constants.USER, user)
                        startActivity(intent)
                        finish()
                    } else {
                        tvIncorrectCredentials.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@LoginActivity, "Unexpected error. Try again.", Toast.LENGTH_SHORT).show()
                } finally {
                    btnLogin.isEnabled = true
                }

            }
        }

        ibGithub.setOnClickListener {
            val url = "https://github.com/frapujgal"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        ibGitlab.setOnClickListener {
            val url = "https://gitlab.com/frapujgal"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        ibLinkedin.setOnClickListener {
            val url = "https://www.linkedin.com/in/francisco-pujol-gallego"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }
    }

    private suspend fun loginUser(username: String, password: String) : User? {
        try {
            val user = apiService.login(username, password)
            println("Login successful: $user")

            return user
        } catch (e: Exception) {
            e.printStackTrace()
            println("Login error: ${e.message}")

            return null
        }
    }
}