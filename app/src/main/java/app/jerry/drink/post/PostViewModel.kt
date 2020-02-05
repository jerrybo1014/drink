package app.jerry.drink.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.LoadApiStatus
import app.jerry.drink.R
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.source.DrinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store

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

    // _allStoreMenu
    private val _selectedMenu = MutableLiveData<Drink>()

    val selectedMenu: LiveData<Drink>
        get() = _selectedMenu







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


    fun selectedStore(position: Int) {
        _allStore.value?.let {
            _selectedStore.value = it[position]
        }
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
