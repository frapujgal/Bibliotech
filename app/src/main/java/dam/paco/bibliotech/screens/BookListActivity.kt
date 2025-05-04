package dam.paco.bibliotech.screens

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dam.paco.bibliotech.R
import dam.paco.bibliotech.data.model.Book
import dam.paco.bibliotech.data.model.BooksAdapter
import dam.paco.bibliotech.data.model.Constants
import dam.paco.bibliotech.data.model.User
import dam.paco.bibliotech.data.service.ApiService
import dam.paco.bibliotech.data.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BookListActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.createService(ApiService::class.java)

    private lateinit var user: User

    private lateinit var svBookFinder: SearchView
    private lateinit var rvBooks: RecyclerView
    private lateinit var booksAdapter: BooksAdapter
    private val booksList = mutableListOf<Book>()

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var btnAvailableBooks: Button
    private lateinit var btnBooksInUse: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book_list)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getSerializableExtra(Constants.USER) as User
        println("Desde menu: $user")

        initComponents()
        preloadImages()
        initListeners()
        fetchBooks { getAvailableBooks() }

    }

    private fun initComponents() {

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Book List"

        svBookFinder = findViewById(R.id.svBookFinder)
        rvBooks = findViewById(R.id.rvBooks)
        booksAdapter = BooksAdapter(booksList) { selectedBook ->
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra(Constants.BOOK, selectedBook)
            intent.putExtra(Constants.USER, user)
            startActivity(intent)
        }
        rvBooks.layoutManager = LinearLayoutManager(this)
        rvBooks.adapter = booksAdapter

        btnAvailableBooks = findViewById(R.id.btnAvailableBooks)
        btnBooksInUse = findViewById(R.id.btnBooksInUse)

    }

    private fun preloadImages() {
        fetchImages()
    }

    private fun initListeners() {

        btnAvailableBooks.setOnClickListener {
            println("AVAILABLE")
            switchButtons(btnAvailableBooks, btnBooksInUse)
            fetchBooks { getAvailableBooks() }
        }

        btnBooksInUse.setOnClickListener {
            println("IN USE")
            switchButtons(btnBooksInUse, btnAvailableBooks)
            fetchBooks { getUnavailableBooks() }
        }

        svBookFinder.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                booksAdapter.filter.filter(newText)
                return true
            }
        })
    }

    private fun fetchBooks(fetcher: suspend () -> List<Book>?) {
        lifecycleScope.launch {
            try {
                val books = fetcher()
                if (books != null) {
                    booksList.clear()
                    booksList.addAll(books)

                    books.forEach { book ->
                        if (!book.image.isNullOrBlank()) {
                            Glide.with(this@BookListActivity)
                                .load(book.image)
                                .preload()
                        }
                    }

                    booksAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@BookListActivity, "Error al cargar los libros", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@BookListActivity, "Error al cargar los libros", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchImages() {
        lifecycleScope.launch {
            val books = getAllBooks()
            if (books != null) {

                books.forEach { book ->
                    if (!book.image.isNullOrBlank()) {
                        Glide.with(this@BookListActivity)
                            .load(book.image)
                            .preload()
                    }
                }
            } else {
                Toast.makeText(this@BookListActivity, "Error al cargar los libros", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun getAllBooks(): List<Book>? {
        try {
            return apiService.getAllBooks()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al obtener libros: ${e.message}")
            return null
        }
    }

    private suspend fun getAvailableBooks(): List<Book>? {
        try {
            return apiService.getAvailableBooks()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al obtener libros: ${e.message}")
            return null
        }
    }

    private suspend fun getUnavailableBooks(): List<Book>? {
        try {
            return apiService.getUnavailableBooks()
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al obtener libros: ${e.message}")
            return null
        }
    }

    private fun switchButtons(activeButton: Button, inactiveButton: Button) {
        val colorActivo = ContextCompat.getColor(activeButton.context, R.color.vibrantOrange)
        val colorApagado = ContextCompat.getColor(inactiveButton.context, R.color.blueGray)
        val textColorActivo = ContextCompat.getColor(activeButton.context, R.color.black)
        val textColorInactivo = ContextCompat.getColor(inactiveButton.context, R.color.white)

        activeButton.backgroundTintList = ColorStateList.valueOf(colorActivo)
        activeButton.setTextColor(textColorActivo)

        inactiveButton.backgroundTintList = ColorStateList.valueOf(colorApagado)
        inactiveButton.setTextColor(textColorInactivo)
    }

    override fun onSupportNavigateUp(): Boolean {
        val intent = Intent(this@BookListActivity, MenuActivity::class.java)
        intent.putExtra(Constants.USER, user)
        startActivity(intent)

        finish()
        return true
    }
}