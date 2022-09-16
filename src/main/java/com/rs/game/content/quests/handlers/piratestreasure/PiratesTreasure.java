package com.rs.game.content.quests.handlers.piratestreasure;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.EnterChunkEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.EnterChunkHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@QuestHandler(Quest.PIRATES_TREASURE)
@PluginEventHandler
public class PiratesTreasure extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int SMUGGLE_RUM = 1;
	public final static int GET_TREASURE = 2;
	public final static int QUEST_COMPLETE = 3;

	//Attributes
	public final static String KNOWS_TREASURE_LOC_ATTR = "TREASURE_LOC_KNOWN";
	public final static String KILLED_GARDENER_ATTR = "KILLED_GARDENER";
	public final static String HAS_SMUGGLED_RUM_ATTR = "HAS_SMUGGLED_RUM";
	public static final String LUTHAS_EMPLOYMENT_ATTR = "LUTHAS_EMPLOYMENT";
	public static final String RUM_IN_SARIM_CRATE_ATTR = "RUM_IN_SARIM_CRATE";
	public static final String WYDIN_EMPLOYMENT_ATTR = "WYDIN_EMPLOYMENT";
	public final static String BANANA_COUNT_ATTR = "BANANA_COUNT";
	public final static String RUM_IN_KARAMJA_CRATE_ATTR = "RUM_IN_KARAMJA_CRATE";

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
	protected final static int BANANA_TREE_PLANT = 2073;
	protected final static int BLUE_MOON_INN_CHEST = 2079;




	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
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

	public static ObjectClickHandler handleTreasureChest = new ObjectClickHandler(new Object[] {BLUE_MOON_INN_CHEST }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(!p.getInventory().containsItem(CHEST_KEY))
				return;
			if(e.getOption().equalsIgnoreCase("open")) {
				p.setNextAnimation(new Animation(536));
				p.lock(2);
				GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
				p.faceObject(openedChest);
				World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
				p.getInventory().addItem(new Item(PIRATE_MESSAGE, 1));
			}
		}
	};

	public static void findTreasure(Player p) {
		if((p.getQuestManager().getStage(Quest.PIRATES_TREASURE) != GET_TREASURE) || !p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB(PiratesTreasure.KNOWS_TREASURE_LOC_ATTR))
			return;
		if(Utils.getDistance(p.getTile(), new WorldTile(2999, 3383, 0)) <= 2) {
			if(p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB(KILLED_GARDENER_ATTR)) {
				p.getQuestManager().completeQuest(Quest.PIRATES_TREASURE);
				return;
			}
			for(NPC npc : World.getNPCsInRegion(p.getRegionId()))
				if(npc.getId()== HOSTILE_GARDENER)
					return;
			NPC gardener = World.spawnNPC(HOSTILE_GARDENER, new WorldTile(p.getTile()), -1, false, true);
			gardener.setTarget(p);
			gardener.forceTalk("First moles, now this!? Take this, vandal!");
		}
	}

	public static EnterChunkHandler handleBreakRum = new EnterChunkHandler() {
		@Override
		public void handle(EnterChunkEvent e) {
			if (e.getEntity() instanceof Player p && p.getQuestManager().getStage(Quest.PIRATES_TREASURE) == SMUGGLE_RUM)
				if (!p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB(HAS_SMUGGLED_RUM_ATTR) && p.getInventory().containsItem(RUM))
					if (Utils.getDistance(p.getTile(), new WorldTile(2928, 3143, 0)) > 70) {
						while (p.getInventory().containsItem(RUM, 1))
							p.getInventory().removeItems(new Item(RUM, 1));
						p.sendMessage("Your Karamja rum gets broken and spilled.");
					}
		}
	};


	public static ItemClickHandler openCasket = new ItemClickHandler(new Object[] { CASKET }, new String[] { "Open" }) {
		@Override
		public void handle(ItemClickEvent e) {
			e.getPlayer().getInventory().removeItems(new Item(CASKET, 1));
			e.getPlayer().getInventory().addItem(new Item(1605, 1), true);//gold ring
			e.getPlayer().getInventory().addItem(new Item(1635, 1), true);//cut emerald
		}
	};

	public static ItemClickHandler readMessage = new ItemClickHandler(new Object[] { PIRATE_MESSAGE }, new String[] { "Read" }) {
		private final int MESSAGE_INTERFACE = 220;
		@Override
		public void handle(ItemClickEvent e) {
			e.getPlayer().getInterfaceManager().sendInterface(MESSAGE_INTERFACE);//Message interface
			e.getPlayer().getPackets().setIFText(MESSAGE_INTERFACE, 8, "Visit the city of the White Knights. In the park,");
			e.getPlayer().getPackets().setIFText(MESSAGE_INTERFACE, 9, "Saradomin points to the X which marks the spot.");
			e.getPlayer().getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB(KNOWS_TREASURE_LOC_ATTR, true);
		}
	};

	public static ObjectClickHandler bananaTreePlantation = new ObjectClickHandler(new Object[] { BANANA_TREE_PLANT }) {
		private static final int FRUIT_PICK_ANIM = 2280;
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextAnimation(new Animation(FRUIT_PICK_ANIM));
			e.getPlayer().getInventory().addItem(BANANA, 1);
		}
	};

	@Override
	public void complete(Player player) {
		player.getInventory().addItem(new Item(CASKET), true);
		getQuest().sendQuestCompleteInterface(player, 7956, "One-Eyed Hector's Treasure");
	}

}
