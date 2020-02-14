package app.jerry.drink.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentDetailBinding
import app.jerry.drink.ext.getVmFactory

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> { getVmFactory(
        DetailFragmentArgs.fromBundle(
            arguments!!
        ).drinkDetail) }

    lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail, container, false
        )



        return binding.root
    }

}