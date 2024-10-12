package com.rs.game.content.quests.eadgars_ruse.utils

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.content.quests.eadgars_ruse.instances.npcs.GoutweedCrateGuard
import com.rs.game.content.quests.eadgars_ruse.instances.npcs.TrollThistle
import com.rs.game.content.world.doors.Doors
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Item
import com.rs.utils.Ticks
import kotlin.random.Random

class EadgarsRuseUtils(val player: Player) {

    private val stage = player.getQuestStage(Quest.EADGARS_RUSE)
    private val learntAboutParrot = player.questManager.getAttribs(Quest.EADGARS_RUSE).getB(LEARNED_ABOUT_VODKA) && player.questManager.getAttribs(Quest.EADGARS_RUSE).getB(LEARNED_ABOUT_PINEAPPLE)

    fun handleAlcoChunks() {
        if (stage >= STAGE_GET_PARROT && learntAboutParrot) {
            player.inventory.removeItems(Item(VODKA), Item(PINEAPPLE_CHUNKS))
            player.inventory.addItemDrop(ALCOCHUNKS, 1)
        } else { player.sendMessage("Nothing interesting happens.") }
    }

    fun captureParrot() {
        val parrotyPete: NPC? = World.getNPCsInChunkRange(player.chunkId, 2).firstOrNull { it.id == PARROTY_PETE }
        player.faceDir(Direction.EAST)
        if (stage in STAGE_GET_PARROT..STAGE_NEED_TO_HIDE_PARROT && learntAboutParrot) {
            if (player.containsOneItem(DRUNK_PARROT) || player.bank.containsItem(DRUNK_PARROT)) {
                player.sendMessage("You already have a parrot.")
                return
            }
            player.cutscene {
                dialogue {
                    item(DRUNK_PARROT, "You manage to attract a parrot and catch it.") {
                        player.inventory.replace(ALCOCHUNKS, DRUNK_PARROT)
                    }
                    player(HAPPY_TALKING, "Hah! Got you now!")
                    npc(PARROT, T_CALM_TALK, "Sqwaawk...*hic*")
                    npc(PARROTY_PETE, FRUSTRATED, "Hey! What are you doing with that parrot?") { player.faceEntityTile(parrotyPete) }
                    player(CALM_TALK, "Nothing.")
                    npc(PARROTY_PETE, WORRIED, "It looks drunk! You should NEVER feed alcohol to a parrot!") { player.faceEntityTile(parrotyPete) }
                    player(CALM_TALK, "Well, good thing I found it!<br>I'll just take it to the vet for you, shall I?")
                    npc(PARROTY_PETE, HAPPY_TALKING, "Oh, thank you! We're ever so busy here at the Zoo.") { player.faceEntityTile(parrotyPete) }
                }
                waitForDialogue()
            }
        } else { player.sendMessage("Nothing interesting happens.") }
    }

    fun hideParrot() {
        if (stage == STAGE_NEED_TO_HIDE_PARROT) {
            player.sendMessage("You hide the parrot under the torture rack.")
            player.inventory.deleteItem(DRUNK_PARROT, 1)
            player.setQuestStage(Quest.EADGARS_RUSE, STAGE_HIDDEN_PARROT)
        } else { player.sendMessage("I don't know why I would want to do that.") }
    }

    fun handInItems(itemId: Int, amount: Int) {
        if (amount > 0) {
            player.inventory.deleteItem(itemId, amount)
            player.questManager.getAttribs(Quest.EADGARS_RUSE).setI(itemId.toString(), amount)
        }
    }

    fun searchRack() {
        when (stage) {
            STAGE_NEED_TO_HIDE_PARROT -> {
                player.sendMessage("I could possibly hide ${if (player.containsOneItem(DRUNK_PARROT)) "the parrot" else "something"} under here.")
            }
            in STAGE_HIDDEN_PARROT..STAGE_NEED_TROLL_POTION -> {
                player.startConversation {
                    simple("You look under the rack and find the drunk parrot.")
                    npc(PARROT, T_CALM_TALK, "Who's a pretty boy then?")
                    player(CALM_TALK, "I don't think it's done yet.")
                }
            }
            STAGE_FETCH_PARROT -> {
                if (player.inventory.hasFreeSlots()) {
                    player.startConversation {
                        simple("You look under the rack and find the drunk parrot.")
                        npc(PARROT, T_CALM_TALK, "Ah, hello Sir. Could you please free me?<br>I seem to have ... OW! What are you doing?<br>That's my spleen!")
                        player(CALM_TALK, "I think it's probably heard enough.") {
                            player.anim(PICKUP_ANIM)
                            player.inventory.addItem(DRUNK_PARROT)
                            player.setQuestStage(Quest.EADGARS_RUSE, STAGE_RETRIEVED_PARROT)
                        }
                    }
                } else { player.simpleDialogue("You need at least 1 free inventory slot to do that.") }
            }
            else -> player.sendMessage("Nothing interesting happens.")
        }
    }

