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
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

public final class GluttonousBehemoth extends DungeonBoss {

	private GameObject heal;
	private int ticks;

	public GluttonousBehemoth(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9948, 9964), manager.getBossLevel()), tile, manager, reference);
		setHitpoints(getMaxHitpoints());
		setCantFollowUnderCombat(true);
	}

	public void setHeal(GameObject food) {
		ticks = 0;
		heal = food;
		removeTarget();
	}

	@Override
	public void processNPC() {
		if (heal != null) {
			setNextFaceEntity(null);
			ticks++;
			if (ticks == 1)
				calcFollow(heal, true);
			else if (ticks == 5)
				setNextAnimation(new Animation(13720));
			else if (ticks < 900 && ticks > 7) {
				if (getHitpoints() >= (getMaxHitpoints() * 0.75)) {
					setNextAnimation(new Animation(-1));
					calcFollow(getRespawnTile(), true);
					ticks = 995;
					return;
				}
				heal(50 + Utils.random(50));
				setNextAnimation(new Animation(13720));
			} else if (ticks > 1000)
				heal = null;
			return;
		}
		super.processNPC();
	}

}
