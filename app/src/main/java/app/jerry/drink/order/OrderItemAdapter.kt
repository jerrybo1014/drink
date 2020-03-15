package app.jerry.drink.order

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemOrderListsBinding
import app.jerry.drink.dataclass.OrderItem
import app.jerry.drink.signin.UserManager

class OrderItemAdapter(private val viewModel: OrderViewModel) :
    ListAdapter<OrderItem, OrderItemAdapter.OrderListsHolder>(
        DiffCallback
    ) {

    class OrderListsHolder(
        private var binding: ItemOrderListsBinding,
        private var viewModel: OrderViewModel
    ) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(orderItem: OrderItem) {
            binding.orderItem = orderItem
            binding.lifecycleOwner = this
            binding.viewModel = viewModel
            val userCurrent = UserManager.user?.let { userCurrent ->
                orderItem.user?.let {
                    userCurrent.id == it.id
                }
            }
            binding.userCurrent = userCurrent
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.CREATED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }

    override fun onViewAttachedToWindow(holder: OrderListsHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is OrderListsHolder -> holder.onAttach()
        }
    }

    override fun onViewDetachedFromWindow(holder: OrderListsHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is OrderListsHolder -> holder.onDetach()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<OrderItem>() {
        override fun areItemsTheSame(
            oldItem: OrderItem,
            newItem: OrderItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: OrderItem,
            newItem: OrderItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderListsHolder {
        return OrderListsHolder(
            ItemOrderListsBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: OrderListsHolder, position: Int) {
        val orderItem = getItem(position)
        holder.bind(orderItem)
    }
}