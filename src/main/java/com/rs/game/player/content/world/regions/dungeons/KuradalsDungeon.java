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
package com.rs.game.player.content.world.regions.dungeons;

import com.rs.game.ForceMovement;
import com.rs.game.pathing.Direction;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.skills.slayer.TaskMonster;
import com.rs.game.player.controllers.KuradalDungeonController;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class KuradalsDungeon {

	public static ObjectClickHandler handleBarriers = new ObjectClickHandler(new Object[] { 47236 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getRotation() == 2)
				e.getPlayer().walkOneStep(e.getPlayer().getX() > e.getObject().getX() ? -1 : 1, 0, false);
			else
				e.getPlayer().walkOneStep(0, e.getPlayer().getY() == e.getObject().getY() ? -1 : 1, false);
		}
	};

	public static ObjectClickHandler handleWallRunShortcut = new ObjectClickHandler(new Object[] { 47237 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();

			if(p.getSkills().getLevel(Skills.AGILITY) < 90) {
				p.getPackets().sendGameMessage("You need level 90 agility to use this shortcut.");
				return;
			}

			WorldTasks.schedule(new WorldTask() {
				int ticks = 0;
				boolean isPlayerNorth = true;

				@Override
				public void run() {
					if(ticks == 0)
						if (p.getX() == 1641 && p.getY() == 5268) {
							p.setNextAnimation(new Animation(2929));
							isPlayerNorth = true;
						}
						else if (p.getX() == 1641 && p.getY() == 5260) {
							p.setNextAnimation(new Animation(2922));
							isPlayerNorth = false;
						} else
							return;

					if (ticks == 1) {
						if(isPlayerNorth) {
							e.getPlayer().setFaceAngle(Direction.getAngleTo(Direction.SOUTH));
							p.setNextForceMovement(new ForceMovement(new WorldTile(1641, 5260, 0), 1, Direction.SOUTH));
						}
						if(!isPlayerNorth) {
							e.getPlayer().setFaceAngle(Direction.getAngleTo(Direction.NORTH));
							p.setNextForceMovement(new ForceMovement(new WorldTile(1641, 5268, 0), 1, Direction.NORTH));
						}
					} else if (ticks >= 2) {
						if(isPlayerNorth)
							p.setNextWorldTile(new WorldTile(1641, 5260, 0));
						if(!isPlayerNorth)
							p.setNextWorldTile(new WorldTile(1641, 5268, 0));
						stop();
					}
					ticks++;
				}
			}, 0, 1);
		}
	};

	public static ObjectClickHandler handleLowWall = new ObjectClickHandler(new Object[] { 47233, 47234, 47235 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getSkills().getLevel(Skills.AGILITY) < 86) {
				p.getPackets().sendGameMessage("You need level 86 agility to use this shortcut.");
				return;
			}

			WorldTasks.schedule(new WorldTask() {
				int ticks = 0;
				boolean isPlayerNorth = true;

				@Override
				public void run() {
					if (ticks == 0) {
						if (p.getX() == 1633 && p.getY() == 5294) {
							p.setFaceAngle(Direction.getAngleTo(Direction.SOUTH));
							p.setNextAnimation(new Animation(839));
							isPlayerNorth = true;
						} else if (p.getX() == 1633 && p.getY() == 5292) {
							p.setFaceAngle(Direction.getAngleTo(Direction.NORTH));
							p.setNextAnimation(new Animation(839));
							isPlayerNorth = false;
						} else
							return;
					} else if (ticks >= 1) {
						if (isPlayerNorth)
							p.setNextWorldTile(new WorldTile(1633, 5292, 0));
						if (!isPlayerNorth)
							p.setNextWorldTile(new WorldTile(1633, 5294, 0));
						stop();
					}
					ticks++;
				}

			}, 0, 1);
		}
	};

	public static ObjectClickHandler handleEntrances = new ObjectClickHandler(new Object[] { 47232 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getSlayer().getTask() != null) {
				TaskMonster currentTask = e.getPlayer().getSlayer().getTask().getMonster();
				switch (currentTask) {
				case HELLHOUNDS:
				case GREATER_DEMONS:
				case BLUE_DRAGONS:
				case GARGOYLES:
				case ABYSSAL_DEMONS:
				case DARK_BEASTS:
				case IRON_DRAGONS:
				case STEEL_DRAGONS:
					e.getPlayer().getControllerManager().startController(new KuradalDungeonController());
					return;
				default:
				}
			}
			e.getPlayer().sendMessage("Sorry, this dungeon is exclusive only to those who need to go in there.");
		}
	};
}
