package app.jerry.drink.signin

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.User
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import app.jerry.drink.util.Logger
import app.jerry.drink.util.Util.getString
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SignInViewModel(private val repository: DrinkRepository) : ViewModel() {

    private val _user = MutableLiveData<User>()

    val user: LiveData<User>
        get() = _user

    // Handle navigation to login success
    private val _navigateToLoginSuccess = MutableLiveData<User>()

    val navigateToLoginSuccess: LiveData<User>
        get() = _navigateToLoginSuccess

    // Handle leave login
    private val _loginMode = MutableLiveData<String>()

    val loginMode: LiveData<String>
        get() = _loginMode

    // Handle leave login
    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    var fbCallbackManager: CallbackManager = CallbackManager.Factory.create()
    private val auth = FirebaseAuth.getInstance()

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun loginFacebook() {
        _status.value = LoadApiStatus.LOADING

        LoginManager.getInstance().registerCallback(fbCallbackManager, object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
//                UserManager.fbToken = loginResult.accessToken.token //save fbToken
//                loginStylish(loginResult.accessToken.token)
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() { _status.value = LoadApiStatus.ERROR }

            override fun onError(exception: FacebookException) {
                Logger.w("[${this::class.simpleName}] exception=${exception.message}")

                exception.message?.let {
                    _error.value = if (it.contains("ERR_INTERNET_DISCONNECTED")) {
                        getString(R.string.internet_not_connected)
                    } else {
                        it
                    }
                }
                _status.value = LoadApiStatus.ERROR
            }
        })
        loginMode(getString(R.string.log_in__facebook))
    }

    fun loginGoogle() {
        loginMode(getString(R.string.log_in__google))
    }

    private fun loginMode(loginMode: String) {
        _loginMode.value = loginMode
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginSuccess()
                    Toast.makeText(DrinkApplication.context, getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                } else {
                    // If sign in fails, display a message to the user.
                    Logger.d("signInWithCredential:failure ${task.exception}")
                    Toast.makeText(DrinkApplication.context, getString(R.string.sign_in_failure),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        _status.value = LoadApiStatus.LOADING
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    loginSuccess()
                    Toast.makeText(
                        DrinkApplication.context,
                        getString(R.string.sign_in_success), Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    // Sign in success, update UI with the signed-in user's information
                    Logger.d( "signInWithCredential:success ${user!!.email}")
                } else {
                    Toast.makeText(DrinkApplication.context, getString(R.string.sign_in_failure),
                        Toast.LENGTH_SHORT).show()
                    Logger.d("signInWithCredential:failure${task.exception}")
                }
            }
    }

    fun loginSuccess(){
        _status.value = LoadApiStatus.DONE
    }
}
