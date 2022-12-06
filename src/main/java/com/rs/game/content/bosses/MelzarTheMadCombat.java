package com.rs.game.content.bosses;

import com.rs.game.World;
import com.rs.game.model.WorldProjectile;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.npc.combat.CombatScript;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions;
import com.rs.game.model.entity.npc.combat.NPCCombatDefinitions.AttackStyle;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class MelzarTheMadCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 753 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if(target instanceof Player) {
			final NPCCombatDefinitions defs = npc.getCombatDefinitions();
			switch(Utils.random(0, 4)) {
			case 0 -> {npc.forceTalk("Let me drink my tea in peace.");}
			case 1 -> {npc.forceTalk("Leave me alone I need to feed my pet rock.");}
			case 2 -> {npc.forceTalk("By the power of custard!");}
			}

			switch (Utils.random(0, 3)) {
			case 0 -> {
				World.addGroundItem(new Item(1965, 1), WorldTile.of(target.getX() + Utils.random(0, 2) - 1, target.getY() + Utils.random(0, 2) - 1, target.getPlane()), (Player)target);
				npc.setNextAnimation(new Animation(Utils.random(0, 2) == 0 ? 423 : 422));
				int damage = getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target);
				delayHit(npc, 0, target, getMeleeHit(npc, damage));
			}
			case 1 -> {
				npc.setNextAnimation(new Animation(1163));
				npc.setNextSpotAnim(new SpotAnim(102));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						WorldProjectile p = World.sendProjectile(npc, target, 103, 80, 30, 40, 5, 5, 0);
						target.setNextSpotAnim(new SpotAnim(104, 0, 100));
						delayHit(npc, p.getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, defs.getMaxHit() - 2, AttackStyle.MAGE, target)));
					}
				}, 2);
			}
			case 2 -> {
				npc.setNextAnimation(new Animation(14209));
				npc.setNextSpotAnim(new SpotAnim(2713));
				WorldTasks.schedule(new WorldTask() {
					@Override
					public void run() {
						WorldProjectile p = World.sendProjectile(npc, target, 103, 80, 30, 40, 5, 5, 0);
						target.setNextSpotAnim(new SpotAnim(2727, 0, 100));
						delayHit(npc, p.getTaskDelay(), target, getMagicHit(npc, getMaxHit(npc, defs.getMaxHit() - 2, AttackStyle.MAGE, target)));
					}
				}, 2);
			}
			}
		}
		return npc.getAttackSpeed();
	}

}
