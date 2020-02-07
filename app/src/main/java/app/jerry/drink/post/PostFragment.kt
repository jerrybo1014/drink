package app.jerry.drink.post

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.MainActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentPostBinding
import app.jerry.drink.ext.getVmFactory

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        viewModel.getAllStoreResult()


        binding.spinnerStore.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                    viewModel.selectedStore(position)
            }
        }

        binding.spinnerDrink.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.selectedDrink(position)
            }
        }


        val listIce = listOf<String>("正常冰","去冰","微冰","常溫")
        val listSugar = listOf<String>("正常糖","半糖","微糖","無糖")
        val sugarAdapter = SugarAdapter(viewModel)
        val iceAdapter = IceAdapter(viewModel)

        binding.recyclerIce.adapter = iceAdapter
        binding.recyclerSugar.adapter = sugarAdapter

        sugarAdapter.submitList(listSugar)
        iceAdapter.submitList(listIce)


        binding.ratingBarComment.setOnRatingBarChangeListener { ratingBar, fl, b ->
            viewModel.commentStar.value = fl.toInt()
        }



        viewModel.selectedStore.observe(this, Observer {
            Log.d("selectedStore", it.storeName)
            viewModel.getStoreMenuResult(it)
        })
        viewModel.allStoreMenu.observe(this, Observer {
            viewModel.selectedDrink(0)
            Log.d("allStoreMenu", "$it")
        })

        viewModel.postFinshed.observe(this, Observer {
            if (it){
                findNavController().navigateUp()
            }
        })

        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

}