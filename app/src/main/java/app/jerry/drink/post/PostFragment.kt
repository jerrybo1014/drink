package app.jerry.drink.post

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Rect
import android.graphics.RectF
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
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
import app.jerry.drink.ext.setImagePreView
import app.jerry.drink.util.Logger
import app.jerry.drink.util.PermissionCode
import app.jerry.drink.util.RequestCode
import app.jerry.drink.util.Util
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_post.*
import java.io.File
import java.io.IOException
import java.util.*

class PostFragment : Fragment() {

    lateinit var binding: FragmentPostBinding
    private var filePath: Uri? = null
    private var photoURI: Uri? = null
    private lateinit var currentPhotoPath: String
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

        val listIce = DrinkApplication.context.resources.getStringArray(R.array.list_ice).toList()
        val listSugar =
            DrinkApplication.context.resources.getStringArray(R.array.list_sugar).toList()

        val sugarAdapter = SugarAdapter(viewModel)
        val iceAdapter = IceAdapter(viewModel)

        binding.postRecyclerIce.adapter = iceAdapter
        binding.postRecyclerSugar.adapter = sugarAdapter

        sugarAdapter.submitList(listSugar)
        iceAdapter.submitList(listIce)

        viewModel.allStore.observe(this, Observer {
            binding.postSpinnerStore.adapter = PostStoreSpinnerAdapter(it)
        })

        viewModel.allStoreMenu.observe(this, Observer {
            binding.postSpinnerDrink.adapter = PostDrinkSpinnerAdapter(it)
        })

        binding.postRatingBarComment.setOnRatingBarChangeListener { ratingBar, fl, b ->
            viewModel.selectStar(fl.toInt())
        }

        viewModel.selectStorePosition.observe(this, Observer {
            viewModel.selectStore(it)
        })

        viewModel.selectDrinkPosition.observe(this, Observer {
            viewModel.selectDrink(it)
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
                        Util.getString(R.string.file_provider_path),
                        it
                    )
                    this.photoURI = photoURI
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, RequestCode.TAKE_PHOTO.requestCode)
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
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            RequestCode.PICK_IMAGE.requestCode
        )
    }

    private fun loadGallery() {

        if (ContextCompat.checkSelfPermission(
                DrinkApplication.instance,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PermissionCode.READ_EXTERNAL_STORAGE.requestCode
            )

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

            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA
                ),
                PermissionCode.CAMERA.requestCode
            )

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
            PermissionCode.CAMERA.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadCamera()
                } else {

                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            activity as MainActivity,
                            Manifest.permission.CAMERA
                        )
                    ) {
                        AlertDialog.Builder(context!!)
                            .setMessage(Util.getString(R.string.need_camera_permission))
                            .setPositiveButton(Util.getString(R.string.open_permission)) { _, _ ->
                                context?.let {
                                    Util.openAppSettingsIntent(it)
                                }
                            }
                            .setNegativeButton(Util.getString(R.string.permission_permanently_denied_negative_button)) { _, _ -> }
                            .show()
                    }

                    for (permissionsItem in permissions) {
                        Logger.d("permissions reject : $permissions")
                    }
                }
                return
            }

            PermissionCode.READ_EXTERNAL_STORAGE.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchGallery()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            activity as MainActivity,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        )
                    ) {
                        AlertDialog.Builder(context!!)
                            .setMessage(Util.getString(R.string.need_gallery_permission))
                            .setPositiveButton(Util.getString(R.string.open_permission)) { _, _ ->
                                context?.let {
                                    Util.openAppSettingsIntent(it)
                                }
                            }
                            .setNegativeButton(Util.getString(R.string.permission_permanently_denied_negative_button)) { _, _ -> }
                            .show()
                    }
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode.PICK_IMAGE.requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }
            data.data?.let {
                filePath = it
//                cropImageView.setImageUriAsync(filePath)
                CropImage.activity(filePath)
                    .setAspectRatio(1, 1)
                    .start(context!!, this)
//                it.setImagePreView(binding.postImageUpdate)
//                val bitmap = filePath?.getBitmap(
//                    binding.postImageUpdate.width,
//                    binding.postImageUpdate.height
//                )
//                viewModel.imageBitmap.value = bitmap
            }
        }

        if (requestCode == RequestCode.TAKE_PHOTO.requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                return
            }
            photoURI?.let {
                CropImage.activity(photoURI)
                    .setAspectRatio(1, 1)
                    .start(context!!, this)
//                cropImageView.setImageUriAsync(photoURI)
//                it.setImagePreView(binding.postImageUpdate)
//                val bitmap =
//                    it.getBitmap(
//                        binding.postImageUpdate.width,
//                        binding.postImageUpdate.height
//                    )
//                viewModel.imageBitmap.value = bitmap
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                val bitmap =
                    resultUri.getBitmap(
                        binding.postImageUpdate.width,
                        binding.postImageUpdate.height
                    )
                viewModel.imageBitmap.value = bitmap
                resultUri.setImagePreView(binding.postImageUpdate)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Logger.d("error = $error")
            }
        }
    }
}