package com.muratguzel.trackyourtime.ui.adapter

import CountdownHelper
import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.CountdowntimerRowBinding
import java.util.Calendar

class CountDownTimeAdapter(
    val countDownTimeList: ArrayList<CountDownTime>,
    private val onItemClicked: (CountDownTime) -> Unit
) : RecyclerView.Adapter<CountDownTimeAdapter.CViewHolder>() {

    // Aktif sayaçları takip eden liste
    private val activeCountdownHelpers = mutableListOf<CountdownHelper>()

    class CViewHolder(val binding: CountdowntimerRowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CViewHolder {
        val binding = CountdowntimerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countDownTimeList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: CViewHolder, position: Int) {
        val countDownTime = countDownTimeList[position]

        // Hedef zamanı ekrana yazdır
        holder.binding.tvTargetTime.text = String.format(
            "%02d/%02d/%04d %02d:%02d",
            countDownTime.targetDay,
            countDownTime.targetMonth,
            countDownTime.targetYear,
            countDownTime.targetHour,
            countDownTime.targetMinute
        )

        // Başlık kısmını ayarla
        holder.binding.tvTitle.text = if (countDownTime.title.isNullOrEmpty()) {
            holder.itemView.context.getString(R.string.countdown)
        } else {
            countDownTime.title
        }

        Log.d("CountDownTime", "Binding position: $position, Time: $countDownTime")

        // Zamanlayıcıyı başlatmak için tarih ve zaman ayarları
        val calendarDate = Calendar.getInstance().apply {
            set(Calendar.YEAR, countDownTime.targetYear!!)
            set(Calendar.MONTH, countDownTime.targetMonth!!)
            set(Calendar.DAY_OF_MONTH, countDownTime.targetDay!!)
        }

        val calendarTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, countDownTime.targetHour!!)
            set(Calendar.MINUTE, countDownTime.targetMinute!!)
            set(Calendar.SECOND, countDownTime.targetSecond!!)
        }

        // CountdownHelper ile sayaç başlat
        val countdownHelper = CountdownHelper(holder.binding, countDownTime.creationTime!!, holder.itemView.context)
        countdownHelper.startCountdown(calendarDate, calendarTime)

        // Aktif sayaçları listeye ekle
        activeCountdownHelpers.add(countdownHelper)

        // Kart tıklama işlemi
        holder.binding.cardView.setOnClickListener {
            onItemClicked(countDownTime)
        }
    }

    // Tüm sayaçları durdurmak için metod
    fun stopAllTimers() {
        activeCountdownHelpers.forEach { it.cancelCountdown() }
        activeCountdownHelpers.clear()
    }
}
