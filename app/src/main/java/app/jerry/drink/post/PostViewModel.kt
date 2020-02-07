package app.jerry.drink.post

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.network.LoadApiStatus
import app.jerry.drink.R
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class PostViewModel(private val repository: DrinkRepository) : ViewModel() {

    // _allStore
    private val _allStore = MutableLiveData<List<Store>>()

    val allStore: LiveData<List<Store>>
        get() = _allStore

    // _selectedStore
    private val _selectedStore = MutableLiveData<Store>()

    val selectedStore: LiveData<Store>
        get() = _selectedStore

    // _allStoreMenu
    private val _allStoreMenu = MutableLiveData<List<Drink>>()

    val allStoreMenu: LiveData<List<Drink>>
        get() = _allStoreMenu

    // _selectedDrink
    private val _selectedDrink = MutableLiveData<Drink>()

    val selectedDrink: LiveData<Drink>
        get() = _selectedDrink


    var selectedIce = MutableLiveData<String>()
    var selectedSugar = MutableLiveData<String>()
    val editComment = MutableLiveData<String>()
    var commentStar = MutableLiveData<Int>()
    var postFinshed = MutableLiveData<Boolean>()

    var selectedIcePosition = MutableLiveData<Int>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // error: The internal MutableLiveData that stores the error of the most recent request
    private val _error = MutableLiveData<String>()

    val error: LiveData<String>
        get() = _error

    // status for the loading icon of swl
    private val _refreshStatus = MutableLiveData<Boolean>()

    val refreshStatus: LiveData<Boolean>
        get() = _refreshStatus

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

init {
    editComment.value = ""
    postFinshed.value = false
}

    fun getAllStoreResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getAllStore()

            _allStore.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = DrinkApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }

    }

    fun getStoreMenuResult(store: Store) {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getStoreMenu(store)

            _allStoreMenu.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = DrinkApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }

    }

    fun postCommentResult() {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val comment = Comment(
                "",
                User("","","",""),
                "",
                selectedStore.value!!.storeName,
                selectedStore.value!!.storeId,
                selectedDrink.value!!.drinkName,
                selectedDrink.value!!.drinkId,
                selectedIce.value!!,
                selectedSugar.value!!,
                commentStar.value!!,
                editComment.value!!,
                "",
                System.currentTimeMillis())

            val result = repository.postComment(comment)

            when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.d("postComentResult","$result.data")
                    postFinshed.value = true
                    Toast.makeText(DrinkApplication.context, "成功送出", Toast.LENGTH_SHORT).show()
                    result.data
                }
                is Result.Fail -> {
                    _error.value = result.error
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                is Result.Error -> {
                    _error.value = result.exception.toString()
                    _status.value = LoadApiStatus.ERROR
                    null
                }
                else -> {
                    _error.value = DrinkApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                    null
                }
            }
            _refreshStatus.value = false
        }

    }


    @SuppressLint("SimpleDateFormat")
    private fun convertLongToDateString(systemTime: Long): String {
        return SimpleDateFormat("MMM-dd-yyyy HH:mm")
            .format(systemTime).toString()
    }

    fun selectedStore(position: Int) {
        _allStore.value?.let {
            _selectedStore.value = it[position]
        }
    }

    fun selectedDrink(position: Int) {
        _allStoreMenu.value?.let {
            _selectedDrink.value= it[position]
            Log.d("_selectedDrink","${_selectedDrink.value}")
        }
    }



    fun selectIce(string: String, position: Int) {
        Log.d("selectIce","$position")
        selectedIce.value = string
        selectedIcePosition.value = position
    }

    private var selectedIceView: View? = null
    private var selectedSugarView: View? = null

    fun selectIceStatus(view: View, string: String) {
        selectedIceView?.isSelected = false
        (selectedIceView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.bottom_navigation_light))
        selectedIceView = view
        selectedIceView?.isSelected = true
        (selectedIceView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.White))
        selectedIce.value = string
        Log.d("selectIceStatus",string)
    }

    fun selectSugarStatus(view: View, string: String) {
        selectedSugarView?.isSelected = false
        (selectedSugarView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.bottom_navigation_light))
        selectedSugarView = view
        selectedSugarView?.isSelected = true
        (selectedSugarView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.White))
        selectedSugar.value = string
        Log.d("selectIceStatus",string)
    }

    val displayAllStore = Transformations.map(allStore) {
        val storeList = mutableListOf<String>()
        for (store in it){
            storeList.add(store.storeName)
        }
        storeList
    }

    val displayStoreDrink = Transformations.map(allStoreMenu) {
        val drinkList = mutableListOf<String>()
        for (drink in it){
            drinkList.add(drink.drinkName)
        }
        drinkList
    }

//    fun leave(needRefresh: Boolean = false) {
//        _leave.value = needRefresh
//    }

//    fun queryAllStore() {
//        val db = FirebaseFirestore.getInstance()
//        db.collection("allStore")
//            .get()
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Log.d("queryPostTag", "${document.id} => ${document.data}")
//                }
//            }
//            .addOnFailureListener { exception ->
//                Log.w("queryPostTag", "Error getting documents.", exception)
//            }
//    }

}
