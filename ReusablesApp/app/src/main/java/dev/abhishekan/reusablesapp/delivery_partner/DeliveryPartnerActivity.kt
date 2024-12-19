package dev.abhishekan.reusablesapp.delivery_partner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.abhishekan.reusablesapp.databinding.ActivityDeliveryPartnerBinding
import dev.abhishekan.reusablesapp.delivery_partner.scanner.DeliveryPartnerScannerActivity
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderResponse
import dev.abhishekan.reusablesapp.vo.State
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DeliveryPartnerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeliveryPartnerBinding
    private val viewModel by viewModels<DeliveryPartnerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeliveryPartnerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getLiveReturnOrders()

        binding.forPickingUpDeliveryOrdersBtn.setOnClickListener {
            Toast.makeText(this, "No extra steps required \uD83E\uDD73", Toast.LENGTH_SHORT).show()
        }

        lifecycleScope.launch {
            viewModel.getLiveReturnOrdersResponse.collect {
                when (it) {
                    State.Empty -> {}
                    is State.Failed -> {
                        Timber.tag("TAG").e("onCreate: ${it.message}")
                    }

                    State.Loading -> {}
                    is State.Success<*> -> {
                        it.data as List<CreateOrderResponse>
                        binding.forPickingUpReturnOrdersBtn.setOnClickListener { _ ->
                            if (it.data.isEmpty()) {
                                Toast.makeText(
                                    this@DeliveryPartnerActivity,
                                    "No orders available!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                startActivity(
                                    Intent(
                                        this@DeliveryPartnerActivity,
                                        DeliveryPartnerScannerActivity::class.java
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}