package app.jerry.drink.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.jerry.drink.MainActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentDetailBinding
import app.jerry.drink.ext.getVmFactory

class DetailFragment : Fragment() {

    private val viewModel by viewModels<DetailViewModel> {
        getVmFactory(
            DetailFragmentArgs.fromBundle(
                arguments!!
            ).drinkDetail
        )
    }

    lateinit var binding: FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail, container, false
        )
        (activity as MainActivity).binding.fab.hide()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recyclerAllComments.adapter = DetailAdapter()

        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE

        binding.layoutNavigationToInternet.setOnClickListener {

            viewModel.drinkInformation.observe(this, Observer { drinkInformation ->
                val uri = Uri.parse(drinkInformation.store?.uri)
                val intent = Intent(Intent.ACTION_VIEW, uri)
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                final ComponentName componentName = intent.resolveActivity(getPackageManager())
                startActivity(Intent.createChooser(intent, "即將開啟官網，請選擇瀏覽器"))
            })
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

}