package app.jerry.drink.post

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import app.jerry.drink.databinding.ItemPostStoreSpinnerBinding
import app.jerry.drink.dataclass.Store

class PostStoreSpinnerAdapter(private val strings: List<Store>) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemPostStoreSpinnerBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        binding.title = strings[position].storeName
        return binding.root
    }

    override fun getItem(position: Int): Any {
        return strings[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return strings.size
    }
}