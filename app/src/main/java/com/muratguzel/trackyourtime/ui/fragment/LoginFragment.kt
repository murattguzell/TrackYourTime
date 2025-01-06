package com.muratguzel.trackyourtime.ui.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.FragmentLoginBinding
import com.muratguzel.trackyourtime.ui.MainActivity
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    var sharedPreferences :SharedPreferences? = null
    var checbox = sharedPreferences?.getString("remember","")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        sharedPreferences = requireActivity().getSharedPreferences("com.muratguzel.trackyourtime", MODE_PRIVATE)
        checbox = sharedPreferences?.getString("remember","")
        binding.toolBarAlternativeTitle.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }
        checbox = sharedPreferences?.getString("remember","")
        if (checbox == "true"){
            binding.checkBoxRememberMe.isChecked = true
         binding.emailEditText.setText(sharedPreferences?.getString("email",""))
        binding.passwordEditText.setText(sharedPreferences?.getString("password",""))
        }else {
            binding.emailEditText.setText("")
            binding.passwordEditText.setText("")
        }
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        observeLiveData()
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            authViewModel.loginUser(email, password)
        }

        binding.checkBoxRememberMe.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                sharedPreferences?.edit()!!.putString("remember","true").apply()
            } else if (!isChecked) {

                    sharedPreferences?.edit()!!.putString("remember", "false").apply()
                }
        }

        binding.toolBarAlternativeTitle.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.tvPasswordShow.setOnClickListener {
            if (binding.tvPasswordShow.text.toString() == "Göster"){
                binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvPasswordShow.text = "Gizle"
            }else{
                binding.passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.tvPasswordShow.text = "Göster"
            }
        }
        binding.tvForgotPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun observeLiveData() {
        authViewModel.loginStatus.observe(viewLifecycleOwner) { loginStatus ->
            if (loginStatus) {
                sharedPreferences?.edit()!!.putString("email",binding.emailEditText.text.toString()).apply()
                sharedPreferences?.edit()!!.putString("password",binding.passwordEditText.text.toString()).apply()
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