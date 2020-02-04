package app.jerry.drink.post

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.jerry.drink.dataclass.source.DrinkRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostViewModel(private val repository: DrinkRepository) : ViewModel() {

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)


    fun queryAllStore() {
        val db = FirebaseFirestore.getInstance()
        db.collection("store")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    Log.d("queryPostTag", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("queryPostTag", "Error getting documents.", exception)
            }
    }

}
