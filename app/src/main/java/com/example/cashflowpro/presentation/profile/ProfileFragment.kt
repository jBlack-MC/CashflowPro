package com.example.cashflowpro.presentation.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cashflowpro.*
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        view.findViewById<MaterialButton>(R.id.btnReports).setOnClickListener {
            startActivity(Intent(requireContext(), ReportsActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnAchievements).setOnClickListener {
            startActivity(Intent(requireContext(), BadgeActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnManageCategories).setOnClickListener {
            startActivity(Intent(requireContext(), CategoryActivity::class.java))
        }

        view.findViewById<MaterialButton>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
