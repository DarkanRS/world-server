package com.rs.game.player.content.skills.dungeoneering.rooms;

import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;

public final class StartRoom extends HandledRoom {

	public StartRoom(int chunkX, int chunkY, int... doorsDirections) {
		super(chunkX, chunkY, new RoomEvent() {
			@Override
			public void openRoom(DungeonManager dungeon, RoomReference reference) {
				dungeon.telePartyToRoom(reference);
				dungeon.spawnNPC(reference, DungeonConstants.SMUGGLER, 8, 8); // smoother
				dungeon.setTableItems(reference);
				dungeon.linkPartyToDungeon();

			}
		}, new int[] {7, 7}, doorsDirections);

	}

	@Override
	public boolean allowResources() {
		return false;
	}

}
