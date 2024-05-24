package com.rs.game.content.miniquests.from_tiny_acorns

import com.rs.engine.dialogue.Dialogue
import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.engine.miniquest.MiniquestHandler
import com.rs.engine.miniquest.MiniquestOutline
import com.rs.game.World
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onLogin
import com.rs.plugin.kts.onObjectClick

@ServerStartupEvent
fun mapFromTinyAcorns() {
    onLogin { (player) ->
        val hasDragon = player.bank.containsItem(18651, 1) || player.inventory.containsItem(18651)
        if (!player.miniquestManager.isComplete(Miniquest.FROM_TINY_ACORNS) && player.miniquestManager.getStage(Miniquest.FROM_TINY_ACORNS) >= 2 && !hasDragon) {
            player.miniquestManager.setStage(Miniquest.FROM_TINY_ACORNS, 1)
            player.vars.setVarBit(7821, 1)
        }
    }
    onItemClick(18649, options = arrayOf("Put-down")) { e ->
        if (!e.player.tile.withinArea(3220, 3427, 3228, 3432)) {
            e.player.sendMessage("I should find a suitable spot to put this. Maybe just north of him...")
            return@onItemClick
        }
        World.addGroundItem(e.item, e.player.tile, e.player)
        e.player.inventory.deleteItem(e.item)
        e.player.inventory.refresh()
    }
    onObjectClick(51656) { (player, obj) ->
        val uristDistracted = player.miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).getB("UristDistracted")
        val guardDistracted = player.miniquestManager.getAttribs(Miniquest.FROM_TINY_ACORNS).getB("GuardDistracted")
        val uristID = 11270
        val guardID = 11269
        if (player.inventory.containsItem(18651)) {
            player.sendMessage("You've stolen the Toy Baby Dragon already.")
            return@onObjectClick
        }
        if (uristDistracted && guardDistracted) {
            player.actionManager.setAction(StealToyDragon(obj))
        } else {
            if (!uristDistracted) {
                player.startConversation {
                    npc(uristID, CALM_TALK, "Sorry, " + player.getPronoun("lad", "miss") + ", I can't let you pick it up just yet. Still needs its oil and polish before I can call it a finished work, see.")
                    player(SKEPTICAL, "It looks finished to me.")
                    npc(uristID, SHAKING_HEAD, "And it'd look finished until the works gummed up or the oil clouded the rubies. Can't let a piece this pricey be a rush job, can I?")
                }
                return@onObjectClick
            }
            player.npcDialogue(guardID, ANGRY, "Oi! Put that back, thief!")
        }
    }
}

@MiniquestHandler(
    miniquest = Miniquest.FROM_TINY_ACORNS,
    startText = "Speak to Darren Lightfinger in his cellar accessed through a trapdoor north of Lumbridge furnace.",
    itemsText = "None",
    combatText = "None",
    rewardsText = "1,000 Thieving XP<br>Access to the advanced pickpocketing trainer and coshing volunteers in the Thieves' Guild",
    completedStage = 5
)

class FromTinyAcorns : MiniquestOutline() {
    override fun getJournalLines(player: Player, stage: Int): List<String> {
        val lines = ArrayList<String>()
        when (stage) {
            0 -> {
                lines.add("I can start this miniquest by speaking to Darren Lightfinger in")
                lines.add("the Lumbridge Thieves' Guild.")
                lines.add("")
            }

            1 -> {
                lines.add("I need to pay Urist Loric a visit and steal the dragon.")
                lines.add("Maybe Robin will be able to help.")
                lines.add("")
            }

            2 -> {
                lines.add("I've stolen the dragon.")
                lines.add("Now I just need to get Dareen's investment back.")
                lines.add("")
            }

            3 -> {
                lines.add("Urist agreed to give Darren his money back after loosing the dragon.")
                lines.add("I should take the Banker's Note back to Darren.")
                lines.add("")
            }

            4 -> lines.add("MINIQUEST COMPLETE!")
            else -> lines.add("Invalid quest stage. Report this to an administrator.")
        }
        return lines
    }

    override fun complete(player: Player) {
        player.skills.addXpQuest(Skills.THIEVING, 1000.0)
        sendQuestCompleteInterface(player, 18651)
    }

    override fun updateStage(player: Player) {
        player.vars.setVarBit(7821, if (player.miniquestManager.getStage(Miniquest.FROM_TINY_ACORNS) == 1) 1 else 0)
    }
}
