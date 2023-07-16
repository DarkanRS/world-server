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
package com.rs.game.content.minigames.clanwars;

import com.rs.game.content.Effect;
import com.rs.game.content.Potions;
import com.rs.game.content.minigames.MinigameUtil;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public final class FFAController extends Controller {
	
	public static ObjectClickHandler handleFFAPortals = new ObjectClickHandler(new Object[] { 38698, 38699 }, e -> {
		WorldTasks.delay(0, () -> {
			e.getPlayer().getVars().setVarBit(5279, e.getObjectId() == 38699 ? 1 : 0);
			if (e.getPlayer().getVars().getVarBit(5294 + e.getPlayer().getVars().getVarBit(5279)) == 1) {
				e.getPlayer().setNextTile(Tile.of(e.getPlayer().getVars().getVarBit(5279) == 1 ? 3007 : 2815, 5511, 0));
				e.getPlayer().getControllerManager().startController(new FFAController(e.getPlayer().getVars().getVarBit(5279) == 1));
				return;
			}
			e.getPlayer().getInterfaceManager().sendInterface(793);
		});
	});
	
	public static ButtonClickHandler confirmOp = new ButtonClickHandler(793, e -> {
		switch(e.getComponentId()) {
		case 9 -> e.getPlayer().getVars().setVarBit(5294 + e.getPlayer().getVars().getVarBit(5279), e.getPlayer().getVars().getVarBit(5294 + e.getPlayer().getVars().getVarBit(5279)) == 1 ? 0 : 1);
		case 14 -> e.getPlayer().closeInterfaces();
		case 15 -> {
			if (e.getPlayer().getVars().getVarBit(5294 + e.getPlayer().getVars().getVarBit(5279)) == 1)
				e.getPlayer().getVars().saveVarBit(5294 + e.getPlayer().getVars().getVarBit(5279), 1);
			e.getPlayer().closeInterfaces();
			e.getPlayer().setNextTile(Tile.of(e.getPlayer().getVars().getVarBit(5279) == 1 ? 3007 : 2815, 5511, 0));
			e.getPlayer().getControllerManager().startController(new FFAController(e.getPlayer().getVars().getVarBit(5279) == 1));
		}
		}
	});

	private transient boolean wasInArea;
	private boolean dangerous;
	
	public FFAController(boolean dangerous) {
		this.dangerous = dangerous;
	}

	@Override
	public void start() {
		if (dangerous) {
			Potions.checkOverloads(player);
			player.addEffect(Effect.OVERLOAD_PVP_REDUCTION, Integer.MAX_VALUE);
		}
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(789);
	}

	@Override
	public boolean sendDeath() {
		player.lock(8);
		player.stopAll();
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				player.setNextAnimation(new Animation(836));
			else if (loop == 1)
				player.sendMessage("Oh dear, you have died.");
			else if (loop == 3) {
				Player killer = player.getMostDamageReceivedSourcePlayer();
				if (killer != null) {
					killer.removeDamage(player);
					killer.increaseKillCount(player);
				}
				if (dangerous) {
					player.sendPVPItemsOnDeath(killer);
					player.getEquipment().init();
					player.getInventory().init();
				}
				player.reset();
				player.setNextTile(Tile.of(2993, 9679, 0));
				remove(true);
				player.setNextAnimation(new Animation(-1));
			} else if (loop == 4) {
				player.jingle(90);
				return false;
			}
			return true;
		});
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		remove(true);
	}

	@Override
	public void forceClose() {
		remove(false);
	}

	private void remove(boolean needRemove) {
		MinigameUtil.checkAndDeleteFoodAndPotions(player);
		if (needRemove)
			removeController();
		if (wasInArea)
			player.setCanPvp(false);
		player.getInterfaceManager().removeOverlay();
		player.removeEffect(Effect.OVERLOAD_PVP_REDUCTION);
	}

	@Override
	public boolean processObjectClick1(GameObject object) {
		switch (object.getId()) {
			case 38700 -> {
				remove(true);
				player.useStairs(-1, Tile.of(2993, 9679, 0), 0, 1);
				return false;
			}
			case 42023 -> MinigameUtil.giveFoodAndPotions(player);
		}
		return true;
	}

	@Override
	public void moved() {
		boolean inArea = inPvpArea(player);
		if (inArea && !wasInArea) {
			player.setCanPvp(true);
			wasInArea = true;
		} else if (!inArea && wasInArea) {
			player.setCanPvp(false);
			wasInArea = false;
		}
	}

	@Override
	public boolean canAttack(Entity target) {
		if (canHit(target))
			return true;
		return false;
	}

	@Override
	public boolean canHit(Entity target) {
		return true;
	}

	private boolean inPvpArea(Player player) {
		return player.getY() >= 5512;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean login() {
		start();
		moved();
		return false;
	}
}