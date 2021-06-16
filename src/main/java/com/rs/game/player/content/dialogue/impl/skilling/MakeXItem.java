package com.rs.game.player.content.dialogue.impl.skilling;

import com.rs.game.player.Player;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.dialogues.CreateAction;
import com.rs.lib.game.Item;

public class MakeXItem extends Dialogue {
	
	private int itemId;
		
	public MakeXItem(Player player, Item[] materials, Item[] products, double xp, int anim, int req, int skill, int delay) {
		this.itemId = products[0].getId();
		this.setFunc(() -> {
			int quantity = SkillsDialogue.getQuantity(player);
			for (Item mat : materials) {
				int newQ = player.getInventory().getNumberOf(mat.getId()) / mat.getAmount();
				if (newQ < quantity)
					quantity = newQ;
			}
			player.getActionManager().setAction(new CreateAction(new Item[][] { materials }, new Item[][] { products }, new double [] { xp }, new int[] { anim },  new int[] { req }, skill, delay, 0).setQuantity(quantity));
		});
	}
	
	public MakeXItem(Player player, Item material, Item product, double xp, int anim, int req, int skill, int delay) {
		this(player, new Item[] { material }, new Item[] { product }, xp, anim, req, skill, delay);
	}

	public int getItemId() {
		return itemId;
	}
}
