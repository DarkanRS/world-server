// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.utils.shop;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.rs.game.content.Shop;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.util.Logger;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class ShopsHandler {

	private static final Map<String, Shop> SHOPS = new HashMap<>();
	private static final Map<String, ShopDef> SHOP_DEFS = new HashMap<>();
	private static final Map<Integer, String> NPC_SHOPS = new HashMap<>();

	private static final String PATH = "data/items/shops/";

	@ServerStartupEvent(Priority.FILE_IO)
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
		Logger.info(ShopsHandler.class, "loadShopFiles", "Loading shops...");
		try {
			File[] dropFiles = new File(PATH).listFiles();
			for (File f : dropFiles)
				loadFile(f);
		} catch (Throwable e) {
			Logger.handle(ShopsHandler.class, "loadShopFiles", e);
		}
		for (String key : SHOP_DEFS.keySet()) {
			ShopDef shop = SHOP_DEFS.get(key);
			addShop(key, new Shop(shop.getName(), shop.getCurrency(), shop.getItems(), shop.isGeneralStore(), shop.isBuyOnly()));
			if (shop.getNpcIds() != null && shop.getNpcIds().length > 0)
				for (int npcId : shop.getNpcIds())
					NPC_SHOPS.put(npcId, key);
		}
		Logger.info(ShopsHandler.class, "loadShopFiles", "Loaded "+SHOPS.size()+" shops...");
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
		if ((shop == null) || !player.getBank().checkPin())
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