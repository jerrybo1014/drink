package app.jerry.drink.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemUserOrderBinding
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.Order
import app.jerry.drink.home.NewCommentAdapter

class UserOrderAdapter(private val onClickListener: UserOrderAdapter.OnClickListener) :
    ListAdapter<Order, UserOrderAdapter.UserOrderViewHolder>(
        DiffCallback
    ) {

    class OnClickListener(val clickListener: (orderId: String) -> Unit) {
        fun onClick(orderId: String) = clickListener(orderId)
    }

    class UserOrderViewHolder(private var binding: ItemUserOrderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, onClickListener: UserOrderAdapter.OnClickListener) {


            binding.textNavigationToOrder.setOnClickListener { onClickListener.onClick(order.id) }
            binding.order = order


//            binding.root.setOnClickListener { onClickListener.onClick(drinkDetail) }
//            binding.detailImage = string
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(
            oldItem: Order,
            newItem: Order
        ): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(
            oldItem: Order,
            newItem: Order
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
    ): UserOrderViewHolder {
        return UserOrderViewHolder(
            ItemUserOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: UserOrderViewHolder, position: Int) {
        val order = getItem(position)
        holder.bind(order, onClickListener)
    }
}