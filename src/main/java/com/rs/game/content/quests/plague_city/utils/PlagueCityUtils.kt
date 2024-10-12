package com.rs.game.content.quests.plague_city.utils

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.World.removeObject
import com.rs.game.World.spawnObject
import com.rs.game.content.items.Spade
import com.rs.game.content.quests.plague_city.dialogues.objects.ElenaPrisonDoorD
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Animation
import com.rs.lib.game.Tile
import com.rs.utils.Ticks

class PlagueCityUtils {

    fun handleGasmaskWardrobe(player: Player, obj: GameObject, option: String) {
        when (obj.id) {

            GAS_MASK_WARDROBE[0] -> {
                player.anim(OPEN_WARDROBE_ANIM)
                obj.setIdTemporary(GAS_MASK_WARDROBE[1], Ticks.fromMinutes(1))
            }

            GAS_MASK_WARDROBE[1] -> {
                if (option == "Close") {
                    player.anim(OPEN_WARDROBE_ANIM)
                    obj.setId(GAS_MASK_WARDROBE[0])
                }
                if (option == "Search") {
                    if (player.questManager.getStage(Quest.PLAGUE_CITY) >= STAGE_RECEIVED_GAS_MASK) {
                        val hasGasMask = player.inventory.containsOneItem(GAS_MASK) || isWearingGasMask(player)
                        if (!hasGasMask) {
                            if (player.inventory.hasFreeSlots()) {
                                player.itemDialogue(GAS_MASK, "You take the spare gasmask which Alrena hid for you inside the wardrobe.")
                                player.inventory.addItem(GAS_MASK)
                            } else {
                                player.itemDialogue(GAS_MASK, "You find the spare gasmask which Alrena hid for you inside the wardrobe, but you don't have room to take it.")
                            }
                        } else {
                            player.sendMessage("You search the wardrobe but you find nothing.")
                        }
                    } else { player.sendMessage("You search the wardrobe but you find nothing.") }
                }
            }

        }
    }

    fun waterOnMudPatch(player: Player) {
        when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

            STAGE_PREPARE_TO_DIG -> {
                if (player.inventory.containsOneItem(BUCKET_OF_WATER)) {
                    player.lock(Animation(POUR_BUCKET_OF_WATER_ANIM).defs.emoteGameTicks)
                    var waterUsedSoFar = player.questManager.getAttribs(Quest.PLAGUE_CITY).getI(WATER_USED_ON_MUD)
                    player.questManager.getAttribs(Quest.PLAGUE_CITY).setI(WATER_USED_ON_MUD, ++waterUsedSoFar)
                    player.startConversation {
                        if (waterUsedSoFar <= 3) simple("You pour water onto the soil.<br>The soil softens slightly.")
                        else simple("You pour water onto the soil.<br>The soil is now soft enough to dig into.") { player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_CAN_DIG) }
                    }
                    player.anim(POUR_BUCKET_OF_WATER_ANIM)
                    player.inventory.deleteItem(BUCKET_OF_WATER, 1)
                    player.inventory.addItem(BUCKET)
                }
            }

