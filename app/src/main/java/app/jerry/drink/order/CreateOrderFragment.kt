package app.jerry.drink.order

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentCreateOrderBinding
import app.jerry.drink.ext.getVmFactory
import java.util.*

class CreateOrderFragment : DialogFragment() {

    lateinit var binding: FragmentCreateOrderBinding

    private val viewModel by viewModels<CreateOrderViewModel> { getVmFactory() }

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
            inflater, R.layout.fragment_create_order, container, false
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

        binding.spinnerStore.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.selectedStore(position)
            }
        }


        viewModel.getAllStoreResult()

        viewModel.createOrderFinished.observe(this, Observer {
            if (it != null && it == true){
                dismiss()
            }
        })

        return binding.root
    }

}