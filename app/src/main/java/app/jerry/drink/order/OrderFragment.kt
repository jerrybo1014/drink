package app.jerry.drink.order

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.order.createorder.CreateOrderFragment
import app.jerry.drink.util.Logger
import app.jerry.drink.util.PermissionCode
import app.jerry.drink.util.Util

class OrderFragment : Fragment() {

    private val viewModel by viewModels<OrderViewModel> {
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

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.orderImageCreateOrder.setOnClickListener {
            childFragmentManager.let {
                CreateOrderFragment().show(it, Util.getString(R.string.create_order_dialog))
            }
        }

        Logger.d("OrderRecord = ${OrderRecord.orderId}")

        val orderListAdapter = OrderItemAdapter(viewModel)
        binding.orderRecyclerOrderList.adapter = orderListAdapter

        binding.orderButtonAddOrder.setOnClickListener {
            findNavController().navigate(
                NavigationDirections.actionGlobalAddOrderFragement(
                    viewModel.orderLive.value!!
                )
            )
        }

        binding.orderSearchView.setOnQueryTextListener(object : OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.getOrderResult(it.toLong())
                    findNavController().navigate(NavigationDirections.actionGlobalOrderFragment(it))
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })

        viewModel.orderLive.observe(this, Observer {
            binding.order = it
            viewModel.orderItemsLive.observe(this@OrderFragment, Observer { orderItemsLive ->
                orderListAdapter.submitList(orderItemsLive)
            })
        })

        binding.orderLayoutRecord.setOnClickListener {
            viewModel.orderRecord.value?.let {
                findNavController().navigate(NavigationDirections.actionGlobalOrderFragment(it))
            }
        }

        binding.orderImageShare.setOnClickListener {
            val shareIntent = Intent()
            val orderId = viewModel.orderLive.value?.id
            val content = "http://drink.jerry1014.com/order?id=$orderId"
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.putExtra(Intent.EXTRA_TEXT, content)
            shareIntent.type = "text/plain"
            startActivity(Intent.createChooser(shareIntent, Util.getString(R.string.share_order)))
        }

        viewModel.orderLive.observe(this, Observer {
            viewModel.checkIfUsersAreTheSame()
        })

        viewModel.navigationToRadar.observe(this, Observer {
            it?.let { store ->
                context?.let { context ->
                    Util.checkGpsPermissionFragment(context, Util.OnPermissionCheckedListener {
                        findNavController().navigate(
                            NavigationDirections.actionGlobalRadarFragment(
                                store
                            )
                        )
                        viewModel.navigationToRadarFinished()
                    },this)
                }
            }
        })
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionCode.LOCATION.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findNavController().navigate(
                        NavigationDirections.actionGlobalRadarFragment(
                            viewModel.navigationToRadar.value!!
                        )
                    )
                    viewModel.navigationToRadarFinished()
                }else{
                    context?.let { Util.openLocationIfDenied(it) }
                }
                return
            }
        }
    }

}