package com.rs.game.player.dialogues;

import com.rs.game.player.Equipment;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class FlamingSkull extends Dialogue {

	private static final String COLORS[] = { "Green", "Purple", "Blue", "Red" };

	private Item item;
	private boolean worn;
	
	public static ItemClickHandler handleSwitchColor = new ItemClickHandler(new Object[] { 24437, 24439, 24440, 24441 }, new String[] { "Change-colour", "Change colour" }) {
		@Override
		public void handle(ItemClickEvent e) {
			e.getPlayer().getDialogueManager().execute(new FlamingSkull(), e.getItem(), e.isEquipped());
		}
	};

	@Override
	public void start() {
		item = (Item) parameters[0];
		worn = (Boolean) parameters[1];
		int index = (item.getId() == 24437 ? 24442 : item.getId()) - 24439;
		sendOptionsDialogue("What colour do you want your skull to be?", COLORS[(index + 1) % 4], COLORS[(index + 2) % 4], COLORS[(index + 3) % 4]);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int index = (item.getId() == 24437 ? 24442 : item.getId()) - 24439;
		int option;
		if (componentId == OPTION_1)
			option = 1;
		else if (componentId == OPTION_2)
			option = 2;
		else
			option = 3;
		int itemId = 24439 + ((index + option) % 4);
		item.setId(itemId == 24442 ? 24437 : itemId);
		if (worn) {
			player.getEquipment().refresh(Equipment.HEAD);
			player.getAppearance().generateAppearanceData();
		} else
			player.getInventory().refresh(item.getSlot());
		end();
	}

	@Override
	public void finish() {

	}
}
