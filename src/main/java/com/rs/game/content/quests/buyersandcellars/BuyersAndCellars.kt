package com.rs.game.content.quests.buyersandcellars

import com.rs.engine.quest.Quest
import com.rs.engine.quest.QuestHandler
import com.rs.engine.quest.QuestOutline
import com.rs.game.content.skills.thieving.PickPocketAction
import com.rs.game.content.skills.thieving.PickPocketableNPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onXpDrop

@QuestHandler(
    quest = Quest.BUYERS_AND_CELLARS,
    startText = "Speak to Darren Lightfinger in his cellar, accessed through a trapdoor next to a small house just north of the Lumbridge furnace.",
    itemsText = "Logs.",
    combatText = "None.",
    rewardsText = "500 Thieving XP<br>Access to the Thieves' guild<br>3 Thieves' Guild pamphlets<br>Ability to collect Hanky Points<br>",
    completedStage = 9
)
class BuyersAndCellars : QuestOutline() {
    override fun getJournalLines(player: Player, stage: Int): List<String> {
        val lines: MutableList<String> = ArrayList()
        when (stage) {
            0 -> {
                lines.add("Darren Lightfinger, in his cellar under the house north of")
                lines.add("Lumbridge's forge, has offered to test my pickpocketing skills")
                lines.add("and offer advice.")
                lines.add("")
            }

            1 -> {
                lines.add("I should practise my thieving technique")
                lines.add("and return to the Thieves' Guild when I am ready.")
                lines.add("")
            }

            2 -> {
                lines.add("Darren can now tell me his plan.")
                lines.add("")
            }

            3 -> {
                lines.add("I should go to the grounds of Lumbridge Castle")
                lines.add("and discover the identity of the chalice's owner from Robin.")
                lines.add("")
            }

            4 -> {
                lines.add("Robin tells me that the chalice has been taken into Lumbridge Swamp by an irritable old man.")
                lines.add("")
            }

            5 -> {
                lines.add("I need to find a way to divert Father Urhney's attention")
                lines.add("so I can get the key off him.")
                lines.add("Maybe Robin can advise me.")
                lines.add("")
            }

            6 -> {
                lines.add("If I light a fire outside one of the windows")
                lines.add("of Father Urhney's house.")
                lines.add(" it might distract him enough for me to be able to pick his pocket.")
                lines.add("")
            }

            7 -> {
                lines.add("I acquired Urhney's key.")
                lines.add("Now to steal the chalice from its display case...")
                lines.add("")
            }

            8 -> {
                lines.add("I have stolen the golden chalice. I should deliver it to Darren.")
                lines.add("")
            }

            9 -> {
                lines.add("I have given the chalice to Darren Lightfinger,")
                lines.add("Guildmaster of the Thieves' Guild.")
                lines.add("")
            }
        }
        return lines
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.THIEVING, 500.0)
        if (player.inventory.hasFreeSlots()) player.inventory.addItem(18646, 3)
        else {
            player.bank.addItem(Item(18646), false)
            player.bank.addItem(Item(18646), false)
            player.bank.addItem(Item(18646), false)
            player.sendMessage("You do not have enough free space so your rewards have been sent to the bank.")
        }
        sendQuestCompleteInterface(player, 18648)
    }

    override fun updateStage(player: Player, stage: Int) {
        when (stage) {
            1, 2 -> {
                player.vars.setVarBit(7820, 1)
                player.vars.setVarBit(7793, 0)
            }

            3, 8 -> {
                player.vars.setVarBit(7820, 1)
                player.vars.setVarBit(7793, 25)
            }

            4, 5, 6, 7 -> {
                player.vars.setVarBit(7820, 1)
                player.vars.setVarBit(7793, 10)
            }
        }
    }
}

@ServerStartupEvent
fun mapDodgyFlierManPickpocket() {
    onXpDrop { e ->
        if (e.skillId != Skills.THIEVING) return@onXpDrop
        val action = e.player.actionManager.action as? PickPocketAction ?: return@onXpDrop
        if (e.player.isQuestStarted(Quest.BUYERS_AND_CELLARS) || e.player.isQuestComplete(Quest.BUYERS_AND_CELLARS)) return@onXpDrop
        if (action.npcData == PickPocketableNPC.MAN && Utils.random(5) == 1 && !e.player.containsAnyItems(18645))
            e.player.inventory.addItem(18645)
    }
}