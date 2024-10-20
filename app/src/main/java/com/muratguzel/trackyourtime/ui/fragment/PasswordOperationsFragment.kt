package com.muratguzel.trackyourtime.ui.fragment

import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.muratguzel.trackyourtime.databinding.FragmentPasswordOperationsBinding
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel
import com.muratguzel.trackyourtime.ui.viewModel.SettingsViewModel

class PasswordOperationsFragment : Fragment() {

    private var _binding: FragmentPasswordOperationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private lateinit var settingsViewModel: SettingsViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentPasswordOperationsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.registerButton.setOnClickListener {
            passwordUpdateOperations()
        }
        binding.tvOldPasswordShow.setOnClickListener {
            val currentCursorPosition = binding.etOldPassword.selectionStart
            if (binding.tvOldPasswordShow.text.toString() == "Göster"){
                binding.etOldPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvOldPasswordShow.text = "Gizle"
            }else{
                binding.etOldPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.tvOldPasswordShow.text = "Göster"
            }
            binding.etOldPassword.setSelection(currentCursorPosition)
        }
        binding.tvNewPasswordShow.setOnClickListener {
            val currentCursorPosition = binding.etNewPassowrd.selectionStart
            if (binding.tvNewPasswordShow.text.toString() == "Göster"){
                binding.etNewPassowrd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvNewPasswordShow.text = "Gizle"
            }else{
                binding.etNewPassowrd.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.tvNewPasswordShow.text = "Göster"
            }
            binding.etNewPassowrd.setSelection(currentCursorPosition)
        }
        binding.tvNewPasswordAgainShow.setOnClickListener {
            val currentCursorPosition = binding.etNewPasswordAgain.selectionStart
            if (binding.tvNewPasswordAgainShow.text.toString() == "Göster"){
                binding.etNewPasswordAgain.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvNewPasswordAgainShow.text = "Gizle"
            }else{
                binding.etNewPasswordAgain.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.tvNewPasswordAgainShow.text = "Göster"
            }
            binding.etNewPasswordAgain.setSelection(currentCursorPosition)
        }

    }

    private fun passwordUpdateOperations() {
        authViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                Log.e("PasswordOperationsFragment", "onViewCreated: ${userData.password}")

                        val action = PasswordOperationsFragmentDirections.actionPasswordOperationsFragmentToUpdateSignInFragment(userData!!,userData.fullName!!,"","password",binding.etNewPasswordAgain.text.toString())
                        Navigation.findNavController(requireView()).navigate(action)

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}