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
package com.rs.game.content.world.areas.karamja;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.World;
import com.rs.game.content.quests.dragonslayer.DragonSlayer;
import com.rs.game.content.skills.agility.Agility;
import com.rs.game.content.world.FurnacesKt;
import com.rs.game.content.world.doors.Doors;
import com.rs.engine.pathfinder.Direction;
import com.rs.engine.pathfinder.RouteEvent;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.lib.game.WorldObject;
import com.rs.lib.net.ClientPacket;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.*;

@PluginEventHandler
public class Karamja  {

	public static ObjectClickHandler handleShiloFurnace = new ObjectClickHandler(new Object[] { 11666 }, (e) -> {
		Player player = e.getPlayer();
		if (!e.getOption().equals("Smelt"))
			return;

		FurnacesKt.use(player, e.getObject());
	});

	public static ObjectClickHandler handleShiloFurnaceDoor = new ObjectClickHandler(new Object[] { 2266, 2267 }, e -> {
		if (e.getObjectId() == 2267)
			return;
		Player player = e.getPlayer();

		int yohnus = 513;
		int blacksmithPays = player.getI("shilo_blacksmith_pay");
		switch (e.getOption()) {
			case "Open" -> {
				if (e.getPlayer().getY() > e.getObject().getY()) {
					Doors.handleDoor(e.getPlayer(), e.getObject());
					return;
				}
				if (blacksmithPays <= 0) {
					player.startConversation(new Dialogue()
							.addNPC(yohnus, HeadE.CALM_TALK, "Sorry but the blacksmiths is closed. But I can let you use the furnace at the cost of 20 gold pieces.")
							.addOptions((ops) -> {
								ops.add("Use Furnace - 20 Gold")
										.addNext(() -> {
											if (!player.getInventory().hasCoins(20)) {
												player.startConversation(new Dialogue()
														.addNPC(yohnus, HeadE.SAD_MILD_LOOK_DOWN, "Sorry Bwana, you do not have enough gold!"));
												return;
											}
											player.getInventory().removeCoins(20);
											Doors.handleDoor(e.getPlayer(), e.getObject());
										})
										.addNPC(yohnus, HeadE.HAPPY_TALKING, "Thanks Bwana! Enjoy the facilities!");
								ops.add("No thanks!")
										.addPlayer(HeadE.CALM_TALK, "No thanks!")
										.addNPC(yohnus, HeadE.CALM_TALK, "Very well Bwana, have a nice day.");
							}));
					return;
				}
				player.set("shilo_blacksmith_pay", blacksmithPays - 1);
				Doors.handleDoor(e.getPlayer(), e.getObject());
			}
			case "Use-furnace(20gp)" -> {
				if (e.getPlayer().getY() > e.getObject().getY()) {
					Doors.handleDoor(e.getPlayer(), e.getObject());
					return;
				}
				if (!player.getInventory().hasCoins(20)) {
					player.startConversation(new Dialogue()
							.addNPC(yohnus, HeadE.SAD_MILD_LOOK_DOWN, "Sorry Bwana, you do not have enough gold!"));
					return;
				}
				player.getInventory().removeCoins(20);
				Doors.handleDoor(e.getPlayer(), e.getObject());
			}
		}
	});

	public static ObjectClickHandler handleBrimhavenDungeonEntrance = new ObjectClickHandler(new Object[] { 5083 }, e -> {
		if(e.getPlayer().getTempAttribs().getB("paid_brimhaven_entrance_fee")) {//12 hours
			e.getPlayer().tele(Tile.of(2713, 9564, 0));
			return;
		}
		e.getPlayer().startConversation(new Dialogue().addNPC(1595, HeadE.FRUSTRATED, "You can't go in there without paying!"));
	});

	public static ObjectClickHandler handleBoatLadder = new ObjectClickHandler(new Object[] { 273 }, new Tile[] { Tile.of(2847, 3235, 1) }, e -> e.getPlayer().useStairs(e.getPlayer().transform(0, 0, -1)));

	public static ObjectClickHandler handleBrimhavenDungeonExit = new ObjectClickHandler(new Object[] { 5084 }, e -> e.getPlayer().tele(Tile.of(2745, 3152, 0)));

