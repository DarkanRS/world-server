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
package com.rs.game.content.minigames.fightpits;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class TzKekPits extends FightPitsNPC {

	public TzKekPits(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		final WorldTile tile = new WorldTile(getTile());
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0) {
				setNextAnimation(new Animation(defs.getDeathEmote()));
				setNextSpotAnim(new SpotAnim(2924 + getSize()));
			} else if (loop >= defs.getDeathDelay()) {
				reset();
				FightPits.addNPC(new FightPitsNPC(2738, tile));
				if (World.floorAndWallsFree(getPlane(), tile.getX() + 1, tile.getY(), 1))
					tile.moveLocation(1, 0, 0);
				else if (World.floorAndWallsFree(getPlane(), tile.getX() - 1, tile.getY(), 1))
					tile.moveLocation(-1, 0, 0);
				else if (World.floorAndWallsFree(getPlane(), tile.getX(), tile.getY() - 1, 1))
					tile.moveLocation(0, -1, 0);
				else if (World.floorAndWallsFree(getPlane(), tile.getX(), tile.getY() + 1, 1))
					tile.moveLocation(0, 1, 0);
				FightPits.addNPC(new FightPitsNPC(2738, tile));
				finish();
				return false;
			}
			return true;
		});
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		if (hit.getLook() != HitLook.MELEE_DAMAGE || hit.getSource() == null)
			return;
		hit.getSource().applyHit(new Hit(this, 10, HitLook.TRUE_DAMAGE));
	}
}
