package com.dn0ne.moneymate.util

import androidx.compose.runtime.Composable
import com.dn0ne.moneymate.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

object DateFormatter {

    @Composable
    fun formatToDayOfWeekDayMonth(localDate: LocalDate): String {
        return "${dayOfWeekLocalized(localDate.dayOfWeek)}, ${localDate.dayOfMonth} ${monthLocalized(localDate.month)}"
    }

    @Composable
    fun formatToDayMonth(localDate: LocalDate): String {
        return "${localDate.dayOfMonth} ${monthLocalized(localDate.month)}"
    }

    @Composable
    fun formatToDayMonthYear(localDate: LocalDate): String {
        return "${formatToDayMonth(localDate)} ${localDate.year}"
    }

    @Composable
    fun formatDateRangeToString(startDate: LocalDate, endDate: LocalDate): String {
        return if (startDate == endDate) {
            formatToDayMonth(startDate)
        } else {
            if (startDate.year == endDate.year) {
                formatToDayMonth(startDate) +
                        " - " +
                        formatToDayMonth(endDate)
            } else {
                formatToDayMonthYear(startDate) +
                        " - " +
                        formatToDayMonthYear(endDate)
            }
        }
    }

    @Composable
    fun dayOfWeekLocalized(dayOfWeek: DayOfWeek): String {
        return stringResource(
            when (dayOfWeek) {
                DayOfWeek.MONDAY -> MR.strings.monday

                DayOfWeek.TUESDAY -> MR.strings.tuesday

                DayOfWeek.WEDNESDAY -> MR.strings.wednesday

                DayOfWeek.THURSDAY -> MR.strings.thursday

                DayOfWeek.FRIDAY -> MR.strings.friday

                DayOfWeek.SATURDAY -> MR.strings.saturday

                DayOfWeek.SUNDAY -> MR.strings.sunday

                else -> MR.strings.monday
            }
        )
    }

    @Composable
    fun monthLocalized(month: Month): String {
        return stringResource(
            when(month) {
                Month.JANUARY -> MR.strings.january
                Month.FEBRUARY -> MR.strings.february
                Month.MARCH -> MR.strings.march
                Month.APRIL -> MR.strings.april
                Month.MAY -> MR.strings.may
                Month.JUNE -> MR.strings.june
                Month.JULY -> MR.strings.july
                Month.AUGUST -> MR.strings.august
                Month.SEPTEMBER -> MR.strings.september
                Month.OCTOBER -> MR.strings.october
                Month.NOVEMBER -> MR.strings.november
                Month.DECEMBER -> MR.strings.december
                else -> MR.strings.january
            }
        )
    }
}