package com.rs.game.player.quests.handlers.merlinscrystal;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.controllers.MerlinsCrystalRitualScene;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.QuestHandler;
import com.rs.game.player.quests.QuestOutline;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.GroundItem;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ItemOnItemEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@QuestHandler(Quest.MERLINS_CRYSTAL)
@PluginEventHandler
public class MerlinsCrystal extends QuestOutline {
	public static final int NOT_STARTED = 0;
	public static final int TALK_TO_KNIGHTS = 1;
	public static final int CONFRONT_KEEP_LA_FAYE = 2;
	public static final int THE_BLACK_CANDLE = 3;
	public static final int OBTAINING_EXCALIBUR = 4;
	public static final int PERFORM_RITUAL = 5;
	public static final int BREAK_MERLIN_CRYSTAL = 6;
	public static final int TALK_TO_ARTHUR = 7;
	public static final int QUEST_COMPLETE = 8;

	public static final String CANDLE_MAKER_KNOWS_ATTR = "KNOWS_ABOUT_BLACK_CANDLE";
	public static final String LADY_LAKE_TEST_ATTR = "LADY_TEST";
	public static final String PLAYER_KNOWS_BEGGAR_ATTR = "KNOWS_BEGGAR";
	protected static final int EXCALIBUR = 35;

	protected WorldTile crate = new WorldTile(2778, 9839, 0);

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("The wizard Merlin has been trapped in a magical crystal by");
			lines.add("the witch Morgan Le Faye. So far, King Arthur hasn't been ");
			lines.add("able to figure out how to free his mentor from his crystal ");
			lines.add("prison. Can you help?");
			lines.add("");
			break;
		case TALK_TO_KNIGHTS:
			lines.add("I must go investigate and talk with the knights of the round");
			lines.add("table to find clues to breaking Merlin's Crystal. Try talking ");
			lines.add("to all the knights, selecting all options. Come back Here to ");
			lines.add("see if you have completed the investigation.");
			lines.add("");
			break;
		case CONFRONT_KEEP_LA_FAYE:
			lines.add("The investigation is finished I must Confront Morgan Le Faye");
			lines.add("at their stronghold. Perhaps I can hide in a crate in Catherby");
			lines.add("by the candle makers shop.");
			lines.add("");
			break;
		case THE_BLACK_CANDLE:
			lines.add("I need a black candle and bat bones. I believe I can find black");
			lines.add("candles in the general store in Catherby by the ports.");
			lines.add("");
			break;
		case OBTAINING_EXCALIBUR:
			lines.add("I can get excalibur by performing a task for The Lady Of The");
			lines.add("Lake in Taverly, south of the summoning shops. She is on the");
			lines.add("peninsula.");
			lines.add("");
			break;
		case PERFORM_RITUAL:
			lines.add("I must see the chaos altar's inscription in South Varrock and");
			lines.add("memorize it. Then I must take the bat bones and black candle and");
			lines.add("bind a spirit using the encantation on the chaos altar.");
			lines.add("");
			break;
		case BREAK_MERLIN_CRYSTAL:
			lines.add("After the ritual you must smash the crystal with excalibur");
			lines.add("");
			break;
		case TALK_TO_ARTHUR:
			lines.add("Talk to Arthur to complete the quest.");
			lines.add("");
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



