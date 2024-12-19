package dev.abhishekan.reusablesapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import dev.abhishekan.reusablesapp.customer.CustomerActivity
import dev.abhishekan.reusablesapp.databinding.ActivityMainBinding
import dev.abhishekan.reusablesapp.debug_view.DebugViewActivity
import dev.abhishekan.reusablesapp.delivery_partner.DeliveryPartnerActivity
import dev.abhishekan.reusablesapp.restaurants.RestaurantUserActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.forRestaurantsBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    RestaurantUserActivity::class.java
                )
            )
        }

        binding.forCustomersBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CustomerActivity::class.java
                )
            )
        }

        binding.forDeliveryPartnersBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DeliveryPartnerActivity::class.java
                )
            )
        }

        binding.debugViewBtn.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    DebugViewActivity::class.java
                )
            )
        }

    }
}