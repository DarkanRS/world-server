package com.rs.game.content.quests.dragonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.World;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCDeathHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

import static com.rs.game.content.quests.dragonslayer.DragonSlayer.*;
import static com.rs.game.content.world.doors.Doors.handleDoor;

@PluginEventHandler
public class MelzarsMaze {
	public final static int FRONT_DOOR = 2595;

	public static ObjectClickHandler handleMelzarsMazeLadders = new ObjectClickHandler(new Object[] { 1754, 32015, 25038, 25214, 1752, 2605 }, e -> {
		GameObject obj = e.getObject();
		if(obj.getTile().matches(Tile.of(2928, 3258, 0)) || obj.getTile().matches(Tile.of(2925, 3258, 0)) || obj.getTile().matches(Tile.of(2930, 3258, 1)) || obj.getTile().matches(Tile.of(2937, 3247, 1)))
			return;
		if(obj.getTile().matches(Tile.of(2934, 3243, 1)) || obj.getTile().matches(Tile.of(2929, 3245, 1)) || obj.getTile().matches(Tile.of(2940, 3240, 1)) || obj.getTile().matches(Tile.of(2937, 3240, 0)))
			return;


		//Exits
		if(obj.getTile().matches(Tile.of(2924, 9650, 0)))//Melzar Mad basement exit ladder to first floor
			e.getPlayer().useLadder(Tile.of(2923, 3250, 0));
		if(obj.getTile().matches(Tile.of(2924, 3250, 0)))//1st floor to Melzar mad exit
			e.getPlayer().useLadder(Tile.of(2924, 9649, 0));
		if(obj.getTile().matches(Tile.of(2939, 3257, 0)))//Front exit to Lesser demon exit ladder
			e.getPlayer().useLadder(Tile.of(2939, 9656, 0));
		if(obj.getTile().matches(Tile.of(2939, 9657, 0)))//Lesser demon exit to front exit
			e.getPlayer().useLadder(Tile.of(2938, 3257, 0));
		if(obj.getTile().matches(Tile.of(2928, 9658, 0)))//Basement exit out
			e.getPlayer().useLadder(Tile.of(2928, 3259, 0));
		if(obj.getTile().matches(Tile.of(2928, 9658, 0)))//Basement exit out
			e.getPlayer().useLadder(Tile.of(2928, 3259, 0));
		if(obj.getTile().matches(Tile.of(2932, 3240, 0)))//Basement exit out
			e.getPlayer().useLadder(Tile.of(2932, 9641, 0));
	});

	public static ObjectClickHandler handleFrontDoor = new ObjectClickHandler(new Object[] { FRONT_DOOR }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(p.getInventory().containsItem(new Item(MELZAR_MAZE_KEY, 1)))
			handleDoor(p, obj);
		else
			p.startConversation(new Conversation(e.getPlayer()) {
				{
					addPlayer(HeadE.CALM_TALK, "The door seems to need a key...");
					create();
				}
			});
	});

	public static ObjectClickHandler handleExitDoors = new ObjectClickHandler(new Object[] { 2602, 32968 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(p.getX() >= obj.getX())
			if(p.getX() >= obj.getX()) {
				handleDoor(p, obj);
				return;
			}
		p.startConversation(new Conversation(e.getPlayer()) {
			{
				addPlayer(HeadE.CALM_TALK, "The door seems to be locked from the other side...");
				create();
			}
		});
	});

	public static ObjectClickHandler handleColoredDoors = new ObjectClickHandler(new Object[] { 2596,2597,2598,2599,2600,2601 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(obj.getId() == 2596)
			if(p.getInventory().containsItem(RED_KEY)) {
				handleDoor(p, obj);
				return;
			}
		if(obj.getId() == 2597)
			if(p.getInventory().containsItem(ORANGE_KEY)) {
				handleDoor(p, obj);
				return;
			}
		if(obj.getId() == 2598)
			if(p.getInventory().containsItem(YELLOW_KEY)) {
				handleDoor(p, obj);
				return;
			}
		if(obj.getId() == 2599)
			if(p.getInventory().containsItem(BLUE_KEY)) {
				handleDoor(p, obj);
				return;
			}
		if(obj.getId() == 2600)
			if(p.getInventory().containsItem(MAGENTA_KEY)) {
				handleDoor(p, obj);
				return;
			}
		if(obj.getId() == 2601)
			if(p.getInventory().containsItem(GREEN_KEY)) {
				handleDoor(p, obj);
				return;
			}

		p.startConversation(new Conversation(e.getPlayer()) {
			{
				addPlayer(HeadE.CALM_TALK, "The door seems to need a key...");
				create();
			}
		});
	});

	static final int MELZAR_BASEMENT_REGION = 11670;

	public static NPCDeathHandler handleBlueKeyDrop = new NPCDeathHandler(ZOMBIE_BLUE_KEY, e -> {
		if(e.getNPC().getRegionId() == MELZAR_BASEMENT_REGION && e.killedByPlayer())
			World.addGroundItem(new Item(BLUE_KEY, 1), Tile.of(e.getNPC().getTile()), (Player)e.getKiller());
	});

	public static NPCDeathHandler handleGreenKeyDrop = new NPCDeathHandler(LESSER_DEMON_GREEN_KEY, e -> {
		if(e.getNPC().getRegionId() == MELZAR_BASEMENT_REGION && e.killedByPlayer())
			World.addGroundItem(new Item(GREEN_KEY, 1), Tile.of(e.getNPC().getTile()), (Player)e.getKiller());
	});

	public static NPCDeathHandler handleMagentaKeyDrop = new NPCDeathHandler(MELZAR_THE_MAD_MEGENTA_KEY, e -> {
		if(e.getNPC().getRegionId() == MELZAR_BASEMENT_REGION && e.killedByPlayer())
			World.addGroundItem(new Item(MAGENTA_KEY, 1), Tile.of(e.getNPC().getTile()), (Player)e.getKiller());
	});

	public static ObjectClickHandler handleMapPieceChest = new ObjectClickHandler(new Object[] { 2603, 2604 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if(e.getOption().equalsIgnoreCase("open")) {
			p.setNextAnimation(new Animation(536));
			p.lock(2);
			GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
			p.faceObject(openedChest);
			World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
		}
		if(e.getOption().equalsIgnoreCase("search"))
			if(p.getInventory().containsItem(MAP_PART1))
				p.sendMessage("The chest is empty");
			else if(p.getBank().containsItem(MAP_PART1, 1)) {
				p.startConversation(new Conversation(e.getPlayer()) {
					{
						addPlayer(HeadE.HAPPY_TALKING, "Oh that's right, the map piece is in my bank.");
						create();
					}
				});
				p.sendMessage("The chest is empty");
			} else
				p.getInventory().addItem(MAP_PART1, 1);
	});



}
