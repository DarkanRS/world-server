package com.rs.game.player.content.skills.smithing;

import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@PluginEventHandler
public class CoalBag {
	
	public static ItemClickHandler handleClickOnCoalBag = new ItemClickHandler(18339) {
		@Override
		public void handle(ItemClickEvent e) {
			int current = e.getPlayer().getI("coalBag");
			if (current == -1)
				e.getPlayer().save("coalBag", 0);
			if (current == -1 || current == 0) {
				e.getPlayer().sendMessage("You do not have any coal in your coal bag.");
				return;
			}
			
			switch(e.getOption()) {
			case "Inspect":
				e.getPlayer().sendMessage("You have " + current + " coal in your bag.");
				break;
			case "Withdraw-one":	
				if (!e.getPlayer().getInventory().hasFreeSlots()) {
					e.getPlayer().sendMessage("You do not have enough inventory spaces to do that.");
					return;
				}
				if (current > 1) {
					e.getPlayer().getInventory().addItem(453, 1);
					e.getPlayer().save("coalBag", current-1);
					e.getPlayer().sendMessage("You withdraw a coal from your bag.");
				}
				break;
			case "Withdraw-many":
				if (!e.getPlayer().getInventory().hasFreeSlots()) {
					e.getPlayer().sendMessage("You do not have enough inventory spaces to do that.");
					return;
				}
				int withdraw = (current > e.getPlayer().getInventory().getFreeSlots() ? e.getPlayer().getInventory().getFreeSlots() : current);
				e.getPlayer().getInventory().addItem(453, withdraw);
				e.getPlayer().save("coalBag", current-withdraw);
				e.getPlayer().sendMessage("You withdraw " + withdraw + " coal from your bag.");
				break;
			}
		}
	};
	
	public static ItemOnItemHandler handleItemOnCoalBag = new ItemOnItemHandler(18339, 453) {
		@Override
		public void handle(ItemOnItemEvent e) {
			int current = e.getPlayer().getI("coalBag");
			
			if (current == -1)
				e.getPlayer().save("coalBag", 0);
			
			int room = 0;
			
			if (current < 27)
				room = 27 - current;
			
			if (room == 0) {
				e.getPlayer().sendMessage("Your coal bag is already full.");
				return;
			}
				
			int coalToStore = e.getPlayer().getInventory().getNumberOf(453);
			if (coalToStore > room)
				coalToStore = room;
			
			e.getPlayer().getInventory().deleteItem(453, coalToStore);
			e.getPlayer().save("coalBag", current+coalToStore);
			e.getPlayer().sendMessage("You store " + coalToStore + " in your coal bag.");
		}
	};
	
	public static ItemOnObjectHandler handleCoalBagOnObject = new ItemOnObjectHandler(new Object[] { "Bank", "Deposit Box", "Counter" }) {
		@Override
		public void handle(ItemOnObjectEvent e) {
			if (e.getItem().getId() != 18339)
				return;
			if (e.isAtObject()) {
				if (ObjectDefinitions.getDefs(e.getObject().getId()).getName().contains("Bank") || ObjectDefinitions.getDefs(e.getObject().getId()).containsOptionIgnoreCase("bank")) {
					e.getPlayer().startConversation(new Dialogue().addOptions("How much would you like to deposit?", new Options() {
						@Override
						public void create() {
							option("All", () -> {
								int coalBagAmount = e.getPlayer().getI("coalBag");
								e.getPlayer().getBank().addItem(new Item(453, coalBagAmount), true);
								e.getPlayer().save("coalBag", 0);
								e.getPlayer().sendMessage("You store all of your coal in the bank.");
							});
							option("Deposit X", () -> { 
								e.getPlayer().sendInputInteger("How much coal would you like to deposit?", amount -> {
									int coalBagAmount = e.getPlayer().getI("coalBag");
									int coalToStore = (amount > coalBagAmount ? coalBagAmount : amount);
									e.getPlayer().getBank().addItem(new Item(453, coalToStore), true);
									e.getPlayer().save("coalBag", coalBagAmount-coalToStore);
									e.getPlayer().sendMessage("You store " + coalToStore + " coal in the bank.");
								});
							});
						}
					}));
				}
			}
		}
	};
}
