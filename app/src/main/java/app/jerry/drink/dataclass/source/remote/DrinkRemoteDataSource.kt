package app.jerry.drink.dataclass.source.remote

import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.model.DocumentKey
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DrinkRemoteDataSource : DrinkDataSource {

    private const val PATH_Stores = "stores"
    private const val PATH_Comments = "comments"
    private const val PATH_Orders = "orders"
    private const val PATH_Users = "users"
    private const val PATH_Menu = "menu"
    private const val TAG = "jerryTest"
    private const val KEY_CREATED_TIME = "createdTime"

    override suspend fun checkUser(): Result<Boolean> = suspendCoroutine { continuation ->
        val users = FirebaseFirestore.getInstance().collection(PATH_Users)
        val userCurrent = FirebaseAuth.getInstance().currentUser
        val document = users.document(userCurrent!!.uid)
        val user = User(userCurrent.uid, userCurrent.displayName, userCurrent.email, "")

        document
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (task.result!!.data == null) {
                        document
                            .set(user).addOnSuccessListener {
                                Log.d("checkUser", "Success setting new user => $user")
                            }
                            .addOnFailureListener {
                                Log.w("checkUser", "Error setting new user.", it)
                            }
                    }

                    continuation.resume(Result.Success(true))
                } else {
                    task.exception?.let {
                        Log.d("checkUser", "Error")
                        Log.w(
                            "",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun getNewComment(): Result<List<Comment>> = suspendCoroutine { continuation ->

        val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
        val users = FirebaseFirestore.getInstance().collection(PATH_Users)
        val commentUser = FirebaseAuth.getInstance().currentUser

        comments
            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Comment>()
                    for (document in task.result!!) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
                        val newComment = document.toObject(Comment::class.java)
                        Log.d(TAG, "for , document=$document")
                        var user: User? = User("", "", "", "")
                        users.document(document.get("userId").toString()).get()
                            .addOnSuccessListener {

                                user = it.toObject(User::class.java)
                                Log.d(TAG, "addOnSuccessListener, user=$user")
                                Log.d(TAG, "${it.id} => ${it.data}")
                                newComment.user = user
                                list.add(newComment)

                                if (list.size == task.result!!.size()) {
                                    Log.w(TAG, "last complete")
                                    continuation.resume(Result.Success(list))
                                }

                            }.addOnFailureListener {

                            Log.d(TAG, "addOnFailureListener")
                            val newComment = document.toObject(Comment::class.java)
//                        val newComment = document.toObject(Comment::class.java)
                            newComment.user = user
                            list.add(newComment)
                        }
                    }

                } else {
                    task.exception?.let {

                        Log.w(
                            "",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }

    }

    override suspend fun getAllStore(): Result<List<Store>> = suspendCoroutine { continuation ->
        FirebaseFirestore.getInstance()
            .collection(PATH_Stores)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Store>()
                    for (document in task.result!!) {
//                        Logger.d(document.id + " => " + document.data)
                        Log.d(TAG, "${document.id} => ${document.data}")
                        val allStore = document.toObject(Store::class.java)

                        list.add(allStore)
                    }
                    Log.d(TAG, "$list")
//                    continuation.resume(Result.Success(list))
                    continuation.resume(Result.Success(list))
                } else {
                    task.exception?.let {

                        Log.w(
                            "",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }

    }

    override suspend fun getStoreMenu(store: Store): Result<List<Drink>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_Stores)
                .document(store.storeId)
                .collection(PATH_Menu)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Drink>()
                        for (document in task.result!!) {
//                        Logger.d(document.id + " => " + document.data)
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val allStoreDrink = document.toObject(Drink::class.java)

                            list.add(allStoreDrink)
                        }
                        Log.d(TAG, "$list")
//                    continuation.resume(Result.Success(list))
                        continuation.resume(Result.Success(list))
                    } else {
                        task.exception?.let {

                            Log.w(
                                "",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun postComment(comment: Comment): Result<Boolean> =
        suspendCoroutine { continuation ->
            val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
            val userCurrent = FirebaseAuth.getInstance().currentUser
            val document = comments.document()

            comment.id = document.id
            comment.userId = userCurrent!!.uid

            document
                .set(comment)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Log.w(
                                "",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun createOrder(order: Order): Result<Boolean> =
        suspendCoroutine { continuation ->
            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)
            val userCurrent = FirebaseAuth.getInstance().currentUser
            val document = orders.document("${Calendar.getInstance().timeInMillis}")

            order.id = document.id
            order.createdTime = Calendar.getInstance().timeInMillis
            userCurrent?.let {
                order.user = User(it.uid, it.displayName, it.email, "")
            }

            document
                .set(order)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Log.w(
                                "",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun getOrder(orderId: Long): Result<OrderLists> = suspendCoroutine { continuation ->
        val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)

        orders
            .document("$orderId")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                    val order = task.result!!.toObject(Order::class.java)
                    val allOrderList = mutableListOf<OrderList>()
                    Log.d(TAG,"addOnCompleteListener")

//                    orders
//                        .document("$orderId").collection("lists").get().addOnSuccessListener {orderListTask->
//                            for (document in orderListTask) {
////                        Logger.d(document.id + " => " + document.data)
//                                Log.d(TAG, "${document.id} => ${document.data}")
//                                val orderList = document.toObject(OrderList::class.java)
//                                allOrderList.add(orderList)
//
//                                if (allOrderList.size == orderListTask.size()){
//                                    val orderLists = OrderLists(order, allOrderList)
//                                    Log.d(TAG,"getOrder = $orderLists")
//                                    continuation.resume(Result.Success(orderLists))
//                                }
//
//                            }
//
//                        }


                    val orderLists = OrderLists(order, listOf())
                    Log.d(TAG,"getOrder = $orderLists")
                    continuation.resume(Result.Success(orderLists))
                } else {
                    task.exception?.let {

                        Log.w(
                            "",
                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
                        )
                        continuation.resume(Result.Error(it))
                        return@addOnCompleteListener
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    /*LiveDataTest*/
//    override suspend fun getOrder(orderId: Long): Result<OrderLists> =
//        suspendCoroutine { continuation ->
//            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)
//            val liveData = MutableLiveData<List<OrderList>>().apply {
//                value = mutableListOf()
//            }
//
//            orders
//                .document("$orderId")
//                .get()
//                .addOnCompleteListener { task ->
//                    if (task.isSuccessful) {
////                    continuation.resume(Result.Success(list))
//                        val order = task.result!!.toObject(Order::class.java)
//
//                    val orderLists = OrderLists(order, listOf())
//                    Log.d(TAG,"getOrder = $orderLists")
//                    continuation.resume(Result.Success(orderLists))
//
//                    } else {
//                        task.exception?.let {
//
//                            Log.w(
//                                "",
//                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
//                            )
//                            continuation.resume(Result.Error(it))
//                            return@addOnCompleteListener
//                        }
//                        continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
//                    }
//                }
//        }

    override fun getOrderLive(orderId: Long): LiveData<List<OrderList>> {
        val liveData = MutableLiveData<List<OrderList>>().apply {
            value = mutableListOf()
        }

        FirebaseFirestore.getInstance()
            .collection(PATH_Orders)
            .document("$orderId")
            .collection("lists")
            .addSnapshotListener { snapshot, exception ->
                exception?.let {
                    Log.w(TAG, "Listen failed.", it)
                }
                val list = mutableListOf<OrderList>()

                for (document in snapshot!!) {
                    val orderList = document.toObject(OrderList::class.java)
                    list.add(orderList)
                    Log.d(TAG,"getOrderLive = $orderList")
                }
                liveData.value = list
                Log.d(TAG,"liveData = ${liveData.value}")

            }

        return liveData
    }

    override suspend fun addOrder(orderList: OrderList, orderId: Long): Result<Boolean> =
        suspendCoroutine { continuation ->
            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)
            val document = orders.document(orderId.toString()).collection("lists").document()
            val userCurrent = FirebaseAuth.getInstance().currentUser

            orderList.id = document.id
            orderList.user = User(userCurrent!!.uid, userCurrent.displayName, userCurrent.email, "")

            document
                .set(orderList)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Log.w(
                                "",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

    override suspend fun editOrderStatus(orderId: Long, editStatus: Boolean): Result<Boolean> =
        suspendCoroutine { continuation ->
            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)
            val document = orders.document(orderId.toString())

            document
                .update("status",editStatus)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                        continuation.resume(Result.Success(true))
                    } else {
                        task.exception?.let {

                            Log.w(
                                "",
                                "[${this::class.simpleName}] Error getting documents. ${it.message}"
                            )
                            continuation.resume(Result.Error(it))
                            return@addOnCompleteListener
                        }
                        continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }
        }

}