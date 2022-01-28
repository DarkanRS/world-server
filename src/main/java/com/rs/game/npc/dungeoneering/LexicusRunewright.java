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
package com.rs.game.npc.dungeoneering;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class LexicusRunewright extends DungeonBoss {

	private static final int[] TELEPORT_LOCS =
		{ 8, 7, 3, 3, 3, 12, 12, 12, 12, 3 };

	private boolean completedFirstAttack;
	private int attackStage;
	private List<TombOfLexicus> books = new CopyOnWriteArrayList<>();

	public LexicusRunewright(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(9842, 9855), manager.getBossLevel()), tile, manager, reference);
	}

	@Override
	public void processHit(Hit hit) {
		int damage = hit.getDamage();
		if (damage > 0)
			if (hit.getLook() == HitLook.MELEE_DAMAGE)
				hit.getSource().applyHit(new Hit(this, (int) (damage * .33), HitLook.REFLECTED_DAMAGE));
		super.processHit(hit);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		for (TombOfLexicus book : books)
			book.sendDeath(book);
	}

	public void sendTeleport() {
		setCantInteract(true);
		setNextAnimation(new Animation(8939));
		setNextSpotAnim(new SpotAnim(1576));
		WorldTasks.schedule(new WorldTask() {

			int cycles = 0;

			@Override
			public void run() {
				cycles++;
				if (cycles == 2) {
					int random = Utils.random(TELEPORT_LOCS.length);
					if (random % 1 == 0 && random != 0)
						random -= 1;
					setNextWorldTile(World.getFreeTile(getManager().getTile(getReference(), TELEPORT_LOCS[random], TELEPORT_LOCS[random + 1]), 2));
					setNextAnimation(new Animation(8941));
					setNextSpotAnim(new SpotAnim(1577));
				} else if (cycles == 4) {
					setCantInteract(false);
					getCombat().removeTarget();
				}
			}
		}, 0, 0);
	}

	public boolean sendAlmanacArmyAttack(final Entity target) {
		final LexicusRunewright boss = this;
		boss.setNextForceTalk(new ForceTalk("Almanac Army, attack!"));
		WorldTasks.schedule(new WorldTask() {

			@Override
			public void run() {
				for (int id = 0; id < 2; id++) {
					if (reachedMaxBookSize())
						break;
					WorldTile tile = getManager().getTile(getReference(), 6 + Utils.random(4), 6 + Utils.random(4));
					TombOfLexicus book = new TombOfLexicus(boss, 9856 + Utils.random(3), tile, getManager()); //TODO scale to combat level
					book.setTarget(target);
					books.add(book);
				}
			}
		}, 2);
		return true;
	}

	public void removeBook(TombOfLexicus book) {
		books.remove(book);
	}

	private boolean reachedMaxBookSize() {
		int size = getManager().getParty().getTeam().size();
		return books.size() >= (size > 3 ? 4 : size) * 3;
	}

	public boolean hasCompletedFirstAttack() {
		return completedFirstAttack;
	}

	public void setCompletedFirstAttack(boolean firstAttack) {
		completedFirstAttack = firstAttack;
	}

	public int getAttackStage() {
		return attackStage;
	}

	public void resetAttackStage() {
		attackStage = reachedMaxBookSize() ? 1 : 0;
	}

	public void incrementAttackStage() {
		attackStage++;
	}
}
