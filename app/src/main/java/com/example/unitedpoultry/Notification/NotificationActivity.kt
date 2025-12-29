package com.example.unitedpoultry.Notification

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.BaseActivity
import com.example.unitedpoultry.Notification.Adapter.NotificationAdapter
import com.example.unitedpoultry.Notification.model.NotificationModel
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.ActivityNotificationBinding

class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureStatusBar(
            isLightBackground = true,
            colorResId = android.R.color.white
        )

        // Dummy data
        val dummyList = listOf(
            NotificationModel(R.drawable.locationvector, "New Order", "You have a new order", "10:00 AM"),
            NotificationModel(R.drawable.locationvector, "Reminder", "Check your pending tasks", "09:30 AM"),
            NotificationModel(R.drawable.locationvector, "Update", "App updated to v2.0", "Yesterday"),
            NotificationModel(R.drawable.locationvector, "Alert", "Server maintenance", "2 days ago")
        )

        val adapter = NotificationAdapter(dummyList)
        binding.rvNotifications.layoutManager = LinearLayoutManager(this)
        binding.rvNotifications.adapter = adapter

        // Back button
        binding.backArrow.setOnClickListener {
            finish()
        }
    }
}
