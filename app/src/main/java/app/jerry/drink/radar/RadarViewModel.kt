package app.jerry.drink.radar

import android.util.Log
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
import java.text.NumberFormat

class RadarViewModel(private val repository: DrinkRepository, private val store: Store) : ViewModel() {

    private val _storeLocation = MutableLiveData<List<StoreLocation>>()

    val storeLocation: LiveData<List<StoreLocation>>
        get() = _storeLocation


    private val _storeComment = MutableLiveData<List<Comment>>()

    val storeComment: LiveData<List<Comment>>
        get() = _storeComment

    private val _selectStore = MutableLiveData<StoreLocation>()

    val selectStore: LiveData<StoreLocation>
        get() = _selectStore


    private val _newDrinkRank = MutableLiveData<List<DrinkRank>>()

    val newDrinkRank: LiveData<List<DrinkRank>>
        get() = _newDrinkRank

    val storeCardStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    val storeDrinkStatus = MutableLiveData<Boolean>().apply {
        value = false
    }

    val navigationToDetail = MutableLiveData<Drink>()

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
    getStoreLocationResult()
}

//    private fun locationInit() {
//        if (store.storeId == ""){
//            getStoreLocationResult()
//        }
//
//
//
//    }


    fun getStoreLocationResult(){
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getStoreLocation()

            _storeLocation.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE

                    if (store.storeId == ""){
                        result.data
                    }else{
                        result.data.filter { it.store.storeId == store.storeId }
                    }

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

    fun getStoreCommentResult(store: Store){
        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getStoreComment(store)

            _storeComment.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    Log.d("jerryTest","getStoreCommentResult = ${result.data}")
                    if (!result.data.isNullOrEmpty()){
                        getDrinkRank(result.data)
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

//    private fun storeFilter(listStoreLocation: List<StoreLocation>){
//
//        if (store.storeId == ""){
//
//        }else{
//            listStoreLocation.filter { it.store.storeId == store.storeId }
//        }
//
//    }




    private fun getDrinkRank(commnetList: List<Comment>) {

        val scoreRank = mutableListOf<DrinkRank>()
        for (commentUnit in commnetList) {
            /*-------------------------------------------------------------*/
            var haveId = false
            var position = -1

            for (checkId in scoreRank) {
                if (commentUnit.drink.drinkId == checkId.drink.drinkId) {
                    haveId = true
                    position = scoreRank.indexOf(checkId)
                }
            }

            if (haveId) {
                var scoreSum = 0F
                scoreRank[position].commentList.add(commentUnit)
                for (score in scoreRank[position].commentList) {
                    scoreSum += score.star
                }
                val avg: Float = scoreSum / scoreRank[position].commentList.size
                val numberFormat = NumberFormat.getNumberInstance()
                numberFormat.maximumFractionDigits = 1
                numberFormat.minimumFractionDigits = 1
                val avgStar = numberFormat.format(avg).toFloat()
                scoreRank[position].score = avgStar
            } else {
                val newDrinkRank = DrinkRank(
                    mutableListOf(commentUnit),
                    commentUnit.drink,
                    commentUnit.store,
                    commentUnit.star.toFloat()
                )
                scoreRank.add(newDrinkRank)
            }
        }
        /*---------------------------------------------------------------------*/
        scoreRank.sortByDescending { it.score }
        _newDrinkRank.value = scoreRank
    }


    val displayStoreLocation = Transformations.map(_selectStore){

        return@map "${it.store.storeName} - ${it.branchName}"
    }

    fun selectStore(storeLocation: StoreLocation){
            _selectStore.value = storeLocation
    }

    fun storeCardClose(){
        storeCardStatus.value?.let {
            storeCardStatus.value = false
        }
        storeDrinkStatus.value?.let {
            storeDrinkStatus.value = false
        }
    }

    fun storeCardOpen(){
        storeCardStatus.value?.let {
            storeCardStatus.value = true
        }
        storeDrinkStatus.value?.let {
            storeDrinkStatus.value = false
        }
    }

    fun storeDrinkStatus(){
        storeDrinkStatus.value?.let {
            storeDrinkStatus.value = !it
        }
    }

    fun navigationToDetail(drink: Drink) {
        navigationToDetail.value = drink
    }

    fun onDetailNavigated() {
        navigationToDetail.value = null
    }
}