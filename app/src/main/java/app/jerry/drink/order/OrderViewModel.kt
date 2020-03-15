package app.jerry.drink.order

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import app.jerry.drink.signin.UserManager
import app.jerry.drink.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrderViewModel(private val repository: DrinkRepository, private val orderId: String) :
    ViewModel() {

    val userStatus = MediatorLiveData<Boolean>()

    private val _enterOrderId = MutableLiveData<Long>()

    val enterOrderId: LiveData<Long>
        get() = _enterOrderId

    private var _orderItemsLive = MutableLiveData<List<OrderItem>>()

    val orderItemsLive: LiveData<List<OrderItem>>
        get() = _orderItemsLive

    private var _orderLive = MutableLiveData<Order>()

    val orderLive: LiveData<Order>
        get() = _orderLive

    private val _navigationToRadar = MutableLiveData<Store>()

    val navigationToRadar: LiveData<Store>
        get() = _navigationToRadar

    val userCurrent = MutableLiveData<User>()
    val orderRecord = MutableLiveData<String>()
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
        getUserCurrentResult()
        getInit()
    }

    private fun getInit() {
        if (orderId != "1") {
            OrderRecord.orderId = orderId
            orderRecord()
            getOrderResult(orderId.toLong())
        }else{
            orderRecord()
        }
    }

    private fun getUserCurrentResult() {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUserCurrent()

            userCurrent.value = when (result) {
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

    fun getOrderResult(orderId: Long) {
        _orderLive = repository.getOrderLive(orderId) as MutableLiveData<Order>
        _orderItemsLive = repository.getOrderItemLive(orderId) as MutableLiveData<List<OrderItem>>
    }

    fun editOrderStatusResult() {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            _orderLive.value?.let {
                val editStatus = !it.status
                /**/
                when (val result = repository.editOrderStatus(it.id.toLong(), editStatus)) {
                    is Result.Success -> {
                        _error.value = null
                        _status.value = LoadApiStatus.DONE
                        result.data
                    }
                    is Result.Fail -> {
                        _error.value = result.error
                        _status.value = LoadApiStatus.ERROR
                    }
                    is Result.Error -> {
                        _error.value = result.exception.toString()
                        _status.value = LoadApiStatus.ERROR
                    }
                    else -> {
                        _error.value =
                            DrinkApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                    }
                }
            }
            _refreshStatus.value = false
        }
    }

    fun removeOrderResult(id: String) {

        coroutineScope.launch {
            _status.value = LoadApiStatus.LOADING
            orderLive.value?.let {
                when (val result = repository.removeOrder(it.id.toLong(), id)) {
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
                        _error.value =
                            DrinkApplication.instance.getString(R.string.you_know_nothing)
                        _status.value = LoadApiStatus.ERROR
                        null
                    }
                }
            }
            _refreshStatus.value = false
        }
    }

    fun checkIfUsersAreTheSame() {
        UserManager.user?.let { userCurrent ->
            orderLive.value?.user?.let {
                userStatus.value = userCurrent.id == it.id
            }
        }
    }

    fun navigationToRadar(store: Store) {
        _navigationToRadar.value = store
    }

    fun navigationToRadarFinished() {
        _navigationToRadar.value = null
    }

    fun orderRecord(){
        orderRecord.value = OrderRecord.orderId
    }

}
