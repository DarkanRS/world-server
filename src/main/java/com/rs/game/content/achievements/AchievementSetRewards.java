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
package com.rs.game.content.achievements;

import com.rs.game.content.DropCleanersKt;
import com.rs.game.content.SkillCapeCustomizer;
import com.rs.game.content.achievements.AchievementDef.Area;
import com.rs.game.content.achievements.AchievementDef.Difficulty;
import com.rs.game.content.interfacehandlers.ItemSelectWindow;
import com.rs.game.content.skills.magic.Alchemy;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.TeleType;
import com.rs.game.content.world.areas.rellekka.Rellekka;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.*;

import static com.rs.game.content.interfacehandlers.ItemSelectWindow.openItemSelectWindow;

@PluginEventHandler
public class AchievementSetRewards {

	private static final Tile ARDY_FARM = Tile.of(2664, 3375, 0);
	private static final Tile KANDARIN_MONASTERY = Tile.of(2607, 3222, 0);
	
	public static NPCDropHandler handleNotingDagBones = new NPCDropHandler(new Object[] { 2881, 2882, 2883 }, new Object[] { 6729 }, e -> {
		if (e.getPlayer().getEquipment().getBootsId() == 19766)
			e.getItem().setId(e.getItem().getDefinitions().getCertId());
		else
			if (DropCleanersKt.bonecrush(e.getPlayer(), e.getItem()))
				e.deleteItem();
	});

	public static ItemClickHandler handleArdougneCloak = new ItemClickHandler(new Object[] { 15345, 15347, 15349, 19748, 20767, 20769, 20771 }, new String[] { "Teleports", "Teleport", "Kandarin Monastery", "Summoning-restore", "Ardougne Farm", "Customise", "Features" }, e -> {
		if (e.getOption().equals("Teleports") || e.getOption().equals("Features")) {
			e.getPlayer().sendOptionDialogue("Where would you like to go?", ops -> {
				ops.add("Ardougne Farm", () -> {
					if (e.getPlayer().getDailyB("ardyCloakFarmTele") && (e.getItem().getId() == 15345 || e.getItem().getId() == 15347 || e.getItem().getId() == 15349)) {
						e.getPlayer().sendMessage("You already used your teleport for today.");
						return;
					}
					Magic.sendTeleportSpell(e.getPlayer(), 4454, 12438, 761, 762, 0, 0, ARDY_FARM, 4, true, TeleType.ITEM, () -> e.getPlayer().setDailyB("ardyCloakFarmTele", true));
				});
				ops.add("Kandarin Monastery", () -> Magic.sendTeleportSpell(e.getPlayer(), 12441, 12442, 2172, 2173, 0, 0, KANDARIN_MONASTERY, 3, true, TeleType.ITEM, null));
				ops.add("Nowhere.");
			});
		} else if (e.getOption().contains("Kandarin Monastery") || e.getOption().equals("Teleport"))
			Magic.sendTeleportSpell(e.getPlayer(), 12441, 12442, 2172, 2173, 0, 0, KANDARIN_MONASTERY, 3, true, TeleType.ITEM, null);
		else if (e.getOption().contains("Ardougne Farm")) {
			if (e.getPlayer().getDailyB("ardyCloakFarmTele") && (e.getItem().getId() == 15345 || e.getItem().getId() == 15347 || e.getItem().getId() == 15349)) {
				e.getPlayer().sendMessage("You already used your teleport for today.");
				return;
			}
			Magic.sendTeleportSpell(e.getPlayer(), 4454, 12438, 761, 762, 0, 0, ARDY_FARM, 4, true, TeleType.ITEM, () -> e.getPlayer().setDailyB("ardyCloakFarmTele", true));
		} else if (e.getOption().equals("Summoning-restore")) {
			if (e.getPlayer().getDailyB("ardyCloakSumm")) {
				e.getPlayer().sendMessage("You've already restored your summoning points today.");
				return;
			}
			e.getPlayer().getSkills().set(Constants.SUMMONING, e.getPlayer().getSkills().getLevelForXp(Constants.SUMMONING));
			e.getPlayer().setDailyB("ardyCloakSumm", true);
			e.getPlayer().setNextSpotAnim(new SpotAnim(7));
			e.getPlayer().sendMessage("You restore your summoning points.");
		} else if (e.getOption().equals("Customise"))
			SkillCapeCustomizer.startCustomizing(e.getPlayer(), e.getItem().getId());
	});

