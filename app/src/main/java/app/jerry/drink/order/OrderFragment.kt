package app.jerry.drink.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.jerry.drink.MainActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.ext.getVmFactory


class OrderFragment : Fragment() {


    private val viewModel by viewModels<OrderVIewModel> { getVmFactory() }
    lateinit var binding: FragmentOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_order, container, false
        )
        (activity as MainActivity).binding.fab.hide()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.textAddOrder.setOnClickListener {
            childFragmentManager.let {
             CreateOrderFragment().show(it,"")
            }
        }

        binding.searchView.setOnQueryTextListener( object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.getOrderResult(query!!.toLong())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        }

        )

        return binding.root
    }

}