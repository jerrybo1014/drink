package app.jerry.drink.order

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.DrinkApplication
import app.jerry.drink.databinding.ItemOrderListsBinding
import app.jerry.drink.dataclass.OrderList

class OrderListsAdapter(private val viewModel: OrderVIewModel) :
    ListAdapter<OrderList, OrderListsAdapter.OrderListsHolder>(
        DiffCallback
    ) {

    class OrderListsHolder(private var binding: ItemOrderListsBinding,
                        private var viewModel: OrderVIewModel
    ) :
        RecyclerView.ViewHolder(binding.root), LifecycleOwner {

        fun bind(orderList: OrderList) {
            binding.orderList = orderList
            binding.lifecycleOwner = this
            binding.viewModel = viewModel

//            val clipboardManager = ClipboardManager.OnPrimaryClipChangedListener {
//
//            }

            val userCurrent = orderList.user?.id == viewModel.userCurrent.value?.id
            Log.d("jerryTest","OrderListsHolder = $userCurrent")
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

    companion object DiffCallback : DiffUtil.ItemCallback<OrderList>() {
        override fun areItemsTheSame(
            oldItem: OrderList,
            newItem: OrderList
        ): Boolean {
            return oldItem.id == newItem.id
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