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

import java.util.HashMap;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Logger;
import com.rs.net.LobbyCommunicator;

public class ReportsManager {

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
		if (displayName == null)
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
//			Rule rule = Rule.forId(type);
//			if (rule != null)
//				WorldDB.getLogs().logReport(player, reported, rule);
		} catch (Throwable e) {
			Logger.handle(ReportsManager.class, "report", e);
		}
	}

	public enum Rule {
		BUG_EXPLOITATION(4),
		STAFF_IMPERSONATION(5),
		REAL_WORLD_TRADING(6, 17),
		MACROING(7),
		ENCOURAGING_RULE_BREAKING(9),
		SCAMMING(15),
		OFFENSIVE_LANGUAGE(16),
		DISRUPTIVE_BEHAVIOR(18),
		OFFENSIVE_NAME(19),
		REAL_WORLD_OFFENSE(20, 21, 13),
		ADVERTISING(11);

		private static Map<Integer, Rule> MAPPING = new HashMap<>();

		static {
			for (Rule rule : Rule.values()) {
				for (int id : rule.ids)
					MAPPING.put(id, rule);
			}
		}

		public static Rule forId(int id) {
			return MAPPING.get(id);
		}

		private int[] ids;

		private Rule(int... ids) {
			this.ids = ids;
		}

		public int[] getIds() {
			return ids;
		}
	}

}
