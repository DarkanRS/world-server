package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class KalphiteQueenCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Kalphite Queen" };
	}

	public static void attackMageTarget(final List<Player> arrayList, Entity fromEntity, final NPC startTile, Entity t, final int projectile, final int gfx) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity, startTile) : t;
		if (fromEntity instanceof NPC) {
			final NPC npc = (NPC) fromEntity;
			if (target == null)
				return;
			if (target instanceof Player)
				arrayList.add((Player) target);
			World.sendProjectile(fromEntity, target, projectile, fromEntity == startTile ? 70 : 20, 20, 30, 6, 0, 0);
			int endTime = 3;
			delayHit(startTile, endTime, target, getMagicHit(startTile, getMaxHit(startTile, npc.getMaxHit(), AttackStyle.MAGE, target)));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					target.setNextSpotAnim(new SpotAnim(gfx));
					attackMageTarget(arrayList, target, startTile, null, projectile, gfx);
				}
			}, endTime);
		}
	}

	private void attackMageTarget(final List<Player> arrayList, Entity fromEntity, final NPC startTile, Entity t) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity, startTile) : t;
		if (target == null)
			return;
		if (target instanceof Player)
			arrayList.add((Player) target);
		World.sendProjectile(fromEntity, target, 280, fromEntity == startTile ? 70 : 20, 20, 60, 30, 0, 0);
		delayHit(startTile, 0, target, getMagicHit(startTile, getMaxHit(startTile, startTile.getMaxHit(), AttackStyle.MAGE, target)));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.setNextSpotAnim(new SpotAnim(281));
				attackMageTarget(arrayList, target, startTile, null);
			}
		});
	}

	private static Player getTarget(List<Player> list, final Entity fromEntity, WorldTile startTile) {
		if (fromEntity == null) {
			return null;
		}
		ArrayList<Player> added = new ArrayList<Player>();
		for (int regionId : fromEntity.getMapRegionsIds()) {
			Set<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = World.getPlayers().get(playerIndex);
				if (player == null || list.contains(player) || !player.withinDistance(fromEntity) || !player.withinDistance(startTile))
					continue;
				added.add(player);
			}
		}
		if (added.isEmpty())
			return null;
		Collections.sort(added, new Comparator<Player>() {

			@Override
			public int compare(Player o1, Player o2) {
				if (o1 == null)
					return 1;
				if (o2 == null)
					return -1;
				if (Utils.getDistance(o1, fromEntity) > Utils.getDistance(o2, fromEntity))
					return 1;
				else if (Utils.getDistance(o1, fromEntity) < Utils.getDistance(o2, fromEntity))
					return -1;
				else
					return 0;
			}
		});
		return added.get(0);

	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attackStyle = Utils.random(3);
		if (attackStyle == 0) {
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1)
				attackStyle = Utils.random(2); // set mage
			else {
				npc.setNextAnimation(new Animation(defs.getAttackEmote()));
				delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
				return npc.getAttackSpeed();
			}
		}
		npc.setNextAnimation(new Animation(npc.getId() == 1158 ? 6240 : 6234));
		if (attackStyle == 1) { // range easy one
			for (final Entity t : npc.getPossibleTargets()) {
				delayHit(npc, 2, t, getRangeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.RANGE, t)));
				World.sendProjectile(npc, t, 288, 46, 31, 50, 30, 16, 0);
			}
		} else {
			npc.setNextSpotAnim(new SpotAnim(npc.getId() == 1158 ? 278 : 279));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					attackMageTarget(new ArrayList<Player>(), npc, npc, target);
				}

			});
		}
		return npc.getAttackSpeed();
	}
}
