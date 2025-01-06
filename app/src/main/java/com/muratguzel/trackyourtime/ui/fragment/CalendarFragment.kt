package com.muratguzel.trackyourtime.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.FragmentCalendarBinding
import com.muratguzel.trackyourtime.ui.adapter.CountDownTimeAdapter
import com.muratguzel.trackyourtime.ui.viewModel.CountDownViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlin.jvm.java

@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    var adapter: CountDownTimeAdapter? = null
    lateinit var countDownViewModel: CountDownViewModel
    private var allCountDownTimes: ArrayList<CountDownTime> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.calendarStatusBarColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root
        countDownViewModel = ViewModelProvider(this)[CountDownViewModel::class.java]
        countDownViewModel.getUserCountDown()
        setAndEventRecyclerView()
        // Veriyi çek
        fetchDataForUser()
        setupCalendarView()
        return view
    }

    private fun setAndEventRecyclerView() {
        adapter = CountDownTimeAdapter(arrayListOf()) { countDownTime ->

            val fragment = CountDetailsFragment()
            val bundle = Bundle().apply {
                putSerializable("countDownData", countDownTime) // Tüm veriyi gönder
                putSerializable("info", "old")
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
    }

    private fun setupCalendarView() {
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            // Seçilen tarihe göre filtre uygula
            val filteredList = allCountDownTimes.filter { countDownTime ->
                countDownTime.targetYear == year &&
                        countDownTime.targetMonth == month + 1 &&
                        countDownTime.targetDay == dayOfMonth
            }

            if (filteredList.isEmpty()) {
                // Tüm sayaçları durdur ve listeyi temizle
                adapter?.stopAllTimers()
                adapter?.countDownTimeList?.clear()
                fetchDataForUser()
                adapter?.notifyDataSetChanged() // Adapter'ı güncelle
            } else {
                // Tüm sayaçları durdur ve listeyi temizle
                adapter?.stopAllTimers()
                adapter?.countDownTimeList?.clear()

                // Filtrelenmiş veriyi adapter'a aktar
                adapter?.countDownTimeList?.addAll(filteredList)
                Log.d("FilteredCountDowns", filteredList.toString())

                adapter?.notifyDataSetChanged() // Adapter'ı güncelle
            }
        }
    }

    private fun fetchDataForUser() {
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
                        allCountDownTimes = newCountDownList
                        adapter?.countDownTimeList?.clear()
                        adapter?.stopAllTimers()
                        adapter?.countDownTimeList?.addAll(countDownList)
                        adapter?.notifyDataSetChanged()
                    }
                }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
