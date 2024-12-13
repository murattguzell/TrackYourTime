package com.muratguzel.trackyourtime.ui.adapter

import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.databinding.ProfileRecyclerRowBinding
import java.util.Calendar

class LinearProgressAdapter(
    val countDownList: ArrayList<CountDownTime>,
    private val onItemClicked: (CountDownTime) -> Unit,
) :
    RecyclerView.Adapter<LinearProgressAdapter.LinearProgressViewHolder>() {
    class LinearProgressViewHolder(val binding: ProfileRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LinearProgressViewHolder {
        val binding =
            ProfileRecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LinearProgressViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return countDownList.size
    }

    override fun onBindViewHolder(holder: LinearProgressViewHolder, position: Int) {
        val countDownTime = countDownList[position]


        if (countDownTime.title == ""){
            holder.binding.tvCountDownTitle.text = holder.itemView.context.getString(R.string.countdown)
        }else {
            holder.binding.tvCountDownTitle.text = countDownTime.title
        }
        holder.binding.tvTargetTime.text = String.format(
            "%02d/%02d/%04d %02d:%02d",
            countDownTime.targetDay,
            countDownTime.targetMonth,
            countDownTime.targetYear,
            countDownTime.targetHour,
            countDownTime.targetMinute
        )

// Hedef zamanı alalım (targetTime)
        val targetDateTime = Calendar.getInstance().apply {
            set(Calendar.YEAR, countDownTime.targetYear!!)
            set(
                Calendar.MONTH,
                countDownTime.targetMonth!! - 1
            ) // Ayları sıfırdan başlattığı için 1 çıkarıyoruz
            set(Calendar.DAY_OF_MONTH, countDownTime.targetDay!!)
            set(Calendar.HOUR_OF_DAY, countDownTime.targetHour!!)
            set(Calendar.MINUTE, countDownTime.targetMinute!!)
            set(Calendar.SECOND, countDownTime.targetSecond!!)
        }
        val targetTime = targetDateTime.timeInMillis

        val creationTime = countDownTime.creationTime ?: System.currentTimeMillis()

        val currentTime = System.currentTimeMillis()

        val totalDuration = targetTime - creationTime

        val countDownTimer = object : CountDownTimer(targetTime - currentTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val elapsedTime = System.currentTimeMillis() - creationTime
                val percentage = if (totalDuration > 0) {
                    val calculatedPercentage =
                        (elapsedTime.toDouble() / totalDuration.toDouble() * 100).toInt()
                    if (calculatedPercentage > 100) 100 else calculatedPercentage
                } else {
                    100
                }

                holder.binding.linearProgressBar.progress = percentage
                holder.binding.tvProgressText.text =" %$percentage ${holder.itemView.context.getString(R.string.complete)}"

                Log.d(
                    "Progress",
                    "Toplam Süre: $totalDuration, Geçen Süre: $elapsedTime, Yüzdelik: $percentage"
                )
            }

            override fun onFinish() {
                // Zaman tamamlandığında %100 göster
                holder.binding.linearProgressBar.progress = 100
                holder.binding.tvProgressText.text = holder.itemView.context.getString(R.string.one_hundred_percent_completed)
            }
        }

        countDownTimer.start()
        holder.binding.cardView.setOnClickListener {
            onItemClicked(countDownTime)
        }
    }
}