package com.rs.game.player.content.pet;

import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public class Incubator {

	public static enum Egg {
		PEGUIN_EGG(30, 12483, 12481), 
		RAVEN_EGG(50, 11964, 12484), 
		BIRD_EGG_ZAMMY(70, 5076, 12506), 
		BIRD_EGG_SARA(70, 5077, 12503), 
		BIRD_EGG_GUTHIX(70, 5078, 12509), 
		VULTURE_EGG(85, 11965, 12498), 
		CHAMELEON_EGG(90, 12494, 12492), 
		RED_DRAGON_EGG(99, 12477, 12469), 
		BLUE_DRAGON_EGG(99, 12478, 12471), 
		GREEN_DRAGON_EGG(99, 12479, 12473), 
		BLACK_DRAGON_EGG(99, 12480, 12475);
		
		private int summoningLevel, eggId, petId;

		private Egg(int summoningLevel, int eggId, int petId) {
			this.summoningLevel = summoningLevel;
			this.eggId = eggId;
			this.petId = petId;
		}
	}

	public static Egg getEgg(int itemId) {
		for (Egg egg : Egg.values())
			if (egg.eggId == itemId)
				return egg;
		return null;
	}

	public static boolean useEgg(Player player, int itemId) {
		Egg egg = getEgg(itemId);
		if (egg == null)
			return false;
		if (player.getSkills().getLevelForXp(Constants.SUMMONING) < egg.summoningLevel) {
			player.sendMessage("You need a level of " + egg.summoningLevel + " summoning to hatch this egg.");
			return true;
		}
		player.lock(1);
		player.setNextAnimation(new Animation(833));
		player.getInventory().deleteItem(itemId, 1);
		player.getInventory().addItem(egg.petId, 1);
		player.sendMessage("You put the egg in the incubator and it hatches.");
		return true;
	}
}
