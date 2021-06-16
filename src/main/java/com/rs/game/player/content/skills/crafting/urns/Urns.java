package com.rs.game.player.content.skills.crafting.urns;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.statements.MakeXStatement;
import com.rs.game.player.content.skills.magic.Rune;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.NPCDropEvent;
import com.rs.plugin.events.XPGainEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.NPCDropHandler;
import com.rs.plugin.handlers.XPGainHandler;

@PluginEventHandler
public class Urns {
	
	public static ItemClickHandler handleTeleportUrns = new ItemClickHandler(Urn.FULL_IDS.keySet().toArray(), new String[] { "Teleport urn" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Urn urn = Urn.forFullId(e.getItem().getId());
			e.getPlayer().getInventory().deleteItem(e.getItem().getId(), 1);
			e.getPlayer().setNextAnimation(urn.getTeleAnim());
			e.getPlayer().getSkills().addXpLamp(urn.getSkill(), urn.getTeleXp());
		}
	};
	
	public static ItemClickHandler handleCheckUrns = new ItemClickHandler(Urn.FILL_IDS.keySet().toArray(), new String[] { "Check level" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Urn urn = Urn.forFillId(e.getItem().getId());
			e.getPlayer().sendMessage("The urn is filled " + Utils.formatDouble(e.getItem().getMetaDataD("xp") / urn.getFillXp() * 100) + "%.");
		}
	};
	
	public static ItemOnItemHandler addRune = new ItemOnItemHandler(Urn.NR_IDS.keySet().stream().mapToInt(i->i).toArray()) {
		@Override
		public void handle(ItemOnItemEvent e) {
			Urn u = Urn.forNRId(e.getItem1().getId());
			if (u == null)
				u = Urn.forNRId(e.getItem2().getId());
			if (u == null)
				return;
			final Urn urn = u;
			Rune rune = Rune.forId(e.getUsedWith(urn.nrId()).getId());
			if (rune == urn.getRune()) {
				e.getPlayer().startConversation(new Conversation(e.getPlayer())
						.addNext(new MakeXStatement(new int[] { urn.rId() }))
						.addNext(() -> {
							int amount = e.getPlayer().getInventory().getNumberOf(urn.nrId());
							if (amount > e.getPlayer().getInventory().getNumberOf(urn.getRune().id()))
								amount = e.getPlayer().getInventory().getNumberOf(urn.getRune().id());
							e.getPlayer().getInventory().deleteItem(urn.nrId(), amount);
							e.getPlayer().getInventory().deleteItem(urn.getRune().id(), amount);
							e.getPlayer().getInventory().addItem(urn.rId(), amount);
							e.getPlayer().getSkills().addXpLamp(urn.getSkill(), amount);
							e.getPlayer().setNextAnimation(urn.getReadyAnim());
						}));
			} else
				e.getPlayer().sendMessage("You must use " + Utils.addArticle(urn.getRune().name().toLowerCase()) + " rune to activate this urn.");
		}
	};
	
	public static XPGainHandler handleUrnXp = new XPGainHandler() {
		@Override
		public void handle(XPGainEvent e) {
			switch(e.getSkillId()) {
			case Constants.SMITHING:
			case Constants.WOODCUTTING:
			case Constants.FISHING:
			case Constants.COOKING:
			case Constants.MINING:
				addXPToUrn(e.getPlayer(), getUrn(e.getPlayer(), e.getSkillId()), e.getXp());
				break;
			}
		}
	};
	
	public static NPCDropHandler handle = new NPCDropHandler(20264, 20266, 20268) {
		@Override
		public void handle(NPCDropEvent e) {
			switch(e.getItem().getId()) {
			case 20264:
				if (addXPToUrn(e.getPlayer(), getUrn(e.getPlayer(), Urn.IMPIOUS, Urn.ACCURSED, Urn.INFERNAL), 4))
					e.deleteItem();
				break;
			case 20266:
				if (addXPToUrn(e.getPlayer(), getUrn(e.getPlayer(), Urn.ACCURSED, Urn.INFERNAL), 12.5))
					e.deleteItem();
				break;
			case 20268:
				if (addXPToUrn(e.getPlayer(), getUrn(e.getPlayer(), Urn.INFERNAL), 62.5))
					e.deleteItem();
				break;
			}
		}
	};
	
	public static Urn getUrn(Player player, int skillId) {
		for (Urn urn : Urn.values()) {
			if (urn.getSkill() == skillId && player.getInventory().containsOneItem(urn.rId(), urn.fillId()))
				return urn;
		}
		return null;
	}
	
	public static Urn getUrn(Player player, Urn... urns) {
		for (Urn urn : urns) {
			if (player.getInventory().containsOneItem(urn.rId(), urn.fillId()))
				return urn;
		}
		return null;
	}
	
	public static boolean addXPToUrn(Player player, Urn urn, double xp) {
		if (urn == null)
			return false;
		Item item = player.getItemFromInv(urn.fillId());
		if (item != null) {
			double newXp = item.getMetaDataD("xp") + xp;
			if (newXp > urn.getFillXp()) {
				newXp = urn.getFillXp();
				player.incrementCount(item.getName() + " filled");
				item.setId(urn.fullId());
				item.deleteMetaData();
				player.getInventory().refresh();
			} else
				item.addMetaData("xp", newXp);
			player.sendMessage("<col=e69d00><shad=000000>Your urn is filled " + Utils.formatDouble(newXp / urn.getFillXp() * 100) + "%.", true);
			return true;
		}
		item = player.getItemFromInv(urn.rId());
		if (item != null) {
			if (!player.getInventory().hasFreeSlots())
				return false;
			player.getInventory().deleteItem(urn.rId(), 1);
			Item fUrn = new Item(urn.fillId(), 1);
			fUrn.addMetaData("xp", xp);
			player.sendMessage("<col=e69d00><shad=000000>You start a new urn.", true);
			player.getInventory().addItem(fUrn);
			return true;
		}
		return false;
	}
}
