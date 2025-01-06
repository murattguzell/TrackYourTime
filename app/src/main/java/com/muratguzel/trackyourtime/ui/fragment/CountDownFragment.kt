package com.muratguzel.trackyourtime.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.FragmentCountDownBinding
import com.muratguzel.trackyourtime.ui.adapter.CountDownTimeAdapter
import com.muratguzel.trackyourtime.ui.viewModel.CountDownViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar

@AndroidEntryPoint
open class CountDownFragment : Fragment() {
    private var _binding: FragmentCountDownBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    var adapter: CountDownTimeAdapter? = null
    lateinit var countDownViewModel: CountDownViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = resources.getColor(R.color.statusBarColor)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCountDownBinding.inflate(inflater, container, false)
        val view = binding.root
        countDownViewModel = ViewModelProvider(this)[CountDownViewModel::class.java]
        countDownViewModel.getUserCountDown()

        adapter = CountDownTimeAdapter(arrayListOf()) { countDownTime ->
            // Tıklama olduğunda geçiş yap
            val fragment = CountDetailsFragment()
            val bundle = Bundle().apply {
                putSerializable("countDownData", countDownTime) // Tüm veriyi gönder
                var old = "old"
                putString("info", old)
            }
            fragment.arguments = bundle

            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.homeContainer, fragment) // Hedef container
            transaction.addToBackStack(null)
            transaction.commit()
        }

        // RecyclerView ayarları
        binding.rvCountDown.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCountDown.adapter = adapter
        observeLiveData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.homeContainer, CountDetailsFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }

    private fun observeLiveData() {
        adapter?.stopAllTimers()

        var newCountDownList = arrayListOf<CountDownTime>()
        countDownViewModel.countDownList.observe(viewLifecycleOwner) { countDownList ->
            if (countDownList != null) {
                newCountDownList.clear()
                for (countDownTime in countDownList) {
                    val targetTimeInMillis = Calendar.getInstance().apply {
                        set(Calendar.YEAR, countDownTime.targetYear!!)
                        set(Calendar.MONTH, countDownTime.targetMonth!! - 1) // 0-based index
                        set(Calendar.DAY_OF_MONTH, countDownTime.targetDay!!)
                        set(Calendar.HOUR_OF_DAY, countDownTime.targetHour!!)
                        set(Calendar.MINUTE, countDownTime.targetMinute!!)
                        set(Calendar.SECOND, 0)
                        set(Calendar.MILLISECOND, 0)
                    }.timeInMillis
                    val currentTime = System.currentTimeMillis()
                    // Eğer hedef tarih geçmişteyse, sayaç aktif değil
                    if (targetTimeInMillis >= currentTime) {
                        newCountDownList.add(countDownTime)
                    }
                }
                // Veriyi RecyclerView Adapter'a aktar

                adapter?.countDownTimeList?.clear()
                adapter?.stopAllTimers()
                adapter?.countDownTimeList?.addAll(newCountDownList)
                adapter?.notifyDataSetChanged() // Adapter'ı güncelle
            }
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}