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
package com.rs.game.content.minigames.sorcgarden;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.rs.game.World;
import com.rs.game.content.transportation.FadingScreen;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Elemental extends NPC {

	private boolean beingTeleported = false;

	private static final WorldTile[][] tiles = { { WorldTile.of(2908, 5460, 0), WorldTile.of(2898, 5460, 0) }, { WorldTile.of(2900, 5448, 0), WorldTile.of(2900, 5455, 0) }, { WorldTile.of(2905, 5449, 0), WorldTile.of(2899, 5449, 0) }, { WorldTile.of(2903, 5451, 0), WorldTile.of(2903, 5455, 0), WorldTile.of(2905, 5455, 0), WorldTile.of(2905, 5451, 0) }, { WorldTile.of(2903, 5457, 0), WorldTile.of(2917, 5457, 0) }, { WorldTile.of(2908, 5455, 0), WorldTile.of(2917, 5455, 0) },
			{ WorldTile.of(2922, 5471, 0), WorldTile.of(2922, 5459, 0) }, { WorldTile.of(2924, 5463, 0), WorldTile.of(2928, 5463, 0), WorldTile.of(2928, 5461, 0), WorldTile.of(2924, 5461, 0) }, { WorldTile.of(2924, 5461, 0), WorldTile.of(2926, 5461, 0), WorldTile.of(2926, 5458, 0), WorldTile.of(2924, 5458, 0) }, { WorldTile.of(2928, 5458, 0), WorldTile.of(2928, 5460, 0), WorldTile.of(2934, 5460, 0), WorldTile.of(2934, 5458, 0) },
			{ WorldTile.of(2931, 5477, 0), WorldTile.of(2931, 5470, 0) }, { WorldTile.of(2935, 5469, 0), WorldTile.of(2928, 5469, 0) }, { WorldTile.of(2925, 5464, 0), WorldTile.of(2925, 5475, 0) }, { WorldTile.of(2931, 5477, 0), WorldTile.of(2931, 5470, 0) }, { WorldTile.of(2907, 5488, 0), WorldTile.of(2907, 5482, 0) }, { WorldTile.of(2907, 5490, 0), WorldTile.of(2907, 5495, 0) }, { WorldTile.of(2910, 5493, 0), WorldTile.of(2910, 5487, 0) },
			{ WorldTile.of(2918, 5483, 0), WorldTile.of(2918, 5485, 0), WorldTile.of(2915, 5485, 0), WorldTile.of(2915, 5483, 0), WorldTile.of(2912, 5483, 0), WorldTile.of(2912, 5485, 0), WorldTile.of(2915, 5485, 0), WorldTile.of(2915, 5483, 0) }, { WorldTile.of(2921, 5486, 0), WorldTile.of(2923, 5486, 0), WorldTile.of(2923, 5490, 0), WorldTile.of(2923, 5486, 0) },
			{ WorldTile.of(2921, 5491, 0), WorldTile.of(2923, 5491, 0), WorldTile.of(2923, 5495, 0), WorldTile.of(2921, 5495, 0) }, { WorldTile.of(2899, 5466, 0), WorldTile.of(2899, 5468, 0), WorldTile.of(2897, 5468, 0), WorldTile.of(2897, 5466, 0), WorldTile.of(2897, 5468, 0), WorldTile.of(2899, 5468, 0) }, { WorldTile.of(2897, 5470, 0), WorldTile.of(2891, 5470, 0) },
			{ WorldTile.of(2897, 5471, 0), WorldTile.of(2899, 5471, 0), WorldTile.of(2899, 5478, 0), WorldTile.of(2897, 5478, 0) }, { WorldTile.of(2896, 5483, 0), WorldTile.of(2900, 5483, 0), WorldTile.of(2900, 5480, 0), WorldTile.of(2897, 5480, 0), WorldTile.of(2896, 5482, 0) }, { WorldTile.of(2896, 5483, 0), WorldTile.of(2896, 5481, 0), WorldTile.of(2891, 5481, 0), WorldTile.of(2891, 5483, 0) }, { WorldTile.of(2889, 5485, 0), WorldTile.of(2900, 5485, 0) } };

	/**
	 *
	 * @param id
	 *            NPC id
	 * @param tile
	 *            WorldTile
	 * @param spawned
	 *            false
	 */
	public Elemental(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setCantFollowUnderCombat(true);
		setCantInteract(true);
	}

	@Override
	public List<Entity> getPossibleTargets() {
		List<Entity> possibleTarget = new ArrayList<>();
		for (int regionId : getMapRegionsIds()) {
			Set<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null)
				for (int playerIndex : playerIndexes) {
					Player player = World.getPlayers().get(playerIndex);
					if (player == null || player.isDead() || player.getAppearance().isHidden() || player.hasFinished() || player.isLocked() || !lineOfSightTo(player, false))
						continue;
					possibleTarget.add(player);
				}
		}
		return possibleTarget;
	}

	private int steps;

	@Override
	public void processNPC() {
		if (!beingTeleported)
			for (Entity t : getPossibleTargets())
				if (withinDistance(t.getTile(), 2) && Utils.getAngleTo(t.getX() - getX(), t.getY() - getY()) == getFaceAngle()) {
					final Player player = (Player) t;
					setNextAnimation(new Animation(5803));
					player.setNextSpotAnim(new SpotAnim(110, 0, 100));
					player.stopAll();
					player.lock();
					player.sendMessage("You've been spotted by an elemental and teleported out of its garden.");
					FadingScreen.fade(player, () -> {
						player.setNextWorldTile(SorceressGardenController.inAutumnGarden(player.getTile()) ? WorldTile.of(2913, 5467, 0) : (SorceressGardenController.inSpringGarden(player.getTile()) ? WorldTile.of(2916, 5473, 0) : (SorceressGardenController.inSummerGarden(player.getTile()) ? WorldTile.of(2910, 5476, 0) : WorldTile.of(2906, 5470, 0))));
						player.lock(1);
						beingTeleported = false;
					});
					break;
				}
		int index = getId() - 5533;
		if (!isForceWalking()) {
			if (steps >= tiles[index].length)
				steps = 0;
			setForceWalk(tiles[index][steps]);
			if (withinDistance(tiles[index][steps], 0))
				steps++;
		}
		super.processNPC();
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(5533, 5534, 5535, 5536, 5537, 5538, 5539, 5540, 5541, 5542, 5543, 5544, 5545, 5546, 5547, 5548, 5549, 5550, 5551, 5552, 5553, 5554, 5555, 5556, 5557, 5558) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Elemental(npcId, tile, false);
		}
	};
}
