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
import app.jerry.drink.signin.UserManager
import app.jerry.drink.util.Util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CreateOrderViewModel(private val repository: DrinkRepository) : ViewModel() {

    // _Order
    private val _createOrder = MutableLiveData<Order>().apply {
        UserManager.user?.let {
            value = Order("", it.id, it, null, 0, 0, "", "", true)
        }
    }

    val createOrder: LiveData<Order>
        get() = _createOrder

    // _allStore
    private val _allStore = MutableLiveData<List<Store>>()

    val allStore: LiveData<List<Store>>
        get() = _allStore

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave

    val createOrderFinished = MutableLiveData<String?>()

    // status: The internal MutableLiveData that stores the status of the most recent request
    private val _status = MutableLiveData<LoadApiStatus>()

    val status: LiveData<LoadApiStatus>
        get() = _status

    // postStatus: The internal MutableLiveData that stores the status of the most recent request
    private val _postStatus = MutableLiveData<LoadApiStatus>()

    val postStatus: LiveData<LoadApiStatus>
        get() = _postStatus

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
        createOrderFinished.value =null
        getAllStoreResult()
    }

    private fun getAllStoreResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getAllStore()

            _allStore.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    _createOrder.value?.let {
                        it.store = result.data[0]
                    }
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

            createOrder.value?.let {
                _postStatus.value = LoadApiStatus.LOADING
                val result = repository.createOrder(it)

                createOrderFinished.value = when (result) {
                    is Result.Success -> {
                        _error.value = null
                        _postStatus.value = LoadApiStatus.DONE
                        Toast.makeText(DrinkApplication.context, getString(R.string.post_success), Toast.LENGTH_SHORT).show()
                        result.data
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _postStatus.value = LoadApiStatus.ERROR
                        null
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _postStatus.value = LoadApiStatus.ERROR
                        null
                    }
                    else -> {
                        _error.value = DrinkApplication.instance.getString(R.string.you_know_nothing)
                        _postStatus.value = LoadApiStatus.ERROR
                        null
                    }
                }
                _refreshStatus.value = false
            }
        }
    }

    fun selectedStore(position: Int) {
        _allStore.value?.let {
            _createOrder.value?.let {order->
                order.store = it[position]
                _createOrder.value = order
            }
        }
    }

    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    val displayAllStore = Transformations.map(allStore) {
        val storeList = mutableListOf<String>()
        for (store in it){
            storeList.add(store.storeName)
        }
        storeList
    }

    fun selectOrderTime() {
        _createOrder.value = _createOrder.value
    }
}
