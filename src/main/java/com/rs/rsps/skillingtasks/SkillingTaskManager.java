package com.rs.rsps.skillingtasks;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.skills.Fletching;
import com.rs.game.player.content.skills.cooking.Cooking;
import com.rs.game.player.content.skills.crafting.GemCutting;
import com.rs.game.player.content.skills.farming.HarvestPatch;
import com.rs.game.player.content.skills.fishing.Fishing;
import com.rs.game.player.content.skills.herblore.Herblore;
import com.rs.game.player.content.skills.mining.Mining;
import com.rs.game.player.content.skills.smithing.Smithing;
import com.rs.game.player.content.skills.woodcutting.Woodcutting;
import com.rs.lib.game.Item;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemAddedToInventoryEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ItemAddedToInventoryHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import java.util.ArrayList;
import java.util.Collections;

@PluginEventHandler
public class SkillingTaskManager {
	
	public enum Difficulty {
		EASY,
		MEDIUM,
		HARD,
		ELITE;
	}
	
	public enum SkillingTask {
		LOGS(1511, 1, Difficulty.EASY, 11, 11, Woodcutting.class, Skills.WOODCUTTING, "chop"),
		OAK_LOGS(1521, 15, Difficulty.EASY, 1, 25, Woodcutting.class, Skills.WOODCUTTING, "chop"),
		WILLOW_LOGS(1519, 30, Difficulty.EASY, 1, 25, Woodcutting.class, Skills.WOODCUTTING, "chop"),
		TEAK_LOGS(6333, 35, Difficulty.MEDIUM, -1, -1, Woodcutting.class, Skills.WOODCUTTING, "chop"),
		MAPLE_LOGS(1517, 45, Difficulty.MEDIUM, -1, -1, Woodcutting.class, Skills.WOODCUTTING, "chop"),
		MAHAGONY_LOGS(6332, 50, Difficulty.MEDIUM, -1, -1, Woodcutting.class, Skills.WOODCUTTING, "chop"),
		YEW_LOGS(1515, 60, Difficulty.HARD, -1, -1, Woodcutting.class, Skills.WOODCUTTING, "chop"),
		MAGIC_LOGS(1513, 75, Difficulty.ELITE, -1, -1, Woodcutting.class, Skills.WOODCUTTING, "chop"),

