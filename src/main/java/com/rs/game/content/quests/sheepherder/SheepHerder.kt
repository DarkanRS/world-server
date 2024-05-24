package com.rs.game.content.quests.sheepherder

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.quests.sheepherder.dialogues.*
import com.rs.game.content.quests.sheepherder.utils.*
import com.rs.game.content.world.doors.Doors.handleGate
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.SHEEP_HERDER,
    startText = "Speak to Councillor Halgrive outside of the Ardougne church.",
    itemsText = "100 coins.",
    combatText = "None.",
    rewardsText = "3100 coins<br>" +
            "Plague jacket and trousers<br>" +
            "Cattleprod",
    completedStage = STAGE_COMPLETE
)

class SheepHerder : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("To start this quest, I can speak to Councillor Halgrive outside of the Ardougne church.")
        STAGE_RECEIVED_SHEEP_FEED -> listOf("Councillor Halgrive told me that four sheep recently escaped from a farm near the city.",
            "When they were found, it was noted that they were strangely discoloured. The mourners examined them and found they had been infected with the plague.",
            "The Councillor asked me if I would herd them into an enclosure, kill them quickly and dispose of their remains in a special incinerator.",
            "He has given me some sheep feed which is poisoned and should finish the sheep off quickly if I feed it to them.",
            "The Councillor also suggested that I should be able to get some protective gear from Doctor Orbon in the Ardougne chapel.")
        STAGE_RECEIVED_PROTECTIVE_CLOTHING -> listOf("Doctor Orbon gave me some protective clothing, in exchange for 100 coins.",
            "I should now be able to go kill the infected sheep and incinerate their bones, without fear of catching the plague.",
            "I still need to incinerate the bones of these sheep:",
            Utils.strikeThroughIf("Red sheep") { player.questManager.getAttribs(Quest.SHEEP_HERDER).getB("RED_SHEEP_BONES") },
            Utils.strikeThroughIf("Green sheep") { player.questManager.getAttribs(Quest.SHEEP_HERDER).getB("GREEN_SHEEP_BONES") },
            Utils.strikeThroughIf("Blue sheep") { player.questManager.getAttribs(Quest.SHEEP_HERDER).getB("BLUE_SHEEP_BONES") },
            Utils.strikeThroughIf("Yellow sheep") { player.questManager.getAttribs(Quest.SHEEP_HERDER).getB("YELLOW_SHEEP_BONES") })
        STAGE_SHEEP_INCINERATED -> listOf("I have hygienically incinerated the bones of all four sheep. I should return to Councillor Halgrive and let him know the good news!")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }
    override fun complete(player: Player) {
        sendQuestCompleteInterface(player, BALL_OF_WOOL)
        player.inventory.addCoins(3100)
    }
}

@ServerStartupEvent
fun handleSheepHerderInteractions() {
    onItemEquip(PLAGUE_JACKET, PLAGUE_TROUSERS) { e ->
        e.apply {
            if (e.dequip() && player.tile.withinArea(2595, 3351, 2609, 3364)) {
                player.startConversation { player(WORRIED, "I should probably keep this protective clothing on, whilst in this enclosure.") }
                cancel()
            }
        }
    }
    onObjectClick(NORTH_GATE, SOUTH_GATE) { (player, obj) ->
        if (player.equipment.getId(Equipment.LEGS) != PLAGUE_TROUSERS || player.equipment.getId(Equipment.CHEST) != PLAGUE_JACKET) {
            player.startConversation { player(WORRIED, "It doesn't look very safe in there. I'm not going in without decent protective clothing.") }
            return@onObjectClick
        }
        handleGate(player, obj);
    }
    onNpcClick(COUNCILLOR_HALGRIVE, FARMER_BRUMTY, RED_SHEEP, GREEN_SHEEP, BLUE_SHEEP, YELLOW_SHEEP) { (player, npc) ->
        when (npc.id) {
            COUNCILLOR_HALGRIVE -> { CouncillorHalgrivesD(player, npc) }
            FARMER_BRUMTY -> { FarmerBrumtyD(player, npc) }
            RED_SHEEP, GREEN_SHEEP, BLUE_SHEEP, YELLOW_SHEEP -> { SheepHerderUtils().prodSheep(player, npc) }
        }
    }
    instantiateNpc(RED_SHEEP, GREEN_SHEEP, BLUE_SHEEP, YELLOW_SHEEP) { id, tile -> SickSheepNPC(id, tile) }
    onItemOnNpc(RED_SHEEP, GREEN_SHEEP, BLUE_SHEEP, YELLOW_SHEEP) { e ->
        when (e.item.id) {
            SHEEP_FEED -> { SheepHerderUtils().feedSheep(e.player, e.npc) }
            POISON -> { e.player.sendMessage("The sheep isn't going to drink the poison from the bottle. Perhaps you should try feeding it something else more appropriate.") }
        }
    }
    onItemOnObject(objectNamesOrIds = arrayOf(INCINERATOR), itemNamesOrIds = arrayOf(RED_SHEEP_BONES, GREEN_SHEEP_BONES, BLUE_SHEEP_BONES, YELLOW_SHEEP_BONES)) { e ->
        SheepHerderUtils().incinerateBones(e.player, e.item)
    }
}

fun completeSheepHerder(player: Player) {
    player.packets.setIFGraphic(1244, 18, 8961)
    player.questManager.completeQuest(Quest.SHEEP_HERDER)
}
