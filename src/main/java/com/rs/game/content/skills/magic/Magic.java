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
import com.rs.game.content.combat.CombatSpell;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Teleport;
import com.rs.game.model.entity.interactions.PlayerCombatInteraction;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
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

import static com.rs.game.content.quests.plaguecity.utils.PlagueCityConstantsKt.ARDOUGNE_TELEPORT_UNLOCKED;

@PluginEventHandler
public class Magic {

	public static boolean isSlayerStaff(int weaponId) {
        return switch (weaponId) {
            case 4170, 15486, 15502, 22207, 22213, 22211, 22209 -> true;
            default -> false;
        };
    }

	public static boolean hasStaffOfLight(int weaponId) {
        return switch (weaponId) {
            case 15486, 15502, 22207, 22213, 22211, 22209 -> true;
            default -> false;
        };
    }

	public static boolean checkCombatSpell(Player player, CombatSpell spell, int set, boolean delete) {
		if (spell == CombatSpell.STORM_OF_ARMADYL && !player.isQuestComplete(Quest.RITUAL_OF_MAHJARRAT, "to cast Storm of Armadyl."))
			return false;
		if (set >= 0)
			if (set == 0)
				player.getCombatDefinitions().setAutoCastSpell(spell);
			else
				player.getCombatDefinitions().setManualCastSpell(spell);
		return true;
	}

	public static void manualCast(Player player, Entity target, CombatSpell spell) {
		if (checkCombatSpell(player, spell, 1, false)) {
			player.setNextFaceTile(target.getMiddleTile());
			player.getInteractionManager().setInteraction(new PlayerCombatInteraction(player, target));
		}
	}

	public static InterfaceOnPlayerHandler manualCastPlayer = new InterfaceOnPlayerHandler(false, new int[]{192, 193, 950}, e -> {
		CombatSpell combat = CombatSpell.forId(e.getInterfaceId(), e.getComponentId());
		if (combat != null)
			manualCast(e.getPlayer(), e.getTarget(), combat);
	});

	public static InterfaceOnNPCHandler manualCastNPC = new InterfaceOnNPCHandler(false, new int[]{192, 193, 950}, e -> {
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

	public static void setCombatSpell(Player player, CombatSpell spell) {
		if (player.getCombatDefinitions().getAutoCast() == spell)
			player.getCombatDefinitions().resetSpells(true);
		else
			checkCombatSpell(player, spell, 0, false);
	}

	public static void processLunarSpell(Player player, int componentId, ClientPacket packetId) {
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

	public static void processAncientSpell(Player player, int componentId, ClientPacket packetId) {
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

	public static void processNormalSpell(Player player, int componentId, ClientPacket packetId) {
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
				sendNormalTeleportSpell(player, 40, 48, Tile.of(player.getHouse().getLocation().getTile()), new RuneSet(Rune.AIR, 1, Rune.EARTH, 1, Rune.LAW, 1), () -> {
					player.tele(Tile.of(player.getTile())); //cancel teleport tile movement
					enterHouseAndResetDamage(player);
				});
				break;
			case 51: // camelot
				sendNormalTeleportSpell(player, 45, 55.5, Tile.of(2757, 3478, 0), new RuneSet(Rune.AIR, 5, Rune.LAW, 1));
				break;
			case 57: // ardy
				if (player.isQuestComplete(Quest.PLAGUE_CITY) && player.getBool(ARDOUGNE_TELEPORT_UNLOCKED)) {
					sendNormalTeleportSpell(player, 51, 61, Tile.of(2664, 3305, 0), new RuneSet(Rune.WATER, 2, Rune.LAW, 2));
				} else {
					player.sendMessage("You have not yet learned this spell.");
				}
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

	public static void sendNormalTeleportNoType(Player player, Tile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3, true, TeleType.BYPASS_HOOKS, null);
	}

	public static void sendDamonheimTeleport(Player player, Tile tile) {
		sendTeleportSpell(player, 13652, 13654, 2602, 2603, 0, 0, tile, 10, true, TeleType.MAGIC, null);
	}

	public static void sendLunarTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes) {
		sendTeleportSpell(player, 9606, -1, 1685, -1, level, xp, tile, 5, true, TeleType.MAGIC, runes, null);
	}

	public static void sendLunarTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes, Runnable onArrive) {
		sendTeleportSpell(player, 9606, -1, 1685, -1, level, xp, tile, 5, true, TeleType.MAGIC, runes, onArrive);
	}

	public static void sendAncientTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes, Runnable onArrive) {
		sendTeleportSpell(player, 9599, -2, 1681, -1, level, xp, tile, 5, true, TeleType.MAGIC, runes, onArrive);
	}

	public static void sendAncientTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes) {
		sendTeleportSpell(player, 9599, -2, 1681, -1, level, xp, tile, 5, true, TeleType.MAGIC, runes, null);
	}

