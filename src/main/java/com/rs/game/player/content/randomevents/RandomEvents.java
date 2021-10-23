package com.rs.game.player.content.randomevents;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class RandomEvents {
	
	private static List<WorldTile> RANDOM_TILES = new ArrayList<>();
	static {
		RANDOM_TILES.add(new WorldTile(3208, 3219, 3));
		RANDOM_TILES.add(new WorldTile(2707, 3472, 1));
		RANDOM_TILES.add(new WorldTile(2995, 3341, 3));
		RANDOM_TILES.add(new WorldTile(3217, 3475, 1));
		RANDOM_TILES.add(new WorldTile(3083, 3415, 0));
	}
	
	public static WorldTile getRandomTile() {
		return RANDOM_TILES.get(Utils.random(RANDOM_TILES.size()));
	}
	
	public static void attemptSpawnRandom(Player player) {
		attemptSpawnRandom(player, false);
	}
	
	public static void attemptSpawnRandom(Player player, boolean force) {
		if (!force && (World.getServerTicks() - player.getNSV().getL("lastRandom") < 3000)) //15 minutes limit on random events
			return;
		if (player.getControllerManager().getController() != null)
			return;
		player.getNSV().setL("lastRandom", World.getServerTicks());

        int random = Utils.random(0, 100);
		if(random < 90)//90% chance
            new SandwichLady(player);
        else
            new Genie(player);
		//TODO add more than this rofl.
	}

}
