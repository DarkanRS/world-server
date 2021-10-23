package com.rs.game.npc.qbd;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.util.Utils;

/**
 * Handles the super dragonfire attack.
 * 
 * @author Emperor
 * 
 */
public final class SuperFireAttack implements QueenAttack {

	/**
	 * The animation.
	 */
	private static final Animation ANIMATION = new Animation(16745);

	/**
	 * The graphics.
	 */
	private static final SpotAnim GRAPHIC = new SpotAnim(3152);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		npc.setNextSpotAnim(GRAPHIC);
		victim.sendMessage("<col=FFCC00>The Queen Black Dragon gathers her strength to breath extremely hot flames.</col>");
		if (Utils.getDistance(npc.getBase().transform(33, 31, 0), victim) <= 4)
			victim.getTempAttribs().setB("canBrandish", true);
		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				int hit;
				
				int protection = PlayerCombat.getAntifireLevel(victim, true);
				if (protection == 1) {
					hit = Utils.random(380, 450);
				} else if (protection == 2) {
					hit = Utils.random(300, 310);
				} else {
					hit = Utils.random(500, 800);
				}
				int distance = (int) Utils.getDistance(npc.getBase().transform(33, 31, 0), victim);
				if (distance <= 4)
					victim.getTempAttribs().setB("canBrandish", true);
				hit /= (distance / 3) + 1;
				victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.TRUE_DAMAGE));
				if (++count == 3) {
					victim.getTempAttribs().setB("canBrandish", false);
					stop();
				}
			}
		}, 4, 1);
		return Utils.random(8, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}

}