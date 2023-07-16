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
package com.rs.game.content.holidayevents.easter.easter21;

import com.rs.engine.dialogue.Conversation;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.plugin.handlers.*;
import com.rs.utils.spawns.NPCSpawn;
import com.rs.utils.spawns.NPCSpawns;
import com.rs.utils.spawns.ObjectSpawn;
import com.rs.utils.spawns.ObjectSpawns;

@PluginEventHandler
public class Easter2021 {

	public static String STAGE_KEY = "easter2021";

	public static final boolean ENABLED = false;

	@ServerStartupEvent(Priority.FILE_IO)
	public static void loadSpawns() {
		if (!ENABLED)
			return;
		ObjectSpawns.add(new ObjectSpawn(23117, 10, 0, Tile.of(3210, 3424, 0), "Rabbit hole"));
		NPCSpawns.add(new NPCSpawn(9687, Tile.of(3212, 3425, 0), "Easter Bunny"));
		NPCSpawns.add(new NPCSpawn(9687, Tile.of(2463, 5355, 0), "Easter Bunny"));
		NPCSpawns.add(new NPCSpawn(7411, Tile.of(2448, 5357, 0), "Easter Bunny Jr").setCustomName("Easter Bunny Jr (Trent with Easter 2021 Event)"));
		NPCSpawns.add(new NPCSpawn(9686, Tile.of(2969, 3431, 0), "Charlie the Squirrel"));
		NPCSpawns.add(new NPCSpawn(3283, Tile.of(2968, 3429, 0), "Squirrel"));
		NPCSpawns.add(new NPCSpawn(3284, Tile.of(2970, 3429, 0), "Squirrel"));
		NPCSpawns.add(new NPCSpawn(3285, Tile.of(2969, 3428, 0), "Squirrel"));
		NPCSpawns.add(new NPCSpawn(3285, Tile.of(2968, 3432, 0), "Squirrel"));
	}

	public static ObjectClickHandler handleEnterExit = new ObjectClickHandler(new Object[] { 23117, 30074 }, e -> {
		if (e.getPlayer().getI(Easter2021.STAGE_KEY) <= 0) {
			e.getPlayer().sendMessage("You don't see a need to go down there yet!");
			return;
		}
		e.getPlayer().useLadder(e.getObjectId() == 23117 ? Tile.of(2483, 5258, 0) : Tile.of(3212, 3425, 0));
	});

	public static ObjectClickHandler handleRabbitTunnel = new ObjectClickHandler(new Object[] { 30075, 30076 }, e -> useBunnyHole(e.getPlayer(), e.getObject(), e.getPlayer().transform(0, e.getObjectId() == 30075 ? 7 : -7)));

	public static ItemClickHandler handleChocCapeEmote = new ItemClickHandler(new Object[] { 12645 }, new String[] { "Emote" }, e -> {
		e.getPlayer().setNextAnimation(new Animation(8903));
		e.getPlayer().setNextSpotAnim(new SpotAnim(1566));
	});

	public static ItemEquipHandler handleEggBasket = new ItemEquipHandler(new Object[] { 4565 }, e -> {
		if (e.dequip())
			e.getPlayer().getAppearance().setBAS(-1);
		else
			e.getPlayer().getAppearance().setBAS(594);
	});

	public static final int COG = 14719;
	public static final int PISTON = 14720;
	public static final int CHIMNEY = 14718;

	private static final Tile[] COG_LOCATIONS = {
			Tile.of(2469, 5328, 0),
			Tile.of(2469, 5321, 0),
			Tile.of(2454, 5334, 0),
			Tile.of(2448, 5341, 0)
	};

	private static final Tile[] PISTON_LOCATIONS = {
			Tile.of(2468, 5324, 0),
			Tile.of(2467, 5319, 0),
			Tile.of(2454, 5335, 0)
	};

	private static final Tile[] CHIMNEY_LOCATIONS = {
			Tile.of(2469, 5323, 0),
			Tile.of(2444, 5329, 0),
			Tile.of(2449, 5343, 0)
	};

