package app.jerry.drink

import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import app.jerry.drink.dataclass.Comment
import app.jerry.drink.dataclass.OrderList
import app.jerry.drink.detail.DetailAdapter
import app.jerry.drink.ext.toDisplayFormat
import app.jerry.drink.home.NewCommentAdapter
import app.jerry.drink.order.OrderListsAdapter
import app.jerry.drink.post.IceAdapter
import app.jerry.drink.post.SugarAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
            )
            .into(imgView)
    }
}

@BindingAdapter("imageUrlCircle")
fun bindImageCircle(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions.circleCropTransform()
                    .placeholder(R.drawable.icons_36px_profile_image)
                    .error(R.drawable.icons_36px_profile_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("imageUrlCorner")
fun bindImageCorner(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions().transform(CenterCrop(),RoundedCorners(15))
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
            )
            .into(imgView)
    }
}

@BindingAdapter("timeToDisplayFormat")
fun bindDisplayFormatTime(textView: TextView, time: Long?) {
    textView.text = time?.toDisplayFormat()
}

@BindingAdapter("listNewComment")
fun bindNewComment(recyclerView: RecyclerView, data: List<Comment>?){
    val adapter = recyclerView.adapter as NewCommentAdapter
    adapter.submitList(data)
}

@BindingAdapter("listDetailComment")
fun bindDetailComment(recyclerView: RecyclerView, data: List<Comment>?){
    val adapter = recyclerView.adapter as DetailAdapter
    adapter.submitList(data)
}

@BindingAdapter("listOrder")
fun bindOrderLists(recyclerView: RecyclerView, data: List<OrderList>?){
    val adapter = recyclerView.adapter as OrderListsAdapter
    adapter.submitList(data)
}

@BindingAdapter("listIce")
fun bindIce(recyclerView: RecyclerView, data: List<String>?){
    val adapter = recyclerView.adapter as IceAdapter
    adapter.submitList(data)
}

@BindingAdapter("listSugar")
fun bindSugar(recyclerView: RecyclerView, data: List<String>?){
    val adapter = recyclerView.adapter as SugarAdapter
    adapter.submitList(data)
}