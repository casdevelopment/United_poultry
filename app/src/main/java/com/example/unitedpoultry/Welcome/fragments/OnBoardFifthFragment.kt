package com.example.unitedpoultry.Welcome.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentOnBoardFifthBinding


class OnBoardFifthFragment : Fragment() {


    private lateinit var binding : FragmentOnBoardFifthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardFifthBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary)

        // Optional: Change status bar icons to dark if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.window?.decorView?.systemUiVisibility = 0 // light icons: 0, dark icons: View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_fifth_to_forth)
        }

        binding.btnNext.setOnClickListener {

            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            startActivity(intent)
        }

        binding.btnContinueEmail.setOnClickListener {

            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()

        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
    }


}
