package app.jerry.drink.homesearch

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemSearchDrinkBinding
import app.jerry.drink.dataclass.Drink

class HomeSearchDrinkAdapter(private val onClickListener: OnClickListener ) :
    ListAdapter<Drink, HomeSearchDrinkAdapter.SearchDrinkViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (drink: Drink) -> Unit) {
        fun onClick(drink: Drink) = clickListener(drink)
    }

    class SearchDrinkViewHolder(private var binding: ItemSearchDrinkBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(drink: Drink, onClickListener: OnClickListener) {
            binding.drink = drink
            binding.root.setOnClickListener { onClickListener.onClick(drink) }
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

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchDrinkViewHolder {
        return SearchDrinkViewHolder(
            ItemSearchDrinkBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SearchDrinkViewHolder, position: Int) {
        val drink = getItem(position)
        holder.bind(drink, onClickListener)
    }
}