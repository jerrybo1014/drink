package app.jerry.drink.order

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import app.jerry.drink.signin.SignInFragment
import app.jerry.drink.util.Logger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


class OrderFragment : Fragment() {


    private val viewModel by viewModels<OrderVIewModel> {
        getVmFactory(
            OrderFragmentArgs
                .fromBundle(arguments!!).orderId
        )
    }
    lateinit var binding: FragmentOrderBinding
    val MY_PERMISSIONS_LOCATION = 200

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


//        val authListener: FirebaseAuth.AuthStateListener =
//            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
//                val user: FirebaseUser? = auth.currentUser
//
//                if (user == null) {
//                    childFragmentManager.let {
//                        SignInFragment().show(it, "")
//                    }
//                    Logger.d("signInWithCredential:no")
//                } else {
//                    viewModel.checkUserResult()
//                }
//            }
//        FirebaseAuth.getInstance().addAuthStateListener(authListener)





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


        viewModel.navigationToRadar.observe(this, Observer {
            it?.let {

                if (ContextCompat.checkSelfPermission(
                        DrinkApplication.context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {

                    // Permission is not granted
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            activity as MainActivity,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        AlertDialog.Builder(context!!)
                            .setMessage("需要開啟GPS權限，再不給試試看")
                            .setPositiveButton("前往設定") { _, _ ->
                                requestPermissions(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    ),
                                    MY_PERMISSIONS_LOCATION
                                )
                            }
                            .setNegativeButton("NO") { _, _ -> }
                            .show()

                    } else {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            ),
                            MY_PERMISSIONS_LOCATION
                        )
                    }
                } else {
                    // Permission has already been granted
                    findNavController().navigate(NavigationDirections.actionGlobalRadarFragment(it))
                    viewModel.navigationToRadarfinished()
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
            MY_PERMISSIONS_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (permissionsItem in permissions) {
                        Log.d("jerryTest", "permissions allow : $permissions")
                    }
                    findNavController().navigate(NavigationDirections.actionGlobalRadarFragment(viewModel.navigationToRadar.value!!))
                    viewModel.navigationToRadarfinished()
                } else {
                    for (permissionsItem in permissions) {
                        Log.d("jerryTest", "permissions reject : $permissionsItem")
                    }
                }
                return
            }

        }

    }

}