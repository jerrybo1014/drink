package app.jerry.drink.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.network.LoadApiStatus
import app.jerry.drink.R
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.DrinkRank
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.source.DrinkRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.NumberFormat

class HomeViewModel(private val repository: DrinkRepository) : ViewModel() {

    private val _newComment = MutableLiveData<List<Comment>>()

    val newComment: LiveData<List<Comment>>
        get() = _newComment

    private val _newDrinkRank = MutableLiveData<List<DrinkRank>>()

    val newDrinkRank: LiveData<List<DrinkRank>>
        get() = _newDrinkRank


    val navigationToDetail = MutableLiveData<DrinkDetail>()

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
        getNewCommentResult()
    }

    fun getNewCommentResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getNewComment()

            _newComment.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    getDrinkRank(result.data)
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


    fun getDrinkRank(commnetList: List<Comment>) {

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

    fun navigationToDetail(drinkDetail: DrinkDetail) {
        navigationToDetail.value = drinkDetail
    }

    fun onDetailNavigated() {
        navigationToDetail.value = null
    }


}

