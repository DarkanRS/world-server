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

import com.rs.cache.loaders.ObjectType;
import com.rs.game.World;
import com.rs.game.model.entity.ForceMovement;
import com.rs.game.model.entity.pathing.Direction;
import com.rs.game.model.entity.pathing.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager.Sub;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class BarbarianOutpostAgility {

	public static ObjectClickHandler handleObstaclePipe = new ObjectClickHandler(new Object[] { 20210 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 35))
				return;
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(10580));
			final WorldTile toTile = WorldTile.of(e.getObject().getX(), e.getPlayer().getY() >= 3561 ? 3558 : 3561, e.getObject().getPlane());
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, toTile, 2, e.getPlayer().getY() >= 3561 ? Direction.SOUTH : Direction.NORTH));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().unlock();
					e.getPlayer().setNextWorldTile(toTile);
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 1.0 / 20.0);
				}

			}, 1);
		}
	};

	public static ObjectClickHandler handleRopeSwing = new ObjectClickHandler(false, new Object[] { 43526 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 35))
				return;
			e.getPlayer().setRouteEvent(new RouteEvent(WorldTile.of(2551, 3554, 0), () -> {
				e.getPlayer().lock();
				e.getPlayer().setNextAnimation(new Animation(751));
				World.sendObjectAnimation(e.getPlayer(), e.getObject(), new Animation(497));
				final WorldTile toTile = WorldTile.of(e.getObject().getX(), 3549, e.getObject().getPlane());
				e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 1, toTile, 3, Direction.SOUTH));
				e.getPlayer().getSkills().addXp(Constants.AGILITY, 22);
				e.getPlayer().sendMessage("You skilfully swing across.", true);
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						e.getPlayer().unlock();
						e.getPlayer().setNextWorldTile(toTile);
						setStage(e.getPlayer(), 0);
					}
				}, 1);
			}));
		}
	};

	public static ObjectClickHandler handleLogBalance = new ObjectClickHandler(new Object[] { 43595 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 35))
				return;
			e.getPlayer().sendMessage("You walk carefully across the slippery log...", true);
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(9908));
			final WorldTile toTile = WorldTile.of(2541, e.getObject().getY(), e.getObject().getPlane());
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getObject().getTile(), 1, toTile, 12, Direction.WEST));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().setNextWorldTile(toTile);
					e.getPlayer().unlock();
					e.getPlayer().setNextAnimation(new Animation(-1));
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 13);
					e.getPlayer().sendMessage("... and make it safely to the other side.", true);
					if (getStage(e.getPlayer()) == 0)
						setStage(e.getPlayer(), 1);
				}

			}, 11);
		}
	};

	public static ObjectClickHandler handleClimbingNet = new ObjectClickHandler(new Object[] { 20211 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 35))
				return;
			e.getPlayer().sendMessage("You climb the netting...", true);
			e.getPlayer().getSkills().addXp(Constants.AGILITY, 8.2);
			e.getPlayer().useStairs(828, WorldTile.of(e.getObject().getX() - 1, e.getPlayer().getY(), 1), 1, 2);
			if (getStage(e.getPlayer()) == 1)
				setStage(e.getPlayer(), 2);
		}
	};

	public static ObjectClickHandler handleBalancingLedge = new ObjectClickHandler(new Object[] { 2302 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 35))
				return;
			e.getPlayer().sendMessage("You put your foot on the ledge and try to edge across...", true);
			e.getPlayer().lock();
			WorldTasks.schedule(new WorldTask() {
				int stage = 0;

				@Override
				public void run() {
					if (stage == 0)
						e.getPlayer().faceObject(e.getObject());
					else if (stage == 1) {
						e.getPlayer().setNextAnimation(new Animation(753));
						e.getPlayer().getAppearance().setBAS(157);
					} else if (stage == 2) {
						WorldTile toTile = WorldTile.of(2532, e.getObject().getY(), e.getObject().getPlane());
						e.getPlayer().addWalkSteps(toTile.getX(), toTile.getY(), -1, false);
					} else if (stage == 5) {
						e.getPlayer().setNextAnimation(new Animation(759));
						e.getPlayer().getAppearance().setBAS(-1);
						e.getPlayer().unlock();
						e.getPlayer().addWalkSteps(2532, 3546);
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 22);
						e.getPlayer().sendMessage("You skilfully edge across the gap.", true);
						if (getStage(e.getPlayer()) == 2)
							setStage(e.getPlayer(), 3);
						stop();
					}
					stage++;
				}
			}, 0, 0);
		}
	};

	public static ObjectClickHandler handleLowWall = new ObjectClickHandler(new Object[] { 1948 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 35))
				return;
			if (e.getPlayer().getX() >= e.getObject().getX()) {
				e.getPlayer().sendMessage("You cannot climb that from this side.");
				return;
			}
			e.getPlayer().sendMessage("You climb the low wall...", true);
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(4853));
			final WorldTile toTile = WorldTile.of(e.getObject().getX() + 1, e.getObject().getY(), e.getObject().getPlane());
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, toTile, 2, Direction.EAST));
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().lock(1);
					e.getPlayer().setNextWorldTile(toTile);
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 13.7);
					int stage = getStage(e.getPlayer());
					if (stage == 3)
						setStage(e.getPlayer(), 4);
					else if (stage == 4) {
						e.getPlayer().incrementCount("Barbarian normal laps");
						removeStage(e.getPlayer());
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 46.2);
					}
				}
			}, 1);
		}
	};

	public static ObjectClickHandler handleWallRun = new ObjectClickHandler(new Object[] { 43533 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 90))
				return;
			e.getPlayer().lock();
			final WorldTile toTile = WorldTile.of(2538, 3545, 2);
			WorldTasks.schedule(new WorldTask() {
				int stage = 0;

				@Override
				public void run() {
					if (stage == 0)
						e.getPlayer().setNextFaceWorldTile(e.getPlayer().transform(0, 1, 0));
					else if (stage == 1)
						e.getPlayer().setNextAnimation(new Animation(10492));
					else if (stage == 7) {
						e.getPlayer().setNextWorldTile(e.getPlayer().transform(0, 0, 2));
						e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, toTile, 1, Direction.NORTH));
						e.getPlayer().setNextAnimation(new Animation(10493));
					} else if (stage == 10) {
						e.getPlayer().unlock();
						e.getPlayer().setNextWorldTile(toTile);
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 15);
					}
					stage++;
				}
			}, 0, 0);
		}
	};

	public static ObjectClickHandler handleWallClimb = new ObjectClickHandler(false, new Object[] { 43597 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 90))
				return;
			e.getPlayer().setRouteEvent(new RouteEvent(e.getObject().getTile(), () -> {
				e.getPlayer().lock();
				WorldTasks.schedule(new WorldTask() {
					int stage = 0;
					@Override
					public void run() {
						if (stage == 0)
							e.getPlayer().faceTile(e.getPlayer().transform(-1, 0, 0));
						else if (stage == 1)
							e.getPlayer().setNextAnimation(new Animation(10023));
						else if (stage == 3) {
							e.getPlayer().setNextWorldTile(WorldTile.of(2536, 3546, 3));
							e.getPlayer().setNextAnimation(new Animation(11794));
						} else if (stage == 4) {
							e.getPlayer().unlock();
							e.getPlayer().getSkills().addXp(Constants.AGILITY, 15);
						}
						stage++;
					}
				}, 0, 0);
			}));
		}
	};

	public static ObjectClickHandler handleSpringDevice = new ObjectClickHandler(false, new Object[] { 43587 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 90))
				return;

			e.getPlayer().setRouteEvent(new RouteEvent(WorldTile.of(2533, 3547, 3), () -> {
				WorldTile toTile = WorldTile.of(2532, 3553, 3);

				e.getPlayer().lock();
				WorldTasks.schedule(new WorldTask() {
					int stage = 0;
					@Override
					public void run() {
						if (stage == 0)
							e.getPlayer().faceTile(WorldTile.of(2531, 3554, 3));
						else if (stage == 1) {
							e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 1, toTile, 3, Direction.NORTH));
							e.getPlayer().setNextAnimation(new Animation(4189));
							World.sendObjectAnimation(e.getPlayer(), e.getObject(), new Animation(11819));
						} else if (stage == 4) {
							e.getPlayer().unlock();
							e.getPlayer().setNextWorldTile(toTile);
							e.getPlayer().getSkills().addXp(Constants.AGILITY, 15);
							World.sendObjectAnimation(e.getPlayer(), World.getObject(WorldTile.of(2531, 3554, 3), ObjectType.SCENERY_INTERACT), new Animation(7527));
							stop();
						}
						stage++;
					}
				}, 0, 0);
			}));
		}
	};

	public static ObjectClickHandler handleBalanceBeam = new ObjectClickHandler(new Object[] { 43527 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 90))
				return;
			e.getPlayer().lock();
			final WorldTile toTile = WorldTile.of(2536, 3553, 3);
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 1, toTile, 3, Direction.EAST));
			e.getPlayer().setNextAnimation(new Animation(16079));
			e.getPlayer().getAppearance().setBAS(330);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().stopAll();
					e.getPlayer().getInterfaceManager().removeSubs(Sub.TAB_INVENTORY, Sub.TAB_MAGIC, Sub.TAB_EMOTES, Sub.TAB_EQUIPMENT, Sub.TAB_PRAYER);
					e.getPlayer().unlock();
					e.getPlayer().setNextWorldTile(toTile);
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 15);
					e.getPlayer().setNextAnimation(new Animation(-1));
					stop();
				}

			}, 2);
		}
	};

	public static ObjectClickHandler handleJumpGap = new ObjectClickHandler(new Object[] { 43531 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 90))
				return;
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(2586));
			e.getPlayer().getAppearance().setBAS(-1);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					e.getPlayer().unlock();
					e.getPlayer().getInterfaceManager().sendSubDefaults(Sub.TAB_INVENTORY, Sub.TAB_MAGIC, Sub.TAB_EMOTES, Sub.TAB_EQUIPMENT, Sub.TAB_PRAYER);
					e.getPlayer().setNextWorldTile(WorldTile.of(2538, 3553, 2));
					e.getPlayer().setNextAnimation(new Animation(2588));
					e.getPlayer().getSkills().addXp(Constants.AGILITY, 15);
					stop();
				}

			}, 0);
		}
	};

	public static ObjectClickHandler handleRoofSlide = new ObjectClickHandler(new Object[] { 43532 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!Agility.hasLevel(e.getPlayer(), 90))
				return;
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(11792));
			final WorldTile toTile = WorldTile.of(2544, e.getPlayer().getY(), 0);
			e.getPlayer().setNextForceMovement(new ForceMovement(e.getPlayer().getTile(), 0, toTile, 5, Direction.EAST));
			WorldTasks.schedule(new WorldTask() {
				int stage;

				@Override
				public void run() {
					if (stage == 0) {
						e.getPlayer().setNextWorldTile(WorldTile.of(2541, e.getPlayer().getY(), 1));
						e.getPlayer().setNextAnimation(new Animation(11790));
						stage = 1;
					} else if (stage == 1)
						stage = 2;
					else if (stage == 2) {
						e.getPlayer().setNextAnimation(new Animation(11791));
						stage = 3;
					} else if (stage == 3) {
						e.getPlayer().unlock();
						e.getPlayer().setNextWorldTile(toTile);
						e.getPlayer().setNextAnimation(new Animation(2588));
						e.getPlayer().getSkills().addXp(Constants.AGILITY, 15);
						if (getStage(e.getPlayer()) == 1) {
							e.getPlayer().incrementCount("Barbarian advanced laps");
							removeStage(e.getPlayer());
							e.getPlayer().getSkills().addXp(Constants.AGILITY, 615);
						}
						stop();
					}
				}

			}, 0, 0);
		}
	};

	public static void removeStage(Player player) {
		player.getTempAttribs().removeI("BarbarianOutpostCourse");
	}

	public static void setStage(Player player, int stage) {
		player.getTempAttribs().setI("BarbarianOutpostCourse", stage);
	}

	public static int getStage(Player player) {
		return player.getTempAttribs().getI("BarbarianOutpostCourse");
	}
}
