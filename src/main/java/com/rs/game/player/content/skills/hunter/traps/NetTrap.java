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
package com.rs.game.player.content.skills.hunter.traps;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.npc.others.BoxHunterNPC;
import com.rs.game.object.GameObject;
import com.rs.game.object.OwnedObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.hunter.BoxTrapType;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.file.FileManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;

public class NetTrap extends BoxStyleTrap {

	private enum TreeType {
		SWAMP(19679, 19681, 19678, 19676, 19677, 19674, 19675),
		DESERT(19652, 19651, 19650, 19657, 19656, 19655, 19654),
		LAVA(19663, 19665, 19662, 19660, 19661, 19658, 19659),
		WILDY(19671, 19673, 19670, 19668, 19669, 19666, 19667);

		private int base;
		private int net;
		private int setUp;
		private int failing;
		private int failed;
		private int catching;
		private int caught;

		private static Map<Integer, TreeType> BY_BASE = new HashMap<>();

		static {
			for (TreeType t : TreeType.values())
				BY_BASE.put(t.base, t);
		}

		public static TreeType fromBase(int base) {
			return BY_BASE.get(base);
		}

		private TreeType(int base, int net, int setUp, int failing, int failed, int catching, int caught) {
			this.base = base;
			this.setUp = setUp;
			this.failing = failing;
			this.failed = failed;
			this.catching = catching;
			this.caught = caught;
			this.net = net;
		}
	}

	private OwnedObject tree;
	private TreeType treeType;

	public NetTrap(Player player, WorldTile tile, GameObject tree) {
		super(player, BoxTrapType.TREE_NET, tile);
		if (tree != null) {
			this.tree = new OwnedObject(player, tree);
			treeType = TreeType.fromBase(tree.getId());
			if (treeType == null)
				FileManager.logError("Tree type null: " + tree);
			setIdNoRefresh(treeType.net);
			setRotation(tree.getRotation());
		}
	}

	@Override
	public void onCreate() {
		if (tree != null) {
			tree.setIdNoRefresh(treeType.setUp);
			tree.createReplace();
		}
	}

	@Override
	public void onDestroy() {
		if (tree != null)
			tree.setId(treeType.base);
	}

	@Override
	public void expire(Player player) {
		WorldTile tile = new WorldTile(this);
		if (tree == null)
			tile = tile.transform(rotation == 1 ? 1 : rotation == 3 ? -1 : 0, rotation == 0 ? 1 : rotation == 2 ? -1 : 0, 0);
		World.addGroundItem(new Item(954, 1), tile, player, true, 60);
		World.addGroundItem(new Item(303, 1), tile, player, true, 60);
	}

	@Override
	public void handleCatch(BoxHunterNPC npc, boolean success) {
		destroy();
		NetTrap compTrap = new NetTrap(getOwner(), tree.transform(rotation == 3 ? -1 : 0, rotation == 2 ? -1 : 0, 0), null);
		compTrap.setRouteType(RouteType.NORMAL);
		compTrap.setRotation(tree.getRotation());
		compTrap.setIdNoRefresh(success ? treeType.catching : treeType.failing);
		compTrap.createReplace();
		if (success) {
			npc.setNextAnimation(new Animation(-1));
			npc.setRespawnTask();
		}
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				compTrap.setNpcTrapped(npc.getType());
				compTrap.setId(success ? treeType.caught : treeType.failed);
				compTrap.setStatus(success ? Status.SUCCESS : Status.FAIL);
			}
		}, 0);
	}
}