	public static ObjectClickHandler handleJogreLogWalk = new ObjectClickHandler(new Object[] { 2332 }, e -> {
		if (e.getPlayer().getX() > 2908)
			Agility.walkToAgility(e.getPlayer(), 155, Direction.WEST, 4, 4);
		else
			Agility.walkToAgility(e.getPlayer(), 155, Direction.EAST, 4, 4);
	});

	public static ObjectClickHandler handleMossGiantRopeSwings = new ObjectClickHandler(new Object[] { 2322, 2323 }, e -> {
		final Tile toTile = e.getObjectId() == 2322 ? Tile.of(2704, 3209, 0) : Tile.of(2709, 3205, 0);
		final Tile fromTile = e.getObjectId() == 2323 ? Tile.of(2704, 3209, 0) : Tile.of(2709, 3205, 0);
		if (!Agility.hasLevel(e.getPlayer(), 10))
			return;
		if (e.getObjectId() == 2322 ? e.getPlayer().getX() == 2704 : e.getPlayer().getX() == 2709) {
			e.getPlayer().sendMessage("You can't reach that.", true);
			return;
		}
		Agility.swingOnRopeSwing(e.getPlayer(), fromTile, toTile, e.getObject(), 0.1);
	});

	public static ObjectClickHandler handleJogreWaterfallSteppingStones = new ObjectClickHandler(new Object[] { 2333, 2334, 2335 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 30))
			return;
		e.getPlayer().forceMove(e.getObject().getTile(), 741, 0, 30);
	});

	public static ObjectClickHandler handleRareTreeDoors = new ObjectClickHandler(new Object[] { 9038, 9039 }, e -> {
		if (e.getOpNum() == ClientPacket.OBJECT_OP2) {
			if (e.getPlayer().getX() >= e.getObject().getX())
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
			else if (e.getPlayer().getInventory().containsItem(6306, 100)) {
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
				e.getPlayer().getInventory().deleteItem(6306, 100);
			} else
				e.getPlayer().sendMessage("You need 100 trading sticks to use this door.");
		} else if (e.getOpNum() == ClientPacket.OBJECT_OP1)
			if (e.getPlayer().getX() >= e.getObject().getX())
				Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
			else
				e.getPlayer().sendOptionDialogue("Pay 100 trading sticks to enter?", ops -> {
					ops.add("Yes", () -> {
						if (e.getPlayer().getInventory().containsItem(6306, 100)) {
							Doors.handleDoubleDoor(e.getPlayer(), e.getObject());
							e.getPlayer().getInventory().deleteItem(6306, 100);
						} else
							e.getPlayer().sendMessage("You need 100 trading sticks to use this door.");
					});
					ops.add("No");
				});
	});

	public static ObjectClickHandler handleCrandorVolcanoCrater = new ObjectClickHandler(new Object[] { 25154 }, e -> {
		Player p = e.getPlayer();
		if (p.getQuestManager().getStage(Quest.DRAGON_SLAYER) == DragonSlayer.PREPARE_FOR_CRANDOR && !hasHeadAlready(p)) {
			if (!p.getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(DragonSlayer.INTRODUCED_ELVARG_ATTR)) {
				DragonSlayer.introduceElvarg(p);
				return;
			}
		}

		e.getPlayer().tele(Tile.of(2834, 9657, 0));
	});

	public static ObjectClickHandler handleCrandorVolcanoRope = new ObjectClickHandler(new Object[] { 25213 }, e -> e.getPlayer().ladder(Tile.of(2832, 3255, 0)));

	public static ObjectClickHandler handleKaramjaVolcanoRocks = new ObjectClickHandler(new Object[] { 492 }, e -> e.getPlayer().tele(Tile.of(2857, 9569, 0)));

	public static ObjectClickHandler handleKaramjaVolcanoRope = new ObjectClickHandler(new Object[] { 1764 }, e -> e.getPlayer().ladder(Tile.of(2855, 3169, 0)));

	public static ObjectClickHandler handleElvargHiddenWall = new ObjectClickHandler(new Object[] { 2606 }, e -> {
		if (e.getPlayer().isQuestComplete(Quest.DRAGON_SLAYER) || e.getPlayer().getQuestManager().getAttribs(Quest.DRAGON_SLAYER).getB(DragonSlayer.FINISHED_BOAT_SCENE_ATTR)) {
			e.getPlayer().sendMessage("You know from your boat accident there is more behind this wall...");
			Doors.handleDoor(e.getPlayer(), e.getObject());
		} else
			e.getPlayer().sendMessage("You see nothing but a wall...");
	});

	public static ObjectClickHandler handleTzhaarEnter = new ObjectClickHandler(new Object[] { 68134 }, e -> e.getPlayer().tele(Tile.of(4667, 5059, 0)));

	public static ObjectClickHandler handleTzhaarExit = new ObjectClickHandler(new Object[] { 68135 }, e -> e.getPlayer().tele(Tile.of(2845, 3170, 0)));

	public static ObjectClickHandler handleJogreCaveEnter = new ObjectClickHandler(new Object[] { 2584 }, e -> e.getPlayer().startConversation(new Dialogue()
            .addSimple("You search the rocks and find an entrance into some caves.")
            .addOptions((ops) -> {
                ops.add("Yes, I'll enter the cave.")
                        .addSimple("You decide to enter the caves.<br>You climb down several steep rock faces into the cavern below.")
                        .addNext(() -> e.getPlayer().tele(Tile.of(2830, 9522, 0)));
                ops.add("No, thanks.")
                        .addSimple("You decide to stay where you are!");
            })));

	public static ObjectClickHandler handleJogreCaveExit = new ObjectClickHandler(new Object[] { 2585 }, e -> e.getPlayer().startConversation(new Dialogue()
            .addSimple("You climb the rocks to get back out.")
            .addNext(() -> e.getPlayer().ladder(Tile.of(2824, 3120, 0)))));

	public static ObjectClickHandler handleSteppingStone = new ObjectClickHandler(false, new Object[] { 10536 }, e -> {
		if (!Agility.hasLevel(e.getPlayer(), 77))
			return;
		Player p = e.getPlayer();
		WorldObject obj = e.getObject();
		Direction dir = Direction.NORTH;
		if(!obj.getTile().matches(Tile.of(2860, 2974, 0)))
			return;
		if(p.getY() > obj.getY())
			dir = Direction.SOUTH;

		final Direction direction = dir;
		p.lock();
		p.setRouteEvent(new RouteEvent(direction == Direction.NORTH ? Tile.of(2860, 2971, 0) : Tile.of(2860, 2977, 0), () -> p.forceMove(Tile.of(2860, 2974, 0), 741, 0, 30, false, () -> p.forceMove(direction == Direction.NORTH ? Tile.of(2860, 2977, 0) : Tile.of(2860, 2971, 0), 741, 0, 30))));
	});

	public static ObjectClickHandler handleShiloEnter = new ObjectClickHandler(new Object[] { 2216 }, e -> {
		e.getPlayer().sendMessage("You quickly climb over the cart.");
		e.getPlayer().ladder(e.getPlayer().getX() > e.getObject().getX() ? e.getPlayer().transform(-4, 0, 0) : e.getPlayer().transform(4, 0, 0));
	});

	public static ObjectClickHandler handleShiloCartEnter = new ObjectClickHandler(new Object[] { 2230 }, e -> e.getPlayer().tele(Tile.of(2833, 2954, 0)));

	public static ObjectClickHandler handleShiloCartExit = new ObjectClickHandler(new Object[] { 2265 }, e -> e.getPlayer().tele(Tile.of(2778, 3210, 0)));

	public static ObjectClickHandler handleElvargEntrance = new ObjectClickHandler(new Object[] { 25161 }, e -> {
		Player p = e.getPlayer();
		int dragonSlayerStage = p.getQuestManager().getStage(Quest.DRAGON_SLAYER);
		boolean hasHeadAlready = hasHeadAlready(p);

		if (((dragonSlayerStage >= PREPARE_FOR_CRANDOR && dragonSlayerStage <= REPORT_TO_OZIACH && !hasHeadAlready) || p.getX() == 2847) ||
				(dragonSlayerStage == REPORT_TO_OZIACH && p.getX() == 2847)) {
		WorldTasks.scheduleLooping(new Task() {
				int ticks = 0;
				boolean goingEast = true;

				@Override
				public void run() {
					if (ticks == 0) {
						if (p.getX() == 2845) {
							p.setFaceAngle(Direction.getAngleTo(Direction.EAST));
							p.setNextAnimation(new Animation(839));
							goingEast = true;
						} else if (p.getX() == 2847) {
							p.setFaceAngle(Direction.getAngleTo(Direction.WEST));
							p.setNextAnimation(new Animation(839));
							goingEast = false;
						} else return;
					} else if (ticks >= 1) {
						if (goingEast) p.tele(Tile.of(2847, p.getY(), 0));
						if (!goingEast) p.tele(Tile.of(2845, p.getY(), 0));
						stop();
					}
					ticks++;
				}
			}, 0, 1);
		} else {
			String message = (dragonSlayerStage < PREPARE_FOR_CRANDOR) ? "I shouldn't need to go in there." : "I shouldn't need to go back in there.";
			p.sendMessage(message);
		}
	});

	public static ObjectClickHandler handleHerbloreHabitatVines = new ObjectClickHandler(new Object[] { 56805 }, e -> {
		switch (e.getObject().getRotation()) {
			case 3,4 -> Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(e.getPlayer().getX() < e.getObject().getX() ? 2 : -2, 0, 0), 0);
			default -> Agility.handleObstacle(e.getPlayer(), 3303, 1, e.getPlayer().transform(0, e.getPlayer().getY() < e.getObject().getY() ? 2 : -2, 0), 0);
		}
	});

	public static ObjectClickHandler handleVineHerbEntrance = new ObjectClickHandler(new Object[] { 27126 }, new Tile[] { Tile.of(2963, 2904, 0) }, e -> e.getPlayer().tele(Tile.of(e.getPlayer().getX() + 12, e.getPlayer().getY() + 2, e.getPlayer().getPlane())));

	public static ObjectClickHandler handleVineHerbExit = new ObjectClickHandler(new Object[] { 27126 }, new Tile[] { Tile.of(2977, 2906, 0) }, e -> e.getPlayer().tele(Tile.of(e.getPlayer().getX() - 12, e.getPlayer().getY() - 2, e.getPlayer().getPlane())));

	public static ObjectClickHandler handleJadinkoLairEntrance = new ObjectClickHandler(new Object[] { 12328 }, e -> e.getPlayer().tele(Tile.of(3011, 9276, 0)));

	public static ObjectClickHandler handleJadinkoLairExit = new ObjectClickHandler(new Object[] { 12327 }, e -> e.getPlayer().tele(Tile.of(2948, 2955, 0)));

	public static ObjectClickHandler handleKhazariJungleTrees = new ObjectClickHandler(new Object[] { 2890, 2892, 2893 }, e -> {
		Player player = e.getPlayer();
		GameObject object = e.getObject();

		if (player.getEquipment().getWeaponId() != 975 && !player.getInventory().containsItem(975, 1) &&
			player.getEquipment().getWeaponId() != 6313 && !player.getInventory().containsItem(6313, 1) &&
			player.getEquipment().getWeaponId() != 6315 && !player.getInventory().containsItem(6315, 1) &&
			player.getEquipment().getWeaponId() != 6317 && !player.getInventory().containsItem(6317, 1)) {
			player.sendMessage("You need a machete in order to cut through the terrain.");
			return;
		}

		player.anim(910);
		WorldTasks.schedule(() -> {
			if (Utils.random(3) == 0) {
				player.sendMessage("You fail to slash through the terrain.");
				return;
			}
			GameObject newObject = new GameObject(object);
			newObject.setId(e.getObjectId() + 1);
			World.spawnObjectTemporary(newObject, 8);
			player.addWalkSteps(object.getX(), object.getY(), 0, false);
		});
	});

	public static ObjectClickHandler handleRocksToCairnIsle = new ObjectClickHandler(new Object[] { 2231 }, e -> {
		e.getPlayer().useStairs(-1, Tile.of(e.getObject().getX() == 2792 ? 2795 : 2791, 2979, 0), 1, 2, e.getObject().getX() == 2792 ? "You climb down the slope." : "You climb up the slope.");
	});

}
