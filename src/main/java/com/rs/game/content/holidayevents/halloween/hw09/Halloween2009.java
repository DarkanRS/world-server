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
package com.rs.game.content.holidayevents.halloween.hw09;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.impl.DestroyItem;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldObject;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemEquipEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemEquipHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.spawns.ObjectSpawn;
import com.rs.utils.spawns.ObjectSpawns;

@PluginEventHandler
public class Halloween2009 {

	public static String STAGE_KEY = "hw2022";
	public static final boolean ENABLED = false;

	static WorldTile WEB_RESET_LOC = WorldTile.of(3936, 5125, 2);

	public static WorldTile START_LOCATION = WorldTile.of(3808, 5135, 0);

	private static HashMap<Integer, Set<Integer>> WEBS = new HashMap<>();

	private static Integer[][] PATHS = {
			{ 46711, 46710, 46706, 46712 },
			{ 46711, 46708, 46705, 46698, 46692, 46695, 46715 },
			{ 46711, 46709, 46707, 46701, 46700, 46696, 46717 },
			{ 46711, 46710, 46707, 46701, 46700, 46696, 46693, 46716 },
			{ 46711, 46708, 46697, 46690, 46692, 46695, 46715 },
			{ 46711, 46709, 46702, 46691, 46694, 46696, 46693, 46695, 46715 },
			{ 46711, 46708, 46697, 46690, 46692, 46695, 46693, 46696, 46717 },
			{ 46711, 46709, 46707, 46701, 46700, 46696, 46717 },
			{ 46711, 46709, 46702, 46691, 46688, 46689, 46692, 46695, 46699, 46714 }
	};

	static {
		//South line
		WEBS.put(46711, new HashSet<>(Arrays.asList(46708, 46709, 46710)));
		WEBS.put(46710, new HashSet<>(Arrays.asList(46711, 46705, 46706, 46707)));
		WEBS.put(46706, new HashSet<>(Arrays.asList(46710, 46703, 46712, 46704)));
		WEBS.put(46712, new HashSet<>(Arrays.asList(46706)));
		//Southeast line
		WEBS.put(46709, new HashSet<>(Arrays.asList(46702, 46707, 46711)));
		WEBS.put(46707, new HashSet<>(Arrays.asList(46701, 46704, 46709, 46710)));
		WEBS.put(46704, new HashSet<>(Arrays.asList(46700, 46706, 46707, 46719)));
		WEBS.put(46719, new HashSet<>(Arrays.asList(46704)));
		//East line
		WEBS.put(46702, new HashSet<>(Arrays.asList(46701, 46709, 46691)));
		WEBS.put(46701, new HashSet<>(Arrays.asList(46702, 46707, 46700, 46694)));
		WEBS.put(46700, new HashSet<>(Arrays.asList(46701, 46704, 46718, 46696)));
		WEBS.put(46718, new HashSet<>(Arrays.asList(46700)));
		//Northeast line
		WEBS.put(46691, new HashSet<>(Arrays.asList(46702, 46694, 46688)));
		WEBS.put(46694, new HashSet<>(Arrays.asList(46691, 46701, 46696, 46689)));
		WEBS.put(46696, new HashSet<>(Arrays.asList(46694, 46700, 46717, 46693)));
		WEBS.put(46717, new HashSet<>(Arrays.asList(46696)));
		//North line
		WEBS.put(46688, new HashSet<>(Arrays.asList(46691, 46689, 46690)));
		WEBS.put(46689, new HashSet<>(Arrays.asList(46688, 46694, 46693, 46692)));
		WEBS.put(46693, new HashSet<>(Arrays.asList(46689, 46696, 46716, 46695)));
		WEBS.put(46716, new HashSet<>(Arrays.asList(46693)));
		//Northwest line
		WEBS.put(46690, new HashSet<>(Arrays.asList(46688, 46692, 46697)));
		WEBS.put(46692, new HashSet<>(Arrays.asList(46689, 46695, 46698, 46690)));
		WEBS.put(46695, new HashSet<>(Arrays.asList(46693, 46715, 46699, 46692)));
		WEBS.put(46715, new HashSet<>(Arrays.asList(46695)));
		//West line
		WEBS.put(46697, new HashSet<>(Arrays.asList(46690, 46698, 46708)));
		WEBS.put(46698, new HashSet<>(Arrays.asList(46697, 46692, 46699, 46705)));
		WEBS.put(46699, new HashSet<>(Arrays.asList(46698, 46695, 46714, 46703)));
		WEBS.put(46714, new HashSet<>(Arrays.asList(46699)));
		//Southwest airline
		WEBS.put(46708, new HashSet<>(Arrays.asList(46697, 46705, 46711)));
		WEBS.put(46705, new HashSet<>(Arrays.asList(46708, 46698, 46703, 46710)));
		WEBS.put(46703, new HashSet<>(Arrays.asList(46705, 46699, 46713, 46706)));
		WEBS.put(46713, new HashSet<>(Arrays.asList(46703)));
	}

