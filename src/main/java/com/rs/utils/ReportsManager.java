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
package com.rs.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Logger;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;

public class ReportsManager {

	private static BufferedWriter reports;

	static {
		try {
			reports = new BufferedWriter(new FileWriter("data/reports.txt", true));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static void report(Player player) {
		report(player, null);
	}

	public static void report(Player player, String name) {
		if (player.getInterfaceManager().containsScreenInter()) {
			player.sendMessage("Please close the interface that you opened before activating the 'Report' system.");
			return;
		}
		if (name != null)
			player.getPackets().sendVarcString(24, name);
		if (player.hasRights(Rights.MOD))
			player.getPackets().setIFHidden(594, 8, false);
		player.getInterfaceManager().sendInterface(594);

	}

	public static void report(Player player, String displayName, int type, boolean mute) {
		if (mute && !player.hasRights(Rights.MOD))
			return;
		Player reported = World.getPlayerByUsername(displayName);
		if (reported == null)
			return;
		if (mute) {
			reported.getAccount().muteDays(2);
			reported.sendMessage("You've been muted for 2 days by " + player.getDisplayName() + ".");
			LobbyCommunicator.updatePunishments(reported);
		}
		player.sendMessage("Thank-you, your abuse report has been received.");
		try {
			reports.write(Utils.getFormatedDate() + " Report by " + player.getUsername() + " - Offender: " + reported.getUsername() + " Offense: " + getType(type));
			reports.newLine();
			reports.flush();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static String getType(int id) {
		switch (id) {
		case 6:
			return "Buying or selling account";
		case 9:
			return "Encouraging rule breaking";
		case 5:
			return "Staff impersonation";
		case 7:
			return "Macroing or use of bots";
		case 15:
			return "Scamming";
		case 4:
			return "Exploiting a bug";
		case 16:
			return "Seriously offensive language";
		case 17:
			return "Solicitation";
		case 18:
			return "Disruptive behaviour";
		case 19:
			return "Offensive account name";
		case 20:
			return "Real-life threats";
		case 13:
			return "Asking for or providing contact information";
		case 21:
			return "Breaking real-world laws";
		case 11:
			return "Advertising websites";
		}
		return "Unknown";
	}

}