	public static LoginHandler loginEaster = new LoginHandler(e -> {
		if (!ENABLED)
			return;
		e.getPlayer().getNSV().setI("easterBirdFood", Utils.random(4));
		e.getPlayer().getNSV().setI("cogLocation", Utils.random(COG_LOCATIONS.length));
		e.getPlayer().getNSV().setI("pistonLocation", Utils.random(PISTON_LOCATIONS.length));
		e.getPlayer().getNSV().setI("chimneyLocation", Utils.random(CHIMNEY_LOCATIONS.length));
		e.getPlayer().getVars().setVarBit(6014, e.getPlayer().getI(Easter2021.STAGE_KEY) >= 3 ? 1 : 0);
		e.getPlayer().getVars().setVarBit(6016, e.getPlayer().getI(Easter2021.STAGE_KEY) >= 6 ? 3: 0);
		if (e.getPlayer().getI(Easter2021.STAGE_KEY) >= 8)
			e.getPlayer().getVars().setVarBit(6014, 85);
	});

	public static ObjectClickHandler handleWaterGrab = new ObjectClickHandler(new Object[] { 30083 }, e -> e.getPlayer().getInventory().addItem(1929));

	public static ObjectClickHandler handleBirdFoods = new ObjectClickHandler(new Object[] { 30089, 30090, 30091, 30092 }, e -> {
		if (!e.getPlayer().getInventory().hasFreeSlots()) {
			e.getPlayer().sendMessage("You don't have enough inventory space.");
			return;
		}
		Item food = new Item(14714 + (e.getObjectId() - 30089));
		e.getPlayer().sendMessage("You grab some " + food.getName() + ".");
		e.getPlayer().getInventory().addItem(food);
	});

	public static ItemOnObjectHandler handleWaterIntoBirdDish = new ItemOnObjectHandler(new Object[] { 42731 }, e -> {
		if (e.getItem().getId() != 1929) {
			e.getPlayer().sendMessage("Nothing interesting happens.");
			return;
		}
		if (e.getPlayer().getI(Easter2021.STAGE_KEY) >= 3) {
			e.getPlayer().sendMessage("You've already woken the bird up! It doesn't need any more water.");
			return;
		}
		e.getPlayer().getInventory().deleteItem(1929, 1);
		e.getPlayer().getInventory().addItem(1925, 1);
		e.getPlayer().getVars().setVarBit(6027, 1);
		e.getPlayer().sendMessage("You fill the bird's dish with water.");
		if (e.getPlayer().getVars().getVarBit(6026) == e.getPlayer().getNSV().getI("easterBirdFood")) {
			e.getPlayer().sendMessage("The bird wakes up and begins eating and drinking!");
			e.getPlayer().save(Easter2021.STAGE_KEY, 3);
			e.getPlayer().getVars().setVarBit(6014, 1);
			WorldTasks.delay(10, () -> {
				e.getPlayer().getVars().setVarBit(6026, 0);
				e.getPlayer().getVars().setVarBit(6027, 0);
			});
		} else
			e.getPlayer().sendMessage("The bird still needs the correct food it seems.");
	});

	public static ItemOnObjectHandler handleFoodIntoBirdDish = new ItemOnObjectHandler(new Object[] { 42732 }, e -> {
		if (e.getItem().getId() < 14714 || e.getItem().getId() > 14717) {
			e.getPlayer().sendMessage("Nothing interesting happens.");
			return;
		}
		if (e.getPlayer().getI(Easter2021.STAGE_KEY) >= 3) {
			e.getPlayer().sendMessage("You've already woken the bird up! It doesn't need any more food.");
			return;
		}
		int foodId = e.getItem().getId() - 14713;
		e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
		e.getPlayer().getVars().setVarBit(6026, foodId);
		e.getPlayer().sendMessage("You fill the bird's dish with " + e.getItem().getName() + ".");
		if (e.getPlayer().getVars().getVarBit(6027) == 0) {
			e.getPlayer().sendMessage("The bird still looks thirsty.");
			return;
		}
		if (e.getPlayer().getVars().getVarBit(6026) == (e.getPlayer().getNSV().getI("easterBirdFood")+1)) {
			e.getPlayer().save(Easter2021.STAGE_KEY, 3);
			e.getPlayer().getVars().setVarBit(6014, 1);
			WorldTasks.delay(10, () -> {
				e.getPlayer().getVars().setVarBit(6026, 0);
				e.getPlayer().getVars().setVarBit(6027, 0);
			});
		} else
			e.getPlayer().sendMessage("That doesn't seem to be the correct food." + e.getPlayer().getNSV().getI("easterBirdFood"));
	});

