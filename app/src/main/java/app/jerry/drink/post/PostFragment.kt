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


        viewModel.selectedStore.observe(this, Observer {
            Log.d("selectedStore", it.storeName)
            viewModel.getStoreMenuResult(it)
        })
        viewModel.allStoreMenu.observe(this, Observer {
            Log.d("allStoreMenu", "$it")
        })

        return binding.root
    }

}