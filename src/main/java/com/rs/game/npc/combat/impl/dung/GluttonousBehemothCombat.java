package com.rs.game.npc.combat.impl.dung;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.DungeonBoss;
import com.rs.game.npc.dungeoneering.GluttonousBehemoth;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class GluttonousBehemothCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Gluttonous behemoth" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		DungeonBoss boss = (DungeonBoss) npc;
		DungeonManager manager = boss.getManager();
		boolean lessThanHalf = npc.getHitpoints() < npc.getMaxHitpoints() * .5;
		if (lessThanHalf && npc.getTemporaryAttributes().get("GLUTTONOUS_HEALING") == null) {
			RoomReference reference = manager.getCurrentRoomReference(npc);
			GameObject food1 = manager.getObject(reference, 49283, 0, 11);
			GameObject food2 = manager.getParty().getTeam().size() <= 1 ? null : manager.getObject(reference, 49283, 11, 11);
			GameObject food = food1;
			if (food1 != null) {
				for (Player player : manager.getParty().getTeam()) {
					if (player.withinDistance(food1, food1.getDefinitions().getSizeX() + 1)) {
						food = null;
						break;
					}
				}
			}
			if (food == null && food2 != null) {
				food = food2;
				for (Player player : manager.getParty().getTeam()) {
					if (player.withinDistance(food2, food1.getDefinitions().getSizeX() + 1)) {
						food = null;
						break;
					}
				}
			}
			if (food != null) {
				npc.getTemporaryAttributes().put("GLUTTONOUS_HEALING", true);
				((GluttonousBehemoth) npc).setHeal(food);
				return 0;
			}
		}
		boolean stomp = false;
		for (Player player : manager.getParty().getTeam()) {
			if (WorldUtil.collides(player.getX(), player.getY(), player.getSize(), npc.getX(), npc.getY(), npc.getSize())) {
				stomp = true;
				delayHit(npc, 0, player, getRegularHit(npc, getMaxHit(npc, AttackStyle.MELEE, player)));
			}
		}
		if (stomp) {
			npc.setNextAnimation(new Animation(13718));
			return npc.getAttackSpeed();
		}
		int attackStyle = Utils.getRandomInclusive(2);
		if (attackStyle == 2) {
			if (!WorldUtil.isInRange(npc.getX(), npc.getY(), npc.getSize(), target.getX(), target.getY(), target.getSize(), 0))
				attackStyle = Utils.getRandomInclusive(1);
			else {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
		}
		if (attackStyle == 0) {
			npc.setNextAnimation(new Animation(13719));
			World.sendProjectile(npc, target, 2612, 41, 16, 41, 35, 16, 0);
			int damage = getMaxHit(npc, AttackStyle.MAGE, target);
			delayHit(npc, 2, target, getMagicHit(npc, damage));
			if (damage != 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						target.setNextSpotAnim(new SpotAnim(2613));
					}
				}, 1);
			}
		} else if (attackStyle == 1) {
			npc.setNextAnimation(new Animation(13721));
			World.sendProjectile(npc, target, 2610, 41, 16, 41, 35, 16, 0);
			delayHit(npc, 2, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(2611));
				}
			}, 1);
		}
		return npc.getAttackSpeed();
	}
}
