package com.rs.utils.drop;

import java.io.IOException;

import com.rs.game.item.ItemsContainer;
import com.rs.game.player.controllers.BarrowsController;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.utils.DropSets;

public class Test {
	
	public static void main(String[] args) throws IOException {
		//Cache.init();
		DropSets.init();
		
		ItemsContainer<Item> drops = new ItemsContainer<>(200, true);
		
		long start = System.currentTimeMillis();
		int sample = 100000;
		for (int i = 0;i < sample;i++) {
			drops.addAll(BarrowsController.getSimulatedDrop(7, 1012));
		}
		long time = System.currentTimeMillis() - start;
		
		drops.sortByItemId();
		for (Item item : drops.toArray()) {
			if (item == null)
				continue;
			System.out.println(item.getDefinitions().getName()+": " + Utils.getFormattedNumber(item.getAmount()) + " (1/" + Math.round((double) sample / (double) item.getAmount()) + ")");
		}
		System.out.println("Generated " + sample + " drops in " + time + "ms");
		int barrowsItems = 0;
		for (Item i : drops.toArray()) {
			if (i != null && i.getName().contains("'"))
				barrowsItems += i.getAmount();
		}
		System.out.println("Barrows item approximately 1/"+(sample/barrowsItems));
	}
	
}
