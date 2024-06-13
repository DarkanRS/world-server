package com.rs.game.content.quests.plague_city.dialogues.objects

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.plague_city.utils.*
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject

class PrisonHouseDoorsD (player: Player, obj: GameObject) {
    private val eastDoor = PlagueCityUtils().isAtEastDoor(player.tile)
    private var closestMourner: NPC? = null
    private var furthestMourner: NPC? = null

    init {
        if (eastDoor) {
            closestMourner = World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == MOURNER_EAST_PRISON_DOOR }
            furthestMourner = World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == MOURNER_WEST_PRISON_DOOR }
        } else {
            closestMourner = World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == MOURNER_WEST_PRISON_DOOR }
            furthestMourner = World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == MOURNER_EAST_PRISON_DOOR }
        }
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                in STAGE_UNSTARTED..STAGE_GET_HANGOVER_CURE,
                in STAGE_FREED_ELENA..STAGE_COMPLETE -> {
                    val attemptedPrisonHouseDoors = player.questManager.getAttribs(Quest.PLAGUE_CITY).getB(ATTEMPTED_PRISON_HOUSE_DOORS)
                    val stageIsAtLeastSpokenToMilli = player.questManager.getStage(Quest.PLAGUE_CITY) >= STAGE_SPOKEN_TO_MILLI
                    if (player.y == 3273) {
                        simple("The door won't open.<br>You notice a black cross on the door.")
                        npc(NON_COMBAT_MOURNER, CALM_TALK, "I'd stand away from there. That black cross means that house has been touched by the plague.") {
                            World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == MOURNER_EAST_PRISON_DOOR }?.let { player.faceTile(it.tile) }
                        }
                        label("initialOps")
                        options {
                            if (!attemptedPrisonHouseDoors && stageIsAtLeastSpokenToMilli) {
                                op("But I think a kidnap victim is in here.") {
                                    player(CALM_TALK, "But I think a kidnap victim is in here.")
                                    npc(NON_COMBAT_MOURNER, CALM_TALK, "Sounds unlikely, even kidnappers wouldn't go in there. Even if someone is in there, they're probably dead by now.")
                                    options {
                                        op("Good point.") {
                                            player(CALM_TALK, "Good point.")
                                            goto("initialOps")
                                        }
                                        op("I want to check anyway.") {
                                            player(CALM_TALK, "I want to check anyway.")
                                            npc(NON_COMBAT_MOURNER, CALM_TALK, "You don't have clearance to go in there.")
                                            player(CALM_TALK, "How do I get clearance?")
                                            npc(NON_COMBAT_MOURNER, CALM_TALK, "Well you'd need to apply to the head mourner, or I suppose Bravek the city warder.")
                                            npc(NON_COMBAT_MOURNER, CALM_TALK, "I wouldn't get your hopes up though.") { player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(ATTEMPTED_PRISON_HOUSE_DOORS, true) }
                                            goto("initialOps")
                                        }
                                    }
                                }
                            }
                            op("I fear not a mere plague.") {
                                player(CALM_TALK, "I fear not a mere plague.")
                                npc(NON_COMBAT_MOURNER, CALM_TALK, "That's irrelevant. You don't have clearance to go in there.")
                                player(CALM_TALK, "How do I get clearance?")
                                npc(NON_COMBAT_MOURNER, CALM_TALK, "Well you'd need to apply to the head mourner, or I suppose Bravek the city warder.")
                                npc(NON_COMBAT_MOURNER, CALM_TALK, "I wouldn't get your hopes up though.") { player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(ATTEMPTED_PRISON_HOUSE_DOORS, true) }
                                goto("initialOps")
                            }
                            op("Thanks for the warning.")
                        }
                    } else if (player.y == 3272) {
                        Doors.handleDoor(player, obj)
                    }
                }

                STAGE_GAVE_HANGOVER_CURE -> {
                    if (player.y == 3273) {
                        player.walkToAndExecute(obj.tile) { handleMournerChat(player, obj) }
                    } else if (player.y == 3272) {
                        Doors.handleDoor(player, obj)
                    }
                }

                else -> { Doors.handleDoor(player, obj) }
            }
        }
    }

    private fun handleMournerChat(player: Player, obj: GameObject) {
        player.startConversation {
            npc(NON_COMBAT_MOURNER, CALM_TALK, "I'd stand away from there. That black cross means that house has been touched by the plague.") {
                player.faceTile(closestMourner?.tile)
                closestMourner?.faceTile(player.tile)
            }
            if (player.inventory.containsOneItem(WARRANT)) {
                player(CALM_TALK, "I have a warrant from Bravek to enter here.")
                npc(MOURNER_EAST_PRISON_DOOR, CONFUSED, "This is highly irregular. Please wait...")
                exec {
                    player.cutscene {
                        closestMourner?.faceEntityTile(furthestMourner)
                        closestMourner?.forceTalk("Hay, I got someone here with a warrant from Bravek, what should we do?")
                        furthestMourner?.faceEntityTile(closestMourner)
                        wait(4)
                        furthestMourner?.forceTalk("Well, you can't let them in...")
                        wait(1)
                        Doors.handleDoor(player, obj)
                        player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(ENTERED_PRISON_HOUSE, true)
                        player.simpleDialogue("You wait until the mourner's back is turned and sneak into the building.")
                        stop()
                    }
                }
            }
        }
    }
}
