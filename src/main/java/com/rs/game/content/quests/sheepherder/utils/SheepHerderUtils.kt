package com.rs.game.content.quests.sheepherder.utils

import com.rs.engine.dialogue.HeadE.*
import com.rs.engine.dialogue.startConversation
import com.rs.engine.quest.Quest
import com.rs.game.World
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Equipment
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.util.Utils

class SheepHerderUtils {

    fun prodSheep(player: Player, npc: NPC) {
        val stage = player.questManager.getStage(Quest.SHEEP_HERDER)
        val cattleprodEquipped = player.equipment.getId(Equipment.WEAPON) == CATTLE_PROD
        val plagueTrousersEquipped = player.equipment.getId(Equipment.LEGS) == PLAGUE_TROUSERS
        val plagueJacketEquipped = player.equipment.getId(Equipment.CHEST) == PLAGUE_JACKET

        var currentTick = npc.tickCounter
        var cooldownTick = player.tempAttribs.getL("prodCooldownExpiration")

        when (stage) {
            STAGE_UNSTARTED, STAGE_RECEIVED_SHEEP_FEED -> {
                player.sendMessage("You feel it would be rude to prod Farmer Brumpty's sheep, without permission.")
            }
            STAGE_RECEIVED_PROTECTIVE_CLOTHING -> {
                val sheepId = npc.id
                val sheepBoneAttrib = when (sheepId) {
                    RED_SHEEP -> "RED_SHEEP_BONES"
                    GREEN_SHEEP -> "GREEN_SHEEP_BONES"
                    BLUE_SHEEP -> "BLUE_SHEEP_BONES"
                    YELLOW_SHEEP -> "YELLOW_SHEEP_BONES"
                    else -> null
                }

                if (!plagueTrousersEquipped || !plagueJacketEquipped) {
                    player.startConversation { player(WORRIED, "The sheep looks extremely unwell. I don't want to touch it without a full protective suit.") }
                    return
                }

                if (!cattleprodEquipped) {
                    player.startConversation { player(WORRIED, "I'm not prodding a sickly-looking sheep with my hands! I'll need an appropriate tool.") }
                    return
                }

                if (sheepBoneAttrib != null && player.questManager.getAttribs(Quest.SHEEP_HERDER).getB(sheepBoneAttrib)) {
                    player.sendMessage("You've already incinerated the bones of this sheep.")
                    return
                }

                if (player.tile != npc.tile) {
                    if (npc is SickSheepNPC && !npc.enteredEnclosure) {
                        if (currentTick < cooldownTick) return
                        player.lock(1)
                        player.anim(PROD_SHEEP_ANIM)
                        npc.soundEffect(PROD_SHEEP_SOUND, true)
                        val diffX = npc.tile.x - player.tile.x
                        val diffY = npc.tile.y - player.tile.y
                        val randomSteps = Utils.random(3, 5)
                        val destination = npc.tile.transform(diffX * randomSteps, diffY * randomSteps)
                        npc.forceTalk("BAAAAA!")
                        npc.setForceWalk(destination)
                        npc.setRandomWalk(false)
                        npc.tempAttribs.setB("proddedRecently", true)
                        npc.tempAttribs.setO<Player>("player", player)
                        npc.tempAttribs.setL("lastProddedTick", npc.tickCounter)
                        player.tempAttribs.setL("prodCooldownExpiration", currentTick + PROD_COOLDOWN_TICKS)
                        return
                    } else {
                        player.sendMessage("The sheep is already in the enclosure. You don't need to prod it.")
                        return
                    }
                }
            }
            STAGE_SHEEP_INCINERATED, STAGE_COMPLETE -> {
                player.sendMessage("You've already cleared the plague from these sheep.")
            }
        }
    }

    fun feedSheep(player: Player, npc: NPC) {
        val sheepItemId = when (npc.id) {
            RED_SHEEP -> RED_SHEEP_BONES
            GREEN_SHEEP -> GREEN_SHEEP_BONES
            BLUE_SHEEP -> BLUE_SHEEP_BONES
            YELLOW_SHEEP -> YELLOW_SHEEP_BONES
            else -> null
        }

        if (sheepItemId != null && npc is SickSheepNPC && npc.tile.withinArea(2595, 3351, 2609, 3364)) {
            player.schedule {
                player.startConversation { simple("You feed some poisoned sheep food to the sheep.<br>It happily eats it.") }
                wait(2)
                npc.anim(SHEEP_DEATH_ANIM)
                npc.soundEffect(DYING_SHEEP_SOUND, true)
                wait(2)
                player.startConversation { simple("You watch as the sheep collapses dead onto the floor.") }
                World.addGroundItem(Item(sheepItemId), npc.tile, player)
                wait(1)
                npc.tele(npc.respawnTile)
                npc.setForceWalk(npc.respawnTile)
                npc.tempAttribs.removeB("prodded")
                npc.tempAttribs.removeL("lastProddedTick")
                npc.enteredEnclosure = false
                npc.timeInEnclosureTicks = 0
            }
        } else {
            player.startConversation { player(WORRIED, "I don't think I should kill the sheep outside the enclosure. The councillor was worried that it might spread the disease.") }
        }
    }

    fun incinerateBones(player: Player, item: Item) {
        val questManager = player.questManager.getAttribs(Quest.SHEEP_HERDER)
        val bonesAlreadyIncinerated = when (item.id) {
            RED_SHEEP_BONES -> questManager.getB("RED_SHEEP_BONES")
            GREEN_SHEEP_BONES -> questManager.getB("GREEN_SHEEP_BONES")
            BLUE_SHEEP_BONES -> questManager.getB("BLUE_SHEEP_BONES")
            YELLOW_SHEEP_BONES -> questManager.getB("YELLOW_SHEEP_BONES")
            else -> false
        }

        if (bonesAlreadyIncinerated) {
            player.sendMessage("You've already incinerated the bones for this sheep.")
            return
        }

        player.lock(4)
        player.schedule {
            player.anim(INCINERATE_BONES_ANIM)
            player.sendMessage("You put the remains into the furnace.")
            player.soundEffect(player, INCINERATE_BONES_SOUND, true)
            wait(4)
            player.inventory.deleteItem(item)
            player.sendMessage("The remains burn to dust.")

            when (item.id) {
                RED_SHEEP_BONES -> questManager.setB("RED_SHEEP_BONES", true)
                GREEN_SHEEP_BONES -> questManager.setB("GREEN_SHEEP_BONES", true)
                BLUE_SHEEP_BONES -> questManager.setB("BLUE_SHEEP_BONES", true)
                YELLOW_SHEEP_BONES -> questManager.setB("YELLOW_SHEEP_BONES", true)
            }

            if (checkSheepBonesCompleted(player)) {
                player.questManager.setStage(Quest.SHEEP_HERDER, STAGE_SHEEP_INCINERATED)
                player.sendMessage("That's all of the plague-ridden sheep taken care of. I should return to Councillor Halgrive.")
            }
        }
    }

    private fun checkSheepBonesCompleted(player: Player): Boolean {
        val questAttribs = player.questManager.getAttribs(Quest.SHEEP_HERDER)
        return listOf("RED", "GREEN", "BLUE", "YELLOW")
            .all { questAttribs.getB("${it}_SHEEP_BONES") }
    }

}
