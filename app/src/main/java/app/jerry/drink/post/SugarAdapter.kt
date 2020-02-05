package app.jerry.drink.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.databinding.ItemPostIceBinding
import app.jerry.drink.databinding.ItemPostSugarBinding

class SugarAdapter :
    ListAdapter<String, SugarAdapter.SugarViewHolder>(
        DiffCallback
    ) {

    class SugarViewHolder(private var binding: ItemPostSugarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(string: String) {
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
            ItemPostSugarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    /**
     * Replaces the contents of a view (invoked by the layout manager)
     */

    override fun onBindViewHolder(holder: SugarViewHolder, position: Int) {
        val comment = getItem(position)
        holder.bind(comment)
    }
}