	public static void sendNormalTeleportSpell(Player player, int level, double xp, Tile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3, true, TeleType.MAGIC, (RuneSet) null, null);
	}

	public static void sendNormalTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3, true, TeleType.MAGIC, runes, null);
	}

	public static void sendNormalTeleportSpell(Player player, int level, double xp, Tile tile, RuneSet runes, Runnable onArrive) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3, true, TeleType.MAGIC, runes, onArrive);
	}

	public static void sendNormalTeleportSpell(Player player, int level, double xp, Tile tile, Runnable onArrive) {
		sendNormalTeleportSpell(player, level, xp, tile, null, onArrive);
	}

	public static void sendNormalTeleportSpell(Player player, Tile tile) {
		sendNormalTeleportSpell(player, 0, 0, tile);
	}

	public static void sendNormalTeleportSpell(Player player, Tile tile, Runnable onArrive) {
		sendNormalTeleportSpell(player, 0, 0, tile, onArrive);
	}

	public static void sendItemTeleportSpell(Player player, boolean randomize, int upEmoteId, int upGraphicId, int delay, Tile tile) {
		sendItemTeleportSpell(player, randomize, upEmoteId, upGraphicId, delay, tile, null);
	}

	public static boolean sendItemTeleportSpell(Player player, boolean randomize, int upEmoteId, int upGraphicId, int downEmoteId, int downGraphicId, int delay, Tile tile) {
		return sendTeleportSpell(player, upEmoteId, downEmoteId, upGraphicId, downGraphicId, 0, 0, tile, delay, randomize, TeleType.ITEM, null);
	}

	public static void sendItemTeleportSpell(Player player, boolean randomize, int upEmoteId, int upGraphicId, int delay, Tile tile, Runnable onArrive) {
		player.getTempAttribs().setB("glory", true);
		sendTeleportSpell(player, upEmoteId, -2, upGraphicId, -1, 0, 0, tile, delay, randomize, TeleType.ITEM, onArrive);
	}

	public static void pushLeverTeleport(final Player player, final Tile tile) {
		player.setNextAnimation(new Animation(2140));
		player.getTasks().schedule(1, () -> Magic.sendObjectTeleportSpell(player, false, tile, null));
	}

	public static void sendObjectTeleportSpell(Player player, boolean randomize, Tile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3, randomize, TeleType.OBJECT, (RuneSet) null, null);
	}

	public static void sendObjectTeleportSpell(Player player, boolean randomize, Tile tile, Runnable onArrive) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3, randomize, TeleType.OBJECT, (RuneSet) null, onArrive);
	}

	public static void sendDelayedObjectTeleportSpell(Player player, int delay, boolean randomize, Tile tile, Runnable onArrive) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, delay, randomize, TeleType.OBJECT, (RuneSet) null, onArrive);
	}

	public static boolean sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId, int upGraphicId, final int downGraphicId, int level, final double xp, final Tile tile, int delay, final boolean randomize, final TeleType teleType, Runnable onArrive) {
		sendTeleportSpell(player, upEmoteId, downEmoteId, upGraphicId, downGraphicId, level, xp, tile, delay, randomize, teleType, (RuneSet) null, onArrive);
		return randomize;
	}

	public static void sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId, int upGraphicId, final int downGraphicId, int level, final double xp, final Tile tile, int delay, final boolean randomize, final TeleType teleType, RuneSet runes, Runnable onArrive) {
		sendTeleportSpell(player, upEmoteId, downEmoteId, upGraphicId, downGraphicId, level, xp, tile, delay, randomize, teleType, runes, null, onArrive);
	}

	public static void sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId, int upGraphicId, final int downGraphicId, int level, final double xp, final Tile tile, int delay, final boolean randomize, final TeleType teleType, Runnable onCast, Runnable onArrive) {
		sendTeleportSpell(player, upEmoteId, downEmoteId, upGraphicId, downGraphicId, level, xp, tile, delay, randomize, teleType, null, onCast, onArrive);
	}

	public static void sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId, int upGraphicId, final int downGraphicId, int level, final double xp, final Tile tile, int delay, final boolean randomize, final TeleType teleType, RuneSet runes, Runnable onCast, Runnable onArrive) {
		Teleport tele = new Teleport(Tile.of(player.getTile()), tile, teleType, () -> {
			if (player.isLocked())
				return false;
			if (player.getSkills().getLevel(Constants.MAGIC) < level) {
				player.sendMessage("Your Magic level is not high enough for this spell.");
				return false;
			}
			if (runes != null && !runes.meetsRequirements(player))
				return false;
			return true;
		}, () -> {
			if (runes != null) {
				List<Item> runeList = runes.getRunesToDelete(player);
				for (Item rune : runeList)
					if (rune != null)
						player.getInventory().deleteItem(rune);
			}
			player.stopAll();
			if (onCast != null)
				onCast.run();
			if (upEmoteId != -1)
				player.setNextAnimation(new Animation(upEmoteId));
			if (upGraphicId != -1)
				player.setNextSpotAnim(new SpotAnim(upGraphicId));
			if (teleType == TeleType.MAGIC)
				player.voiceEffect(5527, true);
			player.lock(3 + delay);
		}, () -> {
			Tile toTile = tile;
			if (randomize) {
				for (int trycount = 0; trycount < 10; trycount++) {
					toTile = Tile.of(tile, 2);
					if (World.floorAndWallsFree(toTile, player.getSize()))
						break;
					toTile = tile;
				}
			}
			player.tele(toTile);
			if (teleType != TeleType.BYPASS_HOOKS)
				player.getControllerManager().onTeleported(teleType);
			if (xp != 0)
				player.getSkills().addXp(Constants.MAGIC, xp);
			if (downEmoteId != -1)
				player.anim(downEmoteId == -2 ? -1 : downEmoteId);
			if (downGraphicId != -1)
				player.spotAnim(downGraphicId);
			if (teleType == TeleType.MAGIC) {
				player.voiceEffect(5524, true);
				player.setNextFaceTile(Tile.of(toTile.getX(), toTile.getY() - 1, toTile.getPlane()));
				player.setFaceAngle(6);
			}
			if (onArrive != null)
				onArrive.run();
			player.resetReceivedHits();
			player.resetReceivedDamage();
		}, true);
		Teleport.execute(player, tele, delay);
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
			npc.tele(teleTile);
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

	public static void npcNormalTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 8939, 8941, 1576, 1577, tile, 3, randomize, onArrive);
	}

	public static void npcAncientTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 9599, -2, 1681, -1, tile, 5, randomize, onArrive);
	}

	public static void npcLunarTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 9606, -1, 1685, -1, tile, 5, randomize, onArrive);
	}

	public static void npcDaemonheimTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 13652, 13654, 2602, 2603, tile, 10, randomize, onArrive);
	}

	public static void npcItemTeleport(NPC npc, Tile tile, boolean randomize, Consumer<NPC> onArrive) {
		npcTeleport(npc, 9603, -2, 1684, -1, tile, 4, randomize, onArrive);
	}

	private static void enterHouseAndResetDamage(Player player) {
		if (!player.getHouse().arriveOutsideHouse()) {
			player.getHouse().setBuildMode(false);
			player.getHouse().enterMyHouse();
			player.setFaceAngle(6);
		} else
			player.tele(Tile.of(player.getHouse().getLocation().getTile()));
		player.resetReceivedHits();
		player.resetReceivedDamage();
		player.setFaceAngle(6);
		player.setNextAnimation(new Animation(-1));
	}

	public static void useHouseTab(Player player) {
		Teleport teleport = new Teleport(Tile.of(player.getTile()), player.getHouse().getLocation().getTile(), TeleType.ITEM, null, null, () -> {
			player.getInventory().deleteItem(8013, 1);
			enterHouseAndResetDamage(player);
		}, true);
		if (player.isLocked() || (teleport.meetsRequirements() != null && !teleport.meetsRequirements().get()))
			return;
		if (!player.getControllerManager().processTeleport(teleport))
			return;
		player.stopAll();
		player.lock();
		player.sync(9597, 1680);
		player.getTasks().scheduleTimer(1, 0, tick -> {
			switch(tick) {
				case 0 -> player.anim(4731);
				case 2 -> {
					player.getControllerManager().onTeleported(teleport.type());
					if (teleport.end() != null)
						teleport.end().run();
					player.anim(-1);
				}
				case 3 -> {
					player.resetReceivedHits();
					player.resetReceivedDamage();
					player.unlock();
					return false;
				}
			}
			return true;
		});
	}

	public static void useTeleTab(Player player, Tile tile, int tabId) {
		Teleport teleport = new Teleport(Tile.of(player.getTile()), tile, TeleType.ITEM, null, null, () -> player.getInventory().deleteItem(tabId, 1), true);
		if (player.isLocked() || (teleport.meetsRequirements() != null && !teleport.meetsRequirements().get()))
			return;
		if (!player.getControllerManager().processTeleport(teleport))
			return;
		player.stopAll();
		player.lock();
		player.sync(9597, 1680);
		player.getTasks().scheduleTimer(1, 0, tick -> {
			switch(tick) {
				case 0 -> player.anim(4731);
				case 2 -> {
					player.getControllerManager().onTeleported(teleport.type());
					player.tele(teleport.destination());
					if (teleport.end() != null)
						teleport.end().run();
					player.anim(-1);
				}
				case 3 -> {
					player.resetReceivedHits();
					player.resetReceivedDamage();
					player.unlock();
					return false;
				}
			}
			return true;
		});
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
