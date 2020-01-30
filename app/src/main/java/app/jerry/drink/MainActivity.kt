package app.jerry.drink

import android.Manifest
import android.os.Bundle
import android.util.Log
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import app.jerry.drink.databinding.ActivityMainBinding
import app.jerry.drink.databinding.FragmentHomeBinding
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.android.synthetic.main.activity_main.*
import android.Manifest.permission
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.location.LocationManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
val TAG = "jerryTest"
    lateinit var binding: ActivityMainBinding
    private val MY_PERMISSIONS_LOCATION = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(toolbar)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val navController = findNavController(R.id.myNavHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)
        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
            navController.navigate(R.id.action_global_postFragment)
        }

//        if (ContextCompat.checkSelfPermission(this,
//                permission.ACCESS_FINE_LOCATION)
//            != PackageManager.PERMISSION_GRANTED) {
//
//            // Permission is not granted
//            if (ActivityCompat.shouldShowRequestPermissionRationale(
//                    this,
//                    permission.ACCESS_FINE_LOCATION
//                )
//            ) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                AlertDialog.Builder(this)
//                    .setMessage("需要開啟GPS權限，勸你是給我喔")
//                    .setPositiveButton("前往設定") { _, _ ->
//                        ActivityCompat.requestPermissions(
//                            this,
//                            arrayOf(permission.ACCESS_FINE_LOCATION),
//                            MY_PERMISSIONS_LOCATION
//                        )
//                    }
//                    .setNegativeButton("NO") { _, _ ->  }
//                    .show()
//
//            } else {
//                ActivityCompat.requestPermissions(
//                    this,
//                    arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
//                    MY_PERMISSIONS_LOCATION
//                )
//            }
//        }else{
//            // Permission has already been granted
//        }


//        ActivityCompat.requestPermissions(this,
//            arrayOf(permission.ACCESS_FINE_LOCATION,permission.ACCESS_COARSE_LOCATION),
//            MY_PERMISSIONS_LOCATION)

        /*Wayne write it outside*/
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.action_global_homeFragment)
                }
                R.id.radarFragment -> {

                    if (ContextCompat.checkSelfPermission(this,
                            permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

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
                                .setMessage("需要開啟GPS權限，勸你是給我喔")
                                .setPositiveButton("前往設定") { _, _ ->
                                    ActivityCompat.requestPermissions(
                                        this,
                                        arrayOf(permission.ACCESS_FINE_LOCATION,permission.ACCESS_COARSE_LOCATION),
                                        MY_PERMISSIONS_LOCATION
                                    )
                                }
                                .setNegativeButton("NO") { _, _ ->  }
                                .show()

                        } else {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION),
                                MY_PERMISSIONS_LOCATION
                            )
                        }
                    }else{
                        // Permission has already been granted
                        navController.navigate(R.id.action_global_radarFragment)
                    }
                }
                R.id.orderFragment -> {
                    navController.navigate(R.id.action_global_orderFragment)
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.action_global_profileFragment)
                }
            };false
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
                } else {
                    for (permissionsItem in permissions){
                        Log.d(TAG,"permissions reject : $permissionsItem")
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
            else -> super.onOptionsItemSelected(item)
        }
    }

//    fun articlePost(
//        title: String,
//        author: String,
//        tag: String,
//        createdTime: String,
//        content: String
//    ) {
//        val user: HashMap<String, Any> = hashMapOf(
//            "title" to title,
//            "author" to author,
//            "tag" to tag,
//            "created_time" to createdTime,
//            "content" to content
//        )
//// Add a new document with a generated ID
//        val db = FirebaseFirestore.getInstance()
//        db.collection("article")
//            .document(createdTime)
//            .set(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d(TAG, "DocumentSnapshot added with content_POST:$title")
//            }
//            .addOnFailureListener { e ->
//                Log.w(TAG, "Error adding document_POST", e)
//            }
//    }
}
