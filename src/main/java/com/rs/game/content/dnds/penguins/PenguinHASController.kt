package com.rs.game.content.dnds.penguins

import com.rs.Settings
import com.rs.db.WorldDB
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.dnds.penguins.PenguinServices.penguinHideAndSeekManager
import com.rs.game.content.dnds.penguins.PenguinServices.penguinSpawnService
import com.rs.game.content.dnds.penguins.PenguinServices.penguinWeeklyScheduler
import com.rs.game.content.dnds.penguins.PenguinServices.polarBearManager
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.npc.NPC
import com.rs.lib.game.Tile
import com.rs.lib.util.Logger
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.annotations.ServerStartupEvent.Priority
import com.rs.plugin.kts.getInteractionDistance
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onNpcClick
import com.rs.plugin.kts.onObjectClick
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

var DND_ENABLED: Boolean = true

const val PENGUIN_POINTS = "PenguinPoints"
const val MAX_PENGUIN_POINTS = 50
const val UNLOCKED_PENGUIN_HAS = "Unlocked_PenguinHAS"

object PenguinServices {
    val penguinHideAndSeekManager = PenguinManager()
    val penguinSpawnService = PenguinSpawnService()
    val penguinWeeklyScheduler = PenguinWeeklyScheduler()
    val polarBearManager = PolarBearManager()
}

@ServerStartupEvent
fun mapPenguinHideAndSeekInteractions() {
    if (!DND_ENABLED) return

    // Penguin interactions
    instantiateNpc(8104, 8105, 8107, 8108, 8109, 8110, 14415, 14766) { id, tile -> PenguinNPC(id, tile) }
    getInteractionDistance(8104, 8105, 8107, 8108, 8109, 8110, 14415, 14766) { _, _ -> 3 }
    onNpcClick(8104, 8105, 8107, 8108, 8109, 8110, 14415, 14766) { (player, npc) ->
        if (player.get(UNLOCKED_PENGUIN_HAS) as Boolean) {
            val penguinsEnum = Penguins.getPenguinByTile(npc.respawnTile)

            if (penguinsEnum != null) {
                val penguin = WorldDB.getPenguinHAS().getPenguinByIdAndLocation(npc.id, npc.respawnTile)
                synchronized(penguin) {
                    if (penguin.spotters.contains(player.username)) {
                        player.sendMessage("You've already spotted this penguin spy.")
                        return@onNpcClick
                    }
                    penguin.addSpotter(player.username)
                    WorldDB.getPenguinHAS().savePenguinSpotters(penguin)
                    player.lock(3)
                    player.anim(10355)
                    player.jingle(345)
                    player.incrementCount("Penguin Agents spied", 1)
                    player.vars.saveVarBit(5276, WorldDB.getPenguinHAS().getPenguinsSpottedByPlayer(player.username)) // Set varbit for QuickChat
                    player.sendMessage("You spy on the penguin.")

                    val currentPoints = player.getI(PENGUIN_POINTS).coerceAtLeast(0)
                    val pointsToAdd = if (player.isQuestComplete(Quest.COLD_WAR)) penguinsEnum.points else 1
                    val newPoints = (currentPoints + pointsToAdd).coerceAtMost(MAX_PENGUIN_POINTS)

                    player.set(PENGUIN_POINTS, newPoints)
                    if (newPoints == MAX_PENGUIN_POINTS) {
                        player.sendMessage("<col=FF0000>You have reached a maximum of $MAX_PENGUIN_POINTS Penguin Points. You should spend them with Larry to continue earning more.</col>")
                    }
                    val spottedPenguins = WorldDB.getPenguinHAS().getPenguinsSpottedByPlayer(player.username)
                    if (spottedPenguins == 10) {
                        player.sendMessage("You've spotted all the penguin spies this week!")
                    }
                }
            } else {
                player.simpleDialogue("You don't recognise this penguin.")
            }
        } else {
            player.startConversation {
                player(HeadE.NERVOUS, "What in ${Settings.getConfig().serverName} is this?!")
                player(HeadE.CONFUSED, "I wonder if Larry at Ardougne Zoo knows anything about this...")
            }
        }
    }

    // Polar bear interactions
    onLogin { (player) ->
        player.vars.setVarBit(2045, polarBearManager.getCurrentLocationId())
        player.vars.saveVarBit(5276, WorldDB.getPenguinHAS().getPenguinsSpottedByPlayer(player.username))
    }
    onObjectClick(43094, 43095, 43096, 43097, 43098, 43099) { (player) ->
        if (player.get(UNLOCKED_PENGUIN_HAS) as Boolean) {
            if (player.isQuestComplete(Quest.HUNT_FOR_RED_RAKTUBER)) {
                val polarBearEnum = PolarBearLocation.entries.find { it.id == polarBearManager.getCurrentLocationId() }

                if (polarBearEnum != null) {
                    val polarBear = WorldDB.getPenguinHAS().polarBear
                    synchronized(polarBear) {
                        if (polarBear.spotters.contains(player.username)) {
                            player.sendMessage("You've already spotted the polar bear agent!")
                            return@onObjectClick
                        }
                        polarBear.addSpotter(player.username)
                        WorldDB.getPenguinHAS().savePolarBearSpotters(polarBear)
                        player.lock(5)
                        player.anim(10355)
                        player.jingle(345)
                        player.incrementCount("Polar Bear Agents found", 1)
                        player.sendMessage("You found the polar bear agent.")

                        val currentPoints = player.getI(PENGUIN_POINTS).coerceAtLeast(0)
                        val newPoints = (currentPoints + polarBear.points).coerceAtMost(MAX_PENGUIN_POINTS)

                        player.set(PENGUIN_POINTS, newPoints)
                        if (newPoints == MAX_PENGUIN_POINTS) {
                            player.sendMessage("<col=FF0000>You have reached a maximum of $MAX_PENGUIN_POINTS Penguin Points. You should spend them with Larry to continue earning more.</col>")
                        }
                    }
                } else {
                    player.simpleDialogue("You don't recognise this polar bear.")
                }
            } else {
                player.sendMessage("You need to have completed Hunt For Red Raktuber to spot this agent.")
            }
        } else {
            player.startConversation {
                player(HeadE.NERVOUS, "Is that a polar bear in the well?!")
                player(HeadE.CONFUSED, "I wonder if Larry at Ardougne Zoo knows anything about this...")
            }
        }
    }

    // Spy Notebook
    onItemClick(13732, options = arrayOf("Read")) { (player) ->
        val penguinsSpottedCount = WorldDB.getPenguinHAS().getPenguinsSpottedByPlayer(player.username)

        var dialogueMessage = "You have spotted $penguinsSpottedCount ${if (penguinsSpottedCount == 1) "penguin" else "penguins"} this week."

        if (player.isQuestComplete(Quest.HUNT_FOR_RED_RAKTUBER)) {
            val polarBear = WorldDB.getPenguinHAS().polarBear
            val spotted = polarBear?.spotters?.contains(player.username) == true
            dialogueMessage += "<br><br>You have ${if (spotted) "spotted the polar bear" else "not yet spotted the polar bear"} this week."
        }

        dialogueMessage += "<br><br>You have ${player.getI(PENGUIN_POINTS).coerceAtLeast(0)} Penguin ${if (player.getI(PENGUIN_POINTS) == 1) "Point" else "Points"} to spend with Larry."

        player.simpleDialogue(dialogueMessage)
    }

}

