package com.rs.game.content.quests.troll_stronghold

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.achievements.Achievement
import com.rs.game.content.quests.troll_stronghold.dialogue.npcs.death_plateau.DadD
import com.rs.game.content.quests.troll_stronghold.dialogue.npcs.troll_stronghold.*
import com.rs.game.content.quests.troll_stronghold.instances.npcs.*
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.content.world.areas.trollheim.npcs.Eadgar
import com.rs.game.model.entity.player.Player
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.*

@QuestHandler(
    quest = Quest.TROLL_STRONGHOLD,
    startText = "Talk to Commander Denulth at the Burthorpe training camp.",
    itemsText = "Climbing boots, rock climbing boots, or 12 coins.",
    combatText = "You will need to defeat a level 101 enemy and a level 171 enemy. You might need to defeat two level 71 enemies.",
    rewardsText = "2 lamps each giving 10,000 XP (any level 30+ skill)<br>" +
            "Access to Trollheim and the troll stronghold<br>" +
            "Access to God Wars Dungeon (with 60 Agility or 60 Strength)",
    completedStage = STAGE_COMPLETE
)

class TrollStronghold : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("To start this quest, I should talk to Commander Denulth at the Burthorpe training camp.")
        STAGE_ACCEPTED_QUEST -> listOf("I promised Denulth I would rescue Godric from the Troll Stronghold.")
        STAGE_ENTERED_ARENA -> listOf("I stumbled upon an arena where a Troll Champion named Dad challenged me to a fight.")
        STAGE_ENGAGED_DAD -> listOf("I engaged in combat with the Troll Champion named Dad.",
            "I should defeat the Troll Champion if I want to pass through to the Troll Stronghold.")
        STAGE_FINISHED_DAD -> listOf("I have defeated the Troll Champion, Dad.",
            "I should proceed through the arena to the Troll Stronghold and find a way into the prison where Godric is being held.")
        STAGE_UNLOCKED_PRISON_DOOR -> listOf("I found my way into the prison. I found Godric and a hermit called Eadgar in their cells.",
            " ",
            "I still need to rescue:",
            Utils.strikeThroughIf("Eadgar") { player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).getB(UNLOCKED_EADGAR_CELL) },
            Utils.strikeThroughIf("Godric") { player.questManager.getAttribs(Quest.TROLL_STRONGHOLD).getB(UNLOCKED_GODRIC_CELL) }
        )
        STAGE_UNLOCKED_BOTH_CELLS -> listOf("I have freed both Godric and Eadgar from their cells.",
            "I should tell Dunstan his son is safe.")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun updateStage(player: Player, stage: Int) {
    }

    override fun complete(player: Player) {
        player.interfaceManager.sendAchievementComplete(Achievement.TROLL_STRONGHOLD_445)
        player.packets.setIFGraphic(1244, 18, 9541)
        sendQuestCompleteInterface(player, QUEST_REWARD_LAMP)
        player.inventory.addItem(QUEST_REWARD_LAMP, 2)
    }
}

@ServerStartupEvent
fun mapTrollStrongholdInteractions() {

    getInteractionDistance(GODRIC, EADGAR) { _, npc ->
        if (npc.id == EADGAR) {
            if (npc.plane == 0) 4 else 0
        } else {
            4
        }
    }

    onNpcClick(DAD, GODRIC, EADGAR, options = arrayOf("Talk-to")) { (player, npc) ->
        when(npc.id) {
            DAD -> DadD(player, npc)
            GODRIC -> {
                if (TrollStrongholdUtils(player).isInCell(npc.tile)) {
                    GodricCellD(player, npc)
                } else {
                    player.sendMessage("Godric is busy right now.")
                    return@onNpcClick
                }
            }
            EADGAR -> {
                if (TrollStrongholdUtils(player).isInCell(npc.tile)) {
                    EadgarCellD(player, npc)
                } else {
                    if (npc.plane == 0) {
                        player.sendMessage("Eadgar is busy right now.")
                        return@onNpcClick
                    } else {
                        Eadgar(player, npc)
                    }
                }
            }
        }
    }

    onNpcClick(TWIG_SLEEPING, BERRY_SLEEPING, options = arrayOf("Pickpocket")) { (player, npc) ->
        when(npc.id) {
            TWIG_SLEEPING -> {
                if (!player.containsOneItem(CELL_KEY_GODRIC)) player.actionManager.setAction(PickpocketTwigAndBerry(npc, NPCType.TWIG.keyToLoot))
                else player.sendMessage("You already have the key from Twig.")
            }
            BERRY_SLEEPING -> {
                if (!player.containsOneItem(CELL_KEY_EADGAR)) player.actionManager.setAction(PickpocketTwigAndBerry(npc, NPCType.BERRY.keyToLoot))
                else player.sendMessage("You already have the key from Berry.")
            }
        }
    }

    onObjectClick(ARENA_DOOR_1, ARENA_DOOR_2, PRISON_DOOR, GODRIC_DOOR, EADGAR_DOOR) { (player, obj, _) ->
        when(obj.id) {
            ARENA_DOOR_1, ARENA_DOOR_2 -> TrollStrongholdUtils(player).handleArenaDoors(obj)
            PRISON_DOOR -> TrollStrongholdUtils(player).handleUnlockPrisonDoor(obj)
            GODRIC_DOOR -> TrollStrongholdUtils(player).handleUnlockGodricDoor(obj)
            EADGAR_DOOR -> TrollStrongholdUtils(player).handleUnlockEadgarDoor(obj)
        }
    }

    onItemOnObject(arrayOf(PRISON_DOOR, GODRIC_DOOR, EADGAR_DOOR)) { (player, obj, item) ->
        when (item.id) {
            PRISON_KEY -> if (obj.id == PRISON_DOOR) TrollStrongholdUtils(player).handleUnlockPrisonDoor(obj) else player.sendMessage("Nothing interesting happens.")
            CELL_KEY_GODRIC -> if (obj.id == GODRIC_DOOR) TrollStrongholdUtils(player).handleUnlockGodricDoor(obj) else player.sendMessage("Nothing interesting happens.")
            CELL_KEY_EADGAR -> if (obj.id == EADGAR_DOOR) TrollStrongholdUtils(player).handleUnlockEadgarDoor(obj) else player.sendMessage("Nothing interesting happens.")
        }
    }

    instantiateNpc(DAD) { id, tile -> Dad(id, tile) }
    instantiateNpc(*TROLL_GENERALS.toTypedArray()) { id, tile -> TrollGenerals(id, tile) }
    instantiateNpc(TWIG_SLEEPING) { id, tile -> TwigAndBerry(id, tile, NPCType.TWIG) }
    instantiateNpc(BERRY_SLEEPING) { id, tile -> TwigAndBerry(id, tile, NPCType.BERRY) }

    onDestroyItem(QUEST_REWARD_LAMP) { (player, item) ->
        if (item.id == QUEST_REWARD_LAMP) {
            if (player.getI(TROLL_STRONGHOLD_QUEST_LAMPS_LOST) < 0) player.set(TROLL_STRONGHOLD_QUEST_LAMPS_LOST, 1)
            else player.set(TROLL_STRONGHOLD_QUEST_LAMPS_LOST, (player.getI(TROLL_STRONGHOLD_QUEST_LAMPS_LOST) + 1))
        }
    }

}
