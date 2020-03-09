package app.jerry.drink.dataclass.source.remote

import android.graphics.Bitmap
import android.icu.util.Calendar
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.*
import app.jerry.drink.dataclass.source.DrinkDataSource
import app.jerry.drink.signin.UserManager
import app.jerry.drink.util.Logger
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object DrinkRemoteDataSource : DrinkDataSource {

    private const val PATH_Stores = "stores"
    private const val PATH_Comments = "comments"
    private const val PATH_Orders = "orders"
    private const val PATH_Users = "users"
    private const val PATH_Menu = "menu"
    private const val PATH_Order_Lists = "lists"
    private const val Field_Order_Status = "status"
    private const val TAG = "jerryTest"

    override suspend fun checkUser(user: User): Result<Boolean> = suspendCoroutine { continuation ->
        val users = FirebaseFirestore.getInstance().collection(PATH_Users)
        val document = users.document(user.id)

        document
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let {
                        if (it.data.isNullOrEmpty()) {
                            document
                                .set(user).addOnSuccessListener {
                                    UserManager.user = user
                                    continuation.resume(Result.Success(true))
                                }
                        } else {
                            UserManager.user = it.toObject(User::class.java)
                            Logger.d("UserManager.user = ${UserManager.user}")
                            continuation.resume(Result.Success(true))
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

    override suspend fun getNewComment(): Result<List<Comment>> = suspendCoroutine { continuation ->

        val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
        val users = FirebaseFirestore.getInstance().collection(PATH_Users)

        comments
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list = mutableListOf<Comment>()
                    for (document in task.result!!) {
                        val newComment = document.toObject(Comment::class.java)
                        var user: User?

                        users.document(document.get("userId").toString()).get()
                            .addOnSuccessListener { userInsert ->
                                user = userInsert.toObject(User::class.java)
                                newComment.user = user
                                list.add(newComment)
                                if (list.size == task.result!!.size()) {
                                    list.sortByDescending { it.createdTime }
                                    continuation.resume(Result.Success(list))
                                }
                            }.addOnFailureListener {
                                Logger.d("getNewComment addOnFailureListener")
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

    override suspend fun deleteComment(comment: Comment): Result<Boolean> =
        suspendCoroutine { continuation ->
            val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
            val document = comments.document(comment.id)
            val userCurrent = FirebaseAuth.getInstance().currentUser

            if (userCurrent?.uid != "2ydu21IsBPSuZ5fb8znsxPhy7rv2") {
                continuation.resume(Result.Success(false))
            } else {
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

        }

    override suspend fun getAllStore(): Result<List<Store>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_Stores)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Store>()
                        for (document in task.result!!) {
                            val allStore = document.toObject(Store::class.java)
                            list.add(allStore)
                        }
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
                            val allStoreDrink = document.toObject(Drink::class.java)
                            list.add(allStoreDrink)
                        }
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

    override suspend fun postComment(comment: Comment, bitmap: Bitmap): Result<Boolean> =
        suspendCoroutine { continuation ->
            val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
            val document = comments.document()
            val storageReference = FirebaseStorage.getInstance().reference

            comment.id = document.id
            comment.createdTime = System.currentTimeMillis()

            val byteArrayOutput = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutput)
            val bytes = byteArrayOutput.toByteArray()

            val riversRef = storageReference.child("uploads/" + UUID.randomUUID().toString())
            val uploadTask = riversRef.putBytes(bytes)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
                if (!taskImage.isSuccessful) {
                    taskImage.exception?.let {
                        throw it
                    }
                }
                return@Continuation riversRef.downloadUrl
            }).addOnCompleteListener { taskImage ->
                if (taskImage.isSuccessful) {

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
            }
        }

    override suspend fun createOrder(order: Order): Result<String> =
        suspendCoroutine { continuation ->
            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)
            val document = orders.document("${Calendar.getInstance().timeInMillis}")

            order.id = document.id
            order.createdTime = Calendar.getInstance().timeInMillis
            document
                .set(order)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
//                    continuation.resume(Result.Success(list))
                        continuation.resume(Result.Success(order.id))
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

    override fun getOrderLive(orderId: Long): LiveData<Order> {
        val liveData = MutableLiveData<Order>()
        FirebaseFirestore.getInstance()
            .collection(PATH_Orders)
            .document("$orderId")
            .addSnapshotListener { snapshot, exception ->
                exception?.let {
                }
                val order = snapshot?.toObject(Order::class.java)
                liveData.value = order
            }
        return liveData
    }

    override fun getOrderItemLive(orderId: Long): LiveData<List<OrderItem>> {
        val liveData = MutableLiveData<List<OrderItem>>().apply {
            value = mutableListOf()
        }
        FirebaseFirestore.getInstance()
            .collection(PATH_Orders)
            .document("$orderId")
            .collection(PATH_Order_Lists)
            .addSnapshotListener { snapshot, exception ->
                exception?.let {
                }
                val list = mutableListOf<OrderItem>()
                for (document in snapshot!!) {
                    val orderItem = document.toObject(OrderItem::class.java)
                    list.add(orderItem)
                }
                liveData.value = list
            }
        return liveData
    }

    override suspend fun addOrder(orderList: OrderItem, orderId: Long): Result<Boolean> =
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
                .update(Field_Order_Status, editStatus)
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
        val user = User(userCurrent!!.uid, userCurrent.displayName, userCurrent.email, "")

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


    override suspend fun uploadAvatar(bitmap: Bitmap): Result<Boolean> =
        suspendCoroutine { continuation ->
            val users = FirebaseFirestore.getInstance().collection(PATH_Users)
            val userCurrent = UserManager.user
            val storageReference = FirebaseStorage.getInstance().reference

            val byteArrayOutput = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutput)
            val bytes = byteArrayOutput.toByteArray()

            val riversRef = storageReference.child("usersAvatar/" + UUID.randomUUID().toString())
            val uploadTask = riversRef.putBytes(bytes)

            UserManager.user?.let {
                uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
                    if (!taskImage.isSuccessful) {
                        taskImage.exception?.let {
                            throw it
                        }
                    }
                    return@Continuation riversRef.downloadUrl
                }).addOnCompleteListener { taskImage ->
                    if (taskImage.isSuccessful) {
                        val imageUri = taskImage.result.toString()

                        users
                            .document(it.id)
                            .update("image", imageUri)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    it.image = imageUri
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
                }
            }

        }

    override suspend fun getDetailComment(drink: Drink): Result<List<Comment>> =
        suspendCoroutine { continuation ->
            val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
            val users = FirebaseFirestore.getInstance().collection(PATH_Users)

            comments
                .whereEqualTo("drink", drink)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Comment>()
                        if (task.result!!.isEmpty) {
                            continuation.resume(Result.Success(list))
                        }
                        for (document in task.result!!) {
                            val detailComment = document.toObject(Comment::class.java)
                            var user: User?

                            users.document(document.get("userId").toString()).get()
                                .addOnSuccessListener { userInsert ->
                                    user = userInsert.toObject(User::class.java)
                                    Log.d(TAG, "users.addOnSuccessListener, user=$user")
                                    detailComment.user = user
                                    list.add(detailComment)
                                    if (list.size == task.result!!.size()) {
                                        Log.w(TAG, "last complete = $list")
                                        list.sortByDescending { it.createdTime }
                                        continuation.resume(Result.Success(list))
                                    }
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
                        Log.d(TAG, "getDetailComment else")
                        continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                    }
                }

        }

    override suspend fun getUserComment(): Result<List<Comment>> =
        suspendCoroutine { continuation ->
            val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
            val userCurrent = FirebaseAuth.getInstance().currentUser

            comments
                .whereEqualTo("userId", userCurrent?.uid)
                .get()
                .addOnCompleteListener { task ->
                    val list = mutableListOf<Comment>()
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val comment = document.toObject(Comment::class.java)
                            list.add(comment)
                        }
                        list.sortByDescending { it.createdTime }
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

    override suspend fun getUserOrder(): Result<List<Order>> =
        suspendCoroutine { continuation ->
            val orders = FirebaseFirestore.getInstance().collection(PATH_Orders)
            val userCurrent = FirebaseAuth.getInstance().currentUser
            var user = User("", "", "", "")
            userCurrent?.let {
                user = User(userCurrent.uid, userCurrent.displayName, userCurrent.email, "")
            }

            orders
                .whereEqualTo("user", user)
                .get()
                .addOnCompleteListener { task ->
                    val list = mutableListOf<Order>()
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val order = document.toObject(Order::class.java)
                            list.add(order)
                        }
                        list.sortByDescending { it.createdTime }
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

    override suspend fun getStoreLocation(): Result<List<StoreLocation>> =
        suspendCoroutine { continuation ->

            FirebaseFirestore.getInstance()
                .collectionGroup("branch")
                .get()
                .addOnCompleteListener { task ->
                    val list = mutableListOf<StoreLocation>()
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val branch = document.toObject(StoreLocation::class.java)
                            list.add(branch)
                        }
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

    override suspend fun getStoreComment(store: Store): Result<List<Comment>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collection(PATH_Comments)
                .whereEqualTo("store", store)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Comment>()
                        for (document in task.result!!) {
//                        Logger.d(document.id + " => " + document.data)
                            Log.d(TAG, "${document.id} => ${document.data}")
                            val comment = document.toObject(Comment::class.java)
                            list.add(comment)
                        }
                        Log.d(TAG, "$list")
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

    override suspend fun getSearchDrink(): Result<List<Drink>> =
        suspendCoroutine { continuation ->
            FirebaseFirestore.getInstance()
                .collectionGroup("menu")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val list = mutableListOf<Drink>()
                        for (document in task.result!!) {
                            val drink = document.toObject(Drink::class.java)
                            list.add(drink)
                        }
                        Log.d(TAG, "$list")
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

    override suspend fun addNewDrink(comment: Comment, bitmap: Bitmap): Result<Boolean> =
        suspendCoroutine { continuation ->

            val stores = FirebaseFirestore.getInstance()
                .collection(PATH_Stores)
                .document(comment.store.storeId)
                .collection("menu")
                .document()

            val documentId = comment.store.storeId + stores.id
            comment.drink.drinkId = documentId
            comment.createdTime = System.currentTimeMillis()

            FirebaseFirestore.getInstance()
                .collection(PATH_Stores)
                .document(comment.store.storeId)
                .collection("menu")
                .document()
                .set(comment.drink)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
/*================================================*/
                        val comments = FirebaseFirestore.getInstance().collection(PATH_Comments)
                        val document = comments.document()
                        val storageReference = FirebaseStorage.getInstance().reference

                        comment.id = document.id
                        val byteArrayOutput = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutput)
                        val bytes = byteArrayOutput.toByteArray()

                        val riversRef =
                            storageReference.child("uploads/" + UUID.randomUUID().toString())
                        val uploadTask = riversRef.putBytes(bytes)

                        uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { taskImage ->
                            if (!taskImage.isSuccessful) {
                                taskImage.exception?.let {
                                    throw it
                                }
                            }
                            return@Continuation riversRef.downloadUrl
                        }).addOnCompleteListener { taskImage ->
                            if (taskImage.isSuccessful) {
                                Logger.d("addNewDrink addOnCompleteListener")
                                comment.drinkImage = taskImage.result.toString()
                                document
                                    .set(comment)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            continuation.resume(Result.Success(true))
                                        } else {
                                            task.exception?.let {

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
/*================================================*/
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

    override suspend fun addStoreToDrink(store: Store): Result<Boolean> =
        suspendCoroutine { continuation ->
            val stores = FirebaseFirestore.getInstance().collection(PATH_Stores)
            stores
                .document(store.storeId)
                .collection(PATH_Menu)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (drink in task.result!!) {
                            val newDrink = drink.toObject(Drink::class.java)
                            newDrink.store = store
                            stores
                                .document(store.storeId)
                                .collection("menu")
                                .document(drink.id)
                                .set(newDrink)
                                .addOnSuccessListener {
                                    Log.d(TAG, "addOnSuccessListener , newDrink = $newDrink")
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

}