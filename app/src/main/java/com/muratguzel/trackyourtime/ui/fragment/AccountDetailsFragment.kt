package com.muratguzel.trackyourtime.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.Users
import com.muratguzel.trackyourtime.databinding.FragmentAccountDetailsBinding
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel
import com.muratguzel.trackyourtime.ui.viewModel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccountDetailsFragment : Fragment() {
    private var _binding: FragmentAccountDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private lateinit var settingsViewModel: SettingsViewModel

    private var getUser: Users? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountDetailsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        observeLiveData()
        binding.registerButton.setOnClickListener {
            updateData(it)
        }
        binding.tvBack.setOnClickListener {
            findNavController().popBackStack(R.id.settingsFragment, false)
        }
    }


    private fun updateData(it: View) {
        if (binding.etEPoasta.text.toString() != getUser?.email && binding.etNameSurName.text.toString() != getUser?.fullName) {
            settingsViewModel.updateFullName(getUser!!, binding.etNameSurName.text.toString())

            val action =
                AccountDetailsFragmentDirections.actionAccountDetailsFragmentToUpdateSignInFragment(
                    getUser!!,
                    binding.etNameSurName.text.toString(),
                    binding.etEPoasta.text.toString(),
                    "email",
                    ""
                )
            Navigation.findNavController(it).navigate(action)
        } else if (binding.etNameSurName.text.toString() != getUser?.fullName) {
            settingsViewModel.updateFullName(getUser!!, binding.etNameSurName.text.toString())
            findNavController().popBackStack(R.id.settingsFragment, false)
            Toast.makeText(requireContext(), "Başarıyla Güncellendi", Toast.LENGTH_SHORT).show()
        } else if (binding.etEPoasta.text.toString() != getUser?.email) {
            val action =
                AccountDetailsFragmentDirections.actionAccountDetailsFragmentToUpdateSignInFragment(
                    getUser!!,
                    binding.etNameSurName.text.toString(),
                    binding.etEPoasta.text.toString(),
                    "email",
                    ""
                )
            Navigation.findNavController(it).navigate(action)
        }
    }

    private fun observeLiveData() {
        authViewModel.getUserData()
        authViewModel.userData.observe(viewLifecycleOwner) { getUserData ->
            getUser = getUserData
            binding.etNameSurName.setText(getUserData.fullName)
            binding.etEPoasta.setText(getUserData.email)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}