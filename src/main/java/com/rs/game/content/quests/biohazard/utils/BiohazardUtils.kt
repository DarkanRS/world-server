package com.rs.game.content.quests.biohazard.utils

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.world.doors.Doors.handleDoubleDoor
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.utils.Ticks

class BiohazardUtils(val player: Player) {
    private val usedBirdfeed = player.questManager.getAttribs(Quest.BIOHAZARD).getB(USED_BIRD_FEED)
    private val gotDistillator = player.questManager.getAttribs(Quest.BIOHAZARD).getB(GOT_DISTILLATOR)
    val stage = player.questManager.getStage(Quest.BIOHAZARD)

    fun handleJericoCupboard(obj: GameObject, option: String) {
        when (obj.id) {

            JERICO_CUPBOARD[0] -> {
                player.anim(OPEN_CUPBOARD_ANIM)
                obj.setIdTemporary(JERICO_CUPBOARD[1], Ticks.fromMinutes(1))
            }

            JERICO_CUPBOARD[1] -> {
                if (option == "Close") {
                    player.anim(OPEN_CUPBOARD_ANIM)
                    obj.setId(JERICO_CUPBOARD[0])
                }
                if (option == "Search") {
                    if (stage >= STAGE_SPEAK_TO_OMART) {
                        player.startConversation {
                            item(BIRD_FEED, "The cupboard is full of birdfeed.")
                            if (player.inventory.hasFreeSlots()) {
                                if (player.inventory.containsOneItem(BIRD_FEED) || usedBirdfeed) {
                                    player(CALM_TALK, "I don't need any more birdfeed.")
                                } else if (player.bank.containsItem(BIRD_FEED) && !player.inventory.containsOneItem(BIRD_FEED)) {
                                    player(CALM_TALK, "I shouldn't take any more of Jerico's birdfeed - I've already got enough in the bank.")
                                } else {
                                    player(CALM_TALK, "Mmm, birdfeed! Now what could I do with that?") { player.inventory.addItem(BIRD_FEED) }
                                }
                            }
                        }
                    } else {
                        player.cutscene {
                            player.sendMessage("You search the cupboard...")
                            wait(2)
                            player.sendMessage("and find nothing of interest.")
                        }
                    }
                }
            }
        }
    }

    fun handleBirdfeedOnWatchtowerFence() {
        if (stage in STAGE_SPEAK_TO_OMART..STAGE_MOURNERS_DISTRACTED) {
            if (!usedBirdfeed) {
                player.cutscene {
                    player.sendMessage("You throw a handful of seeds onto the watchtower.")
                    player.inventory.deleteItem(BIRD_FEED, 1)
                    wait(3)
                    player.sendMessage("The mourners do not seem to notice.")
                    player.questManager.getAttribs(Quest.BIOHAZARD).setB(USED_BIRD_FEED, true)
                }
            } else if (usedBirdfeed && stage == STAGE_MOURNERS_DISTRACTED){
                player.sendMessage("You have already thrown birdfeed on the watchtower and the mourners are distracted.")
            } else {
                player.sendMessage("You have already thrown birdfeed on the watchtower.")
            }
        } else {
            player.sendMessage("Nothing interesting happens.")
        }
    }

    fun handleWatchtowerFence() {
        if (stage in STAGE_SPEAK_TO_OMART..STAGE_MOURNERS_DISTRACTED) {
            if (!usedBirdfeed) {
                player.sendMessage("You wonder if you could use something on the fence to distract the guards.")
            } else {
                player.sendMessage("Looks sturdy.")
            }
        }
        player.startConversation {
            npc(MOURNER_WITH_KEY, CALM_TALK, "Keep away civilian.")
            player(FRUSTRATED, "What's it to you?")
            npc(MOURNER_WITH_KEY, CALM_TALK, "This tower's here for your protection.")
        }
    }

