package com.rs.tools;

import java.io.IOException;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.IndexType;
import com.rs.cache.loaders.InventoryDefinitions;
import com.rs.cache.loaders.cs2.CS2Definitions;
import com.rs.cache.loaders.cs2.CS2Script;
import com.rs.game.player.Bank;

public class BankSpaceIncreaser {

	public static void main(String[] args) throws IOException {
		Cache.init(Settings.getConfig().getCachePath());
		
		final int MAX_SIZE = Bank.MAX_BANK_SIZE;
		final int MEMS_SIZE = MAX_SIZE-87;

		InventoryDefinitions def = InventoryDefinitions.getContainer(95);
		int old = def.maxSize;
		def.maxSize = MAX_SIZE;
		def.write(Cache.STORE);
		System.out.println("Replaced container max size from " + old + " to " + MAX_SIZE);

		CS2Script script = CS2Definitions.getScript(1465);
		replaceOpValue(script.intOpValues, 40, MEMS_SIZE);
		replaceOpValue(script.intOpValues, 59, MEMS_SIZE);
		script.write(Cache.STORE);
		
		script = CS2Definitions.getScript(1467);
		replaceOpValue(script.intOpValues, 28, MAX_SIZE);
		script.write(Cache.STORE);
		
		script = CS2Definitions.getScript(1665);
		replaceOpValue(script.intOpValues, 19, MEMS_SIZE);
		replaceOpValue(script.intOpValues, 32, MEMS_SIZE);
		script.write(Cache.STORE);
		
		script = CS2Definitions.getScript(1329);
		replaceOpValue(script.intOpValues, 0, MAX_SIZE);
		replaceOpValue(script.intOpValues, 1, MEMS_SIZE);
		script.write(Cache.STORE);
		
		script = CS2Definitions.getScript(1248);
		replaceOpValue(script.intOpValues, 0, MAX_SIZE);
		script.write(Cache.STORE);
		

		Cache.STORE.getIndex(IndexType.CONFIG).rewriteTable();
		Cache.STORE.getIndex(IndexType.CS2_SCRIPTS).rewriteTable();
	}
	
	public static void replaceOpValue(int[] values, int index, int value) {
		int old = values[index];
		values[index] = value;
		System.out.println("Replaced intOpValue#"+index+" (" + old + ") with " + value);
	}
}
