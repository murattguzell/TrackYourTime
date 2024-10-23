package com.muratguzel.trackyourtime.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.FragmentRegisterBinding
import com.muratguzel.trackyourtime.databinding.FragmentSettingsBinding
import com.muratguzel.trackyourtime.ui.MainActivity
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        observeLiveData()
        binding.toolBarAlternativeTitle.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvTogglePasswordVisibility.setOnClickListener {
            val currentCursorPosition = binding.etPassword.selectionStart
            if (binding.tvTogglePasswordVisibility.text.toString() == "Göster"){
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvTogglePasswordVisibility.text = "Gizle"
            }else{
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.tvTogglePasswordVisibility.text = "Göster"
            }
            binding.etPassword.setSelection(currentCursorPosition)

        }
        binding.registerButton.setOnClickListener {
            authViewModel.registerUser(
                binding.etNameSurName.text.toString(),
                binding.etEmail.text.toString(),
                binding.etPassword.text.toString()
            )
        }
    }

    private fun observeLiveData() {
        authViewModel.registerStatus.observe(viewLifecycleOwner) { registerStatus ->
            if (registerStatus) {
                Log.e("RegisterFragment", "$registerStatus")
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}