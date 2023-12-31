package com.rs.game.content.world.areas.dungeons;

import com.rs.game.World;
import com.rs.game.content.skills.slayer.npcs.PolyporeNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.ServerStartupEvent
import com.rs.plugin.kts.onItemClick
import com.rs.plugin.kts.onObjectClick
import com.rs.utils.Ticks;

/**
 * interface 893-894 fungal storage
 */

@ServerStartupEvent
fun mapPolypore() {
	data class Vine(val objectId: Int, val x: Int, val y: Int, val targetTile: Tile, val down: Boolean)

	fun useStairs(player: Player, tile: Tile, down: Boolean) {
		player.useStairs(if (down) 15458 else 15456, tile, 2, 3) // TODO find correct emote
		player.tasks.schedule(1) { player.anim(if (down) 15459 else 15457) }
	}

	val vines = listOf(
		Vine(64360, 4629, 5453, Tile.of(4629, 5451, 2), true),
		Vine(64361, 4629, 5452, Tile.of(4629, 5454, 3), false),
		Vine(64359, 4632, 5443, Tile.of(4632, 5443, 1), true),
		Vine(64361, 4632, 5442, Tile.of(4632, 5444, 2), false),
		Vine(64359, 4632, 5409, Tile.of(4632, 5409, 2), true),
		Vine(64361, 4633, 5409, Tile.of(4631, 5409, 3), false),
		Vine(64359, 4642, 5389, Tile.of(4642, 5389, 1), true),
		Vine(64361, 4643, 5389, Tile.of(4641, 5389, 2), false),
		Vine(64359, 4652, 5388, Tile.of(4652, 5388, 0), true),
		Vine(64362, 4652, 5387, Tile.of(4652, 5389, 1), false),
		Vine(64359, 4691, 5469, Tile.of(4691, 5469, 2), true),
		Vine(64361, 4691, 5468, Tile.of(4691, 5470, 3), false),
		Vine(64359, 4689, 5479, Tile.of(4689, 5479, 1), true),
		Vine(64361, 4689, 5480, Tile.of(4689, 5478, 2), false),
		Vine(64359, 4698, 5459, Tile.of(4698, 5459, 2), true),
		Vine(64361, 4699, 5459, Tile.of(4697, 5459, 3), false),
		Vine(64359, 4705, 5460, Tile.of(4704, 5461, 1), true),
		Vine(64361, 4705, 5461, Tile.of(4705, 5459, 2), false),
		Vine(64359, 4718, 5467, Tile.of(4718, 5467, 0), true),
		Vine(64361, 4718, 5466, Tile.of(4718, 5468, 1), false),
		Vine(64360, 4696, 5618, Tile.of(4696, 5618, 2), true),
		Vine(64361, 4696, 5617, Tile.of(4696, 5619, 3), false),
		Vine(64359, 4684, 5586, Tile.of(4684, 5588, 2), true),
		Vine(64361, 4684, 5587, Tile.of(4684, 5585, 3), false),
		Vine(64359, 4699, 5617, Tile.of(4699, 5617, 1), true),
		Vine(64361, 4698, 5617, Tile.of(4700, 5617, 2), false),
		Vine(64359, 4721, 5602, Tile.of(4720, 5601, 1), true),
		Vine(64361, 4720, 5602, Tile.of(4722, 5602, 2), false),
		Vine(64359, 4702, 5612, Tile.of(4702, 5610, 0), true),
		Vine(64361, 4702, 5611, Tile.of(4702, 5613, 1), false)
	).associateBy { Triple(it.objectId, it.x, it.y) }

	onObjectClick(64359, 64360, 64361, 64362) { e ->
		val key = Triple(e.objectId, e.getObject().x, e.getObject().y)
		vines[key]?.let { useStairs(e.player, it.targetTile, it.down) }
	}

	onObjectClick("Neem drupes") { e ->
		if (e.option == "Pick") {
			e.player.inventory.addItemDrop(22445, 1);
			val vb = e.getObject().definitions.varpBit;
			e.player.vars.setVarBit(vb, e.player.vars.getVarBit(vb) + 1);
			e.player.tasks.schedule("neemRespawn$vb", Ticks.fromSeconds(10)) { e.player.vars.setVarBit(vb, 0) }
		}
	}

	onItemClick(22445, options = arrayOf("Squish")) { e ->
		val jugOrOil = e.player.inventory.getItemById(22444) ?: e.player.inventory.getItemById(1935)
		if (jugOrOil == null)
			return@onItemClick e.player.sendMessage("You need a jug to hold your oil.")

		jugOrOil.apply {
			if (id == 1935) {
				id = 22444
				e.player.inventory.refresh(slot)
			}
			val newCharges = e.item.amount + getMetaDataI("neemCharges", 0)
			addMetaData("neemCharges", newCharges)
			e.player.sendMessage("You add ${Utils.formatNumber(e.item.amount)} charges to your jug. It now contains ${Utils.formatNumber(newCharges)} charges.")
		}

		e.player.inventory.deleteItem(e.item)
	}

	onItemClick(22444, options = arrayOf("Sprinkle", "Check")) { e ->
		when(e.option) {
			"Sprinkle" -> {
				val polypores = World.getNPCsInChunkRange(e.player.chunkId, 1)
					.filter { !it.isDead && !it.hasFinished() && Utils.getDistance(it.tile, e.player.tile) <= 5 && it is PolyporeNPC && it.canInfect() }
					.map { it as PolyporeNPC }
				if (polypores.isEmpty()) {
					e.player.sendMessage("There aren't any creatures around that would be affected.");
					return@onItemClick;
				}
				e.player.sync(9954, 2014);
				if (e.item.decMetaDataI("neemCharges") <= 0) {
					e.item.id = 1935;
					e.player.inventory.refresh(e.slotId);
				}
				polypores.forEach { it.neem() }
			}
			"Check" -> e.player.sendMessage("This jug contains " + Utils.formatNumber(e.item.getMetaDataI("neemCharges", 0)) + " ounces of oil.");
		}
	}
}