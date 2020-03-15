package app.jerry.drink

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import app.jerry.drink.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.User
import app.jerry.drink.ext.getVmFactory
import app.jerry.drink.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser


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
                    user.let {
                        val loginUser = User(it.uid, it.displayName, it.email, "")
                        Logger.d("loginUser = $loginUser")
                        viewModel.checkUserResult(loginUser)
                    }
                }
            }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)

        viewModel.checkUser.observe(this, Observer {
            it?.let {
                if (it) intentReceiver()
            }
        })

        binding.bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            viewModel.checkUser.value?.let {
                if (it) {
                    when (menuItem.itemId) {
                        R.id.homeFragment -> {
                            navController.navigate(R.id.action_global_homeFragment)
                        }
                        R.id.radarFragment -> {
                            Util.checkGpsPermission(this, Util.OnPermissionCheckedListener {
                                navController.navigate(
                                    NavigationDirections.actionGlobalRadarFragment(
                                        Store()
                                    )
                                )
                            })
                        }
                        R.id.orderFragment -> {
                            navController.navigate(NavigationDirections.actionGlobalOrderFragment("1"))
                        }
                        R.id.profileFragment -> {
                            navController.navigate(R.id.action_global_profileFragment)
                        }
                    }
                }
            }
            false
        }
    }

    private fun intentReceiver() {
        if (null != intentOrder.extras) {
            if (null != intentOrder.data) {
                val uri = intentOrder.data
                val orderId = uri?.getQueryParameter("id")
                orderId?.let {
                    findNavController(R.id.myNavHostFragment).navigate(
                        NavigationDirections.actionGlobalOrderFragment(
                            orderId
                        )
                    )
                }
            }
        } else {
            if (viewModel.currentFragmentType.value == CurrentFragmentType.SIGNIN) {
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
                    findNavController(R.id.myNavHostFragment).navigate(
                        NavigationDirections.actionGlobalRadarFragment(Store())
                    )
                } else {
                    Util.openLocationIfDenied(this)
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
