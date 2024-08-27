package com.rs.game.content.quests.troll_stronghold.utils

import com.rs.engine.quest.Quest
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.lib.Constants
import com.rs.lib.util.Utils

class PickpocketTwigAndBerry(private val npc: NPC, private val keyToLoot: Int) : PlayerAction() {
    private var success = false

    override fun start(player: Player): Boolean {
        if (checkAll(player)) {
            success = rollSuccess(player)
            player.faceEntityTile(npc)
            player.schedule {
                player.anim(PICKPOCKETING_ANIM)
                player.sendMessage("You attempt to pick the guard's pocket.")
            }
            setActionDelay(player, 2)
            player.lock()
            return true
        }
        return false
    }

    override fun process(player: Player): Boolean {
        return checkAll(player)
    }

    override fun processWithDelay(player: Player): Int {
        if (!success) {
            npc.forceTalk("What you think you doing?")
            player.sendMessage("You fail to pick the guard's pocket.")
            npc.combatTarget = player
        } else {
            if (player.getQuestStage(Quest.TROLL_STRONGHOLD) == STAGE_UNLOCKED_PRISON_DOOR) {
                player.inventory.addItem(keyToLoot, 1)
                player.sendMessage("You find a small key on ${npc.name}'s belt.")
            } else {
                player.sendMessage("You find nothing on ${npc.name}.")
            }
        }
        stop(player)
        return -1
    }

    override fun stop(player: Player) {
        player.unlock()
        player.stopFaceEntity()
        setActionDelay(player, 1)
    }

    fun rollSuccess(player: Player): Boolean {
        return Utils.skillSuccess(player.skills.getLevel(Constants.THIEVING), player.auraManager.thievingMul, 30, 256)
    }

    private fun checkAll(player: Player): Boolean {
        if (player.skills.getLevel(Skills.THIEVING) < 30) {
            player.sendMessage("You need to be at least level 30 Thieving to pickpocket ${npc.name}.")
            return false
        }
        if (player.getQuestStage(Quest.TROLL_STRONGHOLD) >= STAGE_UNLOCKED_BOTH_CELLS) {
            player.sendMessage("You've already freed Godric and Eadgar from their cells.")
            return false
        }
        if (!player.inventory.hasFreeSlots()){
            player.sendMessage("You need at least 1 free inventory slot before pickpocketing ${npc.name}.")
            return false
        }
        if (player.isDead || player.hasFinished() || player.hasPendingHits()) return false
        if (player.attackedBy != null && player.inCombat()) {
            player.sendMessage("You can't do that while you're in combat.")
            return false
        }
        return true
    }
}
