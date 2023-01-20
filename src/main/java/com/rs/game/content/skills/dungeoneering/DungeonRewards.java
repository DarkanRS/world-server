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
package com.rs.game.content.skills.dungeoneering;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.content.skills.herblore.HerbCleaning.Herbs;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Rights;
import com.rs.lib.net.ClientPacket;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class DungeonRewards {

	public enum HerbicideSetting {
		GUAM(Herbs.GUAM, 32),
		MARRENTILL(Herbs.MARRENTILL, 33),
		TARROMIN(Herbs.TARROMIN, 34),
		HARRALANDER(Herbs.HARRALANDER, 35),
		RANARR(Herbs.RANARR, 36),
		TOADFLAX(Herbs.TOADFLAX, 37),
		SPIRIT_WEED(Herbs.SPIRIT_WEED, 38),
		IRIT(Herbs.IRIT, 39),
		WERGALI(Herbs.WERGALI, 40),
		AVANTOE(Herbs.AVANTOE, 41),
		KWUARM(Herbs.KWUARM, 42),
		SNAPDRAGON(Herbs.SNAPDRAGON, 43),
		CADANTINE(Herbs.CADANTINE, 44),
		LANTADYME(Herbs.LANTADYME, 45),
		DWARF_WEED(Herbs.DWARF_WEED, 46),
		FELLSTALK(Herbs.FELLSTALK, 47),
		TORSTOL(Herbs.TORSTOL, 50);

		private static Map<Integer, HerbicideSetting> BY_GRIMY = new HashMap<>();

		static {
			for (HerbicideSetting setting : values())
				BY_GRIMY.put(setting.getHerb().getHerbId(), setting);
		}

		public static HerbicideSetting forGrimy(int herbId) {
			return BY_GRIMY.get(herbId);
		}

		private Herbs herb;
		private int buttonId;

		private HerbicideSetting(Herbs herb, int buttonId) {
			this.herb = herb;
			this.buttonId = buttonId;
		}

		public int getButtonId() {
			return buttonId;
		}

		public Herbs getHerb() {
			return herb;
		}
	}

	public enum DungeonReward {
		BONECRUSHER(18337, 0, 21, 34000),
		HERBICIDE(19675, 5, 21, 34000),
		SCROLL_OF_LIFE(18336, 15, 25, 10000),
		SCROLL_OF_CLEANSING(19890, 40, 49, 20000),
		SCROLL_OF_EFFICIENCY(19670, 105, 55, 20000),
		SCROLL_OF_AUGURY(18344, 150, 77, 153000),
		SCROLL_OF_RIGOUR(18839, 145, 74, 140000),
		SCROLL_OF_RENEWAL(18343, 125, 65, 107000),
		MERCENARY_GLOVES(18347, 140, 73, 48500),
		TOME_OF_FROST(18346, 80, 48, 43000),
		ARCANE_PULSE_NECKLACE(18333, 20, 30, 6500),
		GRAVITE_SHORTBOW(18373, 70, 45, 40000),
		GRAVITE_LONGSWORD(18367, 55, 45, 40000),
		GRAVITE_RAPIER(18365, 50, 45, 40000),
		GRAVITE_STAFF(18371, 65, 45, 40000),
		GRAVITE_2H(18369, 60, 45, 40000),
		ARCANE_BLAST_NECKLACE(18334, 90, 50, 15500),
		RING_OF_VIGOUR(19669, 120, 62, 50000),
		ARCANE_STREAM_NECKLACE(18335, 130, 70, 30500),
		CHAOTIC_RAPIER(18349, 155, 80, 200000),
		CHAOTIC_LONGSWORD(18351, 160, 80, 200000),
		CHAOTIC_MAUL(18353, 165, 80, 200000),
		CHAOTIC_STAFF(18355, 170, 80, 200000),
		CHAOTIC_CROSSBOW(18357, 175, 80, 200000),
		CHAOTIC_KITESHIELD(18359, 180, 80, 200000),
		EAGLE_EYE_KITESHIELD(18361, 185, 80, 200000),
		FARSEER_KITESHIELD(18363, 190, 80, 200000),
		SNEAKERPEEPER(19894, 195, 80, 85000),
		TWISTED_NECKLACE(19886, 25, 30, 8500),
		DRAGONTOOTH_NECKLACE(19887, 115, 60, 17000),
		DEMONHORN_NECKLACE(19888, 200, 90, 35000),
		GEM_BAG(18338, 10, 25, 2000),
		COAL_BAG(18339, 35, 35, 4000);

		private static Map<Integer, DungeonReward> rewards = new HashMap<>();

		public static DungeonReward forId(int id) {
			return rewards.get(id);
		}

		static {
			for (DungeonReward monster : DungeonReward.values())
				rewards.put(monster.slotId, monster);
		}

		private int id;
		private int req;
		private int cost;
		private int slotId;
		private String name;

		private DungeonReward(int id, int slotId, int req, int cost) {
			this.id = id;
			this.req = req;
			this.cost = cost;
			this.slotId = slotId;
			name = ItemDefinitions.getDefs(id).getName();
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public int getCost() {
			return cost;
		}

		public int getSlotId() {
			return slotId;
		}

		public int getRequirement() {
			return req;
		}
	}

	public static void openRewardsShop(Player player) {
		player.getInterfaceManager().sendInterface(940);
		player.getPackets().setIFEvents(new IFEvents(940, 2, 0, 205).enableRightClickOptions(0,1,2,3,4,5,6,9));
		refresh(player);
	}

	public static void refresh(Player player) {
		player.getPackets().setIFText(940, 31, "" + player.getDungManager().getTokens());
	}

	public static void openHerbSelection(Player player) {
		for(HerbicideSetting setting : player.herbicideSettings)
			player.getPackets().setIFGraphic(1006, setting.getButtonId(), 2548);
		player.getInterfaceManager().sendInterface(1006);
	}

	public static ButtonClickHandler handleHerbicideButtons = new ButtonClickHandler(1006, e -> {
		HerbicideSetting setting = null;
		for (HerbicideSetting settings : HerbicideSetting.values())
			if (settings.getButtonId() == e.getComponentId())
				setting = settings;
		if (setting != null)
			if (e.getPlayer().herbicideSettings.contains(setting)) {
				e.getPlayer().herbicideSettings.remove(setting);
				e.getPlayer().getPackets().setIFGraphic(1006, setting.getButtonId(), 2549);
			} else {
				e.getPlayer().herbicideSettings.add(setting);
				e.getPlayer().getPackets().setIFGraphic(1006, setting.getButtonId(), 2548);
			}
	});

	public static ButtonClickHandler handleRewardsInter = new ButtonClickHandler(940, e -> {
		if (e.getComponentId() == 64 && e.getPacket() == ClientPacket.IF_OP1) {
			if (e.getPlayer().getTempAttribs().getO("dungReward") != null) {
				DungeonReward reward = e.getPlayer().getTempAttribs().getO("dungReward");
				if (reward != null) {
					if (e.getPlayer().getSkills().getLevelForXp(Constants.DUNGEONEERING) < reward.getRequirement()) {
						e.getPlayer().sendMessage("You need " + reward.getRequirement() + " dungeoneering to buy this reward.");
						return;
					}
					if (e.getPlayer().getDungManager().getTokens() < reward.getCost()) {
						e.getPlayer().sendMessage("You need " + reward.getCost() + " dungeoneering tokens to buy this reward.");
						return;
					}
				} else
					e.getPlayer().sendMessage("You must choose a reward before trying to buy something.");
				e.getPlayer().getPackets().setIFHidden(940, 42, false);
			}
			return;
		}
		if (e.getComponentId() == 48) {
			DungeonReward reward = e.getPlayer().getTempAttribs().getO("dungReward");
			if (reward != null && e.getPlayer().getDungManager().getTokens() >= reward.getCost())
				if (e.getPlayer().getInventory().hasFreeSlots()) {
					if (reward.getId() >= 18349 && reward.getId() <= 18374) {
						Item rew = new Item(reward.getId(), 1);
						rew.addMetaData("combatCharges", 12000);
						e.getPlayer().getInventory().addItem(rew);
					} else
						e.getPlayer().getInventory().addItem(reward.getId(), 1);
					e.getPlayer().getDungManager().removeTokens(reward.getCost());
				} else
					e.getPlayer().sendMessage("You don't have enough inventory space.");
			refresh(e.getPlayer());
			e.getPlayer().getPackets().setIFHidden(940, 42, true);
		}
		if (e.getComponentId() == 50)
			e.getPlayer().getPackets().setIFHidden(940, 42, true);
		if (e.getComponentId() == 2) {
			DungeonReward reward = DungeonReward.forId(e.getSlotId());
			if (reward == null) {
				e.getPlayer().getTempAttribs().removeO("dungReward");
				e.getPlayer().sendMessage("Reward currently not supported. " + (e.getPlayer().hasRights(Rights.DEVELOPER) ? e.getSlotId() : ""));
				return;
			}
			e.getPlayer().getTempAttribs().setO("dungReward", reward);
		}
	});

}
