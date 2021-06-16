package com.rs.game.npc.dungeoneering;

import com.rs.game.Hit;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.DungeonUtils;
import com.rs.game.player.content.skills.dungeoneering.RoomReference;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;

public class WarpedGulega extends DungeonBoss {

	public WarpedGulega(WorldTile tile, DungeonManager manager, RoomReference reference) {
		super(DungeonUtils.getClosestToCombatLevel(Utils.range(12737, 12751), manager.getBossLevel()), tile, manager, reference);
	}

	//thats default lol
	/* @Override
	 public double getMeleePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }
	 
	 @Override
	 public double getRangePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }
	 
	 @Override
	 public double getMagePrayerMultiplier() {
	return 0.0;//Fully block it.
	 }*/

	@Override
	public void processHit(Hit hit) {
		if (!(hit.getSource() instanceof Familiar))
			hit.setDamage((int) (hit.getDamage() * 0.45D));
		super.processHit(hit);
	}
}
