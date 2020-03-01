package app.jerry.drink.detail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
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
import app.jerry.drink.databinding.FragmentDetailBinding
import app.jerry.drink.dataclass.Store
import app.jerry.drink.ext.getVmFactory

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(
                arguments!!
            ).drinkDetail
        )
    }

    lateinit var binding: FragmentDetailBinding
    val MY_PERMISSIONS_LOCATION = 300

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerAllComments.adapter = DetailAdapter()

        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE

        binding.layoutNavigationToInternet.setOnClickListener {

//            viewModel.drinkInformation.observe(this, Observer { drinkInformation ->
                val uri = Uri.parse(viewModel.drinkInformation.value?.store?.uri)
                val intent = Intent(Intent.ACTION_VIEW, uri)
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                final ComponentName componentName = intent.resolveActivity(getPackageManager())
                startActivity(Intent.createChooser(intent, "即將開啟官網，請選擇瀏覽器"))
//            })
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




    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

}