package com.rs.game.npc.fightpits;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.content.minigames.FightPits;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class TzKekPits extends FightPitsNPC {

	public TzKekPits(int id, WorldTile tile) {
		super(id, tile);
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		final WorldTile tile = this;
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
					setNextSpotAnim(new SpotAnim(2924 + getSize()));
				} else if (loop >= defs.getDeathDelay()) {
					reset();
					FightPits.addNPC(new FightPitsNPC(2738, tile));
					if (World.floorAndWallsFree(getPlane(), tile.getX() + 1, tile.getY(), 1))
						tile.moveLocation(1, 0, 0);
					else if (World.floorAndWallsFree(getPlane(), tile.getX() - 1, tile.getY(), 1))
						tile.moveLocation(-1, 0, 0);
					else if (World.floorAndWallsFree(getPlane(), tile.getX(), tile.getY() - 1, 1))
						tile.moveLocation(0, -1, 0);
					else if (World.floorAndWallsFree(getPlane(), tile.getX(), tile.getY() + 1, 1))
						tile.moveLocation(0, 1, 0);
					FightPits.addNPC(new FightPitsNPC(2738, tile));
					finish();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		if (hit.getLook() != HitLook.MELEE_DAMAGE || hit.getSource() == null)
			return;
		hit.getSource().applyHit(new Hit(this, 10, HitLook.TRUE_DAMAGE));
	}
}
