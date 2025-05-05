package dam.paco.bibliotech.screens

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import dam.paco.bibliotech.R
import dam.paco.bibliotech.data.model.Constants
import dam.paco.bibliotech.data.model.User
import dam.paco.bibliotech.data.service.ApiService
import dam.paco.bibliotech.data.service.RetrofitClient

class ProfileActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.createService(ApiService::class.java)

    private lateinit var user: User

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var ivProfilePic: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var etName: EditText
    private lateinit var etPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getSerializableExtra(Constants.USER) as User
        println("Desde profile: $user")

        initComponents()
        initUI()
        initListeners()

    }



    private fun initComponents() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "My profile"

        ivProfilePic = findViewById(R.id.ivProfilePic)
        tvUsername = findViewById(R.id.tvUsername)
        etName = findViewById(R.id.etName)
        etPassword = findViewById(R.id.etPassword)

    }

    private fun initUI() {

        Glide.with(this)
            .load(user.image)
            .placeholder(R.drawable.profile_pic)
            .error(R.drawable.profile_pic)
            .into(ivProfilePic)

        tvUsername.text = user.login
        etName.setText(user.name)
        etPassword.setText(user.password)
    }

    private fun initListeners() {

    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this@ProfileActivity, MenuActivity::class.java)
        intent.putExtra(Constants.USER, user)
        startActivity(intent)

        finish()
        return true
    }


}