package com.muratguzel.trackyourtime.ui.fragment

import CustomDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.muratguzel.trackyourtime.databinding.FragmentSettingsBinding
import com.muratguzel.trackyourtime.ui.AuthActivity
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
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
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        binding.tvSignOut.setOnClickListener {
            val customDialog = CustomDialog(requireContext(),
                "Dikkat!",
                "Oturumu kapatmak üzeresiniz emin misiniz?",
                "Oturumu sonlandır",
                "Geri dön",
                positiveButtonAction = {
                    authViewModel.signOut()
                    authViewModel.signOutState.observe(viewLifecycleOwner) { signOutState ->
                        if (signOutState) {
                            val intent = Intent(requireContext(), AuthActivity::class.java)
                            requireActivity().finish()
                            startActivity(intent)
                        }
                    }
                }

            )
            customDialog.showDialog()
        }
        binding.backIcon.setOnClickListener {
            requireActivity().finish()
        }
        binding.tvAccountDetails.setOnClickListener {
            val action = SettingsFragmentDirections.actionSettingsFragmentToAccountDetailsFragment()
            Navigation.findNavController(it).navigate(action)
        }
        binding.tvPasswordOperations.setOnClickListener {
            val action =
                SettingsFragmentDirections.actionSettingsFragmentToPasswordOperationsFragment()
            Navigation.findNavController(it).navigate(action)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
               requireActivity().finish()
            }
        })
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}