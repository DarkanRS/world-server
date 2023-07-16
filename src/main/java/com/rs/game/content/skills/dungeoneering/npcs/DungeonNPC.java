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

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.ArrayList;

import static com.rs.game.content.skills.dungeoneering.DungeonConstants.GuardianMonster.*;

public class DungeonNPC extends NPC {

	private DungeonManager manager;
	private boolean marked;

	public DungeonNPC(int id, Tile tile, DungeonManager manager) {
		super(id, tile, true);
		setManager(manager);
		if (getDefinitions().hasAttackOption()) {
			setHitpoints(getMaxHitpoints());
			resetBonuses();
			setForceMultiArea(true);
		}
		setForceAggroDistance(20); //includes whole room
	}

	protected int getTier() {
		int tier = getDefinitions().combatLevel / 11;
		if (tier > 10)
			tier = 11;
		else if (tier < 1)
			tier = 1;
		return tier;
	}

	@Override
	public boolean canAggroPlayer(Player player) {
		DungeonConstants.GuardianMonster mob = DungeonConstants.GuardianMonster.forId(getId());
		if (mob == null)
			return true;
		if (mob == FORGOTTEN_WARRIOR || mob == FORGOTTEN_RANGER || mob == SKELETON_MELEE || mob == SKELETON_RANGED
				|| mob == ZOMBIE_MELEE || mob == ZOMBIE_RANGED || mob == HILL_GIANT || mob == GIANT_SKELETON
				|| mob == HOBGOBLIN || mob == REBORN_WARRIOR || mob == ICE_WARRIOR) {
			if (player.getEquipment().containsOneItem(17279, 15828) && !player.getTempAttribs().getB("ShadowSilkSpellDisable"))
				return false;
		}
		return true;
	}

	public void resetBonuses() {
		setLevels(manager.getLevels(this instanceof DungeonBoss, getCombatLevel(), getMaxHitpoints()));
	}

	/*
	 * they dont respawn anyway, and this way stomp will be fine
	 */
	@Override
	public int getRespawnDirection() {
		return getFaceAngle();
	}

	public NPC getNPC(int id) {
		for (NPC npc : World.getNPCsInChunkRange(getChunkId(), 4)) {
			if (npc.getId() == id)
				return npc;
		}
		return null;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (marked) {
			getManager().removeMark();
			marked = false;
		}
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isUnderCombat()) {
			Entity target = getCombat().getTarget();
			RoomReference thisR = getManager().getCurrentRoomReference(this.getTile());
			RoomReference targetR = getManager().getCurrentRoomReference(target.getTile());
			if (!targetR.equals(thisR))
				getCombat().removeTarget();
		}
	}

	@Override
	public int getMaxHitpoints() {
		return getCombatLevel() * (this instanceof DungeonBoss ? 25 : 8) + 1;
	}

	@Override
	public int getMaxHit() {
		return getCombatLevel();
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	private int getBones() {
		return getName().toLowerCase().contains("dragon") ? 536 : getSize() > 1 ? 532 : 526;
	}

	@Override
	public void drop() {
		int size = getSize();
		ArrayList<Item> drops = new ArrayList<>();
		if (getId() != 10831 && getId() != 10821) //nature & ghost
			drops.add(new Item(getBones()));
		for (int i = 0; i < 1 + Utils.random(10); i++)
			drops.add(new Item(DungeonUtils.getFood(Math.min(1+Utils.random(getTier()), 8))));

		if (Utils.random(10) == 0)
			drops.add(new Item(DungeonUtils.getDagger(Math.min(1+Utils.random(getTier()), 5))));

		if (Utils.random(5) == 0)
			drops.add(new Item(DungeonConstants.RUNES[Utils.random(DungeonConstants.RUNES.length)], 90 + Utils.random(30)));

		if (getManager().getParty().getComplexity() >= 5 && Utils.random(5) == 0) //torm bag, 1
			drops.add(new Item(DungeonUtils.getTornBag(Math.min(1+Utils.random(getTier()), 10))));

		if (getManager().getParty().getComplexity() >= 3 && Utils.random(5) == 0) //ore, up to 10
			drops.add(new Item(DungeonUtils.getOre(Math.min(1+Utils.random(getTier()), 5)), 1 + Utils.random(10)));

		if (getManager().getParty().getComplexity() >= 2 && Utils.random(5) == 0) //branche, up to 10
			drops.add(new Item(DungeonUtils.getBranche(Math.min(1+Utils.random(getTier()), 5)), 1 + Utils.random(10)));

		if (getManager().getParty().getComplexity() >= 4 && Utils.random(5) == 0) //textile, up to 10
			drops.add(new Item(DungeonUtils.getTextile(Math.min(1+Utils.random(getTier()), 5)), 1 + Utils.random(10)));

		if (getManager().getParty().getComplexity() >= 5 && Utils.random(5) == 0) //herb, up to 10
			drops.add(new Item(DungeonUtils.getHerb(Math.min(1+Utils.random(getTier()), 3)), 1 + Utils.random(10)));

		if (getManager().getParty().getComplexity() >= 5 && Utils.random(5) == 0) //seed, up to 10
			drops.add(new Item(DungeonUtils.getSeed(Math.min(1+Utils.random(getTier()), 3)), 1 + Utils.random(10)));

		if (getManager().getParty().getComplexity() >= 5 && Utils.random(3) == 0) //charms, depending in mob size
			drops.add(new Item(DungeonConstants.CHARMS[Utils.random(DungeonConstants.CHARMS.length)], size));

		if (getManager().getParty().getComplexity() >= 2) //coins, 1000 up to 11000
			drops.add(new Item(DungeonConstants.RUSTY_COINS, 1000 + Utils.random(10001)));

		if (getManager().getParty().getComplexity() >= 3 && Utils.random(5) == 0) //essence, 10 up to 300
			drops.add(new Item(DungeonConstants.RUNE_ESSENCE, 10 + Utils.random(300)));
		if (getManager().getParty().getComplexity() >= 2 && Utils.random(5) == 0) //feather, 10 up to 300
			drops.add(new Item(DungeonConstants.FEATHER, 10 + Utils.random(300)));
		if ((getManager().getParty().getComplexity() >= 5 && Utils.random(10) == 0)) //vial, 1
			drops.add(new Item(17490));
		if ((Utils.random(10) == 0)) //anti dragon shield
			drops.add(new Item(16933));
		if ((getManager().getParty().getComplexity() >= 4 && Utils.random(10) == 0)) //bowstring, 1
			drops.add(new Item(17752));
		if ((getManager().getParty().getComplexity() >= 2 && Utils.random(10) == 0)) //fly fishing rod, 1
			drops.add(new Item(17794));
		if ((getManager().getParty().getComplexity() >= 4 && Utils.random(5) == 0)) //thread, 10 up to 300
			drops.add(new Item(17447, 10 + Utils.random(300)));

		for (Item item : drops)
			World.addGroundItem(item, Tile.of(getCoordFaceX(size), getCoordFaceY(size), getPlane()));
	}

	public DungeonManager getManager() {
		return manager;
	}

	public void setManager(DungeonManager manager) {
		this.manager = manager;
	}
}
