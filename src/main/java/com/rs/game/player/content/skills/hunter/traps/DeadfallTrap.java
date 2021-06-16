package com.rs.game.player.content.skills.hunter.traps;

import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.BoxTrapType;

public class DeadfallTrap extends BoxStyleTrap {

	public DeadfallTrap(Player player, GameObject orig) {
		super(player, BoxTrapType.DEAD_FALL, orig);
		this.setRotation(orig.getRotation());
		this.routeType = RouteType.NORMAL;
	}
}
