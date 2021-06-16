package com.rs.game.npc.familiar;

import com.rs.game.player.Player;
import com.rs.game.player.content.skills.summoning.Summoning.Pouches;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;

public class Granitecrab extends Familiar {

	public Granitecrab(Player owner, Pouches pouch, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(owner, pouch, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
	}

	@Override
	public String getSpecialName() {
		return "Stony Shell";
	}

	@Override
	public String getSpecialDescription() {
		return "Increases your restance to all attacks by four.";
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
		int newLevel = player.getSkills().getLevel(Constants.DEFENSE) + 4;
		if (newLevel > player.getSkills().getLevelForXp(Constants.DEFENSE) + 4)
			newLevel = player.getSkills().getLevelForXp(Constants.DEFENSE) + 4;
		player.setNextSpotAnim(new SpotAnim(1300));
		player.setNextAnimation(new Animation(7660));
		setNextSpotAnim(new SpotAnim(8108));
		setNextAnimation(new Animation(1326));
		player.getSkills().set(Constants.DEFENSE, newLevel);
		return true;
	}

}
