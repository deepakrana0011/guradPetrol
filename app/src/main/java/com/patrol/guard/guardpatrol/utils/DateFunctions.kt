package com.patrol.guard.guardpatrol.utils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateFunctions {

    fun getDateFromTimeStamp(timeStamp: Long): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone//get your local time zone.
        val sdf = SimpleDateFormat("dd MMM yyyy")
        sdf.timeZone = tz//set time zone.
        return sdf.format(Date(timeStamp))
    }


    fun get12HoursFormat(date: String?): String {
        if (date != null) {
            val inputPattern = "HH:mm"
            val outputPattern = "hh:mm a"
            val inputFormat = SimpleDateFormat(inputPattern)
            val outputFormat = SimpleDateFormat(outputPattern)

            var newDate: Date? = null
            var str: String? = null

            try {
                newDate = inputFormat.parse(date)
                str = outputFormat.format(newDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }
            return str!!
        } else {
            return ""
        }
    }


    fun getCurrentTime(): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone//get your local time zone.
        val sdf = SimpleDateFormat("hh:mm")
        sdf.timeZone = tz//set time zone.
        var time = sdf.format(Date(System.currentTimeMillis()))
        return time
    }

    fun amOrpm(): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone//get your local time zone.
        val sdf = SimpleDateFormat("a")
        sdf.timeZone = tz//set time zone.
        var time = sdf.format(Date(System.currentTimeMillis()))
        return time
    }

    fun getStartEndTime(timeStamp: Long): String {
        val cal = Calendar.getInstance()
        val tz = cal.timeZone//get your local time zone.
        val time = SimpleDateFormat("hh:mm a")
        time.timeZone = tz//set time zone.

        val date = SimpleDateFormat("dd MMM yyyy")
        date.timeZone = tz

        var formatedTime = time.format(Date(timeStamp))
        var formatedDate = date.format(Date(timeStamp))
        var finalTimeDate = formatedTime + " | " + formatedDate
        return finalTimeDate
    }

    fun formatMonth(month: String?): String? {
        val monthParse = SimpleDateFormat("MM")
        val monthDisplay = SimpleDateFormat("MMMM")
        return monthDisplay.format(monthParse.parse(month))
    }

}