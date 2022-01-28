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
package com.rs.game.npc.nomad;

import java.util.ArrayList;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.content.transportation.FadingScreen;
import com.rs.game.player.dialogues.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Nomad extends NPC {

	private int nextMove;
	private long nextMovePerform;
	private WorldTile throneTile;
	private ArrayList<NPC> copies;
	private boolean healed;
	private int notAttacked;
	private Player target;

	public Nomad(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setForceMultiArea(true);
		setRun(true);
		setCapDamage(750);
		setForceAggroDistance(5);
		setNextMovePerform();
		setRandomWalk(true);
	}

	public void setTarget(Player player) {
		target = player;
		super.setTarget(player);
	}

	public void setNextMovePerform() {
		nextMovePerform = System.currentTimeMillis() + Utils.random(20000, 30000);
	}

	public boolean isMeleeMode() {
		return nextMove == -1;
	}

	public void setMeleeMode() {
		nextMove = -1;
		setForceFollowClose(true);
	}

	@Override
	public void reset() {
		notAttacked = 0;
		if (nextMove == -1) {
			nextMove = 0;
			setForceFollowClose(false);
		}
		healed = false;
		if (copies != null)
			destroyCopies();
		setNextMovePerform();
		super.reset();

	}

	@Override
	public void sendDeath(Entity source) {
		if (throneTile != null) {
			target.lock();
			target.getVars().setVarBit(6962, 0);
			Dialogue.sendNPCDialogueNoContinue(target, getId(), 9802, "You...<br>You have doomed this world.");
			target.getPackets().sendVoice(8260);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					Dialogue.closeNoContinueDialogue(target);
					FadingScreen.fade(target, () -> {
						target.getControllerManager().forceStop();
						target.unlock();
					});
				}
			}, getAttackSpeed() + 1);
		}
		super.sendDeath(source);
	}

	@Override
	public void processNPC() {
		Entity target = getCombat().getTarget();
		if (target instanceof Player && !lineOfSightTo(target, false)) {
			notAttacked++;
			if (notAttacked == 10) {
				if (copies != null) {
					destroyCopies();
					notAttacked = 0;
					return;
				}
				final Player player = (Player) target;
				setNextForceTalk(new ForceTalk("Face me!"));
				player.getPackets().sendVoice(7992);
			} else if (notAttacked == 20) {
				final Player player = (Player) target;
				setNextForceTalk(new ForceTalk("Coward."));
				player.getPackets().sendVoice(8055);
				reset();
				setNextFaceEntity(null);
				sendTeleport(getThroneTile());
			}
		} else if (target instanceof Familiar && this.target != null)
			super.setTarget(this.target);
		else
			notAttacked = 0;
		super.processNPC();

	}

	// 0 mines
	// 1 wrath
	// 2 multiplies
	// 3 k0 move
	public int getNextMove() {
		if (nextMove == 4)
			nextMove = 0;
		return nextMove++;
	}

	public void sendTeleport(final WorldTile tile) {
		setNextAnimation(new Animation(12729));
		setNextSpotAnim(new SpotAnim(1576));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextWorldTile(tile);
				setNextAnimation(new Animation(12730));
				setNextSpotAnim(new SpotAnim(1577));
				setFaceAngle(6);
			}
		}, 3);
	}

	public boolean useSpecialSpecialMove() {
		return System.currentTimeMillis() > nextMovePerform;
	}

	public void setNextMove(int nextMove) {
		this.nextMove = nextMove;
	}

	public WorldTile getThroneTile() {
		/*
		 * if no throne returns middle of area
		 */
		return throneTile == null ? new WorldTile((getRegionX() << 6) + 32, (getRegionY() << 6) + 32, getPlane()) : throneTile;
	}

	public void setThroneTile(WorldTile throneTile) {
		this.throneTile = throneTile;
	}

	public void createCopies(final Player target) {
		setNextAnimation(new Animation(12729));
		setNextSpotAnim(new SpotAnim(1576));
		final int thisIndex = Utils.random(4);
		final Nomad thisNpc = this;
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				copies = new ArrayList<>();
				transformIntoNPC(8529);
				for (int i = 0; i < 4; i++) {
					NPC n;
					if (thisIndex == i) {
						n = thisNpc;
						setNextWorldTile(getCopySpot(i));
					} else {
						n = new FakeNomad(getCopySpot(i), thisNpc);
						copies.add(n);
					}
					n.setCantFollowUnderCombat(true);
					n.setNextAnimation(new Animation(12730));
					n.setNextSpotAnim(new SpotAnim(1577));
					n.setTarget(target);
				}

			}
		}, 3);
	}

	public WorldTile getCopySpot(int index) {
		WorldTile throneTile = getThroneTile();
		switch (index) {
		case 0:
			return throneTile;
		case 1:
			return throneTile.transform(-3, -3, 0);
		case 2:
			return throneTile.transform(3, -3, 0);
		case 3:
		default:
			return throneTile.transform(0, -6, 0);
		}

	}

	public void destroyCopy(NPC copy) {
		copy.finish();
		if (copies == null)
			return;
		copies.remove(copy);
		if (copies.isEmpty())
			destroyCopies();
	}

	public void destroyCopies() {
		transformIntoNPC(8528);
		setCantFollowUnderCombat(false);
		setNextMovePerform();
		if (copies == null)
			return;
		for (NPC n : copies)
			n.finish();
		copies = null;
	}

	@Override
	public void handlePreHit(Hit hit) {
		if (getId() == 8529)
			destroyCopies();
		super.handlePreHit(hit);
	}

	public boolean isHealed() {
		return healed;
	}

	public void setHealed(boolean healed) {
		this.healed = healed;
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(8528, 8529, 8530, 8531, 8532) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Nomad(npcId, tile, false);
		}
	};
}
