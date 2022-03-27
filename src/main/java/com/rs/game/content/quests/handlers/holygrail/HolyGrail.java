package com.rs.game.content.quests.handlers.holygrail;

import com.rs.game.World;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestManager;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.npc.others.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.Skills;
import com.rs.lib.Constants;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.events.PlayerStepEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.plugin.handlers.PlayerStepHandler;
import com.rs.utils.Ticks;

import java.util.ArrayList;

@QuestHandler(Quest.HOLY_GRAIL)
@PluginEventHandler
public class HolyGrail extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int GO_TO_ENTRANA = 1;
	public final static int GO_TO_MCGRUBOR = 2;
	public final static int SPEAK_TO_FISHER_KING = 3;
	public final static int SPEAK_TO_PERCIVAL = 4;
	public final static int GIVE_AURTHUR_HOLY_GRAIL = 5;
	public final static int QUEST_COMPLETE = 6;

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
				lines.add((player.getQuestManager().isComplete(Quest.MERLINS_CRYSTAL) ? "<str>" : "") + "Merlin's Crystal");
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
			case GO_TO_ENTRANA -> {
				lines.add("");
				lines.add("");
			}
			case GO_TO_MCGRUBOR -> {
				lines.add("");
				lines.add("");
			}
			case SPEAK_TO_FISHER_KING -> {
				lines.add("");
				lines.add("");
			}
			case SPEAK_TO_PERCIVAL -> {
				lines.add("");
				lines.add("");
			}
			case GIVE_AURTHUR_HOLY_GRAIL -> {
				lines.add("");
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

	public static PlayerStepHandler handleMagicWhistleSpawn = new PlayerStepHandler(new WorldTile(3106, 3361, 2)) {
		@Override
		public void handle(PlayerStepEvent e) {
			if(e.getPlayer().getInventory().containsItem(15) && e.getPlayer().getTempAttribs().getB("Spawned_Whistle")) {
				e.getPlayer().getTempAttribs().setB("Spawned_Whistle", true);
				World.addGroundItem(new Item(16, 1), new WorldTile(3107, 3359, 2), e.getPlayer());
				World.addGroundItem(new Item(16, 1), new WorldTile(3107, 3359, 2), e.getPlayer());
			}
		}
	};

	public static ItemClickHandler handleMagicWhisle = new ItemClickHandler(16, new String[]{"Blow"}) {
		@Override
		public void handle(ItemClickEvent e) {
			if(e.getPlayer().getRegionId() == 11081)
				Magic.sendNormalTeleportSpell(e.getPlayer(), new WorldTile(2757, 3475, 0));
			if(e.getPlayer().getTile().withinDistance(new WorldTile(2742, 3236, 0), 2))
				e.getPlayer().setNextWorldTile(new WorldTile(2803, 4713, 0));
		}
	};

	public static ItemClickHandler handleGrailBell = new ItemClickHandler(17, new String[]{"Ring"}) {
		@Override
		public void handle(ItemClickEvent e) {
			if(e.getPlayer().getTile().withinDistance(new WorldTile(2762, 4694, 0), 1))
				e.getPlayer().startConversation(new Dialogue()
						.addSimple("Ting-a-ling!")
						.addNPC(210, HeadE.CALM_TALK, "Come in, it is cold out!")
						.addNext(()->{e.getPlayer().setNextWorldTile(new WorldTile(2762, 4692, 0));})
				);
		}
	};

	public static ItemClickHandler handleMagicGoldFeather = new ItemClickHandler(18, new String[]{"Blow-on"}) {
		@Override
		public void handle(ItemClickEvent e) {
			if(e.getPlayer().getQuestManager().getStage(Quest.HOLY_GRAIL) != SPEAK_TO_PERCIVAL) {
				e.getPlayer().sendMessage("The feather seems like an ordinary feather now...");
				return;
			}
			WorldTile playerTile = e.getPlayer().getTile();
			WorldTile percievalTile = new WorldTile(2961, 3505, 0);
			int xDir = playerTile.getX() - percievalTile.getX();
			int yDir = playerTile.getY() - percievalTile.getY();
			if(xDir == 0 && yDir == 0)
				e.getPlayer().sendMessage("The feather points down somewhere near here");
			if(xDir == 0 && yDir > 0)
				e.getPlayer().sendMessage("The feather points to the north");
			if(xDir > 0 && yDir > 0)
				e.getPlayer().sendMessage("The feather points to northeast");
			if(xDir > 0 && yDir == 0)
				e.getPlayer().sendMessage("The feather points to the east");
			if(xDir > 0 && yDir < 0)
				e.getPlayer().sendMessage("The feather points to the southeast");
			if(xDir == 0 && yDir < 0)
				e.getPlayer().sendMessage("The feather points to the south");
			if(xDir < 0 && yDir < 0)
				e.getPlayer().sendMessage("The feather points to the southwest");
			if(xDir < 0 && yDir == 0)
				e.getPlayer().sendMessage("The feather points to the west");
			if(xDir < 0 && yDir > 0)
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
			OwnedNPC percival = new OwnedNPC(e.getPlayer(), 211, new WorldTile(2961, 3504, 0), true);
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
