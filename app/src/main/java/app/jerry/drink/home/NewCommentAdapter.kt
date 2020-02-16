package app.jerry.drink.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemNewCommentBinding
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.Store

class NewCommentAdapter(private val onClickListener: OnClickListener ) :
    ListAdapter<Comment, NewCommentAdapter.NewCommentViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drinkDetail: DrinkDetail) -> Unit) {
        fun onClick(drinkDetail: DrinkDetail) = clickListener(drinkDetail)
    }

    class NewCommentViewHolder(private var binding: ItemNewCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment, onClickListener: OnClickListener) {

            val drinkDetail = DrinkDetail(comment.drink
                ,comment.store)
            binding.comment = comment
            binding.root.setOnClickListener { onClickListener.onClick(drinkDetail) }
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