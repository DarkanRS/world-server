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
package com.rs.game.content.bosses.qbd.npcs;

import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.Task;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;

/**
 * Represents a Tortured soul.
 *
 * @author Emperor
 *
 */
public final class TorturedSoul extends NPC {

	/**
	 * The messages the NPC can say.
	 */
	private static final ForceTalk[] FORCE_MESSAGES = { new ForceTalk("NO MORE! RELEASE ME, MY QUEEN! I BEG YOU!"), new ForceTalk("We lost our free will long ago..."), new ForceTalk("How long has it been since I was taken..."),
			new ForceTalk("The cycle is never ending, mortal...") };

	/**
	 * The teleport graphic.
	 */
	static final SpotAnim TELEPORT_GRAPHIC = new SpotAnim(3147);

	/**
	 * The teleport animation.
	 */
	static final Animation TELEPORT_ANIMATION = new Animation(16861);

	/**
	 * The special attack graphic.
	 */
	private static final SpotAnim SPECIAL_GRAPHIC = new SpotAnim(3146);

	/**
	 * The special attack graphic.
	 */
	private static final SpotAnim SPECIAL_ATT_GFX_ = new SpotAnim(3145);

	/**
	 * The special attack animation.
	 */
	private static final Animation SPECIAL_ATT_ANIM_ = new Animation(16864);

	/**
	 * The queen black dragon reference.
	 */
	private final QueenBlackDragon dragon;

	/**
	 * The player victim.
	 */
	private final Player victim;


	/**
	 * Constructs a new {@code TorturedSoul} {@code Object}.
	 *
	 * @param dragon
	 *            The queen black dragon reference.
	 * @param victim
	 *            The player victim.
	 * @param spawn
	 *            The spawn location.
	 */
	public TorturedSoul(QueenBlackDragon dragon, Player victim, Tile spawn) {
		super(15510, spawn, false);
		super.setHitpoints(500);
		super.getCombatDefinitions().setHitpoints(500);
		super.setForceMultiArea(true);
		this.dragon = dragon;
		this.victim = victim;
		super.setRandomWalk(false);
		super.getCombat().setTarget(victim);
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasks.scheduleTimer(loop -> {
			if (loop == 0)
				setNextAnimation(new Animation(defs.getDeathEmote()));
			else if (loop >= defs.getDeathDelay()) {
				finish();
				return false;
			}
			return true;
		});
	}

	/**
	 * Sends the special attack.
	 */
	public void specialAttack(Tile teleport) {
		super.getCombat().addCombatDelay(10);
		super.setNextTile(teleport);
		super.setNextSpotAnim(TELEPORT_GRAPHIC);
		super.setNextAnimation(TELEPORT_ANIMATION);
		super.getCombat().reset();
		WorldTasks.schedule(new Task() {
			@Override
			public void run() {
				stop();
				int diffX = getX() - victim.getX(), diffY = getY() - victim.getY();
				if (diffX < 0)
					diffX = -diffX;
				if (diffY < 0)
					diffY = -diffY;
				int offsetX = 0, offsetY = 0;
				if (diffX > diffY)
					offsetX = getX() - victim.getX() < 0 ? -1 : 1;
				else
					offsetY = getY() - victim.getY() < 0 ? -1 : 1;
				if (victim.transform(offsetX, offsetY, 0).matches(getTile())) {
					offsetX = -offsetX;
					offsetY = -offsetY;
				}
				final int currentX = offsetX + victim.getX();
				final int currentY = offsetY + victim.getY();
				setNextForceTalk(FORCE_MESSAGES[Utils.random(FORCE_MESSAGES.length)]);
				setNextSpotAnim(SPECIAL_ATT_GFX_);
				setNextAnimation(SPECIAL_ATT_ANIM_);
				getCombat().setTarget(victim);
				getTasks().schedule(new Task() {
					int x = currentX, y = currentY;

					@Override
					public void run() {
						Tile current = Tile.of(x, y, 1);
						victim.getPackets().sendSpotAnim(SPECIAL_GRAPHIC, current);
						Entity target = null;
						for (TorturedSoul soul : dragon.getSouls())
							if (soul.matches(current)) {
								target = soul;
								break;
							}
						if (target == null)
							for (NPC worm : dragon.getWorms())
								if (worm.matches(current)) {
									target = worm;
									break;
								}
						if (target == null && victim.matches(current))
							target = victim;
						if (target != null) {
							stop();
							target.applyHit(new Hit(dragon, Utils.random(200, 260), HitLook.TRUE_DAMAGE));
							return;
						}
						if (x > victim.getX())
							x--;
						else if (x < victim.getX())
							x++;
						if (y > victim.getY())
							y--;
						else if (y < victim.getY())
							y++;
					}
				}, 0, 0);
			}
		}, 1);
	}
}