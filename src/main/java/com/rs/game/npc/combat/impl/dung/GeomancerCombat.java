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
package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.HobgoblinGeomancer;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.Room;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class GeomancerCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Hobgoblin Geomancer" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		HobgoblinGeomancer boss = (HobgoblinGeomancer) npc;

		boolean atDistance = !WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0);

		if (Utils.random(boss.getManager().getParty().getTeam().size() > 1 ? 6 : 8) == 0 && !boss.isCantInteract()) {
			WorldTile tile = new WorldTile(target);
			DungeonManager dungeon = boss.getManager();
			RoomReference rRef = dungeon.getCurrentRoomReference(tile);
			Room room = dungeon.getRoom(rRef);

			if (room != null && room.getRoom() == DungeonUtils.getBossRoomWithChunk(DungeonConstants.ABANDONED_FLOORS, 24, 640)) {
				sendEntangle(npc, target);
				boss.sendTeleport(tile, rRef);
			}
			return 7;
		}

		int attackType = Utils.random(6);
		switch (attackType) {
		case 0:// MELEE
			if (atDistance)
				sendEarthBlast(npc, target, true);
			else {
				npc.setNextAnimation(new Animation(12989));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
			}
			break;
		case 1:// EARTH BLAST
		case 2:
			sendEarthBlast(npc, target, attackType == 2);
			break;
		case 3:// WEAKEN
			sendWeaken(npc, target);
			break;
		case 4:// SNARE
			sendEntangle(npc, target);
			break;
		case 5:
			sendPrayerSpell(npc);
			break;
		}

		return 4;
	}

	private void sendEarthBlast(NPC npc, Entity target, boolean multiAttack) {
		npc.setNextSpotAnim(new SpotAnim(2715));
		npc.setNextAnimation(new Animation(12990));

		for (Entity t : npc.getPossibleTargets()) {
			if (!multiAttack && t.getIndex() != target.getIndex())
				continue;
			t.setNextSpotAnim(new SpotAnim(2726, 75, 100));
			World.sendProjectile(npc, t, 2720, 50, 18, 50, 50, 0, 0);
			delayHit(npc, 1, t, getMagicHit(npc, getMaxHit(npc, (int) (npc.getMaxHit(AttackStyle.MAGE) * .7), AttackStyle.MAGE, t)));
		}
	}

	private void sendEntangle(NPC npc, Entity target) {
		npc.setNextSpotAnim(new SpotAnim(177, 0, 50));
		npc.setNextAnimation(new Animation(12992));
		npc.removeTarget();
		World.sendProjectile(npc, target, 178, 40, 18, 55, 70, 5, 0);

		int damage = getMaxHit(npc, (int) (npc.getMaxHit(AttackStyle.MAGE) * 0.95), AttackStyle.MAGE, target);

		if (damage > 0) {
			target.setNextSpotAnim(new SpotAnim(180, 75, 100));
			// if (!target.isBoundImmune()) {
			// target.setBoundDelay(20, false, 7);
			target.freeze(20);
			// }
			if (target instanceof Player player)
				player.getActionManager().setActionDelay(4);
		}
	}

	private void sendWeaken(NPC npc, Entity target) {
		npc.setNextSpotAnim(new SpotAnim(105, 0, 60));
		npc.setNextAnimation(new Animation(12992));
		World.sendProjectile(npc, target, 106, 40, 18, 55, 70, 5, 0);

		int damage = getMaxHit(npc, AttackStyle.MAGE, target);

		if (damage > 0) {
			target.setNextSpotAnim(new SpotAnim(107, 75, 150));

			if (target instanceof Player player) {
				for (int skill = 0; skill < Constants.MAGIC; skill++) {
					if (skill == 3 || skill == 5)
						continue;
					player.getSkills().set(skill, (int) (player.getSkills().getLevel(skill) * .95));
				}
				player.sendMessage("Your stats have been significantly lowered.");
			}
		}
	}

	private void sendPrayerSpell(NPC npc) {
		npc.setNextAnimation(new Animation(12988));
		npc.setNextSpotAnim(new SpotAnim(2147));

		boolean hasDrained = false;

		for (Entity t : npc.getPossibleTargets()) {
			int damage = getMaxHit(npc, AttackStyle.MAGE, t);

			if (damage > 0)
				if (t instanceof Player player)
					if (player.getPrayer().hasPrayersOn()) {
						if (!hasDrained) {
							int prayerPoints = (int) player.getPrayer().getPoints();

							npc.setNextSpotAnim(new SpotAnim(2369, 70, 0));
							if (prayerPoints > 0) {
								npc.heal(prayerPoints);
								hasDrained = true;
							}
						}
						player.getPrayer().drainPrayer();
						player.getPrayer().closeAllPrayers();
						player.sendMessage("Your prayers have been disabled.");
					}
			delayHit(npc, 1, t, getMagicHit(npc, (int) (damage * .50)));
			t.setNextSpotAnim(new SpotAnim(2147));
			World.sendProjectile(npc, t, 2368, 50, 18, 55, 70, 5, 0);
		}
	}
}
