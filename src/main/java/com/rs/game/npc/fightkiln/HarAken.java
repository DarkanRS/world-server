package com.rs.game.npc.fightkiln;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.player.controllers.FightKilnController;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class HarAken extends NPC {

	private long time;
	private long spawnTentacleTime;
	private boolean underLava;
	private List<HarAkenTentacle> tentacles;

	private FightKilnController controller;

	public void resetTimer() {
		underLava = !underLava;
		if (time == 0)
			spawnTentacleTime = System.currentTimeMillis() + 9000;
		time = System.currentTimeMillis() + (underLava ? 45000 : 30000);
	}
	
	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	public HarAken(int id, WorldTile tile, FightKilnController controller) {
		super(id, tile, true);
		setForceMultiArea(true);
		this.controller = controller;
		tentacles = new ArrayList<HarAkenTentacle>();
	}

	@Override
	public void sendDeath(Entity source) {
		setNextSpotAnim(new SpotAnim(2924 + getSize()));
		if (time != 0) {
			removeTentacles();
			controller.removeNPC();
			time = 0;
		}
		super.sendDeath(source);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		cancelFaceEntityNoCheck();
	}

	public void process() {
		if (isDead())
			return;
		if (time != 0) {
			if (time < System.currentTimeMillis()) {
				if (underLava) {
					controller.showHarAken();
					resetTimer();
				} else
					controller.hideHarAken();
			}
			if (spawnTentacleTime < System.currentTimeMillis())
				spawnTentacle();

		}
	}

	public void spawnTentacle() {
		tentacles.add(new HarAkenTentacle(Utils.random(2) == 0 ? 15209 : 15210, controller.getTentacleTile(), this));
		spawnTentacleTime = System.currentTimeMillis() + Utils.random(15000, 25000);
	}

	public void removeTentacles() {
		for (HarAkenTentacle t : tentacles)
			t.finish();
		tentacles.clear();
	}

	public void removeTentacle(HarAkenTentacle tentacle) {
		tentacles.remove(tentacle);

	}

}
