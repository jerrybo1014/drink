package app.jerry.drink.home

import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import app.jerry.drink.util.Logger
import com.crashlytics.android.Crashlytics
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        /*hideSoftInputFromWindow*/
        CoroutineScope(Dispatchers.Main).launch {
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(binding.root.windowToken,0)
        }

        (activity as MainActivity).binding.layoutHomeSearch.setOnClickListener {
            findNavController().navigate(R.id.action_global_homeSearchFragment)
        }

        val highScoreAdapter = HighScoreAdapter(HighScoreAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        val newCommentAdapter = NewCommentAdapter(NewCommentAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        binding.homeRecyclerHighScore.adapter = highScoreAdapter
        binding.homeRecyclerNewComment.adapter = newCommentAdapter

        viewModel.navigationToDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        binding.layoutSwipeRefreshHome.setOnRefreshListener {
            viewModel.refresh()
        }

        viewModel.refreshStatus.observe(this, Observer {
            it?.let {
                binding.layoutSwipeRefreshHome.isRefreshing = it
            }
        })

        return binding.root
    }
}