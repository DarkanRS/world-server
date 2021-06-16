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

public final class FireBreathAttack implements QueenAttack {

	private static final Animation ANIMATION = new Animation(16721);
	private static final SpotAnim GRAPHIC = new SpotAnim(3143);

	@Override
	public int attack(final QueenBlackDragon npc, final Player victim) {
		npc.setNextAnimation(ANIMATION);
		npc.setNextSpotAnim(GRAPHIC);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				super.stop();
				int hit = 0;
				int protection = PlayerCombat.getAntifireLevel(victim, true);
				if (protection == 1)
					hit = Utils.random(350, 400);
				else if (protection == 2)
					hit = Utils.random(150, 200);
				else
					hit = Utils.random(400, 710);
				victim.setNextAnimation(new Animation(PlayerCombat.getDefenceEmote(victim)));
				victim.applyHit(new Hit(npc, hit, HitLook.TRUE_DAMAGE));
			}
		}, 1);
		return Utils.random(4, 15);
	}

	@Override
	public boolean canAttack(QueenBlackDragon npc, Player victim) {
		return true;
	}
}