	public static ObjectClickHandler handleCrates = new ObjectClickHandler(new Object[] { 30100, 30101, 30102, 30103, 30104 }, e -> {
		if (e.getPlayer().getI(Easter2021.STAGE_KEY) < 5) {
			e.getPlayer().sendMessage("You don't find anything that looks useful to you right now.");
			return;
		}
		if (COG_LOCATIONS[e.getPlayer().getNSV().getI("cogLocation")].matches(e.getObject().getTile()) && !e.getPlayer().getInventory().containsItem(COG)) {
			e.getPlayer().getInventory().addItem(COG);
			e.getPlayer().startConversation(new Conversation(e.getPlayer()).addItem(COG, "You find a cog in the crate!"));
			return;
		}
		if (PISTON_LOCATIONS[e.getPlayer().getNSV().getI("pistonLocation")].matches(e.getObject().getTile()) && !e.getPlayer().getInventory().containsItem(PISTON)) {
			e.getPlayer().getInventory().addItem(PISTON);
			e.getPlayer().startConversation(new Conversation(e.getPlayer()).addItem(PISTON, "You find some pistons in the crate!"));
			return;
		}
		if (CHIMNEY_LOCATIONS[e.getPlayer().getNSV().getI("chimneyLocation")].matches(e.getObject().getTile()) && !e.getPlayer().getInventory().containsItem(CHIMNEY)) {
			e.getPlayer().getInventory().addItem(CHIMNEY);
			e.getPlayer().startConversation(new Conversation(e.getPlayer()).addItem(CHIMNEY, "You find a chimney in the crate!"));
			return;
		}
		e.getPlayer().sendMessage("You find nothing interesting.");
	});

	public static ItemOnObjectHandler handleFixIncubator = new ItemOnObjectHandler(new Object[] { 42733 }, e -> {
		if (e.getPlayer().getI(Easter2021.STAGE_KEY) < 5) {
			e.getPlayer().sendMessage("It looks really broken.");
			return;
		}
		switch(e.getItem().getId()) {
		case COG:
			if (e.getPlayer().getVars().getVarBit(6016) <= 0) {
				e.getPlayer().getInventory().deleteItem(e.getItem());
				e.getPlayer().getVars().setVarBit(6016, 1);
				e.getPlayer().sendMessage("You attach the cog back into place.");
			} else
				e.getPlayer().sendMessage("You already have attached the cog.");
			break;
		case PISTON:
			if (e.getPlayer().getVars().getVarBit(6016) == 1) {
				e.getPlayer().getInventory().deleteItem(e.getItem());
				e.getPlayer().getVars().setVarBit(6016, 2);
				e.getPlayer().sendMessage("You attach the pistons back into place.");
			} else
				e.getPlayer().sendMessage("That part won't fit quite yet.");
			break;
		case CHIMNEY:
			if (e.getPlayer().getVars().getVarBit(6016) == 2) {
				e.getPlayer().getInventory().deleteItem(e.getItem());
				e.getPlayer().getVars().setVarBit(6016, 4);
				e.getPlayer().sendMessage("You attach the chimney back into place.");
				e.getPlayer().sendMessage("You hear the machine whirr as it turns back on.");
				e.getPlayer().save(Easter2021.STAGE_KEY, 6);
			} else
				e.getPlayer().sendMessage("That part won't fit quite yet.");
			break;
		default:
			break;
		}
	});

	public static void useBunnyHole(Player player, GameObject object, Tile toTile) {
		player.lock();
		player.faceObject(object);
		WorldTasks.delay(1, () -> {
			player.setNextAnimation(new Animation(8901));
			player.setNextSpotAnim(new SpotAnim(1567));
		});
		WorldTasks.delay(13, () -> {
			player.setNextTile(toTile);
			player.setNextAnimation(new Animation(8902));
		});
		WorldTasks.delay(22, () -> {
			player.setNextAnimation(new Animation(-1));
			player.unlock();
		});
	}
}
