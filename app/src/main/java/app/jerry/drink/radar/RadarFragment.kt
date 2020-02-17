package app.jerry.drink.radar

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.jerry.drink.MainActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentRadarBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlin.math.cos


class RadarFragment : Fragment(), OnMapReadyCallback {

    val MAPVIEW_BUNDLE_KEY: String? = "MapViewBundleKey"
    lateinit var binding: FragmentRadarBinding
    lateinit var mMap: MapView
    private lateinit var myGoogleMap: GoogleMap
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_radar, container, false
        )

        (activity as MainActivity).binding.fab.hide()

        mMap = binding.radarMap
        initGoogleMap(savedInstanceState)
        Log.d("myMapTest", "onCreateView")
//        mMap.onCreate(savedInstanceState)
//        mMap.getMapAsync(MapsActivity())

//        MapsInitializer.initialize(activity)
//        val googleMap = mMap.getMapAsync(MapsActivity())
//        val myLocation = (activity as MainActivity).getMyLocation()
//        val location = LatLng(myLocation!!.latitude,myLocation.longitude)

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
        mMap!!.onSaveInstanceState(mapViewBundle)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap

        val lan1 = LatLng(25.039321, 121.567173)  //50嵐 微風松高店
        val lan2 = LatLng(25.049595, 121.559667)  //50嵐 八德店
        val lan3 = LatLng(25.028835, 121.565513)  //50嵐 莊敬店
//myGoogleMap.addCircle(1)
//        val mmm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        mmm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        myGoogleMap.uiSettings.isZoomControlsEnabled=true
        myGoogleMap.uiSettings.isMyLocationButtonEnabled=true
        myGoogleMap.uiSettings.isIndoorLevelPickerEnabled= true
        myGoogleMap.isMyLocationEnabled=true

        myGoogleMap.uiSettings.isMapToolbarEnabled=true

//        val myLocation = (activity as MainActivity).getMyLocation()
//        val location: LatLng? = LatLng(myLocation!!.latitude,myLocation.longitude)
        val location: LatLng? = LatLng(25.039321, 121.567173)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((activity as MainActivity) )
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            Log.d("fusedLocationProviderClient", "${it.latitude}")
        }

//        val myLocation = myLocationService.
        binding.button1m.setOnClickListener {
            queryMapNear(location,1, myGoogleMap)
        }

        binding.button2m.setOnClickListener {
            queryMapNear(location,2, myGoogleMap)
        }

        binding.button3m.setOnClickListener {
            queryMapNear(location,3, myGoogleMap)
        }

//        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,15F))
        queryMapNear(location,1, myGoogleMap)
    }


    private fun queryMapNear(myLocation: LatLng?, queryRadius: Int, googleMap: GoogleMap) {
        googleMap.clear()
        val lan1 = LatLng(25.039321, 121.567173)  //50嵐 微風松高店

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient((activity as MainActivity) )
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {
            val newLocation = LatLng(it.latitude,it.longitude)
            Log.d("fusedLocationProviderClient", "${it.latitude}")
            val lat = 0.009043717
            val lon = 0.008983112/cos(newLocation.latitude)

            val lowerLat = it.latitude - (lat * queryRadius)
            val lowerLon = it.longitude - (lon * queryRadius)

            val greaterLat = it.latitude + (lat * queryRadius)
            val greaterLon = it.longitude + (lon * queryRadius)


            val lesserGeopoint = GeoPoint(lowerLat, lowerLon)
            val greaterGeopoint = GeoPoint(greaterLat, greaterLon)

            val db = FirebaseFirestore.getInstance()
            db.collection("stores")
                .document("50lan")
                .collection("branch")
                .whereGreaterThan("geo",lesserGeopoint)
                .whereLessThan("geo",greaterGeopoint)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        Log.d("queryMapNear", "${document.id} => ${document.data}")
                        Log.d("queryMapNear", "${document.id} => ${document.getGeoPoint("geo")!!.latitude}")
                        document.getGeoPoint("geo")?.let {

                            //icon size
                            val height = 100
                            val width = 100

                            // if (isAdded) to avoid fragment not attach context
                            @Suppress("DEPRECATION")
                            val iconDraw = if (isAdded){
                                val bitmapdraw = resources.getDrawable(R.drawable.drink_map_icon_1)
                                val b = bitmapdraw.toBitmap()
                                val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
                                BitmapDescriptorFactory.fromBitmap(smallMarker)
                            }else{
                                BitmapDescriptorFactory.defaultMarker()
                            }

                            val queryResult = LatLng(it.latitude,it.longitude)
                            googleMap.addMarker(MarkerOptions().position(queryResult)
                                .title("${document.data["branchName"]}")
                                .snippet("${document.data["storeName"]}")
//                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bottom_navigation_home_1))
                                .icon(iconDraw)
                                )
                        }
                    }
                    val cameraRatio = 16F - queryRadius
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newLocation,cameraRatio.toFloat()))
                    val circleOptions = CircleOptions()
                    circleOptions.center(newLocation)
                        .radius(queryRadius.toDouble()*1000)
                        .fillColor(Color.argb(70,150,50,50))
                        .strokeWidth(3F)
                        .strokeColor(Color.RED)
                    googleMap.addCircle(circleOptions)
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds())
//                val aa = LatLngBounds(lan1,lan1)
//                googleMap.setLatLngBoundsForCameraTarget(aa)
                }
                .addOnFailureListener { exception ->
                    Log.w("queryMapNear", "Error getting documents.", exception)
                }


        }

