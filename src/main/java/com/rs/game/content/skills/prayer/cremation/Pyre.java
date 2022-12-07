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
package com.rs.game.content.skills.prayer.cremation;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.model.object.OwnedObject;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Pyre extends OwnedObject {

	private PyreLog log;
	private Corpse corpse;
	private int life;
	private boolean lit;
	private boolean shadePyre;

	public Pyre(Player player, GameObject object, PyreLog log, boolean shadePyre) {
		super(player, object);
		id = shadePyre ? log.shadeNoCorpse : log.vyreNoCorpse;
		life = 50;
		this.log = log;
		this.shadePyre = shadePyre;
	}

	@Override
	public void tick(Player owner) {
		if (life-- <= 0)
			destroy();
	}

	public boolean setCorpse(Corpse corpse) {
		if (!log.validCorpse(corpse) || (corpse == Corpse.VYRE && shadePyre))
			return false;
		this.corpse = corpse;
		setId(shadePyre ? log.shadeCorpse : log.vyreCorpse);
		life = 50;
		return true;
	}

	@Override
	public void onDestroy() {
		if (lit)
			return;
		World.addGroundItem(new Item(log.itemId), getCoordFace(), getOwner());
		if (corpse != null)
			World.addGroundItem(new Item(corpse.itemIds[0]), getCoordFace(), getOwner());
	}

	public void light(Player player) {
		life = 50;
		lit = true;
		player.lock();
		player.setNextAnimation(new Animation(16700));
		WorldTasks.delay(1, () -> {
			World.sendSpotAnim(player, new SpotAnim(357), getCoordFace());
			new ReleasedSpirit(player, getCoordFace(), shadePyre);
			player.getSkills().addXp(Constants.FIREMAKING, log.xp);
			player.getSkills().addXp(Constants.PRAYER, corpse.xp);
		});
		WorldTasks.delay(3, () -> {
			destroy();
		});
		WorldTasks.delay(4, () -> {
			player.incrementCount(ItemDefinitions.getDefs(corpse.itemIds[0]).name + " cremated");
			player.unlock();
			GameObject stand = World.getClosestObject(shadePyre ? 4065 : 30488, getCoordFace());
			World.sendSpotAnim(player, new SpotAnim(1605), stand.getTile());
			for (Item item : corpse.getKeyDrop(player, log))
				if (item != null)
					World.addGroundItem(item, stand.getTile());
		});
	}

	public PyreLog getLog() {
		return log;
	}

	public boolean isShadePyre() {
		return shadePyre;
	}

	private static class ReleasedSpirit extends OwnedNPC {

		private int life;

		public ReleasedSpirit(Player owner, WorldTile tile, boolean shade) {
			super(owner, shade ? 1242 : 7687, tile, false);
			life = shade ? 6 : 12;
		}

		@Override
		public void processNPC() {
			if (life-- <= 0)
				finish();
		}

	}

}
