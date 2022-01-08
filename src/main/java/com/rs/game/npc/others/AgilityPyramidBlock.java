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
package com.rs.game.npc.others;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.ForceMovement;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.WorldUtil;

@PluginEventHandler
public class AgilityPyramidBlock extends NPC {

	private int timer;
	private WorldTile dangerTile;

	public AgilityPyramidBlock(int id, WorldTile tile) {
		super(id, tile);
		dangerTile = transform(getId() == 3125 ? 2 : 0, getId() == 3125 ? 0 : 2, 0);
	}

	@Override
	public void processNPC() {
		if (timer-- <= 0) {
			for (Player player : World.getPlayersInRegionRange(getRegionId()))
				if (player.getPlane() == getPlane())
					player.getVars().setVarBit(1550, getId() == 3125 ? 1 : 3);
			setNextForceMovement(new ForceMovement(dangerTile, 2, getId() == 3125 ? Direction.EAST : Direction.NORTH));
			timer = 10;
		}
		if (timer > 7)
			for (Player p : getHittablePlayers()) {
				int dist = 0;
				if (getId() == 3125)
					dist = p.getX() - dangerTile.getX();
				else
					dist = p.getY() - dangerTile.getY();
				if (dist == 0)
					dist = 2;
				p.lock();
				p.setNextAnimation(new Animation(3066));
				final WorldTile tile = p.transform(getId() == 3125 ? dist : 0, getId() == 3125 ? 0 : dist, 0);
				p.setNextForceMovement(new ForceMovement(tile, dist, getId() == 3125 ? Direction.WEST : Direction.SOUTH));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						p.setNextWorldTile(tile.transform(0, 0, -1));
						p.applyHit(new Hit(null, 80, HitLook.TRUE_DAMAGE));
						p.unlock();
					}
				}, 2);
			}
		if (timer == 4)
			for (Player player : World.getPlayersInRegionRange(getRegionId()))
				if (player.getPlane() == getPlane())
					player.getVars().setVarBit(1550, 0);
	}

	public List<Player> getHittablePlayers() {
		List<Player> players = new ArrayList<>();
		for (Player player : World.getPlayersInRegionRange(getRegionId())) {
			if (player.getPlane() != getPlane() || player.isLocked())
				continue;
			if (WorldUtil.collides(player, dangerTile, 1, 2))
				players.add(player);
		}
		return players;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(3124, 2125) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new AgilityPyramidBlock(npcId, tile);
		}
	};

}
