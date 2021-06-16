package com.rs.game.player.content.skills.woodcutting;

import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public enum Hatchet {
	BRONZE(1351, 1, 0.5, new Animation(879)),
	IRON(1349, 1, 0.6, new Animation(877)),
	STEEL(1353, 6, 0.7, new Animation(875)),
	BLACK(1361, 6, 0.7, new Animation(873)),
	MITHRIL(1355, 21, 0.8, new Animation(871)),
	ADAMANT(1357, 31, 1.0, new Animation(869)),
	RUNE(1359, 41, 1.2, new Animation(867)),
	DRAGON(6739, 61, 1.3, new Animation(2846)),
	INFERNO(13661, 61, 1.3, new Animation(10251));

	private int itemId, useLevel;
	private Animation animation;
	private double toolMod;

	private Hatchet(int itemId, int useLevel, double toolMod, Animation animation) {
		this.itemId = itemId;
		this.useLevel = useLevel;
		this.toolMod = toolMod;
		this.animation = animation;
	}

	public int getItemId() {
		return itemId;
	}

	public int getUseLevel() {
		return useLevel;
	}

	public double getToolMod() {
		return toolMod;
	}

	public Animation getAnim() {
		return animation;
	}
	
	public static Hatchet getBest(Player player) {
		for (int i = Hatchet.values().length-1; i >= 0; i--) {
			Hatchet def = Hatchet.values()[i];
			if (player.getInventory().containsItem(def.itemId) || player.getEquipment().getWeaponId() == def.itemId) {
				if (player.getSkills().getLevel(Constants.WOODCUTTING) >= def.useLevel)
					return def;
			}
		}
		return null;
	}
}
