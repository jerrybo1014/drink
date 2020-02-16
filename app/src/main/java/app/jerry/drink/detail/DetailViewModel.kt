package app.jerry.drink.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.DrinkDetail
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Star
import app.jerry.drink.dataclass.source.DrinkRepository
import app.jerry.drink.network.LoadApiStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.NumberFormat

class DetailViewModel(private val repository: DrinkRepository, private val drinkDetail: DrinkDetail) : ViewModel() {

    private val _detailComment = MutableLiveData<List<Comment>>()

    val detailComment: LiveData<List<Comment>>
        get() = _detailComment

    val drinkInformation = MutableLiveData<DrinkDetail>().apply {
        value = drinkDetail
    }

    val avgStar = MutableLiveData<String>()

    val star = MutableLiveData<Star>()

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
        getDetailCommentResult()
    }

    fun getDetailCommentResult() {

        coroutineScope.launch {

            _status.value = LoadApiStatus.LOADING

            val result = repository.getDetailComment(drinkDetail)

            _detailComment.value = when (result) {
                is Result.Success -> {
                    _error.value = null
                    _status.value = LoadApiStatus.DONE
                    calculateStar(result.data)
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

    fun calculateStar (detailComment: List<Comment>) {
        var totalStar: Float = 0F
        var oneStar = 0
        var twoStar = 0
        var threeStar = 0
        var fourStar = 0
        var fiveStar = 0

        for (star in detailComment){
            val starNow = star.star.toFloat()
            totalStar += starNow
            when (star.star){
                1 -> oneStar +=1
                2 -> twoStar +=1
                3 -> threeStar +=1
                4 -> fourStar +=1
                5 -> fiveStar +=1
            }
        }
        val avg: Float = totalStar / detailComment.size
        val numberFormat = NumberFormat.getNumberInstance()
        numberFormat.maximumFractionDigits = 1
        numberFormat.minimumFractionDigits = 1
        val avgStar = numberFormat.format(avg).toFloat()
        star.value = Star(oneStar,
            twoStar,
            threeStar,
            fourStar,
            fiveStar,
            avgStar,
            detailComment.size)

        Log.d("jerryTest","displayAvgStar = ${numberFormat.format(avg)}")
    }

}
