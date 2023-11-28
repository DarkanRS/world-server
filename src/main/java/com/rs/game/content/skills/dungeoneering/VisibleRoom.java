// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.content.skills.dungeoneering;

import com.rs.game.content.skills.dungeoneering.rooms.BossRoom;
import com.rs.game.content.skills.dungeoneering.rooms.HandledRoom;
import com.rs.game.content.skills.dungeoneering.rooms.StartRoom;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;

import java.util.ArrayList;

public class VisibleRoom {

	private int[] musicId;
	private ArrayList<NPC> guardians;
	private int guardianCount;
	private boolean noMusic;
	private boolean loaded;
	protected RoomReference reference;
	protected DungeonManager manager;
	protected int type;

	// TODO: Remove this for a constructor, to satisfy synchronizing non-final field `manager`
	public void init(DungeonManager manager, RoomReference ref, int type, HandledRoom room) {
		this.type = type;
		reference = ref;
		this.manager = manager;
		if (room instanceof StartRoom)
			musicId = new int[]
					{ DungeonConstants.START_ROOM_MUSICS[type] };
		else if (room instanceof BossRoom)
			musicId = new int[]
					{ ((BossRoom) room).getMusicId() };
		else {
			musicId = new int[]
					{ DungeonUtils.getSafeMusic(type), DungeonUtils.getDangerousMusic(type) };
			guardians = new ArrayList<>();
		}
	}

	public int getMusicId() {
		return noMusic ? -2 : musicId[roomCleared() ? 0 : 1];
	}

	public boolean roomCleared() {
		if (guardians == null)
			return true;
		for (NPC n : guardians)
			if (!n.hasFinished() && !n.isDead())
				return false;
		return true;
	}

	public void addGuardian(NPC n) {
		guardians.add(n);
		guardianCount++;
	}

	public boolean removeGuardians() {
		if (roomCleared()) {
			guardians = null;
			return true;
		}
		return false;
	}

	public void forceRemoveGuardians() {
		if (guardians != null) {
			for (NPC n : guardians)
				n.finish();
			guardians.clear();
		}
	}

	public int getGuardiansCount() {
		return guardianCount;
	}

	public int getKilledGuardiansCount() {
		return guardians == null ? guardianCount : (guardianCount - guardians.size());
	}

	public void setNoMusic() {
		noMusic = true;
	}

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded() {
		loaded = true;
	}

	public void destroy() {

	}

	public void openRoom() {

	}

	public boolean processObjectClick1(Player p, GameObject object) {
		return true;
	}

	public boolean processObjectClick2(Player p, GameObject object) {
		return true;
	}

	public boolean processObjectClick3(Player p, GameObject object) {
		return true;
	}

	public boolean processObjectClick4(Player p, GameObject object) {
		return true;
	}

	public boolean processObjectClick5(Player p, GameObject object) {
		return true;
	}

	public boolean handleItemOnObject(Player player, GameObject object, Item item) {
		return true;
	}

	public boolean processNPCClick1(Player player, NPC npc) {
		return true;
	}

	public boolean processNPCClick2(Player player, NPC npc) {
		return true;
	}

	public boolean canMove(Player player, Tile to) {
		return true;
	}

	public boolean processNPCClick3(Player player, NPC npc) {
		return true;
	}

}
