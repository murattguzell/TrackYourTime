package com.muratguzel.trackyourtime.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.FragmentCountDetailsBinding
import com.muratguzel.trackyourtime.databinding.FragmentCountDownBinding
import com.muratguzel.trackyourtime.ui.adapter.CountDownTimeAdapter


open class CountDownFragment : Fragment() {
    private var _binding: FragmentCountDownBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private val mFireStore = Firebase.firestore
    private val mAuth = Firebase.auth
    var adapter: CountDownTimeAdapter? = null
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
        // RecyclerView için adapter'ı ve LayoutManager'ı ayarlayın
        // Adapter'ı oluşturun ve tıklama olayını ayarlayın
        adapter = CountDownTimeAdapter(arrayListOf()) { countDownTime ->
            // Tıklama olduğunda geçiş yap
            val fragment = CountDetailsFragment()
            val bundle = Bundle().apply {
                putSerializable("countDownData", countDownTime) // Tüm veriyi gönder
                var old = "old"
                putString("info",old)
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

        // Veriyi çek
        fetchDataForUser()

        return view    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            val transaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.homeContainer, CountDetailsFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }

    }
    private fun fetchDataForUser() {
        val userId = mAuth.currentUser?.uid // Mevcut kullanıcının ID'sini alın
        if (userId != null) {
            mFireStore.collection("count_down_time")
                .whereEqualTo("userId", userId) // Kullanıcının uid'sine göre sorgula
                .get()
                .addOnSuccessListener { documents ->
                    val countDownList = mutableListOf<CountDownTime>()
                    for (document in documents) {
                        val countDownTime = document.toObject(CountDownTime::class.java)
                        countDownList.add(countDownTime) // countDownList'e veri ekle
                    }
                    // Veriyi RecyclerView Adapter'a aktar
                    adapter!!.countDownTimeList.clear() // Önceki verileri temizle
                    adapter!!.countDownTimeList.addAll(countDownList)
                    adapter!!.notifyDataSetChanged() // Adapter'ı güncelle

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