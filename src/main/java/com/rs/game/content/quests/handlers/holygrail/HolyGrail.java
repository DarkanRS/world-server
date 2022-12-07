package com.rs.game.content.quests.handlers.holygrail;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestManager;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PickupItemEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PickupItemHandler;
import com.rs.plugin.handlers.PlayerStepHandler;

@QuestHandler(Quest.HOLY_GRAIL)
@PluginEventHandler
public class HolyGrail extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int TALK_TO_MERLIN = 1;
	public final static int GO_TO_ENTRANA = 2;
	public final static int GO_TO_MCGRUBOR = 3;
	public final static int SPEAK_TO_FISHER_KING = 4;
	public final static int SPEAK_TO_PERCIVAL = 5;
	public final static int GIVE_AURTHUR_HOLY_GRAIL = 6;
	public final static int QUEST_COMPLETE = 7;

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch (stage) {
			case NOT_STARTED -> {
				lines.add("King Arthur is sending out his knights on a quest for the");
				lines.add("famous Holy Grail. If you are a Knight of the Round Table");
				lines.add("go to King Arthur for further orders.");
				lines.add("");
				lines.add("~~~Quest Requirements~~~");
				lines.add((player.isQuestComplete(Quest.MERLINS_CRYSTAL) ? "<str>" : "") + "Merlin's Crystal");
				lines.add("");
				lines.add("~~~Skill Requirements~~~");
				lines.add((player.getSkills().getLevel(Constants.ATTACK) >= 20 ? "<str>" : "") + "20 Attack(Wield Excalibur)");
				lines.add("");
				if (meetsRequirements(player)) {
					lines.add("You meet the requirements for this quest!");
					lines.add("");
				} else {
					lines.add("You do not meet the requirements for this quest!");
					lines.add("");
				}
			}
			case TALK_TO_MERLIN -> {
				lines.add("King Arthur recommended I get more information about the");
				lines.add("Holy Grail from King Arthur.");
				lines.add("");
			}
			case GO_TO_ENTRANA -> {
				lines.add("Merlin recommends looking for Entrana, perhaps the High");
				lines.add("Priest of the church can help me?");
				lines.add("");
			}
			case GO_TO_MCGRUBOR -> {
				lines.add("I need an artifact of The Fisher Realm. Perhaps Brother");
				lines.add("Galahad can help me get one.");
				lines.add("");
			}
			case SPEAK_TO_FISHER_KING -> {
				lines.add("I must get into the castle and speak to the king of The");
				lines.add("Fisher Realm. However I need some type of whistle. I was");
				lines.add("told I can find it in a haunted mansion. However I need");
				lines.add("to have an artifact of the fisher realm for it to");
				lines.add("appear.");
				lines.add("");
				lines.add("Once I have the whistle I need to find a way to get there.");
				lines.add("6 heads point to the location which I must find.");
				lines.add("");
			}
			case SPEAK_TO_PERCIVAL -> {
				lines.add("I should find the Fisher King's son, Sir Percival. Sir");
				lines.add("Percival is also a knight of the round table. Perhaps");
				lines.add("King Arthur will know how to get to him?");
				lines.add("");
			}
			case GIVE_AURTHUR_HOLY_GRAIL -> {
				lines.add("Legend has it once The Fisher Realm is restored a hero");
				lines.add("can claim the Holy Grail from the castle and after give");
				lines.add("it to King Arthur.");
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
				questManager.isComplete(Quest.MERLINS_CRYSTAL),
				skills.getLevel(Constants.ATTACK) >= 20
		};
		for (boolean hasRequirement : requirements)
			if (!hasRequirement)
				return false;
		return true;
	}

	public static NPCClickHandler handleFisherNPCs = new NPCClickHandler(new Object[]{210, 214, 215}, new String[]{"Talk-to"}) {
		@Override
		public void handle(NPCClickEvent e) {
			if(e.getNPCId()==214)//peasent
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(214, HeadE.SAD_CRYING, "Woe is me! Our crops are all failing... how shall I feed myself this winter?")
				);
			if(e.getNPCId()==215)//paesent
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(215, HeadE.HAPPY_TALKING, "Oh happy day! Suddenly our crops are growing again! It'll be a bumper harvest this year!")
				);
			if(e.getNPCId()==210)//grail maiden
				e.getPlayer().startConversation(new Dialogue()
						.addNPC(210, HeadE.CALM_TALK, "Welcome to the Grail Castle.")
				);
		}
	};

	public static PickupItemHandler handleHolyGrail = new PickupItemHandler(new Object[] {19},
			WorldTile.of(2649, 4684, 2)) {
		@Override
		public void handle(PickupItemEvent e) {
			if(e.getPlayer().isQuestComplete(Quest.HOLY_GRAIL) || e.getPlayer().getInventory().containsItem(19))
				e.cancelPickup();
		}
	};

	public static PlayerStepHandler handleMagicWhistleSpawn = new PlayerStepHandler(WorldTile.of(3106, 3361, 2)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getPlayer().getInventory().containsItem(15) && !e.getPlayer().getTempAttribs().getB("Spawned_Whistle")) {
				e.getPlayer().getTempAttribs().setB("Spawned_Whistle", true);
				World.addGroundItem(new Item(16, 1), WorldTile.of(3107, 3359, 2), e.getPlayer());
				World.addGroundItem(new Item(16, 1), WorldTile.of(3107, 3359, 2), e.getPlayer());
			}
		}
	};

	public static ItemClickHandler handleMagicWhisle = new ItemClickHandler(16) {
		@Override
		public void handle(ItemClickEvent e) {
			if(e.getOption().equalsIgnoreCase("Blow")) {
				if (e.getPlayer().getRegionId() == 11081 || e.getPlayer().getRegionId() == 10569) {
					Magic.sendNormalTeleportSpell(e.getPlayer(), WorldTile.of(2757, 3475, 0));
				}
				if (e.getPlayer().getTile().withinDistance(WorldTile.of(2742, 3236, 0), 2)) {
					if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) >= GIVE_AURTHUR_HOLY_GRAIL) {
						Magic.sendNormalTeleportSpell(e.getPlayer(), WorldTile.of(2678, 4713, 0));
						return;
					}
					Magic.sendNormalTeleportSpell(e.getPlayer(), WorldTile.of(2803, 4713, 0));
				}
			}
			if(e.getOption().equalsIgnoreCase("drop")) {
				e.getPlayer().getInventory().deleteItem(e.getSlotId(), e.getItem());
				World.addGroundItem(e.getItem(), WorldTile.of(e.getPlayer().getTile()), e.getPlayer());
				e.getPlayer().soundEffect(2739);
			}
		}
	};

	public static ItemClickHandler handleGrailBell = new ItemClickHandler(new Object[] { 17 }, new String[] { "Ring" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getPlayer().getTile().withinDistance(WorldTile.of(2762, 4694, 0), 1)) {
				e.getPlayer().startConversation(new Dialogue()
						.addSimple("Ting-a-ling!")
						.addNPC(210, HeadE.CALM_TALK, "Come in, it is cold out!")
						.addNext(() -> e.getPlayer().setNextWorldTile(WorldTile.of(2762, 4692, 0))));
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addSimple("Ting-a-ling!"));
		}
	};

	public static ItemClickHandler handleMagicGoldFeather = new ItemClickHandler(new Object[] { 18 }, new String[] { "Blow-on" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) != SPEAK_TO_PERCIVAL) {
				e.getPlayer().sendMessage("The feather seems like an ordinary feather now...");
				return;
			}
			WorldTile playerTile = e.getPlayer().getTile();
			WorldTile percievalTile = WorldTile.of(2961, 3505, 0);
			int xDir = percievalTile.getX() - playerTile.getX();
			int yDir = percievalTile.getY() - playerTile.getY();
			if (xDir == 0 && yDir == 0)
				e.getPlayer().sendMessage("The feather points down somewhere near here");
			if (xDir == 0 && yDir > 0)
				e.getPlayer().sendMessage("The feather points to the north");
			if (xDir > 0 && yDir > 0)
				e.getPlayer().sendMessage("The feather points to northeast");
			if (xDir > 0 && yDir == 0)
				e.getPlayer().sendMessage("The feather points to the east");
			if (xDir > 0 && yDir < 0)
				e.getPlayer().sendMessage("The feather points to the southeast");
			if (xDir == 0 && yDir < 0)
				e.getPlayer().sendMessage("The feather points to the south");
			if (xDir < 0 && yDir < 0)
				e.getPlayer().sendMessage("The feather points to the southwest");
			if (xDir < 0 && yDir == 0)
				e.getPlayer().sendMessage("The feather points to the west");
			if (xDir < 0 && yDir > 0)
				e.getPlayer().sendMessage("The feather points to the northwest");
		}
	};

	public static ObjectClickHandler handlePercivalSack = new ObjectClickHandler(new Object[]{ 23 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) != SPEAK_TO_PERCIVAL) {
				e.getPlayer().startConversation(new Dialogue().addPlayer(HeadE.CALM_TALK, "It is just an ordinary sack..."));
				return;
			}
			if(e.getOption().equalsIgnoreCase("Prod")) {
				e.getPlayer().startConversation(new Dialogue().addSimple("You hear a muffled groan. The sack wiggles slightly."));
				return;
			}
			e.getPlayer().startConversation(new Dialogue().addSimple("You hear muffled noises from the sack. You open the sack."));
			OwnedNPC percival = new OwnedNPC(e.getPlayer(), 211, WorldTile.of(2961, 3504, 0), true);
			percival.faceEntity(e.getPlayer());
			percival.setRandomWalk(false);
		}
	};

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Skills.PRAYER, 11_000);
		player.getSkills().addXpQuest(Skills.DEFENSE, 15_300);
		getQuest().sendQuestCompleteInterface(player, 19, "11,000 Prayer XP", "15,300 Defence XP");
	}

}
