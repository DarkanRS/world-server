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
package com.rs.game.content.commands.debug;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.engine.command.Commands;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.combat.CombatDefinitions.Spellbook;
import com.rs.game.content.minigames.fightkiln.FightKilnController;
import com.rs.game.content.quests.demonslayer.PlayerVSDelrithController;
import com.rs.game.content.quests.demonslayer.WallyVSDelrithCutscene;
import com.rs.game.content.quests.dragonslayer.DragonSlayer_BoatScene;
import com.rs.game.content.quests.merlinscrystal.MerlinsCrystalCrateScene;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.utils.music.Music;

import java.util.Arrays;

import static com.rs.game.content.randomevents.RandomEvents.attemptSpawnRandom;

@PluginEventHandler
public class Debug {
	private static boolean musicMoveOn = false;
	public static EnterChunkHandler visChunks = new EnterChunkHandler(e -> {
		if (!Settings.getConfig().isDebug())
			return;
		if(musicMoveOn && e.getPlayer() != null && e.getPlayer().hasStarted())
			e.getPlayer().sendMessage("Region: " + e.getPlayer().getRegionId() + ", Chunk: " + e.getChunkId() + ", Genre: " + Music.getGenre(e.getPlayer()).getGenreName());
		if (e.getEntity() instanceof Player player)
			if (player.getNSV().getB("visChunks") && player.hasStarted()) {
				player.devisualizeChunk(e.getEntity().getLastChunkId());
				player.visualizeChunk(e.getChunkId());
				player.sendMessage("Chunk: " + e.getChunkId());
			}
	});

	@ServerStartupEvent
	public static void startup() {
		if (!Settings.getConfig().isDebug())
			return;

		//		Commands.add(Rights.PLAYER, "example [arg1 (optionalArg2)]", "This is an example command to replicate.", (p, args) -> {
		//
		//		});

		Commands.add(Rights.ADMIN, "shapemusic", "Starts showing music shape.", (p, args) -> {
			musicMoveOn = !musicMoveOn;
		});

		Commands.add(Rights.PLAYER, "coords,getpos,mypos,pos,loc", "Gets the coordinates for the tile.", (p, args) -> {
			p.sendMessage("Coords: " + p.getX() + "," + p.getY() + "," + p.getPlane() + ", regionId: " + p.getRegionId() + ", chunkX: " + p.getChunkX() + ", chunkY: " + p.getChunkY());
			p.sendMessage("JagCoords: " + p.getPlane() + "," + p.getRegionX() + "," + p.getRegionY() + "," + p.getXInScene(p.getSceneBaseChunkId()) + "," + p.getYInScene(p.getSceneBaseChunkId()));
			Logger.debug(Debug.class, "coordsCommand", "Tile.of(" + p.getX() + "," + p.getY() + "," + p.getPlane() +")");
		});

		Commands.add(Rights.PLAYER, "search,si,itemid [item name]", "Searches for items containing the words searched.", (p, args) -> {
			p.getPackets().sendDevConsoleMessage("Searching for items containing: " + Arrays.toString(args));
			for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
				boolean contains = true;
				for (String arg : args)
					if (!ItemDefinitions.getDefs(i).getName().toLowerCase().contains(arg.toLowerCase()) || ItemDefinitions.getDefs(i).isLended()) {
						contains = false;
						continue;
					}
				if (contains)
					p.getPackets().sendDevConsoleMessage("Result found: " + i + " - " + ItemDefinitions.getDefs(i).getName() + " " + (ItemDefinitions.getDefs(i).isNoted() ? "(noted)" : "") + "" + (ItemDefinitions.getDefs(i).isLended() ? "(lent)" : ""));
			}
		});

		Commands.add(Rights.PLAYER, "cutscene2 [id]", "Starts crate scene.", (p, args) -> {
			switch (Integer.valueOf(args[0])) {
				case 0 -> {
					p.playCutscene(new WallyVSDelrithCutscene());
				}
				case 1 -> {
					p.getControllerManager().startController(new PlayerVSDelrithController());
				}
				case 2 -> {
					p.getControllerManager().startController(new DragonSlayer_BoatScene());
				}
				case 3 -> {
					p.getControllerManager().startController(new MerlinsCrystalCrateScene());
				}
			}

		});

		Commands.add(Rights.PLAYER, "getcontroller", "Shows current controller", (p, args) -> {
			p.sendMessage("Controller -> " + (p.getControllerManager().getController() == null ? "does not exist..." : p.getControllerManager().getController().getClass().getName()));
		});

