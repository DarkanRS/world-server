package com.rs.game.npc.glacors;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.TimerBar;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class UnstableMinion extends NPC {

	public Glacor parent;
	public boolean defeated = false;

	public boolean startedTimer = false;
	public boolean healing = false;

	public int gainedHp = 0;

	final int EXPLODE_GFX = 956;

	final NPC thisNpc = this;

	public UnstableMinion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, Glacor parent) {
		super(id, tile, spawned);
		this.parent = parent;
		this.setForceAgressive(true);
		this.setForceMultiAttacked(true);
	}

	@Override
	public void processEntity() {
		super.processEntity();

		if ((this.getHitpoints() < 500) && !isDead() && (getHitpoints() != 0))
			this.heal(10);

		if (!startedTimer && this.getHitpoints() > 500) {
			getNextHitBars().add(new TimerBar(700));
			startedTimer = true;
			startExplosionTimer();
			startStopMovingTimer();
		}

		if (getHitpoints() <= 0 || isDead()) {
			if (!defeated)
				World.sendProjectile(this, parent, 634, 34, 16, 30, 35, 16, 15);
			defeated = true;
		}
	}

	public void startStopMovingTimer() {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (thisNpc.getHitpoints() <= 0 || thisNpc.isDead())
					return;
				thisNpc.freeze(50000000);
			}
		}, 22);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (thisNpc.getHitpoints() <= 0 || thisNpc.isDead())
					return;
				thisNpc.freeze(0);
			}
		}, 26);
	}

	public void startExplosionTimer() {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (thisNpc.getHitpoints() <= 0 || thisNpc.isDead())
					return;
				for (Player player : World.getPlayersInRegionRange(getRegionId())) {
					if (Utils.getDistance(thisNpc.getX(), thisNpc.getY(), player.getX(), player.getY()) < 2)
						player.applyHit(new Hit(player, player.getHitpoints() / 3, HitLook.TRUE_DAMAGE));
				}
				thisNpc.applyHit(new Hit(thisNpc, (int) (thisNpc.getHitpoints() * 0.90), HitLook.TRUE_DAMAGE));
				thisNpc.setNextSpotAnim(new SpotAnim(EXPLODE_GFX));
				startedTimer = false;
			}
		}, 25);
	}

}
