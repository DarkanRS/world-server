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
package com.rs.game.content.skills.hunter.falconry;

import com.rs.game.content.skills.hunter.falconry.Kebbit.KebbitType;
import com.rs.game.model.entity.interactions.StandardEntityInteraction;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.Arrays;

@PluginEventHandler
public class FalconryController extends Controller {
	
	public static final int BIRD_GLOVE = 10024, EMPTY_GLOVE = 10023;

	public static NPCClickHandler catchKebbit = new NPCClickHandler(false, Arrays.stream(KebbitType.values()).map(k -> k.kebbitId).toArray(), e -> {
		if (!e.getPlayer().getControllerManager().isIn(FalconryController.class)) {
			e.getPlayer().sendMessage("I should speak to Matthias about using my own falcon before catching these.");
			return;
		}
		if (!(e.getNPC() instanceof Kebbit kebbit)) {
			e.getPlayer().sendMessage("This shouldn't have happened. Report it as a bug please.");
			return;
		}
		e.getPlayer().getInteractionManager().setInteraction(new StandardEntityInteraction(e.getNPC(), 8, () -> kebbit.sendFalcon(e.getPlayer())));
	});

	public static NPCClickHandler lootKebbit = new NPCClickHandler(Arrays.stream(KebbitType.values()).map(k -> k.caughtId).toArray(), e -> {
		if (!e.getPlayer().getControllerManager().isIn(FalconryController.class)) {
			e.getPlayer().sendMessage("I should speak to Matthias about using my own falcon before catching these.");
			return;
		}
		if (!(e.getNPC() instanceof Kebbit kebbit)) {
			e.getPlayer().sendMessage("This shouldn't have happened. Report it as a bug please.");
			return;
		}
		kebbit.loot(e.getPlayer());
	});

	@Override
	public void start() {
		player.getEquipment().setNoPluginTrigger(Equipment.WEAPON, new Item(BIRD_GLOVE, 1));
		player.getEquipment().refresh(Equipment.WEAPON);
		player.getAppearance().generateAppearanceData();
		player.simpleDialogue("Simply click on the target and try your luck.");
	}

	public static ItemEquipHandler handleFalconersGlove = new ItemEquipHandler(new Object[] { BIRD_GLOVE, EMPTY_GLOVE }, e -> {
		if (e.equip()) {
			e.cancel();
			e.getPlayer().getInventory().deleteItem(e.getItem());
			return;
		}
		e.getPlayer().getPackets().sendPlayerMessage(0, 0xFF0000, "You should speak to Matthias to get this removed safely.");
		e.cancel();
	});
	
	@Override
	public boolean login() {
		return false;
	}
	
	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public void forceClose() {
		if (player.getEquipment().containsOneItem(BIRD_GLOVE, EMPTY_GLOVE))
			player.getEquipment().setNoPluginTrigger(Equipment.WEAPON, null);
		player.getEquipment().refresh(Equipment.WEAPON);
		player.getAppearance().generateAppearanceData();
	}

	public static ObjectClickHandler enterArea = new ObjectClickHandler(new Object[] { 19222 }, e -> {
		if (e.getPlayer().getControllerManager().isIn(FalconryController.class)) {
			e.getPlayer().sendOptionDialogue("Are you sure you would like to leave?", ops -> {
				ops.add("Yes", () -> {
					e.getPlayer().getControllerManager().forceStop();
					e.getPlayer().lock(2);
					e.getPlayer().setNextAnimation(new Animation(1560));
					WorldTasks.schedule(() -> e.getPlayer().setNextTile(e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -2 : 2)));
				});
				ops.add("No");
			});
			return;
		}
		e.getPlayer().lock(2);
		e.getPlayer().setNextAnimation(new Animation(1560));
		WorldTasks.schedule(() -> e.getPlayer().setNextTile(e.getPlayer().transform(0, e.getPlayer().getY() > e.getObject().getY() ? -2 : 2)));
	});

	public static void beginFalconry(Player player) {
		if (player.getEquipment().hasItemInSlot(Equipment.WEAPON, Equipment.SHIELD)) {
			player.simpleDialogue("You need both hands free to use a falcon.");
			return;
		}
		if (player.getSkills().getLevel(Constants.HUNTER) < 43) {
			player.simpleDialogue("You need a Hunter level of at least 43 to use a falcon, come back later.");
			return;
		}
		player.sendOptionDialogue("Pay 500 coins to borrow a falcon?", ops -> {
			ops.add("Yes", () -> {
				if (!player.getInventory().hasCoins(500)) {
					player.sendMessage("You need 500 coins to borrow a falcon.");
					return;
				}
				player.getInventory().removeCoins(500);
				player.getControllerManager().startController(new FalconryController());
			});
			ops.add("No");
		});
	}
}
