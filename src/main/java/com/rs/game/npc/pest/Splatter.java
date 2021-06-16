package com.rs.game.npc.pest;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.content.minigames.pest.PestControl;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class Splatter extends PestMonsters {

    public Splatter(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, int index, PestControl manager) {
	super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, index, manager);
    }

    @Override
    public void processNPC() {
	super.processNPC();
    }

    private void sendExplosion() {
	final Splatter splatter = this;
	setNextAnimation(new Animation(3888));
	WorldTasksManager.schedule(new WorldTask() {

	    @Override
	    public void run() {
		setNextAnimation(new Animation(3889));
		setNextSpotAnim(new SpotAnim(649 + (getId() - 3727)));
		WorldTasksManager.schedule(new WorldTask() {

		    @Override
		    public void run() {
			finish();
			for (Entity e : getPossibleTargets())
			    if (e.withinDistance(splatter, 2))
				e.applyHit(new Hit(splatter, Utils.getRandomInclusive(400), HitLook.TRUE_DAMAGE));
		    }
		});
	    }
	});
    }

    @Override
    public void sendDeath(Entity source) {
	final NPCCombatDefinitions defs = getCombatDefinitions();
	resetWalkSteps();
	getCombat().removeTarget();
	setNextAnimation(null);
	WorldTasksManager.schedule(new WorldTask() {
	    int loop;

	    @Override
	    public void run() {
		if (loop == 0)
		    sendExplosion();
		else if (loop >= defs.getDeathDelay()) {
		    reset();
		    stop();
		}
		loop++;
	    }
	}, 0, 1);
    }
}
