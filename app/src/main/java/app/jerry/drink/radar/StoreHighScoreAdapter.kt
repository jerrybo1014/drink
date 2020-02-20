package app.jerry.drink.radar

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemHighScoreBinding
import app.jerry.drink.databinding.ItemStoreHighScoreBinding
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.DrinkRank

class StoreHighScoreAdapter(private val onClickListener: StoreHighScoreAdapter.OnClickListener) :
    ListAdapter<DrinkRank, StoreHighScoreAdapter.StoreHighScoreViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drinkDetail: DrinkDetail) -> Unit) {
        fun onClick(drinkDetail: DrinkDetail) = clickListener(drinkDetail)
    }

    class StoreHighScoreViewHolder(private var binding: ItemStoreHighScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drinkRank: DrinkRank, onClickListener: StoreHighScoreAdapter.OnClickListener) {

            val drinkDetail = DrinkDetail(drinkRank.drink
                , drinkRank.store)
            binding.root.setOnClickListener { onClickListener.onClick(drinkDetail) }
            val imageRandom = (Math.random() * drinkRank.commentList.size).toInt()
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
    ): StoreHighScoreViewHolder {
        return StoreHighScoreViewHolder(
            ItemStoreHighScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: StoreHighScoreViewHolder, position: Int) {
        val drinkRank = getItem(position)
        holder.bind(drinkRank, onClickListener)
    }
}