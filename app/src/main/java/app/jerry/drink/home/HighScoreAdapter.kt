package app.jerry.drink.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemHighScoreBinding
import app.jerry.drink.dataclass.Comment

class HighScoreAdapter :
    ListAdapter<Comment, HighScoreAdapter.HighestScoreViewHolder>(
        DiffCallback
    ) {

    class HighestScoreViewHolder(private var binding: ItemHighScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
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
    ): HighestScoreViewHolder {
        return HighestScoreViewHolder(
            ItemHighScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: HighestScoreViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }
}