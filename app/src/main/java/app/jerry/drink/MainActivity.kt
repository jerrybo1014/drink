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
import app.jerry.drink.util.Logger
import app.jerry.drink.util.PermissionCode
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var intentOrder: Intent
    val viewModel by viewModels<MainActivityViewModel> { getVmFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val navController = findNavController(R.id.myNavHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)


        fab.setOnClickListener {
            navController.navigate(R.id.action_global_postFragment)
        }

        setupNavController()

        intentOrder = intent
        val authListener: FirebaseAuth.AuthStateListener =
            FirebaseAuth.AuthStateListener { auth: FirebaseAuth ->
                val user: FirebaseUser? = auth.currentUser
                if (user == null) {
                    navController.navigate(R.id.action_global_signInFragment)
                    Logger.d("signInWithCredential:no")
                } else {
                    viewModel.checkUserResult()
                }
            }

        viewModel.checkUser.observe(this, Observer {
            it?.let {
                intentReceiver()
            }
        })

        FirebaseAuth.getInstance().addAuthStateListener(authListener)

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
                        if (ActivityCompat.shouldShowRequestPermissionRationale(
                                this,
                                permission.ACCESS_FINE_LOCATION
                            )
                        ) {
                            AlertDialog.Builder(this)
                                .setMessage("需要開啟GPS權限，再不給試試看")
                                .setPositiveButton("前往設定") { _, _ ->
                                    ActivityCompat.requestPermissions(
                                        this,
                                        arrayOf(
                                            permission.ACCESS_FINE_LOCATION,
                                            permission.ACCESS_COARSE_LOCATION
                                        ),
                                        PermissionCode.LOCATION.requestCode
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
                                PermissionCode.LOCATION.requestCode
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
            if (viewModel.currentFragmentType.value == CurrentFragmentType.SIGNIN){
                findNavController(R.id.myNavHostFragment).navigate(R.id.action_global_homeFragment)
            }
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
            PermissionCode.LOCATION.requestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    for (permissionsItem in permissions) {
                        Logger.d("permissions allow : $permissions")
                    }
                    findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.actionGlobalRadarFragment(Store("","","")))
                } else {
                    for (permissionsItem in permissions) {
                        Logger.d("permissions reject : $permissionsItem")
                    }
                }
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> true
            R.id.action_log_out -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
