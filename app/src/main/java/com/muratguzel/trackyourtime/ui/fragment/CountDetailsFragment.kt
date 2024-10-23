package com.muratguzel.trackyourtime.ui.fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.Util.AlarmReceiver
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.FragmentCountDetailsBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID


open class CountDetailsFragment : Fragment() {
    private var _binding: FragmentCountDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarDate: Calendar // Tarih için takvim
    private lateinit var calendarTime: Calendar
    private val mFirestore = Firebase.firestore
    private val mAuth = Firebase.auth
    var countDownData: CountDownTime? = null
    var info: String? = null
    private var countDownDocumentId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countDownData = arguments?.getSerializable("countDownData") as CountDownTime?
        info = arguments?.getString("info")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentCountDetailsBinding.inflate(inflater, container, false)
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(),R.color.statusBarColor)
        val view = binding.root

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backIconClick()
        if (info == "old") {
            backIconClick()
            if (countDownData?.title!!.isEmpty()){
                binding.toolBarTitle.text = requireContext().getString(R.string.countdown)
            }else {
                binding.toolBarTitle.text = countDownData?.title
            }
            dateAndTimeSet()
            binding.tvDate.text = String.format(
                "%02d/%02d/%04d",
                countDownData?.targetDay,
                countDownData?.targetMonth,
                countDownData?.targetYear
            )

            binding.tvTime.text = String.format(
                "%02d:%02d",
                countDownData?.targetHour,
                countDownData?.targetMinute
            )
            binding.etImportanceOfTheDay.setText(countDownData?.title)
            binding.etYourNotes.setText(countDownData?.notes)

            binding.btnRegister.visibility = View.GONE
            binding.btnUpdate.visibility = View.VISIBLE
            binding.btnDelete.visibility = View.VISIBLE


        } else {
            binding.toolBarTitle.text = requireContext().getString(R.string.create_countdown)
            binding.btnRegister.visibility = View.VISIBLE
            binding.btnUpdate.visibility = View.GONE
            binding.btnDelete.visibility = View.GONE


            dateAndTimeSet()

            binding.btnRegister.setOnClickListener {

                countDownDocumentId = UUID.randomUUID().toString()

                calendarTime.set(Calendar.SECOND, 0)
                val title = binding.etImportanceOfTheDay.text.toString()
                val notes = binding.etYourNotes.text.toString()

                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, calendarDate.get(Calendar.YEAR))
                    set(Calendar.MONTH, calendarDate.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))
                    set(Calendar.SECOND, calendarTime.get(Calendar.SECOND)) // Sıfırlanmış saniye
                }
                if (selectedCalendar.timeInMillis > System.currentTimeMillis()) {
                    // Kaydetme işlemi burada gerçekleştirilebilir
                    calendarTime.set(Calendar.SECOND, 0)
                    var targetCalendar = CountDownTime(
                        mAuth.currentUser!!.uid,
                        System.currentTimeMillis(),
                        calendarDate.get(Calendar.YEAR),
                        calendarDate.get(Calendar.MONTH) + 1,
                        calendarDate.get(Calendar.DATE),
                        calendarTime.get(Calendar.HOUR_OF_DAY),
                        calendarTime.get(Calendar.MINUTE),
                        calendarTime.get(Calendar.SECOND),
                        title,
                        notes,
                        documentId = countDownDocumentId
                    )

                    mFirestore.collection("count_down_time").document(countDownDocumentId!!).set(targetCalendar)
                        .addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                parentFragmentManager.popBackStack()


                                Toast.makeText(
                                    requireContext(),
                                    "kayıt başarılı",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val message =
                                    "Zaman doldu: ${targetCalendar.targetDay}/${targetCalendar.targetMonth}/${targetCalendar.targetYear}  ${targetCalendar.targetHour}:${targetCalendar.targetMinute}"
                                setAlarm(selectedCalendar.timeInMillis, message, title,countDownDocumentId!!)

                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "kayıt başarısız",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(requireContext(), "Geçmiş bir tarih seçiniz", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

        binding.btnUpdate.setOnClickListener {

            var title = binding.etImportanceOfTheDay.text.toString()
            var notes = binding.etYourNotes.text.toString()

                cancelAlarm(countDownData?.documentId!!)
                calendarTime.set(Calendar.SECOND, 0)


                // Takvim nesnesinde tarih ve saati birleştir
                val selectedCalendar = Calendar.getInstance().apply {
                    set(Calendar.YEAR, calendarDate.get(Calendar.YEAR))
                    set(Calendar.MONTH, calendarDate.get(Calendar.MONTH))
                    set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))
                    set(Calendar.SECOND, calendarTime.get(Calendar.SECOND)) // Sıfırlanmış saniye
                }
                if (selectedCalendar.timeInMillis > System.currentTimeMillis()) {
                    // Kaydetme işlemi burada gerçekleştirilebilir
                    calendarTime.set(Calendar.SECOND, 0)
                    var targetCalendar = CountDownTime(
                        mAuth.currentUser!!.uid,
                        System.currentTimeMillis(),
                        calendarDate.get(Calendar.YEAR),
                        calendarDate.get(Calendar.MONTH) + 1,
                        calendarDate.get(Calendar.DATE),
                        calendarTime.get(Calendar.HOUR_OF_DAY),
                        calendarTime.get(Calendar.MINUTE),
                        calendarTime.get(Calendar.SECOND),
                        title,
                        notes,
                        documentId = countDownData?.documentId!!
                    )

                    mFirestore.collection("count_down_time").document(countDownData?.documentId!!).update(
                        "creationTime",
                        System.currentTimeMillis(),
                        "notes",
                        notes,
                        "targetDay",
                        calendarDate.get(Calendar.DATE),
                        "targetHour",calendarTime.get(Calendar.HOUR_OF_DAY),
                        "targetMinute",calendarTime.get(Calendar.MINUTE),
                        "targetMonth",
                        calendarDate.get(Calendar.MONTH)+1,
                        "targetSecond",
                        calendarTime.get(Calendar.SECOND),
                        "targetYear",
                        calendarDate.get(Calendar.YEAR),
                        "title",
                        title
                    ).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            parentFragmentManager.popBackStack()
                            Toast.makeText(requireContext(), "Güncelleme başarılı", Toast.LENGTH_SHORT)
                                .show()
                            val message =
                                "Zaman doldu: ${targetCalendar.targetDay}/${targetCalendar.targetMonth}/${targetCalendar.targetYear}  ${targetCalendar.targetHour}:${targetCalendar.targetMinute}"
                            setAlarm(selectedCalendar.timeInMillis, message, title,countDownData?.documentId!!)
                        } else {
                            Toast.makeText(requireContext(), "kayıt başarısız", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }.addOnFailureListener { exception->
                        Log.e("Update","error",exception)
                    }
                } else {
                    Toast.makeText(requireContext(), "Geçmiş bir tarih seçiniz", Toast.LENGTH_SHORT)
                        .show()
                }


            }


        binding.btnDelete.setOnClickListener {

            mFirestore.collection("count_down_time").document(countDownData!!.documentId!!).delete().addOnCompleteListener { deleteTask->
                if (deleteTask.isSuccessful){
                    cancelAlarm(countDownData?.documentId!!)
                    parentFragmentManager.popBackStack()
                    Toast.makeText(requireContext(), "silme başarılı", Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(requireContext(), "silme başarısız", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun backIconClick() {
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()

        }
    }


    private fun setAlarm(
        millis: Long,
        message: String, title: String, documentId: String
    ) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val targetTimeInMillis = millis
        val currentTimeInMillis = System.currentTimeMillis()

        if (targetTimeInMillis <= currentTimeInMillis) {
            Toast.makeText(requireContext(), "Geçmiş bir zamana alarm kurulamaz.", Toast.LENGTH_LONG).show()
            return
        }


        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(requireContext(), "Lütfen uygulama için kesin alarmları etkinleştirin.", Toast.LENGTH_LONG).show()
                return
            }
        }

        val intent = Intent(requireContext(), AlarmReceiver::class.java).apply {
            putExtra("notification_message", message)
            putExtra("notification_title", title)
            putExtra("document_id", documentId)
        }

        val requestCode = documentId.hashCode() // Belge ID'sinden hashCode alıyoruz

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetTimeInMillis,
                pendingIntent
            )
            Toast.makeText(requireContext(), "Alarm başarıyla kuruldu.", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(requireContext(), "Alarm kurulamadı: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



    private fun cancelAlarm(documentId: String) {
        val alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(requireContext(), AlarmReceiver::class.java)


        val requestCode = documentId.hashCode() // Belge ID'sinden hashCode alıyoruz

        val pendingIntent = PendingIntent.getBroadcast(
            requireContext(),
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        alarmManager.cancel(pendingIntent)
    }

    private fun dateAndTimeSet(){
        calendarDate = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
        }
        calendarTime = Calendar.getInstance().apply {
            timeZone = TimeZone.getDefault()
        }
        updateDateLabel(calendarDate)
        updateTimeLabel(calendarTime)

        val datePicker = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendarDate.set(Calendar.YEAR, year)
            calendarDate.set(Calendar.MONTH, month)
            calendarDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateLabel(calendarDate)
        }

        val timePicker = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendarTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendarTime.set(Calendar.MINUTE, minute)
            updateTimeLabel(calendarTime)
        }

        binding.tvDate.setOnClickListener {
            val initialYear = countDownData?.targetYear ?: calendarDate.get(Calendar.YEAR)
            val initialMonth = countDownData?.targetMonth?.minus(1) ?: calendarDate.get(Calendar.MONTH)
            val initialDay = countDownData?.targetDay ?: calendarDate.get(Calendar.DAY_OF_MONTH)


            DatePickerDialog(
                requireContext(),
                R.style.CustomDatePickerDialog,
                datePicker,
                initialYear,
                initialMonth,
                initialDay
            ).show()
        }

        binding.tvTime.setOnClickListener {
            val initialHour = countDownData?.targetHour ?: calendarTime.get(Calendar.HOUR_OF_DAY)
            val initialMinute = countDownData?.targetMinute ?: calendarTime.get(Calendar.MINUTE)
            TimePickerDialog(
                requireContext(),
                R.style.CustomTimePickerDialog,
                timePicker,
                initialHour,
                initialMinute,
                true

            ).show()

        }

    }


    private fun updateTimeLabel(calendarTime: Calendar) {
        val format = SimpleDateFormat("HH:mm", Locale("tr"))
        val time = format.format(calendarTime.time)
        binding.tvTime.text = time
    }

    private fun updateDateLabel(calendarDate: Calendar) {
        val day = SimpleDateFormat("dd", Locale("tr")).format(calendarDate.time)
        val month = SimpleDateFormat("MM", Locale("tr")).format(calendarDate.time)
        val year = SimpleDateFormat("yyyy", Locale("tr")).format(calendarDate.time)

        binding.tvDate.text = "${day}.${month}.${year}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}