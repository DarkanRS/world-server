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
package com.rs.game.content.skills.mining;

import java.util.function.Supplier;

import com.rs.game.World;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.actions.Action;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class Mining extends Action {

	public static ObjectClickHandler handleClay = new ObjectClickHandler(new Object[] { "Clay rocks", "Clay vein" }, 
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.CLAY, e.getObject())));
	
	public static ObjectClickHandler handleCopper = new ObjectClickHandler(new Object[] { "Copper ore rocks", "Copper ore vein" }, 
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.COPPER, e.getObject())));

	public static ObjectClickHandler handleTin = new ObjectClickHandler(new Object[] { "Tin ore rocks", "Tin ore vein" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.TIN, e.getObject())));

	public static ObjectClickHandler handleBlurite = new ObjectClickHandler(new Object[] { "Blurite ore rocks" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.BLURITE, e.getObject())));

	public static ObjectClickHandler handleLimestone = new ObjectClickHandler(new Object[] { "Limestone rocks" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.LIMESTONE, e.getObject())));

	public static ObjectClickHandler handleIron = new ObjectClickHandler(new Object[] { "Iron ore rocks", "Iron ore vein" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.IRON, e.getObject())));

	public static ObjectClickHandler handleSilver = new ObjectClickHandler(new Object[] { "Silver ore rocks", "Silver ore vein" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.SILVER, e.getObject())));

	public static ObjectClickHandler handleGold = new ObjectClickHandler(new Object[] { "Gold ore rocks", "Gold ore vein" }, e -> {
		if(e.getObject().getTile().getRegionId() == 10903)//witchhaven mine
			e.getPlayer().getActionManager().setAction(new Mining(RockType.PERFECT_GOLD, e.getObject()));
		else
			e.getPlayer().getActionManager().setAction(new Mining(RockType.GOLD, e.getObject()));
	});

	public static ObjectClickHandler handleCoal = new ObjectClickHandler(new Object[] { "Coal rocks", "Coal vein" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.COAL, e.getObject())));

	public static ObjectClickHandler handleMithril = new ObjectClickHandler(new Object[] { "Mithril ore rocks", "Mithril ore vein" }, 
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.MITHRIL, e.getObject())));

	public static ObjectClickHandler handleAddy = new ObjectClickHandler(new Object[] { "Adamantite ore rocks", "Adamantite ore vein" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.ADAMANT, e.getObject())));

	public static ObjectClickHandler handleRune = new ObjectClickHandler(new Object[] { "Runite ore rocks" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.RUNE, e.getObject())));

	public static ObjectClickHandler handleGranite = new ObjectClickHandler(new Object[] { "Granite rocks" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.GRANITE, e.getObject())));

	public static ObjectClickHandler handleSandstone = new ObjectClickHandler(new Object[] { "Sandstone rocks" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.SANDSTONE, e.getObject())));

	public static ObjectClickHandler handleGemRocks = new ObjectClickHandler(new Object[] { "Gem rocks" },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.GEM, e.getObject())));

	public static ObjectClickHandler handleLRCCoal = new ObjectClickHandler(new Object[] { 5999 }, 
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.CONC_COAL, e.getObject())));

	public static ObjectClickHandler handleLRCGold = new ObjectClickHandler(new Object[] { 45076 },
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.CONC_GOLD, e.getObject())));

	public static ObjectClickHandler handleEss = new ObjectClickHandler(new Object[] { 2491 }, 
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.ESSENCE, e.getObject())));

	public static NPCClickHandler handleLRCMinerals = new NPCClickHandler(new Object[] { 8837, 8838, 8839 }, 
			e -> e.getPlayer().getActionManager().setAction(new Mining(RockType.LIVING_MINERALS, e.getNPC(), () -> e.getNPC().getId() - 5)));

	public static ObjectClickHandler handleRedSandstone = new ObjectClickHandler(new Object[] { 2330 }, e -> {
		if (e.getPlayer().getDailyI("redSandstoneMined") < 50)
			e.getPlayer().getActionManager().setAction(new Mining(RockType.RED_SANDSTONE, e.getObject()));
		else
			e.getPlayer().sendMessage("You've mined all you can from the rock.");
	});

	public static LoginHandler updateSandstone = new LoginHandler(e -> e.getPlayer().getVars().setVarBit(10133, e.getPlayer().getDailyI("redSandstoneMined")));

	private RockType type;
	private Pickaxe pick;
	private int rockId;
	private GameObject rockObj;
	private NPC rockNPC;
	private Supplier<Integer> replaceId;

	public Mining(RockType type, GameObject rock) {
		this.type = type;
		this.rockId = rock.getId();
		rockObj = rock;
	}

	public Mining(RockType type, NPC rock, Supplier<Integer> replaceId) {
		this.type = type;
		rockNPC = rock;
		this.replaceId = replaceId;
	}

	@Override
	public boolean start(Entity entity) {
		if (entity instanceof Player player)
			pick = Pickaxe.getBest(player);
		else
			pick = Pickaxe.DRAGON_G;
		if (!checkAll(entity))
			return false;
		if (entity instanceof Player player)
			player.sendMessage("You swing your pickaxe at the rock...", true);
		setActionDelay(entity, ((pick == Pickaxe.DRAGON || pick == Pickaxe.DRAGON_G) && Utils.random(2) == 0) ? 1 : 2);
		return true;
	}

	@Override
	public boolean process(Entity entity) {
		entity.setNextAnimation(pick.getAnimation());
		return checkAll(entity);
	}

	@Override
	public int processWithDelay(Entity entity) {
		int level = entity instanceof Player player ? player.getSkills().getLevel(Constants.MINING) + player.getInvisibleSkillBoost(Constants.MINING) : 99;
		boolean success = false;
		if (type.getOres().size() == 1 && !type.getOres().get(0).checkRequirements(entity instanceof Player player ? player : null))
			return -1;
		for (Ore ore : type.getOres()) {
			if (ore.checkRequirements(entity instanceof Player player ? player : null) && ore.rollSuccess(entity instanceof Player player ? player : null, level)) {
				if (entity instanceof Player player)
					ore.giveOre(player);
				success = true;
				if (ore.getRollGem() == 1 && entity instanceof Player player)
					rollForGem(player);
				break;
			}
		}
		if (success && depleteOre(entity)) {
			entity.setNextAnimation(new Animation(-1));
			return -1;
		}
		return ((pick == Pickaxe.DRAGON || pick == Pickaxe.DRAGON_G) && Utils.random(2) == 0) ? pick.getTicks() - 2 : pick.getTicks() - 1;
	}

	public boolean depleteOre(Entity entity) {
		if (type.depletes()) {
			if (rockObj != null)
				rockObj.setIdTemporary(DepletedOres.get(rockObj.getId()), type.getRespawnTime());
			if (rockNPC != null) {
				rockNPC.setNPC(replaceId.get());
				rockNPC.setLocation(rockNPC.getRespawnTile());
				rockNPC.setRandomWalk(true);
				rockNPC.finish();
				if (!rockNPC.isSpawned())
					rockNPC.setRespawnTask();
			}
			return true;
		}
		return false;
	}

	private boolean checkAll(Entity entity) {
		if (!checkRock())
			return false;
		if (rockObj != null && DepletedOres.isDepleted(rockObj.getId())) {
			if (entity instanceof Player player)
				player.sendMessage("This rock is empty right now.");
			return false;
		}
		if (pick == null) {
			if (entity instanceof Player player)
				player.sendMessage("You need a pickaxe and the level required to use it to mine.");
			return false;
		}
		if (entity instanceof Player player) {
			if (player.getSkills().getLevel(Constants.MINING) < type.getLevel()) {
				player.sendMessage("You need a mining level of " + type.getLevel() + " to mine here.");
				return false;
			}
			if (!player.getInventory().hasFreeSlots()) {
				player.setNextAnimation(new Animation(-1));
				player.sendMessage("You don't have enough inventory space.");
				return false;
			}
		}
		return true;
	}

	@Override
	public void stop(final Entity entity) {
		setActionDelay(entity, pick.getTicks());
	}

	public boolean checkRock() {
		return rockObj != null ? World.getRegion(rockObj.getTile().getRegionId()).objectExists(new GameObject(rockObj).setIdNoRefresh(rockId)) : !rockNPC.hasFinished();
	}

	public static double getXPMultiplier(Player player) {
		double mul = 1.0;
		if (player.getEquipment().getGlovesId() == 20787)
			mul += 0.01;
		if (player.getEquipment().getBootsId() == 20788)
			mul += 0.01;
		if (player.getEquipment().getHatId() == 20789)
			mul += 0.01;
		if (player.getEquipment().getChestId() == 20791)
			mul += 0.01;
		if (player.getEquipment().getLegsId() == 20790)
			mul += 0.01;
		return mul;
	}

	public static void rollForGem(Player player) {
		int random = Utils.random(256);
		// >= 10354 && <= 10360 //charged trimmed glory
		// >= 1706 && <= 1712 //charged glory
		int neck = player.getEquipment().getAmuletId();
		if ((neck >= 10354 && neck <= 10360) || (neck >= 1706 && neck <= 1712))
			random = Utils.random(86);
		if (random == 0) {
			random = Utils.random(10);
			if (random >= 9)
				player.getInventory().addItemDrop(1617, 1); // Uncut Diamond
			else if (random >= 7)
				player.getInventory().addItemDrop(1619, 1); // Uncut Ruby
			else if (random >= 4)
				player.getInventory().addItemDrop(1621, 1); // Uncut Emerald
			else
				player.getInventory().addItemDrop(1623, 1); // Uncut Sapphire
		}
	}
}
