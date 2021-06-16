package com.rs.game.npc.dungeoneering;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.game.player.content.skills.dungeoneering.skills.DungPickaxe;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public final class BulwarkBeast extends DungeonBoss {

	private int shieldHP;
	private int maxShieldHP;

	public BulwarkBeast(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10073, 10106, 2), manager.getBossLevel()), tile, manager, reference);
		maxShieldHP = shieldHP = 500;
		setHitpoints(getMaxHitpoints());
	}

	@Override
	public void handlePreHit(final Hit hit) {
		handleHit(hit);
		super.handlePreHit(hit);
	}

	public void handleHit(Hit hit) {
		if (shieldHP <= 0 || hit.getLook() == HitLook.MAGIC_DAMAGE)
			return;
		hit.setDamage(0);
		Entity source = hit.getSource();
		if (source == null || !(source instanceof Player))
			return;
		if (hit.getLook() != HitLook.MELEE_DAMAGE)
			return;
		Player playerSource = (Player) source;
		int weaponId = playerSource.getEquipment().getWeaponId();
		if (weaponId != -1 && DungPickaxe.getBest(playerSource) != null) {
			hit.setDamage(Utils.random(50));
			hit.setSoaking(hit);
			shieldHP -= hit.getDamage();
			playerSource.sendMessage(shieldHP > 0 ? "Your pickaxe chips away at the beast's armour plates." : "Your pickaxe finally breaks through the heavy armour plates.");
			refreshBar();
		}
	}

	public int getShieldHP() {
		return shieldHP;
	}

	public void setShieldHP(int shieldHP) {
		this.shieldHP = shieldHP;
	}

	public boolean hasShield() {
		return shieldHP > 0 && !isDead() && !hasFinished();
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		refreshBar();
	}

	public void refreshBar() {
		if (hasShield())
			getManager().showBar(getReference(), "Bulwark Beast's Armour", shieldHP * 100 / maxShieldHP);
		else
			getManager().hideBar(getReference());
	}

}