	@ServerStartupEvent(Priority.FILE_IO)
	public static void loadPortal() {
		if (ENABLED)
			ObjectSpawns.add(new ObjectSpawn(31845, 10, 0, WorldTile.of(3210, 3425, 0), "Portal to enter Death's House."));
	}

	public static ItemClickHandler handleEek = new ItemClickHandler(new Object[] { 15353 }, new String[] { "Hold", "Talk-to", "Play-with", "Dismiss" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Hold":
				Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId(), true);
				break;
			case "Talk-to":
				break;
			case "Play-with":
				e.getPlayer().setNextAnimation(new Animation(12490));
				e.getPlayer().setNextSpotAnim(new SpotAnim(2178));
				break;
			case "Dismiss":
				e.getPlayer().startConversation(new DestroyItem(e.getPlayer(), e.getSlotId(), e.getItem()));
				break;
			}
		}
	};

	public static ObjectClickHandler handleWebWalks = new ObjectClickHandler(false, WEBS.keySet().toArray()) {
		@Override
		public void handle(ObjectClickEvent e) {
			WorldObject curr = World.getClosestObject("Web", e.getPlayer().getTile(), 3);
			if (curr == null) {
				e.getPlayer().sendMessage("You aren't in a position to walk towards that web!");
				return;
			}
			Set<Integer> possibles = WEBS.get(curr.getId());
			if (possibles == null || !possibles.contains(e.getObjectId())) {
				e.getPlayer().sendMessage("You aren't in a position to walk towards that web!");
				return;
			}
			Halloween2009Controller ctrl = e.getPlayer().getControllerManager().getController(Halloween2009Controller.class);
			if (ctrl == null || ctrl.getPath() == null) {
				e.getPlayer().sendMessage("Error occurred while getting path. Moving you to start point.");
				e.getPlayer().getControllerManager().startController(new Halloween2009Controller());
				return;
			}
			boolean failed = !ctrl.getPath().contains(e.getObjectId());
			boolean running = e.getPlayer().getRun();
			e.getPlayer().lock();
			e.getPlayer().setRunHidden(false);
			WorldTile from = curr.getCoordFace();
			WorldTile to = e.getObject().getCoordFace();
			boolean needStart = !e.getPlayer().matches(from);
			if (needStart)
				e.getPlayer().addWalkSteps(curr.getCoordFace(), 2, false);
			WorldTasks.schedule(new WorldTask() {
				boolean started;
				int failTimer = 3;

				@Override
				public void run() {
					if (failed && failTimer <= 0) {
						e.getPlayer().sendMessage("Oops! That didn't seem like the right way!", true);
						e.getPlayer().resetWalkSteps();
						e.getPlayer().getAppearance().setBAS(-1);
						e.getPlayer().setNextAnimation(new Animation(12917));
						WorldTasks.delay(1, () -> {
							e.getPlayer().setNextAnimation(new Animation(767));
							e.getPlayer().unlock();
							e.getPlayer().setNextWorldTile(WEB_RESET_LOC);
						});
						stop();
					}
					if (!started) {
						started = true;
						e.getPlayer().getAppearance().setBAS(155);
						e.getPlayer().addWalkSteps(to, 25, false);
					}
					if (e.getPlayer().matches(to)) {
						e.getPlayer().getAppearance().setBAS(-1);
						e.getPlayer().setRunHidden(running);
						e.getPlayer().unlock();
						stop();
					}
					failTimer--;
				}
			}, needStart ? 1 : 0, 0);

		}
	};

	public static ObjectClickHandler handleEnter = new ObjectClickHandler(new Object[] { 31845 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!ENABLED)
				return;
			e.getPlayer().getControllerManager().startController(new Halloween2009Controller());
		}
	};

	public static ObjectClickHandler handleGrimStairs = new ObjectClickHandler(new Object[] { 27866, 27870 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(e.getPlayer().transform(e.getObjectId() == 27866 ? -5 : 5, 0, e.getObjectId() == 27866 ? 1 : -1));
		}
	};

	public static ObjectClickHandler spiderPortal = new ObjectClickHandler(new Object[] { 46932 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getI(Halloween2009.STAGE_KEY) >= 6) {
				e.getPlayer().sendOptionDialogue("Select an Option", ops -> {
					ops.add("Go to web maze.", () -> {
						e.getPlayer().setNextAnimation(new Animation(12776));
						WorldTasks.delay(1, () -> {
							e.getPlayer().setNextAnimation(new Animation(12777));
							e.getPlayer().setNextWorldTile(WorldTile.of(3936, 5125, 2));
							e.getPlayer().getPackets().sendRunScript(2582, 837, 0, 0); //turn off scenery shadows so people can see the floor...
						});
					});
					ops.add("Go to spider court.", () -> {
						e.getPlayer().setNextAnimation(new Animation(12776));
						WorldTasks.delay(1, () -> {
							e.getPlayer().setNextAnimation(new Animation(12777));
							e.getPlayer().setNextWorldTile(WorldTile.of(3744, 5287, 0));
							e.getPlayer().getPackets().sendRunScript(2582, 837, 0, 0); //turn off scenery shadows so people can see the floor...
						});
					});
				});
			} else {
				e.getPlayer().setNextAnimation(new Animation(12776));
				WorldTasks.delay(1, () -> {
					e.getPlayer().setNextAnimation(new Animation(12777));
					e.getPlayer().setNextWorldTile(WorldTile.of(3936, 5125, 2));
					e.getPlayer().getPackets().sendRunScript(2582, 837, 0, 0); //turn off scenery shadows so people can see the floor...
				});
			}
		}
	};

	public static ObjectClickHandler spiderPortalExit = new ObjectClickHandler(new Object[] { 46934 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextAnimation(new Animation(12776));
			WorldTasks.delay(1, () -> {
				e.getPlayer().setNextAnimation(new Animation(12777));
				e.getPlayer().setNextWorldTile(WorldTile.of(3805, 5149, 0));
			});
		}
	};

	public static ObjectClickHandler webLaddersDown = new ObjectClickHandler(new Object[] { 46936 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getTile().isAt(3744, 5288)) {
				if (e.getPlayer().getI(Halloween2009.STAGE_KEY) >= 6) {
					e.getPlayer().sendOptionDialogue("Select an Option", ops -> {
						ops.add("Go down the ladder.", () -> e.getPlayer().useLadder(/*WorldTile.of(3936, 5372, 2)*/WorldTile.of(3936, 5150, 2)));
						ops.add("Return to the Grim Reaper's House.", () -> e.getPlayer().useLadder(WorldTile.of(3805, 5149, 0)));
					});
					return;
				}
				e.getPlayer().useLadder(/*WorldTile.of(3936, 5372, 2)*/WorldTile.of(3936, 5150, 2));
			} else
				e.getPlayer().useLadder(WorldTile.of(3744, 5276, 0));
		}
	};

	public static ObjectClickHandler agilCourseLadderDown = new ObjectClickHandler(new Object[] { 46939 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(WorldTile.of(3936, 5150, 2));
		}
	};

	public static ObjectClickHandler agilCourseLadderUp = new ObjectClickHandler(new Object[] { 46731 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useLadder(/*e.getObject().isAt(3936, 5151) ? WorldTile.of(3936, 5315, 2) : */WorldTile.of(3744, 5287, 0));
		}
	};

	public static ObjectClickHandler webLaddersUp = new ObjectClickHandler(new Object[] { 46938 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getI(Halloween2009.STAGE_KEY) < 4) {
				e.getPlayer().startConversation(new Dialogue().addNPC(8976, HeadE.SPIDER_EXCLAIM, "Halt! Nobody is permitted to see the Spider Queen. Especially not a four-limbed intruder like yourself!"));
				return;
			}
			e.getPlayer().useLadder(WorldTile.of(3809, 5277, 0));
		}
	};

	public static Set<Integer> getRandomPath() {
		return new HashSet<>(Arrays.asList(PATHS[Utils.random(PATHS.length)]));
	}

	public static ItemEquipHandler handleEekEquip = new ItemEquipHandler(15353) {
		@Override
		public void handle(ItemEquipEvent e) {
			refreshWebbables(e.getPlayer(), e.equip());
		}
	};

	public static ObjectClickHandler webbables = new ObjectClickHandler(Arrays.stream(Utils.range(46861, 46924)).boxed().toArray(Integer[]::new)) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (!e.getOption().equals("Web"))
				return;
			Halloween2009Controller ctrl = e.getPlayer().getControllerManager().getController(Halloween2009Controller.class);
			if (ctrl == null || ctrl.getWebbedUp() == null) {
				e.getPlayer().sendMessage("Error occurred while getting path. Moving you to start point.");
				e.getPlayer().getControllerManager().startController(new Halloween2009Controller());
				return;
			}
			e.getPlayer().lock();
			e.getPlayer().setNextAnimation(new Animation(12490));
			e.getPlayer().setNextSpotAnim(new SpotAnim(2178));
			WorldTasks.delay(2, () -> {
				e.getPlayer().unlock();
				e.getPlayer().setNextAnimation(new Animation(1264));
				e.getPlayer().setNextSpotAnim(new SpotAnim(-1));
				ctrl.web(e.getObjectId());
			});
		}
	};

	public static void refreshWebbables(Player player, boolean showWebOp) {
		if (player.getI(Halloween2009.STAGE_KEY) < 7)
			return;
		Halloween2009Controller ctrl = player.getControllerManager().getController(Halloween2009Controller.class);
		if (ctrl == null || ctrl.getWebbedUp() == null)
			return;
		for (int i = 46861;i <= 46924;i++)
			player.getVars().setVarBit(ObjectDefinitions.getDefs(i).varpBit, ctrl.getWebbedUp().contains(i) || player.getI(Halloween2009.STAGE_KEY) > 8 ? 2 : showWebOp ? 1 : 0);

	}

}
