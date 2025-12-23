package com.example.unitedpoultry.Welcome.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentOnBoardForthBinding
import com.example.unitedpoultry.databinding.FragmentOnBoardSecondBinding
import com.example.unitedpoultry.databinding.FragmentOnBoardThirdBinding


class OnBoardThirdFragment : Fragment() {


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
            findNavController().navigate(R.id.action_third_to_forth)
        }

    }

}
