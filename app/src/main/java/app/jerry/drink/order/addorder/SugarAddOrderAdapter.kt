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
//            binding.detailImage = string
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
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

    /**
     * Create new [RecyclerView] item views (invoked by the layout manager)
     */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SugarViewHolder {
        return SugarViewHolder(
            ItemAddOrderSugarBinding.inflate(LayoutInflater.from(parent.context), parent, false), viewModel
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: SugarViewHolder, position: Int) {
        val string = getItem(position)
        holder.bind(string)
    }
}