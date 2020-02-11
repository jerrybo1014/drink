package app.jerry.drink.order

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.OrderList
import app.jerry.drink.dataclass.OrderLists
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.dataclass.source.remote.DrinkRemoteDataSource
import app.jerry.drink.network.LoadApiStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class OrderVIewModel(private val repository: DrinkRepository) : ViewModel() {


    private val _enterOrderId = MutableLiveData<Long>()

    val enterOrderId: LiveData<Long>
        get() = _enterOrderId

    private val _orderLists = MutableLiveData<OrderLists>()

    val orderLists: LiveData<OrderLists>
        get() = _orderLists


    private var _orderLive = MutableLiveData<List<OrderList>>()

    val orderLive: LiveData<List<OrderList>>
        get() = _orderLive


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

    }

    fun test(id: Long) {
        _orderLive = repository.getOrderLive(id) as MutableLiveData<List<OrderList>>
    }
    fun getOrderLiveResult(orderId: Long){
        _orderLive = repository.getOrderLive(orderId) as MutableLiveData<List<OrderList>>

        Log.d("getOrderLiveResult","_orderLive = ${repository.getOrderLive(orderId).value}")
    }

    fun getOrderResult(orderId: Long) {

//        getOrderLiveResult(orderId)
//        _orderLive = repository.getOrderLive(orderId) as MutableLiveData<List<OrderList>>
        Log.d("getOrderLiveResult","_orderLive = ${repository.getOrderLive(orderId).value}")
        /**/
//        _orderLists.value?.orderLists = repository.getOrderLive(orderId).value
        /**/

        coroutineScope.launch {
            _orderLive = repository.getOrderLive(orderId) as MutableLiveData<List<OrderList>>

            _status.value = LoadApiStatus.LOADING

            val result = repository.getOrder(orderId)

            /**/
            _orderLists.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    /**/
                    result.data
                    /**/
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

    fun editOrderStatusResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val editStatus = !orderLists.value?.order!!.status

            val result = repository.editOrderStatus(orderLists.value?.order?.id!!.toLong(), editStatus)

            /**/
            when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE

                    getOrderResult(orderLists.value?.order?.id!!.toLong())
                    /**/
                    result.data
                    /**/
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



}
