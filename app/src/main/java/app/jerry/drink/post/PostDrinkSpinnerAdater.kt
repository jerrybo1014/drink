package app.jerry.drink.post

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import app.jerry.drink.databinding.ItemPostDrinkSpinnerBinding
import app.jerry.drink.databinding.ItemPostStoreSpinnerBinding
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Store

class PostDrinkSpinnerAdater(private val strings: List<Drink>) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding = ItemPostDrinkSpinnerBinding.inflate(LayoutInflater.from(parent?.context), parent, false)

        if(position < strings.size){
            binding.title = strings[position].drinkName
        }else{
            binding.title = "新增飲品+"
        }

        return binding.root
    }

    override fun getItem(position: Int): Any {
        return if(position <= strings.size){
            strings[position]
        }else{
            "新增飲品"
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return strings.size + 1
    }
}