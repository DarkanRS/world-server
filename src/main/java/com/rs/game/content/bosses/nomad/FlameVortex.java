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
package com.rs.game.content.bosses.nomad;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class FlameVortex extends NPC {

	private long explodeTime;

	public FlameVortex(Tile tile) {
		this(9441, tile, true);
	}

	public FlameVortex(int id, Tile tile, boolean spawned) {
		super(id, tile, spawned);
		explodeTime = System.currentTimeMillis() + 60000;
		setNextAnimation(new Animation(12720));
	}

	@Override
	public void processNPC() {
		if (explodeTime == -1)
			return;
		Player target = getTargetToCheck();
		if (target != null
				&& ((target.getX() == getX() && target.getY() == getY()) || (target.getNextRunDirection() != null && target.getX() - target.getNextRunDirection().getDx() == getX() && target.getY()
				- target.getNextRunDirection().getDy() == getY())))
			explode(target, 400);
		else if (explodeTime < System.currentTimeMillis())
			explode(target != null && withinDistance(target, 1) ? target : null, Utils.random(400, 701));
	}

	public void explode(final Player target, final int damage) {
		explodeTime = -1;
		final NPC npc = this;
		WorldTasks.schedule(new WorldTask() {

			private boolean secondLoop;

			@Override
			public void run() {
				if (!secondLoop) {
					setNextAnimation(new Animation(12722));
					if (target != null) {
						target.applyHit(new Hit(npc, damage, HitLook.TRUE_DAMAGE));
						target.setRunEnergy(0);
						target.setNextForceTalk(new ForceTalk("Aiiiiiieeeee!"));
					}
					secondLoop = true;
				} else {
					finish();
					stop();
				}
			}
		}, 0, 0);
	}

	public Player getTargetToCheck() {
		for (Player player : queryNearbyPlayersByTileRange(0, player -> true))
			return player;
		return null;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(9441, (npcId, tile) -> new FlameVortex(npcId, tile, false));
}
