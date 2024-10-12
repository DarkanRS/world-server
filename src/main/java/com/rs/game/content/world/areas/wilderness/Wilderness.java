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
package com.rs.game.content.world.areas.wilderness;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.statements.Statement;
import com.rs.game.World;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.thieving.Thieving;
import com.rs.game.content.transportation.WildernessObelisk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.*;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.DropSets;

@PluginEventHandler
public class Wilderness {

	public static ObjectClickHandler handleMagicAxeHutChests = new ObjectClickHandler(new Object[] { 2566 }, new Tile[] { Tile.of(3188, 3962, 0), Tile.of(3189, 3962, 0), Tile.of(3193, 3962, 0) }, e -> {
		switch(e.getOpNum()) {
		case OBJECT_OP1 -> {
			e.getPlayer().sendMessage("You attempt to open the chest without disarming the traps.");
			e.getPlayer().applyHit(new Hit((int) (e.getPlayer().getSkills().getLevel(Skills.HITPOINTS) + 20), Hit.HitLook.TRUE_DAMAGE));
		}
		case OBJECT_OP2 -> Thieving.checkTrapsChest(e.getPlayer(), e.getObject(), 2574, 32, 14, 7.5, DropSets.getDropSet("magic_axe_hut_chest"));
		default -> e.getPlayer();
		}
	});

	public static ObjectClickHandler handleKBDEnterLadder = new ObjectClickHandler(new Object[] { 1765 }, new Tile[] { Tile.of(3017, 3849, 0) }, e -> e.getPlayer().useStairs(828, Tile.of(3069, 10255, 0), 1, 2));

	public static ObjectClickHandler handleKBDExitLadder = new ObjectClickHandler(new Object[] { 32015 }, new Tile[] { Tile.of(3069, 10256, 0) }, e -> e.getPlayer().useStairs(828, Tile.of(3017, 3848, 0), 1, 2));

	public static ObjectClickHandler handleKBDEnterLever = new ObjectClickHandler(new Object[] { 1816 }, new Tile[] { Tile.of(3067, 10252, 0) }, e -> {
		e.getPlayer().stopAll();
		Magic.pushLeverTeleport(e.getPlayer(), Tile.of(2273, 4681, 0));
		e.getPlayer().getControllerManager().forceStop();
	});

	public static ObjectClickHandler handleKBDExitLever = new ObjectClickHandler(new Object[] { 1817 }, new Tile[] { Tile.of(2273, 4680, 0) }, e -> {
		Magic.pushLeverTeleport(e.getPlayer(), Tile.of(3067, 10254, 0));
		e.getPlayer().getControllerManager().startController(new WildernessController());
	});

	public static ObjectClickHandler handleFireGiantDungeonExit = new ObjectClickHandler(new Object[] { 32048 }, new Tile[] { Tile.of(3043, 10328, 0) }, e -> {
		e.getPlayer().tele(e.getPlayer().transform(3, -6400, 0));
		e.getPlayer().getControllerManager().startController(new WildernessController());
	});

