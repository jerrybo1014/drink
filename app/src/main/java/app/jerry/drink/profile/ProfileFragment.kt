import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentOrderBinding
import app.jerry.drink.databinding.FragmentProfileBinding
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.home.HomeViewModel
import app.jerry.drink.profile.ProfileViewModel

class ProfileFragment : Fragment() {


    lateinit var binding: FragmentProfileBinding

    private val viewModel by viewModels<ProfileViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )




        return binding.root
    }

}