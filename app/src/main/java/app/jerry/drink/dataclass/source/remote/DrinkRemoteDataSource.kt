package app.jerry.drink.dataclass.source.remote

import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkDataSource
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.*
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
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Comment>()
                    for (document in task.result!!) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
                        val newComment = document.toObject(Comment::class.java)
                        Log.d(TAG, "comments document=$document")

                        var user: User? = User("", "", "", "")

                            users.document(document.get("userId").toString()).get()
                                .addOnSuccessListener {userInsert ->

                                    user = userInsert.toObject(User::class.java)
                                    Log.d(TAG, "users.addOnSuccessListener, user=$user")
                                    newComment.user = user
                                    list.add(newComment)
                                    if (list.size == task.result!!.size()) {
                                        Log.w(TAG, "last complete = $list")
                                        list.sortByDescending { it.createdTime }
                                        continuation.resume(Result.Success(list))
                                    }

                                }.addOnFailureListener {

                                    Log.d(TAG, "addOnFailureListener")
                                    val newComment = document.toObject(Comment::class.java)
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

//    override suspend fun getNewComment(): Result<List<Comment>> = suspendCoroutine { continuation ->
//
//        val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
//        val users = FirebaseFirestore.getInstance().collection(PATH_Users)
//        val commentUser = FirebaseAuth.getInstance().currentUser
//
//        comments
//            .orderBy(KEY_CREATED_TIME, Query.Direction.DESCENDING)
//            .get()
//            .addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    val list = mutableListOf<Comment>()
//                    for (document in task.result!!) {
////                        Log.d(TAG, "${document.id} => ${document.data}")
//                        val newComment = document.toObject(Comment::class.java)
//                        Log.d(TAG, "for , document=$document")
//                        list.add(newComment)
//                    }
//
//                    var i = -1
//                    var userList = mutableListOf<User>()
//                        users.get()
//                            .addOnSuccessListener {allUser->
//                                for (user in allUser ){
//                                    val userNow = user.toObject(User::class.java)
//                                    userList.add(userNow)
//                                }
//
//
//
//                            }
//
//                    continuation.resume(Result.Success(list))
//
//                } else {
//                    task.exception?.let {
//
//                        Log.w(
//                            "",
//                            "[${this::class.simpleName}] Error getting documents. ${it.message}"
//                        )
//                        continuation.resume(Result.Error(it))
//                        return@addOnCompleteListener
//                    }
//                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
//                }
//            }
//
//    }

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


//    override suspend fun postComment(comment: Comment): Result<Boolean> =
//        suspendCoroutine { continuation ->
//            val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
//            val userCurrent = FirebaseAuth.getInstance().currentUser
//            val document = comments.document()
//
//            val storageReference = FirebaseStorage.getInstance().reference
//
//            comment.id = document.id
//            comment.userId = userCurrent!!.uid
//
////            val ref = storageReference.child("uploads/" + UUID.randomUUID().toString())
////            val uploadTask = ref.putFile("")
//            val file = Uri.fromFile(File("content://com.android.providers.media.documents/document/image%3A159176"))
//            val riversRef = storageReference.child("uploads/" + UUID.randomUUID().toString())
//            val uploadTask = riversRef.putFile(file)
//
//            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
//                if (!taskImage.isSuccessful) {
//                    taskImage.exception?.let {
//                        throw it
//                    }
//                }
//                return@Continuation riversRef.downloadUrl
//            }).addOnCompleteListener { taskImage ->
//                if (taskImage.isSuccessful) {
//
////                    addUploadRecordToDb(downloadUri.toString())
//                    comment.drinkImage = taskImage.result.toString()
//
//                    document
//                        .set(comment)
//                        .addOnCompleteListener { task ->
//                            if (task.isSuccessful) {
////                    continuation.resume(Result.Success(list))
//                                continuation.resume(Result.Success(true))
//                            } else {
//                                task.exception?.let {
//
//                                    Log.w(
//                                        "",
//                                        "[${this::class.simpleName}] Error getting documents. ${it.message}"
//                                    )
//                                    continuation.resume(Result.Error(it))
//                                    return@addOnCompleteListener
//                                }
//                                continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
//                            }
//                        }
//
//
//                }
//
//            }.addOnFailureListener{
//
//            }
//
//        }


    override suspend fun postComment(comment: Comment, uri: Uri): Result<Boolean> =
        suspendCoroutine { continuation ->
            val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
            val userCurrent = FirebaseAuth.getInstance().currentUser
            val document = comments.document()
            val storageReference = FirebaseStorage.getInstance().reference

            comment.id = document.id
            comment.userId = userCurrent!!.uid

            val riversRef = storageReference.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = riversRef.putFile(uri)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
                if (!taskImage.isSuccessful) {
                    taskImage.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            }).addOnCompleteListener { taskImage ->
                if (taskImage.isSuccessful) {

//                    addUploadRecordToDb(downloadUri.toString())
                    comment.drinkImage = taskImage.result.toString()

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
                                continuation.resume(
                                    Result.Fail(
                                        DrinkApplication.instance.getString(
                                            R.string.you_know_nothing
                                        )
                                    )
                                )
                            }
                        }


                }

            }.addOnFailureListener {

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

    override suspend fun getOrder(orderId: Long): Result<OrderLists> =
        suspendCoroutine { continuation ->
            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)

            orders
                .document("$orderId")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                        val order = task.result!!.toObject(Order::class.java)
                        val allOrderList = mutableListOf<OrderList>()

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
                        Log.d(TAG, "getOrder = $orderLists")
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
                    Log.d(TAG, "getOrderLive = $orderList")
                }
                liveData.value = list
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

    override suspend fun removeOrder(orderId: Long, id: String): Result<Boolean> =
        suspendCoroutine { continuation ->
            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)
            val document = orders.document(orderId.toString()).collection("lists").document(id)

            document
                .delete()
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
                .update("status", editStatus)
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

    override suspend fun getUserCurrent(): Result<User> = suspendCoroutine { continuation ->
        val users = FirebaseFirestore.getInstance().collection(PATH_Users)
        val userCurrent = FirebaseAuth.getInstance().currentUser

        val user = User(userCurrent!!.uid,userCurrent.displayName,userCurrent.email,"")

        users
            .document(userCurrent.uid)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                    user.image = task.result?.data!!["image"].toString()
                    continuation.resume(Result.Success(user))
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


    override suspend fun uploadAvatar(uri: Uri): Result<Boolean> =
        suspendCoroutine { continuation ->
            val users = FirebaseFirestore.getInstance().collection(PATH_Users)
            val userCurrent = FirebaseAuth.getInstance().currentUser
            val storageReference = FirebaseStorage.getInstance().reference

            val riversRef = storageReference.child("usersAvatar/" + UUID.randomUUID().toString())
            val uploadTask = riversRef.putFile(uri)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
                if (!taskImage.isSuccessful) {
                    taskImage.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            }).addOnCompleteListener { taskImage ->
                if (taskImage.isSuccessful) {

//                    addUploadRecordToDb(downloadUri.toString())
                    val imageUri = taskImage.result.toString()

                    users
                        .document(userCurrent!!.uid)
                        .update("image",imageUri)
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
                                continuation.resume(
                                    Result.Fail(
                                        DrinkApplication.instance.getString(
                                            R.string.you_know_nothing
                                        )
                                    )
                                )
                            }
                        }


                }

            }.addOnFailureListener {

            }

        }

    override suspend fun getDetailComment(drinkDetail: DrinkDetail): Result<List<Comment>> = suspendCoroutine { continuation ->
        val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
        val users = FirebaseFirestore.getInstance().collection(PATH_Users)

        comments
            .whereEqualTo("drinkId", drinkDetail.drink?.drinkId)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Comment>()
                    for (document in task.result!!) {
//                        Log.d(TAG, "${document.id} => ${document.data}")
                        val detailComment = document.toObject(Comment::class.java)
                        Log.d(TAG, "comments document=$document")

                        var user: User? = User("", "", "", "")

                        users.document(document.get("userId").toString()).get()
                            .addOnSuccessListener {userInsert ->

                                user = userInsert.toObject(User::class.java)
                                Log.d(TAG, "users.addOnSuccessListener, user=$user")
                                detailComment.user = user
                                list.add(detailComment)
                                if (list.size == task.result!!.size()) {
                                    Log.w(TAG, "last complete = $list")
                                    list.sortByDescending { it.createdTime }
                                    continuation.resume(Result.Success(list))
                                }

                            }.addOnFailureListener {

                                Log.d(TAG, "addOnFailureListener")
                                val detailComment = document.toObject(Comment::class.java)
                                detailComment.user = user
                                list.add(detailComment)
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

}