            else -> { player.sendMessage("You see no reason to do that at the moment.") }

        }
    }

    fun digAtMudPatch(player: Player) {
        when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

            in STAGE_UNSTARTED..STAGE_CAN_DIG -> {
                if (player.questManager.getAttribs(Quest.PLAGUE_CITY).getI(WATER_USED_ON_MUD) != 4) {
                    player.anim(DIG_ANIM)
                    player.sendMessage("You dig the soil... The ground is rather hard.")
                } else {
                    player.startConversation {
                        item(SPADE, "You dig deep into the soft soil...<br>Suddenly it crumbles away!") { player.anim(DIG_ANIM) }
                        exec {
                            player.fadeScreen {
                                player.tele(EDMOND_SEWER_TELE_LOC)
                                player.vars.setVarBit(MUD_PATCH_VB, 1)
                                player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_UNCOVERED_SEWER_ENTRANCE)
                            }
                        }
                        simple("You fall through...<br>...you land in the sewer.<br>Edmond follows you down the hole.")
                    }
                }
            }

            else -> { Spade.dig(player) }

        }
    }

    fun handleSewerGrill(player: Player, attachingRope: Boolean) {
        if (attachingRope) {
            if (player.questManager.getStage(Quest.PLAGUE_CITY) == STAGE_NEED_HELP_WITH_GRILL) {
                player.walkToAndExecute(Tile.of(ROPE_ATTACH_TILE)) {
                    player.schedule {
                        player.lock()
                        player.faceDir(Direction.SOUTH)
                        wait(2)
                        player.soundEffect(ATTACH_ROPE_GRILL, false)
                        player.anim(TIE_ROPE_TO_GRILL_ANIM)
                        player.vars.setVarBit(GRILL_VB, 1)
                        wait(Animation(TIE_ROPE_TO_GRILL_ANIM).defs.emoteGameTicks)
                        player.itemDialogue(ROPE, "You tie the end of the rope to the sewer pipe's grill.")
                        player.inventory.deleteItem(ROPE, 1)
                        player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_ROPE_TIED_TO_GRILL)
                        player.unlock()
                    }
                }
            } else if (player.questManager.getStage(Quest.PLAGUE_CITY) >= STAGE_ROPE_TIED_TO_GRILL) {
                player.simpleDialogue("There is already a rope on the grill.<br>You should find someone to help pull it with you...")
            } else {
                player.sendMessage("You don't see any benefit in doing that.")
            }
            return
        }
        when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

            in STAGE_UNCOVERED_SEWER_ENTRANCE..STAGE_NEED_HELP_WITH_GRILL -> {
                player.schedule {
                    player.anim(GRILL_PULL_ATTEMPT_ANIM)
                    wait(Animation(GRILL_PULL_ATTEMPT_ANIM).defs.emoteGameTicks)
                    player.simpleDialogue("The grill is too secure.<br>You can't pull it off alone.")
                    player.questManager.setStage(Quest.PLAGUE_CITY, STAGE_NEED_HELP_WITH_GRILL)
                }
            }

            STAGE_ROPE_TIED_TO_GRILL -> { player.simpleDialogue("You should find someone to help pull the rope with you...") }
        }
    }

    fun handleSewerPipe(player: Player) {
        if (player.questManager.getStage(Quest.PLAGUE_CITY) < STAGE_GRILL_REMOVED) {
            player.simpleDialogue("There is a grill blocking your way.")
        } else if (player.isQuestComplete(Quest.PLAGUE_CITY)) {
            if (!isWearingGasMask(player) && (!player.isQuestStarted(Quest.BIOHAZARD) && !player.isQuestComplete(Quest.BIOHAZARD))) {
                player.playerDialogue(WORRIED, "I shouldn't enter without my gasmask.")
            } else {
                handleEnteringSewerPipe(player)
            }
        } else {
            if (isWearingGasMask(player)) {
                handleEnteringSewerPipe(player)
            } else {
                player.faceTile(Tile.of(EDMOND_SEWER_SPAWN_LOC))
                player.npcDialogue(EDMOND_BELOW_GROUND, WORRIED, "I can't let you enter the city without your gasmask on.")
            }
        }
    }

    private fun handleEnteringSewerPipe(player: Player) {
        player.schedule {
            player.forceMove(Direction.SOUTH, 4, CLIMB_INTO_PIPE_ANIM, 20, 100) {
                player.tele(SEWER_PIPE_TELE_LOC)
                player.soundEffect(OPEN_MANHOLE_SOUND, false)
                player.simpleDialogue("You climb up through the sewer pipe.")
                player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(ENTERED_CITY, true)
            }
            wait(1)
            player.fadeScreen {}
        }
    }

    fun handleManhole(player: Player, obj: GameObject, option: String) {
        val openedCover = GameObject(obj.id + 2, obj.type, obj.rotation, obj.x, obj.y - 1, obj.plane)
        when (option) {

            "Open" -> {
                player.faceObject(obj)
                obj.setId(obj.id + 1)
                spawnObject(openedCover)
                player.soundEffect(OPEN_MANHOLE_SOUND, false)
                WorldTasks.schedule (Ticks.fromSeconds(30)) {
                    obj.setId(obj.id - 1)
                    removeObject(openedCover)
                }
            }

            "Close" -> player.sendMessage("You see no reason to do that.")

            "Climb-down" -> {
                if (player.questManager.getStage(Quest.PLAGUE_CITY) > STAGE_ROPE_TIED_TO_GRILL) {
                    player.sendMessage("You climb down through the manhole.")
                    player.tele(MANHOLE_TELE_LOC)
                } else {
                    player.sendMessage("I don't know what's down there. I don't think I want to find out...")
                }
            }

        }
    }

    fun searchKeyBarrel(player: Player) {
        val hasKeyAlready = player.inventory.containsOneItem(SMALL_KEY)
        player.startConversation {
            when (player.questManager.getStage(Quest.PLAGUE_CITY)) {

                STAGE_GAVE_HANGOVER_CURE -> {
                    if (!hasKeyAlready) {
                        if (player.inventory.hasFreeSlots()) {
                            player.inventory.addItem(SMALL_KEY)
                            player.sendMessage("You find a small key in the barrel.")
                            player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(FOUND_KEY_IN_BARREL, true)
                        } else { player.sendMessage("You find a small key in the barrel, but don't have enough room to take it.") }
                    } else {
                        player.sendMessage("You search the barrel but find nothing.")
                    }
                }

                else -> { player.sendMessage("You search the barrel but find nothing.") }

            }
        }
    }

    fun handlePrisonStairs(player: Player) {
        player.useStairs(player.transform(0, if (player.y > 9000) -6400 else 6400, 0))
        if (player.y > 9000) player.sendMessage("You walk up the stairs...")
        else player.sendMessage("You walk down the stairs...")
    }

    fun handleElenaPrisonDoor(player: Player, obj: GameObject) {
        val hasKeyAlready = player.inventory.containsOneItem(SMALL_KEY)
        if (player.x == 2539) {
            player.walkToAndExecute(obj.tile) {
                if (hasKeyAlready) {
                    player.sendMessage("You unlock the door.")
                    player.questManager.getAttribs(Quest.PLAGUE_CITY).setB(ENTERED_PRISON_CELL, true)
                    Doors.handleDoor(player, obj)
                } else { ElenaPrisonDoorD(player) }
            }
        } else if (player.x == 2540) { Doors.handleDoor(player, obj) }
    }

    fun handleScruffyNote(player: Player) {
        val scruffyNoteText = arrayOf(
            "Got a bncket of nnilk",
            "Tlen qrind sorne lhoculate",
            "vnith a pestal and rnortar",
            "ald the grourd dlocolate to tho milt",
            "fnales add 5cme snape gras5"
        )

        for ((index, note) in scruffyNoteText.withIndex()) {
            player.packets.setIFText(222, index + 3, note)
        }
        player.interfaceManager.sendInterface(222)
    }

    fun handleMagicScroll(player: Player) {
        player.startConversation {
            item(A_MAGIC_SCROLL, "You memorise what is written on the scroll.")
            simple("You can now cast the Ardougne Teleport spell provided you have the required runes and magic level.") {
                player.set(ARDOUGNE_TELEPORT_UNLOCKED, true)
                player.inventory.deleteItem(A_MAGIC_SCROLL, 1)
                player.anim(-1)
            }
        }
    }

    fun isWearingGasMask(player: Player): Boolean {
        return player.equipment.getId(Equipment.HEAD) == GAS_MASK
    }

    fun isInWestArdougne(tile: Tile): Boolean {
        for (range in WEST_ARDOUGNE_RANGES) if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) return true
        return false
    }

    fun isAtEastDoor(tile: Tile): Boolean {
        for (range in SOUTH_EAST_HOUSE_EAST_DOOR) if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) return true
        return false
    }

}
