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
package com.rs.game.player.content.commands.debug;

import static com.rs.game.player.content.randomevents.RandomEvents.attemptSpawnRandom;

import java.util.Arrays;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.commands.Commands;
import com.rs.game.player.quests.Quest;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.EnterChunkHandler;



@PluginEventHandler
public class Debug {

	public static EnterChunkHandler handleTempleChunks = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (!Settings.getConfig().isDebug())
				return;
			if (e.getEntity() instanceof Player player)
				if (player.getNSV().getB("visChunks") && player.hasStarted()) {
					player.devisualizeChunk(e.getEntity().getLastChunkId());
					player.visualizeChunk(e.getChunkId());
					player.sendMessage("Chunk: " + e.getChunkId());
				}
		}
	};

	public static ButtonClickHandler debugButtons = new ButtonClickHandler() {
		@Override
		public boolean handleGlobal(ButtonClickEvent e) {
			if (Settings.getConfig().isDebug())
				System.out.println(e.getInterfaceId() + ", " + e.getComponentId() + ", " + e.getSlotId() + ", " + e.getSlotId2());
			return false;
		}
		@Override
		public void handle(ButtonClickEvent e) { }
	};

	@ServerStartupEvent
	public static void startup() {
		if (!Settings.getConfig().isDebug())
			return;

		//		Commands.add(Rights.PLAYER, "example [arg1 (optionalArg2)]", "This is an example command to replicate.", (p, args) -> {
		//
		//		});

		Commands.add(Rights.PLAYER, "coords,getpos,mypos,pos,loc", "Gets the coordinates for the tile.", (p, args) -> {
			p.sendMessage("Coords: " + p.getX() + "," + p.getY() + "," + p.getPlane() + ", regionId: " + p.getRegionId() + ", chunkX: " + p.getChunkX() + ", chunkY: " + p.getChunkY());
			p.sendMessage("JagCoords: " + p.getPlane() + ","+p.getRegionX()+","+p.getRegionY()+","+p.getXInScene(p.getSceneBaseChunkId())+","+p.getYInScene(p.getSceneBaseChunkId()));
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

		Commands.add(Rights.PLAYER, "random", "Forces a random event.", (p, args) -> {
			attemptSpawnRandom(p, true);
		});

		Commands.add(Rights.PLAYER, "showhitchance", "Toggles the display of your hit chance when attacking opponents.", (p, args) -> {
			p.getNSV().setB("hitChance", p.getNSV().getB("hitChance"));
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
					p.getQuestManager().setStage(quest, stage, true);
					p.sendMessage("Set " + quest.name() + " to stage " + stage);
					return;
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

		Commands.add(Rights.PLAYER, "copy [player name]", "Copies the other player's levels, equipment, and inventory.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target == null) {
				p.sendMessage("Couldn't find player " + Utils.concat(args) + ".");
				return;
			}
			Item[] equip = target.getEquipment().getItemsCopy();
			for (int i = 0; i < equip.length; i++) {
				if (equip[i] == null)
					continue;

				p.getEquipment().set(i, new Item(equip[i]));
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
			switch(args[0].toLowerCase()) {
			case "modern":
			case "normal":
				p.getCombatDefinitions().setSpellBook(0);
				break;
			case "ancient":
			case "ancients":
				p.getCombatDefinitions().setSpellBook(1);
				break;
			case "lunar":
			case "lunars":
				p.getCombatDefinitions().setSpellBook(2);
				break;
			default:
				p.sendMessage("Invalid spellbook. Spellbooks are modern, lunar, and ancient.");
				break;
			}
		});

		Commands.add(Rights.PLAYER, "prayers [normal/curses]", "Switches to curses, or normal prayers.", (p, args) -> {
			switch(args[0].toLowerCase()) {
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
			p.sendOptionDialogue("Clear bank?", new String[] { "Yes", "No" }, new DialogueOptionEvent() {
				@Override
				public void run(Player player) {
					if (getOption() == 1)
						player.getBank().clear();
				}
			});
		});

		Commands.add(Rights.PLAYER, "god", "Toggles god mode for the player.", (p, args) -> {
			boolean god = p.getNSV().getB("godMode");
			p.getNSV().setB("godMode", !god);
			p.sendMessage("GODMODE: " + !god);
		});

		Commands.add(Rights.PLAYER, "deletesave [string/ID]", "Deletes save attributes", (p, args) -> {
			p.delete(args[0]);
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
					System.out.println("floor: " + args[0]);
					System.out.println("seed: " + args[1]);
					System.out.println("difficulty: " + args[2]);
					System.out.println("size: " + args[3]);
					System.out.println("complexity: " + args[4]);

					p.getDungManager().getParty().setFloor(floor);
					p.getDungManager().getParty().setStartingSeed(seed);
					p.getDungManager().getParty().setDificulty(difficulty);
					p.getDungManager().getParty().setSize(size);
					p.getDungManager().getParty().setComplexity(complexity);
					p.getDungManager().enterDungeon(false);
				} else
					p.getPackets().sendGameMessage("You are already in a dungeon");
			} catch(NullPointerException e) {
				e.printStackTrace();
				p.getPackets().sendGameMessage("You need to be in a party");
			}
		});

		Commands.add(Rights.PLAYER, "droptest", "Drops worn equipment and inventory items to the ground if not null, bound, or a ring of kinship.", (p, args) -> {
			for (Item item : p.getEquipment().getItemsCopy()) {
				if (item == null || item.getName().contains("(b)") || item.getName().contains("kinship"))
					continue;
				World.addGroundItem(item, new WorldTile(p));
			}
			for (Item item : p.getInventory().getItems().getItems()) {
				if(item != null)
					System.out.println(item.getName() + ": " + item.getAmount());
				if (item == null || item.getName().contains("(b)") || item.getName().contains("kinship"))
					continue;
				World.addGroundItem(item, new WorldTile(p));
			}
		});
		
		Commands.add(Rights.PLAYER, "tele,tp [x y (z)] or [tileHash] or [z,regionX,regionY,localX,localY]", "Teleports the player to a coordinate.", (p, args) -> {
			if (args[0].contains(",")) {
				args = args[0].split(",");
				int plane = Integer.valueOf(args[0]);
				int x = Integer.valueOf(args[1]) << 6 | Integer.valueOf(args[3]);
				int y = Integer.valueOf(args[2]) << 6 | Integer.valueOf(args[4]);
				p.resetWalkSteps();
				p.setNextWorldTile(new WorldTile(x, y, plane));
			} else if (args.length == 1) {
				p.resetWalkSteps();
				p.setNextWorldTile(new WorldTile(Integer.valueOf(args[0])));
			} else {
				p.resetWalkSteps();
				p.setNextWorldTile(new WorldTile(Integer.valueOf(args[0]), Integer.valueOf(args[1]), args.length >= 3 ? Integer.valueOf(args[2]) : p.getPlane()));
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
