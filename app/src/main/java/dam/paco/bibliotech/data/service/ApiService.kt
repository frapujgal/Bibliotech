package dam.paco.bibliotech.data.service

import dam.paco.bibliotech.data.model.Book
import dam.paco.bibliotech.data.model.Comment
import dam.paco.bibliotech.data.model.Loan
import dam.paco.bibliotech.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("users/login")
    suspend fun login(@Query("username") username: String, @Query("password") password: String): User

    @PATCH("users/{id}")
    suspend fun modifyUser(@Path("id") id: Int, @Body user: User): User

    @GET("books")
    suspend fun getAllBooks(): List<Book>

    @GET("books/")
    suspend fun getBookById(@Query("bookId") bookId: Int): Book

    @GET("books/available")
    suspend fun getAvailableBooks(): List<Book>

    @GET("books/unavailable")
    suspend fun getUnavailableBooks(): List<Book>

    @GET("comments")
    suspend fun getCommentsByBook(@Query("book") bookId: Int): List<Comment>

    @GET("loans")
    suspend fun getAllLoans(): List<Loan>

    @GET("loans/user/{userId}")
    suspend fun getLoansByUser(@Path("userId") userId: Int): List<Loan>

    @POST("loans")
    suspend fun createLoan(@Query("bookId") bookId: Int, @Query("userId") userId: Int): Loan

    @PUT("loans/{id}/return")
    suspend fun returnLoan(@Path("id") loanId: Int): Response<Unit>



}