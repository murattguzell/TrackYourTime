package com.muratguzel.trackyourtime.ui.fragment

import CustomDialog
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.Util.imageDownload
import com.muratguzel.trackyourtime.Util.placeHolderCreate
import com.muratguzel.trackyourtime.databinding.FragmentProfileBinding
import com.muratguzel.trackyourtime.ui.AuthActivity
import com.muratguzel.trackyourtime.ui.SettingsActivity
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel
import com.muratguzel.trackyourtime.ui.viewModel.SettingsViewModel

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private var photoUri : Uri? = null
    private var fullName:String? =null
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = resources.getColor(R.color.blue)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]

        registerLauncher()
        observeLiveData()
        authViewModel.getUserData()
        binding.tvSettings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            requireActivity().finish()
        }
        binding.tvSignOut.setOnClickListener {
            signOutDialog()
        }
        binding.circleImage.setOnClickListener {
            selectImage()
        }
    }

    private fun signOutDialog(){
        val customDialog = CustomDialog(requireContext(),
            "Dikkat!",
            "Oturumu kapatmak üzeresiniz emin misiniz?",
            "Oturumu sonlandır",
            "Geri dön",
            positiveButtonAction = {
                authViewModel.signOut()

            }

        )
        customDialog.showDialog()
    }
    private fun selectImage() {
        //Android 33++
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    Snackbar.make(
                        requireView(),
                        "permission needed for gallery",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("Give Permission") {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)

                        }.show()
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                //go to gallery
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        } else {
            //Android 32 --
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    Snackbar.make(
                        requireView(),
                        "permission needed for gallery",
                        Snackbar.LENGTH_INDEFINITE
                    )
                        .setAction("Give Permission") {
                            //request permission
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

                        }.show()
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            } else {
                //go to gallery
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }

    private fun registerLauncher() {
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    // open gallery
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(requireContext(), "İzin verilmedi", Toast.LENGTH_SHORT).show()
                }
            }

        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == AppCompatActivity.RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        photoUri = intentFromResult.data
                        binding.circleImage.setImageURI(photoUri)
                        authViewModel.uploadProfileImage(authViewModel.userData.value!!,photoUri!!,requireContext())
                    }

                }
            }

    }

    private fun observeLiveData() {
        authViewModel.signOutState.observe(viewLifecycleOwner) { signOutState ->
            if (signOutState) {
                val intent = Intent(requireContext(), AuthActivity::class.java)
                requireActivity().finish()
                startActivity(intent)
            }
        }

        authViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {

                binding.tvUserName.text = userData.fullName
                val imageUri = userData.profileImage
                Log.d("ProfileFragment", "Image URI: ${userData.profileImage}")

                binding.circleImage.imageDownload(imageUri,placeHolderCreate(requireContext()))
            }
        }
        settingsViewModel.updateFullNameStatus.observe(viewLifecycleOwner){userName->
            if (userName){
                authViewModel.getUserData()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}