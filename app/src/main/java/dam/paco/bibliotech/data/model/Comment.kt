package dam.paco.bibliotech.data.model

import java.io.Serializable
import java.util.Date

data class Comment(
    val id: Int? = null,
    val book: Book,
    val user: User,
    val comment: String,
    val rating: Int,
    val date: Date = Date()
) : Serializable
