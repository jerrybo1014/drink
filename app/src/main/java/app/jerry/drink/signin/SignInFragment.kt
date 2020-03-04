package app.jerry.drink.signin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import app.jerry.drink.databinding.FragmentSignInBinding
import android.content.Intent
import android.widget.Toast
import androidx.fragment.app.Fragment
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.util.IntentCode
import app.jerry.drink.util.Logger
import app.jerry.drink.util.Util
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.internal.Utility
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider


class SignInFragment : Fragment() {

    lateinit var binding: FragmentSignInBinding
    private val auth = FirebaseAuth.getInstance()
    lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_in, container, false
        )

        /*GoogleSignIn*/
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)

        /*FB*/
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    Logger.d("FbStatus handleFacebookAccessToken : onSuccess  ${result.accessToken}")
                    handleFacebookAccessToken(result.accessToken)
                }
                override fun onCancel() {
                    Logger.d("FbStatus onCancel")
                }
                override fun onError(error: FacebookException?) {
                    Logger.d("FbStatus onError")
                }
            })

        fun signInGoogle() {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, IntentCode.GOOGLE_SIGN_IN.requestCode)
        }

        fun signInFb() {
            LoginManager.getInstance().logInWithReadPermissions(this,
                Utility.arrayList(Util.getString(R.string.fb_request_profile), Util.getString(R.string.fb_request_email)))
        }

        binding.buttonSignInGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.buttonSignInFb.setOnClickListener {
            signInFb()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentCode.GOOGLE_SIGN_IN.requestCode){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Logger.w("Google sign in failed $e")
            }
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(DrinkApplication.context, Util.getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                    Logger.d("signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Logger.d("signInWithCredential:failure ${task.exception}")
                    Toast.makeText(context, Util.getString(R.string.sign_in_failure),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(DrinkApplication.context,
                        Util.getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    // Sign in success, update UI with the signed-in user's information
                    Logger.d( "signInWithCredential:success ${user!!.email}")
                } else {
                    Toast.makeText(context, Util.getString(R.string.sign_in_failure),
                        Toast.LENGTH_SHORT).show()
                    Logger.d("signInWithCredential:failure${task.exception}")
                }
            }
    }
}