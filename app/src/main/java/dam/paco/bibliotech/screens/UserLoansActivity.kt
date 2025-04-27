package dam.paco.bibliotech.screens

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dam.paco.bibliotech.R
import dam.paco.bibliotech.data.model.Constants
import dam.paco.bibliotech.data.model.Loan
import dam.paco.bibliotech.data.model.LoanAdapter
import dam.paco.bibliotech.data.model.User
import dam.paco.bibliotech.data.service.ApiService
import dam.paco.bibliotech.data.service.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserLoansActivity : AppCompatActivity() {

    private val apiService = RetrofitClient.createService(ApiService::class.java)

    private lateinit var user: User

    private var allLoansList: List<Loan> = emptyList()
    private var loansList = mutableListOf<Loan>()
    private lateinit var rvLoans: RecyclerView
    private lateinit var loansAdapter: LoanAdapter

    private lateinit var toolbar: Toolbar
    private lateinit var btnActiveLoans: Button
    private lateinit var btnReturnedLoans: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_loans)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        user = intent.getSerializableExtra(Constants.USER) as User

        fetchAllLoans()
        initComponents()
        initListeners()



    }

    private fun initComponents() {

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Loans"

        btnActiveLoans = findViewById(R.id.btnActiveLoans)
        btnReturnedLoans = findViewById(R.id.btnReturnedLoans)

        rvLoans = findViewById(R.id.rvLoans)
        loansAdapter = LoanAdapter(loansList) { selectedLoan ->

            if (selectedLoan.status == Loan.LoanStatus.LOANED) {
                showReturnDialog(selectedLoan)
            }

            /*val intent = Intent(this, LoanDetailActivity::class.java)
            intent.putExtra(Constants.BOOK, selectedBook)
            intent.putExtra(Constants.USER, user)
            startActivity(intent)*/
        }
        rvLoans.layoutManager = LinearLayoutManager(this)
        rvLoans.adapter = loansAdapter



    }

    private fun initListeners() {
        btnActiveLoans.setOnClickListener {
            println("ACTIVE")
            switchButtons(btnActiveLoans, btnReturnedLoans)
            fetchActiveLoans()
        }

        btnReturnedLoans.setOnClickListener {
            println("RETURNED")
            switchButtons(btnReturnedLoans, btnActiveLoans)
            fetchReturnedLoans()
        }
    }

    private fun fetchAllLoans() {
        lifecycleScope.launch {
            val loans = getAllLoans()

            if (loans != null) {
                if (allLoansList != null) {
                    allLoansList = loans
                }

                loansList.clear()
                loansList.addAll(loans)

                loans.forEach { loan ->

                    if (!loan.book.image.isNullOrBlank()) {
                        Glide.with(this@UserLoansActivity)
                            .load(loan.book.image)
                            .preload()
                    }
                }

                loansAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@UserLoansActivity, "Error al cargar los préstamos", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private suspend fun getAllLoans(): List<Loan>? {
        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getLoansByUser(user.id).execute()
            }

            if (response.isSuccessful) {
                response.body()
            } else {
                println("Error en la respuesta: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println("Error al obtener préstamos: ${e.message}")
            null
        }
    }

    private fun fetchActiveLoans() {
        val activeLoans = allLoansList.filter { it.status == Loan.LoanStatus.LOANED }.toMutableList()

        loansList.clear()
        loansList.addAll(activeLoans)

        loansList.forEach { loan ->

            if (!loan.book.image.isNullOrBlank()) {
                Glide.with(this@UserLoansActivity)
                    .load(loan.book.image)
                    .preload()
            }
        }

        loansAdapter.notifyDataSetChanged()
    }

    private fun fetchReturnedLoans() {
        val activeLoans = allLoansList.filter { it.status == Loan.LoanStatus.RETURNED }.toMutableList()

        loansList.clear()
        loansList.addAll(activeLoans)

        loansList.forEach { loan ->

            if (!loan.book.image.isNullOrBlank()) {
                Glide.with(this@UserLoansActivity)
                    .load(loan.book.image)
                    .preload()
            }
        }

        loansAdapter.notifyDataSetChanged()
    }

    private fun showReturnDialog(loan: Loan) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Confirm return")
        builder.setMessage("""
            Are you sure you want to return this book?
            
            Title: ${loan.book.title}
            Author: ${loan.book.author}
            
            Loan date: ${loan.loanDate}
            Max return date: ${loan.maxReturnDate}
            """.trimIndent())

        builder.setPositiveButton("Return") { dialog, which ->
            lifecycleScope.launch {
                println("voy a devolver")
                returnLoan(loan)
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

    suspend fun returnLoan(loan: Loan) {
        try {
            val response = apiService.returnLoan(loan.id)

            if (response.isSuccessful) {
                runOnUiThread {
                    Toast.makeText(this@UserLoansActivity, "Successfully returned loan", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@UserLoansActivity, BookListActivity::class.java)
                    intent.putExtra(Constants.USER, user)
                    startActivity(intent)
                    finish()
                }
            } else {
                runOnUiThread {
                    Toast.makeText(this@UserLoansActivity, "Error returning loan: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            runOnUiThread {
                Toast.makeText(this@UserLoansActivity, "Error returning loan: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
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
        onBackPressedDispatcher.onBackPressed()
        return true
    }



}