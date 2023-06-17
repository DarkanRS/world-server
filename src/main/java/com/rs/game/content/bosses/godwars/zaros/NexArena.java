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
package com.rs.game.content.bosses.godwars.zaros;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NexArena {

	private static final NexArena GLOBAL_INSTANCE = new NexArena();

	public static NexArena getGlobalInstance() {
		return GLOBAL_INSTANCE;
	}

	private List<Player> players = Collections.synchronizedList(new ArrayList<Player>());
	public Nex nex;
	public NexMinion fumus;
	public NexMinion umbra;
	public NexMinion cruor;
	public NexMinion glacies;

	public int getPlayersCount() {
		return players.size();
	}

	public void breakFumusBarrier() {
		if (fumus == null)
			return;
		fumus.breakBarrier();
	}

	public void breakUmbraBarrier() {
		if (umbra == null)
			return;
		umbra.breakBarrier();
	}

	public void breakCruorBarrier() {
		if (cruor == null)
			return;
		cruor.breakBarrier();
	}

	public void breakGlaciesBarrier() {
		if (glacies == null)
			return;
		glacies.breakBarrier();
	}

	public void addPlayer(Player player) {
		if (players.contains(player))
			return;
		players.add(player);
		startWar();
	}

	public void removePlayer(Player player) {
		players.remove(player);
		cancelWar();
	}

	public void deleteNPCS() {
		if (nex != null) {
			nex.killBloodReavers();
			nex.finish();
			nex = null;
		}
		if (fumus != null) {
			fumus.finish();
			fumus = null;
		}
		if (umbra != null) {
			umbra.finish();
			umbra = null;
		}
		if (cruor != null) {
			cruor.finish();
			cruor = null;
		}
		if (glacies != null) {
			glacies.finish();
			glacies = null;
		}
	}

	private void cancelWar() {
		if (getPlayersCount() == 0)
			deleteNPCS();
	}

	public List<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<>(players.size());
		for (Player player : players) {
			if (player == null || player.isDead() || player.hasFinished() || !player.isRunning())
				continue;
			possibleTarget.add(player);
		}
		return possibleTarget;
	}

	public void moveNextStage() {
		if (nex == null)
			return;
		nex.nextPhase();
	}

	public void endWar() {
		deleteNPCS();
		WorldTasks.schedule(Ticks.fromMinutes(1), () -> {
			try {
				startWar();
			} catch (Throwable e) {
				Logger.handle(NexArena.class, "endWar", e);
			}
		});
	}

	private void startWar() {
		if (getPlayersCount() >= 1)
			if (nex == null) {
				nex = new Nex(this, Tile.of(2924, 5202, 0));
				WorldTasks.schedule(new WorldTask() {
					private int count = 0;

					@Override
					public void run() {
						if (nex == null) {
							stop();
							return;
						}
						if (count == 1) {
							nex.setNextForceTalk(new ForceTalk("AT LAST!"));
							nex.setNextAnimation(new Animation(6355));
							nex.setNextSpotAnim(new SpotAnim(1217));
							nex.voiceEffect(3295);
						} else if (count == 3) {
							fumus = new NexMinion(NexArena.this, 13451, Tile.of(2912, 5216, 0));
							fumus.setFaceAngle(Utils.getAngleTo(1, -1));
							nex.setNextFaceTile(Tile.of(fumus.getCoordFaceX(fumus.getSize()), fumus.getCoordFaceY(fumus.getSize()), 0));
							nex.setNextForceTalk(new ForceTalk("Fumus!"));
							nex.setNextAnimation(new Animation(6987));
							World.sendProjectile(fumus, nex, 2244, 18, 18, 60, 30, 0, 0);
							nex.voiceEffect(3325);
						} else if (count == 5) {
							umbra = new NexMinion(NexArena.this, 13452, Tile.of(2937, 5216, 0));
							umbra.setFaceAngle(Utils.getAngleTo(-1, -1));
							nex.setNextFaceTile(Tile.of(umbra.getCoordFaceX(umbra.getSize()), umbra.getCoordFaceY(umbra.getSize()), 0));
							nex.setNextForceTalk(new ForceTalk("Umbra!"));
							nex.setNextAnimation(new Animation(6987));
							World.sendProjectile(umbra, nex, 2244, 18, 18, 60, 30, 0, 0);
							nex.voiceEffect(3313);
						} else if (count == 7) {
							cruor = new NexMinion(NexArena.this, 13453, Tile.of(2937, 5190, 0));
							cruor.setFaceAngle(Utils.getAngleTo(-1, 1));
							nex.setNextFaceTile(Tile.of(cruor.getCoordFaceX(cruor.getSize()), cruor.getCoordFaceY(cruor.getSize()), 0));
							nex.setNextForceTalk(new ForceTalk("Cruor!"));
							nex.setNextAnimation(new Animation(6987));
							World.sendProjectile(cruor, nex, 2244, 18, 18, 60, 30, 0, 0);
							nex.voiceEffect(3299);
						} else if (count == 9) {
							glacies = new NexMinion(NexArena.this, 13454, Tile.of(2912, 5190, 0));
							glacies.setNextFaceTile(Tile.of(glacies.getCoordFaceX(glacies.getSize()), glacies.getCoordFaceY(glacies.getSize()), 0));
							glacies.setFaceAngle(Utils.getAngleTo(1, 1));
							nex.setNextFaceTile(Tile.of(glacies.getCoordFaceX(glacies.getSize()), glacies.getCoordFaceY(glacies.getSize()), 0));
							nex.setNextForceTalk(new ForceTalk("Glacies!"));
							nex.setNextAnimation(new Animation(6987));
							World.sendProjectile(glacies, nex, 2244, 18, 18, 60, 30, 0, 0);
							nex.voiceEffect(3304);
						} else if (count == 11) {
							nex.setNextForceTalk(new ForceTalk("Fill my soul with smoke!"));
							World.sendProjectile(fumus, nex, 2244, 18, 18, 60, 30, 0, 0);

							nex.voiceEffect(3310);
						} else if (count == 13) {
							nex.setCantInteract(false);
							stop();
							return;
						}
						count++;
					}
				}, 0, 1);
			}
	}

	public List<Player> getPlayers() {
		return players;
	}
}
