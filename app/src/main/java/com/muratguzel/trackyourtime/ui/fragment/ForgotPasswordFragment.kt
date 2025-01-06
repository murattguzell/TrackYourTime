package com.muratguzel.trackyourtime.ui.fragment

import CustomDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.FragmentForgotPasswordBinding
import com.muratguzel.trackyourtime.databinding.FragmentLoginBinding
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private var _binding: FragmentForgotPasswordBinding? = null
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
        _binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.forgotPasswordButton.setOnClickListener {
            val email = binding.etEmail.text.toString()
            authViewModel.forgotPassword(email)
            observeLiveData()
        }
    }

    fun observeLiveData() {
        authViewModel.forgotPasswordStatus.observe(viewLifecycleOwner) { forgotPasswordStatus ->
            if (forgotPasswordStatus) {
                binding.etEmail.text.clear()
                var dialog = CustomDialog(
                    requireContext(),
                    "Şifre sıfırlama",
                    "Şifre sıfırlama maili gönderildi",
                    "Tamam",
                    negativeButtonText = "",
                    positiveButtonAction = {
                        findNavController().popBackStack(R.id.loginFragment, false)
                    }

                )
                dialog.showDialog()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Şifre sıfırlama başarısız",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    }

}

