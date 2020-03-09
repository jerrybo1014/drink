package app.jerry.drink.profile

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import app.jerry.drink.util.Util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: DrinkRepository) : ViewModel() {

    private val _userComment = MutableLiveData<List<Comment>>()

    val userComment: LiveData<List<Comment>>
        get() = _userComment

    private val _userOrder = MutableLiveData<List<Order>>()

    val userOrder: LiveData<List<Order>>
        get() = _userOrder

    var imageUri = MutableLiveData<Uri>()
    var imageBitmap = MutableLiveData<Bitmap>()

    var allCommentStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    val allOrderStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    val userCurrent = MutableLiveData<User>()
    val navigationToDetail = MutableLiveData<Drink>()
    val navigationToOrder = MutableLiveData<String>()

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
        imageUri.value = null
        getUserCurrentResult()
        getUserCommentResult()
        getUserOrderResult()
    }

    private fun getUserCurrentResult(){
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


    fun uploadAvatarResult() {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.uploadAvatar(imageBitmap.value!!)

            when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Toast.makeText(DrinkApplication.context, getString(R.string.post_success), Toast.LENGTH_SHORT).show()
                    uploadAvatarFinished()
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

    private fun getUserCommentResult(){
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUserComment()

            _userComment.value = when (result) {
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

    private fun getUserOrderResult(){
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getUserOrder()

            _userOrder.value = when (result) {
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

    fun uploadAvatarFinished(){
        imageUri.value = null
    }

    fun uploadAvatarCancel(){
        imageUri.value = null
        userCurrent.value = userCurrent.value!!
    }

    fun navigationToDetail(drink: Drink){
        navigationToDetail.value = drink
    }

    fun navigationToOrder(orderId: String){
        navigationToOrder.value = orderId
    }

    fun onDetailNavigated() {
        navigationToDetail.value = null
    }

    fun onOrderNavigated() {
        navigationToOrder.value = null
    }

    fun allCommentStatus(){
        allCommentStatus.value?.let {
            allCommentStatus.value = !it
        }
    }

    fun allOrderStatus(){
        allOrderStatus.value?.let {
            allOrderStatus.value = !it
        }
    }

}
