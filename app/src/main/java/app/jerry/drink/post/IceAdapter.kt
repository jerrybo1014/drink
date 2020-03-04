package app.jerry.drink.post

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemPostIceBinding

class IceAdapter(private val viewModel: PostViewModel) :
    ListAdapter<String, IceAdapter.IceViewHolder>(
        DiffCallback
    ) {

    class IceViewHolder(private var binding: ItemPostIceBinding,
                        private var viewModel: PostViewModel) :
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
            ItemPostIceBinding.inflate(LayoutInflater.from(parent.context), parent, false),viewModel
        )
    }

    override fun onBindViewHolder(holder: IceViewHolder, position: Int) {
        val string = getItem(position)
        holder.bind(string)
    }
}