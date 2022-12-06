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
package com.rs.game.content.commands;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.lang.SuppressWarnings;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectType;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.content.pet.Pet;
import com.rs.game.content.skills.summoning.Familiar;
import com.rs.game.model.entity.BodyGlow;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.GroundItem.GroundItemType;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;

public final class Commands {

	private static Map<Rights, Map<String , Command>> COMMANDS = new HashMap<>();
	private static Map<Rights, Set<Command>> UNIQUE_COMMANDS = new HashMap<>();

	static {
		for (Rights r : Rights.values()) {
			COMMANDS.put(r, new HashMap<String, Command>());
			UNIQUE_COMMANDS.put(r, new HashSet<Command>());
		}
	}

	public static Set<Command> getCommands(Rights rights) {
		return UNIQUE_COMMANDS.get(rights);
	}

	public static void add(Rights rights, String usage, String description, CommandExecution execution) {
		String aliases = usage.contains(" ") ? usage.substring(0, usage.indexOf(" ")) : usage;
		Command command = new Command(usage, description, execution);
		UNIQUE_COMMANDS.get(rights).add(command);
		if (aliases.contains(",")) {
			String[] aliasesArr = aliases.split(",");
			for (String alias : aliasesArr)
				COMMANDS.get(rights).put(alias, command);
		} else
			COMMANDS.get(rights).put(aliases, command);
	}

	public static boolean executeCommand(Rights rights, Player player, String name, String[] args) {
		Command command = COMMANDS.get(rights).get(name);
		if (command == null)
			return false;
		try {
			command.execute(player, args);
		} catch (Throwable e) {
			if (Settings.getConfig().isDebug())
				e.printStackTrace();
			player.sendMessage("Error handling command. Proper usage is ::" + command.getUsage());
		}
		return true;
	}

