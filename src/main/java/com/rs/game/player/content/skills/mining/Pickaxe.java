package com.rs.game.player.content.skills.mining;

import com.rs.game.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public enum Pickaxe {
	BRONZE(1265, 1, 625, 8),
	BRONZE_G(20780, 1, 234, 8),
	IRON(1267, 1, 626, 7),
	IRON_G(20781, 1, 235, 7),
	STEEL(1269, 6, 627, 6),
	STEEL_G(20782, 6, 236, 6),
	MITHRIL(1273, 21, 629, 5),
	MITHRIL_G(20784, 21, 238, 5),
	ADAMANT(1271, 31, 628, 4),
	ADAMANT_G(20783, 31, 237, 4),
	RUNE(1275, 41, 624, 3),
	RUNE_G(20785, 41, 249, 3),
	DRAGON(15259, 61, 12189, 3),
	DRAGON_G(20786, 61, 250, 3),
	INFERNO_ADZE(13661, 61, 10222, 3);
	
	private int itemId, level, ticks;
	private Animation animation;
	
	private Pickaxe(int itemId, int level, int animId, int ticks) {
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
	
	public static Pickaxe getBest(Player player) {
		for (int i = Pickaxe.values().length-1; i >= 0; i--) {
			Pickaxe def = Pickaxe.values()[i];
			if (player.getInventory().containsItem(def.itemId) || player.getEquipment().getWeaponId() == def.itemId) {
				if (player.getSkills().getLevel(Constants.MINING) >= def.level)
					return def;
			}
		}
		return null;
	}
}
