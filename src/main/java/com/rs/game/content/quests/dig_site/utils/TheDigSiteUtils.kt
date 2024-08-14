package com.rs.game.content.quests.dig_site.utils

import com.rs.engine.cutscenekt.cutscene
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.dig_site.dialogue.npcs.PanningGuideD
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.game.model.`object`.GameObject
import com.rs.lib.game.Animation
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.utils.DropSets
import com.rs.utils.Ticks
import com.rs.utils.drop.DropTable

class TheDigSiteUtils(val player: Player) {

    val stage = player.getQuestStage(Quest.DIG_SITE)

    fun searchBush(obj: GameObject) {
        when (obj.id) {
            WRONG_BUSH -> {
                player.lock(Animation(SEARCH_ANIM).defs.emoteGameTicks)
                player.anim(SEARCH_ANIM)
                player.sendMessage("You search the bush but find nothing of interest.")
            }
            CORRECT_BUSH -> {
                player.lock(Animation(SEARCH_ANIM).defs.emoteGameTicks)
                player.anim(SEARCH_ANIM)
                player.sendMessage("You search the bush...")
                if (stage >= STAGE_BEGIN_EXAM_1 && !player.inventory.containsOneItem(TEDDY) && !player.bank.containsItem(TEDDY) && !player.questManager.getAttribs(Quest.DIG_SITE).getB(PURPLE_STUDENT_EXAM_1_OBTAINED_ANSWER) && !player.isQuestComplete(Quest.DIG_SITE)) {
                    player.startConversation {
                        if (player.inventory.hasFreeSlots()) {
                            player(CONFUSED, "Hey, something has been dropped here...") { player.inventory.addItem(TEDDY) }
                            item(TEDDY, "You find... something.")
                        } else {
                            player(CONFUSED, "Hey, something has been dropped here...")
                            simple("You don't have enough room in your inventory to pick it up.")
                        }
                    }
                } else {
                    player.sendMessage("You find nothing of interest.")
                }
            }
        }
    }

    fun panPoint(obj: GameObject) {
        if (player.questManager.getAttribs(Quest.DIG_SITE).getB(PANNING_GUIDE_GIVEN_TEA) || player.isQuestComplete(Quest.DIG_SITE)) {
            if (player.inventory.containsOneItem(MUD_PANNING_TRAY) || player.inventory.containsOneItem(GOLD_PANNING_TRAY)) {
                player.playerDialogue(SKEPTICAL_THINKING, "I already have a full panning tray. I should try searching that first...")
            } else if (player.inventory.containsOneItem(EMPTY_PANNING_TRAY)) {
                player.actionManager.setAction(PanningAction(obj))
            } else {
                player.itemDialogue(EMPTY_PANNING_TRAY, "You need a panning tray to pan at the panning point.")
            }
        } else {
            val panningGuide: List<NPC> = player.queryNearbyNPCsByTileRange(20) { npc: NPC -> npc.id == PANNING_GUIDE }
            PanningGuideD(player, panningGuide.first()).notAllowedToPan()
            player.faceEntityTile(panningGuide.first())
        }
    }

