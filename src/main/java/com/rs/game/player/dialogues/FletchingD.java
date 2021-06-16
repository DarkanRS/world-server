package com.rs.game.player.dialogues;

import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.content.skills.Fletching;
import com.rs.game.player.content.skills.Fletching.Fletch;

public class FletchingD extends Dialogue {

	private Fletch items;

	@Override
	public void start() {
		items = (Fletch) parameters[0];
		boolean maxQuantityTen = Fletching.maxMakeQuantityTen(items) && items.getProduct()[0] != 52;
		SkillsDialogue.sendSkillsDialogue(player, maxQuantityTen ? SkillsDialogue.MAKE_INTERVAL : SkillsDialogue.MAKE_ALL, "Choose how many you wish to make,<br>then click on the item to begin.", maxQuantityTen ? 10 : 28, items.getProduct(),
				maxQuantityTen ? null : new ItemNameFilter() {
					@Override
					public String rename(String name) {
						return name.replace(" (u)", "");
					}
				});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int option = SkillsDialogue.getItemSlot(componentId);
		if (option > items.getProduct().length) {
			end();
			return;
		}
		int quantity = SkillsDialogue.getQuantity(player);
		int invQuantity = player.getInventory().getItems().getNumberOf(items.getId());
		if (quantity > invQuantity)
			quantity = invQuantity;
		end();
		player.getActionManager().setAction(new Fletching(items, option, quantity));

	}

	@Override
	public void finish() {
	}

}
