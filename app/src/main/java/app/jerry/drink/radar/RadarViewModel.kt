package app.jerry.drink.radar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.StoreLocation
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RadarViewModel(private val repository: DrinkRepository, private val store: Store) : ViewModel() {

    private val _storeLocation = MutableLiveData<List<StoreLocation>>()

    val storeLocation: LiveData<List<StoreLocation>>
        get() = _storeLocation

    val storeCardStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

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
    locationInit()
}

    private fun locationInit() {
        if (store.storeId == ""){
            getStoreLocationResult()
        }

    }


    fun getStoreLocationResult(){
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getStoreLocation()

            _storeLocation.value = when (result) {
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





    fun storeCardClose(){
        storeCardStatus.value?.let {
            storeCardStatus.value = false
        }
    }

    fun storeCardOpen(){
        storeCardStatus.value?.let {
            storeCardStatus.value = true
        }
    }

}