package app.jerry.drink

import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_maps)
        setContentView(R.layout.fragment_radar)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        Log.d("mapTest","onCreate")
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.d("mapTest","onMapReady")
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
        val sydney = LatLng(24.730058, 120.909081)
        val sydneys = LatLng(24.727417, 120.910773)
//        val mmm = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        mmm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.addMarker(MarkerOptions().position(sydneys).title("Marker in Sydney"))
        mMap.uiSettings.isZoomControlsEnabled=true
        mMap.uiSettings.isMyLocationButtonEnabled=true
        mMap.isMyLocationEnabled=true
        mMap.uiSettings.isMapToolbarEnabled=true

//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,15F))
    }


}
