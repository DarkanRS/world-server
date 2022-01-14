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
package com.rs.game.player.content.minigames.shadesofmortton;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Areas;

@PluginEventHandler
public class ShadesOfMortton {

	private static Map<Integer, TempleWall> WALLS = new ConcurrentHashMap<>();
	private static int REPAIR_STATE = 0;

	public static int getRepairState() {
		return REPAIR_STATE;
	}

	public static TempleWall getWall(GameObject obj) {
		TempleWall wall = WALLS.get(obj.getTileHash());
		if (wall == null)
			wall = new TempleWall(obj);
		return wall;
	}

	public static TempleWall getRandomWall() {
		if (WALLS.isEmpty())
			return null;
		return WALLS.get(WALLS.keySet().toArray()[Utils.random(WALLS.size())]);
	}

	public static void addWall(TempleWall wall) {
		WALLS.put(wall.getTileHash(), wall);
		World.spawnObject(wall);
	}

	public static void deleteWall(TempleWall wall) {
		WALLS.remove(wall.getTileHash());
		World.removeObject(wall);
	}

	@ServerStartupEvent
	public static void initUpdateTask() {
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				updateRepairState();
				for (Player player : World.getPlayersInRegion(13875)) {
					if (!player.hasStarted() || player.hasFinished())
						continue;
					removeSanctity(player, 1);
				}
				GameObject altar = World.getObject(new WorldTile(3506, 3316, 0));
				if (REPAIR_STATE >= 99) {
					if (altar != null && altar.getId() == 4092) {
						World.spawnObject(new GameObject(altar).setId(4091));
						World.sendSpotAnim(null, new SpotAnim(1605), altar);
					} else if (altar != null && altar.getId() == 4090 && Utils.random(2) == 0) {
						altar.setId(4091);
						World.sendSpotAnim(null, new SpotAnim(1605), altar);
					}
				} else if (altar != null && altar.getId() != 4092) {
					World.removeObject(altar);
					World.sendSpotAnim(null, new SpotAnim(1605), altar);
				}
			}
		}, 50, 50);
	}

	protected static void updateRepairState() {
		int totalRepair = 0;
		for (TempleWall wall : WALLS.values())
			totalRepair += wall.getRepairPerc();
		REPAIR_STATE = (int) ((totalRepair / 1500.0) * 100.0);
	}

	protected static void updateVars(Player player) {
		player.getVars().setVar(343, REPAIR_STATE);
		player.getVars().setVar(344, player.getI("shadeResources", 0));
		player.getVars().setVar(345, (int) Math.ceil(player.getTempAttribs().getD("shadeSanctity")));
	}

	public static void addSanctity(Player player, double amount) {
		player.getTempAttribs().setD("shadeSanctity", Utils.clampD(player.getTempAttribs().getD("shadeSanctity") + amount, 0, 100));
		updateVars(player);
	}

	public static void addResources(Player player, int amount) {
		player.save("shadeResources", Utils.clampI(player.getI("shadeResources", 0) + amount, 0, 100));
		updateVars(player);
	}

	public static void removeSanctity(Player player, double amount) {
		player.getTempAttribs().setD("shadeSanctity", Utils.clampD(player.getTempAttribs().getD("shadeSanctity") - amount, 0, 100));
		updateVars(player);
	}

	public static void removeResources(Player player, int amount) {
		player.save("shadeResources", Utils.clampI(player.getI("shadeResources", 0) - amount, 0, 100));
		updateVars(player);
	}

	public static EnterChunkHandler handleTempleChunks = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (!(e.getEntity() instanceof Player))
				return;
			boolean wasIn = e.getEntity().getTempAttribs().getB("inShadeTemple");
			if (wasIn) {
				if (!Areas.withinArea("shades_temple", e.getChunkId())) {
					Player player = e.getPlayer();
					if (player != null && player.hasStarted()) {
						player.getInterfaceManager().removeOverlay();
						e.getEntity().getTempAttribs().setB("inShadeTemple", false);
						updateVars(player);
					}
				}
			} else if (Areas.withinArea("shades_temple", e.getChunkId())) {
				Player player = e.getPlayer();
				if (player != null && player.hasStarted()) {
					player.getInterfaceManager().setOverlay(328);
					e.getEntity().getTempAttribs().setB("inShadeTemple", true);
					updateVars(player);
				}
			}
		}
	};

	public static ItemOnObjectHandler handleOilOnAltar = new ItemOnObjectHandler(new Object[] { 4090 }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getPlayer().getTempAttribs().getD("shadeSanctity") < 10) {
				e.getPlayer().sendMessage("You need more sanctity to bless an item.");
				return;
			}
			switch(e.getItem().getId()) {
			//olive oils
			case 3422:
				e.getItem().setId(3430);
				removeSanctity(e.getPlayer(), 3.6);
				break;
			case 3424:
				e.getItem().setId(3432);
				removeSanctity(e.getPlayer(), 2.7);
				break;
			case 3426:
				e.getItem().setId(3434);
				removeSanctity(e.getPlayer(), 1.8);
				break;
			case 3428:
				e.getItem().setId(3436);
				removeSanctity(e.getPlayer(), 0.9);
				break;
				//serums
			case 3408:
				e.getItem().setId(3416);
				removeSanctity(e.getPlayer(), 3.6);
				break;
			case 3410:
				e.getItem().setId(3417);
				removeSanctity(e.getPlayer(), 2.7);
				break;
			case 3412:
				e.getItem().setId(3418);
				removeSanctity(e.getPlayer(), 1.8);
				break;
			case 3414:
				e.getItem().setId(3419);
				removeSanctity(e.getPlayer(), 0.9);
				break;
			}
			updateVars(e.getPlayer());
			e.getPlayer().getInventory().refresh(e.getItem().getSlot());
		}
	};

	public static ObjectClickHandler handleLightAltar = new ObjectClickHandler(new Object[] { 4091 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getTempAttribs().getD("shadeSanctity") < 10) {
				e.getPlayer().sendMessage("You don't have enough sanctity to light the altar!");
				return;
			}
			if (!e.getPlayer().getInventory().containsItem(590)) {
				e.getPlayer().sendMessage("You need a tinderbox to do that.");
				return;
			}
			GameObject altar = World.getObject(new WorldTile(3506, 3316, 0));
			if (altar.getId() == 4091) {
				altar.setId(4090);
				e.getPlayer().setNextAnimation(new Animation(3687));
				e.getPlayer().getSkills().addXp(Constants.FIREMAKING, 100);
			}
		}
	};

	public static ObjectClickHandler handleWallRepairs = new ObjectClickHandler(new Object[] { 4068, 4069, 4070, 4071, 4072, 4073, 4074, 4075, 4076, 4077, 4078, 4079, 4080, 4081, 4082, 4083, 4084, 4085, 4086, 4087, 4088, 4089 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().getActionManager().setAction(new Action() {

				@Override
				public boolean start(Player player) {
					player.getActionManager().setActionDelay(4);
					return true;
				}

				@Override
				public boolean process(Player player) {
					boolean inside = player.getX() >= 3505 && player.getX() <= 3507 && player.getY() >= 3315 && player.getY() <= 3317;
					int anim = player.getInventory().containsItem(3678) ? inside ? 8861 : 8890 : inside ? 8865 : 8888;
					if (getWall(e.getObject()).getRepairPerc() > 50)
						anim = player.getInventory().containsItem(3678) ? 8950 : 8893;
					player.setNextAnimation(new Animation(anim));
					return true;
				}

				@Override
				public int processWithDelay(Player player) {
					player.faceTile(e.getObject());
					if (player.getI("shadeResources", 0) <= 95 && player.getInventory().containsItem(8837) && player.getInventory().containsItem(3420) && player.getInventory().containsItem(1941, 5)) {
						player.getInventory().deleteItem(8837, 1);
						player.getInventory().deleteItem(3420, 1);
						player.getInventory().deleteItem(1941, 5);
						addResources(player, 5);
					}
					if (player.getI("shadeResources", 0) <= 0) {
						player.sendMessage("You have run out of resources!");
						return -1;
					}
					if (Utils.random(player.getInventory().containsItem(3678) ? 2 : 6) != 0) {
						player.getSkills().addXp(Constants.CRAFTING, Utils.random(5, 9));
						return 4;
					}
					TempleWall wall = WALLS.get(e.getObject().getTileHash());
					if (wall == null)
						wall = new TempleWall(e.getObject());
					wall.increaseProgress();
					removeResources(player, 1);
					addSanctity(player, 5);
					player.getSkills().addXp(Constants.CRAFTING, Utils.random(20, 35));
					return 4;
				}

				@Override
				public void stop(Player player) {
					player.setNextAnimation(new Animation(-1));
				}

			});
		}
	};

	public static ItemOnItemHandler handleNecromancerKits = new ItemOnItemHandler(new int[] { 21489 }, new int[] { 14497, 14499, 14501 }) {
		@Override
		public void handle(ItemOnItemEvent e) {
			if (e.getPlayer().getSkills().getLevel(Constants.CRAFTING) < 85) {
				e.getPlayer().sendMessage("You need a Crafting level of 85 to attach the necromancer kit.");
				return;
			}
			if (!e.getPlayer().getInventory().containsItem(3470, 10)) {
				e.getPlayer().sendMessage("You need 10 fine cloth to attach the necromancer kit.");
				return;
			}
			switch(e.getUsedWith(21489).getId()) {
			case 14497:
				e.getUsedWith(21489).setId(21477);
				break;
			case 14499:
				e.getUsedWith(21489).setId(21478);
				break;
			case 14501:
				e.getUsedWith(21489).setId(21479);
				break;
			}
			e.getPlayer().getInventory().deleteItem(21489, 1);
			e.getPlayer().getInventory().deleteItem(3470, 10);
			e.getPlayer().getSkills().addXp(Constants.CRAFTING, 150);
			e.getPlayer().getInventory().refresh();
		}
	};

	public static ItemClickHandler handleRemoveNecromancerKits = new ItemClickHandler(new Object[] { 21477, 21478, 21479 }, new String[] { "Remove-kit" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You don't have enough inventory space!");
				return;
			}
			e.getPlayer().sendOptionDialogue("Would you like to remove the kit? You will not recover the fine cloth.", new String[] { "Yes", "Nevermind" }, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (option == 1) {
						switch(e.getItem().getId()) {
						case 21477:
							e.getItem().setId(14497);
							break;
						case 21478:
							e.getItem().setId(14499);
							break;
						case 21479:
							e.getItem().setId(14501);
							break;
						}
						e.getPlayer().getInventory().addItemDrop(21489, 1);
						e.getPlayer().getInventory().refresh();
					}
				}
			});
		}
	};

	public static ItemOnItemHandler handleShadeSkulls = new ItemOnItemHandler(21488, new int[] { 1381, 1383, 1385, 1387, 1393, 1395, 1397, 1399, 1401, 1403, 1405, 1407, 3053, 3054, 6562, 6563, 11736, 11738 }) {
		@Override
		public void handle(ItemOnItemEvent e) {
			if (e.getPlayer().getSkills().getLevel(Constants.CRAFTING) < 85) {
				e.getPlayer().sendMessage("You need a Crafting level of 85 to attach the skull.");
				return;
			}
			switch(e.getUsedWith(21488).getId()) {
			case 1381: //Skeletal staff of air
				e.getUsedWith(21488).setId(21490);
				break;
			case 1383: //Skeletal staff of water
				e.getUsedWith(21488).setId(21491);
				break;
			case 1385: //Skeletal staff of earth
				e.getUsedWith(21488).setId(21492);
				break;
			case 1387: //Skeletal staff of fire
				e.getUsedWith(21488).setId(21493);
				break;
			case 1393: //Skeletal battlestaff of fire
				e.getUsedWith(21488).setId(21494);
				break;
			case 1395: //Skeletal battlestaff of water
				e.getUsedWith(21488).setId(21495);
				break;
			case 1397: //Skeletal battlestaff of air
				e.getUsedWith(21488).setId(21496);
				break;
			case 1399: //Skeletal battlestaff of earth
				e.getUsedWith(21488).setId(21497);
				break;
			case 1401: //Necromancer's fire staff
				e.getUsedWith(21488).setId(21498);
				break;
			case 1403: //Necromancer's water staff
				e.getUsedWith(21488).setId(21499);
				break;
			case 1405: //Necromancer's air staff
				e.getUsedWith(21488).setId(21500);
				break;
			case 1407: //Necromancer's earth staff
				e.getUsedWith(21488).setId(21501);
				break;
			case 3053: //Skeletal lava battlestaff
				e.getUsedWith(21488).setId(21502);
				break;
			case 3054: //Necromancer's lava staff
				e.getUsedWith(21488).setId(21503);
				break;
			case 6562: //Skeletal mud battlestaff
				e.getUsedWith(21488).setId(21504);
				break;
			case 6563: //Necromancer's mud staff
				e.getUsedWith(21488).setId(21505);
				break;
			case 11736: //Skeletal steam battlestaff
				e.getUsedWith(21488).setId(21506);
				break;
			case 11738: //Necromancer's steam staff
				e.getUsedWith(21488).setId(21507);
				break;
			}
			e.getPlayer().getInventory().deleteItem(21488, 1);
			e.getPlayer().getInventory().refresh();
		}
	};

	public static ItemClickHandler handleRemoveShadeSkulls = new ItemClickHandler(Utils.range(21490, 21507), new String[] { "Remove-skull" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("You don't have enough inventory space!");
				return;
			}
			e.getPlayer().sendOptionDialogue("Would you like to remove the skull?", new String[] { "Yes", "Nevermind" }, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (option == 1) {
						switch(e.getItem().getId()) {
						case 21490: //Skeletal staff of air
							e.getItem().setId(1381);
							break;
						case 21491: //Skeletal staff of water
							e.getItem().setId(1383);
							break;
						case 21492: //Skeletal staff of earth
							e.getItem().setId(1385);
							break;
						case 21493: //Skeletal staff of fire
							e.getItem().setId(1387);
							break;
						case 21494: //Skeletal battlestaff of fire
							e.getItem().setId(1393);
							break;
						case 21495: //Skeletal battlestaff of water
							e.getItem().setId(1395);
							break;
						case 21496: //Skeletal battlestaff of air
							e.getItem().setId(1397);
							break;
						case 21497: //Skeletal battlestaff of earth
							e.getItem().setId(1399);
							break;
						case 21498: //Necromancer's fire staff
							e.getItem().setId(1401);
							break;
						case 21499: //Necromancer's water staff
							e.getItem().setId(1403);
							break;
						case 21500: //Necromancer's air staff
							e.getItem().setId(1405);
							break;
						case 21501: //Necromancer's earth staff
							e.getItem().setId(1407);
							break;
						case 21502: //Skeletal lava battlestaff
							e.getItem().setId(3053);
							break;
						case 21503: //Necromancer's lava staff
							e.getItem().setId(3054);
							break;
						case 21504: //Skeletal mud battlestaff
							e.getItem().setId(6562);
							break;
						case 21505: //Necromancer's mud staff
							e.getItem().setId(6563);
							break;
						case 21506: //Skeletal steam battlestaff
							e.getItem().setId(11736);
							break;
						case 21507: //Necromancer's steam staff
							e.getItem().setId(11738);
							break;
						}
						e.getPlayer().getInventory().addItemDrop(21488, 1);
						e.getPlayer().getInventory().refresh();
					}
				}
			});
		}
	};
}