	public static boolean processCommand(Player player, String commandStr, boolean console, boolean clientCommand) {
		if (commandStr.length() == 0)
			return false;
		String[] cmd = commandStr.split(" ");
		if ((cmd.length == 0) || (cmd.length == 0))
			return false;

		String[] args = new String[cmd.length - 1];
		for (int i = 1; i < cmd.length; i++)
			args[i - 1] = cmd[i];

		for (int i = Rights.values().length-1;i >= 0;i--) {
			if (player.getRights().ordinal() < Rights.values()[i].ordinal())
				continue;
			if (executeCommand(Rights.values()[i], player, cmd[0].toLowerCase(), args)) {
				if (Rights.values()[i] != Rights.PLAYER)
					WorldDB.getLogs().logCommand(player.getUsername(), commandStr);
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unused")
	private static boolean REFACTOR_ALL_THESE_INTO_NEW_SYSTEM(final Player p, String[] casedCmd, String[] args, boolean console, boolean clientCommand) {
		switch (args[0]) {

		case "cc":
			p.getTreasureTrailsManager().openReward(Integer.valueOf(args[1]));
			return true;

		case "companim":
			if (Integer.valueOf(args[1]) > Utils.getNPCDefinitionsSize())
				return true;
			NPCDefinitions defs = NPCDefinitions.getDefs(Integer.valueOf(args[1]));
			if (defs == null)
				return true;
			p.getPackets().sendDevConsoleMessage(Integer.valueOf(args[1]) + ": " + defs.getCompatibleAnimations().toString());
			p.sendMessage(Integer.valueOf(args[1]) + ": " + defs.getCompatibleAnimations().toString());
			Logger.debug(Commands.class, "REFACTOR.compAnim", defs.getCompatibleAnimations().toString());
			return true;

		case "trenttitle":
			String title = "<shad=000000><col=FF0000>T</col><col=FF6600>h</col><col=FFFF00>e</col> <col=00FF00>B</col><col=0000FF>o</col><col=6600FF>s</col><col=FF00FF>s";
			p.setTitle(title);
			p.setTitleColor(null);
			p.setTitleShading(null);
			return true;

		case "dailies":
			p.processDailyTasks();
			return true;

		case "resethouse":
			p.sendOptionDialogue("Delete your house?", ops -> {
				ops.add("Yes", () -> p.getHouse().reset());
				ops.add("No");
			});
			return true;

		case "resettask":
			p.getSlayer().removeTask();
			p.updateSlayerTask();
			return true;

		case "snow":
			for (int x = 0; x < 5; x++)
				for (int y = 0; y < 5; y++)
					World.spawnObject(new GameObject(3701, ObjectType.SCENERY_INTERACT, 1, p.getX() + (x * 10), p.getY() + (y * 10), 3));
			for (int x = 0; x < 5; x++)
				for (int y = 0; y < 5; y++)
					World.spawnObject(new GameObject(3701, ObjectType.SCENERY_INTERACT, 1, p.getX() - (x * 10), p.getY() - (y * 10), 3));
			for (int x = 0; x < 5; x++)
				for (int y = 0; y < 5; y++)
					World.spawnObject(new GameObject(3701, ObjectType.SCENERY_INTERACT, 1, p.getX() + (x * 10), p.getY() - (y * 10), 3));
			for (int x = 0; x < 5; x++)
				for (int y = 0; y < 5; y++)
					World.spawnObject(new GameObject(3701, ObjectType.SCENERY_INTERACT, 1, p.getX() - (x * 10), p.getY() + (y * 10), 3));
			return true;

		case "glowme":
			p.setNextBodyGlow(new BodyGlow(500, Integer.valueOf(args[1]), Integer.valueOf(args[2]), Integer.valueOf(args[3]), Integer.valueOf(args[4])));
			return true;

			//		case "getaccs":
			//			ArrayList<String> ips = Utils.getCharactersWithIP(args[1]);
			//			p.getPackets().sendRunScriptReverse(1207, new Object[] { ips.size() });
			//			p.getInterfaceManager().sendInterface(275);
			//			p.getPackets().setIFText(275, 1, "Characters with IP:  " + args[1]);
			//			int numa = 10;
			//			for (String ip : ips) {
			//				if (numa > 288)
			//					break;
			//				if (ip == null)
			//					continue;
			//				p.getPackets().setIFText(275, numa, ip);
			//				numa++;
			//			}
			//			return true;

		case "script":
			p.getPackets().sendRunScript(Integer.valueOf(args[1]));
			return true;

		case "kinship":
			int sniper = 1;
			int berserker = 3;
			int tactician = 4;
			int tank = 5;
			int artisan = 0;
			int gatherer = 0;
			int medic = 0;
			int blitzer = 0;
			int blaster = 0;
			int blazer = 0;
			int desperado = 0;
			int keenEye = 0;
			p.getInterfaceManager().sendInterface(993);
			p.getVars().setVar(1776, (sniper << 28) + (berserker << 24) + (tactician << 20) + (tank << 16));
			p.getVars().setVar(1851, (artisan << 28) + (gatherer << 24) + (medic << 20) + (blitzer << 16) + (blaster << 12) + (blazer << 8) + (desperado << 4) + keenEye);
			return true;

		case "loadouts":
			String loadouts = "";
			for (String keys : p.getSavingAttributes().keySet())
				if (keys.contains("loadoutinv"))
					loadouts += keys.replace("loadoutinv", "") + ", ";
			p.sendMessage(loadouts);
			return true;

		case "resetbrew":
			p.getKeldagrimBrewery().reset();
			p.getPhasmatysBrewery().reset();
			return true;

		case "ferment":
			p.getKeldagrimBrewery().ferment();
			p.getKeldagrimBrewery().updateVars();
			p.getPhasmatysBrewery().ferment();
			p.getPhasmatysBrewery().updateVars();
			return true;

		case "proj":
			World.sendProjectile(WorldTile.of(p.getX() + 5, p.getY(), p.getPlane()), WorldTile.of(p.getX() - 5, p.getY(), p.getPlane()), Integer.valueOf(args[1]), 40, 40, 0, 0.2, 0, 0);
			return true;

		case "house":
			p.getHouse().enterMyHouse();
			return true;

		case "hintgame":
			for (Player players : World.getPlayers())
				players.getHintIconsManager().addHintIcon(p.getX(), p.getY(), 0, 0, 2, 0, -1, true);
			return true;

		case "endhintgame":
			for (Player players : World.getPlayers())
				players.getHintIconsManager().removeAll();
			return true;

		case "dropitem":
			World.addGroundItem(new Item(Integer.valueOf(args[1]), 1), WorldTile.of(p.getX(), p.getY(), p.getPlane()));
			return true;

		case "trolldropitem":
			for (Player players : World.getPlayers())
				players.getPackets().sendGroundItem(new GroundItem(new Item(Integer.valueOf(args[1]), 1), WorldTile.of(p.getX(), p.getY(), p.getPlane()), players.getUsername(), GroundItemType.NORMAL));
			return true;

		case "deathnpcs":
			if (Settings.isOwner(p.getUsername().toLowerCase()))
				for (NPC npc : World.getNPCs()) {
					if (npc instanceof Familiar || npc instanceof Pet)
						continue;
					if (Utils.getDistance(npc.getTile(), p.getTile()) < 9)
						npc.sendDeath(p);
				}
			return true;

		case "cutscene":
			p.getPackets().sendCutscene(Integer.parseInt(args[1]));
			return true;
		}
		return false;
	}

	public static void sendYell(Player player, String message, boolean staffYell) {
		if (player.getAccount().isMuted()) {
			player.sendMessage("You are muted. The mute will be lifted at " + player.getAccount().getUnmuteDate());
			return;
		}
		if (staffYell) {
			World.sendWorldMessage("[<col=ff0000>Staff Yell</col>] " + (player.hasRights(Rights.MOD) ? "<img=1>" : "") + player.getDisplayName() + ": <col=ff0000>" + message + ".</col>", true);
			return;
		}
		if (message.length() > 100)
			message = message.substring(0, 100);

		if (!player.hasRights(Rights.ADMIN)) {
			String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
			for (String s : invalid)
				if (message.contains(s)) {
					player.sendMessage("You cannot add additional code to the message.");
					return;
				}
			if (player.getRights() == Rights.MOD)
				World.sendWorldMessage("[<img=0><col=0077FF>Moderator</col>] <img=0>" + player.getDisplayName() + ": <col=0077FF>" + message + "", false);
			else
				World.sendWorldMessage("[<col=218736>Yell</col>] " + player.getDisplayName() + ": <col=218736>" + message + "", false);
			return;
		}
		if (Settings.isOwner(player.getUsername().toLowerCase())) {
			World.sendWorldMessage("[<img=1><col=ff0000><shad=000000>Owner/Developer</shad></col>] <img=1>" + player.getDisplayName() + ": <col=ff0000><shad=000000>" + message + "", false);
			return;
		}
		World.sendWorldMessage("[<img=1><col=ff0000>Admin</col>] <img=1>" + player.getDisplayName() + ": <col=ff0000>" + message + "", false);
	}
}