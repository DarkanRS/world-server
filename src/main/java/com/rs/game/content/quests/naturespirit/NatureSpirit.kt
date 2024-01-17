package com.rs.game.content.quests.naturespirit

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.World
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.tasks.Task
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*
import com.rs.utils.Ticks

const val STAGE_UNSTARTED = 0
const val STAGE_FIND_FILLIMAN = 1
const val STAGE_PROVE_GHOST = 2
const val STAGE_GAVE_JOURNAL = 3
const val STAGE_GET_BLESSED = 4
const val STAGE_CAST_BLOOM = 5
const val STAGE_PUZZLING_IT_OUT = 6
const val STAGE_MEET_IN_GROTTO = 7
const val STAGE_BRING_SICKLE = 8
const val STAGE_KILL_GHASTS = 9
const val STAGE_COMPLETE = 10

const val FILLIMAN = 1050
const val NATURE_SPIRIT = 1051

@QuestHandler(
    quest = Quest.NATURE_SPIRIT,
    startText = "Speak to Drezel under the Saradomin temple near the River Salve.",
    itemsText = "Silver sickle, Ghostspeak amulet",
    combatText = "You will have to kill a few level 30 ghasts.",
    rewardsText =
            "3,000 Crafting XP<br>" +
            "2,000 Constitution XP<br>" +
            "2,000 Defence XP<br>" +
            "Access to Mort Myre swamp and the Altar of Nature<br>" +
            "30 Prayer XP each time you kill a ghast",
    completedStage = STAGE_COMPLETE
)
class NatureSpirit : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("I should speak to Drezel under the Saradomin temple!")
        STAGE_FIND_FILLIMAN -> listOf("I need to look for Filliman Tarlock in the Swamps of Mort Myre. I should be wary of the Ghasts.")
        STAGE_PROVE_GHOST -> listOf("I think I need to convince this poor fellow Tarlock that he's actually dead!")
        STAGE_GAVE_JOURNAL -> listOf("Filliman might need my help with his plan.")
        STAGE_GET_BLESSED -> listOf("Filliman gave me a 'bloom' spell but I need to be blessed at the mausoleum near the temple before I can cast it. I am supposed to collect 'something from nature'.")
        STAGE_CAST_BLOOM -> listOf("I need to cast the bloom spell in the swamp to find something from nature.")
        STAGE_PUZZLING_IT_OUT -> listOf("I have collected a mort myre fungus from the swamp which seems to be the part from nature. I just need to find 'something with faith' and 'something of the spirit-to-become freely given'.")
        STAGE_MEET_IN_GROTTO -> listOf("Filliman asked me to meet him in his grotto.")
        STAGE_BRING_SICKLE -> listOf("Filliman has turned into a nature spirit. It was an impressive transformation! Filliman has asked me to bring him a silver sickle.")
        STAGE_KILL_GHASTS -> listOf("I need to use the sickle to make the swamp bloom, add the items to the druid pouch, and use it to defeat 3 ghasts!")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.CRAFTING, 3000.0)
        player.skills.addXpQuest(Skills.HITPOINTS, 2000.0)
        player.skills.addXpQuest(Skills.DEFENSE, 2000.0)
        sendQuestCompleteInterface(player, 2963)
    }
}

