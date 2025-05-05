package dam.paco.bibliotech.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dam.paco.bibliotech.R
import dam.paco.bibliotech.data.model.Constants
import dam.paco.bibliotech.data.model.User

class MenuActivity : AppCompatActivity() {

    private lateinit var user: User

    private lateinit var cvBooks: CardView
    private lateinit var cvMyLoans: CardView
    private lateinit var cvMyProfile: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getSerializableExtra(Constants.USER) as User
        println("Desde menu: $user")

        initComponents()
        initListeners()

    }

    private fun initComponents() {
        cvBooks = findViewById(R.id.cvBooks)
        cvMyLoans = findViewById(R.id.cvMyLoans)
        cvMyProfile = findViewById(R.id.cvMyProfile)
    }

    private fun initListeners() {
        cvBooks.setOnClickListener {
            val intent = Intent(this@MenuActivity, BookListActivity::class.java)
            intent.putExtra(Constants.USER, user)
            startActivity(intent)
        }

        cvMyLoans.setOnClickListener {
            val intent = Intent(this@MenuActivity, UserLoansActivity::class.java)
            intent.putExtra(Constants.USER, user)
            startActivity(intent)
        }

        cvMyProfile.setOnClickListener {
            val intent = Intent(this@MenuActivity, ProfileActivity::class.java)
            intent.putExtra(Constants.USER, user)
            startActivity(intent)
        }
    }
}