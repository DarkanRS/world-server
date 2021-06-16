package com.rs.game.npc.others;

import com.rs.cores.CoresManager;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class DoorSupport extends NPC {

	public DoorSupport(int id, WorldTile tile) {
		super(id, tile, true);
		setCantFollowUnderCombat(true);
	}

	@Override
	public void processNPC() {
		cancelFaceEntityNoCheck();
	}

	public boolean canDestroy(Player player) {
		if (getId() == 2446)
			return player.getY() < getY();
		if (getId() == 2440)
			return player.getY() > getY();
		return player.getX() > getX();
	}
	
	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public void sendDeath(Entity killer) {
		setNextNPCTransformation(getId() + 1);
		final GameObject door = World.getObjectWithId(this, 8967);
		if (door != null)
			World.removeObject(door);
		CoresManager.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					setNextNPCTransformation(getId() - 1);
					reset();
					if (door != null)
						World.spawnObject(door);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, Ticks.fromSeconds(60));
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(2440, 2443, 2446) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new DoorSupport(npcId, tile);
		}
	};
}
