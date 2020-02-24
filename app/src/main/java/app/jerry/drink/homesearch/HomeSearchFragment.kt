package app.jerry.drink.homesearch

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentHomeSearchBinding
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.ext.getVmFactory

class HomeSearchFragment : Fragment() {

    lateinit var binding: FragmentHomeSearchBinding

    private val viewModel by viewModels<HomeSearchViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home_search, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val searchDrinkAdapter = HomeSearchDrinkAdapter(HomeSearchDrinkAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        binding.recyclerSearchDrink.adapter = searchDrinkAdapter

        viewModel.navigationToDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        viewModel.drinkList.observe(this, Observer {
            Log.d("jerryTest", "drinkList = $it")

        })

        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val resultList = mutableListOf<Drink>()

                viewModel.drinkList.value?.let {
                    for (drink in it) {
                        if (!newText.isNullOrEmpty()) {
                            if (drink.drinkName.contains(newText.toString())) {
                                resultList.add(drink)
                            }
                        }
                    }
                    searchDrinkAdapter.submitList(resultList)
                }
                return true
            }
        }
        )
        return binding.root
    }

//    private fun search(keyword: String?){
//        val resultList = mutableListOf<Drink>()
//        for(song in songsList){
//            if(song.songTitle.toLowerCase().contains(keyword.toString())){
//                resultList.add(song)
//            }
//            val adapter = SearchMusicAdapter(viewModel)
//            binding.recyclerViewSearchMusicPage.adapter = adapter
//            adapter.submitList(resultList)
//        }
//    }

}