	public static ItemClickHandler handleExplorersRing = new ItemClickHandler(new Object[] { 13560, 13561, 13562, 19760 }, new String[] { "Cabbage-port", "Run-replenish", "Low-alchemy", "Low-Alchemy", "High-Alchemy", "Superheat", "Alchemy-or-superheat" }, e -> {
		switch(e.getOption()) {
		case "Cabbage-port":
			Magic.sendTeleportSpell(e.getPlayer(), 9984, 9986, 1731, 1732, 0, 0, Tile.of(3053, 3291, 0), 4, true, TeleType.ITEM, null);
			break;
		case "Run-replenish":
			if ((e.getItem().getId() == 13560 && e.getPlayer().getDailyI("eRingRunRep") >= 1) || (e.getItem().getId() == 13561 && e.getPlayer().getDailyI("eRingRunRep") >= 2)) {
				e.getPlayer().sendMessage("You've already used up your run replenishes today.");
				return;
			}
			if (e.getPlayer().getDailyI("eRingRunRep") >= 3) {
				e.getPlayer().sendMessage("You've already used up your run replenishes today.");
				return;
			}
			e.getPlayer().incDailyI("eRingRunRep");
			e.getPlayer().anim(new Animation(9988));
			e.getPlayer().spotAnim(new SpotAnim(1733));
			e.getPlayer().restoreRunEnergy(50);
			e.getPlayer().soundEffect(5035, true);
			e.getPlayer().sendMessage("The ring replenishes your run energy.");
			break;
		case "Low-alchemy":
		case "Low-Alchemy":
			openItemSelectWindow(e.getPlayer(), 0, ItemSelectWindow.Mode.ALCHEMY);
			break;
		case "High-alchemy":
		case "High-Alchemy":
			openItemSelectWindow(e.getPlayer(), 1, ItemSelectWindow.Mode.ALCHEMY);
			break;
		case "Superheat":
			openItemSelectWindow(e.getPlayer(), 0, ItemSelectWindow.Mode.SUPERHEAT);
			break;
		case "Alchemy-or-superheat":
			e.getPlayer().sendOptionDialogue("What would you like to do?", ops -> {
				ops.add("Low-alchemy", () -> openItemSelectWindow(e.getPlayer(), 0, ItemSelectWindow.Mode.ALCHEMY));
				ops.add("High-alchemy", () -> openItemSelectWindow(e.getPlayer(), 1, ItemSelectWindow.Mode.ALCHEMY));
				ops.add("Superheat", () -> openItemSelectWindow(e.getPlayer(), 2, ItemSelectWindow.Mode.SUPERHEAT));
			});
			break;
		}
	});

	public static void handleAlchemyActions(Player player, Item item, int type) {
		if (item == null) {
			return;
		}
		switch (type) {
			case 0: {
				if (player.getDailyI("itemSelectLowAlchs") >= 30) {
					player.sendMessage("You have used up all your low alchemy charges for the day.");
					return;
				}
				if (Alchemy.handleAlchemy(player, item, true, false)) {
					player.incDailyI("itemSelectLowAlchs");
				}
				break;
			}
			case 1: {
				if (player.getDailyI("itemSelectHighAlchs") >= 15) {
					player.sendMessage("You have used up all your high alchemy charges for the day.");
					return;
				}
				if (Alchemy.handleAlchemy(player, item, false, false)) {
					player.incDailyI("itemSelectHighAlchs");
				}
				break;
			}
			case 2:
				handleSuperheatAction(player, item);
				break;
		}
	}

	public static void handleSuperheatAction(Player player, Item item) {
		if (item == null) {
			return;
		}
		if (player.getDailyI("itemSelectSuperheats") >= 27) {
			player.sendMessage("You have used up all your superheat charges for the day.");
			return;
		}
		if (Alchemy.handleSuperheat(player, item, false)) {
			player.incDailyI("itemSelectSuperheats");
		}
	}

	public static ItemEquipHandler handleKaramjaGloveLadderUnlock = new ItemEquipHandler(new Object[] { 11140, 19754 }, e -> e.getPlayer().getVars().setVarBit(3610, e.equip() ? 1 : 0));

	public static ObjectClickHandler handleUndergroundGem = new ObjectClickHandler(new Object[] { 23584, 23586 }, e -> {
		if (e.getObjectId() == 23584)
			e.getPlayer().useLadder(Tile.of(2825, 2997, 0));
		else
			e.getPlayer().useLadder(Tile.of(2838, 9387, 0));
	});

