package com.rs.game.content.quests.handlers.familycrest;

import java.util.ArrayList;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.player.Equipment;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.ItemOnObjectEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.ItemOnObjectHandler;

@QuestHandler(Quest.FAMILY_CREST)
@PluginEventHandler
public class FamilyCrest extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int TALK_TO_CALEB = 1;
	public final static int TALK_TO_GEM_TRADER = 2;
	public final static int TALK_TO_AVAN = 3;
	public final static int TALK_TO_BOOT = 4;
	public final static int GIVE_AVAN_JEWLERY = 5;
	public final static int TALK_TO_JOHNATHAN = 6;
	public final static int KILL_CHRONOZON = 7;
	public final static int QUEST_COMPLETE = 8;

	//items
	public final static int PERFECT_RUBY_RING = 773;
	public final static int PERFECT_RUBY_NECKLACE = 774;
	public final static int CALEB_CREST = 779;
	public final static int AVAN_CREST = 780;
	public final static int JOHNATHAN_CREST = 781;
	public final static int FAMILY_CREST = 782;
	public final static int COOKING_GAUNTLETS = 775;
	public final static int GOLDSMITH_GAUNTLETS = 776;
	public final static int CHAOS_GAUNTLETS = 777;
	public final static int FAMILY_GAUNTLETS = 778;

	//Attr
	public static final String CALEB_ASKED_ATTR = "CALEB_ASKED";

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("There is a man in Varrock who needs a bit of help.");
			lines.add("He is of noble heritage, but without his family's");
			lines.add("crest, he can't prove it. Unfortunately, his three");
			lines.add("sons took the crest with them when they left,");
			lines.add("scattering all across RuneScape.");
			lines.add("");
			lines.add("~~~Requirements~~~");
			lines.add("40 Mining");
			lines.add("40 Smithing");
			lines.add("59 Magic");
			lines.add("40 Crafting");
			lines.add("");
			break;
		case TALK_TO_CALEB:
			lines.add("Speak to Caleb in Catherby to ask about the family");
			lines.add("crest.");
			lines.add("");
			if(player.getQuestManager().getAttribs(Quest.FAMILY_CREST).getB(CALEB_ASKED_ATTR)) {
				lines.add("I must get Caleb cooked shrimp, salmon, tuna, bass and");
				lines.add("swordfish to get a piece of the crest.");
				lines.add("");
			}
			break;
		case TALK_TO_GEM_TRADER:
			lines.add("Caleb says Avan is looking for treasure in Al-Kharid. ");
			lines.add("Talk to the gem trader in town to learn more.");
			lines.add("");
			break;
		case TALK_TO_AVAN:
			lines.add("Avan is looking for treasure by the mines in north");
			lines.add("Al-Kharid.");
			lines.add("");
			break;
		case TALK_TO_BOOT:
			lines.add("Avan says he is looking for the 'perfect' gold. He");
			lines.add("has a solid lead on the ore from a dwarf named Boot");
			lines.add("in the Dwarven Mines of Falador. You must talk to");
			lines.add("Boot to progress.");
			lines.add("");
			break;
		case GIVE_AVAN_JEWLERY:
			lines.add("Boot said there is 'perfect' ore in the Witchaven dungeon in");
			lines.add("east Ardougne.");
			lines.add("");
			break;
		case TALK_TO_JOHNATHAN:
			lines.add("Now that I have two of three crest pieces I only need");
			lines.add("one more from Johnathon in north east Varrock");
			lines.add("");
			break;
		case KILL_CHRONOZON:
			lines.add("Now I just need to kill Chronozon for the last crest piece");
			lines.add("I can find him somewhere in Edgeville dungeon. Look south");
			lines.add("of the earth obelisk.");
			lines.add("");
			lines.add("Afterwards I just need to put the pieces back together and");
			lines.add("return to Dimintheis.");
			break;
		case QUEST_COMPLETE:
			lines.add("");
			lines.add("");
			lines.add("QUEST COMPLETE!");
			break;
		default:
			lines.add("Invalid quest stage. Report this to an administrator.");
			break;
		}
		return lines;
	}

	public static boolean meetsRequirements(Player p) {
		Skills skill = p.getSkills();
		if(skill.getLevel(Constants.MINING) >= 40)
			if(skill.getLevel(Constants.SMITHING) >= 40)
				if(skill.getLevel(Constants.MAGIC) >= 59)
					if(skill.getLevel(Constants.CRAFTING) >= 40)
						return true;
		return false;
	}

	public static ItemOnItemHandler createActualCrest = new ItemOnItemHandler(new int[]{CALEB_CREST, AVAN_CREST, JOHNATHAN_CREST},
			new int[]{CALEB_CREST, AVAN_CREST, JOHNATHAN_CREST}) {
		@Override
		public void handle(ItemOnItemEvent e) {
			if (e.getPlayer().getInventory().containsItem(CALEB_CREST, 1))
				if (e.getPlayer().getInventory().containsItem(AVAN_CREST, 1))
					if (e.getPlayer().getInventory().containsItem(JOHNATHAN_CREST, 1)) {
						e.getPlayer().getInventory().removeItems(new Item(CALEB_CREST, 1), new Item(AVAN_CREST, 1), new Item(JOHNATHAN_CREST, 1));
						e.getPlayer().getInventory().addItem(new Item(FAMILY_CREST, 1), true);
					}

		}
	};

	public static ItemOnObjectHandler perfectItemsOnFurnace = new ItemOnObjectHandler(true, new Object[] { "Furnace" }) {
		int PERFECT_ORE = 446;
		int PERFECT_BAR = 2365;
		//
		int RUBY = 1603;
		int RING_MOULD = 1592;
		int NECKLACE_MOULD = 1597;
		@Override
		public void handle(ItemOnObjectEvent e) {
			Player p = e.getPlayer();
			if(e.getItem().getId() == PERFECT_ORE) {
				p.getInventory().replaceItem(PERFECT_BAR, 1, e.getItem().getSlot());
				p.setNextAnimation(new Animation(3243));
			}
			if(e.getItem().getId() == PERFECT_BAR)
				p.startConversation(new Conversation(p) {
					{
						addOptions("Choose an option:", new Options() {
							@Override
							public void create() {
								if(p.getInventory().containsItem(RING_MOULD, 1, true) && p.getInventory().containsItem(RUBY, 1))
									option("Make perfect ring", new Dialogue()
											.addNext(()->{
												p.setNextAnimation(new Animation(3243));
												p.getInventory().removeItems(new Item(RUBY, 1));
												p.getInventory().replaceItem(PERFECT_RUBY_RING, 1, e.getItem().getSlot());
											}));
								else
									p.sendMessage("You don't have materials for a perfect ring");
								if(p.getInventory().containsItem(NECKLACE_MOULD, 1, true) && p.getInventory().containsItem(RUBY, 1))
									option("Make perfect neck", new Dialogue()
											.addNext(()->{
												p.setNextAnimation(new Animation(3243));
												p.getInventory().removeItems(new Item(RUBY, 1));
												p.getInventory().replaceItem(PERFECT_RUBY_NECKLACE, 1, e.getItem().getSlot());
											}));
								else
									p.sendMessage("You don't have materials for a perfect necklace");
							}
						});

						create();
					}
				});
		}
	};

	@Override
	public void complete(Player player) {
		if(player.getInventory().hasFreeSlots())
			player.getInventory().addItem(FAMILY_GAUNTLETS, 1);
		getQuest().sendQuestCompleteInterface(player, 778, "Family gauntlets");
	}

	public static ItemClickHandler handleFamilyGauntletsQuestRequirement = new ItemClickHandler(new Object[]{775, 776, 777}, new String[]{"Wear"}) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getPlayer().isEquipDisabled())
				return;
			if (!e.getPlayer().isQuestComplete(Quest.FAMILY_CREST)) {
				e.getPlayer().sendMessage("You must complete the Family Crest quest to use this item...");
				return;
			}
			Equipment.sendWear(e.getPlayer(), e.getSlotId(), e.getItem().getId());
		}
	};

}
