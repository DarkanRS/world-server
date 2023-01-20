package com.rs.game.content;

import java.util.Arrays;

import com.rs.game.content.skills.dungeoneering.DungeonRewards;
import com.rs.game.content.skills.prayer.Burying;
import com.rs.game.content.skills.summoning.Pouch;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCDropHandler;

@PluginEventHandler
public class DropCleaners {

	public static NPCDropHandler bonecrusher = new NPCDropHandler(null, Arrays.stream(Burying.Bone.values()).filter(bone -> bone != Burying.Bone.ACCURSED_ASHES && bone != Burying.Bone.IMPIOUS_ASHES && bone != Burying.Bone.INFERNAL_ASHES).map(bone -> bone.getId()).toArray(), e -> {
		if (bonecrush(e.getPlayer(), e.getItem()))
			e.deleteItem();
	});

	public static NPCDropHandler herbicide = new NPCDropHandler(null, Arrays.stream(DungeonRewards.HerbicideSetting.values()).map(setting -> setting.getHerb().getHerbId()).toArray(), e -> {
		if (herbicide(e.getPlayer(), e.getItem())) {
			e.deleteItem();
			return;
		}
		if (e.getPlayer().getFamiliarPouch() == Pouch.MACAW && e.getPlayer().getFamiliar().getInventory().freeSlot() > 0) {
			e.getPlayer().sendMessage("Your macaw picks up the " + e.getItem().getName().toLowerCase() + " from the ground.", true);
			e.getPlayer().getFamiliar().getInventory().add(new Item(e.getItem().getId(), e.getItem().getAmount()));
			e.deleteItem();
		}
	});

	public static NPCDropHandler charmingImp = new NPCDropHandler(null, new Object[] { 12158, 12159, 12160, 12161, 12162, 12163, 12168 }, e -> {
		if (e.getPlayer().getInventory().containsItem(25350, 1) && e.getPlayer().getInventory().hasRoomFor(e.getItem())) {
			e.getPlayer().getInventory().addItem(new Item(e.getItem()));
			e.deleteItem();
			return;
		}
	});

	public static NPCDropHandler goldAccumulator = new NPCDropHandler(null, new Object[] { 995 }, e -> {
		if (e.getPlayer().getInventory().containsItem(25351, 1) && e.getPlayer().getInventory().hasRoomFor(e.getItem())) {
			e.getPlayer().getInventory().addCoins(e.getItem().getAmount());
			e.deleteItem();
			return;
		}
	});

	public static boolean herbicide(Player player, Item item) {
		if (!player.getInventory().containsItem(19675, 1))
			return false;
		DungeonRewards.HerbicideSetting setting = DungeonRewards.HerbicideSetting.forGrimy(item.getId());
		if (setting == null || !player.herbicideSettings.contains(setting))
			return false;
		if (player.getSkills().getLevel(Constants.HERBLORE) >= setting.getHerb().getLevel()) {
			player.getSkills().addXp(Constants.HERBLORE, setting.getHerb().getExperience() * 2);
			return true;
		}
		return false;
	}

	public static boolean bonecrush(Player player, Item item) {
		if (!player.getInventory().containsItem(18337, 1))
			return false;
		Burying.Bone bone = Burying.Bone.forId(item.getId());
		if (bone != null && bone != Burying.Bone.ACCURSED_ASHES && bone != Burying.Bone.IMPIOUS_ASHES && bone != Burying.Bone.INFERNAL_ASHES) {
			player.getSkills().addXp(Constants.PRAYER, bone.getExperience());
			Burying.handleNecklaces(player, bone.getId());
			return true;
		}
		return false;
	}

}
