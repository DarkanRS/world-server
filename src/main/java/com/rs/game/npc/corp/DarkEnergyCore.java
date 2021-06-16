package com.rs.game.npc.corp;

import java.util.List;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.utils.WorldUtil;

public class DarkEnergyCore extends NPC {

	private CorporealBeast beast;
	private Entity target;

	public DarkEnergyCore(CorporealBeast beast) {
		super(8127, beast, true);
		setForceMultiArea(true);
		setIgnoreDocile(true);
		this.beast = beast;
		changeTarget = 2;
	}

	private int changeTarget;
	private int sapTimer;
	private int delay;

	@Override
	public void processNPC() {
		if (isDead() || hasFinished())
			return;
		if (delay > 0) {
			delay--;
			return;
		}
		if (changeTarget > 0) {
			if (changeTarget == 1) {
				List<Entity> possibleTarget = beast.getPossibleTargets();
				if (possibleTarget.isEmpty()) {
					finish();
					beast.removeDarkEnergyCore();
					return;
				}
				target = possibleTarget.get(Utils.getRandomInclusive(possibleTarget.size() - 1));
				setNextWorldTile(new WorldTile(target));
				delay += World.sendProjectile(this, target, 1828, 0, 0, 35, 1, 20, 0).getTaskDelay();
			}
			changeTarget--;
			return;
		}
		if (target == null || !WorldUtil.isInRange(this, target, 0)) {
			changeTarget = 5;
			return;
		}
		if (sapTimer-- <= 0) {
			int damage = Utils.getRandomInclusive(50) + 50;
			target.applyHit(new Hit(this, Utils.random(1, 131), HitLook.TRUE_DAMAGE));
			beast.heal(damage);
			delay = 2;
			if (target instanceof Player) {
				Player player = (Player) target;
				player.sendMessage("The dark core creature steals some life from you for its master.", true);
			}
			sapTimer = getPoison().isPoisoned() ? 40 : 0;
		}
		delay = 2;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		beast.removeDarkEnergyCore();
	}

}
