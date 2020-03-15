package app.jerry.drink.order.addorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemAddOrderSugarBinding

class SugarAddOrderAdapter(private val viewModel: AddOrderViewModel) :
    ListAdapter<String, SugarAddOrderAdapter.SugarViewHolder>(
        DiffCallback
    ) {

    class SugarViewHolder(private var binding: ItemAddOrderSugarBinding,
                          private var viewModel: AddOrderViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(string: String) {
            binding.string = string
            binding.viewModel = viewModel
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SugarViewHolder {
        return SugarViewHolder(
            ItemAddOrderSugarBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewModel
        )
    }

    override fun onBindViewHolder(holder: SugarViewHolder, position: Int) {
        val string = getItem(position)
        holder.bind(string)
    }
}