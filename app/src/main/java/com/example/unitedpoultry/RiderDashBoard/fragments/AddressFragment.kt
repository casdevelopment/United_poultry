package com.example.unitedpoultry.RiderDashBoard.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.unitedpoultry.RiderDashBoard.Area.Adapter.AreaAdapter
import com.example.unitedpoultry.RiderDashBoard.Area.model.AreaModel
import com.example.unitedpoultry.databinding.FragmentAddressBinding

class AddressFragment : Fragment() {

    private lateinit var binding: FragmentAddressBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🧪 Dummy data
        val areaList = listOf(
            AreaModel("Gulberg", "Lahore, Punjab", "17", "7", "10"),
            AreaModel("Johar Town", "Lahore, Punjab", "12", "5", "7"),
            AreaModel("DHA Phase 5", "Lahore, Punjab", "20", "10", "10"),
            AreaModel("Model Town", "Lahore, Punjab", "15", "8", "7")
        )

        binding.rvAreas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAreas.adapter = AreaAdapter(areaList)

        binding.tvAreasCount.text = "${areaList.size} areas"
    }
}
