package com.example.unitedpoultry.AdminDashBoard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.AdminArea.Adapter.AdminAreaAdapter
import com.example.unitedpoultry.AdminArea.AddNewAreaActivity
import com.example.unitedpoultry.AdminArea.model.AdminAreaModel
import com.example.unitedpoultry.databinding.FragmentAreasAdminBinding

class AreasAdminFragment : Fragment() {

    private lateinit var binding: FragmentAreasAdminBinding
    private lateinit var adapter: AdminAreaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAreasAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Dummy data
        val areaList = listOf(
            AdminAreaModel("Gulberg", "Lahore, Punjab", "17", "7", "10000"),
            AdminAreaModel("Johar Town", "Lahore, Punjab", "12", "5", "72000"),
            AdminAreaModel("DHA Phase 5", "Lahore, Punjab", "20", "5", "23100"),
            AdminAreaModel("Model Town", "Lahore, Punjab", "15", "8", "767000")
        )

        // Setup RecyclerView
        adapter = AdminAreaAdapter(areaList)
        binding.rvAreas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAreas.adapter = adapter

        // Show total areas
        binding.tvAreasCount.text = "${areaList.size} areas"

        // Search functionality
        binding.etSearch.addTextChangedListener { editable ->
            adapter.filter(editable.toString())
        }
        binding.fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddNewAreaActivity::class.java)
            startActivity(intent)
        }

    }
}
