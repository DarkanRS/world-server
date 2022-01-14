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
package com.rs.game.player.content.skills.agility;

import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Agility {

	static class GunnjornD extends Conversation {
		public GunnjornD(Player player) {
			super(player);

			addPlayer(HeadE.CONFUSED, "Can I get any rewards?");
			if (player.getCounterValue("Barbarian advanced laps") >= 250) {
				addNPC(607, HeadE.HAPPY_TALKING, "As promised, I'll give you an item you may find useful: an Agile top. You'll find yourself lighter than usual while wearing it.");
				addNPC(607, HeadE.HAPPY_TALKING, "We barbarians are tough folks, as you know, so it'll even keep you safe if you get drawn into combat.", () -> {
					player.getInventory().addItem(14936, 1);
				});
			} else
				addNPC(607, HeadE.HAPPY_TALKING, "Of course! Once you've completed 250 laps of the advanced course, I have something in mind. You've completed " + player.getCounterValue("Barbarian advanced laps") + " laps so far.");

			create();
		}
	}

	public static NPCClickHandler handleGunnjorn = new NPCClickHandler(607) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new GunnjornD(e.getPlayer()));
		}
	};

	static class GnomeTrainerD extends Conversation {
		public GnomeTrainerD(Player player) {
			super(player);

			addPlayer(HeadE.CONFUSED, "Can I get any rewards?");
			if (player.getCounterValue("Gnome advanced laps") >= 250) {
				addNPC(162, HeadE.HAPPY_TALKING, "Well, it looks like you've completed our challenge!");
				addNPC(162, HeadE.HAPPY_TALKING, "Take this as a reward: an Agile leg. You'll find yourself much lighter than usual while wearing them.");
				addNPC(162, HeadE.HAPPY_TALKING, "They are made from the toughest material we gnomes could find, so it might even protect you in combat.", () -> {
					player.getInventory().addItem(14938, 1);
				});
				addNPC(162, HeadE.HAPPY_TALKING, "There you go. Enjoy!");
			} else
				addNPC(162, HeadE.HAPPY_TALKING, "Well, you've still got work to do. Your lap count is  " + player.getCounterValue("Barbarian advanced laps") + ". It's 250 successful laps for the reward!");

			create();
		}
	}

	public static NPCClickHandler handleGnomeTrainer = new NPCClickHandler(162) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new GnomeTrainerD(e.getPlayer()));
		}
	};

	public static boolean hasLevel(Player player, int level) {
		if (player.getSkills().getLevel(Constants.AGILITY) < level) {
			player.sendMessage("You need an agility level of " + level + " to use this obstacle.", true);
			return false;
		}
		return true;
	}

	public static void swingOnRopeSwing(final Player player, final WorldTile startTile, final WorldTile endTile, final GameObject object, final double xp) {
		player.walkToAndExecute(startTile, () -> {
			player.lock();
			player.faceObject(object);
			player.setNextAnimation(new Animation(751));
			World.sendObjectAnimation(player, object, new Animation(497));
			player.setNextForceMovement(new ForceMovement(player, 1, endTile, 3, Utils.getAngleTo(endTile.getX()-player.getX(), endTile.getY()-player.getY())));
			player.sendMessage("You skillfully swing across the rope.", true);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					player.unlockNextTick();
					player.getSkills().addXp(Constants.AGILITY, xp);
					player.setNextWorldTile(endTile);
				}

			}, 1);
		});
	}

	public static void handleObstacle(final Player player, int animationId, int delay, final WorldTile toTile, final double xp) {
		player.lock();
		player.setNextAnimation(new Animation(animationId));
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				player.unlockNextTick();
				player.setNextWorldTile(toTile);
				player.setNextAnimation(new Animation(-1));
				player.getSkills().addXp(Constants.AGILITY, xp);
			}
		}, delay);
	}

	public static void crossMonkeybars(final Player player, WorldTile startTile, final WorldTile endTile, final double xp) {
		player.walkToAndExecute(startTile, () -> walkToAgility(player, 2405, endTile, xp));
	}

	public static void walkToAgility(final Player player, final int renderEmote, final WorldTile toTile, final double xp) {
		final boolean running = player.getRun();
		player.setRunHidden(false);
		player.lock();
		player.addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (player.getX() != toTile.getX() || player.getY() != toTile.getY())
					player.getAppearance().setBAS(renderEmote);
				else {
					player.getAppearance().setBAS(-1);
					player.setRunHidden(running);
					if (xp > 0)
						player.getSkills().addXp(Constants.AGILITY, xp);
					player.unlockNextTick();
					stop();
				}
			}
		}, 0, 1);
	}

}
