package app.jerry.drink.detail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import app.jerry.drink.util.Logger
import app.jerry.drink.util.PermissionCode
import app.jerry.drink.util.Util
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        /*hideSoftInputFromWindow*/
        CoroutineScope(Dispatchers.Main).launch {
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as?
                    InputMethodManager)?.hideSoftInputFromWindow(binding.root.windowToken,0)
        }
        
        binding.detailRecyclerAllComments.adapter = DetailAdapter(viewModel)

        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE

        binding.detailLayoutNavigationToInternet.setOnClickListener {
            val uri = Uri.parse(viewModel.drink.store.uri)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(Intent.createChooser(intent, Util.getString(R.string.choose_browser)))
        }

        viewModel.navigationToRadar.observe(this, Observer {
            it?.let { store ->
                context?.let { context ->
                    Util.checkGpsPermissionFragment(context, Util.OnPermissionCheckedListener {
                        findNavController().navigate(
                            NavigationDirections.actionGlobalRadarFragment(
                                store
                            )
                        )
                        viewModel.navigationToRadarfinished()
                        Logger.d(" viewModel.navigationToRadar.value.observe = ${store}")
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
                Logger.d(" viewModel.navigationToRadar.PermissionCode = ${ PermissionCode.LOCATION.requestCode}")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    findNavController().navigate(
                        NavigationDirections.actionGlobalRadarFragment(
                            viewModel.navigationToRadar.value!!
                        )
                    )
                    Logger.d(" viewModel.navigationToRadar.value!! = ${viewModel.navigationToRadar.value!!}")
                    viewModel.navigationToRadarfinished()

                } else {
                    context?.let {
                        Util.openLocationIfDenied(it)

                    }
                }
                return
            }
        }
    }
}