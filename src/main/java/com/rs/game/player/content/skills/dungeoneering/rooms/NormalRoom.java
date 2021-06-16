package com.rs.game.player.content.skills.dungeoneering.rooms;

public final class NormalRoom extends HandledRoom {

	@Deprecated
	public NormalRoom(int chunkX, int chunkY, int... doorsDirections) {
		this(chunkX, chunkY, null, doorsDirections);
	}
	
	public NormalRoom(int chunkX, int chunkY, int[] keyspot, int... doorsDirections) {
		super(chunkX, chunkY, new SpawnRandomNpcsEvent(), keyspot, doorsDirections);
	}
}
