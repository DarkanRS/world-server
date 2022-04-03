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
package com.rs.game.content.skills.summoning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.content.skills.summoning.familiars.Familiar;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class Summoning {

	public enum Pouches {
		SPIRIT_WOLF(12047, 1, 0.1, 4.8, Ticks.fromMinutes(16), 1),
		DREADFOWL(12043, 4, 0.1, 9.3, Ticks.fromMinutes(16), 1),
		SPIRIT_SPIDER(12059, 10, 0.2, 12.6, Ticks.fromMinutes(16), 2),
		THORNY_SNAIL(12019, 13, 0.2, 12.6, Ticks.fromMinutes(16), 2),
		GRANITE_CRAB(12009, 16, 0.2, 21.6, Ticks.fromMinutes(16), 2),
		SPIRIT_MOSQUITO(12778, 17, 0.2, 46.5, Ticks.fromMinutes(16), 2),
		DESERT_WYRM(12049, 18, 0.4, 31.2, Ticks.fromMinutes(16), 1),
		SPIRIT_SCORPION(12055, 19, 0.9, 83.2, Ticks.fromMinutes(16), 2),
		SPIRIT_TZ_KIH(12808, 22, 1.1, 96.8, Ticks.fromMinutes(16), 3),
		ALBINO_RAT(12067, 23, 2.3, 202.4, Ticks.fromMinutes(16), 3),
		SPIRIT_KALPHITE(12063, 25, 2.5, 220, Ticks.fromMinutes(16), 3),
		COMPOST_MOUNT(12091, 28, 0.6, 49.8, Ticks.fromMinutes(32), 6),
		GIANT_CHINCHOMPA(12800, 29, 2.5, 255.2, Ticks.fromMinutes(32), 1),
		VAMPYRE_BAT(12053, 31, 1.6, 136.0, Ticks.fromMinutes(32), 4),
		HONEY_BADGER(12065, 32, 1.6, 140.8, Ticks.fromMinutes(32), 4),
		BEAVER(12021, 33, 0.7, 57.6, Ticks.fromMinutes(32), 4),
		VOID_RAVAGER(12818, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
		VOID_SPINNER(12780, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
		VOID_TORCHER(12798, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
		VOID_SHIFTER(12814, 34, 0.7, 59.6, Ticks.fromMinutes(32), 4),
		BRONZE_MINOTAUR(12073, 36, 2.4, 316.8, Ticks.fromMinutes(32), 9),
		IRON_MINOTAUR(12075, 46, 4.6, 404.8, Ticks.fromMinutes(32), 9),
		STEEL_MINOTAUR(12077, 56, 5.6, 492.8, Ticks.fromMinutes(48), 9),
		MITHRIL_MINOTAUR(12079, 66, 6.6, 580.8, Ticks.fromMinutes(48), 9),
		ADAMANT_MINOTAUR(12081, 76, 8.6, 668.8, Ticks.fromMinutes(64), 9),
		RUNE_MINOTAUR(12083, 86, 8.6, 756.8, Ticks.fromMinutes(64), 9),
		BULL_ANT(12087, 40, 0.6, 52.8, Ticks.fromMinutes(32), 5),
		MACAW(12071, 41, 0.8, 72.4, Ticks.fromMinutes(32), 5),
		EVIL_TURNIP(12051, 42, 2.1, 184.8, Ticks.fromMinutes(32), 5),
		SPIRIT_COCKATRICE(12095, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5),
		SPIRIT_GUTHATRICE(12097, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5),
		SPIRIT_SARATRICE(12099, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5),
		SPIRIT_ZAMATRICE(12101, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5),
		SPIRIT_PENGATRICE(12103, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5),
		SPIRIT_CORAXATRICE(12105, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5),
		SPIRIT_VULATRICE(12107, 43, 0.9, 75.2, Ticks.fromMinutes(32), 5),
		PYRELORD(12816, 46, 2.3, 202.4, Ticks.fromMinutes(32), 5),
		MAGPIE(12041, 47, 0.9, 83.2, Ticks.fromMinutes(32), 5),
		BLOATED_LEECH(12061, 49, 2.4, 215.2, Ticks.fromMinutes(32), 5),
		SPIRIT_TERRORBIRD(12007, 52, 0.7, 68.4, Ticks.fromMinutes(32), 6),
		ABYSSAL_PARASITE(12035, 54, 1.1, 94.8, Ticks.fromMinutes(32), 6),
		SPIRIT_JELLY(12027, 55, 5.5, 484.0, Ticks.fromMinutes(48), 6),
		IBIS(12531, 56, 1.1, 98.8, Ticks.fromMinutes(32), 6),
		SPIRIT_KYATT(12812, 57, 5.7, 501.6, Ticks.fromMinutes(48), 6),
		SPIRIT_LARUPIA(12784, 57, 5.7, 501.6, Ticks.fromMinutes(48), 6),
		SPIRIT_GRAAHK(12810, 57, 5.6, 501.6, Ticks.fromMinutes(48), 6),
		KARAMTHULU_OVERLOAD(12023, 58, 5.8, 510.4, Ticks.fromMinutes(48), 6),
		SMOKE_DEVIL(12085, 61, 3.1, 268.0, Ticks.fromMinutes(48), 7),
		ABYSSAL_LURKER(12037, 62, 1.9, 109.6, Ticks.fromMinutes(48), 7),
		SPIRIT_COBRA(12015, 63, 3.1, 276.8, Ticks.fromMinutes(48), 7),
		STRANGER_PLANT(12045, 64, 3.2, 281.6, Ticks.fromMinutes(48), 7),
		BARKER_TOAD(12123, 66, 1.0, 87.0, Ticks.fromMinutes(48), 7),
		WAR_TORTOISE(12031, 67, 0.7, 58.6, Ticks.fromMinutes(48), 7),
		BUNYIP(12029, 68, 1.4, 119.2, Ticks.fromMinutes(48), 7),
		FRUIT_BAT(12033, 69, 1.4, 121.2, Ticks.fromMinutes(48), 7),
		RAVENOUS_LOCUST(12820, 70, 1.5, 132.0, Ticks.fromMinutes(48), 4),
		ARCTIC_BEAR(12057, 71, 1.1, 93.2, Ticks.fromMinutes(48), 8),
		PHEONIX(14623, 72, 3.0, 301.0, Ticks.fromMinutes(48), 8),
		OBSIDIAN_GOLEM(12792, 73, 7.3, 642.4, Ticks.fromMinutes(64), 8),
		GRANITE_LOBSTER(12069, 74, 3.7, 325.6, Ticks.fromMinutes(64), 8),
		PRAYING_MANTIS(12011, 75, 3.6, 329.6, Ticks.fromMinutes(64), 8),
		FORGE_REGENT(12782, 76, 1.5, 134.0, Ticks.fromMinutes(64), 9),
		TALON_BEAST(12794, 77, 3.8, 1015.2, Ticks.fromMinutes(64), 9),
		GIANT_ENT(12013, 78, 1.6, 136.8, Ticks.fromMinutes(64), 8),
		HYDRA(12025, 80, 1.6, 140.8, Ticks.fromMinutes(64), 8),
		SPIRIT_DAGANNOTH(12017, 83, 4.1, 364.8, Ticks.fromMinutes(64), 9),
		UNICORN_STALLION(12039, 88, 1.8, 154.4, Ticks.fromMinutes(64), 9),
		WOLPERTINGER(12089, 92, 4.6, 404.8, Ticks.fromMinutes(64), 10),
		PACK_YAK(12093, 96, 4.8, 422.2, Ticks.fromMinutes(64), 10),
		FIRE_TITAN(12802, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
		MOSS_TITAN(12804, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
		ICE_TITAN(12806, 79, 7.9, 695.2, Ticks.fromMinutes(64), 9),
		LAVA_TITAN(12788, 83, 8.3, 730.4, Ticks.fromMinutes(64), 9),
		SWAMP_TITAN(12776, 85, 4.2, 373.6, Ticks.fromMinutes(64), 9),
		GEYSER_TITAN(12786, 89, 8.9, 783.2, Ticks.fromMinutes(64), 10),
		ABYSSAL_TITAN(12796, 93, 1.9, 163.2, Ticks.fromMinutes(64), 10),
		IRON_TITAN(12822, 95, 8.6, 417.6, Ticks.fromMinutes(64), 10),
		STEEL_TITAN(12790, 99, 4.9, 435.2, Ticks.fromMinutes(64), 10);

		private static final Map<Integer, Pouches> pouches = new HashMap<>();

		static {
			for (Pouches pouch : Pouches.values())
				pouches.put(pouch.realPouchId, pouch);
		}

		public static Pouches forId(int id) {
			return pouches.get(id);
		}

		private int realPouchId;
		private int level;
		private int summoningCost;
		private double minorExperience;
		private double experience;
		private int pouchTime;

		private Pouches(int realPouchId, int level, double minorExperience, double experience, int pouchTime, int summoningCost) {
			this.level = level;
			this.realPouchId = realPouchId;
			this.minorExperience = minorExperience;
			this.experience = experience;
			this.summoningCost = summoningCost;
			this.pouchTime = pouchTime;
		}

		public int getLevel() {
			return level;
		}

		public int getRealPouchId() {
			return realPouchId;
		}

		public int getSummoningCost() {
			return summoningCost;
		}

		public double getMinorExperience() {
			return minorExperience;
		}

		public double getExperience() {
			return experience;
		}

		public int getPouchTime() {
			return pouchTime;
		}
	}

	public enum Scrolls {

		WOLF(12425, 0.1, 12047, 1),
		DREADFOWL(12445, 0.1, 12043, 4),
		FETCH_CASKET(19621, 0.1, 19622, 4),
		EGG_SPAWN(12428, 0.2, 12059, 10),
		SLIME_SPRAY(12459, 0.2, 12019, 13),
		GRANITE_CRAB(12533, 0.2, 12009, 16),
		PESTER_SCROLL(12838, 0.5, 12778, 17),
		ELECTRIC_LASH(12460, 0.4, 12049, 18),
		VENOM_SHOT(12432, 0.9, 12055, 19),
		FIREBALL(12839, 1.1, 12808, 22),
		CHEESE_FEAST(12430, 2.3, 12067, 23),
		SANDSTORM(12446, 2.5, 12063, 25),
		COMPOST_GENERATE(12440, 0.6, 12091, 28),
		EXPLODE(12834, 2.9, 12800, 29),
		VAMPYRE(12477, 1.5, 12052, 31),
		INSANE_FEROCITY(12433, 1.6, 12065, 32),
		MULTICHOP(12429, 0.7, 12021, 33),
		CALL_TO_ARMS(12443, 0.7, 12818, 34),
		CALL_TO_ARMS1(12443, 0.7, 12814, 34),
		CALL_TO_ARMS2(12443, 0.7, 12780, 34),
		CALL_TO_ARMS3(12443, 0.7, 12798, 34),
		BRONZE_BULL(12461, 3.6, 12073, 36),
		UNBURDEN(12431, 0.6, 12087, 40),
		HERBCALL(12422, 0.8, 12071, 41),
		EVIL_FLAMES(12448, 2.1, 12051, 42),
		IRON_BULL(12462, 4.6, 12075, 46),
		IMMENSE_HEAT(12829, 2.3, 12816, 46),
		THIEVING_FINGERS(12426, 0.9, 12041, 47),
		BLOOD_DRAIN(12444, 2.4, 12061, 49),
		TIRELESS_RUN(12441, 0.8, 12007, 52),
		ABYSSAL_DRAIN(12454, 1.1, 12035, 54),
		DISSOLVE(12453, 5.5, 12027, 55),
		FISH_RAIN(12424, 1.1, 12531, 56),
		STEEL_BULL(12463, 5.6, 12077, 56),
		AMBUSH(12836, 5.7, 12812, 57),
		RENDING(12840, 5.7, 12784, 57),
		GOAD(12835, 5.7, 12810, 57),
		DOOMSPHERE(12455, 5.8, 12023, 58),
		DUST_CLOUD(12468, 3, 12085, 61),
		ABYSSAL_STEALTH(12427, 1.9, 12037, 62),
		OPHIDIAN(12436, 3.1, 12051, 63),
		POISONOUS_BLAST(12467, 3.2, 12045, 64),
		MITHRIL_BULL(12464, 6.6, 12079, 66),
		TOAD_BARK(12452, 1, 12123, 66),
		TESTUDO(12439, 0.7, 12031, 67),
		SWALLOW_WHOLE(12438, 1.4, 12029, 68),
		FRUITFALL(12423, 1.4, 12033, 69),
		FAMINE(12830, 1.5, 12820, 70),
		ARCTIC_BLAST(12451, 1.1, 12057, 71),
		RISE_FROM_THE_ASHES(14622, 8, 14623, 72),
		VOLCANIC(12826, 7.3, 12792, 73),
		MANTIS_STRIKE(12450, 3.7, 12011, 75),
		INFERNO_SCROLL(12841, 1.5, 12782, 76),
		ADAMANT_BULL(12465, 7.6, 12081, 76),
		DEADLY_CLAW(12831, 11.4, 12794, 77),
		ACORN(12457, 1.6, 12013, 78),
		TITANS(12824, 7.9, 12802, 79),
		TITANS1(12824, 7.9, 12806, 79),
		TITANS2(12824, 7.9, 12804, 79),
		REGROWTH(12442, 1.6, 12025, 80),
		SPIKE_SHOT(12456, 4.1, 12017, 83),
		EBON_THUNDER(12837, 8.3, 12788, 83),
		SWAMP(12832, 4.1, 12776, 85),
		RUNE_BULL(12466, 8.6, 12083, 86),
		HEALING_AURA(12434, 1.8, 12039, 88),
		BOIL(12833, 8.9, 12786, 89),
		MAGIC_FOCUS(12437, 4.6, 12089, 92),
		ESSENCE_SHIPMENT(12827, 1.9, 12796, 93),
		IRON_WITHIN(12828, 4.7, 12822, 95),
		WINTER_STORAGE(12435, 4.8, 12093, 96),
		STEEL_OF_LEGENDS(12825, 4.9, 12790, 99);

		private int scrollId;
		private double experience;
		private int pouchId;
		private int reqLevel;

		private Scrolls(int scrollId, double xp, int pouchId, int reqLevel) {
			setScrollId(scrollId);
			setExperience(xp);
			setPouchId(pouchId);
			setReqLevel(reqLevel);
		}

		public static Scrolls get(int pouchId) {
			for(Scrolls scroll : Scrolls.values())
				if (scroll.getScrollId() == pouchId)
					return scroll;
			return null;
		}

		public int getPouchId() {
			return pouchId;
		}

		public void setPouchId(int pouchId) {
			this.pouchId = pouchId;
		}

		public int getScrollId() {
			return scrollId;
		}

		public void setScrollId(int scrollId) {
			this.scrollId = scrollId;
		}

		public double getExperience() {
			return experience;
		}

		public void setExperience(double experience) {
			this.experience = experience;
		}

		public int getReqLevel() {
			return reqLevel;
		}

		public void setReqLevel(int reqLevel) {
			this.reqLevel = reqLevel;
		}
	}

	public static void spawnFamiliar(Player player, Pouches pouch) {
		if (player.getFamiliar() != null || player.getPet() != null) {
			player.sendMessage("You already have a follower.");
			return;
		}
		if (!player.getControllerManager().canSummonFamiliar())
			return;
		if (player.getSkills().getLevel(Constants.SUMMONING) < pouch.getSummoningCost()) {
			player.sendMessage("You do not have enought summoning points to spawn this.");
			return;
		}
		int levelReq = getRequiredLevel(pouch.getRealPouchId());
		if (player.getSkills().getLevelForXp(Constants.SUMMONING) < levelReq) {
			player.sendMessage("You need a summoning level of " + levelReq + " in order to use this pouch.");
			return;
		}
		WorldTile spawnTile = player.getNearestTeleTile(NPCDefinitions.getDefs(getNPCId(pouch.getRealPouchId()), player.getVars()).size);
		if (spawnTile == null) {
			player.sendMessage("Theres not enough space to summon your familiar here.");
			return;
		}
		final Familiar npc = createFamiliar(player, spawnTile, pouch);
		if (npc == null) {
			player.sendMessage("This familiar is not added yet.");
			return;
		}
		player.getInventory().deleteItem(pouch.getRealPouchId(), 1);
		player.getSkills().drainSummoning(pouch.getSummoningCost());
		player.setFamiliar(npc);
		npc.sendFollowerDetails();
	}

	public static Familiar createFamiliar(Player player, WorldTile tile, Pouches pouch) {
		try {
			return (Familiar) Class.forName("com.rs.game.model.entity.npc.familiar." + (NPCDefinitions.getDefs(getNPCId(pouch.getRealPouchId()))).getName().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")).getConstructor(Player.class, Pouches.class, WorldTile.class, int.class, boolean.class).newInstance(player, pouch, tile, -1, true);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean hasPouch(Player player) {
		for (Pouches pouch : Pouches.values())
			if (player.getInventory().containsOneItem(pouch.getRealPouchId()))
				return true;
		return false;
	}

	public static final int POUCHES_INTERFACE = 672, SCROLLS_INTERFACE = 666;
	private static final Animation SCROLL_INFUSIN_ANIMATION = new Animation(723);
	private static final Animation POUCH_INFUSION_ANIMATION = new Animation(725);
	private static final SpotAnim POUCH_INFUSION_GRAPHICS = new SpotAnim(1207);

	public static int getScrollId(int id) {
		return EnumDefinitions.getEnum(1283).getIntValue(id);
	}

	public static int getRequiredLevel(int id) {
		return EnumDefinitions.getEnum(1185).getIntValue(id);
	}

	public static int getNPCId(int id) {
		return EnumDefinitions.getEnum(1320).getIntValue(id);
	}

	public static boolean isFamiliar(int npcId) {
		return EnumDefinitions.getEnum(1320).getValues().containsValue(npcId);
	}

	public static boolean isFollower(int npcId) {
		return EnumDefinitions.getEnum(1279).getValues().containsKey((long) npcId);
	}

	public static String getRequirementsMessage(int id) {
		return EnumDefinitions.getEnum(1186).getStringValue(id);
	}

	public static void openInfusionInterface(Player player) {
		player.getInterfaceManager().sendInterface(POUCHES_INTERFACE);
		player.getPackets().sendPouchInfusionOptionsScript(POUCHES_INTERFACE, 16, 78, 8, 10, "Infuse<col=FF9040>", "Infuse-5<col=FF9040>", "Infuse-10<col=FF9040>", "Infuse-X<col=FF9040>", "Infuse-All<col=FF9040>", "List<col=FF9040>");
		player.getPackets().setIFEvents(new IFEvents(POUCHES_INTERFACE, 16, 0, 462).enableRightClickOptions(0,1,2,3,4,6));
		player.getTempAttribs().setB("infusing_scroll", false);
	}

	public static void openScrollInfusionInterface(Player player) {
		player.getInterfaceManager().sendInterface(SCROLLS_INTERFACE);
		player.getPackets().sendScrollInfusionOptionsScript(SCROLLS_INTERFACE, 16, 78, 8, 10, "Transform<col=FF9040>", "Transform-5<col=FF9040>", "Transform-10<col=FF9040>", "Transform-All<col=FF9040>", "Transform-X<col=FF9040>");
		player.getPackets().setIFEvents(new IFEvents(SCROLLS_INTERFACE, 16, 0, 462).enableRightClickOptions(0,1,2,3,4,5));
		player.getTempAttribs().setB("infusing_scroll", true);
	}

	public static int getPouchId(int grayId) {
		EnumDefinitions reals = EnumDefinitions.getEnum(1182);
		return reals.getIntValue((grayId-2) / 5 + 1);
	}

	public static ButtonClickHandler handlePouchButtons = new ButtonClickHandler(672) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 16) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), Integer.MAX_VALUE);
				else if (e.getPacket() == ClientPacket.IF_OP5) {
					handlePouchInfusion(e.getPlayer(), getPouchId(e.getSlotId()), 28);
					e.getPlayer().sendMessage("You currently need " + ItemDefinitions.getDefs(e.getSlotId2()).getCreateItemRequirements());
				}
			} else if (e.getComponentId() == 19 && e.getPacket() == ClientPacket.IF_OP1)
				openScrollInfusionInterface(e.getPlayer());
		}
	};

	public static ButtonClickHandler handleScrollButtons = new ButtonClickHandler(666) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 16) {
				if (e.getPacket() == ClientPacket.IF_OP1)
					createScroll(e.getPlayer(), e.getSlotId2(), 1);
				else if (e.getPacket() == ClientPacket.IF_OP2)
					createScroll(e.getPlayer(), e.getSlotId2(), 5);
				else if (e.getPacket() == ClientPacket.IF_OP3)
					createScroll(e.getPlayer(), e.getSlotId2(), 10);
				else if (e.getPacket() == ClientPacket.IF_OP4)
					createScroll(e.getPlayer(), e.getSlotId2(), Integer.MAX_VALUE);
			} else if (e.getComponentId() == 18 && e.getPacket() == ClientPacket.IF_OP1)
				openInfusionInterface(e.getPlayer());
		}
	};

	public static void createScroll(Player player, int itemId, int amount) {
		Scrolls scroll = Scrolls.get(itemId);
		if (scroll == null) {
			player.sendMessage("You do not have the pouch required to create this scroll.");
			return;
		}
		if (amount == 28 || amount > player.getInventory().getItems().getNumberOf(scroll.getPouchId()))
			amount = player.getInventory().getItems().getNumberOf(scroll.getPouchId());
		if (!player.getInventory().containsItem(scroll.getPouchId(), 1)) {
			player.sendMessage("You do not have enough " + ItemDefinitions.getDefs(scroll.getPouchId()).getName().toLowerCase() + "es to create " + amount + " " + ItemDefinitions.getDefs(scroll.getScrollId()).getName().toLowerCase() + "s.");
			return;
		}
		if (player.getSkills().getLevel(Constants.SUMMONING) < scroll.getReqLevel()) {
			player.sendMessage("You need a summoning level of " + scroll.getReqLevel() + " to create " + amount + " " + ItemDefinitions.getDefs(scroll.getScrollId()).getName().toLowerCase() + "s.");
			return;
		}
		player.getInventory().deleteItem(scroll.getPouchId(), amount);
		player.getInventory().addItem(scroll.getScrollId(), amount * 10);
		player.getSkills().addXp(Constants.SUMMONING, scroll.getExperience());

		player.closeInterfaces();
		player.setNextAnimation(SCROLL_INFUSIN_ANIMATION);
	}


	public static void handlePouchInfusion(Player player, int pouchId, int creationCount) {
		Pouches pouch = Pouches.forId(pouchId);
		if (pouch == null)
			return;
		boolean infusingScroll = player.getTempAttribs().removeB("infusing_scroll"), hasRequirements = false;
		ItemDefinitions def = ItemDefinitions.getDefs(pouch.getRealPouchId());
		List<Item> itemReq = def.getCreateItemRequirements(infusingScroll);
		int level = getRequiredLevel(pouch.getRealPouchId());
		if (itemReq != null)
			itemCount: for (int i = 0; i < creationCount; i++) {
				if (!player.getInventory().containsItems(itemReq)) {
					sendItemList(player, infusingScroll, creationCount, pouchId);
					break itemCount;
				}
				if (player.getSkills().getLevelForXp(Constants.SUMMONING) < level) {
					player.sendMessage("You need a summoning level of " + level + " to create this pouch.");
					break itemCount;
				}
				hasRequirements = true;
				player.getInventory().removeItems(itemReq);
				player.getInventory().addItem(new Item(infusingScroll ? getScrollId(pouch.getRealPouchId()) : pouch.getRealPouchId(), infusingScroll ? 10 : 1));
				player.getSkills().addXp(Constants.SUMMONING, infusingScroll ? pouch.getMinorExperience() : pouch.getExperience());
			}
		if (!hasRequirements) {
			player.getTempAttribs().setB("infusing_scroll", infusingScroll);
			return;
		}
		player.closeInterfaces();
		player.setNextAnimation(POUCH_INFUSION_ANIMATION);
		player.setNextSpotAnim(POUCH_INFUSION_GRAPHICS);
	}

	public static void switchInfusionOption(Player player) {
		if (player.getTempAttribs().getB("infusing_scroll"))
			openInfusionInterface(player);
		else
			openScrollInfusionInterface(player);
	}

	public static void sendItemList(Player player, boolean infusingScroll, int count, int pouchId) {
		Pouches pouch = Pouches.forId(pouchId);
		if (pouch == null)
			return;
		if (infusingScroll)
			player.sendMessage("This scroll requires 1 " + ItemDefinitions.getDefs(pouch.getRealPouchId()).name.toLowerCase() + ".");
		else
			player.sendMessage(getRequirementsMessage(pouch.getRealPouchId()));
	}
}
