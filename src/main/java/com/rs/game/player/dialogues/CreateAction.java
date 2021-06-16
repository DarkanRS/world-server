package com.rs.game.player.dialogues;

import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;

public class CreateAction extends Action {

	private int[] anims;
	private Item[][] materials;
	private Item[][] products;
	private double[] xp;
	private int[] reqs;
	private int skill;
	private int delay;
	private int choice;
	private int quantity = -1;

	public CreateAction(Item[][] materials, Item[][] products, double[] xp, int[] anim, int[] reqs, int skill, int delay, int choice) {
		this.anims = anim;
		this.materials = materials;
		this.products = products;
		this.xp = xp;
		this.skill = skill;
		this.delay = delay;
		this.choice = choice;
		this.reqs = reqs;
	}
	
	public CreateAction setQuantity(int quantity) {
		this.quantity = quantity;
		return this;
	}

	public boolean checkAll(Player player) {
		if (choice >= materials.length)
			return false;
		if (!player.getInventory().containsItems(materials[choice]) || !player.getInventory().hasRoomFor(materials[choice], products[choice]))
			return false;
		if (reqs != null) {
			if (player.getSkills().getLevel(skill) < reqs[choice]) {
				player.sendMessage("You need a " + Constants.SKILL_NAME[skill] + " level of " + reqs[choice] + " to make a " + products[choice][0].getName() + ".");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		return checkAll(player);
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		if (quantity != -1) {
			quantity--;
			if (quantity < 0)
				return -1;
		}
		if (!player.getInventory().hasRoomFor(materials[choice], products[choice])) {
			player.sendMessage("You don't have enough inventory space.");
			return -1;
		} else {
			if (anims != null)
				player.setNextAnimation(new Animation(anims[choice]));
			for (int i = 0; i < materials[choice].length; i++)
				player.getInventory().deleteItem(materials[choice][i]);
			for (int i = 0; i < products[choice].length; i++)
				player.getInventory().addItemDrop(products[choice][i]);
			if (xp != null && skill != -1)
				player.getSkills().addXp(skill, xp[choice]);
		}
		return delay;
	}

	@Override
	public void stop(Player player) {

	}

}
