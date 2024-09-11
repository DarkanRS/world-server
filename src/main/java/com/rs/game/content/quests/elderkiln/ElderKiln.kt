package com.rs.game.content.quests.elderkiln

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onLogin

const val STAGE_UNSTARTED = 0
const val STAGE_HATCH_EGG = 1
const val STAGE_SAVE_GAAL_FIGHTPITS = 2
const val STAGE_WRAP_UP_FIGHT_PITS = 4
const val STAGE_GO_TO_KILN = 5
const val STAGE_ESCORT_GAAL_KILN = 6
const val STAGE_COMPLETE = 45

@QuestHandler(
    quest = Quest.ELDER_KILN,
    startText = "Speak to TzHaar-Mej-Jeh by the Birthing Pools in the TzHaar City.",
    itemsText = "Adamant pickaxe (or better), melee, magic, or ranged armor, weapons, potions, and food.",
    combatText = "Several waves of Tok-Haar creatures and a few high level players.",
    rewardsText =
            "100,000 XP lamp (for either Attack<br>" +
            "Strength, Defence, Magic, or Ranged)<br>" +
            "50,000 Magic XP<br>" +
            "30,000 Agility XP<br>" +
            "Tokkul-Zo (boosts 10% damage when<br>" +
            "fighting against lava or obsidian creatures)<br>" +
            "Access to the Fight Kiln",
    completedStage = STAGE_COMPLETE
)
class ElderKiln : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int) = when (stage) {
        STAGE_UNSTARTED -> listOf("I should speak to TzHaar-Mej-Jeh by the Birthing Pools in the TzHaar City.")
        STAGE_HATCH_EGG -> listOf("I need to help the TzHaar-Mej hatch their egg by regulating the temperature.")
        STAGE_SAVE_GAAL_FIGHTPITS -> listOf("The Ga'al is going to be honor killed in the Fight Pits and I need to get him back to Jeh.")
        STAGE_WRAP_UP_FIGHT_PITS -> listOf("I successfully defended the Ga'al in the Fight Pits and took out the champion.", "I should speak with the Mej's in the marketplace.")
        STAGE_GO_TO_KILN -> listOf("TzHaar-Mej-Ak has reluctantly decided to allow our attempt to restore the memories.", "I should take Ga'al to the Fight Kiln.")
        STAGE_COMPLETE -> listOf("QUEST COMPLETE!")
        else -> listOf("Invalid quest stage. Report this to an administrator.")
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.MAGIC, 50000.0)
        player.skills.addXpQuest(Skills.AGILITY, 30000.0)
        player.inventory.addItemDrop(Item(23643, 1).addMetaData("tzhaarCharges", 4000))
        player.inventory.addItemDrop(23645, 1)
        sendQuestCompleteInterface(player, 23643)
    }

    override fun updateStage(player: Player, stage: Int) {
        when(stage) {
            STAGE_SAVE_GAAL_FIGHTPITS -> {
                player.vars.setVarBit(10809, 25)
                player.vars.setVarBit(10833, 2)
                player.vars.setVarBit(10811, 1)
                player.vars.setVarBit(10832, 1)
            }
            STAGE_WRAP_UP_FIGHT_PITS, STAGE_GO_TO_KILN -> {
                player.vars.setVarBit(10809, 40)
                player.vars.setVarBit(10833, 2)
                player.vars.setVarBit(10811, 1)
                player.vars.setVarBit(10832, 1)
                player.vars.setVarBit(10810, 1)
            }
        }
    }
}

@ServerStartupEvent
fun mapElderKiln() {
    onLogin {
        it.player.setQuestStage(Quest.ELDER_KILN, STAGE_ESCORT_GAAL_KILN)
    }
}