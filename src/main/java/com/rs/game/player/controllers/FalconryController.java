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
package com.rs.game.player.controllers;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.FlyingEntityHunter;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class FalconryController extends Controller {

	public int[] xp = { 103, 132, 156 };
	public int[] furRewards = { 10125, 10115, 10127 };
	public int[] levels = { 43, 57, 69 };

	public static void beginFalconry(Player player) {
		if (!player.hasRights(Rights.DEVELOPER)) {
			player.sendMessage("Falconry is temporarily closed.");
			return;
		}
		if ((player.getEquipment().getItem(3) != null && player.getEquipment().getItem(3).getId() == -1) || (player.getEquipment().getItem(5) != null && player.getEquipment().getItem(5).getId() == -1)) {
			player.getDialogueManager().execute(new SimpleMessage(), "You need both hands free to use a falcon.");
			return;
		}
		if (player.getSkills().getLevel(Constants.HUNTER) < 43) {
			player.getDialogueManager().execute(new SimpleMessage(), "You need a Hunter level of at least 43 to use a falcon, come back later.");
			return;
		}
		player.getControllerManager().startController(new FalconryController());
	}

	@Override
	public void start() {
		player.setNextAnimation(new Animation(1560));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextWorldTile(new WorldTile(2371, 3619, 0));
			}
		});
		player.getEquipment().set(3, new Item(10024, 1));
		player.getEquipment().refresh(3);
		player.getAppearance().generateAppearanceData();
		player.getDialogueManager().execute(new SimpleMessage(), "Simply click on the target and try your luck.");
	}

	@Override
	public boolean canEquip(int slotId, int itemId) {
		if (slotId == 3 || slotId == 5)
			return false;
		return true;
	}

	@Override
	public void magicTeleported(int type) {
		player.getControllerManager().forceStop();
	}

	@Override
	public void forceClose() {
		player.getEquipment().set(3, new Item(-1, 1));
		player.getEquipment().refresh(3);
		player.getInventory().deleteItem(10024, Integer.MAX_VALUE);
		player.getAppearance().generateAppearanceData();
	}

	@Override
	public boolean processNPCClick1(final NPC npc) {
		player.setNextFaceEntity(npc);
		if (npc.getDefinitions().getName().toLowerCase().contains("kebbit")) {
			if (player.getTempAttribs().getB("falconReleased")) {
				player.getDialogueManager().execute(new SimpleMessage(), "You cannot catch a kebbit without your falcon.");
				return false;
			}
			int level = levels[(npc.getId() - 5098)];
			if (proccessFalconAttack(npc))
				if (player.getSkills().getLevel(Constants.HUNTER) < level) {
					player.getDialogueManager().execute(new SimpleMessage(), "You need a Hunter level of " + level + " to capture this kebbit.");
					return true;
				} else if (FlyingEntityHunter.isSuccessful(player, level, player -> {
					if (player.getEquipment().getGlovesId() == 10075)
						return 3;
					return 1;
				})) {
					player.getEquipment().set(3, new Item(10023, 1));
					player.getEquipment().refresh(3);
					player.getAppearance().generateAppearanceData();
					player.getTempAttribs().setB("falconReleased", true);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							World.sendProjectile(player, npc, 918, 41, 16, 31, 35, 16, 0);
							WorldTasks.schedule(new WorldTask() {
								@Override
								public void run() {
									npc.transformIntoNPC(npc.getId() - 4);
									player.getTempAttribs().setO("ownedFalcon", npc);
									player.sendMessage("The falcon successfully swoops down and captures the kebbit.");
									player.getHintIconsManager().addHintIcon(npc, 1, -1, false);
								}
							});
						}
					});
				} else {
					player.getEquipment().set(3, new Item(10023, 1));
					player.getEquipment().refresh(3);
					player.getAppearance().generateAppearanceData();
					player.getTempAttribs().setB("falconReleased", true);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							World.sendProjectile(player, npc, 918, 41, 16, 31, 35, 16, 0);
							WorldTasks.schedule(new WorldTask() {
								@Override
								public void run() {
									World.sendProjectile(npc, player, 918, 41, 16, 31, 35, 16, 0);
									WorldTasks.schedule(new WorldTask() {
										@Override
										public void run() {
											player.getEquipment().set(3, new Item(10024, 1));
											player.getEquipment().refresh(3);
											player.getAppearance().generateAppearanceData();
											player.getTempAttribs().removeB("falconReleased");
											player.sendMessage("The falcon swoops down on the kebbit, but just barely misses catching it.");
										}
									});
								}
							}, Utils.getDistance(player, npc) > 3 ? 2 : 1);
						}
					});
				}
			return false;
		}
		if (npc.getDefinitions().getName().toLowerCase().contains("gyr falcon")) {
			NPC kill = player.getTempAttribs().getO("ownedFalcon");
			if (kill == null)
				return false;
			if (kill != npc) {
				player.getDialogueManager().execute(new SimpleMessage(), "This isn't your kill!");
				return false;
			}
			npc.transformIntoNPC(npc.getId() + 4);
			npc.setRespawnTask();
			// player.getInventory().addItem(new Item(furRewards[(npc.getId() -
			// 5094)], 1));
			player.getInventory().addItem(new Item(526, 1));
			// player.getSkills().addXp(Constants.HUNTER, xp[(npc.getId() -
			// 5094)]);
			player.sendMessage("You retreive the falcon as well as the fur of the dead kebbit.");
			player.getHintIconsManager().removeUnsavedHintIcon();
			player.getEquipment().set(3, new Item(10024, 1));
			player.getEquipment().refresh(3);
			player.getAppearance().generateAppearanceData();
			player.getTempAttribs().removeO("ownedFalcon");
			player.getTempAttribs().removeB("falconReleased");
			return false;
		}
		return true;
	}

	private boolean proccessFalconAttack(NPC target) {
		int distanceX = player.getX() - target.getX();
		int distanceY = player.getY() - target.getY();
		int size = player.getSize();
		int maxDistance = 16;
		player.resetWalkSteps();
		if ((!player.lineOfSightTo(target, maxDistance == 0)) || distanceX > size + maxDistance || distanceX < -1 - maxDistance || distanceY > size + maxDistance || distanceY < -1 - maxDistance)
			if (!player.calcFollow(target, 2, true, true))
				return true;
		return true;
	}
}