    fun searchPanningTray(item: Item) {
        val dropSet = DropSets.getDropSet("panning_tray")
        val drops = DropTable.calculateDrops(player, dropSet)

        if (player.inventory.hasFreeSlots()) {
            when (item.id) {
                GOLD_PANNING_TRAY -> {
                    player.inventory.addItem(NUGGETS, 1)
                    player.itemDialogue(NUGGETS, "You find some gold nuggets within the mud.")
                    player.inventory.replace(GOLD_PANNING_TRAY, EMPTY_PANNING_TRAY)
                }

                MUD_PANNING_TRAY -> {
                    if (drops.isEmpty()) {
                        player.simpleDialogue("You find only mud.")
                    } else {
                        val randomDrop = drops.random()
                        var validDrop: Item? = randomDrop

                        if (randomDrop.id == SPECIAL_CUP) {
                            if (player.getQuestStage(Quest.DIG_SITE) != STAGE_BEGIN_EXAM_1) {
                                validDrop = null
                            } else if (player.inventory.containsOneItem(SPECIAL_CUP)) {
                                validDrop = null
                            } else if (player.questManager.getAttribs(Quest.DIG_SITE).getB(BROWN_STUDENT_EXAM_1_OBTAINED_ANSWER)) {
                                validDrop = null
                            }
                        }

                        if (validDrop == null || validDrop.id == -1) {
                            player.simpleDialogue("You find only mud.")
                        } else {
                            player.inventory.addItem(validDrop.id, validDrop.amount)
                            val itemName = when (validDrop.id) {
                                COINS -> "some coins within the mud."
                                UNCUT_OPAL, UNCUT_JADE -> "a gem within the mud!"
                                OYSTER -> "an oyster within the mud."
                                SPECIAL_CUP -> "a shiny cup covered in mud."
                                else -> validDrop.name.lowercase()
                            }
                            player.itemDialogue(validDrop.id, "You find $itemName")
                        }
                    }
                    player.inventory.replace(MUD_PANNING_TRAY, EMPTY_PANNING_TRAY)
                }

                else -> {
                    player.sendMessage("Invalid panning tray item.")
                }
            }
        } else {
            player.simpleDialogue("You need at least 1 free inventory slot to search the panning tray.")
        }
    }

    fun openCertInterface(cert: Int) {
        certToInterfaceMap[cert]?.let { interfaceId ->
            player.packets.sendVarcString(210, player.displayName)
            player.interfaceManager.sendInterface(interfaceId)
        }
    }

    fun handleSpecimenJarCupboard(obj: GameObject, option: String) {
        when (obj.id) {

            SPECIMEN_JAR_CUPBOARD[0] -> {
                player.anim(SEARCH_ANIM)
                obj.setIdTemporary(SPECIMEN_JAR_CUPBOARD[1], Ticks.fromMinutes(1))
            }

            SPECIMEN_JAR_CUPBOARD[1] -> {
                if (option == "Close") {
                    player.anim(SEARCH_ANIM)
                    obj.setId(SPECIMEN_JAR_CUPBOARD[0])
                }
                if (option == "Search") {
                    player.schedule {
                        player.lock()
                        player.sendMessage("You search the cupboard...")
                        wait(3)
                        if (player.inventory.hasFreeSlots()) {
                            player.inventory.addItem(SPECIMEN_JAR)
                            player.sendMessage("You find a specimen jar.")
                        } else {
                            player.sendMessage("You find a specimen jar but your inventory is too full to take it.")
                        }
                        player.unlock()
                    }
                }
            }
        }
    }

    fun handleRockPickCupboard(obj: GameObject, option: String) {
        when (obj.id) {

            ROCK_PICK_CUPBOARD[0] -> {
                player.anim(SEARCH_ANIM)
                obj.setIdTemporary(ROCK_PICK_CUPBOARD[1], Ticks.fromMinutes(1))
            }

            ROCK_PICK_CUPBOARD[1] -> {
                if (option == "Close") {
                    player.anim(SEARCH_ANIM)
                    obj.setId(ROCK_PICK_CUPBOARD[0])
                }
                if (option == "Search") {
                    player.schedule {
                        player.lock()
                        player.sendMessage("You search the cupboard...")
                        wait(3)
                        if (player.inventory.hasFreeSlots()) {
                            player.inventory.addItem(ROCK_PICK)
                            player.sendMessage("You find a rock pick.")
                        } else {
                            player.sendMessage("You find a rock pick but your inventory is too full to take it.")
                        }
                        player.unlock()
                    }
                }
            }
        }
    }

