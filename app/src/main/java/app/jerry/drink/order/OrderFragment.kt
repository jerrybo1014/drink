package app.jerry.drink.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.ext.getVmFactory


class OrderFragment : Fragment() {


    private val viewModel by viewModels<OrderVIewModel> {
        getVmFactory(
            OrderFragmentArgs
                .fromBundle(arguments!!).orderId
        )
    }
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
                CreateOrderFragment().show(it, "")
            }
        }

        val orderListAdapter = OrderListsAdapter(viewModel)
        binding.recyclerOrderList.adapter = orderListAdapter

        binding.buttonAddOrder.setOnClickListener {
            findNavController().navigate(
                NavigationDirections.actionGlobalAddOrderFragement(
                    viewModel.orderLists.value!!
                )
            )
        }

        binding.searchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.getOrderResult(it.toLong())
                }
//                viewModel.getOrderLiveResult(query.toLong())

//                viewModel.orderLive.observe(this@OrderFragment, Observer {
//                    Log.d("viewModel.orderLive", "$it")
//                    orderListAdapter.submitList(it)
//                })
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        }
        )

        viewModel.orderLists.observe(this, Observer {
            viewModel.orderLive.observe(this@OrderFragment, Observer {
                Log.d("viewModel.orderLive", "$it")
                orderListAdapter.submitList(it)
            })
        })

        return binding.root
    }

}