    fun pickThistle(npc: NPC) {
        if (stage >= STAGE_NEED_TROLL_POTION) {
            if (npc is TrollThistle) {
                if (player.inventory.hasFreeSlots()) {
                    player.inventory.addItem(TROLL_THISTLE)
                    player.sendMessage("You pick the Troll Thistle.")
                    npc.transformIntoNPC(1957)
                    npc.sendDeath(player)
                } else {
                    player.sendMessage("You don't have enough room to pick the Troll Thistle.")
                }
            }
        } else { player.sendMessage("I don't know why I would want to do that.") }
    }

    fun dryThistle() {
        if (stage >= STAGE_NEED_TROLL_POTION) {
            player.anim(DRY_THISTLE_ANIM)
            player.inventory.replace(TROLL_THISTLE, DRIED_THISTLE)
            player.sendMessage("You dry the troll thistle over the fire.")
        } else { player.sendMessage("Nothing interesting happens.") }
    }

    fun handleKitchenDrawers(obj: GameObject, option: String) {
        when (obj.id) {
            KITCHEN_DRAWERS[0] -> {
                player.anim(OPEN_DRAWERS_ANIM)
                obj.setIdTemporary(KITCHEN_DRAWERS[1], Ticks.fromMinutes(1))
            }

            KITCHEN_DRAWERS[1] -> {
                if (option == "Close") {
                    player.anim(OPEN_DRAWERS_ANIM)
                    obj.setId(KITCHEN_DRAWERS[0])
                }
                if (option == "Search") {
                    player.cutscene {
                        player.sendMessage("You search the drawers...")
                        wait(4)
                        if (stage != STAGE_DISCOVERED_KEY_LOCATION) {
                            player.sendMessage("You don't find anything.")
                        } else if (player.inventory.hasFreeSlots()) {
                            if (player.inventory.containsOneItem(STOREROOM_KEY)) {
                                player.sendMessage("You don't find anything.")
                            } else {
                                player.sendMessage("You open the fake bottom of the drawer and find the storeroom key.")
                                player.inventory.addItem(STOREROOM_KEY)
                            }
                        } else {
                            player.sendMessage("You open the fake bottom of the drawer and find the storeroom key but you don't have enough room to take it.")
                        }
                    }
                }
            }
        }
    }

    fun handleStoreroomDoor(obj: GameObject) {
        when {
            stage == STAGE_DISCOVERED_KEY_LOCATION && player.inventory.containsOneItem(STOREROOM_KEY) -> {
                player.setQuestStage(Quest.EADGARS_RUSE, STAGE_UNLOCKED_STOREROOM)
                player.inventory.deleteItem(STOREROOM_KEY, 1)
                Doors.handleDoor(player, obj)
                player.sendMessage("You unlock the door.")
            }
            stage >= STAGE_UNLOCKED_STOREROOM -> {
                Doors.handleDoor(player, obj)
            }
            else -> {
                player.sendMessage("The storeroom door is locked.")
            }
        }
    }

    fun handleGoutweedCrate() {
        if (player.getQuestStage(Quest.EADGARS_RUSE) >= STAGE_UNLOCKED_STOREROOM) {
            val guard = World.getNPCsInChunkRange(player.chunkId, 1).firstOrNull { it.id == GOUTWEED_GUARD }
            if (guard != null) {
                if (player.inventory.hasFreeSlots()) {
                    player.cutscene {
                        dialogue { item(GOUTWEED, "You've found some goutweed!") { player.inventory.addItem(GOUTWEED) } }
                        waitForDialogue()
                        guard.schedule {
                            guard.stopAll()
                            guard.anim(GOUTWEED_CRATE_GUARD_ANIM)
                            CombatScript.delayHit(guard, 2, player, Hit.range(guard, Random.nextInt(1, 61)))
                            guard.faceEntityTile(player)
                            guard.forceTalk("Hm?")
                            guard.spotAnim(-1)
                            if (guard is GoutweedCrateGuard) {
                                guard.caughtPlayer = true
                                player.schedule {
                                    wait(2)
                                    player.anim(GOUTWEED_CAUGHT_ANIM)
                                    wait(1)
                                }
                                wait(1)
                                player.lock()
                                player.fadeScreen {
                                    player.spotAnim(STUNNED_BIRDS, 5, 100)
                                    player.tele(OUTSIDE_STOREROOM_TILE)
                                    guard.reset()
                                    player.schedule {
                                        wait(2)
                                        player.unlock()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    player.sendMessage("You don't have enough inventory space to take the goutweed.")
                }
            }
        } else {
            player.sendMessage("You have no reason to do that.")
        }
    }

}
