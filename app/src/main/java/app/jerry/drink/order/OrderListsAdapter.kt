package app.jerry.drink.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemOrderListsBinding
import app.jerry.drink.dataclass.OrderList

class OrderListsAdapter(private val viewModel: OrderVIewModel) :
    ListAdapter<OrderList, OrderListsAdapter.OrderListsHolder>(
        DiffCallback
    ) {

    class OrderListsHolder(private var binding: ItemOrderListsBinding,
                        private var viewModel: OrderVIewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(orderList: OrderList) {
            binding.orderList = orderList
//            binding.string = string
//            binding.detailImage = string
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<OrderList>() {
        override fun areItemsTheSame(
            oldItem: OrderList,
            newItem: OrderList
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: OrderList,
            newItem: OrderList
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
    ): OrderListsHolder {
        return OrderListsHolder(
            ItemOrderListsBinding.inflate(LayoutInflater.from(parent.context), parent, false),viewModel
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: OrderListsHolder, position: Int) {
        val orderList = getItem(position)
        holder.bind(orderList)
    }
}