    fun searchSpecimenTray(obj: GameObject) {
        if(stage > STAGE_BEGIN_EXAM_1) {
            if (player.inventory.hasFreeSlots()) {
                if (player.inventory.containsOneItem(SPECIMEN_JAR)) {
                    player.schedule {
                        player.lock()
                        player.sendMessage("You sift through the earth in the tray.", true)
                        player.faceObject(obj)
                        player.anim(DIGGING_ANIM)
                        wait(4)
                        val dropSet = DropSets.getDropSet("specimen_tray")
                        val drops = DropTable.calculateDrops(player, dropSet)
                        val randomDrop = drops.randomOrNull()
                        if (randomDrop == null) {
                            player.sendMessage("You don't find anything.")
                        } else {
                            player.inventory.addItem(randomDrop.id, randomDrop.amount)
                            player.sendMessage("You find something amongst the earth.")
                        }
                        player.unlock()
                    }
                } else {
                    player.sendMessage("You need a specimen jar to be able to search the tray.")
                }
            } else {
                player.sendMessage("You do not have enough inventory space to do that.")
            }
        } else {
            player.sendMessage("You are not qualified to search here yet.")
        }
    }

    fun searchSoil(obj: GameObject) {
        val playerTile = player.tile
        when {
            isInTrainingDig(playerTile) -> {
                if (player.getQuestStage(Quest.DIG_SITE) < STAGE_BEGIN_EXAM_1) {
                    player.sendMessage("You are not qualified to dig here yet.")
                    return
                }
                if (player.inventory.containsOneItem(TROWEL)) {
                    searchSoilAction(obj, "training_dig", DIGGING_ANIM)
                } else {
                    player.sendMessage("You need a trowel to dig here.")
                }
            }
            isInLvl1Dig(playerTile) -> {
                if (player.getQuestStage(Quest.DIG_SITE) < STAGE_BEGIN_EXAM_2) {
                    player.sendMessage("You are not qualified to dig here. You must complete the Earth Sciences Level 1 exam.")
                    return
                }
                if (player.inventory.containsOneItem(TROWEL) && player.equipment.getId(Equipment.HANDS) == LEATHER_GLOVES && player.equipment.getId(Equipment.FEET) == LEATHER_BOOTS) {
                    searchSoilAction(obj, "level_1_dig", DIGGING_ANIM)
                } else {
                    player.sendMessage("You will need to wear leather gloves and boots, and have a trowel, to dig here.")
                }
            }
            isInLvl2Dig(playerTile) -> {
                if (player.getQuestStage(Quest.DIG_SITE) < STAGE_BEGIN_EXAM_3) {
                    player.sendMessage("You are not qualified to dig here. You must complete the Earth Sciences Level 2 exam.")
                    return
                }
                if (player.inventory.containsOneItem(ROCK_PICK)) {
                    searchSoilAction(obj, "level_2_dig", ROCK_PICK_ANIM)
                } else {
                    player.sendMessage("You need a rock pick to dig here.")
                }
            }
            isInLvl3Dig(playerTile) -> {
                if (player.getQuestStage(Quest.DIG_SITE) < STAGE_COMPLETED_EXAMS) {
                    player.sendMessage("You are not qualified to dig here. You must complete the Earth Sciences Level 3 exam.")
                    return
                }
                if (player.inventory.containsOneItem(TROWEL) && player.inventory.containsOneItem(SPECIMEN_BRUSH) && player.inventory.containsOneItem(SPECIMEN_JAR)) {
                    searchSoilAction(obj, "level_3_dig", DIGGING_ANIM)
                } else {
                    player.sendMessage("You need a trowel, a specimen brush and a specimen jar to dig here.")
                }
            }
            isInPrivateDig(playerTile) -> player.sendMessage("This is a private dig area - you cannot dig here!")
        }
    }

