package com.example.unitedpoultry.Authentications.fragments


import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.unitedpoultry.R
import com.example.unitedpoultry.databinding.FragmentCreateAccountBinding




class CreateAccountFragment : Fragment() {


    private lateinit var binding : FragmentCreateAccountBinding
    private var isRememberChecked = false
    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setClickListners()




    }

    private fun setClickListners(){
        with(binding) {

            tvLoginClick.setOnClickListener {
                findNavController().navigate(R.id.action_createAccount_to_login)
            }

            tvForgotPassword.setOnClickListener {
                findNavController().navigate(R.id.action_createAccount_to_forgot)
            }

            btnSignUp.setOnClickListener {
                if (validateInputs()) {

                    findNavController().navigate(R.id.action_createAccount_to_login)
                }

            }

            rememberLayout.setOnClickListener {
                isRememberChecked = !isRememberChecked

                binding.imgCheckbox.isSelected = isRememberChecked
                binding.imgCheckbox.refreshDrawableState()
            }

            imgTogglePassword.setOnClickListener {

                if (isPasswordVisible) {
                    // HIDE password
                    etPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    imgTogglePassword.setImageResource(R.drawable.eyevectorfinal) // eye closed
                    isPasswordVisible = false
                } else {
                    // SHOW password
                    etPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    imgTogglePassword.setImageResource(R.drawable.hidepasswordsvg) // eye open
                    isPasswordVisible = true
                }

                // Keep cursor at end (VERY IMPORTANT)
                etPassword.setSelection(etPassword.text.length)
            }

            imgToggleConfirmPassword.setOnClickListener {

                if (isConfirmPasswordVisible) {
                    // HIDE password
                    etConfirmPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                    imgToggleConfirmPassword.setImageResource(R.drawable.eyevectorfinal) // eye closed
                    isConfirmPasswordVisible = false
                } else {
                    // SHOW password
                    etConfirmPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                    imgToggleConfirmPassword.setImageResource(R.drawable.hidepasswordsvg) // eye open
                    isConfirmPasswordVisible = true
                }

                // Keep cursor at end (VERY IMPORTANT)
                etConfirmPassword.setSelection(etConfirmPassword.text.length)
            }
        }
    }

    private fun validateInputs(): Boolean {
        var valid = true

        binding.etEmailError.visibility = View.GONE
        binding.etFirstNameError.visibility = View.GONE
        binding.etLastNameError.visibility = View.GONE
        binding.etPasswordError.visibility = View.GONE
        binding.etConfirmPasswordError.visibility = View.GONE



        val email = binding.etEmail.text.toString().trim()
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()



        if (email.isEmpty()) {
            binding.etEmailError.visibility = View.VISIBLE
            binding.etEmailError.setText ("Email required")
            valid = false
        }
        if (firstName.isEmpty()) {
            binding.etFirstNameError.visibility = View.VISIBLE
            binding.etFirstNameError.setText ("First Name required")

            valid = false
        }
        if (lastName.isEmpty()) {
            binding.etLastNameError.visibility = View.VISIBLE
            binding.etLastNameError.setText ("Last Name required")
            valid = false
        }
        if (password.isEmpty()) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.setText ("password required")
            valid = false
        } else if (password.length < 6) {
            binding.etPasswordError.visibility = View.VISIBLE
            binding.etPasswordError.setText ("Password must greater then 6")
            valid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPasswordError.visibility = View.VISIBLE
            binding.etConfirmPasswordError.setText ("Enter Password to confirm")
            valid = false
        }else if (confirmPassword != password) {
            binding.etConfirmPasswordError.visibility = View.VISIBLE
            binding.etConfirmPasswordError.setText ("Password do not match")
            valid = false
        }
        return valid
    }

}
