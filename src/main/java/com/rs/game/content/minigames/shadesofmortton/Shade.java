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
package com.rs.game.content.minigames.shadesofmortton;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.ClipType;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Shade extends NPC {

	private int baseId;
	private int attack;

	public Shade(int id, Tile tile) {
		super(id, tile);
		baseId = id;
		setForceAggroDistance(15);
		setClipType(ClipType.FLYING);
		setNoDistanceCheck(true);
		attack = 0;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 1240, 1241, 1243, 1244, 1245, 1246, 1247, 1248, 1249, 1250 }, (npcId, tile) -> new Shade(npcId, tile));

	@Override
	public void onRespawn() {
		transformIntoNPC(baseId);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (source instanceof Player player)
			ShadesOfMortton.addSanctity(player, 2.0);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead() || hasFinished())
			return;
		if (!inCombat(10000) && getRegionId() == 13875 && ShadesOfMortton.getRepairState() > 0) {
			if (withinArea(3503, 3313, 3509, 3319)) {
				if (getId() == baseId) {
					transformIntoNPC(baseId+1);
					setNextAnimation(new Animation(1288));
				} else {
					resetWalkSteps();
					if (attack-- <= 0) {
						attack = 5;
						faceTile(Tile.of(3506, 3316, 0));
						setNextAnimation(new Animation(1284));
						TempleWall wall = ShadesOfMortton.getRandomWall();
						if (wall != null)
							wall.decreaseProgress();
					}
				}
				return;
			}
			if (Utils.random(10) == 0)
				calcFollow(Tile.of(Tile.of(3506, 3316, 0), 4), false);
		}
		if (getId() == baseId && inCombat(10000)) {
			transformIntoNPC(baseId + 1);
			setNextAnimation(new Animation(1288));
		} else if (getId() != baseId && !inCombat(10000)) {
			transformIntoNPC(baseId);
			resetHP();
		}
	}

}