    private fun searchSoilAction(obj: GameObject, dropSetName: String, anim: Int) {
        if (player.inventory.hasFreeSlots()) {
            player.schedule {
                player.lock()
                player.sendMessage("You dig through the earth...", true)
                player.faceObject(obj)
                player.anim(anim)
                wait(4)
                val dropSet = DropSets.getDropSet(dropSetName)
                val drops = DropTable.calculateDrops(player, dropSet)
                var validDrop: Item? = drops.randomOrNull()

                if (validDrop?.id == ANCIENT_TALISMAN && player.inventory.containsOneItem(ANCIENT_TALISMAN)) {
                    validDrop = null
                } else if (validDrop?.id == ANCIENT_TALISMAN && player.getQuestStage(Quest.DIG_SITE) > STAGE_COMPLETED_EXAMS) {
                    validDrop = null
                }

                if (validDrop == null) {
                    player.sendMessage("You don't find anything.")
                } else {
                    player.inventory.addItem(validDrop.id, validDrop.amount)
                    if (validDrop.id == ANCIENT_TALISMAN) {
                        player.simpleDialogue("You find a strange talisman.")
                    }
                    if (isInLvl3Dig(player.tile)) player.sendMessage("You carefully clean your find with the specimen brush.") else player.sendMessage("You find something in the earth.")
                }
                player.unlock()
            }
        } else {
            player.sendMessage("You do not have enough inventory space to do that.")
        }
    }

    private fun isInTrainingDig(tile: Tile): Boolean {
        return isInDig(tile, TRAINING_DIG)
    }

    private fun isInLvl1Dig(tile: Tile): Boolean {
        return isInDig(tile, LVL_1_DIG)
    }

    private fun isInLvl2Dig(tile: Tile): Boolean {
        return isInDig(tile, LVL_2_DIG)
    }

    private fun isInLvl3Dig(tile: Tile): Boolean {
        return isInDig(tile, LVL_3_DIG)
    }

    private fun isInPrivateDig(tile: Tile): Boolean {
        return isInDig(tile, PRIVATE_DIG)
    }

    private fun isInDig(tile: Tile, ranges: Array<IntArray>): Boolean {
        for (range in ranges) {
            if (tile.x() >= range[0] && tile.x() <= range[1] && tile.y() >= range[2] && tile.y() <= range[3]) {
                return true
            }
        }
        return false
    }

    fun useAltarWinch() {
        handleWinch(ALTAR_DUNGEON_PRE_EXPLOSION, ALTAR_DUNGEON_POST_EXPLOSION, ALTAR_DUNGEON_POST_QUEST, ATTACHED_ROPE_ALTAR_WINCH)
    }

    fun useDougWinch() {
        handleWinch(DOUG_DUNGEON_PRE_EXPLOSION, DOUG_DUNGEON_POST_EXPLOSION, DOUG_DUNGEON_POST_QUEST, ATTACHED_ROPE_DOUG_WINCH)
    }

    private fun handleWinch(preExplosionDest: Tile, postExplosionDest: Tile, postQuestDest: Tile, attachedRope: String) {
        when {
            stage == STAGE_BLOWN_UP_BRICKS -> descendIntoCavern(postExplosionDest)
            stage == STAGE_COMPLETE -> descendIntoCavern(postQuestDest)
            stage >= STAGE_PERMISSION_GRANTED -> {
                if (player.questManager.getAttribs(Quest.DIG_SITE).getB(attachedRope)) {
                    descendIntoCavern(preExplosionDest)
                } else {
                    operateWinchWithoutRope()
                }
            }
            else -> {
                player.startConversation {
                    npc(DIGSITE_WORKMEN[0], CALM_TALK, "Sorry; this area is private. The only way you'll get to use these is by impressing the archaeological expert up at the Exam Centre.")
                    npc(DIGSITE_WORKMEN[0], CALM_TALK, "Find something worthwhile and he might let you use the winches. Until then, get lost!")
                }
            }
        }
    }

    private fun descendIntoCavern(destination: Tile) {
        player.schedule {
            player.lock()
            player.sendMessage("You try to climb down the rope.")
            wait(1)
            player.sendMessage("You lower yourself into the shaft.")
            player.anim(SEARCH_ANIM)
            wait(2)
            player.tele(destination)
            wait(1)
            player.sendMessage("You find yourself in a cavern...")
            player.unlock()
        }
    }

