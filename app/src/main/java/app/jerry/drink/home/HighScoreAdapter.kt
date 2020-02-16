package app.jerry.drink.home

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemHighScoreBinding
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.DrinkRank

class HighScoreAdapter(private val onClickListener: HighScoreAdapter.OnClickListener) :
    ListAdapter<DrinkRank, HighScoreAdapter.HighestScoreViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drinkDetail: DrinkDetail) -> Unit) {
        fun onClick(drinkDetail: DrinkDetail) = clickListener(drinkDetail)
    }

    class HighestScoreViewHolder(private var binding: ItemHighScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drinkRank: DrinkRank, onClickListener: HighScoreAdapter.OnClickListener) {

            val drinkDetail = DrinkDetail(drinkRank.drink
                , drinkRank.store)
            binding.root.setOnClickListener { onClickListener.onClick(drinkDetail) }

            val imageRandom = (Math.random() * drinkRank.commentList.size).toInt()
            Log.d("jerryTest","imageRandom = $imageRandom")
            binding.image = drinkRank.commentList[imageRandom].drinkImage
            binding.drinkRank = drinkRank
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DrinkRank>() {
        override fun areItemsTheSame(
            oldItem: DrinkRank,
            newItem: DrinkRank
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: DrinkRank,
            newItem: DrinkRank
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
        val drinkRank = getItem(position)
        holder.bind(drinkRank, onClickListener)
    }
}