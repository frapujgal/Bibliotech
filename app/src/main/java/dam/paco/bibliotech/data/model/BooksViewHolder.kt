package dam.paco.bibliotech.data.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dam.paco.bibliotech.R

class BooksViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    //private val tvBookId:TextView = view.findViewById(R.id.tvBookId)
    private val ivBookImage:ImageView = view.findViewById(R.id.ivBookImage)
    private val tvBookTitle:TextView = view.findViewById(R.id.tvBookTitle)
    private val tvBookAuthor:TextView = view.findViewById(R.id.tvBookAuthor)

    fun render(book: Book) {

        //tvBookId.text = book.id.toString()
        if (!book.image.isNullOrBlank()) {
            Glide.with(itemView.context)
                .load(book.image)
                .into(ivBookImage)
        }
        tvBookTitle.text = book.title
        tvBookAuthor.text = book.author

    }

}