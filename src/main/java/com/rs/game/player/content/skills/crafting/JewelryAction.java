package com.rs.game.player.content.skills.crafting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.skills.crafting.Jewelry.Bling;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class JewelryAction extends Action {

	Bling bling;
	int numberToMake;

	public JewelryAction(Bling bling, int number) {
		this.bling = bling;
		this.numberToMake = number;
	}

	public boolean checkAll(Player player) {
		if (bling == null || player == null)
			return false;
		if (!player.getInventory().containsItem(bling.getMouldRequired().getId(), 1)) {
			player.sendMessage("You need one " + ItemDefinitions.getDefs(bling.getMouldRequired().getId()).getName().toLowerCase() + " to make that.");
			return false;
		}
		if (!player.getInventory().containsItems(bling.getItemsRequired())) {
			player.sendMessage("You don't have the items required to make that.");
			return false;
		}
		if (player.getSkills().getLevel(Constants.CRAFTING) < bling.getLevelRequired()) {
			player.sendMessage("You need " + bling.getLevelRequired() + " crafting to make that.");
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
			if (player.getTempAttribs().getO("jewelryObject") != null)
				player.faceObject(player.getTempAttribs().getO("jewelryObject"));
			return true;
		}
		return false;
	}

	@Override
	public int processWithDelay(Player player) {
		numberToMake--;
		player.setNextAnimation(new Animation(3243));
		player.getSkills().addXp(Constants.CRAFTING, bling.getExperience());
		for (Item required : bling.getItemsRequired()) {
			player.getInventory().deleteItem(required.getId(), required.getAmount());
		}
		player.getInventory().addItem(bling.getProduct());
		player.sendMessage("You make a " + bling.getProduct().getDefinitions().getName().toLowerCase() + ".", true);

		if (numberToMake > 0) {
			return 2;
		}
		return -1;
	}

	@Override
	public void stop(Player player) {

	}

}
