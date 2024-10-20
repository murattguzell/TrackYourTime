package com.lideatech.imeiguard.ui.fragment

import CustomDialog
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.Users
import com.muratguzel.trackyourtime.databinding.FragmentUpdateSignInBinding
import com.muratguzel.trackyourtime.ui.viewModel.SettingsViewModel


class UpdateSignInFragment : Fragment() {


    private var _binding: FragmentUpdateSignInBinding? = null
    private val binding get() = _binding!!
    private lateinit var settingsViewModel: SettingsViewModel
    private var newEmail: String? = null
    private var fullName: String? = null
    private var user: Users? = null
    private var info :String?=null
    private var newPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val bundle: UpdateSignInFragmentArgs by navArgs()
        user = bundle.user
        newEmail = bundle.newEmail
        fullName = bundle.fullName
        info = bundle.info
        newPassword = bundle.newPassword
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentUpdateSignInBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        observeLiveData()
        binding.confirmButton.setOnClickListener {
         if(info == "password") {
             settingsViewModel.updatePassword(
                 newPassword!!,
                 user!!,
                 binding.etEmail.text.toString(),
                 binding.etPassword.text.toString()
             )

         }
          if (info =="email") {
             settingsViewModel.updateEmail(
                 user!!,
                 binding.etEmail.text.toString(),
                 newEmail!!,
                 binding.etPassword.text.toString())

        }

        }
        binding.tvPasswordShow.setOnClickListener {
            if (binding.tvPasswordShow.text.toString() == "Göster"){
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.tvPasswordShow.text = "Gizle"
            }else{
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

                binding.tvPasswordShow.text = "Göster"
            }
        }
    }

    private fun showDialog() {
        val customDialog = CustomDialog(requireContext(),
            "Son Bir adım!",
            "Güncellemek istediğiniz E-posta adresine gönderdiğimiz bağlantıya tıklayıp E-posta adresinizi doğrulayarak güncelleme işlemini tamaamlayabilirsiniz?",
            "Tamam",
            "Geri dön",
            positiveButtonAction = {
                findNavController().popBackStack(R.id.settingsFragment, false)
            }

        )
        customDialog.showDialog()

    }

    private fun observeLiveData() {
        settingsViewModel.updateStatus.observe(viewLifecycleOwner) { updateStatus ->
            if (updateStatus) {
                showDialog()
            }
        }
        settingsViewModel.updatePasswordStatus.observe(viewLifecycleOwner){updatePasswordStatus->
            if (updatePasswordStatus){
                Toast.makeText(requireContext(), "Şifre Güncellendi", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(requireContext(), "Şifre Güncellenmedi", Toast.LENGTH_SHORT).show()
            }
            findNavController().popBackStack(R.id.settingsFragment, false)
        }
    }
}






