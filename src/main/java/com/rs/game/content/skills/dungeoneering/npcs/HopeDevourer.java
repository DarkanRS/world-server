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

import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

import java.util.ArrayList;
import java.util.List;

public class HopeDevourer extends DungeonBoss {

	private int auraTicks;
	private final int auraDamage;

	public HopeDevourer(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12886, 12900), manager.getBossLevel()), tile, manager, reference);
		setHitpoints(getMaxHitpoints());
		setLureDelay(10000);
		setForceFollowClose(true);
		auraDamage = (int) Utils.random(getMaxHit() * .1, getMaxHit() * .15);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		auraTicks++;
		if (auraTicks == 20) {
			sendAuraAttack();
			auraTicks = 0;
		}
	}

	@Override
	public boolean canMove(Direction dir) {
		int nextX = dir.getDx() + getX();
		int nextY = dir.getDy() + getY();
		int size = getSize(); // I always do this instead of calling at loop cuz it grabs npcdef from hashmap every call
		for (Player player : getManager().getParty().getTeam())
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), nextX, nextY, size))
				return false;
		return true;
	}

	private void sendAuraAttack() {
		for (Entity t : super.getPossibleTargets()) {
			t.applyHit(new Hit(this, auraDamage, HitLook.TRUE_DAMAGE, 60));
			if (t instanceof Player player) {
				int combatSkill = Utils.random(Constants.MAGIC);
				if (combatSkill == 3)
					combatSkill = 1;
				player.getSkills().set(combatSkill, (int) (player.getSkills().getLevel(combatSkill) * Utils.random(0.94, .99)));
				player.sendMessage("You feel hopeless...");
			}
		}
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> targets = super.getPossibleTargets();
		if (getAttackedBy() == null)
			return targets;
		ArrayList<Entity> possibleTargets = new ArrayList<>();
		for (Entity t : targets)
			if (t.inCombat())
				possibleTargets.add(t);
		return possibleTargets;
	}
}
