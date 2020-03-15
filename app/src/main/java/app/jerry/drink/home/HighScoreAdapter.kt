package app.jerry.drink.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemHighScoreBinding
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.DrinkRank

class HighScoreAdapter(private val onClickListener: OnClickListener) :
    ListAdapter<DrinkRank, HighScoreAdapter.HighestScoreViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drink: Drink) -> Unit) {
        fun onClick(drink: Drink) = clickListener(drink)
    }

    class HighestScoreViewHolder(private var binding: ItemHighScoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drinkRank: DrinkRank, onClickListener: OnClickListener) {
            val drink = drinkRank.drink
            binding.root.setOnClickListener { onClickListener.onClick(drink) }
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HighestScoreViewHolder {
        return HighestScoreViewHolder(
            ItemHighScoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: HighestScoreViewHolder, position: Int) {
        val drinkRank = getItem(position)
        holder.bind(drinkRank, onClickListener)
    }
}