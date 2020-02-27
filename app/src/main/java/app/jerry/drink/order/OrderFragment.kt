package app.jerry.drink.order

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
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

        binding.imageAddOrder.setOnClickListener {
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
                    findNavController().navigate(NavigationDirections.actionGlobalOrderFragment(it))
                    Log.d("jerryTest","onQueryTextSubmit = $it")
                }


//                viewModel.orderLists.observe(this@OrderFragment, Observer {
//                    binding.orderLists = it
//                    viewModel.orderLive.observe(this@OrderFragment, Observer {orderLive->
//                        Log.d("viewModel.orderLive", "$orderLive")
//                        orderListAdapter.submitList(orderLive)
//                    })
//                })
//                viewModel.getOrderLiveResult(query.toLong())

//                viewModel.orderLists.observe(this@OrderFragment, Observer {
//                    binding.orderLists = it
//                    Log.d("viewModel.orderLists", "$it")
//
//                    viewModel.orderLive.observe(this@OrderFragment, Observer {orderLive->
//                        Log.d("viewModel.orderLive", "$orderLive")
//                        orderListAdapter.submitList(orderLive)
//                    })
//
//                })

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        }
        )

        viewModel.orderLists.observe(this, Observer {
            binding.orderLists = it
            Log.d("viewModel.orderLists", "$it")

            viewModel.orderLive.observe(this@OrderFragment, Observer {orderLive->
                Log.d("viewModel.orderLive", "$orderLive")
                orderListAdapter.submitList(orderLive)
            })

        })


        binding.imageShare.setOnClickListener {
            val cn = ComponentName(
                "jp.naver.line.android",
                "jp.naver.line.android.activity.selectchat.SelectChatActivityLaunchActivity"
            )
            val shareIntent = Intent()

                val orderId = viewModel.orderLists.value?.order?.id
                val content = "http://drink.jerry1014.com/order?id=$orderId"
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.putExtra(Intent.EXTRA_TEXT, content)
                shareIntent.type = "text/plain"
//            shareIntent.component = cn
//            startActivity(shareIntent)
                startActivity(Intent.createChooser(shareIntent, "分享訂單"))



//            val content = "http://drink.jerry1014.com/order?id=1582002591055"
//            val scheme = "line://msg/text/$content"
//            val uri = Uri.parse(scheme)
////            startActivity(Intent (Intent.ACTION_VIEW, uri))
//            startActivity(Intent.createChooser(Intent(Intent.ACTION_VIEW, uri),""))
        }


        return binding.root
    }

}