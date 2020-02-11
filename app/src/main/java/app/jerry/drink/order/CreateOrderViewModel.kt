package app.jerry.drink.order

import android.icu.util.Calendar
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.Order
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateOrderViewModel(private val repository: DrinkRepository) : ViewModel() {

    // _allStore
    private val _allStore = MutableLiveData<List<Store>>()

    val allStore: LiveData<List<Store>>
        get() = _allStore

    // _selectedStore
    private val _selectedStore = MutableLiveData<Store>()

    val selectedStore: LiveData<Store>
        get() = _selectedStore

    val selectTime = MutableLiveData<String>()
    val enterNote = MutableLiveData<String>()
    val createOrderFinished = MutableLiveData<Boolean?>()

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
        selectTime.value = ""
        enterNote.value = ""
        createOrderFinished.value =false
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

    fun createOrderResult() {

        coroutineScope.launch {

            val order = Order(
                "",
                null,
                selectedStore.value,
                0,
                Calendar.getInstance().timeInMillis,
                selectTime.value,
                enterNote.value!!.trim(),
                true
            )

            _status.value = LoadApiStatus.LOADING

            val result = repository.createOrder(order)

            createOrderFinished.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
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

}
