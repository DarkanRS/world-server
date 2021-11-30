package com.rs.game.npc.combat.impl;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.slayer.Strykewyrm;
import com.rs.game.player.Player;
import com.rs.game.player.content.Effect;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.Ticks;
import com.rs.utils.WorldUtil;

public class StrykewyrmCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 9463, 9465, 9467 };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.getRandomInclusive(10);
		if (attackStyle <= 7 && WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0)) { // melee
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			if (npc.getId() == 9467) {
				if (Utils.getRandomInclusive(10) == 0) {
					target.setNextSpotAnim(new SpotAnim(2309));
					target.getPoison().makePoisoned(44);
				}
			}
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target)));
			return npc.getAttackSpeed();
		}
		if (attackStyle <= 9) { // mage
			npc.setNextAnimation(new Animation(12794));
			final Hit hit = getMagicHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MAGE, target));
			delayHit(npc, 1, target, hit);
			World.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16, 41, 30, 16, 0);
			if (npc.getId() == 9463) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (Utils.getRandomInclusive(10) == 0 && !target.hasEffect(Effect.FREEZE)) {
							target.freeze(Ticks.fromSeconds(3));
							target.setNextSpotAnim(new SpotAnim(369));
							if (target instanceof Player player)
								player.stopAll();
						} else if (hit.getDamage() != 0)
							target.setNextSpotAnim(new SpotAnim(2315));
					}
				}, 1);
			} else if (npc.getId() == 9467) {
				if (Utils.getRandomInclusive(10) == 0) {
					target.setNextSpotAnim(new SpotAnim(2313));
					if (Utils.random(2) == 0)
						target.getPoison().makePoisoned(88);
				}
			}
		} else if (attackStyle == 10) { // bury
			final WorldTile tile = new WorldTile(target);
			tile.moveLocation(-1, -1, 0);
			npc.setNextAnimation(new Animation(12796));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			WorldTasksManager.schedule(new WorldTask() {

				int count;

				@Override
				public void run() {
					if (count == 0) {
						npc.transformIntoNPC(((Strykewyrm) npc).getStompId());
						npc.setForceWalk(tile);
						count++;
					} else if (count == 1 && !npc.hasForceWalk()) {
						npc.transformIntoNPC(((Strykewyrm) npc).getStompId() + 1);
						npc.setNextAnimation(new Animation(12795));
						int distanceX = target.getX() - npc.getX();
						int distanceY = target.getY() - npc.getY();
						int size = npc.getSize();
						if (distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1) {
							delayHit(npc, 0, target, new Hit(npc, 300, HitLook.TRUE_DAMAGE));
							if (npc.getId() == 9467) {
								target.getPoison().makePoisoned(88);
							} else if (npc.getId() == 9465) {
								delayHit(npc, 0, target, new Hit(npc, 300, HitLook.TRUE_DAMAGE));
								target.setNextSpotAnim(new SpotAnim(2311));
							}
						}
						count++;
					} else if (count == 2) {
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								npc.getCombat().setCombatDelay(npc.getAttackSpeed());
								npc.setTarget(target);
								npc.setCantInteract(false);
							}
						});
						stop();
					}
				}
			}, 1, 1);
		}
		return npc.getAttackSpeed();
	}
}
