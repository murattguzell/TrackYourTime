package com.muratguzel.trackyourtime.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.FragmentRegisterBinding
import com.muratguzel.trackyourtime.databinding.FragmentSettingsBinding
import com.muratguzel.trackyourtime.ui.MainActivity
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
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
        binding.toolBarAlternativeTitle.setOnClickListener {
            val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.registerButton.setOnClickListener {
            authViewModel.registerUser(binding.etNameSurName.text.toString(),binding.etEmail.text.toString(),binding.etPassword.text.toString())
            authViewModel.registerStatus.observe(viewLifecycleOwner){registerStatus->
                if (registerStatus){
                    Log.e("RegisterFragment", "$registerStatus")
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    startActivity(intent)
                    requireActivity().finish()
                }
            }
        }
    }
    fun observeLiveData(){

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}