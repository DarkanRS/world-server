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
package com.rs.game.content;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.util.Utils;
import com.rs.plugin.handlers.LoginHandler;

public class AchievementTitles {

	public static String[] gamebreakerVariations = { " <col=DD0000>the Gamebreaker</col>", " <col=4682B4>the Gamebreaker</col>", " <col=FFFFFF>the Gam</col><col=DD0000>ebreaker</col>", " <str><col=DD0000>the Gamebreaker</col></str>", " <col=DD0000>the   Gamebreaker</col>", " <col=DD0000>thE gAmEbrEAkEr</col>", " <col=DD0000>the Gaembreaker</col>", " <col=DD0000>the Gamebreakr</col>"};

	public enum TitleReward {
		GAMEBREAKER("DD0000", "the Gamebreaker", "Gamebreaking bugs found", 1, false),
		WEEDER("00FF00", "Weeder", "Weeds raked", 1420, false),
		DEDICATED("SFFCC00", "Dedicated", "Unique days logged in", 20, false),
		PARTIER("S00FFCC", "Partier", "Party balloons popped", 500, false),
		VOTER("SFFCC00", "Voter", "Voted", 25, false),
		ALCHEMIST("SFFCC00", "Alchemist", "Items alched", 5000, false),
		GLUTTON("SFFCC00", "Glutton", "Food eaten", 10000, false),
		COWKILLER("SFFFFFF", "C0wK1ll3r", "Cow", 2500, true),
		IMPISH("DD0000", "Impish", "Imp", 1000, true),
		ABYSSAL("990000", "The Abyssal", "Abyssal demon", 1000, true),
		UNDERTAKER("006600", "Undertaker", "%Zombie", 2500, true),
		ZAROS("6600CC", "Zaros Zerger", "Nex", 25, true),
		CORPOREAL("S666666", "Corporeal", "Corporeal Beast", 50, true),
		BANDOS("006600", "Bandos Basher", "General Graardor", 100, true),
		SARADOMIN("3333FF", "Saradomin Slayer", "Commander Zilyana", 100, true),
		ZAMORAK("CC0000", "Zamorak Zapper", "K'ril Tsutsaroth", 100, true),
		ARMADYL("9999FF", "Armadyl Assassin", "Kree'arra", 100, true),
		REVENANT("S666666", "Revenant", "%Revenant", 1000, true),
		CHAOSELE("CC00CC", "ChAoTiC", "Chaos Elemental", 250, true),
		GLACYTE("S33CCFF", "Glacyte", "Glacor", 500, true),
		MILLIONS("SFFD11A", "Millionare", "Coins drops earned", 1000000, false),
		SOUL_REAPER("939393", "Soul Reaper", "Health soulsplitted back", 100000, false),
		GARBAGE_CAN("3BB03B", "Garbage Can", "Deaths", 50, false),
		ACTUAL_DUMPHEAP("3BB03B", "Actual Dumpheap", "Deaths", 500, false),
		YORICK("7C7C01", "Harrowing", "Barrows chests looted", 75, false),
		EXPLORER("73A93F", "Explorer", "Easy clues completed", 100, false),
		ENIGMATOLOGIST("73A93F", "Enigmatologist", "Medium clues completed", 100, false),
		GLOBETROTTER("73A93F", "Globetrotter", "Hard clues completed", 100, false),
		CLUE_MANIAC("73A93F", "Clue Maniac", "Elite clues completed", 100, false),
		MUDDY("794c13", "Muddy", "Muddy chests opened", 200, false),

		;

		private String color;
		private String title;
		private String req;
		private int number;
		private boolean npcKills;

		private TitleReward(String color, String title, String req, int number, boolean npcKills) {
			this.color = color;
			this.title = title;
			this.req = req;
			this.number = number;
			this.npcKills = npcKills;
		}

		public boolean isNpcKills() {
			return npcKills;
		}

		public String getColor() {
			return color;
		}

		public String getTitle() {
			return title;
		}

		public String getReq() {
			return req;
		}

		public int getNumber() {
			return number;
		}

