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
package com.rs.game.content.skills.smithing;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

@PluginEventHandler
public class ItemForging {

	//TODO spotanim 2123 sparks off anvil

	private enum ForgeableItem {
		GODSWORD_S1_S2(11710, 11712, 11686, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 80) {
				player.sendMessage("You need 80 Smithing to work with that metal.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(11686, "You join the shards together.");
			player.getSkills().addXp(Skills.SMITHING, 100);
		}),
		GODSWORD_S2_S3(11712, 11714, 11692, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 80) {
				player.sendMessage("You need 80 Smithing to work with that metal.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(11692, "You join the shards together.");
			player.getSkills().addXp(Skills.SMITHING, 100);
		}),
		GODSWORD_S1_S3(11710, 11714, 11688, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 80) {
				player.sendMessage("You need 80 Smithing to work with that metal.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(11688, "You join the shards together.");
			player.getSkills().addXp(Skills.SMITHING, 100);
		}),
		GODSWORD_S1_S2_S3(11686, 11714, 11690, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 80) {
				player.sendMessage("You need 80 Smithing to work with that metal.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(11690, "You join the shards together.");
			player.getSkills().addXp(Skills.SMITHING, 100);
		}),
		GODSWORD_S2_S3_S1(11692, 11710, 11690, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 80) {
				player.sendMessage("You need 80 Smithing to work with that metal.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(11690, "You join the shards together.");
			player.getSkills().addXp(Skills.SMITHING, 100);
		}),
		GODSWORD_S1_S3_S2(11688, 11712, 11690, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 80) {
				player.sendMessage("You need 80 Smithing to work with that metal.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(11690, "You join the shards together.");
			player.getSkills().addXp(Skills.SMITHING, 100);
		}),
		DRAGONFIRE_SHIELD(11286, 1540, 11284, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 90) {
				player.sendMessage("You need 90 Smithing to fuse the visage to the shield.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(11284, "You set to work, trying to attach the ancient draconic visage to your anti-dragonbreath shield. It's not easy to work with the ancient artifact and it takes all of your skill as a master smith.");
			player.getSkills().addXp(Skills.SMITHING, 2000);
		}),
		DRAGON_SQ_SHIELD(2366, 2368, 1187, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 60) {
				player.sendMessage("You need 60 Smithing to work with the dragon metal.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(1187, "Even for an experienced armorer it is not an easy task, but eventually it is ready. You have restored the dragon square shield to its former glory.");
			player.getSkills().addXp(Skills.SMITHING, 75);
		}),
		SPECTRAL_SPIRIT_SHIELD(13752, 13736, 13744, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 85 || player.getSkills().getLevel(Skills.PRAYER) < 90) {
				player.sendMessage("You need 85 Smithing and 90 Prayer to attach the sigil to the spirit shield.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(13744, "You successfully attach the spectral sigil to the blessed spirit shield.");
			player.getSkills().addXp(Skills.SMITHING, 1800);
		}),
		ARCANE_SPIRIT_SHIELD(13746, 13736, 13738, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 85 || player.getSkills().getLevel(Skills.PRAYER) < 90) {
				player.sendMessage("You need 85 Smithing and 90 Prayer to attach the sigil to the spirit shield.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(13738, "You successfully attach the arcane sigil to the blessed spirit shield.");
			player.getSkills().addXp(Skills.SMITHING, 1800);
		}),
		ELYSIAN_SPIRIT_SHIELD(13750, 13736, 13742, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 85 || player.getSkills().getLevel(Skills.PRAYER) < 90) {
				player.sendMessage("You need 85 Smithing and 90 Prayer to attach the sigil to the spirit shield.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(13742, "You successfully attach the elysian sigil to the blessed spirit shield.");
			player.getSkills().addXp(Skills.SMITHING, 1800);
		}),
		DIVINE_SPIRIT_SHIELD(13748, 13736, 13740, player -> {
			if (player.getSkills().getLevel(Skills.SMITHING) < 85 || player.getSkills().getLevel(Skills.PRAYER) < 90) {
				player.sendMessage("You need 85 Smithing and 90 Prayer to attach the sigil to the spirit shield.");
				return false;
			}
			return true;
		}, player -> {
			player.itemDialogue(13740, "You successfully attach the divine sigil to the blessed spirit shield.");
			player.getSkills().addXp(Skills.SMITHING, 1800);
		});

		private int item1, item2, productId;
		private Function<Player, Boolean> hasRequirements;
		private Consumer<Player> onCreate;

		ForgeableItem(int item1, int item2, int productId, Function<Player, Boolean> hasRequirements, Consumer<Player> onCreate) {
			this.item1 = item1;
			this.item2 = item2;
			this.productId = productId;
			this.hasRequirements = hasRequirements;
			this.onCreate = onCreate;
		}
	}

	public static ItemOnObjectHandler smithOnAnvil = new ItemOnObjectHandler(new Object[] { "Anvil" }, Arrays.stream(ForgeableItem.values()).flatMap(item -> Stream.of(item.item1, item.item2)).distinct().toArray(), e -> {
		List<ForgeableItem> creatableItems = new ArrayList<>();
		for (ForgeableItem item : ForgeableItem.values()) {
			if (e.getPlayer().getInventory().containsItem(item.item1) && e.getPlayer().getInventory().containsItem(item.item2))
				creatableItems.add(item);
		}
		if (creatableItems.isEmpty()) {
			e.getPlayer().sendMessage("You don't have all the items required to fix this.");
			return;
		}
		Dialogue makeX = new Dialogue().addMakeX(creatableItems.stream().mapToInt(item -> item.productId).toArray());
		for (ForgeableItem item : creatableItems)
			makeX.addNext(() -> {
				if (e.getPlayer().getInventory().containsItem(item.item1) && e.getPlayer().getInventory().containsItem(item.item2) && item.hasRequirements.apply(e.getPlayer())) {
					e.getPlayer().anim(898);
					e.getPlayer().getInventory().deleteItem(item.item1, 1);
					e.getPlayer().getInventory().deleteItem(item.item2, 1);
					e.getPlayer().getInventory().addItem(item.productId, 1);
					item.onCreate.accept(e.getPlayer());
				}
			});
		e.getPlayer().startConversation(makeX);
	});

	public static ItemOnObjectHandler blessSpiritShield = new ItemOnObjectHandler(new Object[] { 409, 2640, 4008, 19145, 24343, 26287, 27661, 63207 }, new Object[] { 13754, 13734 }, e -> {
		if (!e.getPlayer().getInventory().containsItems(13754, 13734)) {
			e.getPlayer().sendMessage("You need a spirit shield and a holy elixir to create a blessed spirit shield.");
			return;
		}
		if (!e.getPlayer().isQuestComplete(Quest.SUMMERS_END, "to bless a spirit shield."))
			return;
		if (e.getPlayer().getSkills().getLevel(Skills.PRAYER) < 85) {
			e.getPlayer().sendMessage("You need 85 Prayer to bless a spirit shield.");
			return;
		}
		e.getPlayer().sendMessage("You bless the spirit shield.");
		e.getPlayer().anim(645);
		e.getPlayer().getInventory().deleteItem(13754, 1);
		e.getPlayer().getInventory().deleteItem(13734, 1);
		e.getPlayer().getInventory().addItemDrop(13736, 1);
		e.getPlayer().getSkills().addXp(Skills.PRAYER, 1500);
	});
}