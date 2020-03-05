package app.jerry.drink.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.jerry.drink.databinding.FragmentSignInBinding
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.jerry.drink.R
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.util.IntentCode
import app.jerry.drink.util.Logger
import app.jerry.drink.util.Util
import com.facebook.internal.Utility
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SignInFragment : Fragment() {

    lateinit var binding: FragmentSignInBinding
    private val viewModel by viewModels<SignInViewModel> { getVmFactory() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_in, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.loginMode.observe(this, Observer {
            it?.let {
                Logger.d("loginMode = $it")
                when (it){
                    Util.getString(R.string.log_in__facebook) -> {
                        LoginManager.getInstance().logInWithReadPermissions(this,
                            Utility.arrayList(Util.getString(R.string.fb_request_profile), Util.getString(R.string.fb_request_email)))
                    }
                    Util.getString(R.string.log_in__google) -> {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestIdToken(Util.getString(R.string.default_web_client_id))
                            .requestEmail()
                            .build()
                        val mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)
                        val signInIntent = mGoogleSignInClient.signInIntent
                        startActivityForResult(signInIntent, IntentCode.GOOGLE_SIGN_IN.requestCode)
                    }
                }
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.fbCallbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentCode.GOOGLE_SIGN_IN.requestCode){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Logger.w("Google sign in failed $e")
            }
        }
    }
}