    fun handleOpeningPigeonCage() {
        if (player.tile == PIGEON_RELEASE_TILES[0]) {
            if (stage in STAGE_SPEAK_TO_OMART..<STAGE_COMPLETED_WALL_CROSSING) {
                if (usedBirdfeed) {
                    player.cutscene {
                        projectile(PIGEON_RELEASE_TILES[0], PIGEON_RELEASE_TILES[1], BIRD_SPOTANIM, 10, 150, 0, 18, 16, null)
                        projectile(PIGEON_RELEASE_TILES[0], PIGEON_RELEASE_TILES[2], BIRD_SPOTANIM, 12, 160, 0, 17, 14, null)
                        projectile(PIGEON_RELEASE_TILES[0], PIGEON_RELEASE_TILES[3], BIRD_SPOTANIM, 12, 170, 0, 19, 18, null)
                        player.sendMessage("The pigeons fly towards the watch tower.")
                        player.inventory.replace(PIGEON_CAGE, PIGEON_CAGE+1)
                        wait(4)
                        player.sendMessage("The mourners are frantically trying to scare the pigeons away.")
                        player.questManager.setStage(Quest.BIOHAZARD, STAGE_MOURNERS_DISTRACTED)
                    }
                } else {
                    player.sendMessage("I should use something on the fence first, to keep the pigeons busy for a while.")
                }
            } else if (stage in STAGE_MOURNERS_DISTRACTED..STAGE_FOUND_DISTILLATOR) {
                player.sendMessage("The mourners are already distracted.")
            } else {
                player.sendMessage("You don't feel like you should release the pigeons here.")
            }
        } else {
            player.sendMessage("You don't feel like you should release the pigeons here.")
        }
    }

    fun handleWallCrossing() {
        var eastArdougneWall = World.getObjectWithId(EAST_ARDOUGNE_WALL, WALL_CROSSING_OBJ)
        var westArdougneWall = World.getObjectWithId(WEST_ARDOUGNE_WALL, WALL_CROSSING_OBJ)
        if (eastArdougneWall != null && westArdougneWall != null) {
            eastArdougneWall.setIdTemporary(WALL_CROSSING_OBJ - 1, 2)
            westArdougneWall.setIdTemporary(WALL_CROSSING_OBJ - 1, 2)
            player.cutscene {
                player.sendMessage("You climb up the rope ladder...")
                wait(3)
                if (stage == STAGE_MOURNERS_DISTRACTED) {
                    player.questManager.setStage(Quest.BIOHAZARD, STAGE_COMPLETED_WALL_CROSSING)
                }
                if (player.x > EAST_ARDOUGNE_WALL.x) {
                    player.tele(WEST_ARDOUGNE_WALL.x - 1, WEST_ARDOUGNE_WALL.y, 0)
                } else {
                    player.tele(EAST_ARDOUGNE_WALL.x + 1, EAST_ARDOUGNE_WALL.y, 0)
                }
                player.sendMessage("and drop down on the other side.")
            }
        } else {
            player.sendMessage("It looks as though someone else is using the rope ladder at the moment.")
        }
    }

    fun handleUseAppleOnCauldron() {
        if (stage == STAGE_COMPLETED_WALL_CROSSING) {
            player.cutscene {
                player.sendMessage("You place the rotten apple in the pot...")
                player.inventory.deleteItem(ROTTEN_APPLE, 1)
                player.soundEffect(APPLE_IN_CAULDRON_SOUND, false)
                player.questManager.setStage(Quest.BIOHAZARD, STAGE_APPLE_IN_CAULDRON)
                wait(4)
                player.sendMessage("and it quickly dissolves into the stew.")
                wait(2)
                player.sendMessage("That wasn't very nice.")
            }
        } else if (stage >= STAGE_APPLE_IN_CAULDRON) {
            player.playerDialogue(CALM_TALK, "The stew is already looking a bit rotten.<br>I shouldn't need to put another rotten apple in there.")
        } else {
            player.sendMessage("You see no benefit in doing that.")
        }
    }

