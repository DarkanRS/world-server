package com.rs.game.player.content.skills.magic;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.Player;
import com.rs.lib.game.Item;

public class RuneSet {
	
	private Rune[] runes;
	private int[] amounts;
	
	public RuneSet() {
		
	}
	
	public RuneSet(Rune r1, int num1) {
		runes = new Rune[] { r1 };
		amounts = new int[] { num1 };
	}
	
	public RuneSet(Rune r1, int num1, Rune r2, int num2) {
		runes = new Rune[] { r1, r2 };
		amounts = new int[] { num1, num2 };
	}
	
	public RuneSet(Rune r1, int num1, Rune r2, int num2, Rune r3, int num3) {
		runes = new Rune[] { r1, r2, r3 };
		amounts = new int[] { num1, num2, num3 };
	}
	
	public RuneSet(Rune r1, int num1, Rune r2, int num2, Rune r3, int num3, Rune r4, int num4) {
		runes = new Rune[] { r1, r2, r3, r4 };
		amounts = new int[] { num1, num2, num3, num4 };
	}

	public boolean meetsRequirements(Player player) {
		if (runes == null)
			return true;
		for (int i = 0;i < runes.length;i++) {
			if (runes[i].getRunesToDelete(player, amounts[i]) == null) {
				player.sendMessage("You don't have enough " + runes[i].toString().toLowerCase() + " runes to cast this spell.");
				return false;
			}
		}
		return true;
	}
	
	public boolean meetsPortalRequirements(Player player) {
		if (runes == null)
			return true;
		for (int i = 0;i < runes.length;i++) {
			if (runes[i].getRunesToDelete(player, amounts[i]) == null) {
				player.sendMessage("You don't have enough " + runes[i].toString().toLowerCase() + " runes to tune your portal to this location.");
				return false;
			}
		}
		return true;
	}
	
	public List<Item> getRunesToDelete(Player player) {
		List<Item> toDelete = new ArrayList<>();
		if (runes == null)
			return toDelete;
		for (int i = 0;i < runes.length;i++) {
			List<Item> rDel = runes[i].getRunesToDelete(player, amounts[i]);
			if (rDel != null)
				toDelete.addAll(rDel);
		}
		return toDelete;
	}

	public void deleteRunes(Player caster) {
		List<Item> toDelete = getRunesToDelete(caster);
		for (Item i : toDelete) {
			if (i != null)
				caster.getInventory().deleteItem(i);
		}
	}
}
