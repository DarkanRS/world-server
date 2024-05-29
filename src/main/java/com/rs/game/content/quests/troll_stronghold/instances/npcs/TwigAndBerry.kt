package com.rs.game.content.quests.troll_stronghold.instances.npcs

import com.rs.engine.pathfinder.Direction
import com.rs.engine.quest.Quest
import com.rs.game.content.quests.troll_stronghold.utils.*
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.player.Player
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import kotlin.random.Random

enum class NPCType(
    val guardTile: Tile,
    val sleepingState: Int,
    val awakeState: Int,
    val keyToLoot: Int
) {
    TWIG(Tile.of(TWIG_GUARD_TILE), TWIG_SLEEPING, TWIG_AWAKE, CELL_KEY_GODRIC),
    BERRY(Tile.of(BERRY_GUARD_TILE), BERRY_SLEEPING, BERRY_AWAKE, CELL_KEY_EADGAR)
}

class TwigAndBerry(
    id: Int,
    tile: Tile,
    private val npcType: NPCType
) : NPC(id, tile) {

    private var tickCounter = 0
    private var nextSpotAnimTick = Utils.random(2, 8)
    private var isDying = false
    private var outOfCombatTicks = 0

    init {
        setRandomWalk(false)
    }

    private fun handleSpotAnim() {
        if (tickCounter == nextSpotAnimTick) {
            spotAnim(SLEEPING_SPOTANIM)
            nextSpotAnimTick = tickCounter + 4 + Utils.random(2, 8)
        }
    }

    override fun processNPC() {
        super.processNPC()
        if (id == npcType.sleepingState) {
            tickCounter++
            handleSpotAnim()
        }
        if (!isDying) {
            if (tile == npcType.guardTile && combatTarget == null) faceDir(Direction.SOUTH)
            if (combatTarget == null) {
                outOfCombatTicks++
                if (outOfCombatTicks >= 2) {
                    resetCombat()
                    outOfCombatTicks = 0
                }
            } else {
                outOfCombatTicks = 0
            }
        }
    }

    override fun resetCombat() {
        tele(npcType.guardTile)
        transformIntoNPC(npcType.sleepingState)
        setRandomWalk(false)
        hitpoints = maxHitpoints
        super.resetCombat()
    }

    override fun inCombat(): Boolean {
        if (combatTarget != null) {
            transformIntoNPC(npcType.awakeState)
            spotAnim(-1)
            setRandomWalk(true)
        }
        return super.inCombat()
    }

    override fun finish() {
        isDying = false
        transformIntoNPC(npcType.sleepingState)
        super.finish()
    }

    override fun sendDeath(source: Entity?) {
        isDying = true
        super.sendDeath(source)
    }

    override fun drop(killer: Player) {
        if (killer.getQuestStage(Quest.TROLL_STRONGHOLD) == STAGE_UNLOCKED_PRISON_DOOR)
            sendDrop(killer, Item(npcType.keyToLoot))
        super.drop(killer)
    }
}
