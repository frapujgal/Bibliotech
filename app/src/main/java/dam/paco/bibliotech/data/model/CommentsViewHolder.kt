package dam.paco.bibliotech.data.model

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dam.paco.bibliotech.R

class CommentsViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    private val ivUserImage: ImageView = view.findViewById(R.id.ivUserImage)
    private val tvUsername: TextView = view.findViewById(R.id.tvUsername)
    private val tvComment: TextView = view.findViewById(R.id.tvComment)

    fun render(comment: Comment) {

        if (!comment.user.image.isNullOrBlank()) {
            Glide.with(itemView.context)
                .load(comment.user.image)
                .into(ivUserImage)
        }
        tvUsername.text = comment.user.name
        tvComment.text = comment.comment
    }

}