package dam.paco.bibliotech.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dam.paco.bibliotech.R
import dam.paco.bibliotech.data.model.Constants
import dam.paco.bibliotech.data.model.User
import dam.paco.bibliotech.data.service.ApiService
import dam.paco.bibliotech.data.service.RetrofitClient
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.createService(ApiService::class.java)

    private lateinit var user: User

    private lateinit var toolbar: Toolbar
    private lateinit var ivProfilePic: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var pbPoints: ProgressBar
    private lateinit var tvUserPoints: TextView
    private lateinit var etName: EditText
    private lateinit var etPassword: EditText
    private lateinit var etAddress: EditText
    private lateinit var etPhone: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnSaveChanges: Button

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
        pbPoints = findViewById(R.id.pbPoints)
        tvUserPoints = findViewById(R.id.tvUserPoints)
        etName = findViewById(R.id.etName)
        etPassword = findViewById(R.id.etPassword)
        etAddress = findViewById(R.id.etAddress)
        etPhone = findViewById(R.id.etPhone)
        etEmail = findViewById(R.id.etEmail)
        btnSaveChanges = findViewById(R.id.btnSaveChanges)

    }

    private fun initUI() {

        Glide.with(this)
            .load(user.image)
            .placeholder(R.drawable.profile_pic)
            .error(R.drawable.profile_pic)
            .into(ivProfilePic)

        tvUsername.text = user.login
        pbPoints.progress = user.points
        tvUserPoints.text = user.points.toString() + "/100 USER POINTS"
        etName.setText(user.name)
        etPassword.setText(user.password)
        etAddress.setText(user.address)
        etPhone.setText(user.phone)
        etEmail.setText(user.email)

    }

    private fun initListeners() {

        btnSaveChanges.setOnClickListener {
            println("MODIFICAR")
            val updatedUser = User(
                id = user.id,
                name = etName.text.toString(),
                email = etEmail.text.toString(),
                phone = etPhone.text.toString(),
                address = etAddress.text.toString(),
                country = user.country,
                login = user.login,
                password = etPassword.text.toString(),
                points = user.points,
                registrationDate = user.registrationDate,
                image = user.image
            )

            lifecycleScope.launch {
                try {
                    val modifiedUser = apiService.modifyUser(user.id, updatedUser)
                    Toast.makeText(this@ProfileActivity, "User successfully modified", Toast.LENGTH_SHORT).show()
                    println("Usuario modificado: ${modifiedUser.name}")

                    val intent = Intent(this@ProfileActivity, MenuActivity::class.java)
                    intent.putExtra(Constants.USER, modifiedUser)
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@ProfileActivity, "Error modifying user", Toast.LENGTH_SHORT).show()
                    println("ERROR: ${e.message}")
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this@ProfileActivity, MenuActivity::class.java)
        intent.putExtra(Constants.USER, user)
        startActivity(intent)

        finish()
        return true
    }


}