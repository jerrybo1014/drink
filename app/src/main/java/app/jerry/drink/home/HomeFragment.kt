package app.jerry.drink.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentHomeBinding
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.User
import app.jerry.drink.ext.getVmFactory

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        (activity as MainActivity).binding.layoutHomeSearch.setOnClickListener {
            findNavController().navigate(R.id.action_global_homeSearchFragment)
            Log.d("jerryTest", "layoutHomeSearch")
        }

        (activity as MainActivity).binding.fab.show()

        val highScoreAdapter = HighScoreAdapter(HighScoreAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        val newCommentAdapter = NewCommentAdapter(NewCommentAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerHighScore.adapter = highScoreAdapter
        binding.recyclerNewComment.adapter = newCommentAdapter

        viewModel.navigationToDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        viewModel.newComment.observe(this, Observer {
            Log.d("newComment", "$it")
        })

        return binding.root
    }

}