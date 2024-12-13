import android.content.Context
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import androidx.viewbinding.ViewBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.muratguzel.trackyourtime.R
import com.muratguzel.trackyourtime.databinding.CountdowntimerRowBinding
import com.muratguzel.trackyourtime.ui.fragment.CountDetailsFragment
import java.util.Calendar

class CountdownHelper(
    private val binding: ViewBinding,
    private var getCreationTime: Long,
    private val context: Context // Context ekleyin
) : CountDetailsFragment() {
    private lateinit var countdownTimer: CountDownTimer
    private var countdownMillis: Long = 0
    private lateinit var targetCalendar: Calendar
    private var creationTime: Long = 0

    fun startCountdown(calendarDate: Calendar, calendarTime: Calendar) {
        targetCalendar = Calendar.getInstance().apply {
            set(Calendar.YEAR, calendarDate.get(Calendar.YEAR))
            set(Calendar.MONTH, calendarDate.get(Calendar.MONTH) - 1)
            set(Calendar.DAY_OF_MONTH, calendarDate.get(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
            set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))
            set(Calendar.SECOND, 0)
        }

        countdownMillis = targetCalendar.timeInMillis - System.currentTimeMillis()

        if (countdownMillis > 0) {
            creationTime = getCreationTime
            startCountdownTimer(countdownMillis) // countdown timer başlat
        } else {
            // Geçmiş bir tarih ve saat seçildiğinde hata mesajı göster
            showError(context.getString(R.string.completed_countdown)) // Hata mesajı kaynak kullanılarak gösterildi
        }
    }

    private fun startCountdownTimer(millis: Long) {
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }

        countdownTimer = object : CountDownTimer(millis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countDownText(millisUntilFinished)
                progress((millisUntilFinished / 1000).toInt())
            }

            override fun onFinish() {
                showError(context.getString(R.string.time_is_up)) // Zaman doldu mesajı kaynak kullanılarak gösterildi
            }
        }.start()
    }

    private fun countDownText(millisUntilFinished: Long) {
        val secondsLeft = (millisUntilFinished / 1000).toInt()
        val days = secondsLeft / (24 * 3600)
        val hours = (secondsLeft % (24 * 3600)) / 3600
        val minutes = (secondsLeft % 3600) / 60
        val seconds = secondsLeft % 60

        val countdownText = formatCountdown(days, hours, minutes, seconds)

        when (binding) {
            is CountdowntimerRowBinding -> binding.tvCountDown.text = countdownText
        }
    }

    fun cancelCountdown() {
        if (::countdownTimer.isInitialized) {
            countdownTimer.cancel()
        }
    }

    private fun progress(secondsLeft: Int) {
        val totalTime = ((targetCalendar.timeInMillis - creationTime) / 1000).toFloat()
        val progress = (secondsLeft / totalTime) * 100

        // Binding türüne göre ilerleme çubuğunu güncelleme
        when (binding) {
            is CountdowntimerRowBinding -> binding.circularProgressBar.progress = progress.toInt()
        }
    }

    private fun formatCountdown(days: Int, hours: Int, minutes: Int, seconds: Int): SpannableString {
        val years = days / 365
        val remainingDaysAfterYears = days % 365
        val months = remainingDaysAfterYears / 30
        val remainingDaysAfterMonths = remainingDaysAfterYears % 30

        val countdownText = when {
            years >= 1 -> "${years}y ${months}a ${remainingDaysAfterMonths}g \n ${String.format("%02d", hours)}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
            days >= 30 -> "${months}a ${remainingDaysAfterMonths}g \n ${String.format("%02d", hours)}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
            days >= 1 -> "${days}g \n ${String.format("%02d", hours)}:${String.format("%02d", minutes)}:${String.format("%02d", seconds)}"
            else -> String.format("%02d:%02d:%02d", hours, minutes, seconds)
        }

        val spannableString = SpannableString(countdownText)

        // Font boyutunu ayarla
        val firstLineEnd = countdownText.indexOf('\n')
        if (firstLineEnd != -1) {
            spannableString.setSpan(RelativeSizeSpan(2.0f), 0, firstLineEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            val secondLineStart = firstLineEnd + 1 // İkinci satırın başlangıcı
            spannableString.setSpan(RelativeSizeSpan(1.0f), secondLineStart, countdownText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        return spannableString
    }

    private fun showError(message: String) {
        val spannableString = SpannableString(message)
        spannableString.setSpan(RelativeSizeSpan(0.4375f), 0, message.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Binding türüne göre hata mesajını gösterme
        when (binding) {
            is CountdowntimerRowBinding -> binding.tvCountDown.text = spannableString
        }

        // Binding türüne göre ilerleme çubuğunu sıfırlama
        when (binding) {
            is CountdowntimerRowBinding -> binding.circularProgressBar.progress = 0
        }
    }
}
