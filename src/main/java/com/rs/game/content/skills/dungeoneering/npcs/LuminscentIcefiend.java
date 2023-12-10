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
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.LinkedList;
import java.util.List;

public class LuminscentIcefiend extends DungeonBoss {

	private static final byte FIRST_STAGE = 3;
	private static final SpotAnim ICE_SHARDS = new SpotAnim(2525);
	private static final int KNOCKBACK = 10070;

	private final List<Tile> icicles;

	private int specialStage;
	private boolean specialEnabled;

	public LuminscentIcefiend(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9912, 9928), manager.getBossLevel()), tile, manager, reference);
		specialStage = FIRST_STAGE;
		icicles = new LinkedList<>();
	}

	@Override
	public void processNPC() {
		super.processNPC();

		int max_hp = getMaxHitpoints();
		int current_hp = getHitpoints();

		if (current_hp < max_hp * (.25 * specialStage) && !specialEnabled)//75, 50, 25
			prepareSpecial();
	}

	private void prepareSpecial() {
		setNextFaceEntity(null);
		setCapDamage(0);
		specialEnabled = true;
		specialStage--;
	}

	@Override
	public void handlePreHit(Hit hit) {
		int current_hp = getHitpoints();
		if (hit.getDamage() >= current_hp && specialStage == 0 && !specialEnabled) {
			hit.setDamage(current_hp - 1);
			prepareSpecial();
		}
		super.handlePreHit(hit);
	}

	public boolean isSpecialEnabled() {
		return specialEnabled;
	}

	public void commenceSpecial() {
		specialEnabled = false;

		final NPC icefiend = this;
		WorldTasks.schedule(new Task() {

			int count = 0;

			@Override
			public void run() {
				if (count == 21 || isDead()) {
					stop();
					icicles.clear();
					setCapDamage(-1);
					for (Player player : getManager().getParty().getTeam()) {
						player.setCantWalk(false);
						player.getTempAttribs().removeB("FIEND_FLAGGED");
					}
					return;
				}
				count++;

				if (count < 5) {
					if (count == 1)
						for (Entity t : getPossibleTargets()) {
							Player player = (Player) t;
							player.sendMessage("The luminescent ice fiend is encased in ice and cannot be harmed!");
						}
					return;
				}

				for (Entity t : getPossibleTargets()) {
					Player player = (Player) t;
					if (player == null || player.isDead() || player.hasFinished())
						continue;

					Tile currentTile = player.getTempAttribs().getB("FIEND_FLAGGED") ? Tile.of(player.getTile()) : player.getLastTile();
					for (int i = 0; i < icicles.size(); i++) {
						Tile tile = icicles.remove(i);
						player.getPackets().sendSpotAnim(ICE_SHARDS, tile);
						if (player.getTempAttribs().getB("FIEND_FLAGGED") || player.getX() != tile.getX() || player.getY() != tile.getY())
							continue;
						player.getTempAttribs().setB("FIEND_FLAGGED", true);
					}
					icicles.add(currentTile);
				}

				if (count < 5)
					return;

				for (Tile tile : icicles) {
					for (Entity t : getPossibleTargets()) {
						Player player = (Player) t;
						if (!player.getTempAttribs().getB("FIEND_FLAGGED"))
							continue;

						Tile nextTile = World.getFreeTile(player.getTile(), 1);

						if (!player.isCantWalk())
							player.setCantWalk(true);
						if (player.getActionManager().getAction() != null)
							player.getActionManager().forceStop();
						player.forceMove(nextTile, KNOCKBACK, 5, 30);
						int damageCap = (int) (player.getMaxHitpoints() * .10);
						if (player.getHitpoints() < damageCap)// If has 10% of HP.
							continue;
						int damage = Utils.random(20, 100);
						if (player.getHitpoints() - damage <= damageCap)
							damage = damageCap;
						player.applyHit(new Hit(icefiend, damage, HitLook.TRUE_DAMAGE));
					}
				}
			}
		}, 0, 0);
	}
}
