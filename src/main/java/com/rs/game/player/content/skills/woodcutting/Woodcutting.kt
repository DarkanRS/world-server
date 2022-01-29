// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.skills.woodcutting

import com.rs.cache.loaders.ItemDefinitions
import com.rs.cache.loaders.ObjectType
import com.rs.cores.CoresManager
import com.rs.game.World
import com.rs.game.`object`.GameObject
import com.rs.game.player.Player
import com.rs.plugin.annotations.PluginEventHandler
import com.rs.game.player.actions.Action
import com.rs.game.player.content.Effect
import com.rs.lib.Constants
import com.rs.lib.game.Animation
import com.rs.lib.game.Item
import com.rs.lib.game.SpotAnim
import com.rs.lib.game.WorldTile
import com.rs.lib.util.Logger
import com.rs.lib.util.Utils
import com.rs.plugin.events.LoginEvent
import com.rs.plugin.events.ObjectClickEvent
import com.rs.plugin.handlers.LoginHandler
import com.rs.plugin.handlers.ObjectClickHandler
import com.rs.utils.DropSets
import com.rs.utils.Ticks
import com.rs.utils.drop.DropTable
import java.util.*

@PluginEventHandler
open class Woodcutting(treeObj: GameObject, type: TreeType) : Action() {
	private val treeObj: GameObject
	private val type: TreeType
	private var hatchet: Hatchet? = null
	private val usingBeaver = false

	init {
		this.treeObj = treeObj
		this.type = type
	}

	override fun start(player: Player): Boolean {
		if (!checkAll(player)) return false
		player.faceObject(treeObj)
		player.sendMessage(if (usingBeaver) "Your beaver uses its strong teeth to chop down the tree..." else "You swing your hatchet at the " + (if (TreeType.IVY == type) "ivy" else "tree") + "...", true)
		setActionDelay(player, 4)
		return true
	}

