package com.rs.game.content.quests.piratestreasure;

import com.rs.engine.quest.Quest;
import com.rs.engine.quest.QuestHandler;
import com.rs.engine.quest.QuestOutline;
import com.rs.game.World;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

import java.util.ArrayList;
import java.util.List;

@QuestHandler(
		quest = Quest.PIRATES_TREASURE,
		startText = "Speak to Redbeard Frank just south of The Rusty Anchor pub in Port Sarim.",
		itemsText = "White apron, 60 coins (or an activated ring of Charos and 30 coins).",
		combatText = "Optionally defeat a level 4 gardener.",
		rewardsText = "One-Eyed Hector's treasure casket (containing 450 coins, an emerald and a gold ring)<br>Ability to work as a menial labourer on a banana plantation (30 coins for each export crate you fill with 10 bananas)",
		completedStage = 3
)
@PluginEventHandler
public class PiratesTreasure extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int SMUGGLE_RUM = 1;
	public final static int GET_TREASURE = 2;
	public final static int QUEST_COMPLETE = 3;

	//items
	protected final static int SPADE = 952;
	protected final static int RUM = 431;
	protected final static int BANANA = 1963;
	protected final static int PIRATE_MESSAGE = 433;
	protected final static int CHEST_KEY = 432;
	protected final static int CASKET = 7956;
	protected final static int APRON = 1005;

	//NPCs
	protected final static int REDBEARD = 375;
	protected final static int LUTHAS = 379;
	protected final static int WYDIN = 557;
	protected final static int ZAMBO = 568;
	protected final static int HOSTILE_GARDENER = 1217;
	protected final static int CUSTOMS_OFFICER = 380;

	//Objects
	protected final static int BLUE_MOON_INN_CHEST = 2079;

	@Override
	public List<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
            lines.add("You can start this quest at Port Sarim. Redbeard Frank");
            lines.add("knows the location of pirate treasure, but he'll only");
			lines.add("part with the knowledge for a bottle of Karamjan rum.");
            lines.add("");
			break;
		case SMUGGLE_RUM:
			lines.add("To get the pirate treasure I must first help Redbeard");
			lines.add("Frank by smuggling rum for him from Karamja. There is");
			lines.add("a general store that sells the rum by the banana");
			lines.add("plantation,");
			lines.add("");
			break;
		case GET_TREASURE:
			lines.add("Now that Redbeard Frank has his rum he has given me the");
			lines.add("key to a chest on the second floor of the blue moon inn.");
			lines.add("Inside the chest there is word of a map to who knows where?");
			lines.add("");
			break;
		case QUEST_COMPLETE:
			lines.add("I have gotten the Pirate's Treasure!");
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

	public static ObjectClickHandler handleTreasureChest = new ObjectClickHandler(new Object[] {BLUE_MOON_INN_CHEST }, e -> {;
		GameObject obj = e.getObject();
		if(!e.getPlayer().getInventory().containsItem(CHEST_KEY))
			return;
		if(e.getOption().equalsIgnoreCase("open")) {
			e.getPlayer().setNextAnimation(new Animation(536));
			e.getPlayer().lock(2);
			GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
			e.getPlayer().faceObject(openedChest);
			World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
			e.getPlayer().getInventory().addItem(new Item(PIRATE_MESSAGE, 1));
		}
	});

	public static void findTreasure(Player player) {
		if((player.getQuestManager().getStage(Quest.PIRATES_TREASURE) != GET_TREASURE) || !player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("TREASURE_LOC_KNOWN"))
			return;
		if(Utils.getDistance(player.getTile(), Tile.of(2999, 3383, 0)) <= 2) {
			if(player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("KILLED_GARDENER")) {
				player.getQuestManager().completeQuest(Quest.PIRATES_TREASURE);
				return;
			}
			for(NPC npc : World.getNPCsInChunkRange(player.getChunkId(), 1))
				if(npc.getId()== HOSTILE_GARDENER)
					return;
			NPC gardener = World.spawnNPC(HOSTILE_GARDENER, Tile.of(player.getTile()), true, true);
			gardener.setCombatTarget(player);
			gardener.forceTalk("First moles, now this!? Take this, vandal!");
		}
	}

	public static EnterChunkHandler handleBreakRum = new EnterChunkHandler(e -> {
		if (e.getEntity() instanceof Player player && player.getQuestManager().getStage(Quest.PIRATES_TREASURE) == SMUGGLE_RUM)
			if (!player.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB("HAS_SMUGGLED_RUM") && player.getInventory().containsItem(RUM))
				if (Utils.getDistance(player.getTile(), Tile.of(2928, 3143, 0)) > 70) {
					while (player.getInventory().containsItem(RUM, 1))
						player.getInventory().removeItems(new Item(RUM, 1));
					player.sendMessage("Your Karamja rum gets broken and spilled.");
				}
	});


	public static ItemClickHandler openCasket = new ItemClickHandler(new Object[] { CASKET }, new String[] { "Open" }, e -> {
		e.getPlayer().getInventory().removeItems(new Item(CASKET, 1));
		e.getPlayer().getInventory().addItem(new Item(1605, 1), true);//gold ring
		e.getPlayer().getInventory().addItem(new Item(1635, 1), true);//cut emerald
	});

	public static ItemClickHandler readMessage = new ItemClickHandler(new Object[] { PIRATE_MESSAGE }, new String[] { "Read" }, e -> {
		e.getPlayer().getInterfaceManager().sendInterface(220);//Message interface
		e.getPlayer().getPackets().setIFText(220, 8, "Visit the city of the White Knights. In the park,");
		e.getPlayer().getPackets().setIFText(220, 9, "Saradomin points to the X which marks the spot.");
		e.getPlayer().getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB("TREASURE_LOC_KNOWN", true);
	});

	public static ObjectClickHandler bananaTreePlantation = new ObjectClickHandler(new Object[] { 2073, 2074, 2075, 2076, 2077, 2078 }, e -> {
		if (e.getObjectId() == 2078) {
			e.getPlayer().sendMessage("There are no bananas left on the tree.");
			return;
		}
		e.getPlayer().anim(2280);
		e.getPlayer().getInventory().addItem(BANANA, 1);
		e.getObject().setIdTemporary(e.getObjectId()+1, Ticks.fromSeconds(30));
	});

	@Override
	public void complete(Player player) {
		player.getInventory().addItem(new Item(CASKET), true);
		sendQuestCompleteInterface(player, 7956);
	}
}