		Commands.add(Rights.PLAYER, "fightkiln [wave]", "Starts Fight kiln at a wave", (p, args) -> {
			if(args.length != 1) {
				p.sendMessage("Must be one argument.");
				return;
			}
			if(p.getControllerManager().getController() != null) {
				p.sendMessage("You are already in a minigame, dedicated area(controller)!");
				return;
			}
			if(Integer.valueOf(args[0]) > 37 || Integer.valueOf(args[0]) < 1) {
				p.sendMessage("Invalid wave, must be between 1 and 37.");
				return;
			}
			p.getControllerManager().startController(new FightKilnController(Integer.valueOf(args[0]), true));
		});

		Commands.add(Rights.PLAYER, "random", "Forces a random event.", (p, args) -> {
			attemptSpawnRandom(p, true);
		});

		Commands.add(Rights.PLAYER, "fightcaves", "Marks fight caves as having been completed.", (p, args) -> {
			p.incrementCount("Fight Caves clears");
		});
		
		Commands.add(Rights.PLAYER, "showhitchance", "Toggles the display of your hit chance when attacking opponents.", (p, args) -> {
			p.getNSV().setB("hitChance", !p.getNSV().getB("hitChance"));
			p.sendMessage("Hit chance display: " + p.getNSV().getB("hitChance"));
		});

		Commands.add(Rights.PLAYER, "item,spawn [itemId (amount)]", "Spawns an item with specified id and amount.", (p, args) -> {
			if (ItemDefinitions.getDefs(Integer.valueOf(args[0])).getName().equals("null")) {
				p.sendMessage("That item is unused.");
				return;
			}
			p.getInventory().addItem(Integer.valueOf(args[0]), args.length >= 2 ? Integer.valueOf(args[1]) : 1);
			p.stopAll();
		});

