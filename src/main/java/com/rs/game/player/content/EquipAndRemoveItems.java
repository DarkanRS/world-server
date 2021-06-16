package com.rs.game.player.content;

import com.rs.game.player.Equipment;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class EquipAndRemoveItems  {
	
	public static ItemClickHandler handle = new ItemClickHandler(new String[] { "Wear", "Remove", "Wield" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch(e.getOption()) {
			case "Remove":
				Equipment.sendRemove(e.getPlayer(), Equipment.getItemSlot(e.getItem().getId()));
				break;
			case "Wear":
			case "Wield":
				if (e.getPlayer().isEquipDisabled())
					return;
				if (!EnchantedHeadwear.canEquip(e.getItem().getId(), e.getPlayer()))
					return;
				Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId());
				break;
			}
		}
	};

}
