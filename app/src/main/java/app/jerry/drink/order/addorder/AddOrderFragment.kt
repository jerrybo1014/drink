package app.jerry.drink.order.addorder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import app.jerry.drink.DrinkApplication
import app.jerry.drink.R
import app.jerry.drink.databinding.FragmentAddOrderBinding
import app.jerry.drink.ext.getVmFactory


class AddOrderFragment : DialogFragment() {

    private val viewModel by viewModels<AddOrderViewModel> { getVmFactory(
        AddOrderFragmentArgs.fromBundle(
            arguments!!
        ).orderLists) }
    lateinit var binding: FragmentAddOrderBinding
    val TAG = "jerryTest"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AddOrderDialog)
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

        viewModel.getStoreMenuResult()

        binding.spinnerDrink.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.selectedDrink(position)
            }
        }




        val listIce = listOf<String>("正常冰","去冰","微冰","常溫")
        val listSugar = listOf<String>("正常糖","半糖","微糖","無糖")
        val sugarAdapter = SugarAddOrderAdapter(viewModel)
        val iceAdapter = IceAddOrderAdapter(viewModel)

        binding.recyclerIce.adapter = iceAdapter
        binding.recyclerSugar.adapter = sugarAdapter

        sugarAdapter.submitList(listSugar)
        iceAdapter.submitList(listIce)

        viewModel.selectedQty.observe(this, Observer {
            Log.d(TAG,"selectedQty = $it")
        })

        viewModel.addOrderfinished.observe(this, Observer {
            if (it != null && it == true){
                Toast.makeText(DrinkApplication.context, "成功送出", Toast.LENGTH_SHORT).show()
                dismiss() }
        })

        viewModel.leave.observe(this, Observer {
            if (!it){
                dismiss() }
        })

        return binding.root
    }

}