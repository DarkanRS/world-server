package com.rs.game.content.minigames.pyramidplunder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class OuterPyramidHandler {//OuterPyramidHandler plunder is all in one region.
	static WorldTile[] MUMMY_LOCATIONS = {WorldTile.of(1934, 4458, 2), WorldTile.of(1968, 4428, 2),
			WorldTile.of(1934, 4428, 3), WorldTile.of(1968, 4458, 3)};
	static Set<Integer> MUMMY_CHUNKS = new HashSet<>(Arrays.asList(3952992, 4034880, 3952960, 4034912));
	static int MUMMY_ROOM = 0;
	@ServerStartupEvent
	public static void init() {
		WorldTasks.schedule(0, 600, () -> {
			MUMMY_ROOM = Utils.randomInclusive(0, 3);
		});
	}

	public static ObjectClickHandler handleOuterPyramidDoors = new ObjectClickHandler(new Object[] { 16543, 16544, 16545, 16546 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			WorldTile[] tiles = MUMMY_LOCATIONS;
			if(e.getObjectId() == 16543)
				enterMummyRoom(p, tiles[0]);
			else if(e.getObjectId() == 16544)
				enterMummyRoom(p, tiles[1]);
			else if(e.getObjectId() == 16545)
				enterMummyRoom(p, tiles[2]);
			else if(e.getObjectId() == 16546)
				enterMummyRoom(p, tiles[3]);
		}
	};

	private static void enterMummyRoom(Player p, WorldTile tile) {
		p.lock(5);
		WorldTasks.schedule(new WorldTask() {
			int tick;
			@Override
			public void run() {
				if(tick == 0)
					p.getInterfaceManager().setFadingInterface(115);
				if(tick == 2) {
					p.faceNorth();
					p.setNextWorldTile(WorldTile.of(tile.getX(), tile.getY() - 8, tile.getPlane()));
				}
				if(tick == 3)
					p.getInterfaceManager().setFadingInterface(170);
				tick++;
			}
		}, 0, 1);
	}

	public static ObjectClickHandler handlePyramidExits = new ObjectClickHandler(new Object[] { 16459 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			WorldTile[] tiles = MUMMY_LOCATIONS;
			if(p.withinDistance(tiles[0]))
				exitMummyRoom(p, WorldTile.of(3288, 2801, 0), 0);
			else if(p.withinDistance(tiles[1]))
				exitMummyRoom(p, WorldTile.of(3295, 2795, 0), 1);
			else if(p.withinDistance(tiles[2]))
				exitMummyRoom(p, WorldTile.of(3289, 2788, 0), 2);
			else if(p.withinDistance(tiles[3]))
				exitMummyRoom(p, WorldTile.of(3282, 2794, 0), 3);

		}
	};

	private static void exitMummyRoom(Player p, WorldTile tile, int dir) {
		p.lock(4);
		WorldTasks.schedule(new WorldTask() {
			int tick;
			@Override
			public void run() {
				if(tick == 0)
					p.getInterfaceManager().setFadingInterface(115);
				if(tick == 2) {
					if(dir ==0)
						p.faceNorth();
					if(dir ==1)
						p.faceEast();
					if(dir ==2)
						p.faceSouth();
					if(dir ==3)
						p.faceWest();
					p.setNextWorldTile(tile);
				}
				if(tick == 3)
					p.getInterfaceManager().setFadingInterface(170);
				tick++;
			}
		}, 0, 1);
	}

	public static EnterChunkHandler handleMummySpawn = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			Player p = e.getPlayer();
			if(MUMMY_CHUNKS.contains(e.getChunkId())) {
				if(p == null)
					return;
				moveMummy(p);
			}
		}
	};

	private static void moveMummy(Player p) {
		for(NPC npc : World.getNPCsInRegion(p.getRegionId()))//If mummy is correct dont do anything
			if(npc.getId() == 4476)
				if(npc.withinDistance(MUMMY_LOCATIONS[MUMMY_ROOM], 5))
					return;
		for(NPC npc : World.getNPCsInRegion(p.getRegionId()))//else finish
			if(npc.getId() == 4476)
				npc.setNextWorldTile(MUMMY_LOCATIONS[MUMMY_ROOM]);
	}
}
