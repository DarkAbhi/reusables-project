package dev.abhishekan.reusablesapp.restaurants

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import dagger.hilt.android.AndroidEntryPoint
import dev.abhishekan.reusablesapp.databinding.ActivityRestaurantUserBinding
import dev.abhishekan.reusablesapp.restaurants.models.CreateOrderRequest
import dev.abhishekan.reusablesapp.vo.State
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class RestaurantUserActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: ActivityRestaurantUserBinding
    private val viewModel by viewModels<RestaurantUserViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupQrCodeScanner()

        viewModel.scannedCodes.observe(this) { codes ->
            binding.scannedCountTv.isInvisible = codes.isEmpty()
            binding.clearScannedBtn.isInvisible = codes.isEmpty()
            binding.scannedCountTv.text = "Scanned: ${codes.size}"
        }

        binding.clearScannedBtn.setOnClickListener {
            viewModel.clearScannedCodes()
        }

        binding.createOrderBtn.setOnClickListener {
            val itemUuids = viewModel.scannedCodes.value ?: emptyList()

            if (itemUuids.isEmpty()) {
                Toast.makeText(this, "No items to create an order.", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.createOrder(CreateOrderRequest(item_uuids = itemUuids))
            }
        }

        lifecycleScope.launch {
            viewModel.createOrderResponse.collect {
                when (it) {
                    State.Empty -> {}
                    is State.Failed -> {
                        Toast.makeText(
                            this@RestaurantUserActivity,
                            it.message,
                            Toast.LENGTH_LONG
                        ).show()
                        Timber.tag("TAG").e("onCreate: ${it.message}")
                    }

                    State.Loading -> {}
                    is State.Success<*> -> {
                        Toast.makeText(
                            this@RestaurantUserActivity,
                            "Order packed!",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.clearScannedCodes()
                        finish()
                    }
                }
            }
        }
    }

    private fun setupQrCodeScanner() {
        codeScanner = CodeScanner(this, binding.scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                val scannedCodes = viewModel.scannedCodes.value ?: emptyList()

                if (scannedCodes.contains(it.text)) {
                    Toast.makeText(this, "Item already scanned.", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                    viewModel.addScannedCode(it.text) // Add the scanned code
                }
            }
        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}