package dam.paco.bibliotech.data.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dam.paco.bibliotech.R

class LoanAdapter (private val loans:List<Loan>, private val onItemClick: (Loan) -> Unit) : RecyclerView.Adapter<LoansViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LoansViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_loan, parent, false)
        return LoansViewHolder(view)
    }

    override fun onBindViewHolder(holder: LoansViewHolder, position: Int) {
        holder.render(loans[position])
        holder.itemView.setOnClickListener {
            println("Pulsado libro con id " + loans[position].id)
            onItemClick(loans[position])
        }
    }

    override fun getItemCount() = loans.size

}
