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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvIncorrectCredentials: TextView
    private lateinit var btnLogin: Button
    private lateinit var ibGithub: ImageButton
    private lateinit var ibGitlab: ImageButton
    private lateinit var ibLinkedin: ImageButton

    val apiService = RetrofitClient.createService(ApiService::class.java)

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

            lifecycleScope.launch {
                val user = loginUser(username, password)
                if (user != null) {
                    println("User logged in: $user")

                    user.let {
                        Toast.makeText(this@LoginActivity, "Welcome, ${it.name}!", Toast.LENGTH_SHORT).show()
                    }

                    val intent = Intent(this@LoginActivity, MenuActivity::class.java)
                    intent.putExtra(Constants.USER, user)
                    startActivity(intent)
                    finish()
                } else {
                    withContext(Dispatchers.Main) {
                        tvIncorrectCredentials.visibility = View.VISIBLE
                    }
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

    suspend fun loginUser(username: String, password: String) : User? {
        try {
            val response = withContext(Dispatchers.IO) {
                apiService.login(username, password).execute()
            }

            if (response.isSuccessful) {
                val responseBody = response.body()
                println("Login successful: $responseBody")
                return responseBody
            } else {
                println("Login failed: ${response.code()} - ${response.message()}")
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error occurred: ${e.message}")
            return null
        }
    }
}