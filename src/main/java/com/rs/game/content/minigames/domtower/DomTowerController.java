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
package com.rs.game.content.minigames.domtower;

import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.object.GameObject;

public class DomTowerController extends Controller {

	private transient int mode;

	public DomTowerController(int mode) {
		this.mode = mode;
	}

	@Override
	public void start() {

	}

	public int getMode() {
		return mode;
	}

	private NPC[] bosses;
	private int onArena;

	@Override
	public boolean processObjectClick1(GameObject object) {
		if (object.getId() == 62682 || object.getId() == 62683 || object.getId() == 62690) {
			player.getDominionTower().destroyArena(false, getMode());
			return false;
		}
		if (object.getId() == 62684 || object.getId() == 62685) {
			if (bosses == null && onArena == 0) {
				bosses = player.getDominionTower().createBosses();
				onArena = 1;
				if (bosses.length > 1 || player.getDominionTower().getNextBoss().isForceMulti()) {
					player.setForceMultiArea(true);
					for (NPC n : bosses)
						n.setForceMultiArea(true);
				}
				player.getDominionTower().startFight(bosses);
			}
			return false;
		}
		if (object.getId() == 62686 || object.getId() == 62687) {
			if (bosses == null && onArena == 1) {
				onArena = 2;
				player.lock(2);
				player.stopAll();
				player.addWalkSteps(player.getX() + 1, player.getY(), 1, false);
				player.setForceMultiArea(false);
				return false;
			}
		} else if (object.getId() == 62691) {
			if (getMode() == DominionTower.ENDURANCE) {
				player.sendMessage("You can't bank on endurance mode.");
				return false;
			}
			player.getBank().open();
			return false;
		} else if (object.getId() == 62689) {
			if (player.getDominionTower().getProgress() == 0) {
				player.getDominionTower().openModes();
				return false;
			}
			if (getMode() == DominionTower.ENDURANCE)
				player.getDominionTower().openEnduranceMode();
			else
				player.getDominionTower().openClimberMode();
			return false;
		}
		return true;
	}

	public int getPlayerHPPercentage() {
		return player.getHitpoints() * 100 / player.getMaxHitpoints();
	}

	public int getBossesHPPercentage() {
		int totalHp = 0;
		int totalMaxHp = 0;
		for (NPC n : bosses) {
			totalHp += n.getHitpoints();
			totalMaxHp += n.getMaxHitpoints();
		}
		return totalHp * 100 / totalMaxHp;
	}

	private int playerHp, bossHp;

	@Override
	public boolean sendDeath() {
		if (bosses != null)
			for (NPC n : bosses)
				n.finish();
		bosses = null;
		player.getDominionTower().loss(getMode());
		return false;
	}

	@Override
	public void process() {
		if (bosses != null) {
			if (bossesDead()) {
				bosses = null;
				player.getDominionTower().win(getMode());
				return;
			}
			int playerHp = getPlayerHPPercentage();
			int bossHp = getBossesHPPercentage();

			if (bossHp != this.bossHp) {
				this.bossHp = bossHp;
				player.getPackets().sendVarc(1672, bossHp);
			}
			if (playerHp != this.playerHp) {
				this.playerHp = playerHp;
				player.getPackets().sendVarc(1673, playerHp);
			}
		}
	}

	public boolean bossesDead() {
		for (NPC n : bosses)
			if (!n.hasFinished())
				return false;
		return true;
	}

	@Override
	public void sendInterfaces() {
		if (bosses != null) {
			player.getInterfaceManager().sendOverlay(1159);
			player.getPackets().setIFHidden(1159, 14, true);
			player.getPackets().setIFText(1159, 32, getMode() == DominionTower.CLIMBER ? "Climber" : "Endurance" + ". Floor " + (player.getDominionTower().getProgress() + 1));
			player.getPackets().setIFText(1159, 40, player.getDisplayName());
			player.getPackets().setIFText(1159, 41, player.getDominionTower().getNextBoss().getName());
		}
	}

	@Override
	public void onTeleported(Magic.TeleType type) {
		if (type != Magic.TeleType.OBJECT)
			player.getDominionTower().destroyArena(false, getMode());
	}

	@Override
	public boolean login() {
		if (player.isDead())
			return true;
		player.getDominionTower().selectBoss();
		player.getDominionTower().createArena(getMode());
		return false;
	}

	@Override
	public boolean logout() {
		player.getDominionTower().destroyArena(true, getMode());
		return false;
	}

}
