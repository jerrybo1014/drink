package app.jerry.drink.homesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemSearchDrinkBinding
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.DrinkDetail

class HomeSearchDrinkAdapter(private val onClickListener: OnClickListener ) :
    ListAdapter<Drink, HomeSearchDrinkAdapter.SearchDrinkViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drinkDetail: DrinkDetail) -> Unit) {
        fun onClick(drinkDetail: DrinkDetail) = clickListener(drinkDetail)
    }

    class SearchDrinkViewHolder(private var binding: ItemSearchDrinkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drink: Drink, onClickListener: OnClickListener) {


            val drinkDetail = DrinkDetail(drink, drink.store)
            binding.drink = drink
            binding.root.setOnClickListener { onClickListener.onClick(drinkDetail) }
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Drink>() {
        override fun areItemsTheSame(
            oldItem: Drink,
            newItem: Drink
        ): Boolean {
            return oldItem.drinkId === newItem.drinkId
        }

        override fun areContentsTheSame(
            oldItem: Drink,
            newItem: Drink
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
    ): SearchDrinkViewHolder {
        return SearchDrinkViewHolder(
            ItemSearchDrinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: SearchDrinkViewHolder, position: Int) {
        val drink = getItem(position)
        holder.bind(drink, onClickListener)
    }
}