package com.rs.game.player.content.skills.hunter.traps;

import com.rs.game.World;
import com.rs.game.npc.others.BoxHunterNPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.BoxTrapType;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;

public class MarasamawPlant extends BoxStyleTrap {

	public MarasamawPlant(Player player, WorldTile tile) {
		super(player, BoxTrapType.MARASAMAW_PLANT, tile);
	}

	@Override
	public void handleCatch(BoxHunterNPC npc, boolean success) {
		if (success) {
			setId(npc.getType().getObjectCatch());
			npc.setNextAnimation(new Animation(-1));
			npc.setRespawnTask();
		} else
			setId(npc.getType().getObjectFail());
		setStatus(Status.CATCHING);
		if (success) {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					World.sendObjectAnimation(getOwner(), MarasamawPlant.this, new Animation(3300));
				}
			}, 0);
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (success)
					setId(npc.getType().getObjectSuccess());
				setNpcTrapped(npc.getType());
				setStatus(success ? Status.SUCCESS : Status.FAIL);
			}
		}, 3);
	}
}
