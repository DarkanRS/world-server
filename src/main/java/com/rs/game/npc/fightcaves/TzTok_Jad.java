package com.rs.game.npc.fightcaves;

import com.rs.game.Entity;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.controllers.FightCavesController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class TzTok_Jad extends FightCavesNPC {

	private boolean spawnedMinions;
	private FightCavesController controller;

	public TzTok_Jad(int id, WorldTile tile, FightCavesController controller) {
		super(id, tile);
		this.controller = controller;
		this.setNoDistanceCheck(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (!spawnedMinions && getHitpoints() < getMaxHitpoints() / 2) {
			spawnedMinions = true;
			controller.spawnHealers();
		}
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					setNextSpotAnim(new SpotAnim(2924 + getSize()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					finish();
					controller.win();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

}
