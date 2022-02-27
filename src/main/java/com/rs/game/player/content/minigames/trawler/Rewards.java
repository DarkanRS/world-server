package com.rs.game.player.content.minigames.trawler;

import com.google.gson.GsonBuilder;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.content.skills.fishing.Fish;
import com.rs.game.player.controllers.Controller;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.PacketAdapter;
import com.rs.lib.util.PacketEncoderAdapter;
import com.rs.lib.util.RecordTypeAdapterFactory;
import com.rs.lib.util.Utils;
import com.rs.utils.drop.DropList;
import com.rs.utils.drop.DropTable;
import com.rs.utils.json.ControllerAdapter;
import com.rs.utils.json.FamiliarAdapter;

import java.io.IOException;
import java.util.Date;

public class Rewards {
	private enum Rate {
		LOWEST(11, 1),
		LOW(28, 10),
		MEDIUM(63, 43),
		HIGH(150, 201);

		private int low, high;

		Rate(int low, int high) {
			this.low = low;
			this.high = high;
		}
	}

	public static void main(String[] args) throws IOException {
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Familiar.class, new FamiliarAdapter())
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());
		Cache.init(Settings.getConfig().getCachePath());
		ItemsContainer<Item> rewards = generateRewards(99, 500_000);
		for (Item item : rewards.getItems()) {
			if (item == null)
				continue;
			System.out.println(item.getName() + ": " + item.getAmount() + " (" + (item.getAmount() / 500_000.0D * 100.0) + "%)");
		}
	}

	public static ItemsContainer<Item> generateRewards(int level, int amount) {
		ItemsContainer<Item> rewards = new ItemsContainer<>(20, true);
		DropTable[] tables = new DropTable[Rate.values().length];
		for (int i = 0;i < Rate.values().length;i++) {
			double perc = (double)level / 99.0D;
			int chance = Utils.clampI((int)((double)Rate.values()[i].low + ((double)Rate.values()[i].high - (double)Rate.values()[i].low) * perc), 0, 256);
			tables[i] = new DropTable(chance, 256, getFishIdAtTier(level, i), 1);
		}
		DropList list = new DropList(tables);
		for (int i = 0;i < amount;i++)
			rewards.addAll(list.genDrop());
		return rewards;
	}

	private static final int[] FISH_ORDER = { Fish.SHRIMP.getId(), Fish.SHRIMP.getId(), Fish.SARDINES.getId(), Fish.ANCHOVIES.getId(), Fish.OYSTER.getId(), Fish.TUNA.getId(), Fish.LOBSTER.getId(), Fish.SWORDFISH.getId(), Fish.SHARK.getId(), 395, 389 };

	public static int getFishIdAtTier(int level, int tier) {
		if (level >= 81)
			return FISH_ORDER[7 + tier];
		if (level >= 79)
			return FISH_ORDER[6 + tier];
		if (level >= 76)
			return FISH_ORDER[5 + tier];
		if (level >= 50)
			return FISH_ORDER[4 + tier];
		if (level >= 40)
			return FISH_ORDER[3 + tier];
		if (level >= 35)
			return FISH_ORDER[2 + tier];
		if (level >= 16)
			return FISH_ORDER[1 + tier];
		return FISH_ORDER[0 + tier];
	}
}
