package com.rs.game.content.dnds.penguins

import com.rs.db.WorldDB
import com.rs.game.World
import com.rs.lib.util.Logger
import java.io.IOException
import java.time.temporal.WeekFields
import java.util.Locale

enum class PolarBearLocation(val id: Int) { RELLEKKA(0), VARROCK(1), RIMMINGTON(2), MUSA_POINT(3), ARDOUGNE(4), FALADOR(5) }
data class PolarBearState(val location: LocationAndWeek, val previousLocations: List<PolarBearLocation>)
data class LocationAndWeek(val location: PolarBearLocation, val weekNumber: Int)

const val POLAR_BEAR_POINTS = 1

class PolarBearManager() {
    private var polarBearLocationId: Int = 0
    private var previousLocations = mutableListOf<PolarBearLocation>()
    private var currentWeekNumber: Int? = null

    fun setLocation() {
        val currentDateTime = PenguinServices.penguinWeeklyScheduler.getCurrentDayAndTime()
        val isResetDay = currentDateTime.dayOfWeek == PenguinServices.penguinWeeklyScheduler.getResetDay()
        val weekNumber = currentDateTime.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())

        val latestPolarBear = WorldDB.getPenguinHAS().getPolarBear()
        val polarBearState = latestPolarBear?.let { polarBear ->
            PolarBearState(
                location = LocationAndWeek(location = polarBear.location, weekNumber = polarBear.week),
                previousLocations = polarBear.previousLocations.map { PolarBearLocation.valueOf(it) }
            )
        }

        polarBearLocationId = polarBearState?.location?.location?.id ?: 0
        currentWeekNumber = polarBearState?.location?.weekNumber
        previousLocations = polarBearState?.previousLocations?.toMutableList() ?: mutableListOf()

        if (currentWeekNumber == null || (weekNumber != currentWeekNumber && isResetDay)) {
            val newLocation = getNextPolarBearLocation()
            if (newLocation != null) {
                if (!previousLocations.contains(newLocation)) {
                    previousLocations.add(newLocation)
                }

                if (previousLocations.size > 4) {
                    previousLocations.removeAt(0)
                }

                val locationWithWeek = LocationAndWeek(location = newLocation, weekNumber = weekNumber)
                val newState = PolarBearState(location = locationWithWeek, previousLocations = previousLocations)

                try {
                    WorldDB.getPenguinHAS().createPolarBear(newLocation, previousLocations.map { it.name }, null, weekNumber, POLAR_BEAR_POINTS)
                    polarBearLocationId = newLocation.id
                    World.players.forEach { player -> player.vars.setVarBit(2045, polarBearLocationId) }
                    Logger.debug(PolarBearManager::class.java, "setLocation", "Polar bear location changed to ${newState.location.location} for ${World.players.size()} logged in player(s).")
                } catch (e: IOException) {
                    Logger.debug(PolarBearManager::class.java, "setLocation", "Failed to save polar bear state: ${e.message}")
                }
            }
        }
    }

    fun setNewLocation() {
        val newLocation = getNextPolarBearLocation()
        if (newLocation != null) {
            if (!previousLocations.contains(newLocation)) {
                previousLocations.add(newLocation)
            }
            if (previousLocations.size > 4) {
                previousLocations.removeAt(0)
            }

            val weekNumber = PenguinServices.penguinWeeklyScheduler.getCurrentDayAndTime().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear())
            val locationWithWeek = LocationAndWeek(location = newLocation, weekNumber = weekNumber)
            val newState = PolarBearState(location = locationWithWeek, previousLocations = previousLocations)

            try {
                WorldDB.getPenguinHAS().createPolarBear(newLocation, previousLocations.map { it.name }, null, weekNumber, POLAR_BEAR_POINTS)
                polarBearLocationId = newLocation.id
                World.players.forEach { player -> player.vars.setVarBit(2045, polarBearLocationId) }
                Logger.debug(PolarBearManager::class.java, "setNewLocation", "Polar bear location manually changed to ${newState.location.location} for ${World.players.size()} logged in player(s).")
            } catch (e: IOException) {
                Logger.debug(PolarBearManager::class.java, "setNewLocation", "Failed to save polar bear state: ${e.message}")
            }
        }
    }

    private fun getNextPolarBearLocation(): PolarBearLocation? {
        val availableLocations = PolarBearLocation.entries.toMutableList()
        availableLocations.removeAll(previousLocations)

        if (availableLocations.isEmpty()) {
            previousLocations.clear()
            availableLocations.addAll(PolarBearLocation.entries)
        }

        val newLocation = availableLocations.random()
        previousLocations.add(newLocation)

        if (previousLocations.size > 4) {
            previousLocations.removeAt(0)
        }

        return newLocation
    }

    fun getCurrentLocationId(): Int = polarBearLocationId

    fun getLocationName(locationId: Int): String? = PolarBearLocation.entries.find { it.id == locationId }?.name
}