    private fun operateWinchWithoutRope() {
        player.schedule {
            player.lock()
            player.sendMessage("You operate the winch.")
            wait(2)
            player.playerDialogue(SKEPTICAL_THINKING, "Hey, I think I could fit down here. I need something to help me get all the way down.")
            player.sendMessage("The bucket descends, but does not reach the bottom.")
            player.unlock()
        }
    }

    fun handleChest(obj: GameObject, option: String) {
        when (obj.id) {

            CHEST[1] -> {
                if (player.inventory.containsOneItem(CHEST_KEY) && !player.questManager.getAttribs(Quest.DIG_SITE).getB(UNLOCKED_CHEST)) {
                    player.anim(SEARCH_ANIM)
                    obj.setIdTemporary(CHEST[0], Ticks.fromSeconds(5))
                    player.sendMessage("You use the key in the chest.")
                    player.inventory.deleteItem(CHEST_KEY, 1)
                    player.questManager.getAttribs(Quest.DIG_SITE).setB(UNLOCKED_CHEST, true)
                } else if (player.questManager.getAttribs(Quest.DIG_SITE).getB(UNLOCKED_CHEST)) {
                    player.anim(SEARCH_ANIM)
                    obj.setIdTemporary(CHEST[0], Ticks.fromSeconds(5))
                    player.sendMessage("You open the chest.")
                } else {
                    player.sendMessage("The chest is locked.")
                }
            }

            CHEST[0] -> {
                if (option == "Close") {
                    player.anim(SEARCH_ANIM)
                    obj.setId(CHEST[1])
                }
                if (option == "Search") {
                    player.schedule {
                        if (stage >= STAGE_SPEAK_TO_DOUG && !player.isQuestComplete(Quest.DIG_SITE)) {
                            if (player.inventory.containsOneItem(CHEMICAL_POWDER) || player.inventory.containsOneItem(AMMONIUM_NITRATE))
                                player.sendMessage("You find nothing of interest.")
                            else
                                if (player.inventory.hasFreeSlots()) {
                                    player.inventory.addItem(CHEMICAL_POWDER)
                                    player.itemDialogue(CHEMICAL_POWDER, "You find some unusual powder inside...")
                                } else {
                                    player.itemDialogue(CHEMICAL_POWDER, "You find unusual powder inside but your inventory is too full to take it.")
                                }
                        } else {
                            player.sendMessage("You find nothing of interest.")
                        }
                    }
                }
            }
        }
    }

    fun openAndSearchBarrel(option: String) {
        if (option == "Open") {
            if (player.getQuestStage(Quest.DIG_SITE) > STAGE_PERMISSION_GRANTED) {
                player.playerDialogue(CALM_TALK, "The lid is shut tight; I'll have to find something to lever it off.")
            } else {
                player.playerDialogue(CALM_TALK, "I'm not sure why I'd want to open this potentially dangerous looking barrel.")
            }
        }
        if (option == "Search") {
            if (player.vars.getVarBit(BARREL_VB) == 1) {
                player.playerDialogue(SKEPTICAL_THINKING, "I'll need a container to collect some. It looks and smells rather dangerous, so it'll need to be something small and capable of containing dangerous chemicals.")
            } else {
                player.playerDialogue(CALM_TALK, "The lid is shut tight; I'll have to find something to lever it off.")
            }
        }
    }

    fun collectLiquid() {
        player.startConversation {
            item(UNIDENTIFIED_LIQUID, "You fill the vial with the liquid.") {
                player.inventory.deleteItem(VIAL, 1)
                player.inventory.addItem(UNIDENTIFIED_LIQUID)
            }
            player(CALM_TALK, "I'm not sure what this stuff is. I had better be VERY careful with it; I had better not drop it either...")
            player.sendMessage("You put the lid back on the barrel just in case it's dangerous.")
            player.vars.saveVarBit(BARREL_VB, 0)
        }
    }