	public static ObjectClickHandler handleRedDragIsleShortcut = new ObjectClickHandler(new Object[] { 73657 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 54)) {
			e.getPlayer().getPackets().sendGameMessage("You need level 54 agility to use this shortcut.");
			return;
		}
		e.getPlayer().forceMove(e.getPlayer().transform(e.getPlayer().getY() > 3800 ? 1 : -1, e.getPlayer().getY() > 3800 ? -2 : 2), 4721, 25, 30);
	});

	public static ObjectClickHandler handleGWDShortcut = new ObjectClickHandler(new Object[] { 26323, 26324, 26328, 26327 }, e -> {
		Player p = e.getPlayer();
		WorldObject obj = e.getObject();
		if (!Agility.hasLevel(p, 60)) {
			p.getPackets().sendGameMessage("You need level 60 agility to use this shortcut.");
			return;
		}

		//Wildy
		if(obj.getId() == 26327)
			e.getPlayer().forceMove(Tile.of(2943, 3767, 0), 2049, 25, 60);
		if(obj.getId() == 26328) {
			p.tele(Tile.of(2943, 3767, 0));
			e.getPlayer().forceMove(Tile.of(2950, 3767, 0), 2050, 25, 60);
		}

		//Outside GWD
		if(obj.getId() == 26324)
			e.getPlayer().forceMove(Tile.of(2928, 3757, 0), 2049, 25, 60);
		if(obj.getId() == 26323)
			e.getPlayer().forceMove(Tile.of(2927, 3761, 0), 2050, 25, 60);
	});

	public static ObjectClickHandler handleTeleportObelisks = new ObjectClickHandler(new Object[] { 65616, 65617, 65618, 65619, 65620, 65621, 65622 }, e -> WildernessObelisk.activateObelisk(e.getObjectId(), e.getPlayer()));

	public static ObjectClickHandler handleChaosAltar = new ObjectClickHandler(new Object[] { 65371 }, e -> e.getPlayer().getPrayer().worshipAltar());

	public static ObjectClickHandler handleArmouredZombieTrapdoor = new ObjectClickHandler(new Object[] { 65715 }, e -> e.getPlayer().tele(Tile.of(3241, 9991, 0)));

	public static ObjectClickHandler handleArmouredZombieLadder = new ObjectClickHandler(new Object[] { 39191 }, e -> {
		e.getPlayer().useStairs(828, Tile.of(3240, 3607, 0), 1, 1);
		e.getPlayer().getControllerManager().startController(new WildernessController());
	});

	public static ObjectClickHandler handleForinthryDungeon = new ObjectClickHandler(new Object[] { 18341, 20599, 18342, 20600 }, e -> {
		Player player = e.getPlayer();
		switch (e.getObjectId()) {
			case 18341 -> player.useStairs(-1, Tile.of(3039, 3765, 0), 0, 1);
			case 20599 -> player.useStairs(-1, Tile.of(3037, 10171, 0), 0, 1);
			case 18342 -> player.useStairs(-1, Tile.of(3071, 3649, 0), 0, 1);
			case 20600 -> player.useStairs(-1, Tile.of(3077, 10058, 0), 0, 1);
		}
	});

	public static ObjectClickHandler handleSparklingPool = new ObjectClickHandler(new Object[] { 2878, 2879 }, e -> {
		Player player = e.getPlayer();
		int id = e.getObject().getId();

		player.simpleDialogue("You step into the pool of sparkling water. You feel the energy rush through your veins.");
		Tile destination = id == 2879 ? Tile.of(2509, 4687, 0) : Tile.of(2542, 4720, 0);
		Tile teleportTarget = id == 2879 ? Tile.of(2542, 4718, 0) : Tile.of(2509, 4689, 0);

		player.forceMove(destination, 13842, 0, 60, () -> {
			player.anim(-1);
			player.tele(teleportTarget);
		});
	});

	public static ObjectClickHandler handleGodStatues = new ObjectClickHandler(new Object[] { 2873, 2874, 2875 }, e -> {
		int id = e.getObjectId();
		e.getPlayer().sendMessage("You kneel and begin to chant to " + e.getObject().getDefinitions().getName().replace("Statue of ", "") + "...");
		e.getPlayer().setNextAnimation(new Animation(645));

		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				e.getPlayer().simpleDialogue("You feel a rush of energy charge through your veins. Suddenly a cape appears before you.");
				Tile location = Tile.of(e.getObject().getX(), e.getObject().getY() - 1, 0);
				World.sendSpotAnim(location, new SpotAnim(1605));
				Item capeItem = new Item(id == 2873 ? 2412 : id == 2874 ? 2414 : 2413);
				World.addGroundItem(capeItem, location);
			}
		}, 3);
	});

	public static ObjectClickHandler handleCorpCave = new ObjectClickHandler(new Object[] { 38811, 37928, 37929, 38815 }, e -> {
		Player player = e.getPlayer();
        switch (e.getObjectId()) {
			case 38811 -> player.getInterfaceManager().sendInterface(650);
			case 37929 -> {
				player.stopAll();
				player.tele(Tile.of(e.getPlayer().getX() == 2921 ? 2917 : 2921, e.getPlayer().getY(), player.getPlane()));
			}
			case 37928 -> {
				player.stopAll();
				player.tele(Tile.of(3214, 3782, 0));
				player.getControllerManager().startController(new WildernessController());
			}
			case 38815 -> {
				if (player.getSkills().getLevelForXp(Constants.WOODCUTTING) < 37 ||
					player.getSkills().getLevelForXp(Constants.MINING) < 45 ||
					player.getSkills().getLevelForXp(Constants.SUMMONING) < 23 ||
					player.getSkills().getLevelForXp(Constants.FIREMAKING) < 47 ||
					player.getSkills().getLevelForXp(Constants.PRAYER) < 55) {

					player.sendMessage("You need 23 Summoning, 37 Woodcutting, 45 Mining, 47 Firemaking and 55 Prayer to enter this dungeon.");
					return;
				}
				player.stopAll();
				player.tele(Tile.of(2885, 4372, 2));
				player.getControllerManager().forceStop();
			}
		}
	});

	public static ObjectClickHandler handleCorpCave2 = new ObjectClickHandler(new Object[] { 37929 }, new Tile[] { Tile.of(2918, 4382, 0) }, e -> {
		e.getPlayer().stopAll();
		e.getPlayer().tele(Tile.of(e.getPlayer().getX() == 2921 ? 2917 : 2921, e.getPlayer().getY(), e.getPlayer().getPlane()));
	});

	public static ObjectClickHandler handleWildyLevers = new ObjectClickHandler(new Object[] { 5959, 5960, 1814, 1815 }, e -> {
		Player player = e.getPlayer();
		switch (e.getObjectId()) {
			case 5959 -> Magic.pushLeverTeleport(player, Tile.of(2539, 4712, 0));
			case 5960 -> Magic.pushLeverTeleport(player, Tile.of(3089, 3957, 0));
			case 1814 -> Magic.pushLeverTeleport(player, Tile.of(3155, 3923, 0));
			case 1815 -> Magic.pushLeverTeleport(player, Tile.of(2561, 3311, 0));
		}
	});

	public static ObjectClickHandler handlePiratesEnclave = new ObjectClickHandler(new Object[] { 65349, 32048 }, e -> {
		switch (e.getObjectId()) {
			case 65349 -> e.getPlayer().useStairs(-1, Tile.of(3044, 10325, 0), 0, 1);
			case 32048 -> e.getPlayer().useStairs(-1, Tile.of(3045, 3927, 0), 0, 1);
		}
	});

	public static boolean isDitch(int id) {
		return id >= 1440 && id <= 1444 || id >= 65076 && id <= 65087;
	}

	public static ObjectClickHandler handleWildernessDitch = new ObjectClickHandler(new Object[] { 1440, 1441, 1442, 1443, 1444, 65076, 65077, 65078, 65079, 65080, 65081, 65082, 65083, 65084, 65085, 65086, 65087 }, e -> {
		Player player = e.getPlayer();
		GameObject object = e.getObject();

		if (WildernessController.isDitch(e.getObjectId())) {
			player.startConversation(new Dialogue()
				.addNext(new Statement() {
					@Override
					public void send(Player player) {
						player.getInterfaceManager().sendInterface(382);
					}

					@Override
					public int getOptionId(int componentId) {
						return componentId == 19 ? 0 : 1;
					}

					@Override
					public void close(Player player) {
						// No action needed on close
					}
				})
				.addNext(() -> {
					player.stopAll();
					player.forceMove(
						Tile.of(
							object.getRotation() == 3 || object.getRotation() == 1 ? object.getX() - 1 : player.getX(),
							object.getRotation() == 0 || object.getRotation() == 2 ? object.getY() + 2 : player.getY(),
							object.getPlane()
						),
						6132,
						25,
						60,
						() -> {
							player.faceObject(object);
							player.getControllerManager().startController(new WildernessController());
							player.resetReceivedDamage();
						}
					);
				})
			);
		}
	});

}
