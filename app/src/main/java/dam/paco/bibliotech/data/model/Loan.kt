package dam.paco.bibliotech.data.model

import java.io.Serializable
import java.util.Date

data class Loan(
    val id: Int,
    val book: Book,
    val user: User,
    val loanDate: Date,
    val maxReturnDate: Date,
    val returnDate: Date? = null,
    val status: LoanStatus
) : Serializable
{
    enum class LoanStatus {
        LOANED, RETURNED
    }

}
