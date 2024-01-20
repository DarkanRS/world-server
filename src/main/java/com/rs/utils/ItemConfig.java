package com.rs.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.content.combat.RangedWeapon;
import com.rs.game.model.entity.player.Controller;
import com.rs.game.model.entity.player.Equipment;
import com.rs.lib.file.JsonFileManager;
import com.rs.lib.game.Item;
import com.rs.lib.json.DateAdapter;
import com.rs.lib.net.packets.Packet;
import com.rs.lib.net.packets.PacketEncoder;
import com.rs.lib.util.*;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.annotations.ServerStartupEvent;
import com.rs.plugin.annotations.ServerStartupEvent.Priority;
import com.rs.utils.json.ControllerAdapter;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@PluginEventHandler
public class ItemConfig {
	private static Map<Integer, ItemConfig> CONFIG_CACHE = new HashMap<>();
	private static final Map<String, Integer> UID_TO_ID = new HashMap<>();
	private final static String PATH = "./data/items/config.json";

	private String uidName;
	private int dropSound = -1;
	private int equipSound = -1;
	private int attackDelay = -1;
	private Map<Integer, Integer> attackAnims;
	private Map<Integer, Integer> attackSounds;
	private int defendAnim = -1;
	private int defendSound = -1;
	private int attackRange = 0;
	private double weight;
	private String examine;

	@ServerStartupEvent(Priority.FILE_IO)
	public static void init() throws JsonIOException, IOException {
		if (new File(PATH).exists())
			CONFIG_CACHE = JsonFileManager.loadJsonFile(new File(PATH), new TypeToken<Map<Integer, ItemConfig>>(){}.getType());
		else
			Logger.error(ItemConfig.class, "init", "No item config file found at " + PATH + "!");
		for (int key : CONFIG_CACHE.keySet()) {
			if (UID_TO_ID.get(CONFIG_CACHE.get(key).uidName) != null)
				Logger.error(ItemConfig.class, "init", "Duplicate item uid names " + CONFIG_CACHE.get(key).uidName + " - " + key);
			UID_TO_ID.put(CONFIG_CACHE.get(key).uidName, key);
		}
	}

	public static void main(String[] args) throws IOException {
		JsonFileManager.setGSON(new GsonBuilder()
				.registerTypeAdapter(Controller.class, new ControllerAdapter())
				.registerTypeAdapter(Date.class, new DateAdapter())
				.registerTypeAdapter(PacketEncoder.class, new PacketEncoderAdapter())
				.registerTypeAdapter(Packet.class, new PacketAdapter())
				.registerTypeAdapterFactory(new RecordTypeAdapterFactory())
				.disableHtmlEscaping()
				.setPrettyPrinting()
				.create());
		Settings.loadConfig();
		Cache.init(Settings.getConfig().getCachePath());
		init();

		for (int i = 0;i < Utils.getItemDefinitionsSize();i++) {
			ItemConfig config = get(i);
			ItemDefinitions def = ItemDefinitions.getDefs(i);
			if (def.getEquipSlot() == Equipment.WEAPON) {
				String weaponName = def.getName().toLowerCase();
				if (config.getAttackAnim(0) == -1)
					System.out.println("Missing attack animation for: " + i + " (" + weaponName + ")");
			}
		}
	}

	public ItemConfig() {

	}

	public int getAttackRange() {
		return attackRange;
	}

	public int getDropSound() {
		return dropSound <= 0 ? 2739 : dropSound;
	}

	public int getEquipSound() {
		return equipSound <= 0 ? 2240 : equipSound;
	}

	/**
	 * TODO
	 * Dump all sounds into the config
	 * Dump all existing anims into the config
	 * Automatically dump any equippable item that is wieldable but does not have an animation
	 */

	public int getAttackSound(int attackStyleIndex) {
		return attackSounds == null ? -1 : attackSounds.getOrDefault(attackStyleIndex, attackSounds.get(0));
	}

	public int getDefendSound() {
		return defendSound <= 0 ? 2240 : defendSound;
	}

	public int getAttackAnim(int attackStyleIndex) { //422
		return attackAnims == null ? -1 : attackAnims.getOrDefault(attackStyleIndex, attackAnims.get(0));
	}

	public int getAttackDelay() {
		return attackDelay <= 0 ? 3 : attackDelay;
	}

	public int getDefendAnim() {
		return defendAnim;
	}

	public double getWeight(boolean equipped) {
		if (weight < 0.0) {
			if (equipped)
				return weight;
			return 0;
		}
		return weight;
	}

	public String getUidName() {
		return uidName;
	}

	public String getExamine(Item item) {
		if (item.getAmount() >= 100000)
			return Utils.formatNumber(item.getAmount()) + " x " + item.getDefinitions().getName() + ".";
		if (item.getDefinitions().isNoted())
			return "Swap this note at any bank for the equivalent item.";
		return examine;
	}

	public static ItemConfig get(int itemId) {
		return CONFIG_CACHE.getOrDefault(itemId, new ItemConfig());
	}

	public static ItemConfig get(Item item) {
		return CONFIG_CACHE.get(item.getId());
	}

	public static int forUid(String uid) {
		if (UID_TO_ID.get(uid) == null)
			throw new IllegalArgumentException("No item with UID " + uid + " found in item configuration.");
		return UID_TO_ID.get(uid);
	}
}
