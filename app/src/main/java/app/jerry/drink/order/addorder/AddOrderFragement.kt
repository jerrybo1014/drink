package app.jerry.drink.order

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentAddOrderBinding
import app.jerry.drink.ext.getVmFactory

class AddOrderFragement : DialogFragment() {

    private val viewModel by viewModels<AddOrderViewModel> { getVmFactory(AddOrderFragementArgs.fromBundle(arguments!!).orderLists) }
    lateinit var binding: FragmentAddOrderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_order, container, false
        )



        return binding.root
    }

}