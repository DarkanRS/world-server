package com.rs.game.player.content.minigames.shadesofmortton;

import com.rs.game.Entity;
import com.rs.game.npc.NPC;
import com.rs.game.pathing.ClipType;
import com.rs.game.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class Shade extends NPC {
	
	private int baseId;
	private int attack;

	public Shade(int id, WorldTile tile) {
		super(id, tile);
		this.baseId = id;
		this.setForceAggroDistance(15);
		this.setClipType(ClipType.FLYING);
		this.setNoDistanceCheck(true);
		attack = 0;
	}
	
	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(1240, 1241, 1243, 1244, 1245, 1246, 1247, 1248, 1249, 1250) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new Shade(npcId, tile);
		}
	};
	
	@Override
	public void onRespawn() {
		transformIntoNPC(baseId);
	}
	
	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		if (source instanceof Player player)
			ShadesOfMortton.addSanctity(player, 2.0);
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead() || hasFinished())
			return;
		if (!inCombat(10000) && getRegionId() == 13875 && ShadesOfMortton.getRepairState() > 0) {
			if (withinArea(3503, 3313, 3509, 3319)) {
				if (getId() == baseId) {
					transformIntoNPC(baseId+1);
					setNextAnimation(new Animation(1288));
				} else {
					resetWalkSteps();
					if (attack-- <= 0) {
						attack = 5;
						faceTile(new WorldTile(3506, 3316, 0));
						setNextAnimation(new Animation(1284));
						TempleWall wall = ShadesOfMortton.getRandomWall();
						if (wall != null)
							wall.decreaseProgress();
					}
				}
				return;
			} else {
				if (Utils.random(10) == 0)
					calcFollow(new WorldTile(new WorldTile(3506, 3316, 0), 4), false);
			}
		}
		if (getId() == baseId && inCombat(10000)) {
			transformIntoNPC(baseId + 1);
			setNextAnimation(new Animation(1288));
		} else if (getId() != baseId && !inCombat(10000)) {
			transformIntoNPC(baseId);
			resetHP();
		}
	}

}