	public static NPCClickHandler handleCandleMakerDialogue = new NPCClickHandler(562) {
		final int NPC=562;
		final int UNLIT_BLACK_CANDLE = 38;
		final int BUCKET_WAX = 30;
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getOption().equalsIgnoreCase("Talk-to"))
				if(e.getPlayer().getQuestManager().getStage(Quest.MERLINS_CRYSTAL) >= MerlinsCrystal.THE_BLACK_CANDLE
				&& !e.getPlayer().getQuestManager().isComplete(Quest.MERLINS_CRYSTAL))
					e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
						{
							if(e.getPlayer().getInventory().containsItem(UNLIT_BLACK_CANDLE)) {
								addNPC(NPC, HeadE.CALM_TALK, "Good luck with your candle!");
								addPlayer(HeadE.HAPPY_TALKING, "Thanks!");
							} else if (e.getPlayer().getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).getB(MerlinsCrystal.CANDLE_MAKER_KNOWS_ATTR)) {
								if (e.getPlayer().getInventory().containsItem(BUCKET_WAX, 1)) {
									addNPC(NPC, HeadE.CALM_TALK, "Do you have the wax?");
									addPlayer(HeadE.HAPPY_TALKING, "Yes");
									addSimple("You exchange the wax with the candle maker for a black candle.", () -> {
										e.getPlayer().getInventory().removeItems(new Item(30, 1));
										e.getPlayer().getInventory().addItem(new Item(38, 1), true);
										e.getPlayer().getQuestManager().setStage(Quest.MERLINS_CRYSTAL, OBTAINING_EXCALIBUR);
									});

								} else {
									addNPC(NPC, HeadE.CALM_TALK, "Do you have the wax?");
									addPlayer(HeadE.HAPPY_TALKING, "Not yet");
								}
							} else {
								addNPC(NPC, HeadE.CALM_TALK, "Hi! Would you be interested in some of my fine candles?");
								addPlayer(HeadE.HAPPY_TALKING, "Have you got any black candles?");
								addNPC(NPC, HeadE.CALM_TALK, "BLACK candles??? Hmmm. In the candle making trade, we have a tradition that it's very bad luck" +
										" to make black candles. VERY bad luck.");
								addPlayer(HeadE.HAPPY_TALKING, "I will pay good money for one...");
								addNPC(NPC, HeadE.CALM_TALK, "I still dunno...Tell you what: I'll supply you with a black candle... IF you can bring me a bucket FULL of wax.", () -> {
									e.getPlayer().getQuestManager().getAttribs(Quest.MERLINS_CRYSTAL).setB(MerlinsCrystal.CANDLE_MAKER_KNOWS_ATTR, true);
								});
							}
							create();
						}
					});
		}

	};

	final static int LIT_BLACK_CANDLE = 32;
	final static int UNLIT_BLACK_CANDLE = 38;
	final static int TINDERBOX = 590;
	public static ItemOnItemHandler handleCandleLighting = new ItemOnItemHandler(new int[]{UNLIT_BLACK_CANDLE}, new int[]{TINDERBOX}) {
		@Override
		public void handle(ItemOnItemEvent e) {
			e.getPlayer().getInventory().replaceItem(LIT_BLACK_CANDLE, 1, e.getUsedWith(TINDERBOX).getSlot());
		}
	};

	public static ItemClickHandler handleLitCandle = new ItemClickHandler(LIT_BLACK_CANDLE) {
		@Override
		public void handle(ItemClickEvent e) {
			if(e.getOption().equalsIgnoreCase("Extinguish"))
				e.getPlayer().getInventory().replaceItem(UNLIT_BLACK_CANDLE, 1, e.getItem().getSlot());
			if(e.getOption().equalsIgnoreCase("drop")) {
				e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
				World.addGroundItem(e.getItem(), new WorldTile(e.getPlayer()), e.getPlayer());
				e.getPlayer().getPackets().sendSound(2739, 0, 1);
			}
		}
	};

	private static final int MERLIN_FREE_VAR = 14;
	private static final int MERLINS_CRYSTAL_OBJ = 62;
	public static ObjectClickHandler handleMerlinsCrystal = new ObjectClickHandler(new Object[] { MERLINS_CRYSTAL_OBJ }) {
		final int MERLIN = 249;
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if (e.getOption().equalsIgnoreCase("smash")) {
				p.sendMessage("You attempt to smash the crystal...");
				if (p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) == BREAK_MERLIN_CRYSTAL)
					if (p.getInventory().containsItem(EXCALIBUR, 1) || p.getEquipment().containsOneItem(EXCALIBUR)) {
						p.sendMessage("... and it shatters under the force of Excalibur!");
						p.getQuestManager().setStage(Quest.MERLINS_CRYSTAL, TALK_TO_ARTHUR);
						p.getVars().setVar(14, 7);
						NPC merlin = World.spawnNPC(MERLIN, new WorldTile(obj.getX(), obj.getY(), obj.getPlane()), -1, false, true);
						merlin.setCantInteract(true);
						merlin.setRandomWalk(false);
						merlin.finishAfterTicks(5);
						e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
							int NPC = MERLIN;

							{
								addNPC(NPC, HeadE.CALM_TALK, "Thank you! Thank you! Thank you!");
								addNPC(NPC, HeadE.CALM_TALK, "It's not fun being trapped in a giant crystal!");
								addSimple("You have set Merlin free. Now talk to King Arthur.");
								create();
							}
						});
					} else
						e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
							{
								addPlayer(HeadE.HAPPY_TALKING, "Hmm, looks like I will need excalibur to do this.");
								create();
							}
						});
			}
		}
	};

	public static PlayerStepHandler handleRitualSpot = new PlayerStepHandler(new WorldTile(2780, 3515, 0)) {
		final int BAT_BONES = 530;
		final WorldTile ritualTile = new WorldTile(2780, 3515, 0);
		@Override
		public void handle(PlayerStepEvent e) {
			Player p = e.getPlayer();
			if(p.getQuestManager().getStage(Quest.MERLINS_CRYSTAL) != PERFORM_RITUAL)
				return;
			WorldTasks.schedule(new WorldTask() {
				int tick;
				@Override
				public void run() {
					if(tick == 1)
						if(p.getInventory().containsItem(LIT_BLACK_CANDLE, 1))
							for (GroundItem item : World.getRegion(p.getRegionId()).getAllGroundItems())
								if (item.getId() == BAT_BONES && item.getTile().matches(ritualTile)) {
									p.getControllerManager().startController(new MerlinsCrystalRitualScene());
									stop();
								}
					if(tick == 3)
						if(p.matches(ritualTile))
							tick = 0;
					if(tick == 5)
						stop();
					tick++;
				}
			}, 0, 1);
		}
	};

	public static LoginHandler onLogin = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			if(e.getPlayer().getQuestManager().getStage(Quest.MERLINS_CRYSTAL) >= TALK_TO_ARTHUR)
				e.getPlayer().getVars().setVar(MERLIN_FREE_VAR, 7);
		}
	};

	@Override
	public void complete(Player player) {
		getQuest().sendQuestCompleteInterface(player, EXCALIBUR, "Excalibur");
	}
}