package com.rs.game.npc.godwars.zaros;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class NexMinion extends NPC {
	
	private NexArena arena;

	public NexMinion(NexArena arena, int id, WorldTile tile) {
		super(id, tile, true);
		this.arena = arena;
		setCantFollowUnderCombat(true);
		setCapDamage(0);
		setIgnoreDocile(true);
	}

	public void breakBarrier() {
		setCapDamage(-1);
	}
	
	@Override
	public boolean ignoreWallsWhenMeleeing() {
		return true;
	}

	@Override
	public void processNPC() {
		if (isDead())
			return;
		if (!getCombat().process())
			checkAggressivity();
	}
	
	@Override
	public void handlePreHit(Hit hit) {
		super.handlePreHit(hit);
		if (hit.getLook() != HitLook.RANGE_DAMAGE)
			hit.setDamage(0);
		if (getCapDamage() != -1)
			setNextSpotAnim(new SpotAnim(1549));
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
		arena.moveNextStage();
	}

}
