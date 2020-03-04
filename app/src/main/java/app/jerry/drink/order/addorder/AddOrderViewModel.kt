package app.jerry.drink.order.addorder

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.databinding.InverseMethod
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddOrderViewModel(private val repository: DrinkRepository, private val order: Order) : ViewModel() {

    // _allStoreMenu
    private val _allStoreMenu = MutableLiveData<List<Drink>>()

    val allStoreMenu: LiveData<List<Drink>>
        get() = _allStoreMenu

    // _selectedDrink
    private val _selectedDrink = MutableLiveData<Drink>()

    val selectedDrink: LiveData<Drink>
        get() = _selectedDrink

    // _selectedDrink
    private val _orderItem = MutableLiveData<OrderItem>()

    val orderItem: LiveData<OrderItem>
        get() = _orderItem

    private val _leave = MutableLiveData<Boolean>()

    val leave: LiveData<Boolean>
        get() = _leave


    var selectedIce = MutableLiveData<String>()
    var selectedSugar = MutableLiveData<String>()
    var selectedQty = MutableLiveData<Long>()
    var enterNote = MutableLiveData<String>()

    var addOrderfinished = MutableLiveData<Boolean>()

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
    selectedQty.value = 1
    enterNote.value = ""
    _orderItem.value = OrderItem("",null,null,null, null,1,"")
    addOrderfinished.value = false
}

    fun getStoreMenuResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getStoreMenu(order.store!!)

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

    fun addOrderResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.addOrder(_orderItem.value!!, order.id.toLong())

            addOrderfinished.value = when (result) {
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

    fun selectedDrink(position: Int) {
        _allStoreMenu.value?.let {
            _orderItem.value!!.drink= it[position]
            Log.d("_orderList","${_orderItem.value!!.drink}")
        }
    }

    private var selectedIceView: View? = null
    private var selectedSugarView: View? = null

    fun selectIceStatus(view: View, string: String) {
        selectedIceView?.isSelected = false
        (selectedIceView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.bottom_navigation_light))
        selectedIceView = view
        selectedIceView?.isSelected = true
        (selectedIceView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.White))
        _orderItem.value?.ice = string
        selectedIce.value = string
        Log.d("selectIceStatus","${_orderItem.value}")
    }

    fun selectSugarStatus(view: View, string: String) {
        selectedSugarView?.isSelected = false
        (selectedSugarView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.bottom_navigation_light))
        selectedSugarView = view
        selectedSugarView?.isSelected = true
        (selectedSugarView as? TextView)?.setTextColor(DrinkApplication.context.resources.getColor(R.color.White))
        _orderItem.value?.sugar = string
        selectedSugar.value = string
        Log.d("selectIceStatus","${_orderItem.value}")
    }

    val displayStoreDrink = Transformations.map(allStoreMenu) {
        val drinkList = mutableListOf<String>()
        for (drink in it){
            drinkList.add(drink.drinkName)
        }
        drinkList
    }


    fun leave(needRefresh: Boolean = false) {
        _leave.value = needRefresh
    }

    fun onLeft() {
        _leave.value = null
    }


    @InverseMethod("convertLongToString")
    fun convertStringToLong(value: String): Long {
        return try {
            value.toLong()
        } catch (e: NumberFormatException) {
            1L
        }
    }
    fun convertLongToString(value: Long): String {
        return value.toString()
    }

    fun increase(){
        selectedQty.value = selectedQty.value?.plus(1)
    }
    fun decrease(){
        selectedQty.value = selectedQty.value?.minus(1)
    }


}