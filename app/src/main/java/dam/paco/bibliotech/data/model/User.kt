package dam.paco.bibliotech.data.model

import java.io.Serializable
import java.util.Date
import java.time.LocalDate

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val country: String,
    val login: String,
    val password: String,
    val points: Int,
    val registrationDate: Date,
    val image: String,
    val comments: List<Comment>
) : Serializable