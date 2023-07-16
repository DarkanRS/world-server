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
package com.rs.game.content.skills.prayer;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.content.skills.prayer.Burying.Bone;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.actions.PlayerAction;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class BoneAltar  {

	public static final int ANIM = 3705;
	public static final int GFX = 624;

	public enum Altar {
		OAK(13179, 2.0f),
		TEAK(13182, 2.1f),
		CLOTH(13185, 2.25f),
		MAHOGANY(13188, 2.5f),
		LIMESTONE(13191, 2.75f),
		MARBLE(13194, 3.0f),
		GILDED(13197, 3.5f);

		int objectId;
		float xpMul;

		private Altar(int objectId, float xpMul) {
			this.objectId = objectId;
			this.xpMul = xpMul;
		}

		public int getObjectId() {
			return objectId;
		}

		public float getXpMul() {
			return xpMul;
		}
	}

	static class BoneAction extends PlayerAction {

		private Altar altar;
		private Bone bone;
		private GameObject object;

		public BoneAction(Altar altar, Bone bone, GameObject object) {
			this.altar = altar;
			this.bone = bone;
			this.object = object;
		}

		@Override
		public boolean start(Player player) {
			if (object != null && player.getInventory().containsItem(bone.getId(), 1))
				return true;
			return false;
		}

		@Override
		public boolean process(Player player) {
			if (player.getInventory().containsItem(bone.getId(), 1) && object != null)
				return true;
			return false;
		}

		@Override
		public int processWithDelay(Player player) {
			if (player.getInventory().containsItem(bone.getId(), 1)) {
				player.incrementCount(ItemDefinitions.getDefs(bone.getId()).getName()+" offered at altar");
				player.getInventory().deleteItem(bone.getId(), 1);
				player.getSkills().addXp(Constants.PRAYER, bone.getExperience()*altar.getXpMul());
				player.setNextAnimation(new Animation(ANIM));
				World.sendSpotAnim(object.getTile(), new SpotAnim(GFX));
			}
			return 2;
		}

		@Override
		public void stop(Player player) {

		}

	}

	public static ItemOnObjectHandler handleBonesOnAltar = new ItemOnObjectHandler(new Object[] { 13179, 13182, 13185, 13188, 13191, 13194, 13197 }, e -> {
		Altar altar = null;
		Bone bone = null;
		for (Altar altars : Altar.values())
			if (altars.getObjectId() == e.getObject().getId()) {
				altar = altars;
				break;
			}
		for (Bone bones : Bone.values())
			if (bones.getId() == e.getItem().getId()) {
				bone = bones;
				break;
			}
		if (bone == null)
			return;
		e.getPlayer().getActionManager().setAction(new BoneAction(altar, bone, e.getObject()));
	});

}
