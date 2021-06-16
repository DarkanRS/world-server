package com.rs.game.npc.familiar;

import com.rs.game.player.Player;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Compostmound extends Familiar {

	public Compostmound(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Generate Compost";
	}

	@Override
	public String getSpecialDescription() {
		return "Fill a nearby compost bin with compost, with a chance of creating super compost.";
	}

	@Override
	public int getBOBSize() {
		return 0;
	}

	@Override
	public int getSpecialAmount() {
		return 12;
	}

	@Override
	public SpecialAttack getSpecialAttack() {
		return SpecialAttack.CLICK;
	}

	@Override
	public boolean submitSpecial(Object object) {
		Player player = (Player) object;
		int newLevel = (int) (player.getSkills().getLevel(Constants.FARMING) + 1 + (Math.round(player.getSkills().getLevelForXp(Constants.FARMING) * .02)));
		if (newLevel > player.getSkills().getLevelForXp(Constants.FARMING) + 1 + (Math.round(player.getSkills().getLevelForXp(Constants.FARMING) * .02)))
			newLevel = (int) (player.getSkills().getLevelForXp(Constants.FARMING) + 1 + (Math.round(player.getSkills().getLevelForXp(Constants.FARMING) * .02)));
		/*
		 * if
		 * (object.getDefinitions().name.toLowerCase().contains("compost bin"))
		 * { WorldObject nextObject = new WorldObject(Utils.getRandom(100) == 0
		 * ? 13001: 13000, object.getType(), object.getRotation(),
		 * object.getX(), object.getY(), object.getPlane());
		 * World.spawnObject(nextObject, true); World.removeObject(object,
		 * true); }
		 */
		player.setNextSpotAnim(new SpotAnim(1300));
		player.setNextAnimation(new Animation(7660));
		player.getSkills().set(Constants.FARMING, newLevel);
		return true;
	}

}
