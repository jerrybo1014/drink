import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.databinding.FragmentProfileBinding
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.home.HomeViewModel
import app.jerry.drink.profile.ProfileViewModel
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

        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.profileAvatarChoose.setOnClickListener {
            launchGallery()
        }

        viewModel.userCurrent.observe(this, Observer {
            Log.d("userCurrent.observe","userCurrent = $it")
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
            if(data == null || data.data == null){
                return
            }

            filePath = data.data
            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, filePath)
//                uploadImage.setImageBitmap(bitmap)
                Glide.with(this).load(filePath).apply(
                    RequestOptions().circleCrop()).into(profile_avatar)
                viewModel.imageUri.value = filePath
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}