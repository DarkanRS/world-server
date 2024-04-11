package com.rs.game.content.miniquests.huntforsurok.bork

import com.rs.cache.loaders.ItemDefinitions
import com.rs.engine.dialogue.HeadE
import com.rs.engine.dialogue.startConversation
import com.rs.engine.miniquest.Miniquest
import com.rs.game.World
import com.rs.game.content.achievements.AchievementDef
import com.rs.game.content.achievements.SetReward
import com.rs.game.content.minigames.treasuretrails.TreasureTrailsManager
import com.rs.game.model.entity.Entity
import com.rs.game.model.entity.async.schedule
import com.rs.game.model.entity.npc.NPC
import com.rs.game.model.entity.npc.combat.CombatScript.*
import com.rs.game.model.entity.player.Player
import com.rs.game.model.entity.player.Skills
import com.rs.game.tasks.WorldTasks
import com.rs.lib.game.Item
import com.rs.lib.game.Tile
import com.rs.lib.util.Utils
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.instantiateNpc
import com.rs.plugin.kts.npcCombat

class Bork(id: Int, tile: Tile, spawned: Boolean) : NPC(id, tile, spawned) {

	init {
		lureDelay = 0
		setForceAgressive(true)
	}

	override fun blocksOtherNpcs(): Boolean = false

	override fun sendDeath(source: Entity?) {
		World.getNPCsInChunkRange(source?.chunkId ?: return, 2)
            .filter { it.id == 7135 }
            .forEach { it.sendDeath(source) }
		resetWalkSteps()
		combat.removeTarget()
		schedule {
			(source as? Player)?.let {
				it.resetReceivedHits()
				it.interfaceManager.sendForegroundInterfaceOverGameWindow(693)
			}
			wait(8)
			(source as? Player)?.let { it.interfaceManager.closeInterfacesOverGameWindow() }
			anim(combatDefinitions.deathEmote)
			wait(4)
			drop()
			reset()
			setLocation(respawnTile)
			finish()
		}
	}

	override fun drop() {
		val killer = mostDamageReceivedSourcePlayer ?: return
		if (killer.getMiniquestStage(Miniquest.HUNT_FOR_SUROK) == 4) {
			killer.miniquestManager.complete(Miniquest.HUNT_FOR_SUROK)
			killer.startConversation {
				player(HeadE.CONFUSED, "It looks like Surok managed to escape during the fight. I wonder what he is up to now...")
				player(HeadE.AMAZED, "What the-? This power! It must be Zamorak! I can't fight something this strong! I better loot what I can and get out of here!")
			}
		}
		if (!killer.getDailyB("borkKilled")) {
			val diaryReward = SetReward.VARROCK_ARMOR.hasRequirements(killer, AchievementDef.Area.VARROCK, AchievementDef.Difficulty.HARD, false)
			val row = killer.equipment.getRingId() != -1 && ItemDefinitions.getDefs(killer.equipment.getRingId()).name.lowercase().contains("ring of wealth")
			killer.setDailyB("borkKilled", true)
			killer.skills.addXp(Skills.SLAYER, if (diaryReward) 3000.0 else 1500.0)

			val drops = ArrayList<Item>().apply {
				add(Item(532, 1))
				add(Item(12159, 2 * (if (diaryReward) 2 else 1) + if (row) 1 else 0))
				add(Item(12160, 7 * (if (diaryReward) 2 else 1) + if (row) 3 else 0))
				add(Item(12163, 5 * (if (diaryReward) 2 else 1)))
				add(Item(995, Utils.random(2, 20000) * (if (diaryReward) 2 else 1)))
				add(Item(1623, if (diaryReward) 2 else 1))
				add(Item(1621, (if (diaryReward) 2 else 1) + if (row) 2 else 0))
				add(Item(1619, (if (diaryReward) 2 else 1) + if (row) 1 else 0))
			}
			if (Utils.random(64) == 0)
				sendDrop(killer, Item(18778, 1))
			if (Utils.random(32) == 0)
				sendDrop(killer, Item(TreasureTrailsManager.SCROLL_BOXES[3], 1))
			if (Utils.random(16) == 0)
				sendDrop(killer, Item(TreasureTrailsManager.SCROLL_BOXES[2], 1))
			drops.forEach { item -> sendDrop(killer, item) }
		}
	}
}

@ServerStartupEvent
fun instantiateAndCombat() {
	instantiateNpc("Bork") { npcId, tile -> Bork(npcId, tile, false) }

	npcCombat("Bork") { npc, target ->
		val defs = npc.combatDefinitions
		if (target is Player && npc.hitpoints <= defs.hitpoints * 0.4 && !npc.tempAttribs.getB("spawnedOrks")) {
			npc.forceTalk("Come to my aid, brothers!")
			npc.tempAttribs.setB("spawnedOrks", true)
			target.lock()
			npc.isCantInteract = true
			target.playCutscene { cs ->
				cs.action(2) { target.interfaceManager.sendForegroundInterfaceOverGameWindow(691) }
				cs.delay(6)
				cs.action {
					repeat(3) {
						World.spawnNPC(7135, Tile.of(npc.tile, 1), true).apply {
							setForceAgressive(true)
							setForceMultiArea(true)
						}
					}
					target.interfaceManager.closeInterfacesOverGameWindow()
					target.unlock()
					target.resetReceivedHits()
				}
			}
			npc.apply {
				isCantInteract = false
				forceTalk("Destroy the intruder, my Legions!")
			}
			return@npcCombat 0
		}
		npc.anim(if ((0..1).random() == 0) defs.attackEmote else 8757)
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.maxHit, null, target)))
		npc.attackSpeed
	}

	val messages = arrayOf("For Bork!", "Die Human!", "To the attack!", "All together now!")

	npcCombat("Ork legion") { npc, target ->
		npc.anim(npc.combatDefinitions.attackEmote)
		if ((0..3).random() == 0) npc.forceTalk(messages.random())
		delayHit(npc, 0, target, getMeleeHit(npc, npc.combatDefinitions.maxHit))
		npc.attackSpeed
	}
}