		public void handleActivate(Player player) {
			if (hasRequirements(player)) {
				player.setTitle(title);
				if (!color.startsWith("S"))
					player.setTitleColor(color);
				else {
					player.setTitleColor(color.replace("S", ""));
					player.setTitleShading("000000");
				}

				if (isUsingGamebreakerTitle(player)) {
					player.setTitle(gamebreakerVariations[Utils.random(gamebreakerVariations.length)]);
					player.setTitleAfter(true);
				}

				player.sendMessage("You have set your title to "+getShadeColor(color)+""+title+"</col></shad>.");
				player.getAppearance().generateAppearanceData();
			} else
				player.sendMessage("You don't meet the requirements for this title.");
		}

		public boolean hasRequirements(Player player) {
			if (npcKills) {
				if (player.getNumberKilled(req) >= number)
					return true;
			} else if (player.getCounterValue(req) >= number)
				return true;
			return false;
		}
	}

	public static String getShadeColor(String color) {
		if (!color.startsWith("S"))
			return "<col="+color+">";
		return "<shad=000000><col="+color.substring(1)+">";
	}

	public static void openInterface(Player player) {
		int[] names = { 30, 32, 34, 36, 38, 49, 51, 53, 55, 57, 59, 62, 64, 66, 68, 70, 72, 74, 76, 190, 79, 81, 83, 85, 88, 90, 92, 94, 97, 99, 101, 104, 106, 108, 110, 115, 117, 119, 121, 123, 125, 131, 127, 129, 2, 173, 175, 177, 182,
				184, 186, 188 };
		player.getTempAttribs().removeB("RemoteFarm");
		player.getInterfaceManager().sendInterface(1082);
		player.getPackets().setIFHidden(1082, 158, true);
		player.getPackets().setIFText(1082, 41, "Titles");
		player.getPackets().setIFText(1082, 42, "Requirements");
		player.getPackets().setIFText(1082, 11, "Welcome to the title shop. Click a title to activate it or check requirements.<br>Clear your title by right clicking Xuan and selecting the Clear Title option.");
		for (int i = 0; i < names.length; i++) {
			if (i > TitleReward.values().length-1) {
				player.getPackets().setIFText(1082, names[i], "");
				player.getPackets().setIFText(1082, names[i] + 1, "");
				continue;
			}
			TitleReward title = TitleReward.values()[i];
			if (title != null) {
				player.getPackets().setIFText(1082, names[i], getShadeColor(title.getColor())+title.getTitle()+"</col></shad>");
				player.getPackets().setIFText(1082, names[i] + 1, (title.hasRequirements(player) ? "<col=00FF00>" : "<col=FF0000>")+(title.isNpcKills() ? player.getNumberKilled(title.getReq()) : player.getCounterValue(title.getReq()))+"/"+title.getNumber()+" "+title.getReq().replace("%", "")+" "+(title.isNpcKills() ? "kills" : ""));
			} else {
				player.getPackets().setIFText(1082, names[i], "");
				player.getPackets().setIFText(1082, names[i] + 1, "");
			}
		}
	}

	public static void handleButtons(Player player, int componentId) {
		int[] names = { 30, 32, 34, 36, 38, 49, 51, 53, 55, 57, 59, 62, 64, 66, 68, 70, 72, 74, 76, 190, 79, 81, 83, 85, 88, 90, 92, 94, 97, 99, 101, 104, 106, 108, 110, 115, 117, 119, 121, 123, 125, 131, 127, 129, 2, 173, 175, 177, 182,
				184, 186, 188 };
		for (int i = 0; i < names.length; i++)
			if ((names[i]+1) == componentId)
				if (i < TitleReward.values().length && TitleReward.values()[i] != null)
					TitleReward.values()[i].handleActivate(player);
	}
	
	public static LoginHandler login = new LoginHandler(e -> {
		if (isUsingGamebreakerTitle(e.getPlayer())) {
			e.getPlayer().setTitle(gamebreakerVariations[Utils.random(gamebreakerVariations.length)]);
			e.getPlayer().getAppearance().generateAppearanceData();
			e.getPlayer().setTitleAfter(true);
		}
	});

	public static boolean isUsingGamebreakerTitle(Player p) {
		if (p.getTitle() == null)
			return false;
		for (String variation : gamebreakerVariations)
			if (variation != null && variation.contains(p.getTitle()))
				return true;
		return false;
	}

}
