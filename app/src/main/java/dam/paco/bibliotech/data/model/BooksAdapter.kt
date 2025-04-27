package dam.paco.bibliotech.data.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import dam.paco.bibliotech.R
import dam.paco.bibliotech.screens.BookListActivity

class BooksAdapter (private val books:List<Book>, private val onItemClick: (Book) -> Unit) : RecyclerView.Adapter<BooksViewHolder>(),
    Filterable {

    private var booksListFull = books
    private var booksListFiltered = booksListFull

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BooksViewHolder(view)
    }

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        holder.render(booksListFiltered[position])
        holder.itemView.setOnClickListener {
            println("Pulsado libro con id " + booksListFiltered[position].id)
            onItemClick(booksListFiltered[position])
        }
    }

    override fun getItemCount() = booksListFiltered.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Book>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(booksListFull)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for (item in booksListFull) {
                        if (item.title.lowercase().contains(filterPattern) || item.author.lowercase().contains(filterPattern)) {
                            if (!filteredList.contains(item)) {
                                filteredList.add(item)
                            }
                        }
                    }
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                booksListFiltered = results?.values as List<Book>
                notifyDataSetChanged()
            }
        }
    }
}