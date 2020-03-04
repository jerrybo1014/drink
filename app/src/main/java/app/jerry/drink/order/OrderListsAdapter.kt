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

class OrderListsAdapter(private val viewModel: OrderViewModel) :
    ListAdapter<OrderItem, OrderListsAdapter.OrderListsHolder>(
        DiffCallback
    ) {

    class OrderListsHolder(private var binding: ItemOrderListsBinding,
                        private var viewModel: OrderViewModel
    ) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(orderItem: OrderItem) {
            binding.orderItem = orderItem
            binding.lifecycleOwner = this
            binding.viewModel = viewModel

            val userCurrent = orderItem.user?.id == viewModel.userCurrent.value?.id
            Log.d("jerryTest","userCurrent = $userCurrent")
            Log.d("jerryTest","viewModel.userCurrent = ${viewModel.userCurrent.value?.id}")
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

    /**
     * It for [LifecycleRegistry] change [onViewAttachedToWindow]
     */
    override fun onViewAttachedToWindow(holder: OrderListsHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is OrderListsHolder -> holder.onAttach()
        }
    }

    /**
     * It for [LifecycleRegistry] change [onViewDetachedFromWindow]
     */
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
        val orderItem = getItem(position)
        holder.bind(orderItem)
    }
}