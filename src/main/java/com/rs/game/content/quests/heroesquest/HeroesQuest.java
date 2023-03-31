package com.rs.game.content.quests.heroesquest;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.content.quests.shieldofarrav.ShieldOfArrav;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestManager;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemAddedToInventoryHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@QuestHandler(Quest.HEROES_QUEST)
@PluginEventHandler
public class HeroesQuest extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int GET_ITEMS = 1;
	public final static int QUEST_COMPLETE = 2;


	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case NOT_STARTED -> {
				lines.add("You will first have to prove you are worthy to enter the");
				lines.add("Heroes' Guild. To prove your status as a hero, you will");
				lines.add("need to obtain a number of items. There are many challenges");
				lines.add("standing between you and these items.");
				lines.add("");
				lines.add("~~~Quest Requirements~~~");
				lines.add((player.getQuestManager().getQuestPoints() >= 56 ? "<str>" : "") + "56 Quest Points");
				lines.add((player.isQuestComplete(Quest.SHIELD_OF_ARRAV) ? "<str>" : "") + "Shield Of Arrav");
				lines.add((player.isQuestComplete(Quest.LOST_CITY) ? "<str>" : "") + "Lost City");
				lines.add((player.isQuestComplete(Quest.DRAGON_SLAYER) ? "<str>" : "") + "Dragon Slayer");
				lines.add((player.isQuestComplete(Quest.MERLINS_CRYSTAL) ? "<str>" : "") + "Merlin's Crystal");
				lines.add((player.isQuestComplete(Quest.DRUIDIC_RITUAL) ? "<str>" : "") + "Druidic Ritual");
				lines.add("");
				lines.add("~~~Skill Requirements~~~");
				lines.add((player.getSkills().getLevel(Constants.COOKING) >= 53 ? "<str>" : "") + "53 Cooking");
				lines.add((player.getSkills().getLevel(Constants.FISHING) >= 53 ? "<str>" : "") + "53 Fishing");
				lines.add((player.getSkills().getLevel(Constants.HERBLORE) >= 25 ? "<str>" : "") + "25 Herblore");
				lines.add((player.getSkills().getLevel(Constants.DEFENSE) >= 25 ? "<str>" : "") + "25 Defence");
				lines.add((player.getSkills().getLevel(Constants.MINING) >= 50 ? "<str>" : "") + "50 Mining");
				lines.add("");
				if (meetsRequirements(player)) {
					lines.add("You meet the requirements for this quest!");
					lines.add("");
				}
			}
			case GET_ITEMS -> {
				if (!player.getInventory().containsItem(2149, 1)) { //Lava Eel
					lines.add("You need a cooked Lava Eel. Maybe Garrent in");
					lines.add("Port Sarim can help?");
					lines.add("");
				} else {
					lines.add("You got the cooked Lava Eeel, finally!");
					lines.add("");
				}

				if (!player.getInventory().containsItem(1583, 1)) {
					lines.add("You need a fire feather. They can be found");
					lines.add("by killing a fire bird in Entrana. Rumor is");
					lines.add("you need ice gloves from the ice queen under");
					lines.add("White Wolf Mountain.");
					lines.add("");
				} else {
					lines.add("You finally got the fire feather!");
					lines.add("");
				}

				if (player.getInventory().containsItem(1579, 1)) {
					lines.add("Finally, you got the master thieves armband!");
					lines.add("");
				} else {
					if (ShieldOfArrav.hasGang(player)) {
						if (ShieldOfArrav.isPhoenixGang(player)) {
							lines.add("To get the master thieves armband you");
							lines.add("should talk to Straven for a mission...");
						}
						if (ShieldOfArrav.isBlackArmGang(player)) {
							if(player.getQuestManager().getAttribs(Quest.HEROES_QUEST).getB("black_arm_trick")) {
								lines.add("The black arm gang has given me a mission to steal Pete's");
								lines.add("candle sticks. I am to sneak in as a guard in black armour");
								lines.add("by giving my paper ID then finding the chest with the sticks.");
							} else {
								lines.add("To get the master thieves armband you");
								lines.add("should talk to Katrine for a mission...");
							}
						}
					} else {
						lines.add("Error, you don't have a gang, contact an admin!");
					}
					lines.add("");
				}
				lines.add("");
			}
			case QUEST_COMPLETE -> {
				lines.add("");
				lines.add("");
				lines.add("QUEST COMPLETE!");
			}
			default -> {
				lines.add("Invalid quest stage. Report this to an administrator.");
			}
		}
		return lines;
	}

	public static boolean meetsRequirements(Player p) {
		QuestManager questManager = p.getQuestManager();
		Skills skills = p.getSkills();
		boolean[] requirements = new boolean[]{
				questManager.getQuestPoints() >= 56,
				questManager.isComplete(Quest.SHIELD_OF_ARRAV),
				questManager.isComplete(Quest.LOST_CITY),
				questManager.isComplete(Quest.DRAGON_SLAYER),
				questManager.isComplete(Quest.MERLINS_CRYSTAL),
				questManager.isComplete(Quest.DRUIDIC_RITUAL),
				skills.getLevel(Constants.COOKING) >= 53,
				skills.getLevel(Constants.FISHING) >= 53,
				skills.getLevel(Constants.HERBLORE) >= 25,
				skills.getLevel(Constants.DEFENSE) >= 25,
				skills.getLevel(Constants.MINING) >= 50,
		};
		for (boolean hasRequirement : requirements)
			if (!hasRequirement)
				return false;
		return true;
	}

	public static ItemAddedToInventoryHandler handleFireFeather = new ItemAddedToInventoryHandler(1583, e -> {
		Player p = e.getPlayer();
		if (p.getEquipment().getGlovesId() == 1580)//ice gloves
			return;
		else {
			e.cancel();
			World.addGroundItem(new Item(1583, 1), Tile.of(p.getTile()));
			p.startConversation(new Dialogue().addSimple("The feather is too hot to pick up with your bare hands..."));
		}
	});

	public static ItemOnItemHandler handleMakeOilyRod = new ItemOnItemHandler(new int[]{1582}, new int[]{307}, e -> {
		int rod_slot = e.getItem1().getId() == 307 ? e.getItem1().getSlot() : e.getItem2().getSlot();
		int oil_slot = e.getItem1().getId() == 1582 ? e.getItem1().getSlot() : e.getItem2().getSlot();
		e.getPlayer().getInventory().deleteItem(oil_slot, new Item(1582, 1));
		e.getPlayer().getInventory().replaceItem(1585, 1, rod_slot);
	});

	public static ItemOnItemHandler handlePromptHarllander = new ItemOnItemHandler(new int[]{1581}, new int[]{307}, e -> e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.CALM_TALK, "I'll need to add unfinished Harralander to the slime before I make it oily...")));

	public static ItemClickHandler handleClickBlamishOil = new ItemClickHandler(new Object[] { 1582 }, e -> {
		Player p = e.getPlayer();
		if (e.getOption().equalsIgnoreCase("drop")) {
			p.getInventory().removeItems(e.getItem());
			World.addGroundItem(e.getItem(), Tile.of(e.getPlayer().getTile()), e.getPlayer());
			e.getPlayer().soundEffect(2739);
			return;
		}
		p.sendMessage("You know... I'd really rather not.");
	});

	@Override
	public void complete(Player player) {
		Object[][] xpAdded = {{Constants.ATTACK, 3075}, {Constants.DEFENSE, 3075}, {Constants.STRENGTH, 3075}, {Constants.HITPOINTS, 3075},
				{Constants.RANGE, 2075}, {Constants.FISHING, 2725}, {Constants.COOKING, 2825}, {Constants.WOODCUTTING, 1575}, {Constants.FIREMAKING, 1575},
				{Constants.SMITHING, 2257}, {Constants.MINING, 2575}, {Constants.HERBLORE, 1325}};
		for (int i = 0; i < xpAdded.length; i++) {
			player.sendMessage("You have gained " + xpAdded[i][1] + " in " + Skills.SKILL_NAME[(int) xpAdded[i][0]] + ".");
			player.getSkills().addXpQuest((int) xpAdded[i][0], (int) xpAdded[i][1]);
		}
		getQuest().sendQuestCompleteInterface(player, 1377, "Access to the heroes guild", "Access to Heroes Guild Shop", "Total of 29,232XP over twelve skills");
	}

}