@ServerStartupEvent(Priority.POST_PROCESS)
fun initializePenguinHideAndSeek() {
    if (!DND_ENABLED) return

    // Penguin spawning
    penguinHideAndSeekManager.checkAndSpawn()

    // Polar Bear spawning
    polarBearManager.setLocation()

    // Weekly reset task
    scheduleWeeklyReset()
}

fun scheduleWeeklyReset() {
    penguinWeeklyScheduler.scheduleWeeklyReset {
        for (player in World.players) { // Reset Quickchat varbit to 0 for all logged in players
            player.vars.saveVarBit(5276, 0)
        }
        penguinHideAndSeekManager.checkAndSpawn()
        polarBearManager.setLocation()
    }
}

class PenguinManager() {
    fun checkAndSpawn() {

        penguinSpawnService.loadSpawns()

        val today = LocalDate.now()
        val currentWeek = today.get(WeekFields.of(Locale.getDefault()).weekOfYear())
        val currentWeekSpawns = penguinSpawnService.getSpawnsForWeek(currentWeek)

        val isResetDay = today.dayOfWeek == penguinWeeklyScheduler.getResetDay()

        if (currentWeekSpawns.isEmpty()) {
            if (penguinSpawnService.isSpawnEmpty()) {
                Logger.debug(PenguinManager::class.java, "checkAndSpawn", "Spawning new penguins")
                penguinSpawnService.prepareNew(currentWeek)
            } else if (isResetDay) {
                Logger.debug(PenguinManager::class.java, "checkAndSpawn", "Refreshing penguin spawns.")
                penguinSpawnService.prepareNew(currentWeek)
            } else {
                penguinSpawnService.prepareExisting(currentWeek)
                Logger.debug(PenguinManager::class.java, "checkAndSpawn", "Spawn existing penguins.")
            }
        } else {
            penguinSpawnService.prepareExisting(currentWeek)
            Logger.debug(PenguinManager::class.java, "checkAndSpawn", "Current week penguins are already populated. Spawn existing penguins.")
        }
    }
}