		Commands.add(Rights.PLAYER, "setqstage [questName, stage]", "Resets the specified quest.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.name().toLowerCase().contains(args[0]) && quest.isImplemented()) {
					int stage = Integer.parseInt(args[1]);
					p.getQuestManager().setStage(quest, stage);
					p.sendMessage("Set " + quest.name() + " to stage " + stage);
					return;
				}
		});
		
		Commands.add(Rights.PLAYER, "resetquest [questName]", "Resets the specified quest.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.name().toLowerCase().contains(args[0]) && quest.isImplemented()) {
					p.getQuestManager().resetQuest(quest);
					p.sendMessage("Resetted quest: " + quest.name());
					return;
				}
		});

		Commands.add(Rights.PLAYER, "completequest [questName]", "Resets the specified quest.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.name().toLowerCase().contains(args[0])) {
					p.getQuestManager().completeQuest(quest);
					p.sendMessage("Completed quest: " + quest.name());
					return;
				}
		});

		Commands.add(Rights.PLAYER, "completeallquests", "Completes all quests.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.isImplemented()) {
					p.getQuestManager().completeQuest(quest);
					p.sendMessage("Completed quest: " + quest.name());
				}
		});

		Commands.add(Rights.PLAYER, "resetallquests", "Resets all quests.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.isImplemented()) {
					p.getQuestManager().resetQuest(quest);
					p.sendMessage("Reset quest: " + quest.name());
				}
		});

		Commands.add(Rights.PLAYER, "getqstage [questName]", "Resets the specified quest.", (p, args) -> {
			for (Quest quest : Quest.values())
				if (quest.name().toLowerCase().contains(args[0]) && quest.isImplemented()) {
					int stage = p.getQuestManager().getStage(quest);
					p.sendMessage(quest.name() + " is at stage " + stage);
					return;
				}
		});

		Commands.add(Rights.PLAYER, "master,max", "Maxes all stats out.", (p, args) -> {
			for (int skill = 0; skill < 25; skill++)
				p.getSkills().setXp(skill, 105000000);
			p.reset();
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.PLAYER, "setlevel [skillId level]", "Sets a skill to a specified level.", (p, args) -> {
			if (!p.getEquipment().isEmpty()) {
				p.sendMessage("Please unequip everything you're wearing first.");
				return;
			}
			int skill = Integer.parseInt(args[0]);
			int level = Integer.parseInt(args[1]);
			if (level < 0 || level > (skill == Constants.DUNGEONEERING ? 120 : 99)) {
				p.sendMessage("Please choose a valid level.");
				return;
			}
			if (skill < 0 || skill >= Constants.SKILL_NAME.length) {
				p.sendMessage("Please choose a valid skill.");
				return;
			}
			p.getSkills().set(skill, level);
			p.getSkills().setXp(skill, Skills.getXPForLevel(level));
			p.getAppearance().generateAppearanceData();
			p.reset();
			p.sendMessage("Successfully set " + Constants.SKILL_NAME[skill] + " to " + level + ".");
		});

		Commands.add(Rights.PLAYER, "reset", "Resets all stats to 1.", (p, args) -> {
			for (int skill = 0; skill < 25; skill++)
				p.getSkills().setXp(skill, 0);
			p.getSkills().init();
		});
		
		Commands.add(Rights.PLAYER, "spec", "Restores special attack energy to full.", (p, args) -> {
			p.getCombatDefinitions().resetSpecialAttack();
		});

		Commands.add(Rights.PLAYER, "copy [player name]", "Copies the other player's levels, equipment, and inventory.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null) {
				p.sendMessage("Couldn't find player " + Utils.concat(args) + ".");
				return;
			}
			Item[] equip = target.getEquipment().getItemsCopy();
			for (int i = 0; i < equip.length; i++) {
				if (equip[i] == null)
					continue;

				p.getEquipment().setSlot(i, new Item(equip[i]));
				p.getEquipment().refresh(i);
			}
			Item[] inv = target.getInventory().getItems().getItemsCopy();
			for (int i = 0; i < inv.length; i++) {
				if (inv[i] == null)
					continue;

				p.getInventory().getItems().set(i, new Item(inv[i]));
				p.getInventory().refresh(i);
			}
			for (int i = 0; i < p.getSkills().getLevels().length; i++) {
				p.getSkills().set(i, target.getSkills().getLevelForXp(i));
				p.getSkills().setXp(i, Skills.getXPForLevel(target.getSkills().getLevelForXp(i)));
			}
			p.getAppearance().generateAppearanceData();
		});

		Commands.add(Rights.PLAYER, "spellbook [modern/lunar/ancient]", "Switches to modern, lunar, or ancient spellbooks.", (p, args) -> {
			switch (args[0].toLowerCase()) {
				case "modern":
				case "normal":
					p.getCombatDefinitions().setSpellbook(Spellbook.MODERN);
					break;
				case "ancient":
				case "ancients":
					p.getCombatDefinitions().setSpellbook(Spellbook.ANCIENT);
					break;
				case "lunar":
				case "lunars":
					p.getCombatDefinitions().setSpellbook(Spellbook.LUNAR);
					break;
				default:
					p.sendMessage("Invalid spellbook. Spellbooks are modern, lunar, and ancient.");
					break;
			}
		});

		Commands.add(Rights.PLAYER, "prayers [normal/curses]", "Switches to curses, or normal prayers.", (p, args) -> {
			switch (args[0].toLowerCase()) {
				case "normal":
				case "normals":
					p.getPrayer().setPrayerBook(false);
					break;
				case "curses":
				case "ancients":
					p.getPrayer().setPrayerBook(true);
					break;
				default:
					p.sendMessage("Invalid prayer book. Prayer books are normal and curses.");
					break;
			}
		});

		Commands.add(Rights.PLAYER, "maxbank", "Sets all the item counts in the player's bank to 10m.", (p, args) -> {
			for (Item i : p.getBank().getContainerCopy())
				if (i != null)
					i.setAmount(10500000);
		});

		Commands.add(Rights.PLAYER, "clearbank,emptybank", "Empties the players bank entirely.", (p, args) -> {
			p.sendOptionDialogue("Clear bank?", ops -> {
				ops.add("Yes", () -> p.getBank().clear());
				ops.add("No");
			});
		});

		Commands.add(Rights.PLAYER, "god", "Toggles god mode for the player.", (p, args) -> {
			boolean god = p.getNSV().getB("godMode");
			p.getNSV().setB("godMode", !god);
			p.sendMessage("GODMODE: " + !god);
		});

		Commands.add(Rights.PLAYER, "infrunes", "Toggles infinite runes for the player.", (p, args) -> {
			p.getNSV().setB("infRunes", !p.getNSV().getB("infRunes"));
			p.sendMessage("INFINITE RUNES: " + p.getNSV().getB("infRunes"));
		});

		Commands.add(Rights.PLAYER, "deletesave [string/ID]", "Deletes save attributes", (p, args) -> {
			p.delete(args[0]);
		});

		Commands.add(Rights.PLAYER, "owner", "Makes you owner if your username is the owner.", (p, args) -> {
			if (p.getUsername().equals(Settings.getConfig().getOwnerName())) {
				p.setRights(Rights.OWNER);
				p.sendMessage("You are owner.");
				return;
			}
			p.sendMessage("You are not owner.");
		});

		Commands.add(Rights.PLAYER, "infspec", "Toggles infinite special attack for the player.", (p, args) -> {
			boolean spec = p.getNSV().getB("infSpecialAttack");
			p.getNSV().setB("infSpecialAttack", !spec);
			p.sendMessage("INFINITE SPECIAL ATTACK: " + !spec);
		});

		Commands.add(Rights.PLAYER, "infpray", "Toggles infinite prayer for the player.", (p, args) -> {
			boolean spec = p.getNSV().getB("infPrayer");
			p.getNSV().setB("infPrayer", !spec);
			p.sendMessage("INFINITE PRAYER: " + !spec);
		});

		Commands.add(Rights.PLAYER, "startdung [floor, seed, difficulty, size, complexity]", "Shows dungeon seed", (p, args) -> {
			try {
				int floor = Integer.parseInt(args[0]);
				long seed = Long.parseLong(args[1]);
				int difficulty = Integer.parseInt(args[2]);
				int size = Integer.parseInt(args[3]);
				int complexity = Integer.parseInt(args[4]);

				if (!p.getDungManager().isInsideDungeon()) {
					Logger.debug(Debug.class, "startdung", "floor: " + args[0]);
					Logger.debug(Debug.class, "startdung", "seed: " + args[1]);
					Logger.debug(Debug.class, "startdung", "difficulty: " + args[2]);
					Logger.debug(Debug.class, "startdung", "size: " + args[3]);
					Logger.debug(Debug.class, "startdung", "complexity: " + args[4]);

					p.getDungManager().getParty().setFloor(floor);
					p.getDungManager().getParty().setStartingSeed(seed);
					p.getDungManager().getParty().setDificulty(difficulty);
					p.getDungManager().getParty().setSize(size);
					p.getDungManager().getParty().setComplexity(complexity);
					p.getDungManager().enterDungeon(false);
				} else
					p.getPackets().sendGameMessage("You are already in a dungeon");
			} catch (NullPointerException e) {
				e.printStackTrace();
				p.getPackets().sendGameMessage("You need to be in a party");
			}
		});

		Commands.add(Rights.PLAYER, "droptest", "Drops worn equipment and inventory items to the ground if not null, bound, or a ring of kinship.", (p, args) -> {
			for (Item item : p.getEquipment().getItemsCopy()) {
				if (item == null || item.getName().contains("(b)") || item.getName().contains("kinship"))
					continue;
				World.addGroundItem(item, Tile.of(p.getTile()));
			}
			for (Item item : p.getInventory().getItems().array()) {
				if (item != null)
					Logger.debug(Debug.class, "droptest", item.getName() + ": " + item.getAmount());
				if (item == null || item.getName().contains("(b)") || item.getName().contains("kinship"))
					continue;
				World.addGroundItem(item, Tile.of(p.getTile()));
			}
		});

		Commands.add(Rights.PLAYER, "tele,tp [x y (z)] or [tileHash] or [z,regionX,regionY,localX,localY]", "Teleports the player to a coordinate.", (p, args) -> {
			if (args[0].contains(",")) {
				args = args[0].split(",");
				int plane = Integer.valueOf(args[0]);
				int x = Integer.valueOf(args[1]) << 6 | Integer.valueOf(args[3]);
				int y = Integer.valueOf(args[2]) << 6 | Integer.valueOf(args[4]);
				p.resetWalkSteps();
				p.setNextTile(Tile.of(x, y, plane));
			} else if (args.length == 1) {
				p.resetWalkSteps();
				p.setNextTile(Tile.of(Integer.valueOf(args[0])));
			} else {
				p.resetWalkSteps();
				p.setNextTile(Tile.of(Integer.valueOf(args[0]), Integer.valueOf(args[1]), args.length >= 3 ? Integer.valueOf(args[2]) : p.getPlane()));
			}
		});
		// case "load":
		// if (!player.getInterfaceManager().containsInterface(762) || (Boolean)
		// player.getTemporaryAttributes().get("viewingOtherBank") != null && (Boolean)
		// player.getTemporaryAttributes().get("viewingOtherBank") == true) {
		// player.sendMessage("You must be in your bank screen to do
		// this.");
		// return true;
		// }
		// player.loadLoadout(cmd[1]);
		// return true;
		//
		// case "saveload":
		// player.saveLoadout(cmd[1]);
		// return true;
		//
		// case "delload":
		// player.deleteLoadout(cmd[1]);
		// return true;
		//
		// case "loadouts":
		// player.sendLoadoutText();
		// return true;
	}
}
