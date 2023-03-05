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
package com.rs.game.content.commands.admin;

import com.rs.Settings;
import com.rs.db.WorldDB;
import com.rs.game.World;
import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.engine.command.Commands;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Rights;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;

@PluginEventHandler
public class PlayerModifiers {

	@ServerStartupEvent
	public static void load() {
		Commands.add(Rights.OWNER, "setdeveloper,givedeveloper [player name]", "Will set a player to developer status.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.DEVELOPER);
				p.sendMessage("Successfully gave developer to " + Utils.formatPlayerNameForDisplay(target.getUsername()));
			} else
				p.sendMessage("Couldn't find player.");
		});

		Commands.add(Rights.OWNER, "setadmin,giveadmin [player name]", "Will set a player to admin status.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.ADMIN);
				p.sendMessage("Successfully gave admin to " + Utils.formatPlayerNameForDisplay(target.getUsername()));
			} else
				p.sendMessage("Couldn't find player.");
		});

		Commands.add(Rights.DEVELOPER, "setmod,givemod [player name]", "Will set a player to player moderator status.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.MOD);
				p.sendMessage("Successfully gave player moderator to " + Utils.formatPlayerNameForDisplay(target.getUsername()));
			} else
				p.sendMessage("Couldn't find player.");
		});

		Commands.add(Rights.DEVELOPER, "demote [player name]", "Will demote a player's mod level to normal player status.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.PLAYER);
				p.sendMessage("Successfully demoted " + Utils.formatPlayerNameForDisplay(target.getUsername()));
			} else
				p.sendMessage("Couldn't find player.");
		});

		Commands.add(Rights.ADMIN, "teleto [player name]", "Teleports the user to another player without exception.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else
				p.setNextTile(target.getTile());
		});

		Commands.add(Rights.ADMIN, "teletome [player name]", "Teleports another player to the user without exception.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else
				target.setNextTile(p.getTile());
		});

		Commands.add(Rights.ADMIN, "kick [player name]", "Kicks a player from the game. Will force the player's character out of the game no matter what.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null) {
				p.sendMessage(Utils.formatPlayerNameForDisplay(Utils.concat(args)) + " is not logged in.");
				return;
			}
			p.sendMessage("Successfully kicked " + Utils.concat(args) + ".");
			target.forceLogout();
		});

		Commands.add(Rights.ADMIN, "ban [player_name banDurationDays]", "Bans a player for specified number of days.", (p, args) -> {
			World.forceGetPlayerByDisplay(args[0], target -> {
				if (target != null) {
					target.getAccount().banDays(Integer.valueOf(args[1]));
					p.sendMessage("You have banned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + " for "+Integer.valueOf(args[1])+" days.");
					LobbyCommunicator.updatePunishments(target);
					if (target.hasStarted())
						target.getSession().getChannel().close();
				} else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.ADMIN, "permban [player name]", "Bans a player permanently.", (p, args) -> {
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target != null) {
					target.getAccount().banPerm();
					p.sendMessage("You have permanently banned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + ".");
					if (target.hasStarted())
						target.forceLogout();
					LobbyCommunicator.updatePunishments(target);
				} else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.ADMIN, "mute [player_name muteDurationDays]", "Mutes a player for specified number of days.", (p, args) -> {
			World.forceGetPlayerByDisplay(args[0], target -> {
				if (target != null) {
					target.getAccount().muteDays(Integer.valueOf(args[1]));
					p.sendMessage("You have muted " + Utils.formatPlayerNameForDisplay(args[0]) + " for "+Integer.valueOf(args[1])+" days.");
					LobbyCommunicator.updatePunishments(target);
				} else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.ADMIN, "permmute [player name]", "Bans a player permanently.", (p, args) -> {
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target != null) {
					target.getAccount().mutePerm();
					p.sendMessage("You have permanently muted " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + ".");
					LobbyCommunicator.updatePunishments(target);
				} else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.ADMIN, "unban [player name]", "Unbans a player.", (p, args) -> {
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target != null)
					p.sendMessage("You have unbanned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + ".");
				else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.ADMIN, "unmute [player name]", "Unmutes a player.", (p, args) -> {
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target != null) {
					target.getAccount().unmute();
					p.sendMessage("You have unmuted " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + ".");
					LobbyCommunicator.updatePunishments(target);
				} else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.ADMIN, "ipban [player name]", "Bans a player permanently and blocks their IP from connecting.", (p, args) -> {
			World.forceGetPlayerByDisplay(Utils.concat(args), target -> {
				if (target != null)
					p.sendMessage("You have IP banned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + ".");
				else
					p.sendMessage("Unable to find player.");
			});
		});

		Commands.add(Rights.ADMIN, "unnull,sendhome [player name]", "Forces the player out of a controller and unlocks them hopefully freeing any stuck-ness.", (p, args) -> {
			Player target = World.getPlayerByDisplay(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player " + Utils.concat(args) + ".");
			else {
				target.unlock();
				target.getControllerManager().forceStop();
				if (target.getNextTile() == null)
					target.setNextTile(Settings.getConfig().getPlayerRespawnTile());
				p.sendMessage("You have unnulled: " + target.getDisplayName() + ".");
			}
		});

		Commands.add(Rights.ADMIN, "nextclue [player name]", "Moves the player on to the next clue step.", (p, args) -> {
			Player target = World.getPlayerByDisplay(args[0]);
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				target.getTreasureTrailsManager().setNextClue(0, true);
				target.sendMessage("Your clue step has been automatically completed.");
				p.sendMessage("Successfully moved them on to the next clue.");
			}
		});

		Commands.add(Rights.ADMIN, "giveitem [player_name itemId (amount)]", "Gives the specified player an item.", (p, args) -> {
			Player target = World.getPlayerByDisplay(args[0]);
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				target.getInventory().addItem(Integer.valueOf(args[1]), args.length > 1 ? Integer.valueOf(args[2]) : 1);
				p.sendMessage("Successfully given them the item..");
			}
		});

		Commands.add(Rights.ADMIN, "givedungtokens [player_name amount]", "Gives the specified player dungeoneering tokens.", (p, args) -> {
			Player target = World.getPlayerByDisplay(args[0]);
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				target.getDungManager().addTokens(Integer.valueOf(args[1]));
				p.sendMessage("Successfully given them the item..");
			}
		});

		Commands.add(Rights.ADMIN, "setlevelother [player_name skillId level]", "Sets another player's skill to a certainl level.", (p, args) -> {
			World.forceGetPlayerByDisplay(args[0], target -> {
				int skill = Integer.parseInt(args[1]);
				int level = Integer.parseInt(args[2]);
				if (level < 0 || level > (skill == Constants.DUNGEONEERING ? 120 : 99)) {
					p.sendMessage("Please choose a valid level.");
					return;
				}
				if (skill < 0 || skill >= Constants.SKILL_NAME.length) {
					p.sendMessage("Please choose a valid skill.");
					return;
				}
				if (target == null)
					p.sendMessage("Couldn't find player.");
				else {
					target.getSkills().set(skill, level);
					target.getSkills().setXp(skill, Skills.getXPForLevel(level));
					target.getAppearance().generateAppearanceData();
					WorldDB.getPlayers().save(target, () -> p.sendMessage("Successfully set players level.."));
				}
			});
		});

		Commands.add(Rights.ADMIN, "givegamebreaker [player_name]", "Increments targets Gamebreaking bugs found.", (p, args) -> {
			World.forceGetPlayerByDisplay(args[0], target -> {
				if (target == null)
					p.sendMessage("Couldn't find player.");
				else {
					target.incrementCount("Gamebreaking bugs found");
					p.sendMessage("Successfully incremented gamebreaking bugs found...");
					if (target.getCounterValue("Gamebreaking bugs found") == 1)
						World.sendWorldMessage("<img=4><shad=000000><col=00FF00>" + target.getDisplayName() + " has been awarded the Gamebreaker title!", false);
					WorldDB.getPlayers().save(target, () -> p.sendMessage("Successfully gave player gamebreaker title.."));
				}
			});
		});

		Commands.add(Rights.ADMIN, "playerquestreset [player_name questName]", "Resets the specified quest for the player", (p, args) -> {
			Player player = World.getPlayerByDisplay(args[0]);
			for (Quest quest : Quest.values())
				if (quest.name().toLowerCase().contains(args[1])) {
					player.getQuestManager().setStage(quest, 0);
					p.sendMessage("Resetted quest: " + quest.name() + " for " + player.getUsername());
					player.sendMessage("Resetted quest: " + quest.name());
					return;
				}
		});

        Commands.add(Rights.ADMIN, "setgang [player_name gang]", "Sets Player Gang", (p, args) -> {
            World.forceGetPlayerByDisplay(args[0], target -> {
				ShieldOfArrav.setGang(target, args[1].toLowerCase());
				target.sendMessage("Set " + target.getDisplayName() + "'s gang to " + args[1].toLowerCase());
			});
        });
	}

}
