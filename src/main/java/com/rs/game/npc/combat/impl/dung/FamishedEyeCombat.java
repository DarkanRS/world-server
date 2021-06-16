package com.rs.game.npc.combat.impl.dung;

import java.util.LinkedList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.dungeoneering.FamishedEye;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class FamishedEyeCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { "Famished warrior-eye", "Famished ranger-eye", "Famished mage-eye" };
	}

	@Override
	public int attack(NPC npc, final Entity target) {
		final FamishedEye eye = (FamishedEye) npc;

		if (eye.isInactive())
			return 0;
		else if (!eye.isFirstHit()) {
			eye.setFirstHit(true);
			return Utils.random(5, 15);
		}

		npc.setNextAnimation(new Animation(14916));
		WorldTasksManager.schedule(new WorldTask() {

			private List<WorldTile> tiles;
			private WorldTile targetTile;

			int cycles;

			@Override
			public void run() {
				cycles++;
				if (cycles == 1) {
					tiles = new LinkedList<WorldTile>();
					targetTile = new WorldTile(target);
					World.sendProjectile(eye, targetTile, 2849, 35, 30, 41, 0, 15, 0);
				} else if (cycles == 2) {
					for (int x = -1; x < 2; x++) {
						for (int y = -1; y < 2; y++) {
							WorldTile attackedTile = targetTile.transform(x, y, 0);
							if (x != y)
								World.sendProjectile(targetTile, attackedTile, 2851, 35, 0, 26, 40, 16, 0);
							tiles.add(attackedTile);
						}
					}
				} else if (cycles == 3) {
					for (WorldTile tile : tiles) {
						if (!tile.matches(targetTile))
							World.sendSpotAnim(eye, new SpotAnim(2852, 35, 5), tile);
						for (Entity t : eye.getPossibleTargets()) {
							if (t.matches(tile))
								t.applyHit(new Hit(eye, (int) Utils.random(eye.getMaxHit() * .25, eye.getMaxHit()), HitLook.TRUE_DAMAGE));
						}
					}
					tiles.clear();
					stop();
					return;
				}
			}
		}, 0, 0);
		return (int) Utils.random(5, 35);
	}
}
