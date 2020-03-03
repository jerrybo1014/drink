package app.jerry.drink.post

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.icu.text.SimpleDateFormat
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import app.jerry.drink.DrinkApplication
import app.jerry.drink.MainActivity
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentPostBinding
import app.jerry.drink.ext.getBitmap
import app.jerry.drink.ext.getVmFactory
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_post.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.util.*

class PostFragment : Fragment() {

    lateinit var binding: FragmentPostBinding
    private val PICK_IMAGE_REQUEST = 3
    private val TAKE_PHOTO_REQUEST = 6
    private val MY_PERMISSIONS_CAMERA = 10
    private val MY_PERMISSIONS_READ_EXTERNAL_STORAGE = 12
    private var filePath: Uri? = null
//    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_TAKE_PHOTO = 1
    lateinit var currentPhotoPath: String
    private var fileName = ""
    private var TAG = "jerryTest"
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


//        binding.spinnerStore.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                viewModel.selectedStore(position)
//            }
//        }

//        binding.spinnerDrink.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                viewModel.selectedDrink(position)
//            }
//        }
        val listIce = listOf("正常冰", "去冰", "微冰", "常溫")
        val listSugar = listOf("正常糖", "半糖", "微糖", "無糖")
        val sugarAdapter = SugarAdapter(viewModel)
        val iceAdapter = IceAdapter(viewModel)

        binding.recyclerIce.adapter = iceAdapter
        binding.recyclerSugar.adapter = sugarAdapter

        sugarAdapter.submitList(listSugar)
        iceAdapter.submitList(listIce)

        binding.ratingBarComment.setOnRatingBarChangeListener { ratingBar, fl, b ->
            viewModel.commentStar.value = fl.toInt()
        }

        viewModel.allStore.observe(this, Observer {

            binding.spinnerStore.adapter = PostStoreSpinnerAdapter(it)
        })

        viewModel.selectedStore.observe(this, Observer {
            viewModel.getStoreMenuResult(it)
//            viewModel.addStoreToDrinkResult(it)
        })

        viewModel.allStoreMenu.observe(this, Observer {
            binding.spinnerDrink.adapter = PostDrinkSpinnerAdater(it)
            Log.d("allStoreMenu", "$it")
        })

        viewModel.selectedDrink.observe(this, Observer {
            Log.d("jerryTest", "selectedDrink = $it")
        })

        viewModel.postFinished.observe(this, Observer {
            if (it) {
                findNavController().navigateUp()
            }
        })

//        (activity as MainActivity).binding.bottomNavigationView.visibility = View.GONE

        viewModel.imageUri.observe(this, Observer {
            Log.d("jerryTest", "imageUri.observe = $it")
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

//    private fun galleryAddPic() {
//        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
//            val f = File(currentPhotoPath)
//            mediaScanIntent.data = Uri.fromFile(f)
//            sendBroadcast(mediaScanIntent)
//        }
//    }

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
                        Log.d(TAG, "permissions allow : $permissions")
                    }
                    loadCamera()
                } else {
                    for (permissionsItem in permissions) {
                        Log.d(TAG, "permissions reject : $permissionsItem")
                    }
                }
                return
            }

            MY_PERMISSIONS_READ_EXTERNAL_STORAGE ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (permissionsItem in permissions) {
                        Log.d(TAG, "permissions allow : $permissions")
                    }
                    launchGallery()
                } else {
                    for (permissionsItem in permissions) {
                        Log.d(TAG, "permissions reject : $permissionsItem")
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
            filePath = data.data

            val bitmap = filePath?.getBitmap(binding.imageUpdate.width, binding.imageUpdate.height)
//            binding.imageUpdate.setImageBitmap(bitmap)
                            Glide.with(this).load(filePath)
                                .apply(RequestOptions().centerCrop())
                                .into(image_update)
            viewModel.imageBitmap.value = bitmap
//            try {
////                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, filePath)
////                uploadImage.setImageBitmap(bitmap)
//                Glide.with(this).load(filePath)
//                    .apply(RequestOptions().centerCrop())
//                    .into(image_update)
//                viewModel.imageUri.value = filePath
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
        }

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }

            val bitmap = photoURI?.getBitmap(binding.imageUpdate.width, binding.imageUpdate.height)
//            binding.imageUpdate.setImageBitmap(bitmap)
            Glide.with(this).load(photoURI)
                .apply(RequestOptions().centerCrop())
                .into(image_update)
            viewModel.imageBitmap.value = bitmap
        }

    }

//    override fun onDestroy() {
//        super.onDestroy()
//        (activity as MainActivity).binding.bottomNavigationView.visibility = View.VISIBLE
//    }

}