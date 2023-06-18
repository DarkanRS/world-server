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
package com.rs.game.content.skills.magic;

import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.bosses.godwars.GodwarsController;
import com.rs.game.content.combat.CombatSpell;
import com.rs.game.content.skills.construction.HouseController;
import com.rs.game.content.skills.dungeoneering.DamonheimController;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.InterfaceOnNPCHandler;
import com.rs.plugin.handlers.InterfaceOnPlayerHandler;

import java.util.List;
import java.util.function.Consumer;

@PluginEventHandler
public class Magic {

	//342 343 teleother spotanims

	public static final int MAGIC_TELEPORT = 0, ITEM_TELEPORT = 1, OBJECT_TELEPORT = 2;

	public static boolean isSlayerStaff(int weaponId) {
		switch(weaponId) {
		case 4170:
		case 15486:
		case 15502:
		case 22207:
		case 22213:
		case 22211:
		case 22209:
			return true;
		}
		return false;
	}

	public static boolean hasStaffOfLight(int weaponId) {
		switch(weaponId) {
		case 15486:
		case 15502:
		case 22207:
		case 22213:
		case 22211:
		case 22209:
			return true;
		}
		return false;
	}

	public static final boolean checkCombatSpell(Player player, CombatSpell spell, int set, boolean delete) {
		if (spell == CombatSpell.STORM_OF_ARMADYL && !player.isQuestComplete(Quest.RITUAL_OF_MAHJARRAT, "to cast Storm of Armadyl."))
			return false;
		if (set >= 0)
			if (set == 0)
				player.getCombatDefinitions().setAutoCastSpell(spell);
			else
				player.getCombatDefinitions().setManualCastSpell(spell);
		return true;
	}

	public static final void manualCast(Player player, Entity target, CombatSpell spell) {
		if (checkCombatSpell(player, spell, 1, false)) {
			player.setNextFaceTile(target.getMiddleTile());
			player.getInteractionManager().setInteraction(new PlayerCombatInteraction(player, target));
		}
	}
	
	public static InterfaceOnPlayerHandler manualCastPlayer = new InterfaceOnPlayerHandler(false, new int[] { 192, 193, 950 }, e -> {
		CombatSpell combat = CombatSpell.forId(e.getInterfaceId(), e.getComponentId());
		if (combat != null)
			manualCast(e.getPlayer(), e.getTarget(), combat);
	});
	
	public static InterfaceOnNPCHandler manualCastNPC = new InterfaceOnNPCHandler(false, new int[] { 192, 193, 950 }, e -> {
		e.getPlayer().stopAll(false);
		CombatSpell combat = CombatSpell.forId(e.getInterfaceId(), e.getComponentId());
		if (combat != null) {
			if (!e.getTarget().getDefinitions().hasAttackOption()) {
				e.getPlayer().sendMessage("You can't attack that.");
				return;
			}
			manualCast(e.getPlayer(), e.getTarget(), combat);
		}
	});

	public static ButtonClickHandler handleNormalSpellbookButtons = new ButtonClickHandler(192, e -> {
		if (e.getComponentId() == 2)
			e.getPlayer().getCombatDefinitions().switchDefensiveCasting();
		else if (e.getComponentId() == 7)
			e.getPlayer().getCombatDefinitions().switchShowCombatSpells();
		else if (e.getComponentId() == 9)
			e.getPlayer().getCombatDefinitions().switchShowTeleportSkillSpells();
		else if (e.getComponentId() == 11)
			e.getPlayer().getCombatDefinitions().switchShowMiscSpells();
		else if (e.getComponentId() == 13)
			e.getPlayer().getCombatDefinitions().switchShowSkillSpells();
		else if (e.getComponentId() >= 15 & e.getComponentId() <= 17)
			e.getPlayer().getCombatDefinitions().setSortSpellBook(e.getComponentId() - 15);
		else
			Magic.processNormalSpell(e.getPlayer(), e.getComponentId(), e.getPacket());
	});