@ServerStartupEvent
fun mapNatureSpirit() {
    onObjectClick(3517) { (player, _, op) ->
        when (op) {
            "Search" -> if (player.getQuestStage(Quest.NATURE_SPIRIT) in STAGE_FIND_FILLIMAN..STAGE_PROVE_GHOST && !player.containsOneItem(2967)) {
                player.itemDialogue(2967, "You search the tree. You find a knot and inside of it you discover a small tome. The words on the front are a bit vague, but you make out the words 'Tarlock' and 'journal'.")
                player.inventory.addItem(2967)
            }

            "Enter" -> if (player.getQuestStage(Quest.NATURE_SPIRIT) in STAGE_FIND_FILLIMAN..STAGE_PUZZLING_IT_OUT)
                    fillimanDialogue(player, World.getNPCsInChunkRange(player.chunkId, 1).first { it.id == FILLIMAN })
                else if (player.getQuestStage(Quest.NATURE_SPIRIT) >= STAGE_MEET_IN_GROTTO)
                    player.useLadder(Tile.of(2271, 5334, if (player.isQuestComplete(Quest.NATURE_SPIRIT)) 1 else 0))
                else
                    player.sendMessage("Nothing interesting happens.")
        }
    }

    onObjectClick(3525, 3526) { (player) ->
        player.useLadder(Tile.of(3440, 3337, 0))
    }

    onObjectClick(3521) { (player) ->
        val maxPrayer = player.skills.getLevelForXp(Constants.PRAYER) * 10.0
        if (player.prayer.points < maxPrayer) {
            player.anim(645)
            player.prayer.restorePrayer(maxPrayer)
        }
    }

    onItemClick(2967, options = arrayOf("Read")) { (player) ->
        player.startConversation {
            item(2967, "Most of the writing is pretty uninteresting, but something inside refers to a nature spirit. The requirements for which are,")
            item(2967, "'Something from nature', 'something with faith' and 'something of the spirit-to-become freely given'. It's all pretty vague.")
        }
    }

    onPickupItem(2966, tiles = arrayOf(Tile.of(3437, 3337, 0))) { e ->
        e.cancelPickup()
        if (e.player.getQuestStage(Quest.NATURE_SPIRIT) != STAGE_PROVE_GHOST) {
            e.player.playerDialogue(HeadE.CONFUSED, "I don't think I have much use for a mirror right now.")
            return@onPickupItem
        }
        if (!e.player.inventory.containsItem(2966))
            e.player.inventory.addItemDrop(2966, 1)
    }

    onNpcClick(FILLIMAN, NATURE_SPIRIT) { (player, npc) -> fillimanDialogue(player, npc) }
    instantiateNpc(FILLIMAN) { id, tile -> Filliman(id, tile) }
    instantiateNpc(NATURE_SPIRIT) { id, tile -> NatureSpiritNpc(id, tile) }

    onObjectClick(3527, 3528, 3529) { e ->
        e.player.simpleDialogue("You search the stone and fine that it has some sort of ${when(e.objectId) { 3527 -> "nature" 3528 -> "faith" else -> "spirit" }} symbol scratched into it.")
    }

    onItemOnObject(arrayOf(3521), arrayOf(2961)) { e ->
        e.player.anim(9104)
        e.item.id = 2963
        e.player.inventory.refresh(e.item.slot)
        e.player.itemDialogue(2963, "You dip the sickle into the grotto water and bless it.")
    }

    onItemOnObject(arrayOf(3527, 3528, 3529)) { e ->
        e.player.anim(827)
        when(e.objectId) {
            3527 -> if (e.item.id == 2970) {
                e.player.inventory.deleteItem(e.item)
                World.addGroundItem(e.item, e.getObject().tile, e.player, true, Ticks.fromMinutes(10))
                e.player.questManager.getAttribs(Quest.NATURE_SPIRIT).setB("placedFungus", true)
                e.player.npcDialogue(FILLIMAN, HeadE.CHEERFUL, "Great! That seems correct.")
                return@onItemOnObject
            }
            else -> if (e.item.id == 2969) {
                e.player.inventory.deleteItem(e.item)
                World.addGroundItem(e.item, e.getObject().tile, e.player, true, Ticks.fromMinutes(10))
                e.player.questManager.getAttribs(Quest.NATURE_SPIRIT).setB("placedSpell", true)
                e.player.npcDialogue(FILLIMAN, HeadE.CONFUSED, "Great! That seems correct.")
                return@onItemOnObject
            }
        }
        e.player.npcDialogue(FILLIMAN, HeadE.CONFUSED, "Hmm, that doesn't seem quite right.")
    }

    //Bless silver sickle on grotto/altar of nature
    onItemOnObject(objectNamesOrIds = arrayOf(3520, 3521), itemNamesOrIds = arrayOf(2961)) { e ->
        if (e.player.getQuestStage(Quest.NATURE_SPIRIT) < STAGE_KILL_GHASTS) {
            e.player.sendMessage("You haven't been given permission to use the grotto yet.")
            return@onItemOnObject
        }
        e.item.id = 2963
        e.player.inventory.refresh(e.item.slot)
    }

    onNpcDeath("Ghast") { e ->
        val killer = e.killer
        if (killer is Player) {
            killer.questManager.getAttribs(Quest.NATURE_SPIRIT).incI("ghastsKilled")
            val leftToKill = 3 - killer.questManager.getAttribs(Quest.NATURE_SPIRIT).getI("ghastsKilled")
            if (leftToKill > 0)
                killer.sendMessage("That's a ghast down. $leftToKill more to go!")
            else if (leftToKill == 0)
                killer.sendMessage("That's all 3 ghasts! I better go tell Filliman.")
        }
    }
}

class Filliman(id: Int, tile: Tile) : NPC(id, tile) {
    override fun withinDistance(player: Player, distance: Int): Boolean {
        val stage = player.getQuestStage(Quest.NATURE_SPIRIT)
        if (stage in STAGE_FIND_FILLIMAN..STAGE_PUZZLING_IT_OUT)
            return super.withinDistance(player, distance)
        if (stage == STAGE_MEET_IN_GROTTO)
            return super.withinDistance(player, distance) && setOf(580251, 580250, 582298, 582299).contains(player.chunkId)
        return false
    }
}

class NatureSpiritNpc(id: Int, tile: Tile) : NPC(id, tile) {
    override fun withinDistance(player: Player, distance: Int) = player.getQuestStage(Quest.NATURE_SPIRIT) >= STAGE_BRING_SICKLE && super.withinDistance(tile, distance)
}