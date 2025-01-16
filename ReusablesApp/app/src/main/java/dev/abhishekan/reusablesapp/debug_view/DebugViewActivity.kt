package dev.abhishekan.reusablesapp.debug_view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.abhishekan.reusablesapp.databinding.ActivityDebugViewBinding
import dev.abhishekan.reusablesapp.delivery_partner.scanner.DeliveryPartnerScannerActivity
import dev.abhishekan.reusablesapp.vo.State
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class DebugViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugViewBinding
    private val viewModel by viewModels<DebugViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.clearAllOrdersBtn.setOnClickListener {
            viewModel.clearAllOrders()
        }

        lifecycleScope.launch {
            viewModel.clearAllOrdersResponse.collect {
                when (it) {
                    State.Empty -> {}
                    is State.Failed -> {
                        Timber.e("onCreate: ${it.message}")
                    }

                    State.Loading -> {}
                    is State.Success<*> -> {
                        Toast.makeText(
                            this@DebugViewActivity,
                            "Orders cleared!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

    }

}