package dam.paco.bibliotech.data.model

import java.io.Serializable

data class Book(
    val id: Int,
    val title: String,
    val author: String,
    val publicationYear: Int,
    val genre: String,
    val synopsis: String,
    val image: String,
    val available: Boolean,
    var comments: List<Comment>
) : Serializable
