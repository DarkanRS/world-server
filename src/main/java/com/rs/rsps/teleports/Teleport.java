package com.rs.rsps.teleports;

import com.rs.game.player.Player;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;

import java.util.function.Consumer;

public class Teleport {
	
	private String name;
	private WorldTile tile;
	private Consumer<Player> onTeleport;
	
	public Teleport(String name, WorldTile tile, Consumer<Player> onTeleport) {
		this.name = name;
		this.tile = tile;
		this.onTeleport = onTeleport;
	}
	
	public Teleport(String name, WorldTile tile) {
		this(name, tile, null);
	}
	
	public String getName() {
		return name;
	}

	public WorldTile getTile() {
		return tile;
	}
	
	public void teleport(Player player) {
		if (Magic.sendNormalTeleportSpell(player, tile) && onTeleport != null)
			WorldTasks.delay(5, () -> onTeleport.accept(player));
	}

}
