package app.jerry.drink.order.addorder

import android.os.Bundle
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
import app.jerry.drink.util.Logger
import app.jerry.drink.util.Util


class AddOrderFragment : DialogFragment() {

    private val viewModel by viewModels<AddOrderViewModel> { getVmFactory(
        AddOrderFragmentArgs.fromBundle(
            arguments!!
        ).order) }
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

        binding.addOrderSpinnerDrink.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
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

        val listIce = DrinkApplication.context.resources.getStringArray(R.array.list_ice).toList()
        val listSugar = DrinkApplication.context.resources.getStringArray(R.array.list_sugar).toList()
        val sugarAdapter = SugarAddOrderAdapter(viewModel)
        val iceAdapter = IceAddOrderAdapter(viewModel)

        binding.addOrderRecyclerIce.adapter = iceAdapter
        binding.addOrderRecyclerSugar.adapter = sugarAdapter

        sugarAdapter.submitList(listSugar)
        iceAdapter.submitList(listIce)

        viewModel.addOrderFinished.observe(this, Observer {
            if (it != null && it == true){
                Toast.makeText(DrinkApplication.context, Util.getString(R.string.post_success), Toast.LENGTH_SHORT).show()
                dismiss() }
        })

        viewModel.leave.observe(this, Observer {
            if (!it){
                dismiss() }
        })

        return binding.root
    }

}