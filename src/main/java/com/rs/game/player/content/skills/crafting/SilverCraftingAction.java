package com.rs.game.player.content.skills.crafting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.skills.crafting.Silver.SilverItems;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

public class SilverCraftingAction extends Action {

	SilverItems itemToMake;
	int numberToMake;

	public SilverCraftingAction(SilverItems bling, int number) {
		this.itemToMake = bling;
		this.numberToMake = number;
	}

	public boolean checkAll(Player player) {
		if (itemToMake == null || player == null)
			return false;
		if (itemToMake.getMouldRequired() != -1 && !player.getInventory().containsItem(itemToMake.getMouldRequired(), 1)) {
			player.sendMessage("You need one " + ItemDefinitions.getDefs(itemToMake.getMouldRequired()).getName().toLowerCase() + " to make that.");
			return false;
		}
		if (!player.getInventory().containsItem(Silver.SILVER_BAR, 1)) {
			player.sendMessage("You don't have the items required to make that.");
			return false;
		}
		if (player.getSkills().getLevel(Constants.CRAFTING) < itemToMake.getLevelRequired()) {
			player.sendMessage("You need " + itemToMake.getLevelRequired() + " crafting to make that.");
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player))
			return true;
		return false;
	}

	@Override
	public boolean process(Player player) {
		if (checkAll(player)) {
			if (player.getTempAttribs().get("silverObject") != null)
				player.faceObject((GameObject) player.getTempAttribs().get("silverObject"));
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		numberToMake--;
		player.setNextAnimation(new Animation(3243));
		player.getSkills().addXp(Constants.CRAFTING, itemToMake.getExperience());
		player.getInventory().deleteItem(Silver.SILVER_BAR, 1);
		player.getInventory().addItem(itemToMake.getProduct());
		player.sendMessage("You make a " + itemToMake.getProduct().getDefinitions().getName().toLowerCase() + ".", true);

		if (numberToMake > 0) {
			return 2;
		}
		return -1;
	}

	@Override
	public void stop(Player player) {

	}

}
