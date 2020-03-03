package app.jerry.drink

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import app.jerry.drink.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import app.jerry.drink.dataclass.Store
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.signin.SignInFragment
import app.jerry.drink.util.CurrentFragmentType
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {
    val TAG = "jerryTest"
    lateinit var binding: ActivityMainBinding
    private val MY_PERMISSIONS_LOCATION = 100
    private val auth = FirebaseAuth.getInstance()
    lateinit var loctionGps: Location
    lateinit var intentOrder: Intent
    val viewModel by viewModels<MainActivityViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        setSupportActionBar(toolbar)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(R.id.myNavHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)
        fab.setOnClickListener {
            //            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            navController.navigate(R.id.action_global_postFragment)
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        setupNavController()


//        val authProvider: List<AuthUI.IdpConfig> = listOf(
//            AuthUI.IdpConfig.FacebookBuilder().build(),
//            AuthUI.IdpConfig.GoogleBuilder().build()
//        )
//        val authListener: FirebaseAuth.AuthStateListener =
//            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
//                val user: FirebaseUser? = auth.currentUser
//                if (user == null) {
//                    val intent = AuthUI.getInstance()
//                        .createSignInIntentBuilder()
//                        .setAvailableProviders(authProvider)
//                        .setAlwaysShowSignInMethodScreen(true)
//                        .setIsSmartLockEnabled(false)
//                        .build()
//                    startActivityForResult(intent, 101)
//                } else {
////                    this.firebaseUser = user
////                    displayInfo()
//                }
//            }
//        FirebaseAuth.getInstance().addAuthStateListener(authListener)

        fun signOut() {
//            FirebaseAuth.getInstance().signOut()
            AuthUI.getInstance().signOut(this)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "已登出", Toast.LENGTH_SHORT).show()
                }
        }

//        binding.toolbar.setOnMenuItemClickListener {
//            when (it.itemId) {
//                R.id.action_log_out -> signOut()
//            }
//            false
//        }
        intentOrder = intent
        val authListener: FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
                val user: FirebaseUser? = auth.currentUser
                if (user == null) {
                    navController.navigate(R.id.action_global_signInFragment)
                    Log.d(TAG, "signInWithCredential:no")
                } else {
                    viewModel.checkUserResult()
                    intentReceiver()
                }
            }

        FirebaseAuth.getInstance().addAuthStateListener(authListener)

        /*Wayne write it outside*/
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.action_global_homeFragment)
                }
                R.id.radarFragment -> {

                    if (ContextCompat.checkSelfPermission(
                            this,
                            permission.ACCESS_FINE_LOCATION
                        )
                        != PackageManager.PERMISSION_GRANTED
                    ) {

                        // Permission is not granted
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            AlertDialog.Builder(this)
                                .setMessage("需要開啟GPS權限，再不給試試看")
                                .setPositiveButton("前往設定") { _, _ ->
                                    ActivityCompat.requestPermissions(
                                        this,
                                        arrayOf(
                                            permission.ACCESS_FINE_LOCATION,
                                            permission.ACCESS_COARSE_LOCATION
                                        ),
                                        MY_PERMISSIONS_LOCATION
                                    )
                                }
                                .setNegativeButton("NO") { _, _ -> }
                                .show()

                        } else {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(
                                    permission.ACCESS_FINE_LOCATION,
                                    permission.ACCESS_COARSE_LOCATION
                                ),
                                MY_PERMISSIONS_LOCATION
                            )
                        }
                    } else {
                        // Permission has already been granted
                        navController.navigate(NavigationDirections.actionGlobalRadarFragment(Store("","","")))
                    }
                }
                R.id.orderFragment -> {
                    navController.navigate(NavigationDirections.actionGlobalOrderFragment("1"))
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.action_global_profileFragment)
                }
            };false
        }

    }

    private fun intentReceiver(){
        if (null != intentOrder.extras) {
            if (null != intentOrder.data) {
                val uri = intentOrder.data
                val orderId = uri?.getQueryParameter("id")
                orderId?.let {
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalOrderFragment(orderId))
                }
            }
        }else{
            findNavController(R.id.myNavHostFragment).navigate(R.id.action_global_homeFragment)
        }
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.radarFragment -> CurrentFragmentType.RADAR
                R.id.orderFragment -> CurrentFragmentType.ORDER
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.detailFragment -> CurrentFragmentType.DETAIL
                R.id.homeSearchFragment -> CurrentFragmentType.SEARCH
                R.id.postFragment -> CurrentFragmentType.POST
                R.id.signInFragment -> CurrentFragmentType.SIGNIN
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            MY_PERMISSIONS_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (permissionsItem in permissions) {
                        Log.d(TAG, "permissions allow : $permissions")
                    }
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalRadarFragment(Store("","","")))
                } else {
                    for (permissionsItem in permissions) {
                        Log.d(TAG, "permissions reject : $permissionsItem")
                    }
                }
                return
            }


            10 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (permissionsItem in permissions) {
                        Log.d(TAG, "permissions allow : $permissions")
                    }
                } else {
                    for (permissionsItem in permissions) {
                        Log.d(TAG, "permissions reject : $permissionsItem")
                    }
                }
                return
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_about -> true
            R.id.action_log_out -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == 101) {
//            if (resultCode != Activity.RESULT_OK) {
//                val response = IdpResponse.fromResultIntent(data)
//                Toast.makeText(
//                    applicationContext,
//                    response?.error?.errorCode.toString(),
//                    Toast.LENGTH_SHORT
//                ).show()
//                Log.d(TAG, "onActivityResult=no")
//            }
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            Log.d(TAG, "onActivityResult=success and ${task.result!!.email}")
//        }
//
//    }


    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            Log.d(TAG, "onLocationChanged ${location!!.latitude}")
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }

    }

    fun getMyLocation(): Location? {
        val myLocationService = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return if (ContextCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(TAG, "myLocationService.getLastKnownLocation(LocationManager.GPS_PROVIDER)")
            myLocationService.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0F,
                object : LocationListener {
                    override fun onLocationChanged(location: Location?) {
                        Log.d(TAG, "onLocationChanged ${location!!.latitude}")
//                    loctionGps = location
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    }

                    override fun onProviderEnabled(provider: String?) {
                    }

                    override fun onProviderDisabled(provider: String?) {
                    }

                })

//            Log.d("myLocationService","${myLocationService.getLastKnownLocation(LocationManager.GPS_PROVIDER).latitude}")
            myLocationService.getLastKnownLocation(LocationManager.GPS_PROVIDER)

//            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
//            fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,
//                Looper.myLooper())
//            Log.d(TAG,"fusedLocationProviderClient.lastLocation.result ${fusedLocationProviderClient.lastLocation.result?.latitude}")
//            fusedLocationProviderClient.lastLocation.result
        } else {
            null
        }
    }




}