	public static ButtonClickHandler handleAncientSpellbookButtons = new ButtonClickHandler(193, e -> {
		if (e.getComponentId() == 5)
			e.getPlayer().getCombatDefinitions().switchShowCombatSpells();
		else if (e.getComponentId() == 7)
			e.getPlayer().getCombatDefinitions().switchShowTeleportSkillSpells();
		else if (e.getComponentId() >= 9 && e.getComponentId() <= 11)
			e.getPlayer().getCombatDefinitions().setSortSpellBook(e.getComponentId() - 9);
		else if (e.getComponentId() == 18)
			e.getPlayer().getCombatDefinitions().switchDefensiveCasting();
		else
			Magic.processAncientSpell(e.getPlayer(), e.getComponentId(), e.getPacket());
	});

	public static ButtonClickHandler handleLunarSpellbookButtons = new ButtonClickHandler(430, e -> {
		if (e.getComponentId() == 5)
			e.getPlayer().getCombatDefinitions().switchShowCombatSpells();
		else if (e.getComponentId() == 7)
			e.getPlayer().getCombatDefinitions().switchShowTeleportSkillSpells();
		else if (e.getComponentId() == 9)
			e.getPlayer().getCombatDefinitions().switchShowMiscSpells();
		else if (e.getComponentId() >= 11 & e.getComponentId() <= 13)
			e.getPlayer().getCombatDefinitions().setSortSpellBook(e.getComponentId() - 11);
		else if (e.getComponentId() == 20)
			e.getPlayer().getCombatDefinitions().switchDefensiveCasting();
		else
			Magic.processLunarSpell(e.getPlayer(), e.getComponentId(), e.getPacket());
	});

	public static final void setCombatSpell(Player player, CombatSpell spell) {
		if (player.getCombatDefinitions().getAutoCast() == spell)
			player.getCombatDefinitions().resetSpells(true);
		else
			checkCombatSpell(player, spell, 0, false);
	}

