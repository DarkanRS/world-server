package com.rs.utils.shop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.rs.game.player.Player;
import com.rs.game.player.content.Shop;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ShopsHandler {

	private static final Map<String, Shop> SHOPS = new HashMap<>();
	private static final Map<String, ShopDef> SHOP_DEFS = new HashMap<>();
	private static final Map<Integer, String> NPC_SHOPS = new HashMap<>();

	private static final String PATH = "data/items/shops/";

	@ServerStartupEvent
	public static void loadShops() {
		loadShopFiles();
	}

	public static void reloadShops() {
		SHOPS.clear();
		SHOP_DEFS.clear();
		NPC_SHOPS.clear();
		loadShopFiles();
	}

	private static void loadShopFiles() {
		Logger.log("ShopsHandler", "Loading shops...");
		try {
			File[] dropFiles = new File(PATH).listFiles();
			for (File f : dropFiles)
				loadFile(f);
		} catch (Throwable e) {
			Logger.handle(e);
		}
		for (String key : SHOP_DEFS.keySet()) {
			ShopDef shop = SHOP_DEFS.get(key);
			addShop(key, new Shop(shop.getName(), shop.getCurrency(), shop.getItems(), shop.isGeneralStore(), shop.isBuyOnly()));
			if (shop.getNpcIds() != null && shop.getNpcIds().length > 0)
				for (int npcId : shop.getNpcIds())
					NPC_SHOPS.put(npcId, key);
		}
		Logger.log("ShopsHandler", "Loaded "+SHOPS.size()+" shops...");
	}
	
	private static void loadFile(File f) {
		try {
			if (f.isDirectory()) {
				for (File dir : f.listFiles())
					loadFile(dir);
				return;
			}
			ShopDef def = (ShopDef) JsonFileManager.loadJsonFile(f, ShopDef.class);
			if (def != null)
				SHOP_DEFS.put(f.getName().replace(".json", ""), def);
		} catch(Throwable e) {
			System.err.println("Error loading file: " + f.getPath());
		}			
	}
	
	private static String getShopForNpc(int npcId) {
		return NPC_SHOPS.get(npcId);
	}
	
	public static NPCClickHandler handleShop = new NPCClickHandler(new String[] { "Trade", "Shop" }) {
		@Override
		public void handle(NPCClickEvent e) {
			String key = getShopForNpc(e.getNPCId());
			if (key == null)
				return;
			openShop(e.getPlayer(), key);
		}
	};
	
	public static void restoreShops() {
		for (Shop shop : SHOPS.values())
			shop.restoreItems();
	}

	public static boolean openShop(Player player, String key) {
		Shop shop = getShop(key);
		if (shop == null)
			return false;
		if (!player.getBank().checkPin())
			return false;
		shop.addPlayer(player);
		return true;
	}

	public static Shop getShop(String key) {
		return SHOPS.get(key);
	}

	public static void addShop(String key, Shop shop) {
		SHOPS.put(key, shop);
	}
}