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
package com.rs.game.content.commands.mod;

import com.rs.engine.command.Commands;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.utils.record.Recorder;

@PluginEventHandler
public class PlayerModifiers {

	@ServerStartupEvent
	public static void loadCommands() {
		Commands.add(Rights.MOD, "checkbank [player name]", "Displays the contents of another player's bank.", (p, args) -> {
			if (args.length == 0) {
				p.getBank().openBankOther(p);
				return;
			}
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target == null) {
					p.sendMessage("Could not find player " + Utils.concat(args) + ".");
					return;
				}
				p.getBank().openBankOther(target);
			});
		});

		Commands.add(Rights.MOD, "kick [player name]", "Kicks a player from the game. Will not kick through combat.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null) {
				p.sendMessage(Utils.formatPlayerNameForDisplay(Utils.concat(args)) + " is not logged in.");
				return;
			}
			p.sendMessage("Successfully kicked " + Utils.concat(args) + ".");
			target.getSession().getChannel().close();
		});

		Commands.add(Rights.MOD, "ban [player name]", "Bans a player for 2 days.", (p, args) -> {
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target != null) {
					target.getAccount().banDays(2);
					p.sendMessage("You have banned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + " for 2 days.");
					LobbyCommunicator.updatePunishments(target);
					if (target.hasStarted())
						target.getSession().getChannel().close();
				} else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.MOD, "teleto", "Teleports the user to another player as long as they aren't in a controller or locked.", (p, args) -> {
			if (p.isLocked() || p.getControllerManager().getController() != null) {
				p.sendMessage("You cannot tele anywhere from here.");
				return;
			}
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				if (target.isLocked() || target.getControllerManager().getController() != null) {
					p.sendMessage("You cannot teleport this player.");
					return;
				}
				p.setNextTile(target.getTile());
			}
		});

		Commands.add(Rights.MOD, "teletome", "Teleports another player to the user as long as they aren't in a controller or locked.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				if (target.isLocked() || target.getControllerManager().getController() != null) {
					p.sendMessage("You cannot teleport this player.");
					return;
				}
				if (target.hasRights(Rights.DEVELOPER)) {
					p.sendMessage("Unable to teleport a developer to you.");
					return;
				}
				target.setNextTile(p.getTile());
			}
		});

		Commands.add(Rights.MOD, "mute [player name]", "Mutes a player for 2 days.", (p, args) -> {
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target != null) {
					target.getAccount().muteDays(2);
					p.sendMessage("You have muted " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + " for 2 days.");
					LobbyCommunicator.updatePunishments(target);
				} else
					p.sendMessage("Unable to find player.");
			});
		});
		
		Commands.add(Rights.MOD, "actions [player_name ...]", "Displays the last recorded actions the players specified have done.", (p, args) -> Recorder.showConcatenatedActions(p, args));
		Commands.add(Rights.MOD, "watch [player_name ...]", "Continuously displays recorded actions for the players specified.", (p, args) -> Recorder.watchPlayers(p, args));
		Commands.add(Rights.MOD, "stopwatch", "Stops watching players.", (p, args) -> p.getNSV().setB("stopWatchActionLoop", true));

	}

}
