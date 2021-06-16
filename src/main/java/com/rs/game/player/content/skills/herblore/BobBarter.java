package com.rs.game.player.content.skills.herblore;

import java.util.ArrayList;
import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.Potions.Potion;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class BobBarter {
	
	public static NPCClickHandler handleBobBarter = new NPCClickHandler(6524) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().equalsIgnoreCase("decant")) { 
				decant(e.getPlayer());
			} else if (e.getOption().equalsIgnoreCase("decant-x")) {
				Dialogue decantChoices = new Dialogue();
				decantChoices.addOption("Decant to dose", "1 dose", "2 dose", "3 dose", "4 dose");
				decantChoices.addSimple("Bob decant's your potions into 1 doses", () -> decant(e.getPlayer(), 1));
				decantChoices.addSimple("Bob decant's your potions into 2 doses", () -> decant(e.getPlayer(), 2));
				decantChoices.addSimple("Bob decant's your potions into 3 doses", () -> decant(e.getPlayer(), 3));
				decantChoices.addSimple("Bob decant's your potions into 4 doses", () -> decant(e.getPlayer(), 4));
				e.getPlayer().startConversation(decantChoices);
			}
		}
	};
	
	public static void decant(Player p) {
		decant(p, 4);
	}
	
	public static void decant(Player p, int dose) {
		ArrayList<Integer> checkedPotions = new ArrayList<Integer>();
		HashMap<String, ArrayList<Integer>> potionDoses = new HashMap<String, ArrayList<Integer>>();
		Potion potion = null;
		
		for (Item i : p.getInventory().getItems().getItems()) {
			if (i == null)
				continue;
			ItemDefinitions itemdefs = ItemDefinitions.getDefs(i.getId());		
			
			if (checkedPotions.contains(i.getId()) && !itemdefs.isNoted()) {
				p.getInventory().deleteItem(i);
				continue;
			}
			
			for (Potion potions : Potion.values()) {
				if (potions != null && potions.isVial()) {
					int[] potIds = potions.getIds();
					for (int potId : potIds) {
						if (itemdefs.isNoted()) {
							if (potId == itemdefs.getCertId()) {
								potion = potions;
								checkedPotions.add(i.getId());
							}
						} else {
							if (potId == i.getId()) {
								potion = potions;
								checkedPotions.add(i.getId());
							}							
						}
					}
				}
			}
			
			if (potion == null || !potion.isVial()) {
				continue;
			}

			String potionName = getNameWithoutDoses(i.getName());
			int doses = p.getInventory().getNumberOf(i.getId()) * getDosage(i.getName());
			//System.out.println("doses " + doses);
			if (potionDoses.containsKey(potionName)) {
				ArrayList<Integer> temp = potionDoses.get(potionName);
				doses += (int) temp.get(0);
			}
			ArrayList<Integer> qtyAndIds = new ArrayList<Integer>();
			qtyAndIds.add(doses);
			for (int potId : potion.getIds())
				qtyAndIds.add(potId);

			potionDoses.put(potionName, qtyAndIds);
			p.getInventory().deleteItem(i);
		}
		
		if (potionDoses == new HashMap<String, ArrayList<Integer>>() || potionDoses.isEmpty()) {
			p.sendMessage("You don't have any potions to decant.");
		} else {
			for (String name : potionDoses.keySet()) {
				ArrayList<Integer> pot = potionDoses.get(name);
				int totalDoses = (int) pot.get(0);
				int fullQty = totalDoses / dose;
				int partialDose = totalDoses % dose;
//				System.out.println("Making " + dose + " dose " + name + " potions.");
//				System.out.println("Total doses available: " + totalDoses + ", converting to " + fullQty + " potions with a remaining " + partialDose + " dose");
//				
//				System.out.println("item id full: " + ((int)(pot.get(pot.size()-dose))+1));
				ItemDefinitions fullDef = ItemDefinitions.getDefs(((int)(pot.get(pot.size()-dose))));
				if (fullDef.name.contains(name) && fullQty > 0)
					p.getInventory().addItemDrop(fullDef.getCertId() == -1 ? fullDef.getId() : fullDef.getCertId(), fullQty);
				if (partialDose > 0) {
					//System.out.println("item id partial: " + ((int)(pot.get(pot.size()-partialDose))));
					ItemDefinitions partialDef = ItemDefinitions.getDefs(((int)pot.get(pot.size()-partialDose)));
					if (partialDef.name.contains(name))
						p.getInventory().addItemDrop(partialDef.getCertId() == -1 ? partialDef.getId() : partialDef.getCertId(), 1);
				}
			}
		}
	}
	
	public static String getNameWithoutDoses(String itemName) {
		int end = itemName.indexOf("(") - 1;
		if (end < 0)
			return "";
		return itemName.substring(0, end);
	}
	
	public static int getDosage(String itemName) {
		int start = itemName.indexOf("(") + 1;
		int end = start+1;
		return Integer.parseInt(itemName.substring(start, end));
	}
}