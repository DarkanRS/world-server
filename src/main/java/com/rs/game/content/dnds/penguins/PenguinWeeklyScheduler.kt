package com.rs.game.content.dnds.penguins

import com.rs.game.tasks.WorldTasks
import com.rs.utils.Ticks
import com.rs.lib.util.Logger
import java.time.DayOfWeek
import java.time.Duration
import java.time.Month
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.WeekFields
import java.util.Date

class PenguinWeeklyScheduler() {

    companion object {
        var RESET_DAY = DayOfWeek.WEDNESDAY
        var RESET_HOUR = 0
        var RESET_MIN = 0
        var RESET_SEC = 0
    }

    fun scheduleWeeklyReset(action: () -> Unit) {

        val delayInTicks = getDelayUntilNextResetInTicks()

        if (delayInTicks > 0) {
            Logger.debug(PenguinWeeklyScheduler::class.java, "scheduleWeeklyReset", "Scheduled weekly reset in $delayInTicks ticks.")

            if (!WorldTasks.hasTask("penguin_has")) {
                WorldTasks.schedule("penguin_has", delayInTicks) {
                    action()
                    scheduleWeeklyReset(action)
                }
            }
        } else {
            Logger.debug(PenguinWeeklyScheduler::class.java, "scheduleWeeklyReset", "No valid delay found; skipping rescheduling.")
        }
    }

    private fun getDelayUntilNextResetInTicks(): Int {
        val currentTime = ZonedDateTime.now(ZoneOffset.UTC)
        val delayUntilResetMillis = Duration.between(currentTime, getNextWeeklyReset()).toMillis()
        val delayInSeconds = (delayUntilResetMillis / 1000).toInt()
        return maxOf(Ticks.fromSeconds(delayInSeconds), 1)
    }

    fun getNextWeeklyReset(): ZonedDateTime {
        var nextReset = ZonedDateTime.now(ZoneOffset.UTC)
            .with(getResetDay())
            .withHour(getResetHour())
            .withMinute(getResetMin())
            .withSecond(getResetSec())
            .withNano(0)

        if (nextReset.isBefore(ZonedDateTime.now(ZoneOffset.UTC))) {
            nextReset = nextReset.plusWeeks(1)
        }

        Logger.debug(PenguinWeeklyScheduler::class.java, "getNextWeeklyReset", "Next weekly reset: ${Date.from(nextReset.toInstant())}.")
        return nextReset
    }

    fun setResetDay(day: DayOfWeek) {
        RESET_DAY = day
    }
    fun setResetHour(hour: Int) {
        RESET_HOUR = hour
    }
    fun setResetMin(min: Int) {
        RESET_MIN = min
    }
    fun setResetSec(sec: Int) {
        RESET_SEC = sec
    }

    fun getResetDay() : DayOfWeek {
        return RESET_DAY;
    }
    fun getResetHour() : Int {
        return RESET_HOUR;
    }
    fun getResetMin() : Int {
        return RESET_MIN;
    }
    fun getResetSec() : Int {
        return RESET_SEC;
    }

    fun getCurrentDayAndTime(): ZonedDateTime {
        return ZonedDateTime.now(ZoneOffset.UTC)
    }

    fun getCurrentWeek(): Int {
        return ZonedDateTime.now(ZoneOffset.UTC).get(WeekFields.ISO.weekOfWeekBasedYear())
    }

    fun getCurrentMonth(): Month {
        return ZonedDateTime.now(ZoneOffset.UTC).month
    }

}
