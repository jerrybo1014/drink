package app.jerry.drink.homesearch

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.input.InputManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentHomeSearchBinding
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.ext.getVmFactory
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

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
//        binding.editTextSearch.requestFocus()
        binding.editTextSearch.showSoftInputOnFocus= true
//        binding.editTextSearch.hasWindowFocus()
//        binding.editTextSearch.requestFocusFromTouch()
//        (activity as MainActivity).window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        CoroutineScope(Dispatchers.Main).launch {
            binding.editTextSearch.requestFocus()
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_NOT_ALWAYS)
        }

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

//        binding.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                val resultList = mutableListOf<Drink>()
//
//                viewModel.drinkList.value?.let {
//                    for (drink in it) {
//                        if (!newText.isNullOrEmpty()) {
//                            if (drink.drinkName.contains(newText.toString())) {
//                                resultList.add(drink)
//                            }
//                        }
//                    }
//                    searchDrinkAdapter.submitList(resultList)
//                }
//                return true
//            }
//        }
//        )

        viewModel.searchEditText.observe(this, Observer {searchEditText->
            Log.d("jerryTest","searchEditText = $searchEditText")
            val resultList = mutableListOf<Drink>()
            viewModel.drinkList.value?.let {
                for (drink in it) {
                    if (!searchEditText.isNullOrEmpty()) {
                        if (drink.drinkName.toLowerCase(Locale.getDefault()).contains(searchEditText.toString())
                            || drink.store.storeName.toLowerCase(Locale.getDefault()).contains(searchEditText.toString())) {
                            resultList.add(drink)
                        }
                    }
                }
                searchDrinkAdapter.submitList(resultList)
            }
        })

        binding.imageBack.setOnClickListener {
            findNavController().navigateUp()
        }



        return binding.root
    }


}