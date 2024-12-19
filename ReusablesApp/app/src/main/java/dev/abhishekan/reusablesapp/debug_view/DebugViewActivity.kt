package dev.abhishekan.reusablesapp.debug_view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dev.abhishekan.reusablesapp.databinding.ActivityDebugViewBinding

class DebugViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDebugViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDebugViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}