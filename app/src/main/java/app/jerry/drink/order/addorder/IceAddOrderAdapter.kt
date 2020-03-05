package app.jerry.drink.order.addorder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemAddOrderIceBinding

class IceAddOrderAdapter(private val viewModel: AddOrderViewModel) :
    ListAdapter<String, IceAddOrderAdapter.IceViewHolder>(
        DiffCallback
    ) {

    class IceViewHolder(private var binding: ItemAddOrderIceBinding,
                        private var viewModel: AddOrderViewModel
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(string: String) {
            binding.string = string
            binding.viewHolder = this
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
    ): IceViewHolder {
        return IceViewHolder(
            ItemAddOrderIceBinding.inflate(LayoutInflater.from(parent.context), parent, false),viewModel
        )
    }

    override fun onBindViewHolder(holder: IceViewHolder, position: Int) {
        val string = getItem(position)
        holder.bind(string)
    }
}