    fun handleDoctorsGownBox(obj: GameObject, option: String) {
        when (obj.id) {

            DOCTORS_GOWN_BOX[0] -> {
                player.anim(OPEN_CUPBOARD_ANIM)
                obj.setIdTemporary(DOCTORS_GOWN_BOX[1], Ticks.fromMinutes(1))
                player.sendMessage("You open the box.")
            }

            DOCTORS_GOWN_BOX[1] -> {
                if (option == "Close") {
                    player.anim(OPEN_CUPBOARD_ANIM)
                    obj.setId(DOCTORS_GOWN_BOX[0])
                }
                if (option == "Search") {
                    player.startConversation {
                        player.sendMessage("You search the box...")
                        player.cutscene {
                            wait(2)
                            if (stage in STAGE_COMPLETED_WALL_CROSSING..STAGE_FOUND_DISTILLATOR) {
                                if (player.inventory.containsOneItem(DOCTORS_GOWN))
                                    player.sendMessage("but you find nothing of interest.")
                                else
                                    if (player.inventory.hasFreeSlots()) {
                                        player.sendMessage("and find a doctors' gown.")
                                        player.inventory.addItem(DOCTORS_GOWN)
                                    } else {
                                        player.sendMessage("and find a doctors' gown, but you don't have room to take it.")
                                    }
                            } else {
                                player.sendMessage("but you find nothing of interest.")
                            }
                        }

                    }
                }
            }
        }
    }

    fun handleDistillatorCageGate(obj: GameObject) {
        if (player.x > 2551) {
            handleDoubleDoor(player, obj)
        } else {
            if (!player.inventory.containsOneItem(KEY)) {
                player.sendMessage("The gate is locked.", "You need a key.")
            } else {
                player.sendMessage("The key fits the gate.")
                handleDoubleDoor(player, obj)
            }
        }
    }

    fun handleDistillatorCrate() {
        player.cutscene {
            player.sendMessage("You search the crate...")
            wait(3)
            if (stage in STAGE_APPLE_IN_CAULDRON..STAGE_FOUND_DISTILLATOR && !gotDistillator) {
                if (player.inventory.containsOneItem(DISTILLATOR)) {
                    player.sendMessage("It's empty.")
                } else {
                    if (player.inventory.hasFreeSlots()) {
                        player.sendMessage("and find Elena's distillator.")
                        player.inventory.addItem(DISTILLATOR)
                        player.questManager.setStage(Quest.BIOHAZARD, STAGE_FOUND_DISTILLATOR)
                    } else {
                        player.sendMessage("and find Elena's distillator, but you don't have room to take it.")
                    }
                }
            } else {
                player.sendMessage("It's empty.")
            }
        }
    }

    fun handleReceivingVials() {
        if (player.inventory.containsOneItem(LIQUID_HONEY)) {
            player.inventory.deleteItem(LIQUID_HONEY, player.inventory.getAmountOf(LIQUID_HONEY))
        }
        if (player.inventory.containsOneItem(ETHENEA)) {
            player.inventory.deleteItem(ETHENEA, player.inventory.getAmountOf(ETHENEA))
        }
        if (player.inventory.containsOneItem(SULPHURIC_BROLINE)) {
            player.inventory.deleteItem(SULPHURIC_BROLINE, player.inventory.getAmountOf(SULPHURIC_BROLINE))
        }
        if (player.inventory.containsOneItem(PLAGUE_SAMPLE)) {
            player.inventory.deleteItem(PLAGUE_SAMPLE, player.inventory.getAmountOf(PLAGUE_SAMPLE))
        }
        player.inventory.addItem(LIQUID_HONEY)
        player.inventory.addItem(ETHENEA)
        player.inventory.addItem(SULPHURIC_BROLINE)
        player.inventory.addItem(PLAGUE_SAMPLE)
        player.sendMessage("Elena gives you three vials and a sample in a tin container.")
        player.questManager.getAttribs(Quest.BIOHAZARD).removeB(LOST_ITEM_TO_GUARD)
        resetAllErrandBoyItems()
    }

