package com.rs.game.npc.dungeoneering;

import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public final class UnholyCrossbearer extends DungeonBoss {

	public UnholyCrossbearer(int id, WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10111, 10127), (int)Math.ceil((double)manager.getBossLevel() - manager.getBossLevel()*0.15)), tile, manager, reference);
	}

}
