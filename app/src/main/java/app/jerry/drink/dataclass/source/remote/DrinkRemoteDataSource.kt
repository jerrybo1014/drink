package app.jerry.drink.dataclass.source.remote

import android.icu.util.Calendar
import android.util.Log
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.Drink
import app.jerry.drink.dataclass.Result
import app.jerry.drink.dataclass.Store
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
    private const val PATH_Menu = "menu"
    private const val TAG = "jerryTest"
    private const val KEY_CREATED_TIME = "createdTime"

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
//                        val allStore = document.toObject(Store::class.java)
                        val storeName = document.data["storeName"].toString()
                        val storeId = document.data["storeId"].toString()
                        val store = Store(storeId,storeName)
                        list.add(store)
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
//                        val allStore = document.toObject(Store::class.java)
                        val drinkName = document.data["drinkName"].toString()
                        val drinkId = document.data["drinkId"].toString()
                        val drink = Drink(drinkId, drinkName)
                        list.add(drink)
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
//        val aa =""
//        var cc: DocumentReference =FirebaseFirestore.getInstance().document("users/$aa")
//        cc.get()


        comment.id = document.id
        comment.user.id = userCurrent!!.uid
        comment.user.email = userCurrent!!.email
        comment.user.name = userCurrent!!.displayName
//        FieldValue.serverTimestamp()
//        comment.createdTime = Calendar.getInstance().timeInMillis
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

}