package app.jerry.drink.radar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemMapDrinkRankBinding
import app.jerry.drink.dataclass.DrinkRank

class RadarStoreDrinkAdapter:
    ListAdapter<DrinkRank, RadarStoreDrinkAdapter.RadarStoreDrinkViewHolder>(
        DiffCallback
    ) {

//    class OnClickListener(val clickListener: (drinkDetail: DrinkDetail) -> Unit) {
//        fun onClick(drinkDetail: DrinkDetail) = clickListener(drinkDetail)
//    }

    class RadarStoreDrinkViewHolder(private var binding: ItemMapDrinkRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drinkRank: DrinkRank) {

//            val drinkDetail = DrinkDetail(drinkRank.drink
//                , drinkRank.store)
//            binding.root.setOnClickListener { onClickListener.onClick(drinkDetail) }
//            val imageRandom = (Math.random() * drinkRank.commentList.size).toInt()
//            binding.image = drinkRank.commentList[imageRandom].drinkImage
            binding.adapterPosition = adapterPosition + 1
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
    ): RadarStoreDrinkViewHolder {
        return RadarStoreDrinkViewHolder(
            ItemMapDrinkRankBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: RadarStoreDrinkViewHolder, position: Int) {
        val drinkRank = getItem(position)
        holder.bind(drinkRank)
    }
}