		SHRIMP(317, 1, Difficulty.EASY, 11, 11, Fishing.class, Skills.FISHING, "fish"),
		TROUT(335, 20, Difficulty.EASY, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		SALMON(331, 30, Difficulty.EASY, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		TUNA(359, 35, Difficulty.MEDIUM, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		LOBSTER(377, 40, Difficulty.MEDIUM, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		SWORDFISH(371, 50, Difficulty.MEDIUM, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		MONKFISH(7944, 62, Difficulty.HARD, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		SHARK(383, 76, Difficulty.HARD, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		CAVEFISH(15264, 85, Difficulty.ELITE, -1, -1, Fishing.class, Skills.FISHING, "fish"),
		ROCKTAIL(15270, 90, Difficulty.ELITE, -1, -1, Fishing.class, Skills.FISHING, "fish"),

		RAW_CHICKEN(2140, 1, Difficulty.EASY, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_SHRIMP(315, 1, Difficulty.EASY, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_TROUT(333, 15, Difficulty.EASY, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_SALMON(329, 25, Difficulty.EASY, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_TUNA(361, 30, Difficulty.MEDIUM, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_LOBSTER(379, 40, Difficulty.MEDIUM, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_SWORDFISH(373, 45, Difficulty.MEDIUM, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_MONKFISH(7946, 62, Difficulty.HARD, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_SHARK(385, 80, Difficulty.HARD, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_CAVEFISH(15266, 88, Difficulty.ELITE, -1, -1, Cooking.class, Skills.COOKING, "cook"),
		RAW_ROCKTAIL(15272, 93, Difficulty.ELITE, -1, -1, Cooking.class, Skills.COOKING, "cook"),

		RUNE_ESSENCE(1436, 1, Difficulty.EASY, -1, -1, Mining.class, Skills.MINING, "mine"),
		COPPER_ORE(436, 1, Difficulty.EASY, -1, -1, Mining.class, Skills.MINING, "mine"),
		TIN_ORE(438, 1, Difficulty.EASY, -1, -1, Mining.class, Skills.MINING, "mine"),
		IRON_ORE(440, 15, Difficulty.EASY, -1, -1, Mining.class, Skills.MINING, "mine"),
		COAL(453, 30, Difficulty.MEDIUM, -1, -1, Mining.class, Skills.MINING, "mine"),
		PURE_ESSENCE(7936, 30, Difficulty.MEDIUM, -1, -1, Mining.class, Skills.MINING, "mine"),
		GOLD_ORE(444, 40, Difficulty.MEDIUM, -1, -1, Mining.class, Skills.MINING, "mine"),
		MITHRIL_ORE(447, 55, Difficulty.HARD, -1, -1, Mining.class, Skills.MINING, "mine"),
		ADAMANTITE_ORE(449, 70, Difficulty.HARD, -1, -1, Mining.class, Skills.MINING, "mine"),
		RUNITE_ORE(451, 85, Difficulty.ELITE, -1, -1, Mining.class, Skills.MINING, "mine"),

		BRONZE_BARS(2349, 1, Difficulty.EASY, -1, -1, Smithing.class, Skills.SMITHING, "smelt"),
		IRON_BARS(2351, 15, Difficulty.EASY, -1, -1, Smithing.class, Skills.SMITHING, "smelt"),
		STEEL_BARS(2353, 30, Difficulty.MEDIUM, -1, -1, Smithing.class, Skills.SMITHING, "smelt"),
		GOLD_BARS(2357, 40, Difficulty.MEDIUM, -1, -1, Smithing.class, Skills.SMITHING, "smelt"),
		MITHRIL_BARS(2359, 50, Difficulty.HARD, -1, -1, Smithing.class, Skills.SMITHING, "smelt"),
		ADAMANT_BARS(2361, 70, Difficulty.HARD, -1, -1, Smithing.class, Skills.SMITHING, "smelt"),
		RUNE_BARS(2363, 85, Difficulty.ELITE, -1, -1, Smithing.class, Skills.SMITHING, "smelt"),

		BRONZE_PLATEBODIES(1117, 18, Difficulty.EASY, -1, -1, Smithing.class, Skills.SMITHING, "smith"),
		IRON_PLATEBODIES(1115, 33, Difficulty.EASY, -1, -1, Smithing.class, Skills.SMITHING, "smith"),
		STEEL_PLATEBODIES(1119, 48, Difficulty.MEDIUM, -1, -1, Smithing.class, Skills.SMITHING, "smith"),
		MITHRIL_PLATEBODIES(1121, 68, Difficulty.HARD, -1, -1, Smithing.class, Skills.SMITHING, "smith"),
		ADAMANT_PLATEBODIES(1123, 88, Difficulty.HARD, -1, -1, Smithing.class, Skills.SMITHING, "smith"),
		RUNITE_PLATEBODIES(1127, 99, Difficulty.ELITE, -1, -1, Smithing.class, Skills.SMITHING, "smith"),
		
		OPAL(1609, 1, Difficulty.EASY, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		JADE(1611, 13, Difficulty.EASY, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		TOPAZ(1613, 16, Difficulty.EASY, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		SAPPHIRE(1607, 20, Difficulty.MEDIUM, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		EMERALD(1605, 27, Difficulty.MEDIUM, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		RUBY(1603, 34, Difficulty.MEDIUM, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		DIAMOND(1601, 43, Difficulty.MEDIUM, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		DRAGONSTONE(1615, 55, Difficulty.HARD, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),
		ONYX(6573, 67, Difficulty.ELITE, -1, -1, GemCutting.class, Skills.CRAFTING, "cut"),

		GUAM(199, 9, Difficulty.EASY, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		MARRENTILL(201, 14, Difficulty.EASY, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		TARROMIN(203, 19, Difficulty.EASY, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		HARRALANDER(205, 26, Difficulty.EASY, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		RANARR(207, 32, Difficulty.MEDIUM, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		TOADFLAX(3049, 38, Difficulty.MEDIUM, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		IRITS(209, 44, Difficulty.MEDIUM, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		AVANTOE(211, 50, Difficulty.MEDIUM, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		KWUARM(213, 56, Difficulty.MEDIUM, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		SNAPDRAGON(3051, 62, Difficulty.HARD, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		CADANTINE(215, 67, Difficulty.HARD, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		LANTADYME(2485, 73, Difficulty.HARD, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		DWARF_WEED(217, 79, Difficulty.HARD, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		TORSTOL(219, 85, Difficulty.ELITE, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),
		FELLSTALK(21626, 91, Difficulty.ELITE, -1, -1, HarvestPatch.class, Skills.FARMING, "harvest"),

		UNSTRUNG_SHORTBOWS(50, 1, Difficulty.EASY, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_LONGBOWS(48, 10, Difficulty.EASY, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_OAK_SHORTBOWS(54, 20, Difficulty.EASY, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_OAK_LONGBOWS(56, 25, Difficulty.EASY, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_WILLOW_SHORTBOWS(60, 35, Difficulty.MEDIUM, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_WILLOW_LONGBOWS(58, 40, Difficulty.MEDIUM, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_MAPLE_SHORTBOWS(64, 50, Difficulty.MEDIUM, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_MAPLE_LONGBOWS(62, 55, Difficulty.MEDIUM, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_YEW_SHORTBOWS(68, 65, Difficulty.HARD, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_YEW_LONGBOWS(66, 70, Difficulty.HARD, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_MAGIC_SHORTBOWS(72, 80, Difficulty.ELITE, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),
		UNSTRUNG_MAGIC_LONGBOWS(70, 85, Difficulty.ELITE, -1, -1, Fletching.class, Skills.FLETCHING, "fletch"),

		ATTACK_POTIONS(121, 1, Difficulty.EASY, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		STRENGTH_POTIONS(115, 12, Difficulty.EASY, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		DEFENCE_POTIONS(133, 30, Difficulty.EASY, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		PRAYER_POTIONS(139, 38, Difficulty.EASY, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SUPER_ATTACK_POTIONS(145, 45, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SUPER_ANTIPOISON_POTIONS(181, 48, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SUPER_STRENGTH_POTIONS(157, 55, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SUPER_RESTORE_POTIONS(3026, 63, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SUPER_DEFENCE_POTIONS(163, 66, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		ANTIFIRE_POTIONS(2454, 69, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		RANGING_POTIONS(169, 72, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		MAGIC_POTIONS(3040, 76, Difficulty.MEDIUM, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SARADOMIN_BREWS(6687, 81, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		RECOVER_SPECIAL_POTIONS(14484, 84, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SUPER_ANTIFIRE_POTIONS(15305, 85, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		EXTREME_ATTACK_POTIONS(15309, 88, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		EXTREME_STRENGTH_POTIONS(15313, 89, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		EXTREME_DEFENCE_POTIONS(15317, 90, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		EXTREME_MAGIC_POTIONS(15321, 91, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		EXTREME_RANGING_POTIONS(15325, 92, Difficulty.HARD, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		SUPER_PRAYER_POTIONS(15329, 94, Difficulty.ELITE, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		PRAYER_RENEWAL_POTIONS(21632, 94, Difficulty.ELITE, -1, -1, Herblore.class, Skills.HERBLORE, "mix"),
		OVERLOADS(15333, 96, Difficulty.ELITE, -1, -1, Herblore.class, Skills.HERBLORE, "mix");

		private int itemId;
		private int minLevel;
		private Difficulty difficulty;
		private int[] minMaxQuantity;
		private Class<? extends Action> action;
		private int skill;
		private String actionString;
		
		private SkillingTask(int itemId, int minLevel, Difficulty difficulty, int min, int max, Class<? extends Action> action, int skill, String actionString) {
			this.itemId = itemId;
			this.minLevel = minLevel;
			this.difficulty = difficulty;
			this.minMaxQuantity = new int[] { min, max };
			this.action = action;
			this.skill = skill;
			this.actionString = actionString;
		}
		
		public int getItemId() {
			return itemId;
		}
		
		public int getMinLevel() {
			return minLevel;
		}
		
		public Difficulty getDifficulty() {
			return difficulty;
		}
		
		public int getMinQuantity() {
			return minMaxQuantity[0];
		}
		
		public int getMaxQuantity() {
			return minMaxQuantity[1];
		}
		
		public Class<? extends Action> getAction() {
			return action;
		}
		
		public int getSkill() {
			return skill;
		}
		
		public String getActionString() {
			return actionString;
		}
	}
	
	private final static int taskMaster = 14858;
	private final static int skillingTickets = 13663;
	
	public static SkillingTask getCurrentSkillingTask(Player p) {
		return p.getO("skillingTask");
	}
	
	public static void setCurrentSkillingTask(Player p, SkillingTask task) {
		p.save("skillingTask", task);
	}
	
	public static int getActionsRemaining(Player p) {
		return p.getI("skillingActionsRemaining");
	}
	
	public static void setActionsRemaining(Player p, int num) {
		if (num < 0)
			return;
		p.save("skillingActionsRemaining", num);
	}
	
	public static int getConsecutiveTasks(Player p) {
		if (p.getI("skillingTasksConsecutively") < 0)
			return 0;
		return p.getI("skillingTasksConsecutively");
	}
	
	public static void setConsecutiveTasks(Player p, int num) {
		if (num < 0)
			return;
		p.save("skillingTasksConsecutively", num);
	}
	
	public static ItemAddedToInventoryHandler onItemAdd = new ItemAddedToInventoryHandler(1511, 1519, 1521, 317) {

		@Override
		public void handle(ItemAddedToInventoryEvent e) {
			SkillingTask task = getCurrentSkillingTask(e.getPlayer());
			int actionsRemaining = getActionsRemaining(e.getPlayer());
			
			if (task == null || e.getItem().getId() != task.getItemId() || !e.getPlayer().getActionManager().doingAction(task.getAction()))
				return;
		
			actionsRemaining--;
			setActionsRemaining(e.getPlayer(), actionsRemaining);
			
			if (actionsRemaining % 10 == 0 && actionsRemaining != 0)
				e.getPlayer().sendMessage("<col=FF0000><shad=000000>" + "You're doing great, only " + actionsRemaining + " " + task.name().toString().toLowerCase().replace("_", " ") + " left to " + task.getActionString());
			
			if (actionsRemaining == 0)
				completeTask(e.getPlayer());
		}
		
	};

	public static void completeTask(Player p) {
		if (getCurrentSkillingTask(p) == null)
			return;
		
		int tasksInARow = getConsecutiveTasks(p)+1;
		setConsecutiveTasks(p, tasksInARow);

		int amount = ((tasksInARow % 50 == 0) ? 50 : (tasksInARow % 10 == 0) ? 10 : 1);	
		Item taskReward = new Item(skillingTickets, amount);
		p.sendMessage("You have completed " + tasksInARow + " tasks in a row and receive " + amount + " skilling tickets!");

		
		if (p.getInventory().hasRoomFor(taskReward))
			p.getInventory().addItem(taskReward);
		else {
			p.getBank().addItem(taskReward, true);
			p.sendMessage("Since your inventory is full, your skilling tickets have been sent directly to your bank!");
		}
		
		p.sendMessage("You have finished your skilling task, talk to " + NPCDefinitions.getDefs(taskMaster).getName(p.getVars()) + " for a new one.");
		p.incrementCount("Skilling tasks completed");
		
		setCurrentSkillingTask(p, null);
		setActionsRemaining(p, 0);
	}
	
	public static void generateNewTask(Player p, Difficulty difficulty) {
		SkillingTask task = getCurrentSkillingTask(p);
		if (task != null)
			return;

		ArrayList<SkillingTask> possibleTasks = new ArrayList<SkillingTask>();
		for (SkillingTask t : SkillingTask.values()) {
			if (p.getSkills().getLevelForXp(t.getSkill()) < t.getMinLevel() || t.getDifficulty() != difficulty)
				continue;
			possibleTasks.add(t);
		}
		
		if (possibleTasks.size() <= 0) {
			p.sendMessage("Unable to find possible tasks, please contact an admin.");
			return;
		}
		
		Collections.shuffle(possibleTasks);
		task = possibleTasks.get(Utils.random(possibleTasks.size()-1));
		int amount =Utils.random(task.getMinQuantity(), task.getMaxQuantity());
		setCurrentSkillingTask(p, task);
		setActionsRemaining(p, amount);
		p.startConversation(new Dialogue().addSimple("Your new task is to " + task.getActionString() + " " + amount + " " + task.name().toString().toLowerCase().replace("_", " ")));
	}
	
	public static NPCClickHandler handleTaskMaster = new NPCClickHandler(new Object[]{ 100000 }) { //TODO NPC id
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getOption().contains("Talk-to")) {
				SkillingTask task = getCurrentSkillingTask(e.getPlayer());
				int actionsRemaining = getActionsRemaining(e.getPlayer());
				
				Dialogue taskMasterD = new Dialogue();
				taskMasterD.addOptions(new Options() {
					@Override
					public void create() {
						option("Get new task", new Dialogue().addOptions("Select a difficulty", new Options() {	
							@Override
							public void create() {
								if (task != null) {
									e.getPlayer().startConversation(new Dialogue().addSimple("You have already been assigned a skilling task."));
									return;
								}
								option("Easy", () -> { generateNewTask(e.getPlayer(), Difficulty.EASY); });
								option("Medium", () -> { generateNewTask(e.getPlayer(), Difficulty.MEDIUM); });
								option("Hard", () -> { generateNewTask(e.getPlayer(), Difficulty.HARD); });
								option("Elite", () -> { generateNewTask(e.getPlayer(), Difficulty.ELITE); });
							}
						}));
						if (task != null)
							option("Check task", new Dialogue().addNPC(taskMaster, HeadE.CALM, "You're doing great, only " + actionsRemaining + " " + task.name().toString().toLowerCase().replace("_", " ") + (actionsRemaining > 1 ? "s" : "") + " left to " + task.getActionString()));
						if (task != null) {
							option("Cancel task", () -> { 
								if (!e.getPlayer().getInventory().containsItem(skillingTickets, 5)) {
									e.getPlayer().startConversation(new Dialogue()
											.addNPC(taskMaster, HeadE.CALM, "You seem to be running low on skilling tickets. I suppose I could give you another task but I'd have to end your streak.")
											.addOption("Get a new task?", "Yes, end my streak.", "No thanks.")
											.addNext(() -> {
												setCurrentSkillingTask(e.getPlayer(), null);
												setActionsRemaining(e.getPlayer(), -1);
												setConsecutiveTasks(e.getPlayer(), 0);
											})
									);
								} else {
									e.getPlayer().startConversation(new Dialogue()
											.addNPC(taskMaster, HeadE.CALM, "I suppose I could give you a new task in exchange for 5 skilling tickets.")
											.addOption("Get a new task?", "Yes, pay 5 skilling tickets.", "No thanks.")
											.addNext(() -> {
												e.getPlayer().getInventory().deleteItem(skillingTickets, 5);
												setCurrentSkillingTask(e.getPlayer(), null);
												setActionsRemaining(e.getPlayer(), -1);
											})
									);
								}
							});
						}
						option("Open shop", () -> { e.getPlayer().startConversation(new Dialogue().addSimple("Shop not yet added")); });
					}
				});
				e.getPlayer().startConversation(taskMasterD);
			} else if (e.getOption().contains("Trade")) {
				e.getPlayer().startConversation(new Dialogue().addSimple("Shop not yet added"));
			}
		}
	};
}
