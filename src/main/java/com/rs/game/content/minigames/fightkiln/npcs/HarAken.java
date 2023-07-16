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
package com.rs.game.content.minigames.fightkiln.npcs;

import com.rs.game.content.minigames.fightkiln.FightKilnController;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class HarAken extends NPC {
	
	private static final int TENTACLE_CAP = 25;

	private long time;
	private long spawnTentacleTime;
	private boolean underLava;
	private List<HarAkenTentacle> tentacles;

	private FightKilnController controller;

	public void resetTimer() {
		underLava = !underLava;
		if (time == 0)
			spawnTentacleTime = System.currentTimeMillis() + 9000;
		if(tentacles.size() == TENTACLE_CAP)
			spawnTentacleTime = System.currentTimeMillis() + 25000;
		time = System.currentTimeMillis() + (underLava ? 43000 : 31000);
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public boolean canMove(Direction dir) {
		return false;
	}

	public HarAken(int id, Tile tile, FightKilnController controller) {
		super(id, tile, true);
		setForceMultiArea(true);
		this.controller = controller;
		tentacles = new ArrayList<>();
		setCantFollowUnderCombat(true);
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
			if (time < System.currentTimeMillis())
				if (underLava) {
					controller.showHarAken();
					resetTimer();
				} else
					controller.hideHarAken();
			if (tentacles.size() < TENTACLE_CAP && spawnTentacleTime < System.currentTimeMillis())
				spawnTentacle();

		}
	}

	public void spawnTentacle() {
		tentacles.add(new HarAkenTentacle(Utils.random(2) == 0 ? 15209 : 15210, controller.getTentacleTile(), this));
		spawnTentacleTime = System.currentTimeMillis() + Utils.random(18000, 23000);
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
