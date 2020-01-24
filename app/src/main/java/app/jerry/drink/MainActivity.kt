package app.jerry.drink

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

class MainActivity : AppCompatActivity() {
val TAG = "jerryTest"
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setSupportActionBar(toolbar)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()

        }

        val navController = findNavController(R.id.myNavHostFragment)
        binding.bottomNavigationView.setupWithNavController(navController)

        /*Wayne write it outside*/
        binding.bottomNavigationView.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.homeFragment -> {
                    navController.navigate(R.id.action_global_homeFragment)
                }
                R.id.radarFragment -> {
                    navController.navigate(R.id.action_global_radarFragment)
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