	private fun checkAll(player: Player): Boolean {
		hatchet = Hatchet.getBest(player)
		if (hatchet == null) {
			player.sendMessage("You dont have the required level to use that axe or you don't have a hatchet.")
			return false
		}
		if (!hasWoodcuttingLevel(player)) return false
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("Not enough space in your inventory.")
			return false
		}
		return true
	}

	private fun hasWoodcuttingLevel(player: Player): Boolean {
		if (type.level > player.getSkills().getLevel(8)) {
			player.sendMessage("You need a woodcutting level of " + type.level + " to chop down this tree.")
			return false
		}
		return true
	}

	override fun process(player: Player): Boolean {
		if (!usingBeaver) player.nextAnimation = hatchet!!.anim
		return checkAll(player) && checkTree()
	}

	override fun processWithDelay(player: Player): Int {
		var level: Int = player.skills.getLevel(Constants.WOODCUTTING)
		player.faceObject(treeObj)
//		if (player.familiar != null)
//			level += getSpecialFamiliarBonus(player.familiar.id) //TODO: Familiar bonus
		if (type.rollSuccess(player, level, hatchet)) {
			giveLog(player, type, usingBeaver)
			if (!type.isPersistent || Utils.random(8) == 0) {
				fellTree()
				player.nextAnimation = Animation(-1)
				return -1
			}
		}
		return 3
	}

	open fun fellTree() {
		if (!World.isSpawnedObject(treeObj) && treeObj.plane < 3 && type != TreeType.IVY) {
			var obj = World.getObject(WorldTile(treeObj.x - 1, treeObj.y - 1, treeObj.plane + 1), ObjectType.SCENERY_INTERACT)
			if (obj == null) {
				obj = World.getObject(WorldTile(treeObj.x - 1, treeObj.y - 1, treeObj.plane + 1), ObjectType.SCENERY_INTERACT)
				if (obj == null) {
					obj = World.getObject(WorldTile(treeObj.x, treeObj.y - 1, treeObj.plane + 1), ObjectType.SCENERY_INTERACT)
					if (obj == null) {
						obj = World.getObject(WorldTile(treeObj.x - 1, treeObj.y, treeObj.plane + 1), ObjectType.SCENERY_INTERACT)
						if (obj == null) obj = World.getObject(WorldTile(treeObj.x, treeObj.y, treeObj.plane + 1), ObjectType.SCENERY_INTERACT)
					}
				}
			}
			if (obj != null) World.removeObjectTemporary(obj, type.respawnDelay)
		}
		if (!World.isSpawnedObject(treeObj)) World.spawnObjectTemporary(GameObject(TreeStumps.getStumpId(treeObj.id), treeObj.getType(), treeObj.getRotation(), treeObj.getX(), treeObj.getY(), treeObj.getPlane()), type.respawnDelay)
	}

	open fun checkTree(): Boolean {
		return World.getRegion(treeObj.regionId).objectExists(treeObj)
	}

	override fun stop(player: Player) {
		setActionDelay(player, 4)
	}

	companion object {
		var unlockBlisterwoodTree: LoginHandler = object : LoginHandler() {
			override fun handle(e: LoginEvent) {
				e.player.vars.setVarBit(9776, 1)
			}
		}
		var handleTree: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Tree", "Swamp tree", "Dead tree", "Evergreen", "Dying tree", "Jungle tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.NORMAL)
			}
		}
		var handleOak: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Oak", "Oak tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.OAK)
			}
		}
		var handleWillow: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Willow", "Willow tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.WILLOW)
			}
		}
		var handleMaple: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Maple", "Maple tree", "Maple Tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.MAPLE)
			}
		}
		var handleTeak: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Teak", "Teak tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.TEAK)
			}
		}
		var handleMahogany: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Mahogany", "Mahogany tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.MAHOGANY)
			}
		}
		var handleArcticPine: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Arctic Pine")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down") || e.getObject().definitions.containsOption(0, "Cut down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.ARCTIC_PINE)
			}
		}
		var handleIvy: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Ivy")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.IVY)
			}
		}
		var handleYew: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Yew", "Yew tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.YEW)
			}
		}
		var handleMagic: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Magic tree", "Cursed magic tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Chop down"))
					e.player.actionManager.action = Woodcutting(e.getObject(), TreeType.MAGIC)
			}
		}
		var handleSwayingTree: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>("Swaying tree")) {
			override fun handle(e: ObjectClickEvent) {
				if (e.getObject().definitions.containsOption(0, "Cut-branch"))
					e.player.actionManager.action = object : Woodcutting(e.getObject(), TreeType.SWAYING) {
					override fun fellTree() {}
				}
			}
		}
		var handleBlisterwood: ObjectClickHandler = object : ObjectClickHandler(arrayOf<Any>(61321)) {
			override fun handle(e: ObjectClickEvent) {
				if (e.option == "Chop") e.player.actionManager.action = object : Woodcutting(e.getObject(), TreeType.BLISTERWOOD) {
					override fun fellTree() {
						e.player.vars.setVarBit(9776, 2)
						CoresManager.schedule(Runnable {
							try {
								if (e.player != null && !e.player.hasFinished()) e.player.vars.setVarBit(9776, 1)
							} catch (e1: Throwable) {
								Logger.handle(e1)
							}
						}, Ticks.fromMinutes(2))
					}

					override fun checkTree(): Boolean {
						return e.player.vars.getVarBit(9776) == 1
					}
				}
			}
		}

		fun getLumberjackBonus(player: Player): Double {
			var xpBoost = 1.00
			if (player.equipment.chestId == 10939)
				xpBoost += 0.008
			if (player.equipment.legsId == 10940)
				xpBoost += 0.006
			if (player.equipment.hatId == 10941)
				xpBoost += 0.004
			if (player.equipment.bootsId == 10933)
				xpBoost += 0.002
			if (player.equipment.chestId == 10939 && player.equipment.legsId == 10940 && player.equipment.hatId == 10941 && player.equipment.bootsId == 10933)
				xpBoost += 0.005
			return xpBoost
		}

		fun giveLog(player: Player, type: TreeType, usingBeaver: Boolean) {
			if (type != TreeType.IVY) {
				if (type.logsId != null) player.incrementCount(ItemDefinitions.getDefs(type.logsId[0]).getName() + " chopped")
			} else player.incrementCount("Choking ivy chopped")
			if (Utils.random(256) == 0) {
				for (rew in DropTable.calculateDrops(player, DropSets.getDropSet("nest_drop"))) World.addGroundItem(rew, WorldTile(player), player, true, 30)
				player.sendMessage("<col=FF0000>A bird's nest falls out of the tree!")
			}
			if (!usingBeaver) player.getSkills().addXp(Constants.WOODCUTTING, type.xp * getLumberjackBonus(player))
			if (player.hasEffect(Effect.JUJU_WOODCUTTING)) {
				val random = Utils.random(100)
				if (random < 11) player.addEffect(Effect.JUJU_WC_BANK, 75)
			}
			if (Utils.random(256) == 0) {
				for (rew in DropTable.calculateDrops(player, DropSets.getDropSet("nest_drop"))) World.addGroundItem(rew, WorldTile(player), player, true, 30)
				player.sendMessage("<col=FF0000>A bird's nest falls out of the tree!")
			}
			//		if (type != TreeType.IVY) {
			//			if (type.getLogsId() != null)
			//				player.incrementCount(ItemDefinitions.getDefs(type.getLogsId()[0]).getName() + " chopped");
			//		} else {
			//			player.incrementCount("Choking ivy chopped");
			//		}
			if (type.logsId != null) {
				if (usingBeaver) {
					if (player.familiar != null) for (item in type.logsId) player.inventory.addItemDrop(item, 1)
				} else if (player.hasEffect(Effect.JUJU_WC_BANK)) {
					for (item in type.logsId) player.bank.addItem(Item(item, 1), true)
					player.setNextSpotAnim(SpotAnim(2897))
				} else for (item in type.logsId) player.inventory.addItemDrop(item, 1)
				if (type == TreeType.FRUIT_TREE) return
				if (type == TreeType.IVY) player.sendMessage("You succesfully cut an ivy vine.", true) else {
					val logName: String = ItemDefinitions.getDefs(type.logsId[0]).getName().lowercase(Locale.getDefault())
					player.sendMessage("You get some $logName.", true)
					if (player.equipment.weaponId == 13661 && type != TreeType.IVY) if (Utils.getRandomInclusive(3) == 0) {
						player.skills.addXp(Constants.FIREMAKING, type.xp * 1)
						player.inventory.deleteItem(type.logsId[0], 1)
						player.sendMessage("The adze's heat instantly incinerates the $logName.")
						player.setNextSpotAnim(SpotAnim(1776))
					}
				}
			}
		}
	}
}