package com.rs.game.player.content.commands.admin;

import com.rs.Settings;
import com.rs.db.collection.Players;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.commands.Commands;
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
			Player target = World.getPlayer(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.DEVELOPER);
				Players.save(target, () -> p.sendMessage("Successfully gave developer to " + Utils.formatPlayerNameForDisplay(target.getUsername())));
			} else {
				p.sendMessage("Couldn't find player.");
			}
		});
		
		Commands.add(Rights.OWNER, "setadmin,giveadmin [player name]", "Will set a player to admin status.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.ADMIN);
				Players.save(target, () -> p.sendMessage("Successfully gave admin to " + Utils.formatPlayerNameForDisplay(target.getUsername())));
			} else {
				p.sendMessage("Couldn't find player.");
			}
		});
		
		Commands.add(Rights.DEVELOPER, "setmod,givemod [player name]", "Will set a player to player moderator status.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.MOD);
				Players.save(target, () -> p.sendMessage("Successfully gave player moderator to " + Utils.formatPlayerNameForDisplay(target.getUsername())));
			} else {
				p.sendMessage("Couldn't find player.");
			}
		});
		
		Commands.add(Rights.DEVELOPER, "demote [player name]", "Will demote a player's mod level to normal player status.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target != null) {
				target.setRights(Rights.PLAYER);
				Players.save(target, () -> p.sendMessage("Successfully demoted " + Utils.formatPlayerNameForDisplay(target.getUsername())));
			} else {
				p.sendMessage("Couldn't find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "teleto [player name]", "Teleports the user to another player without exception.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else
				p.setNextWorldTile(target);
		});
		
		Commands.add(Rights.ADMIN, "teletome [player name]", "Teleports another player to the user without exception.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				target.setNextWorldTile(p);
			}
		});
		
		Commands.add(Rights.ADMIN, "kick [player name]", "Kicks a player from the game. Will force the player's character out of the game no matter what.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target == null) {
				p.sendMessage(Utils.formatPlayerNameForDisplay(Utils.concat(args)) + " is not logged in.");
				return;
			}
			p.sendMessage("Successfully kicked " + Utils.concat(args) + ".");
			target.forceLogout();
		});
		
		Commands.add(Rights.ADMIN, "ban [player_name banDurationDays]", "Bans a player for specified number of days.", (p, args) -> {
			Player target = World.forceGetPlayer(args[0]);
			if (target != null) {
				target.getAccount().banDays(Integer.valueOf(args[1]));
				Players.save(target, () -> p.sendMessage("You have banned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + " for "+Integer.valueOf(args[1])+" days."));
				LobbyCommunicator.updateAccount(target);
				if (target.hasStarted())
					target.getSession().getChannel().close();
			} else {
				p.sendMessage("Unable to find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "permban [player name]", "Bans a player permanently.", (p, args) -> {
			Player target = World.forceGetPlayer(Utils.concat(args));
			if (target != null) {
				target.getAccount().banPerm();
				Players.save(target, () -> p.sendMessage("You have permanently banned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + "."));
				if (target.hasStarted())
					target.forceLogout();
				LobbyCommunicator.updateAccount(target);
			} else {
				p.sendMessage("Unable to find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "mute [player_name muteDurationDays]", "Mutes a player for specified number of days.", (p, args) -> {
			Player target = World.forceGetPlayer(args[0]);
			if (target != null) {
				target.getAccount().muteDays(Integer.valueOf(args[1]));
				Players.save(target, () -> p.sendMessage("You have muted " + Utils.formatPlayerNameForDisplay(args[0]) + " for "+Integer.valueOf(args[1])+" days."));
				LobbyCommunicator.updateAccount(target);
			} else {
				p.sendMessage("Unable to find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "permmute [player name]", "Bans a player permanently.", (p, args) -> {
			Player target = World.forceGetPlayer(Utils.concat(args));
			if (target != null) {
				target.getAccount().mutePerm();
				Players.save(target, () -> p.sendMessage("You have permanently muted " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + "."));
				LobbyCommunicator.updateAccount(target);
			} else {
				p.sendMessage("Unable to find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "unban [player name]", "Unbans a player.", (p, args) -> {
			Player target = World.forceGetPlayer(Utils.concat(args));
			if (target != null) {
				Players.save(target, () -> p.sendMessage("You have unbanned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + "."));
			} else {
				p.sendMessage("Unable to find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "unmute [player name]", "Unmutes a player.", (p, args) -> {
			Player target = World.forceGetPlayer(Utils.concat(args));
			if (target != null) {
				target.getAccount().unmute();
				Players.save(target, () -> p.sendMessage("You have unmuted " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + "."));
				LobbyCommunicator.updateAccount(target);
			} else {
				p.sendMessage("Unable to find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "ipban [player name]", "Bans a player permanently and blocks their IP from connecting.", (p, args) -> {
			Player target = World.forceGetPlayer(Utils.concat(args));
			if (target != null) {
				Players.save(target, () -> p.sendMessage("You have IP banned " + Utils.formatPlayerNameForDisplay(Utils.concat(args)) + "."));
			} else {
				p.sendMessage("Unable to find player.");
			}
		});
		
		Commands.add(Rights.ADMIN, "unnull,sendhome [player name]", "Forces the player out of a controller and unlocks them hopefully freeing any stuck-ness.", (p, args) -> {
			Player target = World.getPlayer(Utils.concat(args));
			if (target == null)
				p.sendMessage("Couldn't find player " + Utils.concat(args) + ".");
			else {
				target.unlock();
				target.getControllerManager().forceStop();
				if (target.getNextWorldTile() == null)
					target.setNextWorldTile(Settings.getConfig().getPlayerRespawnTile());
				p.sendMessage("You have unnulled: " + target.getDisplayName() + ".");
			}
		});
		
		Commands.add(Rights.ADMIN, "nextclue [player name]", "Moves the player on to the next clue step.", (p, args) -> {
			Player target = World.getPlayer(args[0]);
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				target.getTreasureTrailsManager().setNextClue(0);
				target.sendMessage("Your clue step has been automatically completed.");
				p.sendMessage("Successfully moved them on to the next clue.");
			}
		});
		
		Commands.add(Rights.ADMIN, "giveitem [player_name itemId (amount)]", "Gives the specified player an item.", (p, args) -> {
			Player target = World.getPlayer(args[0]);
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				target.getInventory().addItem(Integer.valueOf(args[1]), args.length > 1 ? Integer.valueOf(args[2]) : 1);
				p.sendMessage("Successfully given them the item..");
			}
		});
		
		Commands.add(Rights.ADMIN, "givedungtokens [player_name amount]", "Gives the specified player dungeoneering tokens.", (p, args) -> {
			Player target = World.getPlayer(args[0]);
			if (target == null)
				p.sendMessage("Couldn't find player.");
			else {
				target.getDungManager().addTokens(Integer.valueOf(args[1]));
				p.sendMessage("Successfully given them the item..");
			}
		});
		
		Commands.add(Rights.ADMIN, "setlevelother [player_name skillId level]", "Sets another player's skill to a certainl level.", (p, args) -> {
			Player target = World.forceGetPlayer(args[0]);
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
				Players.save(target, () -> p.sendMessage("Successfully set players level.."));
			}
		});
	}

}