//        val lat = 0.009043717
//        val lon = 0.008983112/cos(myLocation.latitude)
//
//        val lowerLat = myLocation.latitude - (lat * queryRadius)
//        val lowerLon = myLocation.longitude - (lon * queryRadius)
//
//        val greaterLat = myLocation.latitude + (lat * queryRadius)
//        val greaterLon = myLocation.longitude + (lon * queryRadius)
//
//        val lesserGeopoint = GeoPoint(lowerLat, lowerLon)
//        val greaterGeopoint = GeoPoint(greaterLat, greaterLon)
//
//        val db = FirebaseFirestore.getInstance()
//        db.collection("stores")
//            .document("50lan")
//            .collection("branch")
//            .whereGreaterThan("geo",lesserGeopoint)
//            .whereLessThan("geo",greaterGeopoint)
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d("queryMapNear", "${document.id} => ${document.data}")
//                    Log.d("queryMapNear", "${document.id} => ${document.getGeoPoint("geo")!!.latitude}")
//                    document.getGeoPoint("geo")?.let {
//                        val queryResult = LatLng(it.latitude,it.longitude)
//                        googleMap.addMarker(MarkerOptions().position(queryResult).title("${document.data["name"]}"))
//                    }
//                }
//                val cameraRatio = 16F - queryRadius
//                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,cameraRatio))
//                val circleOptions = CircleOptions()
//                circleOptions.center(myLocation)
//                    .radius(queryRadius.toDouble()*1000)
//                    .fillColor(Color.argb(70,150,50,50))
//                    .strokeWidth(3F)
//                    .strokeColor(Color.RED)
//                googleMap.addCircle(circleOptions)
////                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds())
////                LatLngBounds
////                googleMap.setLatLngBoundsForCameraTarget()
//            }
//            .addOnFailureListener { exception ->
//                Log.w("queryMapNear", "Error getting documents.", exception)
//            }
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


}

//class RadarFragment : Fragment() {
//
//
//    lateinit var binding: FragmentRadarBinding
//    lateinit var mMap: MapView
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        binding = DataBindingUtil.inflate(
//            inflater, R.layout.fragment_radar, container, false
//        )
//
//        (activity as MainActivity).binding.fab.hide()
//
//        mMap = binding.radarMap
//        mMap.onCreate(savedInstanceState)
//        mMap.getMapAsync(MapsActivity())
////        MapsInitializer.initialize(activity)
////        val googleMap = mMap.getMapAsync(MapsActivity())
//
//        return binding.root
//    }
//
//    override fun onResume() {
//        super.onResume()
//        mMap.onResume()
//    }
//
//    override fun onLowMemory() {
//        super.onLowMemory()
//        mMap.onLowMemory()
//    }
//
//    override fun onPause() {
//        super.onPause()
//        mMap.onPause()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mMap.onDestroy()
//    }
//
//}