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
import androidx.lifecycle.ViewModelProvider
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.FragmentCountDetailsBinding
import com.muratguzel.trackyourtime.ui.viewModel.CountDownViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
open class CountDetailsFragment : Fragment() {
    private var _binding: FragmentCountDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var calendarDate: Calendar // Tarih için takvim
    private lateinit var calendarTime: Calendar
    var countDownData: CountDownTime? = null
    var info: String? = null
    lateinit var countDownViewModel: CountDownViewModel
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
        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.statusBarColor)
        val view = binding.root
        countDownViewModel = ViewModelProvider(this)[CountDownViewModel::class.java]

        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backIconClick()
        if (info == "old") {
            backIconClick()
            if (countDownData?.title!!.isEmpty()) {
                binding.toolBarTitle.text = requireContext().getString(R.string.countdown)
            } else {
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


                val title = binding.etImportanceOfTheDay.text.toString()
                val notes = binding.etYourNotes.text.toString()
                countDownViewModel.registerCountDown(title, notes, calendarDate, calendarTime)
                countDownViewModel.registerCountDownStatus.observe(viewLifecycleOwner) { registerStatus ->
                    if (registerStatus) {
                        parentFragmentManager.popBackStack()

                        Toast.makeText(
                            requireContext(),
                            "Kayıt başarılı",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Kayıt başarısız",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

        }

        binding.btnUpdate.setOnClickListener {

            var title = binding.etImportanceOfTheDay.text.toString()
            var notes = binding.etYourNotes.text.toString()
            countDownViewModel.updateCountDown(
                title,
                notes,
                countDownData!!.documentId!!,
                calendarDate,
                calendarTime
            )

            countDownViewModel.updateCountDownStatus.observe(viewLifecycleOwner) { updateStatus ->
                if (updateStatus) {
                    Toast.makeText(requireContext(), "güncelleme başarılı", Toast.LENGTH_SHORT)
                        .show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "güncelleme başarısız", Toast.LENGTH_SHORT)
                        .show()
                }

            }


        }


        binding.btnDelete.setOnClickListener {

            countDownViewModel.deleteCountDownTimer(countDownData?.documentId!!)
            countDownViewModel.deleteCountDownStatus.observe(viewLifecycleOwner) { deleteStatus ->

                if (deleteStatus) {
                    Toast.makeText(requireContext(), "Silme başarılı", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }else {
                    Toast.makeText(requireContext(), "Silme başarısız", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun backIconClick() {
        binding.backIcon.setOnClickListener {
            parentFragmentManager.popBackStack()

        }
    }


    private fun dateAndTimeSet() {

        if (info == "old") {
            calendarDate = Calendar.getInstance().apply {
                set(Calendar.YEAR, countDownData?.targetYear!!)
                set(Calendar.MONTH, countDownData?.targetMonth?.minus(1)!!)
                set(Calendar.DAY_OF_MONTH, countDownData?.targetDay!!)
                calendarTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, countDownData?.targetHour!!)
                    set(Calendar.MINUTE, countDownData?.targetMinute!!)
                }
            }
        } else {
            calendarDate = Calendar.getInstance().apply {
                timeZone = TimeZone.getDefault()

            }
            calendarTime = Calendar.getInstance().apply {
                timeZone = TimeZone.getDefault()

            }
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
            val initialMonth =
                countDownData?.targetMonth?.minus(1) ?: calendarDate.get(Calendar.MONTH)
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