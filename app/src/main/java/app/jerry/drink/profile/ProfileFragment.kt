import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.databinding.FragmentProfileBinding
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.home.HomeViewModel
import app.jerry.drink.home.NewCommentAdapter
import app.jerry.drink.profile.ProfileViewModel
import app.jerry.drink.profile.UserCommentAdapter
import app.jerry.drink.profile.UserOrderAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_post.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.IOException

class ProfileFragment : Fragment() {


    lateinit var binding: FragmentProfileBinding
    private val PICK_AVATAR_REQUEST = 5
    private val viewModel by viewModels<ProfileViewModel> { getVmFactory() }
    private var filePath: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )

        (activity as MainActivity).binding.fab.hide()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val userCommentAdapter = UserCommentAdapter(UserCommentAdapter.OnClickListener {
            viewModel.navigationToDetail(it)
        })

        val userOrderAdapter = UserOrderAdapter(UserOrderAdapter.OnClickListener {
            viewModel.navigationToOrder(it)
        })

        binding.recyclerProfileAllComments.adapter = userCommentAdapter
        binding.recyclerProfileAllOrders.adapter = userOrderAdapter

        binding.profileAvatarChoose.setOnClickListener {
            launchGallery()
        }

        viewModel.userCurrent.observe(this, Observer {
            Log.d("userCurrent.observe", "userCurrent = $it")
        })

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


        val mShowAction = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        )
        mShowAction.duration = 500

        val mHiddenAction = TranslateAnimation(
            Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            -1.0f
        )
        mHiddenAction.duration = 500

//        viewModel.allCommentStatus.observe(this, Observer {
//            if (it) {
//                binding.recyclerProfileAllComments.startAnimation(mShowAction)
//                binding.recyclerProfileAllComments.visibility = View.VISIBLE
//            } else {
//                binding.recyclerProfileAllComments.startAnimation(mHiddenAction)
//                binding.recyclerProfileAllComments.visibility = View.GONE
//            }
//        })

        viewModel.allOrderStatus.observe(this, Observer {

        })



        return binding.root
    }

    private fun launchGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_AVATAR_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_AVATAR_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data == null || data.data == null) {
                return
            }

            filePath = data.data
            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, filePath)
//                uploadImage.setImageBitmap(bitmap)
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