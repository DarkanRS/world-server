package com.rs.game.npc.godwars.armadyl;

import com.rs.cores.CoresManager;
import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.GodWarMinion;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerCombat;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KreeArra extends NPC {

	private GodWarMinion[] minions = new GodWarMinion[3];

	public KreeArra(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
		setForceAggroDistance(64);
		minions[0] = new GodWarMinion(6223, tile.transform(8, 0), spawned);
		minions[1] = new GodWarMinion(6225, tile.transform(-4, -2), spawned);
		minions[2] = new GodWarMinion(6227, tile.transform(-2, -4), spawned);
	}
	
	@Override
	public void onRespawn() {
		respawnMinions();
	}
	
	public void respawnMinions() {
		CoresManager.schedule(() -> {
			for (GodWarMinion minion : minions) {
				if (minion.hasFinished() || minion.isDead())
					minion.respawn();
			}
		}, 2);
	}
	
	@Override
	public boolean canBeAttackedBy(Player player) {
		if (!PlayerCombat.isRanging(player)) {
			player.sendMessage("Kree'arra is flying too high for you to attack using melee.");
			return false;
		}
		return true;
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6222) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new KreeArra(npcId, tile, false);
		}
	};
}
