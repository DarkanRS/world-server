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
		time = System.currentTimeMillis() + (underLava ? 43000 : 31000);
	}

	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	public HarAken(int id, WorldTile tile, FightKilnController controller) {
		super(id, tile, true);
		setForceMultiArea(true);
		this.controller = controller;
		tentacles = new ArrayList<>();
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
			if (spawnTentacleTime < System.currentTimeMillis())
				spawnTentacle();

		}
	}

	public void spawnTentacle() {
		tentacles.add(new HarAkenTentacle(Utils.random(2) == 0 ? 15209 : 15210, controller.getTentacleTile(), this));
		spawnTentacleTime = System.currentTimeMillis() + Utils.random(20000, 30000);
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
