package com.rs.game.player.content.skills.magic;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.game.player.managers.InterfaceManager.Tab;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;

public class Enchanting {

	public enum Sapphire {

		RING(1637, 2550), NECKLACE(1656, 3853), AMULET(1694, 1727), BRACELET(11072, 11074);

		private static Map<Integer, Sapphire> sapJewelry = new HashMap<Integer, Sapphire>();

		public static Sapphire forId(int unenchanted) {
			return sapJewelry.get(unenchanted);
		}

		static {
			for (Sapphire ring : Sapphire.values()) {
				sapJewelry.put(ring.getUnenchanted(), ring);
			}
		}

		private int unenchanted;
		private int enchanted;

		private Sapphire(int unenchanted, int enchanted) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}
	};

	public enum Emerald {

		RING(1639, 2552), NECKLACE(1658, 5521), AMULET(1696, 1729), BRACELET(11076, 11079);

		private static Map<Integer, Emerald> sapJewelry = new HashMap<Integer, Emerald>();

		public static Emerald forId(int unenchanted) {
			return sapJewelry.get(unenchanted);
		}

		static {
			for (Emerald ring : Emerald.values()) {
				sapJewelry.put(ring.getUnenchanted(), ring);
			}
		}

		private int unenchanted;
		private int enchanted;

		private Emerald(int unenchanted, int enchanted) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}
	};

	public enum Ruby {

		RING(1641, 2568), NECKLACE(1660, 11194), AMULET(1698, 1725), BRACELET(11085, 11088);

		private static Map<Integer, Ruby> sapJewelry = new HashMap<Integer, Ruby>();

		public static Ruby forId(int unenchanted) {
			return sapJewelry.get(unenchanted);
		}

		static {
			for (Ruby ring : Ruby.values()) {
				sapJewelry.put(ring.getUnenchanted(), ring);
			}
		}

		private int unenchanted;
		private int enchanted;

		private Ruby(int unenchanted, int enchanted) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}
	};

	public enum Diamond {

		RING(1643, 2570), NECKLACE(1662, 11090), AMULET(1700, 1731), BRACELET(11092, 11095);

		private static Map<Integer, Diamond> sapJewelry = new HashMap<Integer, Diamond>();

		public static Diamond forId(int unenchanted) {
			return sapJewelry.get(unenchanted);
		}

		static {
			for (Diamond ring : Diamond.values()) {
				sapJewelry.put(ring.getUnenchanted(), ring);
			}
		}

		private int unenchanted;
		private int enchanted;

		private Diamond(int unenchanted, int enchanted) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}
	};

	public enum Dragonstone {

		RING(1645, 20659), NECKLACE(1664, 11105), AMULET(1702, 1712), BRACELET(11115, 11118);

		private static Map<Integer, Dragonstone> sapJewelry = new HashMap<Integer, Dragonstone>();

		public static Dragonstone forId(int unenchanted) {
			return sapJewelry.get(unenchanted);
		}

		static {
			for (Dragonstone ring : Dragonstone.values()) {
				sapJewelry.put(ring.getUnenchanted(), ring);
			}
		}

		private int unenchanted;
		private int enchanted;

		private Dragonstone(int unenchanted, int enchanted) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}
	};

	public enum Onyx {

		RING(6575, 6583), NECKLACE(6577, 11128), AMULET(6581, 6585), BRACELET(11130, 11133);

		private static Map<Integer, Onyx> sapJewelry = new HashMap<Integer, Onyx>();

		public static Onyx forId(int unenchanted) {
			return sapJewelry.get(unenchanted);
		}

		static {
			for (Onyx ring : Onyx.values()) {
				sapJewelry.put(ring.getUnenchanted(), ring);
			}
		}

		private int unenchanted;
		private int enchanted;

		private Onyx(int unenchanted, int enchanted) {
			this.unenchanted = unenchanted;
			this.enchanted = enchanted;
		}

		public int getUnenchanted() {
			return unenchanted;
		}

		public int getEnchanted() {
			return enchanted;
		}
	};

	public static void handleEnchanting(Player player, Item item, int comp1) {
		player.getInterfaceManager().openGameTab(Tab.MAGIC);
		if (!player.canCastSpell()) {
			return;
		}
		switch (comp1) {
		// Sapphire
		case 29: {
			Sapphire sapphire = Sapphire.forId(item.getId());
			if (sapphire != null) {
				if (Magic.checkMagicAndRunes(player, 7, true, new RuneSet(Rune.COSMIC, 1, Rune.WATER, 1))) {
					player.setNextAnimation(new Animation(719));
					player.setNextSpotAnim(new SpotAnim(114, 0, 100));
					player.getInventory().deleteItem(sapphire.getUnenchanted(), 1);
					player.getInventory().addItem(sapphire.getEnchanted(), 1);
					player.getSkills().addXp(Constants.MAGIC, 17);
					player.addSpellDelay(2);
				}
			} else {
				player.sendMessage("That item cannot be enchanted with this spell.");
			}
		}
			break;
		// Emerald
		case 41: {
			Emerald emerald = Emerald.forId(item.getId());
			if (emerald != null) {
				if (Magic.checkMagicAndRunes(player, 27, true, new RuneSet(Rune.COSMIC, 1, Rune.AIR, 3))) {
					player.setNextAnimation(new Animation(719));
					player.setNextSpotAnim(new SpotAnim(114, 0, 100));
					player.getInventory().deleteItem(emerald.getUnenchanted(), 1);
					player.getInventory().addItem(emerald.getEnchanted(), 1);
					player.getSkills().addXp(Constants.MAGIC, 37);
					player.addSpellDelay(2);
				}
			} else {
				player.sendMessage("That item cannot be enchanted with this spell.");
			}
		}
			break;
		// Ruby
		case 53: {
			Ruby ruby = Ruby.forId(item.getId());
			if (ruby != null) {
				if (Magic.checkMagicAndRunes(player, 49, true, new RuneSet(Rune.COSMIC, 1, Rune.FIRE, 5))) {
					player.setNextAnimation(new Animation(719));
					player.setNextSpotAnim(new SpotAnim(114, 0, 100));
					player.getInventory().deleteItem(ruby.getUnenchanted(), 1);
					player.getInventory().addItem(ruby.getEnchanted(), 1);
					player.getSkills().addXp(Constants.MAGIC, 59);
					player.addSpellDelay(2);
				}
			} else {
				player.sendMessage("That item cannot be enchanted with this spell.");
			}
		}
			break;
		// Diamond
		case 61: {
			Diamond diamond = Diamond.forId(item.getId());
			if (diamond != null) {
				if (Magic.checkMagicAndRunes(player, 57, true, new RuneSet(Rune.COSMIC, 1, Rune.EARTH, 10))) {
					player.setNextAnimation(new Animation(719));
					player.setNextSpotAnim(new SpotAnim(114, 0, 100));
					player.getInventory().deleteItem(diamond.getUnenchanted(), 1);
					player.getInventory().addItem(diamond.getEnchanted(), 1);
					player.getSkills().addXp(Constants.MAGIC, 67);
					player.addSpellDelay(2);
				}
			} else {
				player.sendMessage("That item cannot be enchanted with this spell.");
			}
		}
			break;
		// Dragonstone
		case 76: {
			Dragonstone dragonstone = Dragonstone.forId(item.getId());
			if (dragonstone != null) {
				if (Magic.checkMagicAndRunes(player, 68, true, new RuneSet(Rune.COSMIC, 1, Rune.EARTH, 15, Rune.WATER, 15))) {
					player.setNextAnimation(new Animation(719));
					player.setNextSpotAnim(new SpotAnim(114, 0, 100));
					player.getInventory().deleteItem(dragonstone.getUnenchanted(), 1);
					player.getInventory().addItem(dragonstone.getEnchanted(), 1);
					player.getSkills().addXp(Constants.MAGIC, 78);
					player.addSpellDelay(2);
				}
			} else {
				player.sendMessage("That item cannot be enchanted with this spell.");
			}
		}
			break;
		// Onyx
		case 88: {
			Onyx onyx = Onyx.forId(item.getId());
			if (onyx != null) {
				if (Magic.checkMagicAndRunes(player, 87, true, new RuneSet(Rune.COSMIC, 1, Rune.FIRE, 20, Rune.EARTH, 20))) {
					player.setNextAnimation(new Animation(719));
					player.setNextSpotAnim(new SpotAnim(114, 0, 100));
					player.getInventory().deleteItem(onyx.getUnenchanted(), 1);
					player.getInventory().addItem(onyx.getEnchanted(), 1);
					player.getSkills().addXp(Constants.MAGIC, 97);
					player.addSpellDelay(2);
				}
			} else {
				player.sendMessage("That item cannot be enchanted with this spell.");
			}
		}
			break;
		}
	}

}