    fun resetAllErrandBoyItems() {
        player.questManager.getAttribs(Quest.BIOHAZARD).removeI(GAVE_HOPS_VIAL_OF)
        player.questManager.getAttribs(Quest.BIOHAZARD).removeI(GAVE_DA_VINCI_VIAL_OF)
        player.questManager.getAttribs(Quest.BIOHAZARD).removeI(GAVE_CHANCY_VIAL_OF)
    }

    fun handleTeleportHook() {
        player.controllerManager.addTeleportHook { _, _ ->
            if (stage in STAGE_RECEIVED_VIALS..STAGE_RETURN_TO_ELENA)
                handleBreakingPlagueSample()
            return@addTeleportHook true
        }
    }

    private fun handleBreakingPlagueSample() {
        if (player.inventory.containsOneItem(PLAGUE_SAMPLE)) {
            player.sendMessage("The plague sample is too delicate...it disintegrates in the crossing.")
            player.inventory.deleteItem(PLAGUE_SAMPLE, player.inventory.getAmountOf(PLAGUE_SAMPLE))
        }
        if (player.inventory.containsOneItem(ETHENEA)) {
            player.sendMessage("The ethenea is too delicate...it disintegrates in the crossing.")
            player.inventory.deleteItem(ETHENEA, player.inventory.getAmountOf(ETHENEA))
        }
    }

    fun handleGiveItemToErrandBoy(key: String, item: Int) {
        player.questManager.getAttribs(Quest.BIOHAZARD).setI(key, item)
        player.inventory.deleteItem(item, 1)
        player.sendMessage("You give him the vial of ${Item(item).name.lowercase()}.")
    }

    fun handleGuardRemovingItems() {
        if (player.inventory.containsOneItem(ETHENEA)) {
            player.sendMessage("He takes the vial of ethenea from you.")
            player.inventory.deleteItem(ETHENEA, player.inventory.getAmountOf(ETHENEA))
            player.questManager.getAttribs(Quest.BIOHAZARD).setB(LOST_ITEM_TO_GUARD, true)
            resetAllErrandBoyItems()
        }
        if (player.inventory.containsOneItem(LIQUID_HONEY)) {
            player.sendMessage("He takes the vial of liquid honey from you.")
            player.inventory.deleteItem(LIQUID_HONEY, player.inventory.getAmountOf(LIQUID_HONEY))
            player.questManager.getAttribs(Quest.BIOHAZARD).setB(LOST_ITEM_TO_GUARD, true)
            resetAllErrandBoyItems()
        }
        if (player.inventory.containsOneItem(SULPHURIC_BROLINE)) {
            player.sendMessage("He takes the vial of sulphuric broline from you.")
            player.inventory.deleteItem(SULPHURIC_BROLINE, player.inventory.getAmountOf(SULPHURIC_BROLINE))
            player.questManager.getAttribs(Quest.BIOHAZARD).setB(LOST_ITEM_TO_GUARD, true)
            resetAllErrandBoyItems()
        }
    }

    fun handleCollectItemFromErrandBoy(key: String, item: Int) {
        player.questManager.getAttribs(Quest.BIOHAZARD).setI(key, 2)
        player.inventory.addItem(item)
        player.sendMessage("He gives you the vial of ${Item(item).name.lowercase()}.")
    }

    fun handleResetErrandBoy(key: String) {
        player.questManager.getAttribs(Quest.BIOHAZARD).setI(key, 1)
    }

    fun isInMournerHQ(tile: Tile): Boolean {
        for (range in MOURNER_HQ) if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) return true
        return false
    }
    fun isInMournerHQGarden(tile: Tile): Boolean {
        for (range in MOURNER_HQ_GARDEN) if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) return true
        return false
    }

}
