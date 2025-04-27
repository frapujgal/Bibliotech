package dam.paco.bibliotech.data.service

import dam.paco.bibliotech.data.model.Book
import dam.paco.bibliotech.data.model.Comment
import dam.paco.bibliotech.data.model.Loan
import dam.paco.bibliotech.data.model.User
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users/login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<User>

    @GET("books")
    fun getAllBooks(): Call<List<Book>>

    @GET("books/")
    fun getBookById(
        @Query("bookId") bookId: Int
    ): Call<Book>

    @GET("books/available")
    fun getAvailableBooks(): Call<List<Book>>

    @GET("books/unavailable")
    fun getUnavailableBooks(): Call<List<Book>>

    @GET("comments")
    suspend fun getCommentsByBook(@Query("book") bookId: Int): List<Comment>

    @GET("loans")
    fun getAllLoans(): Call<List<Loan>>

    @GET("loans/user/{userId}")
    fun getLoansByUser(@Path("userId") userId: Int): Call<List<Loan>>

    @POST("loans")
    suspend fun createLoan(
        @Query("bookId") bookId: Int,
        @Query("userId") userId: Int
    ): Loan

    @PUT("loans/{id}/return")
    suspend fun returnLoan(@Path("id") loanId: Int): Response<Unit>


}