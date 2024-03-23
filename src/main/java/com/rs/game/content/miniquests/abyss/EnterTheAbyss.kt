package com.rs.game.content.miniquests.abyss

import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.miniquest.MiniquestHandler
import com.rs.engine.miniquest.MiniquestOutline
import com.rs.game.content.skills.runecrafting.Abyss
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onNpcClick
import com.rs.utils.shop.ShopsHandler

@ServerStartupEvent
fun mapEnterTheAbyss() {
    onNpcClick(2257) { (player, npc, option) ->
        val miniquestStage = player.miniquestManager.getStage(Miniquest.ENTER_THE_ABYSS)
        val dialogue = Dialogue()

        when (option) {
            "Talk-to" -> {
                when (miniquestStage) {
                    EnterTheAbyss.NOT_STARTED -> {
                        dialogue.apply {
                            addPlayer(HeadE.CONFUSED, "Hello there, what are you doing here?")
                            addNPC(npc, HeadE.SECRETIVE, "I am researching an interesting phenomenon I call the 'Abyss' and selling runes.")
                            addPlayer(HeadE.CONFUSED, "Where do you get your runes?")
                            addNPC(npc, HeadE.FRUSTRATED, "This is no place to talk! Meet me at the Varrock Chaos Temple!") {
                                player.miniquestManager.setStage(Miniquest.ENTER_THE_ABYSS, EnterTheAbyss.MEET_IN_VARROCK)
                            }
                        }
                    }
                    EnterTheAbyss.MEET_IN_VARROCK, EnterTheAbyss.SCRYING_ORB, EnterTheAbyss.COMPLETED_SCRYING_ORB  -> {
                        dialogue.addNPC(npc, HeadE.FRUSTRATED, "This is no place to talk! Meet me at the Varrock Chaos Temple!")
                    }
                    EnterTheAbyss.COMPLETED -> {
                        dialogue.addNPC(npc, HeadE.FRUSTRATED, "This is no place to talk! If you need help getting out of my sight I can send you to the Abyss?")
                    }
                }
                player.startConversation(dialogue)
            }
            "Trade" -> ShopsHandler.openShop(player, "zamorak_mage_shop")
            "Teleport" -> Abyss.teleport(player, npc)
        }
    }
    onNpcClick(2260) { e ->
        e.player.startConversation(ZamorakMageD(e.player))
    }
    onItemClick(5520, options = arrayOf("Read")) { e ->
        if (e.player.miniquestManager.getStage(Miniquest.ENTER_THE_ABYSS) == EnterTheAbyss.COMPLETED)
            e.player.openBook(AbyssalBook())
        else
            e.player.sendMessage("You don't seem to understand what this book is about.")
    }
}

@MiniquestHandler(
    miniquest = Miniquest.ENTER_THE_ABYSS,
    startText = "The Mage of Zamorak at the beginning of the above-ground River Lum in the Wilderness (roams levels 4-7).",
    itemsText = "None",
    combatText = "None",
    rewardsText = "1,000 Runecrafting XP<br>Small pouch<br>Abyssal book<br>Access to the Abyss.",
    completedStage = EnterTheAbyss.COMPLETED
)

@PluginEventHandler
class EnterTheAbyss : MiniquestOutline() {
    override fun getJournalLines(player: Player, stage: Int): List<String> {
        val lines = ArrayList<String>()
        when (stage) {
            NOT_STARTED -> {
                lines.add("I can start this miniquest by speaking to the Zamorakian mage in the")
                lines.add("wilderness north of Edgeville.")
                lines.add("")
            }

            MEET_IN_VARROCK -> {
                lines.add("I spoke to the Zamorakian mage in the wilderness north of Edgeville.")
                lines.add("He told me I should meet him in Varrock.")
                lines.add("")
            }

            SCRYING_ORB -> {
                lines.add("I was given a scrying orb and told to enter the rune essence mines")
                lines.add("via 3 different teleport methods to gather data on the teleport matrix.")
                lines.add("")
            }

            COMPLETED_SCRYING_ORB -> {
                lines.add("I gave the mage the scrying orb and he told me to speak to him after")
                lines.add("he's finished gathering the data from it. I should speak to him again.")
                lines.add("")
            }

            COMPLETED -> {
                lines.add("")
                lines.add("")
                lines.add("MINIQUEST COMPLETE!")
            }

            else -> lines.add("Invalid quest stage. Report this to an administrator.")
        }
        return lines
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.RUNECRAFTING, 1000.0)
        player.inventory.addItemDrop(5509, 1)
        player.inventory.addItemDrop(5520, 1)
        sendQuestCompleteInterface(player, 5509)
    }

    override fun updateStage(player: Player) {
        player.vars.setVar(492, player.miniquestManager.getStage(Miniquest.ENTER_THE_ABYSS))
    }

    companion object {
        const val NOT_STARTED: Int = 0
        const val MEET_IN_VARROCK: Int = 1
        const val SCRYING_ORB: Int = 2
        const val COMPLETED_SCRYING_ORB: Int = 3
        const val COMPLETED: Int = 4
    }
}
