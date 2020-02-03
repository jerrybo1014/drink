package app.jerry.drink.radar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import app.jerry.drink.MainActivity
import app.jerry.drink.MapsActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentRadarBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.SupportMapFragment

class RadarFragment : Fragment() {


    lateinit var binding: FragmentRadarBinding
    lateinit var mMap: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_radar, container, false
        )
//        Intent intent = new Intent();
//        intent.setClass(A.this, B.class);
//        startActivity(intent);
//        (activity as MapsActivity).onMapReady(GoogleMap())

//        val intent = Intent(activity,MapsActivity::class.java)
//        startActivity(intent)

//        GoogleMap map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
//        val map: GoogleMap = (activity as MapsActivity).
//        val map: GoogleMap = FragmentManager().findFragmentById(R.id.map)

//        val map = SupportMapFragment.newInstance()
//        val fragmentTransaction =fragmentManager!!.beginTransaction()
//        fragmentTransaction.add(binding.map.id, map)
//        fragmentTransaction.commit()

        (activity as MainActivity).binding.fab.hide()

        mMap = binding.radarMap
        mMap.onCreate(savedInstanceState)
        mMap.getMapAsync(MapsActivity())

//        MapsInitializer.initialize(activity)
//        val googleMap = mMap.getMapAsync(MapsActivity())

        return binding.root
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