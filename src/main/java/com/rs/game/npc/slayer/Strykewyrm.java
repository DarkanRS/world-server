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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.slayer;

import com.rs.game.Hit;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.combat.CombatSpell;
import com.rs.game.player.content.skills.slayer.TaskMonster;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Strykewyrm extends NPC {

	private int stompId;

	public Strykewyrm(int id, WorldTile tile) {
		super(id, tile, false);
		stompId = id;
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		if (getId() != stompId && !isCantInteract() && !isUnderCombat()) {
			setNextAnimation(new Animation(12796));
			setCantInteract(true);
			WorldTasks.schedule(new WorldTask() {
				@Override
				public void run() {
					transformIntoNPC(stompId);
					WorldTasks.schedule(new WorldTask() {
						@Override
						public void run() {
							setCantInteract(false);
						}
					});
				}
			});
		}
	}

	@Override
	public void handlePreHit(Hit hit) {
		if (getId() == 9462 || getId() == 9463)
			if (hit.getSource() instanceof Player) {
				Player player = (Player) hit.getSource();

				switch (getId()) {
				case 9462:
				case 9463:
					if (!player.isOnTask(TaskMonster.ICE_STRYKEWYRMS)) {
						player.sendMessage("You seem to be unable to damage it.");
						hit.setDamage(0);
						setCapDamage(0);
					}
					if (hit.getData("combatSpell") != null && hit.getData("combatSpell", CombatSpell.class).isFireSpell())
						hit.setDamage(hit.getDamage() * 2);
					break;
				case 9464:
				case 9465:
					if (!player.isOnTask(TaskMonster.DESERT_STRYKEWYRMS)) {
						player.sendMessage("You seem to be unable to damage it.");
						hit.setDamage(0);
						setCapDamage(0);
					}
					break;
				case 9466:
				case 9467:
					if (!player.isOnTask(TaskMonster.JUNGLE_STRYKEWYRMS)) {
						player.sendMessage("You seem to be unable to damage it.");
						hit.setDamage(0);
						setCapDamage(0);
					}
					break;
				}

				if (!player.getEquipment().hasFirecape() && !player.iceStrykeNoCape()) {
					player.sendMessage("The strykewyrm numbs your hands and freezes your attack.");
					hit.setDamage(0);
					setCapDamage(0);
					return;
				}
				if (getCapDamage() == 0)
					setCapDamage(-1);
			}
		super.handlePreHit(hit);
	}

	@Override
	public void reset() {
		setNPC(stompId);
		super.reset();
	}

	public int getStompId() {
		return stompId;
	}

	public static void handleStomping(final Player player, final NPC npc) {
		if (npc.isCantInteract())
			return;
		if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
			if (player.getAttackedBy() != npc && player.inCombat()) {
				player.sendMessage("You are already in combat.");
				return;
			}
			if (npc.getAttackedBy() != player && npc.inCombat()) {
				if (!(npc.getAttackedBy() instanceof NPC)) {
					player.sendMessage("That npc is already in combat.");
					return;
				}
				npc.setAttackedBy(player);
			}
		}
		switch (npc.getId()) {
		case 9462:
			if (player.getSkills().getLevel(18) < 93) {
				player.sendMessage("You need at least a slayer level of 93 to fight this.");
				return;
			}
			if (!player.isOnTask(TaskMonster.ICE_STRYKEWYRMS)) {
				player.sendMessage("The mound doesn't respond.");
				return;
			}
			break;
		case 9464:
			if (player.getSkills().getLevel(18) < 77) {
				player.sendMessage("You need at least a slayer level of 77 to fight this.");
				return;
			}
			if (!player.isOnTask(TaskMonster.DESERT_STRYKEWYRMS)) {
				player.sendMessage("The mound doesn't respond.");
				return;
			}
			break;
		case 9466:
			if (player.getSkills().getLevel(18) < 73) {
				player.sendMessage("You need at least a slayer level of 73 to fight this.");
				return;
			}
			if (!player.isOnTask(TaskMonster.JUNGLE_STRYKEWYRMS)) {
				player.sendMessage("The mound doesn't respond.");
				return;
			}
			break;
		default:
			return;
		}
		player.setNextAnimation(new Animation(4278));
		player.lock(2);
		npc.setCantInteract(true);
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				npc.setNextAnimation(new Animation(12795));
				npc.transformIntoNPC(((Strykewyrm) npc).stompId + 1);
				stop();
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						npc.setTarget(player);
						npc.setAttackedBy(player);
						npc.setCantInteract(false);
					}
				}, 2);
			}

		}, 2);
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(9462, 9463, 9464, 9465, 9466, 9467) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Strykewyrm(npcId, tile);
		}
	};
}
