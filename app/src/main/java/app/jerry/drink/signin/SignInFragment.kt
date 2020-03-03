package app.jerry.drink.signin

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import app.jerry.drink.databinding.FragmentSignInBinding
import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.jerry.drink.DrinkApplication
import app.jerry.drink.MainActivity
import app.jerry.drink.NavigationDirections
import app.jerry.drink.R
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
    val TAG = "jerryTest"
    private val auth = FirebaseAuth.getInstance()
    lateinit var callbackManager: CallbackManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sign_in, container, false
        )


//        this.dialog?.setOnKeyListener(DialogInterface.OnKeyListener { _, keyCode, _ ->
//            if (keyCode == KeyEvent.KEYCODE_BACK){
//                (activity as MainActivity).finish()
//            }
//            true
//        })

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val mGoogleSignInClient = GoogleSignIn.getClient(context!!, gso)

        fun signInGoogle() {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, 102)
        }

        fun signInFb() {
            LoginManager.getInstance().logInWithReadPermissions(this,
                Utility.arrayList("public_profile", "email"))
        }

        binding.buttonSigninGoogle.setOnClickListener {
            signInGoogle()
        }

        binding.buttonSigninFb.setOnClickListener {
            signInFb()
        }

        /*FB*/
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    //val accessToken = result?.accessToken
                    Log.d("FbStatus","handleFacebookAccessToken : onSuccess  ${result.accessToken}")
                    handleFacebookAccessToken(result.accessToken)
                }
                override fun onCancel() {
                    Log.d("FbStatus", "onCancel")
                }
                override fun onError(error: FacebookException?) {
                    Log.d("FbStatus", "onError")
                }
            })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 102){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
//                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(context, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }

                // ...
            }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Toast.makeText(DrinkApplication.context, "成功登入", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "signInWithCredential:success ${user!!.email}")
                    Log.d(TAG, "signInWithCredential:success ${user.displayName}")
                    Log.d(TAG, "signInWithCredential:success ${user.uid}")
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Snackbar.make(main_layout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                    updateUI(null)
                }
                // ...
            }
    }

}