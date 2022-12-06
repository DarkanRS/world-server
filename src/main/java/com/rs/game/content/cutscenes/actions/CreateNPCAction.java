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
package com.rs.game.content.cutscenes.actions;

import java.util.Map;

import com.rs.game.World;
import com.rs.game.content.cutscenes.Cutscene;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;

public class CreateNPCAction extends CutsceneAction {

	private int id, x, y, plane;

	public CreateNPCAction(String key, int id, int x, int y, int plane, int actionDelay) {
		super(key, actionDelay);
		this.id = id;
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	@Override
	public void process(Player player, Map<String, Object> objects) {
		Cutscene scene = (Cutscene) objects.get("cutscene");
		if (objects.get(getObjectKey()) != null)
			scene.deleteObject(objects.get(getObjectKey()));
		NPC npc = World.spawnNPC(id, WorldTile.of(scene.getX(x), scene.getY(y), plane), -1, true, true);
		objects.put(getObjectKey(), npc);
		npc.setRandomWalk(false);
	}

}
