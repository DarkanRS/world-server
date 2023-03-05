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
package com.rs.game.content.holidayevents.christmas;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.interactions.PlayerEntityInteraction;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.PlayerClickHandler;

@PluginEventHandler
public class SnowballInteraction extends PlayerEntityInteraction {

	public static ItemEquipHandler handleSnowballWield = new ItemEquipHandler(11951, e -> e.getPlayer().setPlayerOption(e.equip() ? "Pelt" : "null", 8, true));
	public static PlayerClickHandler handlePelt = new PlayerClickHandler(false, "Pelt", e -> e.getPlayer().getInteractionManager().setInteraction(new SnowballInteraction(e.getTarget())));

	public SnowballInteraction(Entity target) {
		super(target, 7);
	}

	@Override
	public void interact(Player player) {
		player.setNextFaceTile(target.getTile());
		if (player.getInventory().containsItem(11951, 1))
			player.getInventory().deleteItem(11951, 1);
		else {
			player.getEquipment().deleteItem(player.getEquipment().getWeaponId(), 1);
			player.getAppearance().generateAppearanceData();
		}
		if (target instanceof Player)
			player.sendMessage("You pelt " + ((Player)target).getDisplayName() + " with a snowball.");
		player.setNextAnimation(new Animation(7530));
		player.resetWalkSteps();
		WorldTasks.schedule(1, () -> World.sendProjectile(player, target, 861, 6, 10, 0, 1, 20, 0, p -> target.setNextSpotAnim(new SpotAnim(862))));
	}

	@Override
	public boolean canStart(Player player) {
		return true;
	}

	@Override
	public boolean checkAll(Player player) {
		return true;
	}

	@Override
	public void onStop(Player player) {

	}
}
