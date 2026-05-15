package com.example.rakta_seva.fragments

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.rakta_seva.DataManager
import com.example.rakta_seva.DonorProfileActivity
import com.example.rakta_seva.R
import com.google.android.material.button.MaterialButton

class DonorsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_donors, container, false)

        val glDonorsBG = view.findViewById<GridLayout>(R.id.glDonorsBG)
        val tvClearFilter = view.findViewById<TextView>(R.id.tvClearFilter)

        for (i in 0 until glDonorsBG.childCount) {
            val child = glDonorsBG.getChildAt(i)
            if (child is MaterialButton) {
                child.setOnClickListener {
                    resetButtons(glDonorsBG)
                    child.setBackgroundTintList(ColorStateList.valueOf(Color.BLACK))
                    child.setTextColor(Color.WHITE)
                    
                    showDonors(view, child.text.toString())
                }
            }
        }

        tvClearFilter.setOnClickListener {
            resetButtons(glDonorsBG)
            hideDonors(view)
        }

        return view
    }

    private fun showDonors(root: View, bloodGroup: String) {
        root.findViewById<View>(R.id.nsvDonors).visibility = View.VISIBLE
        root.findViewById<View>(R.id.ivNoDonors).visibility = View.GONE
        root.findViewById<View>(R.id.tvNoDonors).visibility = View.GONE
        
        val list = root.findViewById<LinearLayout>(R.id.llDonorsList)
        list.removeAllViews()
        
        // Show real registered users first
        val realDonors = DataManager.getUsers().filter { 
            !it.isAdmin && it.isAvailable && it.bloodGroup == bloodGroup && it.email != DataManager.currentUser?.email 
        }
        
        realDonors.forEach { user ->
            addDonorCard(list, user.fullName, user.bloodGroup, "Near you", user.fullName.firstOrNull()?.toString() ?: "D", user.email, user.phone)
        }

        // Then show dummy data
        val dummyDonors = DataManager.getDummyDonors(bloodGroup)
        dummyDonors.forEach { donor ->
            addDonorCard(list, donor.name, donor.bloodGroup, donor.distance, donor.name.firstOrNull()?.toString() ?: "D", "donor@example.com", donor.phoneNumber)
        }
    }

    private fun addDonorCard(list: LinearLayout, name: String, bloodGroup: String, distance: String, initial: String, email: String, phone: String) {
        val donorView = layoutInflater.inflate(R.layout.item_donor, list, false)
        donorView.findViewById<TextView>(R.id.tvDonorName).text = name
        donorView.findViewById<TextView>(R.id.tvBloodGroup).text = bloodGroup
        donorView.findViewById<TextView>(R.id.tvDonorLocation).text = distance
        donorView.findViewById<TextView>(R.id.tvInitial).text = initial
        
        donorView.setOnClickListener {
            val intent = Intent(requireContext(), DonorProfileActivity::class.java)
            intent.putExtra("DONOR_NAME", name)
            intent.putExtra("BLOOD_GROUP", bloodGroup)
            intent.putExtra("DONOR_EMAIL", email)
            intent.putExtra("DONOR_PHONE", phone)
            startActivity(intent)
        }
        list.addView(donorView)
    }

    private fun hideDonors(root: View) {
        root.findViewById<View>(R.id.nsvDonors).visibility = View.GONE
        root.findViewById<View>(R.id.ivNoDonors).visibility = View.VISIBLE
        root.findViewById<View>(R.id.tvNoDonors).visibility = View.VISIBLE
    }

    private fun resetButtons(gl: GridLayout) {
        for (i in 0 until gl.childCount) {
            val child = gl.getChildAt(i)
            if (child is MaterialButton) {
                child.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE))
                child.setTextColor(Color.BLACK)
                child.setStrokeColor(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.divider_gray)))
            }
        }
    }
}