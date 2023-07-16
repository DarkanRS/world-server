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
package com.rs.game.content.skills.agility.agilitypyramid;

import com.rs.game.World;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.WorldUtil;

import java.util.ArrayList;
import java.util.List;

@PluginEventHandler
public class AgilityPyramidBlock extends NPC {

	private int timer;
	private Tile dangerTile;

	public AgilityPyramidBlock(int id, Tile tile) {
		super(id, tile);
		dangerTile = transform(getId() == 3125 ? 2 : 0, getId() == 3125 ? 0 : 2, 0);
	}

	@Override
	public void processNPC() {
		if (timer-- <= 0) {
			for (Player player : World.getPlayersInChunkRange(getChunkId(), 1))
				player.getVars().setVarBit(1550, getId() == 3125 ? 1 : 3);
			forceMoveVisually(dangerTile, 20, 0);
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
				final Tile tile = p.transform(getId() == 3125 ? dist : 0, getId() == 3125 ? 0 : dist, -1);
				p.forceMove(tile, 10, 30, () -> p.applyHit(new Hit(null, 80, HitLook.TRUE_DAMAGE)));
			}
		if (timer == 4)
			for (Player player : World.getPlayersInChunkRange(getChunkId(), 1))
				player.getVars().setVarBit(1550, 0);
	}

	public List<Player> getHittablePlayers() {
		List<Player> players = new ArrayList<>();
		for (Player player : World.getPlayersInChunkRange(getChunkId(), 1)) {
			if (player.getPlane() != getPlane() || player.isLocked())
				continue;
			if (WorldUtil.collides(player.getTile(), dangerTile, 1, 2))
				players.add(player);
		}
		return players;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 3124, 2125 }, (npcId, tile) -> new AgilityPyramidBlock(npcId, tile));

}
