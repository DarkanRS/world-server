package com.rs.game.npc.combat.impl.dung;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.rs.cache.loaders.ObjectType;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.npc.dungeoneering.DungeonNPC;
import com.rs.game.npc.dungeoneering.LexicusRunewright;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class LexicusRunewrightCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Lexicus Runewright" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		LexicusRunewright boss = (LexicusRunewright) npc;
		if (boss.getAttackStage() == 0) {
			boss.sendAlmanacArmyAttack(target);
			boss.incrementAttackStage();
			return 3;
		}

		if (boss.getAttackStage() == 1 && Utils.random(4) == 0) {
			sendBookBarrage(boss);
			boss.incrementAttackStage();
			return 5;
		}
		if (boss.getAttackStage() == 2 && Utils.random(4) == 0) {
			boss.sendTeleport();
			boss.resetAttackStage();
			return 9;
		}

		int attack = Utils.random(WorldUtil.isInRange(target.getX(), target.getY(), target.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0) ? 5 : 4);
		switch (attack) {
		case 0:// Range
		case 1:
		case 2:
		case 3:// Magic
			boolean range_style = attack == 0 || attack == 1;
			boss.setNextAnimation(new Animation(13470));
			boss.setNextSpotAnim(new SpotAnim(range_style ? 2408 : 2424));
			World.sendProjectile(npc, target, range_style ? 2409 : 2425, 40, 40, 54, 35, 5, 0);
			if (range_style)
				delayHit(npc, 1, target, getRangeHit(npc, getMaxHit(npc, AttackStyle.RANGE, target)));
			else
				delayHit(npc, 1, target, getMagicHit(npc, getMaxHit(npc, AttackStyle.MAGE, target)));
			target.setNextSpotAnim(new SpotAnim(range_style ? 2410 : 2426, 75, 0));
			break;
		case 4:// MELEE
			boss.setNextAnimation(new Animation(13469));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, AttackStyle.MELEE, target)));
			break;
		}
		return 5;
	}

	public static void sendBookBarrage(final LexicusRunewright npc) {

		final List<GameObject> cases = new ArrayList<GameObject>();
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				GameObject o = npc.getManager().getObjectWithType(npc.getReference(), ObjectType.SCENERY_INTERACT, x, y);
				if (o != null && o.getId() >= 49280 && o.getId() <= 49282)
					cases.add(o);
			}
		}

		npc.setNextForceTalk(new ForceTalk("Book barrage!"));
		WorldTasksManager.schedule(new WorldTask() {

			private int cycle = 0;
			private LinkedList<WorldTile> targets = new LinkedList<WorldTile>();

			@Override
			public void run() {
				cycle++;

				if (npc == null || npc.isDead()) {
					stop();
					return;
				}

				if (cycle == 1) {
					for (Entity entity : npc.getPossibleTargets(true)) {
						if (entity instanceof DungeonNPC)
							continue;
						WorldTile tile = new WorldTile(entity);
						targets.add(tile);

						for (int i = 0; i < 3; i++) {
							if (cases.isEmpty())
								break;
							GameObject c = cases.get(Utils.random(cases.size()));
							cases.remove(c);
							World.sendProjectile(c, tile, 2422, 60, 75, 30, 0, 0, 0);
						}
					}
				} else if (cycle == 4) {

					for (WorldTile tile : targets)
						World.sendSpotAnim(npc, new SpotAnim(2423), tile);

					for (Entity entity : npc.getPossibleTargets(true)) {
						if (entity instanceof DungeonNPC)
							continue;
						tileLoop: for (WorldTile tile : targets) {
							if (entity.getX() != tile.getX() || entity.getY() != tile.getY())
								continue tileLoop;
							entity.applyHit(new Hit(npc, (int) (entity instanceof Familiar ? 1000 : Utils.random(entity.getMaxHitpoints() * .6, entity.getMaxHitpoints() * .9)), HitLook.TRUE_DAMAGE));
						}
					}
					targets.clear();
					stop();
				}

			}
		}, 0, 0);
		// GFX 2421+
	}
}
