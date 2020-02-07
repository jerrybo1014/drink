package app.jerry.drink.order

import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentAddOrderBinding
import app.jerry.drink.ext.getVmFactory
import java.util.*

class AddOrderFragment : DialogFragment() {

    lateinit var binding: FragmentAddOrderBinding

    private val viewModel by viewModels<AddOrderViewModel> { getVmFactory() }

    val TAG = "jerryTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setStyle(STYLE_NO_FRAME, R.style.SignInDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_add_order, container, false
        )

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val cal = Calendar.getInstance()

        binding.textSelectTime.setOnClickListener {
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val minute = cal.get(Calendar.MINUTE)
            TimePickerDialog(context!!, 3,{
                    _, hour, minute->
                binding.textSelectTime.text = String.format("%02d:%02d", hour, minute)
            }, hour, minute, true).show()
        }

        viewModel.getAllStoreResult()

        viewModel.selectTime.observe(this, Observer {
            Log.d(TAG,"selectTime = $it")
        })

        return binding.root
    }

}