	public static final void processLunarSpell(Player player, int componentId, ClientPacket packetId) {
		switch (componentId) {
		case 39:
			player.stopAll(false);
			useHomeTele(player);
			break;
		case 38:
			Lunars.handleBakePie(player);
			break;
		case 26:
			//Lunars.handleNPCContact(player);
			//inter 88
			break;
		case 29:
			Lunars.handleHumidify(player);
			break;
		case 43:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 69, 66, Tile.of(2112, 3915, 0), new RuneSet(Rune.LAW, 1, Rune.ASTRAL, 2, Rune.EARTH, 2));
			break;
		case 56:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 70, 67, Tile.of(2112, 3915, 0), new RuneSet(Rune.LAW, 1, Rune.ASTRAL, 2, Rune.EARTH, 4));
			break;
		case 54:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 71, 69, Tile.of(2466, 3248, 0), new RuneSet(Rune.LAW, 1, Rune.ASTRAL, 2, Rune.EARTH, 6));
			break;
		case 46:
			Lunars.handleCureMe(player);
			break;
		case 30:
			Lunars.handleHunterKit(player);
			break;
		case 67:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 72, 70, Tile.of(3005, 3327, 0), new RuneSet(Rune.LAW, 1, Rune.ASTRAL, 2, Rune.AIR, 2));
			break;
		case 47:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 72, 71, Tile.of(2546, 3757, 0), new RuneSet(Rune.LAW, 1, Rune.ASTRAL, 2, Rune.WATER, 1));
			break;
		case 57:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 73, 72, Tile.of(2546, 3757, 0), new RuneSet(Rune.LAW, 1, Rune.ASTRAL, 2, Rune.WATER, 5));
			break;
		case 25:
			Lunars.handleCureGroup(player);
			break;
		case 22:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 75, 76, Tile.of(2542, 3574, 0), new RuneSet(Rune.LAW, 2, Rune.ASTRAL, 2, Rune.FIRE, 3));
			break;
		case 69:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 76, 76, Tile.of(2613, 3345, 0), new RuneSet(Rune.LAW, 1, Rune.ASTRAL, 2, Rune.WATER, 5));
			break;
		case 58:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 76, 77, Tile.of(2542, 3574, 0), new RuneSet(Rune.LAW, 2, Rune.ASTRAL, 2, Rune.FIRE, 6));
			break;
		case 48:
			Lunars.handleSuperGlassMake(player);
			break;
		case 70:
			Lunars.handleRemoteFarm(player);
			break;
		case 41:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 78, 80, Tile.of(2630, 3167, 0), new RuneSet(Rune.LAW, 2, Rune.ASTRAL, 2, Rune.WATER, 4));
			break;
		case 59:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 79, 81, Tile.of(2630, 3167, 0), new RuneSet(Rune.LAW, 2, Rune.ASTRAL, 2, Rune.WATER, 8));
			break;
		case 32:
			Lunars.handleDream(player);
			break;
		case 45:
			Lunars.handleStringJewelry(player);
			break;
		case 36:
			Lunars.handleMagicImbue(player);
			break;
		case 40:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 85, 89, Tile.of(2614, 3382, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 10));
			break;
		case 60:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 86, 90, Tile.of(2614, 3382, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 14));
			break;
		case 44:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 87, 92, Tile.of(2804, 3434, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 10));
			break;
		case 61:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 88, 93, Tile.of(2804, 3434, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 12));
			break;
		case 51:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 89, 96, Tile.of(2977, 3924, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 8));
			break;
		case 62:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 90, 99, Tile.of(2977, 3924, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 16));
			break;
		case 73:
			Lunars.handleDisruptionShield(player);
			break;
		case 75:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 92, 101, Tile.of(2814, 3677, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 10));
			break;
		case 76:
			player.stopAll(false);
			sendLunarTeleportSpell(player, 93, 102, Tile.of(2814, 3677, 0), new RuneSet(Rune.LAW, 3, Rune.ASTRAL, 3, Rune.WATER, 20));
			break;
		case 37:
			Lunars.handleVengeance(player);
			break;
		case 74:
			Lunars.handleGroupVengeance(player);
			break;
		case 53:
			Lunars.handleHealGroup(player);
			break;
		case 34:
			Lunars.handleSpellbookSwap(player);
			break;
		default:
			if (player.hasRights(Rights.DEVELOPER))
				player.sendMessage("Unhandled lunar spell: " + componentId);
			break;
		}
	}

	public static final void processAncientSpell(Player player, int componentId, ClientPacket packetId) {
		player.stopAll(false);
		CombatSpell combatSpell = CombatSpell.forId(193, componentId);
		if (combatSpell != null) {
			setCombatSpell(player, combatSpell);
			return;
		}
		switch (componentId) {
		case 40:
			sendAncientTeleportSpell(player, 54, 64, Tile.of(3099, 9882, 0), new RuneSet(Rune.LAW, 2, Rune.FIRE, 1, Rune.AIR, 1));
			break;
		case 41:
			sendAncientTeleportSpell(player, 60, 70, Tile.of(3222, 3336, 0), new RuneSet(Rune.LAW, 2, Rune.SOUL, 1));
			break;
		case 42:
			sendAncientTeleportSpell(player, 66, 76, Tile.of(3492, 3471, 0), new RuneSet(Rune.LAW, 2, Rune.BLOOD, 1));
			break;
		case 43:
			sendAncientTeleportSpell(player, 72, 82, Tile.of(3006, 3471, 0), new RuneSet(Rune.LAW, 2, Rune.WATER, 4));
			break;
		case 44:
			sendAncientTeleportSpell(player, 78, 88, Tile.of(2990, 3696, 0), new RuneSet(Rune.LAW, 2, Rune.FIRE, 3, Rune.AIR, 2));
			break;
		case 45:
			sendAncientTeleportSpell(player, 84, 94, Tile.of(3217, 3677, 0), new RuneSet(Rune.LAW, 2, Rune.SOUL, 2));
			break;
		case 46:
			sendAncientTeleportSpell(player, 90, 100, Tile.of(3288, 3886, 0), new RuneSet(Rune.LAW, 2, Rune.BLOOD, 2));
			break;
		case 47:
			sendAncientTeleportSpell(player, 96, 106, Tile.of(2977, 3873, 0), new RuneSet(Rune.LAW, 2, Rune.WATER, 8));
			break;
		case 48:
			useHomeTele(player);
			break;
		}
	}

	public static final void processNormalSpell(Player player, int componentId, ClientPacket packetId) {
		player.stopAll(false);
		CombatSpell combatSpell = CombatSpell.forId(192, componentId);
		if (combatSpell != null) {
			setCombatSpell(player, combatSpell);
			return;
		}
		switch (componentId) {
		case 27:
			if (player.getSkills().getLevel(Constants.MAGIC) < 4) {
				player.sendMessage("Your Magic level is not high enough for this spell.");
				return;
			}
			player.stopAll();
			player.getInterfaceManager().sendInterface(432);
			break;
		case 24:
			useHomeTele(player);
			break;
		case 37: // mobi
			sendNormalTeleportSpell(player, 10, 19, Tile.of(2413, 2848, 0), new RuneSet(Rune.LAW, 1, Rune.WATER, 1, Rune.AIR, 1));
			break;
		case 40: // varrock
			sendNormalTeleportSpell(player, 25, 19, Tile.of(3212, 3424, 0), new RuneSet(Rune.FIRE, 1, Rune.AIR, 3, Rune.LAW, 1));
			break;
		case 43: // lumby
			sendNormalTeleportSpell(player, 31, 41, Tile.of(3222, 3218, 0), new RuneSet(Rune.EARTH, 1, Rune.AIR, 3, Rune.LAW, 1));
			break;
		case 46: // fally
			sendNormalTeleportSpell(player, 37, 48, Tile.of(2964, 3379, 0), new RuneSet(Rune.WATER, 1, Rune.AIR, 3, Rune.LAW, 1));
			break;
		case 48:
			if (player.getSkills().getLevel(Constants.MAGIC) >= 40) {
				if (!player.isLocked() && checkRunes(player, true, new RuneSet(Rune.AIR, 1, Rune.EARTH, 1, Rune.LAW, 1)))
					if (useHouseTeleport(player))
						player.getSkills().addXp(Constants.MAGIC, 48);
			} else
				player.sendMessage("You need a magic level of 40 to use this spell.");
			break;
		case 51: // camelot
			sendNormalTeleportSpell(player, 45, 55.5, Tile.of(2757, 3478, 0), new RuneSet(Rune.AIR, 5, Rune.LAW, 1));
			break;
		case 57: // ardy
			sendNormalTeleportSpell(player, 51, 61, Tile.of(2664, 3305, 0), new RuneSet(Rune.WATER, 2, Rune.LAW, 2));
			break;
		case 62: // watch
			sendNormalTeleportSpell(player, 58, 68, Tile.of(2547, 3113, 2), new RuneSet(Rune.EARTH, 2, Rune.LAW, 2));
			break;
		case 69: // troll
			sendNormalTeleportSpell(player, 61, 68, Tile.of(2888, 3674, 0), new RuneSet(Rune.FIRE, 2, Rune.LAW, 2));
			break;
		case 72: // ape
			sendNormalTeleportSpell(player, 64, 76, Tile.of(2797, 2798, 1), new RuneSet(Rune.FIRE, 2, Rune.WATER, 2, Rune.LAW, 2));
			break;
		}
	}

	public static void processDungSpell(Player player, int componentId, ClientPacket packetId) {
		if (player.hasRights(Rights.DEVELOPER))
			player.sendMessage("Dungeoneering spell: " + componentId + ", " + packetId);
		if (packetId == ClientPacket.IF_OP6) {
			CombatSpell combatSpell = CombatSpell.forId(950, componentId);
			if (combatSpell != null)
				setCombatSpell(player, combatSpell);
		}
	}

	private static void useHomeTele(Player player) {
		player.stopAll();
		player.getInterfaceManager().sendInterface(1092);
	}

	public static boolean checkSpellLevel(Player player, int level) {
		if (player.getSkills().getLevel(Constants.MAGIC) < level) {
			player.sendMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		return true;
	}

	public static final void sendNormalTeleportNoType(Player player, Tile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3, true, -1, null);
	}

	public static final void sendDamonheimTeleport(Player player, Tile tile) {
		sendTeleportSpell(player, 13652, 13654, 2602, 2603, 0, 0, tile, 10, true, MAGIC_TELEPORT, null);
	}
	
	public static final void sendLunarTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes) {
		sendTeleportSpell(player, 9606, -1, 1685, -1, level, xp, tile, 5, true, MAGIC_TELEPORT, runes, null);
	}

	public static final void sendLunarTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes, Consumer<Player> onArrive) {
		sendTeleportSpell(player, 9606, -1, 1685, -1, level, xp, tile, 5, true, MAGIC_TELEPORT, runes, onArrive);
	}
	
	public static final void sendAncientTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes, Consumer<Player> onArrive) {
		sendTeleportSpell(player, 9599, -2, 1681, -1, level, xp, tile, 5, true, MAGIC_TELEPORT, runes, onArrive);
	}
	
	public static final void sendAncientTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes) {
		sendTeleportSpell(player, 9599, -2, 1681, -1, level, xp, tile, 5, true, MAGIC_TELEPORT, runes, null);
	}
	
	public static final boolean sendNormalTeleportSpell(Player player, int level, double xp, Tile tile) {
		return sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3, true, MAGIC_TELEPORT, null, null);
	}
	
	public static final boolean sendNormalTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes) {
		return sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3, true, MAGIC_TELEPORT, runes, null);
	}

	public static final boolean sendNormalTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes, Consumer<Player> onArrive) {
		return sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3, true, MAGIC_TELEPORT, runes, onArrive);
	}

	public static final boolean sendNormalTeleportSpell(Player player, int level, double xp, Tile tile, Consumer<Player> onArrive) {
		return sendNormalTeleportSpell(player, level, xp, tile, null, onArrive);
	}
	
	public static final boolean sendNormalTeleportSpell(Player player, Tile tile) {
		return sendNormalTeleportSpell(player, 0, 0, tile);
	}

	public static final boolean sendNormalTeleportSpell(Player player, Tile tile, Consumer<Player> onArrive) {
		return sendNormalTeleportSpell(player, 0, 0, tile, onArrive);
	}

	public static final boolean sendItemTeleportSpell(Player player, boolean randomize, int upEmoteId, int upGraphicId, int delay, Tile tile) {
		player.getTempAttribs().setB("glory", true);
		return sendTeleportSpell(player, upEmoteId, -2, upGraphicId, -1, 0, 0, tile, delay, randomize, ITEM_TELEPORT, null);
	}

	public static void pushLeverTeleport(final Player player, final Tile tile) {
		if (!player.getControllerManager().processObjectTeleport(tile))
			return;
		player.setNextAnimation(new Animation(2140));
		player.lock();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.unlock();
				Magic.sendObjectTeleportSpell(player, false, tile, null);
			}
		}, 1);
	}
	
	public static final void sendObjectTeleportSpell(Player player, boolean randomize, Tile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3, randomize, OBJECT_TELEPORT, null, null);
	}

	public static final void sendObjectTeleportSpell(Player player, boolean randomize, Tile tile, Consumer<Player> onArrive) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3, randomize, OBJECT_TELEPORT, null, onArrive);
	}

	public static final void sendDelayedObjectTeleportSpell(Player player, int delay, boolean randomize, Tile tile, Consumer<Player> onArrive) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, delay, randomize, OBJECT_TELEPORT, null, onArrive);
	}

	public static final boolean sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId, int upGraphicId, final int downGraphicId, int level, final double xp, final Tile tile, int delay, final boolean randomize, final int teleType, Consumer<Player> onArrive) {
		return sendTeleportSpell(player, upEmoteId, downEmoteId, upGraphicId, downGraphicId, level, xp, tile, delay, randomize, teleType, null, onArrive);
	}

	public static final boolean sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId, int upGraphicId, final int downGraphicId, int level, final double xp, final Tile tile, int delay, final boolean randomize, final int teleType, RuneSet runes, Consumer<Player> onArrive) {
		if (player.isLocked())
			return false;
		if (player.getSkills().getLevel(Constants.MAGIC) < level) {
			player.sendMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		if (runes != null && !runes.meetsRequirements(player))
			return false;
		if (teleType == MAGIC_TELEPORT) {
			if (!player.getControllerManager().processMagicTeleport(tile))
				return false;
		} else if (teleType == ITEM_TELEPORT) {
			if (!player.getControllerManager().processItemTeleport(tile)) {
				player.getTempAttribs().setB("glory", false);
				return false;
			}
		} else if (teleType == OBJECT_TELEPORT)
			if (!player.getControllerManager().processObjectTeleport(tile))
				return false;
		if (runes != null) {
			List<Item> runeList = runes.getRunesToDelete(player);
			for (Item rune : runeList)
				if (rune != null)
					player.getInventory().deleteItem(rune);
		}
		player.stopAll();
		if (upEmoteId != -1)
			player.setNextAnimation(new Animation(upEmoteId));
		if (upGraphicId != -1)
			player.setNextSpotAnim(new SpotAnim(upGraphicId));
		if (teleType == MAGIC_TELEPORT)
			player.voiceEffect(5527);
		player.lock(3 + delay);
		WorldTasks.schedule(new WorldTask() {

			boolean removeDamage;

			@Override
			public void run() {
				if (!removeDamage) {
					Tile teleTile = tile;
					if (randomize)
						// attemps to randomize tile by 4x4 area
						for (int trycount = 0; trycount < 10; trycount++) {
							teleTile = Tile.of(tile, 2);
							if (World.floorAndWallsFree(teleTile, player.getSize()))
								break;
							teleTile = tile;
						}
					player.setNextTile(teleTile);
					if (teleType != -1) {
						player.getControllerManager().magicTeleported(teleType);
						if (player.getControllerManager().getController() == null)
							teleControllersCheck(player, teleTile);
					}
					if (xp != 0)
						player.getSkills().addXp(Constants.MAGIC, xp);
					if (downEmoteId != -1)
						player.setNextAnimation(new Animation(downEmoteId == -2 ? -1 : downEmoteId));
					if (downGraphicId != -1)
						player.setNextSpotAnim(new SpotAnim(downGraphicId));
					if (teleType == MAGIC_TELEPORT) {
						player.voiceEffect(5524);
						player.setNextFaceTile(Tile.of(teleTile.getX(), teleTile.getY() - 1, teleTile.getPlane()));
						player.setFaceAngle(6);
					}
					if (onArrive != null)
						onArrive.accept(player);
					removeDamage = true;
				} else {
					player.resetReceivedHits();
					player.resetReceivedDamage();
					stop();
				}
			}
		}, delay, 0);
		return true;
	}
	
	public static void npcTeleport(NPC npc, int upEmoteId, final int downEmoteId, int upGraphicId, final int downGraphicId, final Tile tile, int delay, final boolean randomize, Consumer<NPC> onArrive) {
		npc.resetWalkSteps();
		npc.setRouteEvent(null);
		npc.getActionManager().forceStop();
		npc.getInteractionManager().forceStop();
		if (upEmoteId != -1)
			npc.setNextAnimation(new Animation(upEmoteId));
		if (upGraphicId != -1)
			npc.setNextSpotAnim(new SpotAnim(upGraphicId));
		WorldTasks.delay(delay, () -> {
			Tile teleTile = tile;
			if (randomize) {
				for (int trycount = 0; trycount < 10; trycount++) {
					teleTile = Tile.of(tile, 2);
					if (World.floorAndWallsFree(teleTile, npc.getSize()))
						break;
					teleTile = tile;
				}
			}
			npc.setNextTile(teleTile);
			if (downEmoteId != -1)
				npc.setNextAnimation(new Animation(downEmoteId == -2 ? -1 : downEmoteId));
			if (downGraphicId != -1)
				npc.setNextSpotAnim(new SpotAnim(downGraphicId));
			if (onArrive != null)
				onArrive.accept(npc);
			npc.resetReceivedDamage();
			npc.resetReceivedHits();
		});
	}
	
	public static final void npcNormalTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 8939, 8941, 1576, 1577, tile, 3, randomize, onArrive);
	}
	
	public static final void npcAncientTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 9599, -2, 1681, -1, tile, 5, randomize, onArrive);
	}
	
	public static final void npcLunarTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 9606, -1, 1685, -1, tile, 5, randomize, onArrive);
	}
	
	public static final void npcDaemonheimTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 13652, 13654, 2602, 2603, tile, 10, randomize, onArrive);
	}
	
	public static final void npcItemTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 9603, -2, 1684, -1, tile, 4, randomize, onArrive);
	}

	public static boolean useHouseTeleport(final Player player) {
		//		if (player.getControllerManager().getController() instanceof HouseController)
		//			return false;
		if (!player.getControllerManager().processMagicTeleport(Tile.of(3217, 3426, 0)) || player.isLocked())
			return false;

		player.lock();
		player.setNextAnimation(new Animation(8939));
		player.setNextSpotAnim(new SpotAnim(1576));

		WorldTasks.schedule(new WorldTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.getControllerManager().removeControllerWithoutCheck();
					stage = 1;
				} else if (stage == 1) {
					player.getControllerManager().magicTeleported(MAGIC_TELEPORT);
					if (!player.getHouse().arriveOutsideHouse()) {
						player.getHouse().setBuildMode(false);
						player.getHouse().enterMyHouse();
						player.setFaceAngle(6);
					} else
						player.setNextTile(Tile.of(player.getHouse().getLocation().getTile()));
					player.setNextAnimation(new Animation(-1));
					stage = 2;
				} else if (stage == 2) {
					player.resetReceivedHits();
					player.resetReceivedDamage();
					player.unlock();
					stop();
				}

			}
		}, 3, 1);
		return true;
	}

	public static boolean useHouseTab(final Player player) {
		if (!player.getControllerManager().processItemTeleport(Tile.of(3217, 3426, 0)) || (player.getControllerManager().getController() instanceof HouseController))
			return false;
		player.lock();
		player.setNextAnimation(new Animation(9597));
		player.setNextSpotAnim(new SpotAnim(1680));
		WorldTasks.schedule(new WorldTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.setNextAnimation(new Animation(4731));
					stage = 1;
				} else if (stage == 1) {
					player.getControllerManager().magicTeleported(ITEM_TELEPORT);
					if (!player.getHouse().arriveOutsideHouse()) {
						player.getHouse().setBuildMode(false);
						player.getHouse().enterMyHouse();
						player.setFaceAngle(6);
					} else
						player.setNextTile(Tile.of(player.getHouse().getLocation().getTile()));
					player.setNextAnimation(new Animation(-1));
					stage = 2;
				} else if (stage == 2) {
					player.resetReceivedHits();
					player.resetReceivedDamage();
					player.unlock();
					stop();
				}

			}
		}, 2, 1);
		return true;
	}

	public static boolean useTeleTab(final Player player, final Tile tile) {
		if (!player.getControllerManager().processItemTeleport(tile))
			return false;
		player.lock();
		player.setNextAnimation(new Animation(9597));
		player.setNextSpotAnim(new SpotAnim(1680));
		WorldTasks.schedule(new WorldTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.setNextAnimation(new Animation(4731));
					stage = 1;
				} else if (stage == 1) {
					Tile teleTile = tile;
					// attemps to randomize tile by 4x4 area
					for (int trycount = 0; trycount < 10; trycount++) {
						teleTile = Tile.of(tile, 2);
						if (World.floorAndWallsFree(teleTile, player.getSize()))
							break;
						teleTile = tile;
					}
					player.setNextTile(teleTile);
					player.getControllerManager().magicTeleported(ITEM_TELEPORT);
					if (player.getControllerManager().getController() == null)
						teleControllersCheck(player, teleTile);
					player.setNextFaceTile(Tile.of(teleTile.getX(), teleTile.getY() - 1, teleTile.getPlane()));
					player.setFaceAngle(6);
					player.setNextAnimation(new Animation(-1));
					stage = 2;
				} else if (stage == 2) {
					player.resetReceivedHits();
					player.resetReceivedDamage();
					player.unlock();
					stop();
				}

			}
		}, 2, 1);
		return true;
	}

	public static void teleControllersCheck(Player player, Tile teleTile) {
		if (DamonheimController.isAtKalaboss(teleTile))
			player.getControllerManager().startController(new DamonheimController());
		else if (GodwarsController.isAtGodwars(teleTile))
			player.getControllerManager().startController(new GodwarsController());
		else if (WildernessController.isAtWild(teleTile))
			player.getControllerManager().startController(new WildernessController());
	}

	public static boolean checkMagicAndRunes(Player player, int magicLevel, boolean deleteRunes, RuneSet runes) {
		if (player.getSkills().getLevel(Constants.MAGIC) < magicLevel) {
			player.sendMessage("You need a magic level of " + magicLevel + " to cast this spell.");
			return false;
		}
		if (runes != null && !runes.meetsRequirements(player))
			return false;
		if (runes != null && deleteRunes) {
			List<Item> toDelete = runes.getRunesToDelete(player);
			for (Item i : toDelete)
				if (i != null)
					player.getInventory().deleteItem(i);
		}
		return true;
	}

	public static boolean checkRunes(Player player, boolean delete, RuneSet runes) {
		if (runes == null)
			return true;
		if (!runes.meetsRequirements(player))
			return false;
		if (delete) {
			List<Item> toDelete = runes.getRunesToDelete(player);
			for (Item i : toDelete)
				if (i != null)
					player.getInventory().deleteItem(i);
		}
		return true;
	}
}
