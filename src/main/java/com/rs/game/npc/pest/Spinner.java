package com.rs.game.npc.pest;

import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.minigames.pest.PestControl;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Spinner extends PestMonsters {

    private byte healTicks;

    public Spinner(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, int index, PestControl manager) {
	super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
    }

    @Override
    public void processNPC() {
	PestPortal portal = manager.getPortals()[portalIndex];
	if (portal.isDead()) {
	    explode();
	    return;
	}
	if (!portal.isLocked) {
	    healTicks++;
	    if (!withinDistance(portal, 1))
		this.addWalkSteps(portal.getX(), portal.getY());
	    else if (healTicks % 6 == 0)
		healPortal(portal);
	}
    }

    private void healPortal(final PestPortal portal) {
	setNextFaceEntity(portal);
	WorldTasksManager.schedule(new WorldTask() {

	    @Override
	    public void run() {
		setNextAnimation(new Animation(3911));
		setNextSpotAnim(new SpotAnim(658, 0, 96 << 16));
		if (portal.getHitpoints() != 0)
		    portal.heal((portal.getMaxHitpoints() / portal.getHitpoints()) * 45);
		healTicks = 0; /* Saves memory in the long run. Meh */
	    }
	});
    }

    private void explode() {
	final NPC npc = this;
	WorldTasksManager.schedule(new WorldTask() {

	    @Override
	    public void run() {
		for (Player player : manager.getPlayers()) {
		    if (!withinDistance(player, 7))
			continue;
		    player.getPoison().makePoisoned(50);
		    player.applyHit(new Hit(npc, 50, HitLook.TRUE_DAMAGE));
		    npc.reset();
		    npc.finish();
		}
	    }
	}, 1);
    }
}
