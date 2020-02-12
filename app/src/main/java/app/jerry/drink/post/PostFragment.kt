package app.jerry.drink.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.MainActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentPostBinding
import app.jerry.drink.ext.getVmFactory
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_post.*
import java.io.IOException
import java.util.*

class PostFragment : Fragment() {

    lateinit var binding: FragmentPostBinding
    private val viewModel by viewModels<PostViewModel> { getVmFactory() }
    private val PICK_IMAGE_REQUEST = 3
    private var filePath: Uri? = null

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

        viewModel.postFinished.observe(this, Observer {
            if (it){
                findNavController().navigateUp()
            }
        })

        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE



        viewModel.imageUri.observe(this, Observer {
            Log.d("jerryTest","imageUri.observe = $it")
        })


        binding.buttonImageUpdate.setOnClickListener {
            launchGallery()
        }

        return binding.root
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, filePath)
//                uploadImage.setImageBitmap(bitmap)
                Glide.with(this).load(filePath).into(image_update)
                viewModel.imageUri.value = filePath
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
    }

}