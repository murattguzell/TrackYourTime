package com.muratguzel.trackyourtime.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.FragmentLoginBinding
import com.muratguzel.trackyourtime.databinding.FragmentSplashBinding
import com.muratguzel.trackyourtime.ui.MainActivity


class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private val mAuth = Firebase.auth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else {
            val action = SplashFragmentDirections.actionSplashFragmentToRegisterFragment()
            val navOptions = NavOptions.Builder()
                .setPopUpTo(
                    R.id.splashFragment,
                    true
                )  // Geçiş yapılan fragment'ı backstack'ten sil
                .build()
            Navigation.findNavController(requireView()).navigate(action, navOptions)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}