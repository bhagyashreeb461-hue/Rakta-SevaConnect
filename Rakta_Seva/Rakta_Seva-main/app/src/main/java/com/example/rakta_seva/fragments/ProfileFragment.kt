package com.example.rakta_seva.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.rakta_seva.DataManager
import com.example.rakta_seva.LocationUtils
import com.example.rakta_seva.MainActivity
import com.example.rakta_seva.MyActivityActivity
import com.example.rakta_seva.NotificationsActivity
import com.example.rakta_seva.R
import com.google.android.material.materialswitch.MaterialSwitch

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val user = DataManager.currentUser
        if (user != null) {
            view.findViewById<TextView>(R.id.tvProfileName).text = user.fullName
            view.findViewById<TextView>(R.id.tvProfileEmail).text = user.email
            view.findViewById<TextView>(R.id.tvProfileInitial).text = user.fullName.firstOrNull()?.toString() ?: "D"
            view.findViewById<TextView>(R.id.tvProfileBloodGroup).text = user.bloodGroup
            view.findViewById<TextView>(R.id.tvProfileLocation).text = user.location
            
            val switchAvailability = view.findViewById<MaterialSwitch>(R.id.switchAvailability)
            switchAvailability.isChecked = user.isAvailable
            switchAvailability.setOnCheckedChangeListener { _, isChecked ->
                DataManager.updateAvailability(isChecked)
                val status = if (isChecked) "Available" else "Unavailable"
                Toast.makeText(context, "Status updated to $status", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<View>(R.id.cvMyActivity).setOnClickListener {
            startActivity(Intent(requireContext(), MyActivityActivity::class.java))
        }

        view.findViewById<View>(R.id.cvNotifications).setOnClickListener {
            startActivity(Intent(requireContext(), NotificationsActivity::class.java))
        }
        
        view.findViewById<View>(R.id.cvUpdateLocation).setOnClickListener {
            LocationUtils.getCurrentLocationName(requireContext()) { location ->
                view.findViewById<TextView>(R.id.tvProfileLocation).text = location
                Toast.makeText(context, "Location updated to $location", Toast.LENGTH_SHORT).show()
            }
        }
        
        view.findViewById<View>(R.id.cvLogDonation).setOnClickListener {
            Toast.makeText(context, "Donation logged! 90-day cooldown started.", Toast.LENGTH_LONG).show()
        }
        
        view.findViewById<View>(R.id.cvRefreshStatus).setOnClickListener {
            Toast.makeText(context, "Status Refreshed", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.cvEligibility).setOnClickListener {
            Toast.makeText(context, "You are eligible to donate!", Toast.LENGTH_SHORT).show()
        }

        view.findViewById<View>(R.id.cvLogOut).setOnClickListener {
            DataManager.logout()
            startActivity(Intent(requireContext(), MainActivity::class.java))
            requireActivity().finish()
        }

        return view
    }
}