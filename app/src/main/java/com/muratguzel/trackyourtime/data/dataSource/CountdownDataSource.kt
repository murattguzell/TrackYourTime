package com.muratguzel.trackyourtime.data.dataSource

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.muratguzel.trackyourtime.data.entitiy.CountDownTime
import com.muratguzel.trackyourtime.util.AlarmReceiver
import kotlinx.coroutines.suspendCancellableCoroutine
import android.icu.util.Calendar
import android.os.Build
import java.util.UUID
import javax.inject.Inject
import kotlin.coroutines.resume

class CountdownDataSource @Inject constructor(
    val mAuth: FirebaseAuth,
    val mFireStore: FirebaseFirestore,
) {


    suspend fun getUserCountDown(): List<CountDownTime> =
        suspendCancellableCoroutine { continuation ->
            val currentUserId = mAuth.currentUser?.uid

            if (currentUserId != null) {
                mFireStore.collection("count_down_time")
                    .whereEqualTo("userId", currentUserId)
                    .get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val countDownList = arrayListOf<CountDownTime>()
                            val documents = task.result.documents
                            for (document in documents) {
                                val countDownTime = document.toObject(CountDownTime::class.java)
                                if (countDownTime != null) {
                                    countDownList.add(countDownTime)
                                }

                            }
                            continuation.resume(countDownList)

                        } else {
                            continuation.resume(emptyList())
                        }
                    }

            }
        }

    private fun setAlarm(
        context: Context,
        millis: String,
        message: String, title: String, documentId: String,
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val targetTimeInMillis = millis
        val currentTimeInMillis = System.currentTimeMillis()

        if (targetTimeInMillis <= currentTimeInMillis.toString()) {
            Toast.makeText(
                context,
                "Geçmiş bir zamana alarm kurulamaz.",
                Toast.LENGTH_LONG
            ).show()
            return
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Toast.makeText(
                    context,
                    "Lütfen uygulama için kesin alarmları etkinleştirin.",
                    Toast.LENGTH_LONG
                ).show()
                return
            }
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notification_message", message)
            putExtra("notification_title", title)
            putExtra("document_id", documentId)
        }

        val requestCode = documentId.hashCode() // Belge ID'sinden hashCode alıyoruz

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetTimeInMillis.toLong(),
                pendingIntent
            )
            Toast.makeText(context, "Alarm başarıyla kuruldu.", Toast.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            Toast.makeText(context, "Alarm kurulamadı: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun cancelAlarm(documentId: String, context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, AlarmReceiver::class.java)


        val requestCode = documentId.hashCode() // Belge ID'sinden hashCode alıyoruz

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        alarmManager.cancel(pendingIntent)
    }

    suspend fun registerCountDown(
        title: String,
        notes: String,
        calendarDate: Calendar,
        calendarTime: Calendar,
        context: Context,
    ): Boolean =
        suspendCancellableCoroutine { continuation ->
            var countDownDocumentId = UUID.randomUUID().toString()

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
                    documentId = countDownDocumentId
                )



                mFireStore.collection("count_down_time").document(countDownDocumentId)
                    .set(targetCalendar)
                    .addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            continuation.resume(true)
                            val message =
                                "Zaman doldu: ${targetCalendar.targetDay}/${targetCalendar.targetMonth}/${targetCalendar.targetYear}  ${targetCalendar.targetHour}:${targetCalendar.targetMinute}"
                            setAlarm(
                                millis = selectedCalendar.timeInMillis.toString(),
                                message = message,
                                title = title,
                                documentId = countDownDocumentId,
                                context = context,
                            )

                        }
                    }
            }
        }


    suspend fun updateCountDown(
        title: String,
        notes: String,
        documentId: String,
        calendarDate: Calendar,
        calendarTime: Calendar,
        context: Context,
    ): Boolean =
        suspendCancellableCoroutine { continuation ->
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
                    documentId = documentId
                )

                mFireStore.collection("count_down_time").document(documentId)
                    .update(
                        "creationTime",
                        System.currentTimeMillis(),
                        "notes",
                        notes,
                        "targetDay",
                        calendarDate.get(Calendar.DATE),
                        "targetHour", calendarTime.get(Calendar.HOUR_OF_DAY),
                        "targetMinute", calendarTime.get(Calendar.MINUTE),
                        "targetMonth",
                        calendarDate.get(Calendar.MONTH) + 1,
                        "targetSecond",
                        calendarTime.get(Calendar.SECOND),
                        "targetYear",
                        calendarDate.get(Calendar.YEAR),
                        "title",
                        title
                    ).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
                            val message =
                                "Zaman doldu: ${targetCalendar.targetDay}/${targetCalendar.targetMonth}/${targetCalendar.targetYear}  ${targetCalendar.targetHour}:${targetCalendar.targetMinute}"
                            setAlarm(
                                context,
                                selectedCalendar.timeInMillis.toString(),
                                message,
                                title,
                                documentId
                            )
                            continuation.resume(true)
                        } else {
                            continuation.resume(false)

                        }
                    }

            }
        }


    suspend fun deleteCountDownTimer(countDownDocumentId: String, context: Context): Boolean =
        suspendCancellableCoroutine { continuation ->
            mFireStore.collection("count_down_time").document(countDownDocumentId).delete()
                .addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        cancelAlarm(countDownDocumentId, context)
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)

                    }
                }

        }
}


