package com.muratguzel.trackyourtime.data.entitiy

import java.io.Serializable

data class CountDownTime(
    val userId: String? = null,
    val creationTime: Long? = null,
    val targetYear: Int? = null,
    val targetMonth: Int? = null,
    val targetDay: Int? = null,
    val targetHour: Int? = null,
    val targetMinute: Int? = null,
    val targetSecond: Int? = null,
    val title:String? = null,
    val notes:String? = null,
    var documentId: String? = null
): Serializable {

}