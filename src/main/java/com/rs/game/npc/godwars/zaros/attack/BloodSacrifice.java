package com.rs.game.npc.godwars.zaros.attack;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;

public class BloodSacrifice implements NexAttack {

	@Override
	public int attack(Nex nex, Entity target) {
		if (!(target instanceof Player))
			return 0;
		nex.setNextForceTalk(new ForceTalk("I demand a blood sacrifice!"));
		nex.playSound(3293, 2);
		final Player player = (Player) target;
		player.getAppearance().setGlowRed(true);
		player.sendMessage("Nex has marked you as a sacrifice, RUN!");
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getAppearance().setGlowRed(false);
				if (Utils.getDistance(nex, player) < 7) {
					player.sendMessage("You didn't make it far enough in time - Nex fires a punishing attack!");
					nex.setNextAnimation(new Animation(6987));
					for (final Entity t : nex.getPossibleTargets()) {
						World.sendProjectile(nex, t, 374, 41, 16, 41, 35, 16, 0, () -> {
							nex.heal(t.getHitpoints());
							t.applyHit(new Hit(nex, (int) (t.getHitpoints() * 0.1), HitLook.TRUE_DAMAGE));
						});
					}
				}
			}
		}, nex.getAttackSpeed());
		return nex.getAttackSpeed() * 2;
	}

}
