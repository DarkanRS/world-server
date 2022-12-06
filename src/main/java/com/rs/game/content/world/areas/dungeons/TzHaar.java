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
package com.rs.game.content.world.areas.dungeons;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class TzHaar {

	public static final int TOKKUL = 6529;
	public static final int TOKKUL_ZO_CHARGED = 23643;
	public static final int TOKKUL_ZO_UNCHARGED = 23644;

	private static WorldTile[] TOKKUL_ZO_TELEPORTS = { WorldTile.of(4744, 5156, 0), WorldTile.of(4599, 5062, 0), WorldTile.of(4613, 5128, 0), WorldTile.of(4744, 5170, 0) };

	public static ItemClickHandler handleCheckTokkulZoOptions = new ItemClickHandler(new Object[] { TOKKUL_ZO_CHARGED }, new String[] { "Check-charge", "Check-charges", "Teleport" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!e.getPlayer().isQuestComplete(Quest.ELDER_KILN, "to use the Tokkul-Zo."))
				return;
			if (e.getOption().equals("Teleport")) {
				if (e.isEquipped()) {
					if (Magic.sendNormalTeleportSpell(e.getPlayer(), TOKKUL_ZO_TELEPORTS[2]))
						depleteTokkulZo(e.getPlayer());
				} else
					e.getPlayer().sendOptionDialogue("Where would you like to teleport?", ops -> {
						ops.add("Main Plaza", () -> {
							if (Magic.sendNormalTeleportSpell(e.getPlayer(), TOKKUL_ZO_TELEPORTS[0]))
								depleteTokkulZo(e.getPlayer());
						});
						ops.add("Fight Pits", () -> {
							if (Magic.sendNormalTeleportSpell(e.getPlayer(), TOKKUL_ZO_TELEPORTS[1]))
								depleteTokkulZo(e.getPlayer());
						});
						ops.add("Fight Caves", () -> {
							if (Magic.sendNormalTeleportSpell(e.getPlayer(), TOKKUL_ZO_TELEPORTS[2]))
								depleteTokkulZo(e.getPlayer());
						});
						ops.add("Fight Kiln", () -> {
							if (Magic.sendNormalTeleportSpell(e.getPlayer(), TOKKUL_ZO_TELEPORTS[3]))
								depleteTokkulZo(e.getPlayer());
						});
					});
			} else
				e.getPlayer().sendMessage("Your Tokkul-Zo has " + e.getItem().getMetaDataI("tzhaarCharges") + " charges left.");
		}
	};

	public static NPCClickHandler handleTzhaarMejJeh = new NPCClickHandler(new Object[] { 15166 }) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addNPC(15166, HeadE.CONFUSED, "What do you need from me?");
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							boolean recTZ = player.getBool("recTokkulZo");
							if (!player.containsItems(TOKKUL_ZO_UNCHARGED, TOKKUL_ZO_CHARGED))
								if (player.isQuestComplete(Quest.ELDER_KILN, "to obtain a Tokkul-Zo."))
									option("Can I have a Tokkul-Zo?" + (recTZ ? " I've lost mine." : ""), new Dialogue()
											.addPlayer(HeadE.CONFUSED, "Can I have a Tokkul-Zo?" + (player.getBool("recTokkulZo") ? " I've lost mine." : ""))
											.addNPC(15166, HeadE.CALM_TALK, "Alright, you have proven yourself. Try not to lose it."
													+ (recTZ ? "" : " As this is your first time receiving the ring, I have fully charged it for you for free."))
											.addPlayer(HeadE.CHEERFUL, "Thank you!")
											.addItem(TOKKUL_ZO_CHARGED, "TzHaar-Mej-Jeh hands you a ring. It is extremely hot.", () -> {
												if (!player.getInventory().hasFreeSlots()) {
													player.sendMessage("You don't have enough inventory space.");
													return;
												}
												if (!recTZ) {
													player.getInventory().addItem(new Item(TOKKUL_ZO_CHARGED).addMetaData("tzhaarCharges", 3000));
													player.save("recTokkulZo", true);
												} else
													player.getInventory().addItem(TOKKUL_ZO_UNCHARGED);
											}));

							option("About the Tokkul-Zo", new Dialogue()
									.addNPC(15166, HeadE.CONFUSED, "You want to know more about Tokkul-Zo?")
									.addPlayer(HeadE.CONFUSED, "Yes, what does it do?")
									.addNPC(15166, HeadE.CALM_TALK, "This ring has a piece of Tokkul in it. When worn, it will guide "
											+ "your hand and make you better when fighting TzHaar, fire creatures, and maybe even TokHaar.")
									.addPlayer(HeadE.CONFUSED, "How does it do that?")
									.addNPC(15166, HeadE.CALM_TALK, "My magic taps into the memories in the ring, so you better at fighting like a TzHaar. "
											+ "The magic will fade after time. When this happens, return to me and I will recharge it for you... for a price.")
									.addPlayer(HeadE.CONFUSED, "What's your price?")
									.addNPC(15166, HeadE.CALM_TALK, "48,000 Tokkul for a full recharge. Normally I would do it for free, but we need all the Tokkul we can "
											+ "get, so they we can melt it down in the sacred lave, and release our ancestors from their suffering."));

							if (player.getItemWithPlayer(TOKKUL_ZO_UNCHARGED) != null || player.getItemWithPlayer(TOKKUL_ZO_CHARGED) != null)
								option("Recharging the Tokkul-Zo", new Dialogue()
										.addPlayer(HeadE.CONFUSED, "Could you please recharge my ring?")
										.addNPC(15166, HeadE.CALM_TALK, player.getInventory().containsItem(TOKKUL, 16) ? "Of course. Here you go." : "You don't have enough Tokkul with you.", () -> {
											rechargeTokkulZo(player);
										}));
						}
					});
				}
			});
		}
	};

	public static void rechargeTokkulZo(Player player) {
		Item ring = player.getItemWithPlayer(TOKKUL_ZO_UNCHARGED);
		if (ring != null) {
			int chargesToAdd = Utils.clampI(player.getInventory().getAmountOf(TOKKUL)/16, 0, 3000);
			if (chargesToAdd > 0) {
				player.getInventory().deleteItem(TOKKUL, chargesToAdd * 16);
				ring.setId(TOKKUL_ZO_CHARGED);
				ring.addMetaData("tzhaarCharges", chargesToAdd);
				player.getInventory().refresh();
				player.sendMessage("TzHaar-Mej-Jeh adds " + chargesToAdd + " charges to your ring in exchange for " + Utils.formatNumber(chargesToAdd * 16) + " Tokkul.");
			}
			return;
		}
		ring = player.getItemWithPlayer(TOKKUL_ZO_CHARGED);
		if (ring != null) {
			int charges = ring.getMetaDataI("tzhaarCharges", -1);
			int chargesToAdd = Utils.clampI(player.getInventory().getAmountOf(TOKKUL)/16, 0, 3000-charges);
			if (chargesToAdd > 0) {
				player.getInventory().deleteItem(TOKKUL, chargesToAdd * 16);
				ring.addMetaData("tzhaarCharges", charges + chargesToAdd);
			}
			player.sendMessage("TzHaar-Mej-Jeh adds " + chargesToAdd + " charges to your ring in exchange for " + Utils.formatNumber(chargesToAdd * 16) + " Tokkul.");
		}
	}

	public static boolean depleteTokkulZo(Player player) {
		Item ring = player.getItemWithPlayer(TOKKUL_ZO_CHARGED);
		if (ring != null && ring.getId() == TzHaar.TOKKUL_ZO_CHARGED) {
			int charges = ring.getMetaDataI("tzhaarCharges", -1);
			if (charges <= 1) {
				ring.setId(TzHaar.TOKKUL_ZO_UNCHARGED);
				ring.deleteMetaData();
				player.getEquipment().refresh(Equipment.RING);
				player.getInventory().refresh();
				player.sendMessage("<col=FF0000>Your Tokkul-Zo has degraded and requires recharging.");
				return false;
			}
			ring.addMetaData("tzhaarCharges", charges - 1);
			return true;
		}
		return false;
	}

}
