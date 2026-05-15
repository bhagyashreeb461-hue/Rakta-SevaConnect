package com.example.rakta_seva

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MyActivityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_activity)

        val btnBack = findViewById<ImageView>(R.id.btnBack)
        val btnMyRequests = findViewById<MaterialButton>(R.id.btnMyRequests)
        val btnMyResponses = findViewById<MaterialButton>(R.id.btnMyResponses)

        btnBack.setOnClickListener { finish() }

        btnMyRequests.setOnClickListener {
            updateTabs(btnMyRequests, btnMyResponses, true)
        }

        btnMyResponses.setOnClickListener {
            updateTabs(btnMyResponses, btnMyRequests, false)
        }
        
        // Initial state: Show My Requests if the user just dispatched one, 
        // but traditionally apps show "Responses" or the first tab. 
        // Let's show Requests first to see the data you just entered.
        updateTabs(btnMyRequests, btnMyResponses, true)
    }

    private fun updateTabs(selected: MaterialButton, unselected: MaterialButton, isRequests: Boolean) {
        selected.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK))
        selected.setTextColor(Color.WHITE)
        unselected.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
        unselected.setTextColor(Color.BLACK)
        
        val llItemsList = findViewById<LinearLayout>(R.id.llItemsList)
        val llEmptyState = findViewById<View>(R.id.llEmptyState)
        val ivEmpty = findViewById<ImageView>(R.id.ivEmptyState)
        val tvTitle = findViewById<TextView>(R.id.tvEmptyTitle)
        val tvSubtitle = findViewById<TextView>(R.id.tvEmptySubtitle)
        
        llItemsList.removeAllViews()
        
        val currentUserEmail = DataManager.currentUser?.email
        val data = if (isRequests) {
            DataManager.getRequests().filter { it.requesterEmail == currentUserEmail }
        } else {
            DataManager.getMyResponses()
        }
        
        if (data.isEmpty()) {
            llEmptyState.visibility = View.VISIBLE
            llItemsList.visibility = View.GONE
            if (isRequests) {
                ivEmpty.setImageResource(R.drawable.ic_request)
                tvTitle.text = "No emergencies posted"
                tvSubtitle.text = "Tap \"Request\" tab to dispatch your first\nalert."
            } else {
                ivEmpty.setImageResource(R.drawable.ic_check_circle)
                tvTitle.text = "No responses yet"
                tvSubtitle.text = "You haven't accepted or declined any\nrequests yet."
            }
        } else {
            llEmptyState.visibility = View.GONE
            llItemsList.visibility = View.VISIBLE
            data.forEach { request ->
                val itemView = layoutInflater.inflate(R.layout.item_request, llItemsList, false)
                itemView.findViewById<TextView>(R.id.tvBloodGroup).text = request.bloodGroup
                itemView.findViewById<TextView>(R.id.tvUrgency).text = "● ${request.urgency.uppercase()}"
                itemView.findViewById<TextView>(R.id.tvHospitalName).text = request.hospitalName
                itemView.findViewById<TextView>(R.id.tvPatientInfo).text = "For: ${request.patientName} • ${request.units} units"
                itemView.findViewById<TextView>(R.id.tvTimestamp).text = "Just now"
                
                // Color urgency text
                val urgencyColor = when(request.urgency.lowercase()) {
                    "critical" -> R.color.primary_red
                    "high" -> R.color.urgency_high
                    else -> R.color.text_gray
                }
                itemView.findViewById<TextView>(R.id.tvUrgency).setTextColor(getColor(urgencyColor))

                llItemsList.addView(itemView)
            }
        }
    }
}