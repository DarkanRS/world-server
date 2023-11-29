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
package com.rs.game.content.skills.dungeoneering.npcs;

import com.rs.game.World;
import com.rs.game.content.skills.dungeoneering.DungeonManager;
import com.rs.game.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.content.skills.dungeoneering.RoomReference;
import com.rs.game.content.skills.dungeoneering.npcs.bosses.DungeonBoss;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;

public class RuneboundBehemoth extends DungeonBoss {

	private static final String[] ARTIFACT_TYPE =
		{ "Melee", "Range", "Magic" };
	private final BehemothArtifact[] artifacts;
	private final int baseId;

	public RuneboundBehemoth(Tile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(11812, 11826), manager.getBossLevel()), tile, manager, reference);
		this.baseId = getId();
		this.artifacts = new BehemothArtifact[3];
		for (int idx = 0; idx < artifacts.length; idx++)
			artifacts[idx] = new BehemothArtifact(idx);
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (artifacts != null)
			for (BehemothArtifact artifact : artifacts)
				artifact.cycle();
		super.processNPC();
	}

	@Override
	public void processHit(Hit hit) {
		if (hit.getDamage() > 0)
			reduceHit(hit);
		super.processHit(hit);
	}

	public void reduceHit(Hit hit) {
		if ((hit.getLook() == HitLook.MELEE_DAMAGE && artifacts[0].isPrayerEnabled()) || (hit.getLook() == HitLook.RANGE_DAMAGE && artifacts[1].isPrayerEnabled()) || (hit.getLook() == HitLook.MAGIC_DAMAGE && artifacts[2].isPrayerEnabled()))
			return;
		hit.setDamage(0);
	}

	public void activateArtifact(Player player, GameObject object, int type) {
		BehemothArtifact artifact = artifacts[type];
		if (artifact.isActive())//Hax unit 2k13
			return;
		GameObject o = new GameObject(object);
		o.setId(type == 0 ? 53980 : type == 1 ? 53982 : 53981);
		World.spawnObjectTemporary(o, Ticks.fromSeconds(30));
		artifact.setActive(true, true);
		sendNPCTransformation();
	}

	public void resetTransformation() {
		for (BehemothArtifact artifact : artifacts)
			artifact.setActive(false, false);
		sendNPCTransformation();
	}

	private void sendNPCTransformation() {
		setNextNPCTransformation(getNPCId());
	}

	public int getNPCId() {
		boolean melee = artifacts[0].isPrayerEnabled();
		boolean range = artifacts[1].isPrayerEnabled();
		boolean magic = artifacts[2].isPrayerEnabled();
		if (melee && magic && range)
			return baseId - 45;
		if (melee && range)
			return baseId + 30;
		if (melee && magic)
			return baseId + 15;
		else if (magic && range)
			return baseId + 45;
		else if (melee)
			return baseId - 15;
		else if (range)
			return baseId - 30;
		else if (magic)
			return baseId - 60;
		return baseId;
	}

	class BehemothArtifact {

		final int type;
		private boolean active;
		private int cycle;

		public BehemothArtifact(int type) {
			this.type = type;
		}

		public void cycle() {
			if (active) {
				cycle++;
				if (cycle == 50)
					setActive(false, true);
				else if (cycle == 25)
					sendNPCTransformation();
			}
		}

		public boolean isPrayerEnabled() {
			return cycle < 25 && active;
		}

		public boolean isActive() {
			return active;
		}

		public void setActive(boolean active, boolean message) {
			this.active = active;
			if (!active)
				cycle = 0;
			if (message)
				for (Player p2 : getManager().getParty().getTeam()) {
					if (getManager().isAtBossRoom(p2.getTile()))
						continue;
					p2.sendMessage("The " + ARTIFACT_TYPE[type] + " artifact has been " + (active ? "desactivated" : "re-charged") + "!");
				}
		}
	}
}