    fun dropLiquid(item: Item, dmgAmt: Int) {
        if (player.hitpoints == 1) {
            player.forceTalk("Ow! That really hurt!")
        } else {
            if (item.id == NITROGLYCERIN) {
                player.forceTalk("Ow! The nitroglycerin exploded!")
            } else {
                player.forceTalk("Ow! The liquid exploded!")
            }
            player.sendMessage("You were injured by the burning liquid.")
            val actualDmgAmt = if (player.hitpoints > dmgAmt) dmgAmt else player.hitpoints - 1
            player.applyHit(Hit(player, actualDmgAmt, Hit.HitLook.TRUE_DAMAGE))
        }

        player.inventory.deleteItem(item.slot, item)
    }

    fun searchBrick() {
        when (stage) {
            STAGE_PERMISSION_GRANTED -> {
                player.playerDialogue(SKEPTICAL_THINKING, "Hmmm, there's a room past these bricks. If I could move them out of the way then I could find out what's inside. Maybe there's someone around here who can help...")
                player.setQuestStage(Quest.DIG_SITE, STAGE_SPEAK_TO_DOUG)
            }
            STAGE_SPEAK_TO_DOUG -> player.playerDialogue(SKEPTICAL_THINKING, "Hmmm, there's a room past these bricks. If I could move them out of the way then I could find out what's inside. Maybe there's someone around here who can help...")
            STAGE_COVERED_IN_COMPOUND -> player.playerDialogue(SKEPTICAL_THINKING, "The brick is covered with the chemicals I made.")
        }
    }

    fun brickExplosionCutscene() {
        player.cutscene {
            player.anim(BEND_DOWN_ANIM)
            player.sendMessage("You strike the tinderbox...")
            wait(3)
            player.sendMessage("Fizz...")
            dialogue { player(WORRIED, "Woah! This is going to blow! I'd better run!") }
            waitForDialogue()
            player.lock()
            if (player.runEnergy < 6.0)
                player.restoreRunEnergy(5.0)
            entityRunTo(player, 3378, 9828)
            wait(2)
            entityRunTo(player, 3374, 9828)
            wait(2)
            entityRunTo(player, 3367, 9830)
            wait(2)
            camShake(0, 20, 8, 128, 40)
            wait(4)
            camShakeReset()
            player.unlock()
            returnPlayerFromInstance()
            stop()
            player.setQuestStage(Quest.DIG_SITE, STAGE_BLOWN_UP_BRICKS)
            player.tele(3367, 9766, 0)
            dialogue { player(CALM_TALK, "Wow, that was a big explosion! What's that noise I can hear? Sounds like bones moving or something...") }
        }
    }

    fun takeStoneTablet() {
        if (player.inventory.hasFreeSlots()) {
            player.vars.setVarBit(TABLET_VB, 1)
            player.inventory.addItem(STONE_TABLET)
            player.sendMessage("You pick the stone tablet up.")
        } else {
            player.sendMessage("You do not have enough inventory space to take the stone tablet.")
        }
    }

    fun searchSacks() {
        player.schedule {
            player.lock()
            player.sendMessage("You search the sack...")
            wait(3)
            if (player.inventory.hasFreeSlots()) {
                player.inventory.addItem(SPECIMEN_JAR)
                player.sendMessage("You find a specimen jar.")
            } else {
                player.sendMessage("You find a specimen jar but your inventory is too full to take it.")
            }
            player.unlock()
        }
    }

    fun searchBookcase(obj: GameObject) {
        player.schedule {
            player.lock()
            player.sendMessage("You search the bookcase...")
            wait(3)
            if (obj.id == BOOKCASES[0])
                if (!player.inventory.containsOneItem(BOOK_ON_CHEMICALS)) {
                    if (player.inventory.hasFreeSlots()) {
                        player.inventory.addItem(BOOK_ON_CHEMICALS)
                        player.sendMessage("You find a book on chemicals.")
                    } else {
                        player.sendMessage("You find a book on chemicals but your inventory is too full to take it.")
                    }
                } else {
                    player.sendMessage("You find nothing of interest.")
                }
            else player.sendMessage("You find nothing of interest.")
            player.unlock()
        }
    }
}
