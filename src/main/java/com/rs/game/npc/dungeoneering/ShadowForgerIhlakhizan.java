package com.rs.game.npc.dungeoneering;

import java.util.List;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public final class ShadowForgerIhlakhizan extends DungeonBoss {

	public ShadowForgerIhlakhizan(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(10143, 10156), manager.getBossLevel()), tile, manager, reference);
		setCantFollowUnderCombat(true); //force cant walk
	}

	@Override
	public void setNextFaceEntity(Entity entity) {
		//this boss doesnt face
	}
	
	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		super.processNPC();
		for (Player player : getManager().getParty().getTeam()) {
			if (!getManager().isAtBossRoom(player) || lineOfSightTo(player, false) || player.getTempAttribs().get("SHADOW_FORGER_SHADOW") != null)
				continue;
			player.setNextSpotAnim(new SpotAnim(2378));
			player.getTempAttribs().put("SHADOW_FORGER_SHADOW", Boolean.TRUE);
			player.applyHit(new Hit(this, Utils.random((int) (player.getMaxHitpoints() * 0.1)) + 1, HitLook.TRUE_DAMAGE));
		}
	}

	public void setUsedShadow() {
		for (Player player : getManager().getParty().getTeam()) {
			player.getTempAttribs().put("SHADOW_FORGER_SHADOW", Boolean.TRUE);
		}
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	@Override
	public void sendDrop(Player player, Item item) {
		List<Player> players = getManager().getParty().getTeam();
		if (players.size() == 0)
			return;
		player.getInventory().addItemDrop(item);
		player.sendMessage("<col=D2691E>You received: " + item.getAmount() + " " + item.getName() + ".");
		for (Player p2 : players) {
			if (p2 == player)
				continue;
			p2.sendMessage("<col=D2691E>" + player.getDisplayName() + " received: " + item.getAmount() + " " + item.getName() + ".");
		}
	}

}
