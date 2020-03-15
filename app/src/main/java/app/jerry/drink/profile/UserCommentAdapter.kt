package app.jerry.drink.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemUserCommentBinding
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink

class UserCommentAdapter(private val onClickListener: OnClickListener ) :
    ListAdapter<Comment, UserCommentAdapter.UserCommentViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drink: Drink) -> Unit) {
        fun onClick(drink: Drink) = clickListener(drink)
    }

    class UserCommentViewHolder(private var binding: ItemUserCommentBinding) :
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
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Comment,
            newItem: Comment
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserCommentViewHolder {
        return UserCommentViewHolder(
            ItemUserCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: UserCommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment, onClickListener)
    }
}