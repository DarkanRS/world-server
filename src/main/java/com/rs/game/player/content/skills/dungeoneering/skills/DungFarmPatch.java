package com.rs.game.player.content.skills.dungeoneering.skills;

import com.rs.game.object.GameObject;
import com.rs.game.object.OwnedObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.skills.dungeoneering.DungeonConstants;
import com.rs.game.player.content.skills.dungeoneering.DungeonManager;
import com.rs.game.player.content.skills.dungeoneering.skills.DungeoneeringFarming.Harvest;

public class DungFarmPatch extends OwnedObject {
	
	private int time;
	private int stage = 1;
	private Harvest harvest;
	private DungeonManager manager;

	public DungFarmPatch(Player player, Harvest harvest, GameObject basePatch, DungeonManager manager) {
		super(player, DungeonConstants.EMPTY_FARMING_PATCH + 1 + 1 + (harvest.ordinal() * 3), basePatch.getType(), basePatch.getRotation(), basePatch);
		this.harvest = harvest;
		this.manager = manager;
	}
	
	@Override
	public void onDestroy() {
		manager.getFarmingPatches().remove(this);
	}
	
	@Override
	public void tick(Player owner) {
		time++;
		if (time >= 50 && stage < 3) {
			time = 0;
			stage++;
			setId(DungeonConstants.EMPTY_FARMING_PATCH + 1 + stage + (harvest.ordinal() * 3));
		}
	}
}
