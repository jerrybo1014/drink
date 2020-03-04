package app.jerry.drink.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemNewCommentBinding
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink

class NewCommentAdapter(private val onClickListener: OnClickListener ) :
    ListAdapter<Comment, NewCommentAdapter.NewCommentViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drink: Drink) -> Unit) {
        fun onClick(drink: Drink) = clickListener(drink)
    }

    class NewCommentViewHolder(private var binding: ItemNewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment, onClickListener: OnClickListener) {

            val drink = comment.drink
            binding.comment = comment
            binding.root.setOnClickListener { onClickListener.onClick(drink) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem == newItem
        }
    }

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewCommentViewHolder {
        return NewCommentViewHolder(
            ItemNewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: NewCommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment, onClickListener)
    }
}