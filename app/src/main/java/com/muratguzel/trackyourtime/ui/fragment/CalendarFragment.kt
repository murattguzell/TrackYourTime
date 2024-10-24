package com.muratguzel.trackyourtime.ui.fragment

import CountdownHelper
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.color
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.FragmentCalendarBinding
import com.muratguzel.trackyourtime.ui.adapter.CountDownTimeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlin.jvm.java
@AndroidEntryPoint
class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!
    private val mFireStore = Firebase.firestore
    private val mAuth = Firebase.auth
    var adapter: CountDownTimeAdapter? = null
    private var allCountDownTimes: ArrayList<CountDownTime> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.calendarStatusBarColor)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        val view = binding.root

        setAndEventRecyclerView()


        // Veriyi çek
        fetchDataForUser()
        setupCalendarView()
        return view
    }

    private fun setAndEventRecyclerView(){
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
            if (filteredList.isEmpty()){
                adapter?.countDownTimeList?.clear() //
                fetchDataForUser()
                adapter?.notifyDataSetChanged() // Adapter'ı güncelle

            }else{
                // Filtrelenmiş veriyi adapter'a aktar
                adapter?.countDownTimeList?.clear()

                adapter?.countDownTimeList?.addAll(filteredList)
                Log.d("FilteredCountDowns", filteredList.toString())

                adapter?.notifyDataSetChanged() // Adapter'ı güncelle
            }
        }
    }

    private fun fetchDataForUser() {
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            mFireStore.collection("count_down_time")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { documents ->
                    val countDownList = ArrayList<CountDownTime>()
                    for (document in documents) {
                        val countDownTime = document.toObject(CountDownTime::class.java)
                        countDownList.add(countDownTime)
                    }
                    allCountDownTimes = countDownList
                    adapter?.countDownTimeList?.clear()
                    adapter?.countDownTimeList?.addAll(countDownList)
                    adapter?.notifyDataSetChanged()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(requireContext(), "Veri çekme hatası: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Kullanıcı kimliği bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
