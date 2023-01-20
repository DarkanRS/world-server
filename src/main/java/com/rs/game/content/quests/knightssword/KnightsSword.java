package com.rs.game.content.quests.knightssword;

import java.util.ArrayList;

import com.rs.game.World;
import com.rs.game.engine.dialogue.Conversation;
import com.rs.game.engine.dialogue.HeadE;
import com.rs.game.engine.quest.Quest;
import com.rs.game.engine.quest.QuestHandler;
import com.rs.game.engine.quest.QuestOutline;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.Ticks;

@QuestHandler(Quest.KNIGHTS_SWORD)
@PluginEventHandler
public class KnightsSword extends QuestOutline {
	public static final int NOT_STARTED = 0;
	public static final int TALK_TO_RELDO = 1;
	public static final int FIND_DWARF = 2;
	public static final int GET_PICTURE = 3;
	public static final int GET_MATERIALS = 4;
	public static final int QUEST_COMPLETE = 6;

	//NPCs
	protected static final int RELDO = 647;
	protected static final int THURGO = 604;
	protected static final int SIR_VYVIN = 605;
	protected static final int SQUIRE = 606;
	//items
	protected static final int PORTRAIT = 666;
	protected static final int BLURITE_SWORD = 667;
	protected static final int BLURITE_ORE = 668;
	protected static final int IRON_BAR = 2351;

	protected static final int REDBERRY_PIE = 2325;

	//objects
	protected static final int CUPBOARD = 2271;

	protected static final String PICTURE_LOCATION_KNOWN_ATTR = "picture_location_known";
	protected static final String GAVE_THRUGO_PIE_ATTR = "gave_thurgo_pie";
	protected static final String MADE_SWORD_ATTR = "made_sword";

	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("Sir Vyvin's squire is in trouble. He has accidentally");
			lines.add("lost Sir Vyvin's ceremonial sword. Help him find a");
			lines.add("replacement without Sir Vyvin findingout.");
			lines.add("");
			lines.add("You can start this quest by talking to the Squire on");
			lines.add("the 1st floor of Falador castle");
			lines.add("");
			break;
		case TALK_TO_RELDO:
			lines.add("Reldo may know something about dwarves. You can find");
			lines.add("him at Varrock Castle.");
			lines.add("");
			break;
		case FIND_DWARF:
			lines.add("The dwarf is south of Port Sarim, living in a hut.");
			lines.add("Maybe he can help me restore the Knight's Sword? I");
			lines.add("also hear they like redberry pies...");
            lines.add("");
			break;
		case GET_PICTURE:
			lines.add("I will need a picture of the sword. Maybe the");
			lines.add("Squire knows where to find one...");
			lines.add("");
			if(player.getQuestManager().getAttribs(Quest.KNIGHTS_SWORD).getB(PICTURE_LOCATION_KNOWN_ATTR)) {
				lines.add("The squire says Sir Vyvin keeps an image of his sword ");
				lines.add("in a cupboard in his room on the 3rd floor on the east");
				lines.add("side of the castle. He can't know we are stealing it.");
				lines.add("");
			}
			break;
		case GET_MATERIALS:
			lines.add("I will need 1 blurite ore and 2 iron ingots to make");
			lines.add("the sword. I can get blurite ore from the mine next");
			lines.add("to the dwarf's hut.");
			lines.add("");
			if(player.getInventory().containsItem(BLURITE_SWORD)) {
				lines.add("Now that you have the sword you can give it to");
				lines.add("the squire...");
				lines.add("");
			}
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

	@Override
	public void complete(Player player) {
		player.getSkills().addXpQuest(Constants.SMITHING, 12725);
		getQuest().sendQuestCompleteInterface(player, BLURITE_SWORD, "12,725 Smithing XP");
	}

	public static ObjectClickHandler handleVyvinCupboard = new ObjectClickHandler(new Object[] { 2271, 2272 }, e -> {
		Player p = e.getPlayer();
		GameObject obj = e.getObject();
		if (e.getOption().equalsIgnoreCase("open")) {
			p.setNextAnimation(new Animation(536));
			p.lock(2);
			GameObject openedChest = new GameObject(obj.getId() + 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
			p.faceObject(openedChest);
			World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
		}
		if (e.getOption().equalsIgnoreCase("shut")) {
			p.setNextAnimation(new Animation(536));
			p.lock(2);
			GameObject openedChest = new GameObject(obj.getId() - 1, obj.getType(), obj.getRotation(), obj.getX(), obj.getY(), obj.getPlane());
			p.faceObject(openedChest);
			World.spawnObjectTemporary(openedChest, Ticks.fromMinutes(1));
		}
		if(e.getOption().equalsIgnoreCase("search")) {
			if(p.getQuestManager().getStage(Quest.KNIGHTS_SWORD) != GET_PICTURE) {
				p.sendMessage("There is nothing interesting here...");
				return;
			}
			for(NPC npc : World.getNPCsInRegion(e.getPlayer().getRegionId()))
				if(npc.getName().equalsIgnoreCase("Sir Vyvin"))
					if(npc.lineOfSightTo(p, false)) {
						p.startConversation(new Conversation(p) {
							{
								addPlayer(HeadE.SKEPTICAL_THINKING, "Sir Vyvin can see me...");
								create();
							}
						});
						return;
					}
			if(p.getInventory().containsItem(PORTRAIT))
				p.sendMessage("The cupboard is empty...");
			else
				p.getInventory().addItem(new Item(PORTRAIT, 1));
		}
	});
}