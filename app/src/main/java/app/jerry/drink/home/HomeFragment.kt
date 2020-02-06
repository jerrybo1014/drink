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
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentHomeBinding
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.User
import app.jerry.drink.getVmFactory
import app.jerry.drink.post.PostViewModel

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
            Log.d("jerryTest","layoutHomeSearch")
        }

        (activity as MainActivity).binding.fab.show()

        val highScoreAdapter = HighScoreAdapter()
        val newCommentAdapter = NewCommentAdapter()

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerHighScore.adapter = highScoreAdapter
        binding.recyclerNewComment.adapter = newCommentAdapter

        val mockData = mutableListOf<Comment>()
        val comment = Comment("",User("","","","")
            ,"","","","","","","",1,"","","")
        mockData.add(comment)
        mockData.add(comment)
        mockData.add(comment)
        mockData.add(comment)
//        mockData.add(comment)
//
        highScoreAdapter.submitList(mockData)
//        newCommentAdapter.submitList(mockData)

        viewModel.getNewCommentResult()

        viewModel.newComment.observe(this, Observer {
            Log.d("newComment","$it")
        })

        return binding.root
    }

}