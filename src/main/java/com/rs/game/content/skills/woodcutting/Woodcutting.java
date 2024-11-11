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
package com.rs.game.content.skills.woodcutting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.content.Effect;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.map.ChunkManager;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.actions.Action;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.entity.player.managers.AuraManager;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.DropSets;
import com.rs.utils.Ticks;
import com.rs.utils.drop.DropTable;

@PluginEventHandler
public class Woodcutting extends Action {

	private final int treeId;
	private final GameObject treeObj;
	private final TreeType type;
	private Hatchet hatchet;
	private int wcLevel = -1;

	public Woodcutting(GameObject treeObj, TreeType type) {
		this.treeId = treeObj.getId();
		this.treeObj = treeObj;
		this.type = type;
	}
	
	public Woodcutting setHatchet(Hatchet hatchet) {
		this.hatchet = hatchet;
		return this;
	}
	
	public Woodcutting setLevel(int level) {
		this.wcLevel = level;
		return this;
	}

	public static LoginHandler unlockBlisterwoodTree = new LoginHandler(e -> e.getPlayer().getVars().setVarBit(9776, 1));

	public static ObjectClickHandler handleTree = new ObjectClickHandler(new Object[] { "Tree", "Swamp tree", "Dead tree", "Evergreen", "Dying tree", "Jungle Tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down") || e.getObject().getDefinitions().containsOption(0, "Chop-down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.NORMAL));
	});

	public static ObjectClickHandler handleAchey = new ObjectClickHandler(new Object[] { "Achey", "Achey Tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.ACHEY));
	});

	public static ObjectClickHandler handleOak = new ObjectClickHandler(new Object[] { "Oak", "Oak tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.OAK));
	});

	public static ObjectClickHandler handleWillow = new ObjectClickHandler(new Object[] { "Willow", "Willow tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.WILLOW));
	});

	public static ObjectClickHandler handleMaple = new ObjectClickHandler(new Object[] { "Maple", "Maple tree", "Maple Tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.MAPLE));
	});

	public static ObjectClickHandler handleTeak = new ObjectClickHandler(new Object[] { "Teak", "Teak tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.TEAK));
	});

	public static ObjectClickHandler handleMahogany = new ObjectClickHandler(new Object[] { "Mahogany", "Mahogany tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.MAHOGANY));
	});

