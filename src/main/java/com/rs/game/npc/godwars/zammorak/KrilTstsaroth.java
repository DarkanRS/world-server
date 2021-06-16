package com.rs.game.npc.godwars.zammorak;

import com.rs.cores.CoresManager;
import com.rs.game.npc.NPC;
import com.rs.game.npc.godwars.GodWarMinion;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class KrilTstsaroth extends NPC {

	private GodWarMinion[] minions = new GodWarMinion[3];

	public KrilTstsaroth(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
		setIntelligentRouteFinder(true);
		setIgnoreDocile(true);
		setForceAggroDistance(64);
		minions[0] = new GodWarMinion(6204, tile.transform(8, 4), spawned);
		minions[1] = new GodWarMinion(6206, tile.transform(-6, 6), spawned);
		minions[2] = new GodWarMinion(6208, tile.transform(-6, -2), spawned);
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
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(6203) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new KrilTstsaroth(npcId, tile, false);
		}
	};
}
