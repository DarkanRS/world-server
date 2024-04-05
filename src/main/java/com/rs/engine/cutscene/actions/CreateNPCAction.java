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
package com.rs.engine.cutscene.actions;

import com.rs.engine.cutscene.Cutscene;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

import java.util.Map;
import java.util.function.Consumer;

public class CreateNPCAction extends CutsceneAction {

	private final int id;
    private final int x;
    private final int y;
    private final int plane;
	private final Consumer<NPC> configureNpc;

	public CreateNPCAction(String key, int id, int x, int y, int plane, int actionDelay, Consumer<NPC> configureNpc) {
		super(key, actionDelay);
		this.id = id;
		this.x = x;
		this.y = y;
		this.plane = plane;
		this.configureNpc = configureNpc;
	}

	@Override
	public void process(Player player, Map<String, Object> objects) {
		Cutscene scene = (Cutscene) objects.get("cutscene");
		if (objects.get(getObjectKey()) != null)
			scene.deleteObject(objects.get(getObjectKey()));
		NPC npc = World.spawnNPC(id, Tile.of(scene.getX(x), scene.getY(y), plane), true, true);
		objects.put(getObjectKey(), npc);
		npc.setRandomWalk(false);
		if (configureNpc != null)
			configureNpc.accept(npc);
	}

}
