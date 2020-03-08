import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.DrinkApplication
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.databinding.FragmentProfileBinding
import app.jerry.drink.ext.getBitmap
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.home.HomeViewModel
import app.jerry.drink.home.NewCommentAdapter
import app.jerry.drink.profile.ProfileViewModel
import app.jerry.drink.profile.UserCommentAdapter
import app.jerry.drink.profile.UserOrderAdapter
import app.jerry.drink.util.Logger
import app.jerry.drink.util.PermissionCode
import app.jerry.drink.util.RequestCode
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.IOException

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel> { getVmFactory() }
    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val userCommentAdapter = UserCommentAdapter(UserCommentAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        val userOrderAdapter = UserOrderAdapter(UserOrderAdapter.OnClickListener {
            viewModel.navigationToOrder(it)
        })

        binding.profileRecyclerAllComments.adapter = userCommentAdapter
        binding.profileRecyclerAllOrders.adapter = userOrderAdapter

        binding.profileAvatarChoose.setOnClickListener {
            loadGallery()
        }

        viewModel.navigationToDetail.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalDetailFragment(it))
                viewModel.onDetailNavigated()
            }
        })

        viewModel.navigationToOrder.observe(this, Observer {
            it?.let {
                findNavController().navigate(NavigationDirections.actionGlobalOrderFragment(it))
                viewModel.onOrderNavigated()
            }
        })

        viewModel.allCommentStatus.observe(this, Observer {
            binding.profileFoldCommentArrow.isSelected = it
        })

        viewModel.allOrderStatus.observe(this, Observer {
            binding.profileFoldOrderArrow.isSelected = it
        })

        return binding.root
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RequestCode.PICK_IMAGE.requestCode)
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

    private fun openAppSettingsIntent() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context!!.packageName, null))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PermissionCode.READ_EXTERNAL_STORAGE.requestCode ->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    launchGallery()
                } else {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(activity as MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        AlertDialog.Builder(context!!)
                            .setMessage("需要開啟相簿權限!")
                            .setPositiveButton("前往設定") { _, _ ->
                                openAppSettingsIntent()
                            }
                            .setNegativeButton("先不要") { _, _ -> }
                            .show()
                    }
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

        if (requestCode == RequestCode.PICK_IMAGE.requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            val bitmap = filePath?.getBitmap(binding.profileAvatar.width, binding.profileAvatar.height)
            viewModel.imageBitmap.value = bitmap
            try {
                Glide.with(this).load(filePath).apply(
                    RequestOptions().circleCrop()
                ).into(profile_avatar)
                viewModel.imageUri.value = filePath

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}