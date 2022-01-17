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
package com.rs.game.player.content.skills.woodcutting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.Effect;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.DropSets;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class Woodcutting extends Action {

	private GameObject treeObj;
	private TreeType type;
	private Hatchet hatchet;
	private boolean usingBeaver;

	public Woodcutting(GameObject treeObj, TreeType type) {
		this.treeObj = treeObj;
		this.type = type;
	}

	public static LoginHandler unlockBlisterwoodTree = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(9776, 1);
		}
	};

	public static ObjectClickHandler handleTree = new ObjectClickHandler(new Object[] { "Tree", "Swamp tree", "Dead tree", "Evergreen", "Dying tree", "Jungle tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.NORMAL));
		}
	};

	public static ObjectClickHandler handleOak = new ObjectClickHandler(new Object[] { "Oak", "Oak tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.OAK));
		}
	};

	public static ObjectClickHandler handleWillow = new ObjectClickHandler(new Object[] { "Willow", "Willow tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.WILLOW));
		}
	};

	public static ObjectClickHandler handleMaple = new ObjectClickHandler(new Object[] { "Maple", "Maple tree", "Maple Tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.MAPLE));
		}
	};

	public static ObjectClickHandler handleTeak = new ObjectClickHandler(new Object[] { "Teak", "Teak tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.TEAK));
		}
	};

	public static ObjectClickHandler handleMahogany = new ObjectClickHandler(new Object[] { "Mahogany", "Mahogany tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.MAHOGANY));
		}
	};

	public static ObjectClickHandler handleArcticPine = new ObjectClickHandler(new Object[] { "Arctic Pine" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down") || e.getObject().getDefinitions().containsOption(0, "Cut down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.ARCTIC_PINE));
		}
	};

	public static ObjectClickHandler handleIvy = new ObjectClickHandler(new Object[] { "Ivy" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.IVY));
		}
	};

	public static ObjectClickHandler handleYew = new ObjectClickHandler(new Object[] { "Yew", "Yew tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.YEW));
		}
	};

	public static ObjectClickHandler handleMagic = new ObjectClickHandler(new Object[] { "Magic tree", "Cursed magic tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.MAGIC));
		}
	};

	public static ObjectClickHandler handleSwayingTree = new ObjectClickHandler(new Object[] { "Swaying tree" }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getDefinitions().containsOption(0, "Cut-branch"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.SWAYING) {
					@Override
					public void fellTree() {

					}
				});
		}
	};

	public static ObjectClickHandler handleBlisterwood = new ObjectClickHandler(new Object[] { 61321 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getOption().equals("Chop"))
				e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.BLISTERWOOD) {
					@Override
					public void fellTree() {
						e.getPlayer().getVars().setVarBit(9776, 2);
						CoresManager.schedule(() -> {
							try {
								if (e.getPlayer() != null && !e.getPlayer().hasFinished())
									e.getPlayer().getVars().setVarBit(9776, 1);
							} catch (Throwable e1) {
								Logger.handle(e1);
							}
						}, Ticks.fromMinutes(2));
					}

					@Override
					public boolean checkTree() {
						return e.getPlayer().getVars().getVarBit(9776) == 1;
					}
				});
		}
	};

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.faceObject(treeObj);
		player.sendMessage(usingBeaver ? "Your beaver uses its strong teeth to chop down the tree..." : "You swing your hatchet at the " + (TreeType.IVY == type ? "ivy" : "tree") + "...", true);
		setActionDelay(player, 4);
		return true;
	}

	private boolean checkAll(Player player) {
		hatchet = Hatchet.getBest(player);
		if (hatchet == null) {
			player.sendMessage("You dont have the required level to use that axe or you don't have a hatchet.");
			return false;
		}
		if (!hasWoodcuttingLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("Not enough space in your inventory.");
			return false;
		}
		return true;
	}

	private boolean hasWoodcuttingLevel(Player player) {
		if (type.getLevel() > player.getSkills().getLevel(8)) {
			player.sendMessage("You need a woodcutting level of " + type.getLevel() + " to chop down this tree.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (!usingBeaver)
			player.setNextAnimation(hatchet.getAnim());
		return checkAll(player) && checkTree();
	}

	@Override
	public int processWithDelay(Player player) {
		int level = player.getSkills().getLevel(Constants.WOODCUTTING);
		player.faceObject(treeObj);
		if (player.getFamiliar() != null)
			level += getSpecialFamiliarBonus(player.getFamiliar().getId());
		if (type.rollSuccess(player, level, hatchet)) {
			giveLog(player, type, usingBeaver);
			if (!type.isPersistent() || (Utils.random(8) == 0)) {
				fellTree();
				player.setNextAnimation(new Animation(-1));
				return -1;
			}
		}
		return 3;
	}

	public void fellTree() {
		if (!World.isSpawnedObject(treeObj) && treeObj.getPlane() < 3 && type != TreeType.IVY) {
			GameObject object = World.getObject(new WorldTile(treeObj.getX() - 1, treeObj.getY() - 1, treeObj.getPlane() + 1), ObjectType.SCENERY_INTERACT);
			if (object == null) {
				object = World.getObject(new WorldTile(treeObj.getX() - 1, treeObj.getY() - 1, treeObj.getPlane() + 1), ObjectType.SCENERY_INTERACT);
				if (object == null) {
					object = World.getObject(new WorldTile(treeObj.getX(), treeObj.getY() - 1, treeObj.getPlane() + 1), ObjectType.SCENERY_INTERACT);
					if (object == null) {
						object = World.getObject(new WorldTile(treeObj.getX() - 1, treeObj.getY(), treeObj.getPlane() + 1), ObjectType.SCENERY_INTERACT);
						if (object == null)
							object = World.getObject(new WorldTile(treeObj.getX(), treeObj.getY(), treeObj.getPlane() + 1), ObjectType.SCENERY_INTERACT);
					}
				}
			}

			if (object != null)
				World.removeObjectTemporary(object, type.getRespawnDelay());
		}
		if (!World.isSpawnedObject(treeObj))
			World.spawnObjectTemporary(new GameObject(TreeStumps.getStumpId(treeObj.getId()), treeObj.getType(), treeObj.getRotation(), treeObj.getX(), treeObj.getY(), treeObj.getPlane()), type.getRespawnDelay());
	}

	public static double getLumberjackBonus(Player player) {
		double xpBoost = 1.00;
		if (player.getEquipment().getChestId() == 10939)
			xpBoost += 0.008;
		if (player.getEquipment().getLegsId() == 10940)
			xpBoost += 0.006;
		if (player.getEquipment().getHatId() == 10941)
			xpBoost += 0.004;
		if (player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.002;
		if (player.getEquipment().getChestId() == 10939 && player.getEquipment().getLegsId() == 10940 && player.getEquipment().getHatId() == 10941 && player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.005;
		return xpBoost;
	}

	public static void giveLog(Player player, TreeType type, boolean usingBeaver) {
		if (type != TreeType.IVY) {
			if (type.getLogsId() != null)
				player.incrementCount(ItemDefinitions.getDefs(type.getLogsId()[0]).getName() + " chopped");
		} else
			player.incrementCount("Choking ivy chopped");
		if (Utils.random(256) == 0) {
			for (Item rew : DropTable.calculateDrops(player, DropSets.getDropSet("nest_drop")))
				World.addGroundItem(rew, new WorldTile(player), player, true, 30);
			player.sendMessage("<col=FF0000>A bird's nest falls out of the tree!");
		}
		if (!usingBeaver)
			player.getSkills().addXp(Constants.WOODCUTTING, type.getXp() * getLumberjackBonus(player));
		if (player.hasEffect(Effect.JUJU_WOODCUTTING)) {
			int random = Utils.random(100);
			if (random < 11)
				player.addEffect(Effect.JUJU_WC_BANK, 75);
		}
		if (Utils.random(256) == 0) {
			for (Item rew : DropTable.calculateDrops(player, DropSets.getDropSet("nest_drop")))
				World.addGroundItem(rew, new WorldTile(player), player, true, 30);
			player.sendMessage("<col=FF0000>A bird's nest falls out of the tree!");
		}
		//		if (type != TreeType.IVY) {
		//			if (type.getLogsId() != null)
		//				player.incrementCount(ItemDefinitions.getDefs(type.getLogsId()[0]).getName() + " chopped");
		//		} else {
		//			player.incrementCount("Choking ivy chopped");
		//		}
		if (type.getLogsId() != null) {
			if (usingBeaver) {
				if (player.getFamiliar() != null)
					for (int item : type.getLogsId())
						player.getInventory().addItemDrop(item, 1);
			} else if (player.hasEffect(Effect.JUJU_WC_BANK)) {
				for (int item : type.getLogsId())
					player.getBank().addItem(new Item(item, 1), true);
				player.setNextSpotAnim(new SpotAnim(2897));
			} else
				for (int item : type.getLogsId())
					player.getInventory().addItemDrop(item, 1);
			if (type == TreeType.FRUIT_TREE)
				return;
			if (type == TreeType.IVY)
				player.sendMessage("You succesfully cut an ivy vine.", true);
			else {
				String logName = ItemDefinitions.getDefs(type.getLogsId()[0]).getName().toLowerCase();
				player.sendMessage("You get some " + logName + ".", true);
				if (player.getEquipment().getWeaponId() == 13661 && !(type == TreeType.IVY))
					if (Utils.getRandomInclusive(3) == 0) {
						player.getSkills().addXp(Constants.FIREMAKING, type.getXp() * 1);
						player.getInventory().deleteItem(type.getLogsId()[0], 1);
						player.sendMessage("The adze's heat instantly incinerates the " + logName + ".");
						player.setNextSpotAnim(new SpotAnim(1776));
					}
			}
		}
	}

	public boolean checkTree() {
		return World.getRegion(treeObj.getRegionId()).objectExists(treeObj);
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 4);
	}

	public static int getSpecialFamiliarBonus(int id) {
		switch (id) {

		}
		return 0;
	}
}
