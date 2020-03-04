package app.jerry.drink.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemUserCommentBinding
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.Store

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
//            binding.detailImage = string
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
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

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserCommentViewHolder {
        return UserCommentViewHolder(
            ItemUserCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: UserCommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment, onClickListener)
    }
}