package com.rs.game.content.quests.dig_site.utils

import com.rs.engine.quest.Quest
import com.rs.game.content.skills.thieving.PickPocketableNPC
import com.rs.game.model.entity.Hit
import com.rs.game.model.entity.Hit.HitLook
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.model.entity.player.actions.PlayerAction
import com.rs.game.tasks.WorldTasks
import com.rs.lib.Constants
import com.rs.lib.game.Item
import com.rs.lib.util.Utils
import com.rs.utils.DropSets
import com.rs.utils.drop.DropTable

class PickpocketWorkmanAction(private val npc: NPC) : PlayerAction() {
    private var success = false
    override fun start(player: Player): Boolean {
        if (checkAll(player)) {
            success = successful(player)
            player.faceEntityTile(npc)
            player.sendMessage("You attempt to pick the workman's pocket...", true)
            WorldTasks.delay(0) {
                player.anim(PICKPOCKETING_ANIM)
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
            player.sendMessage("You failed to pick the workman's pocket.")
            npc.forceTalk("What do you think you're doing?")
            npc.anim(NPC_STUN_ANIM)
            player.applyHit(Hit(player, 20, HitLook.TRUE_DAMAGE))
            npc.faceEntityTile(player)
            player.anim(PLAYER_STUN_ANIM)
            player.spotAnim(STUN_BIRDS_SPOTANIM, 5, 60)
            player.sendMessage("You've been stunned.")
        } else {
            player.incrementCount(npc.definitions.name + " pickpocketed")
            val dropSet = DropSets.getDropSet("workman_pickpocket")
            var drops = DropTable.calculateDrops(player, dropSet)

            if (drops.isEmpty()) {
                player.sendMessage("You fail to steal anything.")
            } else {
                var randomDrop: Item? = null
                var attempts = 0
                val maxAttempts = 10

                do {
                    randomDrop = drops.random()
                    if (randomDrop != null && randomDrop.id == ANIMAL_SKULL && player.inventory.containsItem(ANIMAL_SKULL)) {
                        randomDrop = null
                    } else if (randomDrop != null && randomDrop.id == ANIMAL_SKULL && player.getQuestStage(Quest.DIG_SITE) != STAGE_BEGIN_EXAM_1) {
                        randomDrop = null
                    } else if (randomDrop != null && randomDrop.id == ANIMAL_SKULL && player.questManager.getAttribs(Quest.DIG_SITE).getB(GREEN_STUDENT_EXAM_1_OBTAINED_ANSWER)) {
                        randomDrop = null
                    }
                    attempts++
                } while (randomDrop == null && drops.isNotEmpty() && attempts < maxAttempts)

                if (randomDrop == null) {
                    player.sendMessage("You fail to steal anything.")
                } else {
                    if (randomDrop.id == ANIMAL_SKULL) {
                        player.sendMessage("You steal an animal skull.")
                    } else {
                        player.sendMessage("You steal something from the workman's pockets.")
                    }
                    player.skills.addXp(Skills.THIEVING, 10.0)
                    player.inventory.addItem(randomDrop.id, randomDrop.amount)
                }
            }
        }
        stop(player)
        return -1
    }

    override fun stop(player: Player) {
        player.unlock()
        player.stopFaceEntity()
        setActionDelay(player, 1)
        if (!success) {
            player.schedule {
                player.lock()
                wait(4)
                player.unlock()
                wait(1)
                player.spotAnim(-1)
            }
        }
    }

    fun rollSuccess(player: Player): Boolean {
        return Utils.skillSuccess(player.skills.getLevel(Constants.THIEVING), player.auraManager.thievingMul + (if (PickPocketableNPC.hasArdyCloak(player)) 0.1 else 0.0), 84, 240)
    }

    private fun successful(player: Player): Boolean {
        if (!rollSuccess(player)) return false
        return true
    }

    private fun checkAll(player: Player): Boolean {
        if (player.isDead || player.hasFinished() || player.hasPendingHits()) return false
        if (player.attackedBy != null && player.inCombat()) {
            player.sendMessage("You can't do that whilst you're in combat.")
            return false
        }
        if (!player.inventory.hasFreeSlots()) {
            player.sendMessage("You do not have enough inventory space to do that.")
            return false
        }
        if (player.skills.getLevel(Skills.THIEVING) < 25) {
            player.sendMessage("You need at least level 25 Thieving in order to pick the workman's pockets.")
            return false
        }
        return true
    }
}
