package app.jerry.drink

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.databinding.FragmentRadarBinding

class RadarFragment : Fragment() {


    lateinit var binding: FragmentRadarBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_radar, container, false
        )

        return binding.root
    }

}