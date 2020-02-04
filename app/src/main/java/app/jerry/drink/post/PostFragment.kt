package app.jerry.drink.post

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentPostBinding
import app.jerry.drink.getVmFactory

class PostFragment : Fragment() {

    lateinit var binding: FragmentPostBinding
    private val viewModel by viewModels<PostViewModel> { getVmFactory() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_post, container, false
        )

        return binding.root
    }

}