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
package com.rs.game.content.skills.dungeoneering.npcs;

import java.util.List;
import java.util.Set;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public final class SkeletalAdventurer extends DungeonBoss {

	public static final int MELEE = 0, RANGE = 1, MAGE = 2;

	private int npcId;

	public SkeletalAdventurer(int type, Tile tile, DungeonManager manager, RoomReference reference) {
		super(type == MELEE ? DungeonUtils.getClosestToCombatLevel(Utils.range(11940, 11984, 3), manager.getBossLevel()) : type == RANGE ? DungeonUtils.getClosestToCombatLevel(Utils.range(12044, 12088, 3), manager.getBossLevel()) : DungeonUtils.getClosestToCombatLevel(Utils.range(11999, 12043, 3), manager.getBossLevel()), tile, manager, reference);
		npcId = getId();
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
		if (Utils.random(15) == 0)
			setNextNPCTransformation(npcId + Utils.random(3));
	}

	@Override
	public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		boolean last = true;
		for (NPC npc : World.getNPCsInChunkRange(getChunkId(), 2)) {
			if (npc == this || npc.isDead() || npc.hasFinished() || !npc.getName().startsWith("Skeletal "))
				continue;
			last = false;
		}
		final boolean l = last;
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				setNextAnimation(new Animation(defs.getDeathEmote()));
			else if (loop >= defs.getDeathDelay()) {
				if (source instanceof Player player)
					player.getControllerManager().processNPCDeath(SkeletalAdventurer.this);
				if (l)
					drop();
				reset();
				finish();
				return false;
			}
			return true;
		});
		if (last)
			getManager().openStairs(getReference());
	}

	@Override
	public int getMaxHit() {
		return super.getMaxHit() * 2;
	}

	public int getPrayer() {
		return getId() - npcId;
	}

	@Override
	public void handlePreHit(Hit hit) {
		if ((hit.getLook() == HitLook.MELEE_DAMAGE && getPrayer() == 0) || (hit.getLook() == HitLook.RANGE_DAMAGE && getPrayer() == 1) || (hit.getLook() == HitLook.MAGIC_DAMAGE && getPrayer() == 2))
			hit.setDamage(0);
		super.handlePreHit(hit);
	}

	@Override
	public void sendDrop(Player player, Item item) {
		List<Player> players = getManager().getParty().getTeam();
		if (players.size() == 0)
			return;
		player.getInventory().addItemDrop(item);
		player.sendMessage("<col=D2691E>You received: " + item.getAmount() + " " + item.getName() + ".");
		for (Player p2 : players) {
			if (p2 == player)
				continue;
			p2.sendMessage("<col=D2691E>" + player.getDisplayName() + " received: " + item.getAmount() + " " + item.getName() + ".");
		}
	}

}
