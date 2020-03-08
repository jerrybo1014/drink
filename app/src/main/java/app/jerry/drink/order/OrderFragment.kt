package app.jerry.drink.order

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.DrinkApplication
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.util.Logger
import app.jerry.drink.util.PermissionCode
import app.jerry.drink.util.PermissionRequest
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
            it?.let {
                val locationManager = (activity as MainActivity).getSystemService(Context.LOCATION_SERVICE) as LocationManager
                val hasGps =
                    locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                if (!hasGps){
                    context?.let { context ->
                        PermissionRequest(context, activity as MainActivity).showDialogIfLocationServiceOff()
                    }
                }else {
                    if (ContextCompat.checkSelfPermission(
                            DrinkApplication.context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            PermissionCode.LOCATION.requestCode
                        )
                    } else {
                        // Permission has already been granted
                        findNavController().navigate(
                            NavigationDirections.actionGlobalRadarFragment(
                                it
                            )
                        )
                        viewModel.navigationToRadarFinished()
                    }
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
                    context?.let {
                        PermissionRequest(it, activity as MainActivity).fineLocationIfDenied()
                    }
                }
                return
            }
        }
    }

}