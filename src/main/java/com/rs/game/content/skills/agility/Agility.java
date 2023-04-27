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
package com.rs.game.content.skills.agility;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
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

	public static NPCClickHandler handleGunnjorn = new NPCClickHandler(new Object[] { 607 }, e -> e.getPlayer().startConversation(new GunnjornD(e.getPlayer())));

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
				addNPC(162, HeadE.HAPPY_TALKING, "Well, you've still got work to do. Your lap count is  " + player.getCounterValue("Gnome advanced laps") + ". It's 250 successful laps for the reward!");

			create();
		}
	}

	public static NPCClickHandler handleGnomeTrainer = new NPCClickHandler(new Object[] { 162 }, e -> e.getPlayer().startConversation(new GnomeTrainerD(e.getPlayer())));

	public static boolean hasLevel(Player player, int level) {
		if (player.getSkills().getLevel(Constants.AGILITY) < level) {
			player.sendMessage("You need an agility level of " + level + " to use this obstacle.", true);
			return false;
		}
		return true;
	}

	public static void swingOnRopeSwing(final Player player, final Tile startTile, final Tile endTile, final GameObject object, final double xp) {
		player.walkToAndExecute(startTile, () -> {
			player.faceObject(object);
			player.setNextAnimation(new Animation(751));
			World.sendObjectAnimation(object, new Animation(497));
			player.forceMove(endTile, 30, 90, () -> {
				player.sendMessage("You skillfully swing across the rope.", true);
				player.getSkills().addXp(Constants.AGILITY, xp);
			});
		});
	}

	public static void handleObstacle(final Player player, int animationId, int delay, final Tile toTile, final double xp) {
		player.lock();
		WorldTasks.schedule(1, () -> player.setNextAnimation(new Animation(animationId)));
		WorldTasks.schedule(delay+1, () -> {
			player.unlockNextTick();
			player.setNextTile(toTile);
			player.setNextAnimation(new Animation(-1));
			player.getSkills().addXp(Constants.AGILITY, xp);
		});
	}

	public static void crossMonkeybars(final Player player, Tile startTile, final Tile endTile, final double xp) {
		player.walkToAndExecute(startTile, () -> {
			player.faceTile(endTile);
			walkToAgility(player, 2405, Direction.forDelta(endTile.getX()-startTile.getX(), endTile.getY()-startTile.getY()), Utils.getDistanceI(startTile, endTile), Utils.getDistanceI(startTile, endTile), xp);
		});
	}

	public static void walkToAgility(final Player player, final int renderEmote, final Direction direction, final int distance, final int delay) {
		walkToAgility(player, renderEmote, direction, distance, delay, 0.0);
	}

	public static void walkToAgility(final Player player, final int renderEmote, final Direction direction, final int distance, final int delay, final double xp) {
		if (direction != player.getDirection())
			return;
		player.lock();
		boolean running = player.getRun();
		player.setRunHidden(false);
		WorldTasks.schedule(1, () -> {
			player.setBas(renderEmote);
			player.addWalkSteps(player.transform(direction.getDx()*distance, direction.getDy()*distance), distance,false);
		});
		WorldTasks.schedule(delay+1, () -> {
			if (xp > 0)
				player.getSkills().addXp(Constants.AGILITY, xp);
			player.setBas(-1);
			player.unlockNextTick();
			player.setRunHidden(running);
		});
	}

}
