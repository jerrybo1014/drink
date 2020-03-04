package app.jerry.drink.radar

import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.DrinkApplication
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentRadarBinding
import app.jerry.drink.dataclass.StoreLocation
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.util.Logger
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import kotlin.math.cos


class RadarFragment : Fragment(), GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private val MAPVIEW_BUNDLE_KEY: String? = "MapViewBundleKey"
    lateinit var binding: FragmentRadarBinding
    lateinit var mMap: MapView
    private lateinit var myGoogleMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var markerOld: Marker? = null
    private val viewModel by viewModels<RadarViewModel> {
        getVmFactory(
            RadarFragmentArgs.fromBundle(
                arguments!!
            ).store
        )
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        markerOld?.let {
            it.alpha = 0.5F
        }
        marker?.let {
            markerOld = it
            it.alpha = 1F
            val storeLocation = (it.tag as StoreLocation)
            val store = storeLocation.store
            viewModel.selectStore(storeLocation)
            viewModel.storeCardOpen()
            viewModel.getStoreCommentResult(store)
        }
        return false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_radar, container, false
        )

        (activity as MainActivity).binding.fab.hide()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        (activity as MainActivity).binding.layoutHomeSearch.setOnClickListener {
            findNavController().navigate(R.id.action_global_homeSearchFragment)
        }

        viewModel.storeDrinkStatus.observe(this, Observer {
            binding.radarImageFoldArrow.isSelected = it
        })

        binding.imageCallPhone.setOnClickListener {
            viewModel.selectStore.value?.let {
                callPhone(it.phone)
            }
        }

        binding.imageNavigationToStore.setOnClickListener {

            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient((activity as MainActivity))

            fusedLocationProviderClient.lastLocation.addOnSuccessListener {
                val storeLnt = viewModel.selectStore.value?.latitude
                val storeLon = viewModel.selectStore.value?.longitude
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "http://maps.google.com/maps?"
                                + "saddr=" + it.latitude + "," + it.longitude
                                + "&daddr=" + storeLnt + "," + storeLon
                                + "&avoid=highway"
                                + "&language=zh-CN"
                    )
                )
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                startActivity(intent)
            }
        }

        viewModel.getStoreLocationResult()

        val storeHighScoreAdapter = StoreHighScoreAdapter(StoreHighScoreAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        val radarStoreDrinkAdapter = RadarStoreDrinkAdapter()

        binding.radarRecyclerStoreHighScore.adapter = storeHighScoreAdapter
        binding.radarRecyclerMapDrinkRank.adapter = radarStoreDrinkAdapter

        viewModel.navigationToDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalDetailFragment(it))
                viewModel.storeCardClose()
                viewModel.onDetailNavigated()
            }
        })

        mMap = binding.radarMap
        initGoogleMap(savedInstanceState)

        return binding.root
    }

    private fun initGoogleMap(savedInstanceState: Bundle?) { // *** IMPORTANT ***
// MapView requires that the Bundle you pass contain _ONLY_ MapView SDK
// objects or sub-Bundles.
        var mapViewBundle: Bundle? = null
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }

        mMap.onCreate(mapViewBundle)
        mMap.getMapAsync(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mMap.onSaveInstanceState(mapViewBundle)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap

        myGoogleMap.isMyLocationEnabled = true
        myGoogleMap.uiSettings.isMapToolbarEnabled = false
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient((activity as MainActivity))

        googleMap.setOnMapClickListener {
            viewModel.storeCardClose()
            markerOld?.let {
                it.alpha = 0.5F
            }
        }
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        googleMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                context, R.raw.style_json
            )
        )

        viewModel.storeLocation.observe(this, Observer {
            it?.let {

                fusedLocationProviderClient =
                    LocationServices.getFusedLocationProviderClient((DrinkApplication.context))

                fusedLocationProviderClient.lastLocation.addOnSuccessListener { myLocation ->
                    val queryRadius = 3
                    val newLocation = LatLng(myLocation.latitude, myLocation.longitude)
                    val lat = 0.009043717
                    val lon = 0.008983112 / cos(newLocation.latitude)

                    val lowerLat = myLocation.latitude - (lat * queryRadius)
                    val lowerLon = myLocation.longitude - (lon * queryRadius)

                    val greaterLat = myLocation.latitude + (lat * queryRadius)
                    val greaterLon = myLocation.longitude + (lon * queryRadius)

                    for (storeLocation in it) {

                        val queryResult = LatLng(
                            storeLocation.latitude
                            , storeLocation.longitude
                        )

                        // if (isAdded) to avoid fragment not attach context
//                            val iconDraw =
//                                if (isAdded){

                        val widthIcon = Resources.getSystem().displayMetrics.widthPixels / 10
                        val bitmapDraw =
                            DrinkApplication.instance.getDrawable(R.drawable.drink_map_icon_1)
                        val b = bitmapDraw?.toBitmap()
                        val smallMarker = Bitmap.createScaledBitmap(b!!, widthIcon, widthIcon, false)
                        val iconDraw = BitmapDescriptorFactory.fromBitmap(smallMarker)
//                            }else{

//                            }

//                        if (queryResult.latitude in lowerLat..greaterLat
//                            && queryResult.longitude in lowerLon..greaterLon
//                        ) {


                        val addMarker = googleMap.addMarker(
                            MarkerOptions().position(queryResult)
                                .flat(true)
                                .alpha(0.5F)
//                                .icon(iconDraw)
//                                    .snippet(storeLocation.branchName)
//                                    .title(storeLocation.store.storeName)
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bottom_navigation_home_1).)
//                                .icon(iconDraw)
                        )
                        addMarker.tag = storeLocation
//                        }

                    }
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            newLocation,
                            15.toFloat()
                        )
                    )

                    val circleOptions = CircleOptions()
                    circleOptions.center(newLocation)
                        .radius(queryRadius.toDouble() * 1000)
                        .fillColor(Color.argb(70, 150, 50, 50))
                        .strokeWidth(3F)
                        .strokeColor(Color.RED)
//                    googleMap.addCircle(circleOptions)

                }

            }
        })
        googleMap.setOnMarkerClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        mMap.onResume()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mMap.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mMap.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mMap.onDestroy()
    }

    private fun callPhone(phone: String) {
        val intent = Intent()
        intent.action = Intent.ACTION_DIAL
        intent.data = Uri.parse("tel:$phone")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}