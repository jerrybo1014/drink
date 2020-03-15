package app.jerry.drink.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemDetailCommentBinding
import app.jerry.drink.dataclass.Comment

class DetailAdapter(val viewModel: DetailViewModel) :
    ListAdapter<Comment, DetailAdapter.DetailViewHolder>(
        DiffCallback
    ) {

    class DetailViewHolder(private val binding: ItemDetailCommentBinding,
                           private val viewModel: DetailViewModel) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.comment = comment
            binding.viewModel = viewModel
            binding.root.setOnLongClickListener {
                viewModel.deleteComment(comment)
                true
            }
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DetailViewHolder {
        return DetailViewHolder(
            ItemDetailCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        , viewModel)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }
}