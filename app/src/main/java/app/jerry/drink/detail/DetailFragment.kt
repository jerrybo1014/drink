package app.jerry.drink.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
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
import app.jerry.drink.util.PermissionCode
import app.jerry.drink.util.PermissionRequest
import app.jerry.drink.util.Util

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(
                arguments!!
            ).drink
        )
    }
    lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.detailRecyclerAllComments.adapter = DetailAdapter(viewModel)

        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE

        binding.detailLayoutNavigationToInternet.setOnClickListener {
            val uri = Uri.parse(viewModel.drink.store.uri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(Intent.createChooser(intent, Util.getString(R.string.choose_browser)))
        }

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
                        findNavController().navigate(
                            NavigationDirections.actionGlobalRadarFragment(
                                it
                            )
                        )
                        viewModel.navigationToRadarfinished()
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
                    viewModel.navigationToRadarfinished()
                } else {
                    context?.let {
                        PermissionRequest(it, activity as MainActivity).fineLocationIfDenied()
                    }
                }
                return
            }
        }
    }
}