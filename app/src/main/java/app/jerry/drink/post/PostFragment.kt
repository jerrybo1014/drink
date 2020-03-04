package app.jerry.drink.post

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.DrinkApplication
import app.jerry.drink.MainActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentPostBinding
import app.jerry.drink.ext.getBitmap
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.ext.setImage
import app.jerry.drink.util.Logger
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_post.*
import java.io.File
import java.io.IOException
import java.util.*

class PostFragment : Fragment() {

    lateinit var binding: FragmentPostBinding
    private val PICK_IMAGE_REQUEST = 3
    private val MY_PERMISSIONS_CAMERA = 10
    private val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 12
    private var filePath: Uri? = null
    private val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String
    private var photoURI: Uri? = null

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

        val listIce = listOf("正常冰", "去冰", "微冰", "常溫")
        val listSugar = listOf("正常糖", "半糖", "微糖", "無糖")
        val sugarAdapter = SugarAdapter(viewModel)
        val iceAdapter = IceAdapter(viewModel)

        binding.postRecyclerIce.adapter = iceAdapter
        binding.postRecyclerSugar.adapter = sugarAdapter

        sugarAdapter.submitList(listSugar)
        iceAdapter.submitList(listIce)

        binding.postRatingBarComment.setOnRatingBarChangeListener { ratingBar, fl, b ->
            viewModel.commentStar.value = fl.toInt()
        }

        viewModel.allStore.observe(this, Observer {
            binding.postSpinnerStore.adapter = PostStoreSpinnerAdapter(it)
        })

        viewModel.selectedStore.observe(this, Observer {
            viewModel.getStoreMenuResult(it)
        })

        viewModel.allStoreMenu.observe(this, Observer {
            binding.postSpinnerDrink.adapter = PostDrinkSpinnerAdapter(it)
        })

        viewModel.postFinished.observe(this, Observer {
            if (it) {
                findNavController().navigateUp()
            }
        })

        binding.layoutGallery.setOnClickListener {
            loadGallery()
            viewModel.closeCameraGallery()
        }

        binding.layoutCamera.setOnClickListener {
            loadCamera()
            viewModel.closeCameraGallery()
        }

        return binding.root
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context!!.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context!!,
                        "app.jerry.drink.fileprovider",
                        it
                    )
                    this.photoURI = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    private fun loadGallery() {

        if (ContextCompat.checkSelfPermission(
                DrinkApplication.instance,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (activity as MainActivity),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(context!!)
                    .setMessage("需要開啟內部存取權限，再不給試試看")
                    .setPositiveButton("前往設定") { _, _ ->
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            ),
                            MY_PERMISSIONS_READ_EXTERNAL_STORAGE
                        )
                    }
                    .setNegativeButton("NO") { _, _ -> }
                    .show()

            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    MY_PERMISSIONS_READ_EXTERNAL_STORAGE
                )
            }
        } else {
            launchGallery()
        }
    }


    private fun loadCamera() {

        if (ContextCompat.checkSelfPermission(
                DrinkApplication.instance,
                Manifest.permission.CAMERA
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    (activity as MainActivity),
                    Manifest.permission.CAMERA
                )
            ) {
                AlertDialog.Builder(context!!)
                    .setMessage("需要開啟相機，再不給試試看")
                    .setPositiveButton("前往設定") { _, _ ->
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.CAMERA
                            ),
                            MY_PERMISSIONS_CAMERA
                        )
                    }
                    .setNegativeButton("NO") { _, _ -> }
                    .show()

            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.CAMERA
                    ),
                    MY_PERMISSIONS_CAMERA
                )
            }
        } else {
            dispatchTakePictureIntent()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_CAMERA -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (permissionsItem in permissions) {
                        Logger.d("permissions allow : $permissions")
                    }
                    loadCamera()
                } else {
                    for (permissionsItem in permissions) {
                        Logger.d("permissions reject : $permissions")
                    }
                }
                return
            }

            MY_PERMISSIONS_READ_EXTERNAL_STORAGE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (permissionsItem in permissions) {
                        Logger.d("permissions allow : $permissions")
                    }
                    launchGallery()
                } else {
                    for (permissionsItem in permissions) {
                        Logger.d("permissions reject : $permissions")
                    }
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            data.data?.let {
                filePath = it
                setImage(binding.postImageUpdate, it)
                val bitmap = filePath?.getBitmap(binding.postImageUpdate.width, binding.postImageUpdate.height)
                viewModel.imageBitmap.value = bitmap
            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            data.data?.let {
                photoURI = it
                setImage(binding.postImageUpdate, it)
                val bitmap = photoURI?.getBitmap(binding.postImageUpdate.width, binding.postImageUpdate.height)
                viewModel.imageBitmap.value = bitmap
            }
        }

    }
}