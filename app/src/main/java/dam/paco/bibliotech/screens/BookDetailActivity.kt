package dam.paco.bibliotech.screens

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dam.paco.bibliotech.R
import dam.paco.bibliotech.data.model.Book
import dam.paco.bibliotech.data.model.Comment
import dam.paco.bibliotech.data.model.Constants
import dam.paco.bibliotech.data.model.User
import dam.paco.bibliotech.data.service.ApiService
import dam.paco.bibliotech.data.service.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BookDetailActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.createService(ApiService::class.java)

    private lateinit var book: Book
    private lateinit var user: User

    private lateinit var ivBookImage: ImageView
    private lateinit var tvBookTitle: TextView
    private lateinit var tvBookAuthor: TextView
    private lateinit var tvBookGenre: TextView
    private lateinit var tvBookYear: TextView
    private lateinit var tvBookSynopsis: TextView
    private lateinit var tvComments: TextView
    private lateinit var tvRating: TextView

    private lateinit var btnLoan: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_detail)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getSerializableExtra(Constants.USER) as User
        book = intent.getSerializableExtra(Constants.BOOK) as Book

        println("Soy el usuario " + user.id)
        println("Recibido el book " + book.id)
        println(book.toString())

        initComponents()
        initUI()
        initListeners()

    }

    private fun initComponents() {
        ivBookImage = findViewById(R.id.ivBookImage)
        tvBookTitle = findViewById(R.id.tvBookTitle)
        tvBookAuthor = findViewById(R.id.tvBookAuthor)
        tvBookGenre = findViewById(R.id.tvBookGenre)
        tvBookYear = findViewById(R.id.tvBookYear)
        tvBookSynopsis = findViewById(R.id.tvBookSynopsis)
        tvComments = findViewById(R.id.tvComments)
        tvRating = findViewById(R.id.tvRating)
        btnLoan = findViewById(R.id.btnLoan)
    }

    private fun initUI() {

        lifecycleScope.launch {
            val comments = loadCommentsForBook(book.id)
            book.comments = comments

            if (comments.isNotEmpty()) {
                tvComments.text = book.comments.size.toString()
                tvRating.text = calcularMedia(book)
            } else {
                tvComments.text = "0"
            }
        }

        println(book.image)

        Glide.with(this)
            .load(book.image)
            .error(R.drawable.logo)
            .into(ivBookImage)

        tvBookTitle.text = book.title
        tvBookAuthor.text = book.author
        tvBookGenre.text = book.genre
        tvBookYear.text = book.publicationYear.toString()
        tvBookSynopsis.text = book.synopsis

        if (book.available == false) {
            val colorApagado = ContextCompat.getColor(btnLoan.context, R.color.lightGray)
            btnLoan.backgroundTintList = ColorStateList.valueOf(colorApagado)
            btnLoan.isEnabled = false
        }

    }

    private fun initListeners() {
        btnLoan.setOnClickListener {
            println("LOAN!")
            showLoanDialog()
        }
    }

    private fun calcularMedia(book: Book) : String {

        if (book.comments.isEmpty()) return "0.0"

        val totalRatings = book.comments.sumOf { it.rating }
        return String.format("%.1f", totalRatings.toDouble() / book.comments.size)
    }

    suspend fun loadCommentsForBook(bookId: Int): List<Comment> {
        return try {
            return apiService.getCommentsByBook(bookId) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun showLoanDialog() {
        val builder = AlertDialog.Builder(this)

        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()) // Fecha actual
        val maxReturnDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000)))

        builder.setTitle("Confirm loan")
        builder.setMessage("""
            Are you sure you want to loan this book?
            
            Title: ${book.title}
            Author: ${book.author}
            
            Loan date: $currentDate
            Max return date: $maxReturnDate
            """.trimIndent())

        builder.setPositiveButton("Loan") { dialog, which ->
            lifecycleScope.launch {
                executeLoan(book.id, user.id)
            }
        }

        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(ContextCompat.getColor(this, R.color.green))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(ContextCompat.getColor(this, R.color.red))
    }

    suspend fun executeLoan(bookId: Int, userId: Int) {
        try {
            val loan = apiService.createLoan(bookId, userId)
            runOnUiThread {
                Toast.makeText(this@BookDetailActivity, "Successfully created loan (ID: ${loan.id})", Toast.LENGTH_SHORT).show()
                btnLoan.isEnabled = false
                btnLoan.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(btnLoan.context, R.color.lightGray))

                val intent = Intent(this@BookDetailActivity, BookListActivity::class.java)
                intent.putExtra(Constants.USER, user)
                startActivity(intent)
                finish()
            }
        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this@BookDetailActivity, "Error creating loan", Toast.LENGTH_SHORT).show()
            }
        }
    }
}