package com.muratguzel.trackyourtime.ui.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.FragmentSplashBinding
import com.muratguzel.trackyourtime.ui.MainActivity
import com.muratguzel.trackyourtime.ui.viewModel.AuthViewModel


class SplashFragment : Fragment() {
    private var _binding: FragmentSplashBinding? = null

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
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        authViewModel.currentUserNavigateState.observe(viewLifecycleOwner){ currentUserRedirectionState->
            if (currentUserRedirectionState){
                val intent = Intent(requireContext(), MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
            else{
                val action =SplashFragmentDirections.actionSplashFragmentToRegisterFragment()
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(
                        R.id.splashFragment,
                        true
                    )
                Navigation.findNavController(binding.root).navigate(action,navOptions.build())
            }

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}