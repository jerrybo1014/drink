package app.jerry.drink.dataclass.source.remote

import android.icu.util.Calendar
import android.util.Log
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
    private const val PATH_Users = "users"
    private const val PATH_Menu = "menu"
    private const val TAG = "jerryTest"
    private const val KEY_CREATED_TIME = "createdTime"

    override suspend fun checkUser(): Result<Boolean> = suspendCoroutine { continuation ->
        val users = FirebaseFirestore.getInstance().collection(PATH_Users)
        val userCurrent = FirebaseAuth.getInstance().currentUser
        val document = users.document(userCurrent!!.uid)
        val user = User(userCurrent.uid,userCurrent.displayName,userCurrent.email,"")

        document
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    if (task.result!!.data == null){
                        document
                            .set(user).addOnSuccessListener {
                                Log.d("checkUser", "Success setting new user => $user") }
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
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun getNewComment(): Result<List<Comment>> = suspendCoroutine {continuation ->

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

                        Log.d(TAG, "for , document=$document")
                        var user: User? = User("","","","")
                        users.document(document.get("userId").toString()).get().addOnSuccessListener {

                            user = it.toObject(User::class.java)
                            Log.d(TAG, "addOnSuccessListener, user=$user")
                            Log.d(TAG, "${it.id} => ${it.data}")
                            val newComment = document.toObject(Comment::class.java)
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

                        Log.w("","[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }

    }

    override suspend fun getAllStore(): Result<List<Store>> = suspendCoroutine {continuation ->
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

                        Log.w("","[${this::class.simpleName}] Error getting documents. ${it.message}")
                        continuation.resume(Result.Error(it))
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }

    }

    override suspend fun getStoreMenu(store: Store): Result<List<Drink>> = suspendCoroutine { continuation ->
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
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun postComment(comment: Comment): Result<Boolean> = suspendCoroutine { continuation ->
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
                    }
                    continuation.resume(Result.Fail(DrinkApplication.instance.getString(R.string.you_know_nothing)))
                }
            }
    }

    override suspend fun addOrder(order: Order): Result<Boolean> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}