	public static ObjectClickHandler handleArcticPine = new ObjectClickHandler(new Object[] { "Arctic Pine" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down") || e.getObject().getDefinitions().containsOption(0, "Cut down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.ARCTIC_PINE));
	});

	public static ObjectClickHandler handleEucalyptus = new ObjectClickHandler(new Object[] { "Eucalyptus", "Eucalyptus tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.EUCALYPTUS));
	});

	public static ObjectClickHandler handleIvy = new ObjectClickHandler(new Object[] { "Ivy" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.IVY));
	});

	public static ObjectClickHandler handleYew = new ObjectClickHandler(new Object[] { "Yew", "Yew tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.YEW));
	});

	public static ObjectClickHandler handleMagic = new ObjectClickHandler(new Object[] { "Magic tree", "Cursed magic tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Chop down"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.MAGIC));
	});

	public static ObjectClickHandler handleSwayingTree = new ObjectClickHandler(new Object[] { "Swaying tree" }, e -> {
		if (e.getObject().getDefinitions().containsOption(0, "Cut-branch"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.SWAYING) {
				@Override
				public void fellTree() {

				}
			});
	});

	public static ObjectClickHandler handleBlisterwood = new ObjectClickHandler(new Object[] { 61321 }, e -> {
		if (e.getOption().equals("Chop"))
			e.getPlayer().getActionManager().setAction(new Woodcutting(e.getObject(), TreeType.BLISTERWOOD) {
				@Override
				public void fellTree() {
					e.getPlayer().getVars().setVarBit(9776, 2);
					WorldTasks.schedule(Ticks.fromMinutes(2), () -> {
						try {
							if (e.getPlayer() != null && !e.getPlayer().hasFinished())
								e.getPlayer().getVars().setVarBit(9776, 1);
						} catch (Throwable e1) {
							Logger.handle(Woodcutting.class, "handleBlisterwood", e1);
						}
					});
				}

				@Override
				public boolean checkTree() {
					return e.getPlayer().getVars().getVarBit(9776) == 1;
				}
			});
	});

	@Override
	public boolean start(Entity entity) {
		if (!checkAll(entity))
			return false;
		entity.faceObject(treeObj);
		if (entity instanceof Familiar familiar)
			familiar.getOwner().sendMessage("Your beaver uses its strong teeth to chop down the tree...");
		else if (entity instanceof Player player)
			player.sendMessage("You swing your hatchet at the " + (TreeType.IVY == type ? "ivy" : "tree") + ".", true);
		setActionDelay(entity, 4);
		return true;
	}

	private boolean checkAll(Entity entity) {
		if (hatchet == null)
			hatchet = entity instanceof Player player ? Hatchet.getBest(player) : Hatchet.MITHRIL;
		if (entity instanceof Player player) {
			if (hatchet == null) {
				player.sendMessage("You do not have a hatchet which you have the woodcutting level to use.");
				return false;
			}
			if (!hasWoodcuttingLevel(player))
				return false;
			if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("Not enough space in your inventory.");
				player.anim(-1);
				return false;
			}
		} else if (entity instanceof Familiar familiar && familiar.getInventory().freeSlots() == 0)
			return false;
		return true;
	}

	private boolean hasWoodcuttingLevel(Player player) {
		if (type.getLevel() > player.getSkills().getLevel(8)) {
			player.sendMessage("You need a woodcutting level of " + type.getLevel() + " to chop down " + (TreeType.IVY == type ? "the ivy" : "this tree") + ".");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Entity entity) {
		entity.anim(entity instanceof Familiar ? 7722 : hatchet.getAnim(type));
		if (entity instanceof Familiar)
			entity.spotAnim(1459);
		return checkAll(entity) && checkTree();
	}

	@Override
	public int processWithDelay(Entity entity) {
		if (!checkTree())
			return -1;
		int level = entity instanceof Player player ? player.getSkills().getLevel(Constants.WOODCUTTING) + player.getInvisibleSkillBoost(Skills.WOODCUTTING) : wcLevel;
		entity.faceObject(treeObj);
		if (type.rollSuccess(entity instanceof Player player ? player.getAuraManager().getWoodcuttingMul() : 1.0, level, hatchet)) {
			giveLog(entity);
			int fellChance = entity instanceof Player player && player.hasEffect(Effect.EVIL_TREE_WOODCUTTING_BUFF) ? 16 : 8;
			if (!type.isPersistent() || (Utils.random(fellChance) == 0)) {
				if (entity instanceof Player player && player.getAuraManager().isActivated(AuraManager.Aura.RESOURCEFUL) && Utils.random(10) == 0) {
					player.sendMessage("Your resourceful aura prevents the " + (TreeType.IVY == type ? "ivy" : "tree") + " from being felled.");
					return 3;
				}
				fellTree();
				entity.anim(-1);
				return -1;
			}
		}
		return 3;
	}

	public void fellTree() {
		if (!World.isSpawnedObject(treeObj) && treeObj.getPlane() < 3 && type != TreeType.IVY) {
			for (int x = -1;x <= 1;x++) {
				for (int y = -1;y <= 1;y++) {
					World.removeObjectTemporary(World.getObject(treeObj.getTile().transform(x, y, 1), ObjectType.SCENERY_INTERACT), type.getRespawnDelay());
					World.removeObjectTemporary(World.getObject(treeObj.getTile().transform(x, y, 1), ObjectType.GROUND_INTERACT), type.getRespawnDelay());
				}
			}
		}
		if (!World.isSpawnedObject(treeObj)) {
			treeObj.getAttribs().setI("originalTrunkId", treeObj.getId());
			treeObj.setIdTemporary(TreeStumps.getStumpId(treeObj.getId()), type.getRespawnDelay());
		}
	}

	public static double getLumberjackBonus(Player player) {
		double xpBoost = 0.0;
		if (player.getEquipment().getChestId() == 10939)
			xpBoost += 0.01;
		if (player.getEquipment().getLegsId() == 10940)
			xpBoost += 0.01;
		if (player.getEquipment().getHatId() == 10941)
			xpBoost += 0.01;
		if (player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.01;
		if (player.getEquipment().getChestId() == 10939 && player.getEquipment().getLegsId() == 10940 && player.getEquipment().getHatId() == 10941 && player.getEquipment().getBootsId() == 10933)
			xpBoost += 0.01;
		return xpBoost;
	}

	public void giveLog(Entity entity) {
		if (entity instanceof Player player) {
			if (type != TreeType.IVY) {
				if (type.getLogsId() != null)
					player.incrementCount(ItemDefinitions.getDefs(type.getLogsId()[0]).getName() + " chopped");
			} else
				player.incrementCount("Choking ivy chopped");
			if (Utils.random(256) == 0) {
				for (Item rew : DropTable.calculateDrops(player, DropSets.getDropSet("nest_drop")))
					World.addGroundItem(rew, Tile.of(player.getTile()), player, true, 30);
				player.sendMessage("<col=FF0000>A bird's nest falls out of the " + (TreeType.IVY == type ? "ivy" : "tree") + "!");
			}
			double bxp = type.getXp() * getLumberjackBonus(player);
			player.getSkills().addXp(Constants.WOODCUTTING, type.getXp() + bxp);
			if (bxp > 0.01)
				player.getSkills().queueBonusXPDrop(bxp);
			if (player.hasEffect(Effect.JUJU_WOODCUTTING)) {
				int random = Utils.random(100);
				if (random < 11)
					player.addEffect(Effect.JUJU_WC_BANK, 75);
			}
			if (Utils.random(256) == 0) {
				for (Item rew : DropTable.calculateDrops(player, DropSets.getDropSet("nest_drop")))
					World.addGroundItem(rew, Tile.of(player.getTile()), player, true, 30);
				player.sendMessage("<col=FF0000>A bird's nest falls out of the " + (TreeType.IVY == type ? "ivy" : "tree") + "!");
			}
			if (type.getLogsId() != null) {
				if (player.hasEffect(Effect.JUJU_WC_BANK) || player.hasEffect(Effect.EVIL_TREE_WOODCUTTING_BUFF)) {
					for (int item : type.getLogsId())
						player.getBank().addItem(new Item(item, 1), true);
					player.spotAnim(player.hasEffect(Effect.EVIL_TREE_WOODCUTTING_BUFF) ? 312 : 2897);
				} else
					for (int item : type.getLogsId())
						player.getInventory().addItemDrop(item, 1);
				if (type == TreeType.FRUIT_TREE)
					return;
				if (type == TreeType.IVY) {
					player.sendMessage("You successfully cut an ivy vine.", true);
				} else {
					String logName = ItemDefinitions.getDefs(type.getLogsId()[0]).getName().toLowerCase();
					player.sendMessage("You get some " + logName + ".", true);
					if (player.getEquipment().getWeaponId() == 13661)
						if (Utils.getRandomInclusive(3) == 0) {
							player.getSkills().addXp(Constants.FIREMAKING, type.getXp() * 1);
							player.getInventory().deleteItem(type.getLogsId()[0], 1);
							player.sendMessage("The adze's heat instantly incinerates the " + logName + ".");
							player.spotAnim(1776);
						}
				}
			}
		}
		if (entity instanceof Familiar familiar) {
			for (int item : type.getLogsId())
				familiar.getInventory().add(new Item(item, 1));
			familiar.getOwner().getSkills().addXp(Constants.WOODCUTTING, type.getXp() * getLumberjackBonus(familiar.getOwner()));
		}
	}

	public boolean checkTree() {
		return ChunkManager.getChunk(treeObj.getTile().getChunkId()).objectExists(new GameObject(treeObj, treeId));
	}

	@Override
	public void stop(Entity player) {
		setActionDelay(player, 4);
	}
}