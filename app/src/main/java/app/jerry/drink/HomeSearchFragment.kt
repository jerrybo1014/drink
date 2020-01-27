package app.jerry.drink

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.jerry.drink.databinding.FragmentHomeSearchBinding

class HomeSearchFragment : Fragment() {

    lateinit var binding: FragmentHomeSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_search, container, false
        )


        return binding.root
    }

}