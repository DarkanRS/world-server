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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.dungeoneering;

import com.rs.game.Hit;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class WarpedGulega extends DungeonBoss {

	public WarpedGulega(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12737, 12751), manager.getBossLevel()), tile, manager, reference);
	}

	//thats default lol
	/* @Override
	 public double getMeleePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }

	 @Override
	 public double getRangePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }

	 @Override
	 public double getMagePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }*/

	@Override
	public void processHit(Hit hit) {
		if (!(hit.getSource() instanceof Familiar))
			hit.setDamage((int) (hit.getDamage() * 0.45D));
		super.processHit(hit);
	}
}
