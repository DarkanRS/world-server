package com.rs.game.content.quests.naturespirit

import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Tile
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

const val STAGE_UNSTARTED = 0
const val STAGE_FIND_FILLIMAN = 1
const val STAGE_PROVE_GHOST = 2
const val STAGE_GAVE_JOURNAL = 3
const val STAGE_GET_BLESSED = 4

const val FILLIMAN = 1050

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
    completedStage = 7
)
class NatureSpirit : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("I should speak to Drezel under the Saradomin temple!")
        STAGE_FIND_FILLIMAN -> listOf("I need to look for Filliman Tarlock in the Swamps of Mort Myre. I should be wary of the Ghasts.")
        STAGE_PROVE_GHOST -> listOf("I think I need to convince this poor fellow Tarlock that he's actually dead!")
        STAGE_GAVE_JOURNAL -> listOf("Filliman might need my help with his plan.")
        STAGE_GET_BLESSED -> listOf("Filliman gave me a 'bloom' spell but I need to be blessed at the mausoleum near the temple before I can cast it. I am supposed to collect 'something from nature'.")
        5 -> listOf()
        6 -> listOf()
        7 -> listOf("QUEST COMPLETE!")
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

            "Enter" -> when(player.getQuestStage(Quest.NATURE_SPIRIT)) {
                STAGE_FIND_FILLIMAN, STAGE_PROVE_GHOST, STAGE_GAVE_JOURNAL, STAGE_GET_BLESSED -> { fillimanDialogue(player) }
                else -> player.sendMessage("Nothing interesting happens.")
            }
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

    onNpcClick(1050) { (player) -> fillimanDialogue(player) }
    instantiateNpc(1050) { id, tile -> Filliman(id, tile) }

    onObjectClick(3527, 3528, 3529) { e ->
        e.player.simpleDialogue("You search the stone and fine that it has some sort of ${when(e.objectId) { 3527 -> "nature" 3528 -> "faith" else -> "spirit" }} symbol scratched into it.")
    }

    onItemOnObject(arrayOf(3521), arrayOf(2961)) { e ->
        e.player.anim(9104)
        e.item.id = 2963
        e.player.inventory.refresh(e.item.slot)
        e.player.itemDialogue(2963, "You dip the sickle into the grotto water and bless it.")
    }
}

class Filliman(id: Int, tile: Tile) : NPC(id, tile) {
    override fun withinDistance(player: Player, distance: Int) =
        player.getQuestStage(Quest.NATURE_SPIRIT) in STAGE_FIND_FILLIMAN..STAGE_GET_BLESSED && super.withinDistance(tile, distance)
}