	public static ItemClickHandler handleKaramjaGlovesTele = new ItemClickHandler(new Object[] { 11140, 19754 }, new String[] { "Teleport" }, e -> Magic.sendNormalTeleportSpell(e.getPlayer(), Tile.of(2841, 9387, 0)));

	public static ObjectClickHandler handleCooksGuildSpecialDoor = new ObjectClickHandler(new Object[] { 26810 }, e -> {
		if (AchievementDef.meetsRequirements(e.getPlayer(), Area.VARROCK, Difficulty.HARD))
			Doors.handleDoor(e.getPlayer(), e.getObject());
		else
			e.getPlayer().sendMessage("You must have the requirements for the Varrock Hard Achievement Set to enter here.");
	});

	public static ItemClickHandler handleFaladorShieldOps = new ItemClickHandler(new Object[] { 14577, 14578, 14579, 19749 }, new String[] { "Prayer-restore", "Emote" }, e -> {
		if (e.getOption().equals("Emote"))
			switch(e.getItem().getId()) {
			case 14577:
				e.getPlayer().setNextAnimation(new Animation(13843));
				break;
			case 14578:
				e.getPlayer().setNextAnimation(new Animation(13844));
				break;
			case 14579:
				e.getPlayer().setNextAnimation(new Animation(13845));
				e.getPlayer().setNextSpotAnim(new SpotAnim(1965));
				break;
			case 19749:
				e.getPlayer().setNextAnimation(new Animation(14713));
				e.getPlayer().setNextSpotAnim(new SpotAnim(1965));
				break;
			}
		else if (e.getOption().equals("Prayer-restore")) {
			if (e.getPlayer().getDailyI("fallyShieldPrayer") >= (e.getItem().getId() == 19749 ? 2 : 1)) {
				e.getPlayer().sendMessage("You've already used up your prayer restores for the day.");
				return;
			}
			if (e.getPlayer().getPrayer().hasFullPoints()) {
				e.getPlayer().sendMessage("You already have full prayer points.");
				return;
			}
			switch(e.getItem().getId()) {
			case 14577:
				e.getPlayer().getPrayer().restorePrayer((e.getPlayer().getSkills().getLevelForXp(Constants.PRAYER) * 10) * 0.25);
				break;
			case 14578:
				e.getPlayer().getPrayer().restorePrayer((e.getPlayer().getSkills().getLevelForXp(Constants.PRAYER) * 10) * 0.5);
				break;
			case 14579:
			case 19749:
				e.getPlayer().getPrayer().restorePrayer(e.getPlayer().getSkills().getLevelForXp(Constants.PRAYER) * 10);
				break;
			}
			e.getPlayer().incDailyI("fallyShieldPrayer");
			e.getPlayer().setNextSpotAnim(new SpotAnim(1964));
			e.getPlayer().sendMessage("The shield replenishes your prayer points.");
		}
	});
	
	public static ItemClickHandler handleMorytaniaLegs = new ItemClickHandler(new Object[] { 24135, 24136, 24137 }, new String[] { "Slime Pit Teleport" }, e -> {
		int teleLimit = switch(e.getItem().getId()) {
		case 24135 -> 5;
		case 24136 -> 10;
		default -> 20;
	};
	if (e.getOption().equals("Slime Pit Teleport")) {
		if (e.getPlayer().getDailyI("moryLegSlimeTeles") >= teleLimit) {
			e.getPlayer().sendMessage("You already used your teleports for today.");
			return;
		}
		Magic.sendTeleportSpell(e.getPlayer(), 8939, 8941, 1678, 1679, 0, 0, Tile.of(3683, 9888, 0), 3, false, TeleType.ITEM, () -> e.getPlayer().incDailyI("moryLegSlimeTeles"));
	}
	});
	
	public static ItemClickHandler handleFremmyBoots = new ItemClickHandler(new Object[] { 14571, 14572, 14573, 19766 }, new String[] { "Operate", "Contact the Fossegrimen", "Free lyre teleport" }, e -> {
		switch(e.getOption()) {
			case "Operate" -> e.getPlayer().sendOptionDialogue(ops -> {
				ops.add("Contact the Fossegrimen", () -> Rellekka.rechargeLyre(e.getPlayer()));
				ops.add("Free lyre teleport", () -> e.getPlayer().startConversation(Rellekka.getLyreTeleOptions(e.getPlayer(), null, true)));
			});

			case "Free lyre teleport" -> e.getPlayer().startConversation(Rellekka.getLyreTeleOptions(e.getPlayer(), null, true));
			case "Contact the Fossegrimen" -> Rellekka.rechargeLyre(e.getPlayer());
		}
	});
}
