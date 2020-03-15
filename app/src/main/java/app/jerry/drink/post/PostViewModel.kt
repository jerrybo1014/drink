package app.jerry.drink.post

import android.graphics.Bitmap
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.*
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import app.jerry.drink.signin.UserManager
import app.jerry.drink.util.Logger
import app.jerry.drink.util.Util.getColor
import app.jerry.drink.util.Util.getString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PostViewModel(private val repository: DrinkRepository) : ViewModel() {

    // _allStore
    private val _allStore = MutableLiveData<List<Store>>()

    val allStore: LiveData<List<Store>>
        get() = _allStore

    // _comment
    private val _comment = MutableLiveData<Comment>().apply {
        UserManager.user?.let {
            value = Comment(userId = it.id, user = it, star = 1)
        }
    }

    val comment: LiveData<Comment>
        get() = _comment

    val selectStorePosition = MutableLiveData<Int>()

    val selectDrinkPosition = MutableLiveData<Int>()

    val chooseCameraGallery = MutableLiveData<Boolean>().apply {
        value = false
    }

    var addNewDrink: LiveData<Boolean> = Transformations.map(selectDrinkPosition) {
        _allStoreMenu.value?.let { allStoreMenu ->
            it >= allStoreMenu.size
        }
    }

    val newDrinkName = MutableLiveData<String>().apply {
        value = ""
    }

    // _allStoreMenu
    private val _allStoreMenu = MutableLiveData<List<Drink>>()

    val allStoreMenu: LiveData<List<Drink>>
        get() = _allStoreMenu

    var postFinished = MutableLiveData<Boolean>()
    var imageBitmap = MutableLiveData<Bitmap>()

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
        postFinished.value = false
        getAllStoreResult()
    }

    fun getAllStoreResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING
            val result = repository.getAllStore()

            _allStore.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    selectStorePosition.value = 0
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
                    selectDrinkPosition.value = 0
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

            _comment.value?.let { comment ->
                imageBitmap.value?.let { image ->
                    _postStatus.value = LoadApiStatus.LOADING

                    when (val result = repository.postComment(comment, image)) {
                        is Result.Success -> {
                            _error.value = null
                            _postStatus.value = LoadApiStatus.DONE
                            postFinished.value = true
                            Toast.makeText(
                                DrinkApplication.context,
                                getString(R.string.post_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            result.data
                        }
                        is Result.Fail -> {
                            _error.value = result.error
                            _postStatus.value = LoadApiStatus.ERROR
                        }
                        is Result.Error -> {
                            _error.value = result.exception.toString()
                            _postStatus.value = LoadApiStatus.ERROR
                        }
                        else -> {
                            _error.value =
                                DrinkApplication.instance.getString(R.string.you_know_nothing)
                            _postStatus.value = LoadApiStatus.ERROR
                        }
                    }
                    _refreshStatus.value = false
                }
            }
        }
    }

    fun addNewDrinkResult() {

        coroutineScope.launch {

            _comment.value?.let { comment ->
                imageBitmap.value?.let { image ->
                    newDrinkName.value?.let { newDrinkName ->
                        _postStatus.value = LoadApiStatus.LOADING

                        comment.drink = Drink("", newDrinkName, comment.store)

                        when (val result = repository.addNewDrink(comment, image)) {
                            is Result.Success -> {
                                _error.value = null
                                _postStatus.value = LoadApiStatus.DONE
                                postFinished.value = true
                                Toast.makeText(DrinkApplication.context, getString(R.string.post_success), Toast.LENGTH_SHORT)
                                    .show()
                                result.data
                            }
                            is Result.Fail -> {
                                _error.value = result.error
                                _postStatus.value = LoadApiStatus.ERROR
                            }
                            is Result.Error -> {
                                _error.value = result.exception.toString()
                                _postStatus.value = LoadApiStatus.ERROR
                            }
                            else -> {
                                _error.value =
                                    DrinkApplication.instance.getString(R.string.you_know_nothing)
                                _postStatus.value = LoadApiStatus.ERROR
                            }
                        }
                        _refreshStatus.value = false
                    }
                }
            }
        }
    }


    fun addStoreToDrinkResult(store: Store) {
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.addStoreToDrink(store)

            when (result) {
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
                    _error.value = DrinkApplication.instance.getString(R.string.you_know_nothing)
                    _status.value = LoadApiStatus.ERROR
                }
            }
            _refreshStatus.value = false
        }

    }

    fun openCameraGallery() {
        chooseCameraGallery.value = true
    }

    fun closeCameraGallery() {
        chooseCameraGallery.value = false
    }

    private var selectedIceView: View? = null
    private var selectedSugarView: View? = null

    fun selectIceStatus(view: View, string: String) {
        selectedIceView?.isSelected = false
        (selectedIceView as? TextView)?.setTextColor(getColor(R.color.bottom_navigation_light))
        selectedIceView = view
        selectedIceView?.isSelected = true
        (selectedIceView as? TextView)?.setTextColor(getColor(R.color.White))
        _comment.value?.let {
            it.ice = string
            _comment.value = it
        }
    }

    fun selectSugarStatus(view: View, string: String) {
        selectedSugarView?.isSelected = false
        (selectedSugarView as? TextView)?.setTextColor(getColor(R.color.bottom_navigation_light))
        selectedSugarView = view
        selectedSugarView?.isSelected = true
        (selectedSugarView as? TextView)?.setTextColor(getColor(R.color.White))
        _comment.value?.let {
            it.sugar = string
            _comment.value = it
        }
    }

    fun cancelAddNewDrink() {
        selectDrinkPosition.value = 0
    }

    fun postComment() {
        if (imageBitmap.value == null){
            Toast.makeText(DrinkApplication.context,getString(R.string.please_add_pic), Toast.LENGTH_SHORT).show()
        }else {
            if (!addNewDrink.value!!) {
                postCommentResult()
            } else {
                if (allStoreMenu.value!!.none { it.drinkName == newDrinkName.value!!.trim() }) {
                    addNewDrinkResult()
                } else {
                    Toast.makeText(
                        DrinkApplication.context,
                        getString(R.string.new_drink_exist),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    fun selectStore(position: Int) {
        _comment.value?.let { comment ->
            _allStore.value?.let { allStore ->
                comment.store = allStore[position]
                getStoreMenuResult(allStore[position])
            }
        }
    }

    fun selectDrink(position: Int) {
        _comment.value?.let { comment ->
            _allStoreMenu.value?.let { allStoreMenu ->
                if (allStoreMenu.size > position) {
                    comment.drink = allStoreMenu[position]
                } else {
                    newDrinkName.value = ""
                }
            }
        }
    }

    fun selectStar(star: Int) {
        _comment.value?.let { comment ->
            comment.star = star
        }
    }

    val newDrinkStatus = MediatorLiveData<Boolean>().apply {
        addSource(addNewDrink){checkNewDrinkStatus()}
        addSource(newDrinkName){checkNewDrinkStatus()}
    }

    private fun checkNewDrinkStatus() {
        addNewDrink.value?.let {
            newDrinkStatus.value = !(it && newDrinkName.value =="")
        }
    }

}
