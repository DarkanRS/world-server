package com.rs.game.npc.combat.impl;

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

public class GiantMoleCombat extends CombatScript {

	private static final WorldTile[] COORDS = { new WorldTile(1737, 5228, 0), new WorldTile(1751, 5233, 0), new WorldTile(1778, 5237, 0), new WorldTile(1736, 5227, 0), new WorldTile(1780, 5152, 0), new WorldTile(1758, 5162, 0),
			new WorldTile(1745, 5169, 0), new WorldTile(1760, 5183, 0) };

	@Override
	public Object[] getKeys() {
		return new Object[] { 3340 };
	}

	@Override
	public int attack(final NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (Utils.random(15) == 0) { // bury
			npc.setNextAnimation(new Animation(3314));
			npc.setCantInteract(true);
			npc.getCombat().removeTarget();
			final Player player = (Player) (target instanceof Player ? target : null);
			if (player != null)
				player.getInterfaceManager().setOverlay(226);
			final WorldTile middle = npc.getMiddleWorldTile();
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (player != null)
						player.getInterfaceManager().removeOverlay();
					npc.setCantInteract(false);
					if (npc.isDead())
						return;
					World.sendSpotAnim(npc, new SpotAnim(572), middle);
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX(), middle.getY() - 1, middle.getPlane()));
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX(), middle.getY() + 1, middle.getPlane()));
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX() - 1, middle.getY() - 1, middle.getPlane()));
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX() - 1, middle.getY() + 1, middle.getPlane()));
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX() + 1, middle.getY() - 1, middle.getPlane()));
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX() + 1, middle.getY() + 1, middle.getPlane()));
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX() - 1, middle.getY(), middle.getPlane()));
					World.sendSpotAnim(npc, new SpotAnim(571), new WorldTile(middle.getX() + 1, middle.getY(), middle.getPlane()));
					npc.setNextWorldTile(new WorldTile(COORDS[Utils.random(COORDS.length)]));
					npc.setNextAnimation(new Animation(3315));

				}

			}, 2);

		} else {
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, defs.getMaxHit(), AttackStyle.MELEE, target)));
		}
		return npc.getAttackSpeed();
	}

}
