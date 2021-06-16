package com.rs.game.npc.pestqueen;

import com.rs.game.npc.NPC;
import com.rs.game.npc.pestqueen.attack.Attack;
import com.rs.game.npc.pestqueen.attack.impl.MeleeAttack;
import com.rs.lib.game.WorldTile;

/**
 * 
 * @author Tyler
 * 
 */
public class PestQueen extends NPC {

	public Attack currentAttack;

	public PestQueen(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile);
		this.setHitpoints(20000);
		this.setCombatLevel(599);
		this.currentAttack = new MeleeAttack();
	}

	@Override
	public void processNPC() {

	}
}
