package com.example.rakta_seva

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NotificationsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)

        findViewById<ImageView>(R.id.btnBack).setOnClickListener { finish() }

        refreshNotifications()
    }

    private fun refreshNotifications() {
        val listContainer = findViewById<LinearLayout>(R.id.llNotificationsList)
        listContainer.removeAllViews()
        val notifications = DataManager.getNotifications()

        if (notifications.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No notifications yet"
            emptyText.textAlignment = View.TEXT_ALIGNMENT_CENTER
            emptyText.setPadding(0, 100, 0, 0)
            listContainer.addView(emptyText)
            return
        }

        notifications.forEach { notification ->
            val view = layoutInflater.inflate(R.layout.item_notification, listContainer, false)
            view.findViewById<TextView>(R.id.tvNotifTitle).text = notification.title
            view.findViewById<TextView>(R.id.tvNotifMessage).text = notification.message

            val actions = view.findViewById<View>(R.id.llNotifActions)
            val statusText = view.findViewById<TextView>(R.id.tvNotifStatus)

            if (notification.type == "DIRECT_REQUEST" && notification.status == "PENDING") {
                actions.visibility = View.VISIBLE
                statusText.visibility = View.GONE

                view.findViewById<Button>(R.id.btnNotifAccept).setOnClickListener {
                    DataManager.updateNotificationStatus(notification.id, "ACCEPTED")
                    Toast.makeText(this, "Request Accepted!", Toast.LENGTH_SHORT).show()
                    refreshNotifications()
                }

                view.findViewById<Button>(R.id.btnNotifReject).setOnClickListener {
                    DataManager.updateNotificationStatus(notification.id, "REJECTED")
                    Toast.makeText(this, "Request Declined", Toast.LENGTH_SHORT).show()
                    refreshNotifications()
                }
            } else {
                actions.visibility = View.GONE
                if (notification.status != "PENDING") {
                    statusText.visibility = View.VISIBLE
                    statusText.text = notification.status
                    if (notification.status == "REJECTED") {
                        statusText.setTextColor(getColor(R.color.primary_red))
                    } else {
                        statusText.setTextColor(getColor(R.color.success_green))
                    }
                } else {
                    statusText.visibility = View.GONE
                }
            }

            listContainer.addView(view)
        }
    }
}