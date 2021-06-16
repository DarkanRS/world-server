package com.rs.game.player.content.skills.dungeoneering.skills;

import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public enum DungPickaxe {
	NOVITE(16295, 1, 13074, 1),
	BATHUS(16297, 10, 13075, 3),
	MARMAROS(16299, 20, 13076, 5),
	KRATONITE(16301, 30, 13077, 7),
	FRACTITE(16303, 40, 13078, 10),
	ZEPHYRIUM(16305, 50, 13079, 12),
	ARGONITE(16307, 60, 13080, 13),
	KATAGON(16309, 70, 13081, 15),
	GORGONITE(16311, 80, 13082, 16),
	PROMETHIUM(16313, 90, 13083, 17),
	PRIMAL(16315, 99, 13084, 20);
	
	private int itemId, level, ticks;
	private Animation animation;

	private DungPickaxe(int itemId, int level, int animId, int ticks) {
		this.itemId = itemId;
		this.level = level;
		this.animation = new Animation(animId);
		this.ticks = ticks;
	}
	
	public int getItemId() {
		return itemId;
	}

	public int getLevel() {
		return level;
	}

	public int getTicks() {
		return ticks;
	}

	public Animation getAnimation() {
		return animation;
	}
	
	public static DungPickaxe getBest(Player player) {
		for (int i = DungPickaxe.values().length-1; i >= 0; i--) {
			DungPickaxe def = DungPickaxe.values()[i];
			if (player.getInventory().containsItem(def.itemId) || player.getEquipment().getWeaponId() == def.itemId) {
				if (player.getSkills().getLevel(Constants.MINING) >= def.level)
					return def;
			}
		}
		return null;
	}
}
