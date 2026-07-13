package com.example.unitedpoultry.Welcome.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.Authentications.AuthenticationActivity
import com.example.unitedpoultry.R
import com.example.unitedpoultry.SessionManager
import com.example.unitedpoultry.databinding.FragmentOnBoardForthBinding
import com.example.unitedpoultry.databinding.FragmentOnBoardSecondBinding
import com.example.unitedpoultry.databinding.FragmentOnBoardThirdBinding
import org.koin.android.ext.android.inject


class OnBoardThirdFragment : Fragment() {

    private val sessionManager: SessionManager by inject()


    private lateinit var binding : FragmentOnBoardThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardThirdBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnNext.setOnClickListener {
           // findNavController().navigate(R.id.action_third_to_forth)

            sessionManager.setFirstTimeLaunch(false)
            Log.d("SPLASH_DEBUG", "in third onboarding marking flag to false")


            val intent = Intent(requireContext(), AuthenticationActivity::class.java)
            startActivity(intent)

            // 3. CRUCIAL: Close the WelcomeActivity so they cannot back-button into onboarding
            requireActivity().finish()
        }

    }

}
