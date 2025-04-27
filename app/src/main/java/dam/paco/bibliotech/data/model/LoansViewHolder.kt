package dam.paco.bibliotech.data.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dam.paco.bibliotech.R

class LoansViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val ivBookImage: ImageView = view.findViewById(R.id.ivBookImage)
    private val tvBookTitle: TextView = view.findViewById(R.id.tvBookTitle)
    private val tvLoanStatus: TextView = view.findViewById(R.id.tvLoanStatus)

    fun render(loan: Loan) {

        if (!loan.book.image.isNullOrBlank()) {
            Glide.with(itemView.context)
                .load(loan.book.image)
                .into(ivBookImage)
        }

        tvBookTitle.text = loan.book.title
        tvLoanStatus.text = loan.status.toString()
        if (loan.status == Loan.LoanStatus.LOANED) {
            val activeLoan = ContextCompat.getColor(tvLoanStatus.context, R.color.green)
            tvLoanStatus.setTextColor(activeLoan)
        }
        else {
            val returnedLoan = ContextCompat.getColor(tvLoanStatus.context, R.color.red)
            tvLoanStatus.setTextColor(returnedLoan)
        }

    }

}