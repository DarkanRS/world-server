package com.rs.game.content.quests.handlers.lostcity;

import java.util.ArrayList;

import com.rs.game.content.quests.Quest;
import com.rs.game.content.quests.QuestHandler;
import com.rs.game.content.quests.QuestOutline;
import com.rs.game.content.transportation.FairyRings;
import com.rs.game.content.world.doors.Doors;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.object.GameObject;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ObjectClickHandler;

@QuestHandler(Quest.LOST_CITY)
@PluginEventHandler
public class LostCity extends QuestOutline {
	public final static int NOT_STARTED = 0;
	public final static int TALK_TO_LEPRAUCAN = 1;
	public final static int CHOP_DRAMEN_TREE = 2;
	public final static int FIND_ZANARIS = 3;
	public final static int QUEST_COMPLETE = 4;


	//Attributes


	//items


	//NPCs
	protected final static int ARCHER = 649;
	protected final static int WARRIOR = 650;
	protected final static int MONK = 651;
	protected final static int WIZARD = 652;
	protected final static int LEPRACAUN = 654;
	protected final static int TREE_SPIRIT = 655;

	//Objects
	protected final static int LEPRACAUN_TREE = 2409;
	//    protected final static int
	protected final static int DRAMEN_STAFF = 772;



	@Override
	public int getCompletedStage() {
		return QUEST_COMPLETE;
	}

	@Override
	public ArrayList<String> getJournalLines(Player player, int stage) {
		ArrayList<String> lines = new ArrayList<>();
		switch(stage) {
		case NOT_STARTED:
			lines.add("Legends tell of a magical lost city hidden in the swamps.");
			lines.add("Many adventurers have tried to find this city, but it is");
			lines.add("proving difficult. Can you unlock the secrets of the city");
			lines.add("of Zanaris?");
			lines.add("");
			lines.add("I can start this quest by talking to a warrior in Lumbridge Swamp.");
			lines.add("You will need to puzzle through the dialogues to start the quest.");
			lines.add("Come back here to see when to look for the dramen branches.");
			lines.add("");
			lines.add("~~Requirements~~");
			lines.add("31 Crafting");
			lines.add("36 Woodcutting");
			lines.add("");
			break;
		case TALK_TO_LEPRAUCAN:
			lines.add("The warrior in Lumbridge Swamp said I am to find a leprecaun in the");
			lines.add("area by trying to chop down trees?");
			lines.add("");
			break;
		case CHOP_DRAMEN_TREE:
			lines.add("I am to go to Entrana and chop a dramen tree");
			lines.add("");
			break;
		case FIND_ZANARIS:
			lines.add("Now that I have a dramen branch I should turn it into a staff and");
			lines.add("head to Lumbridge Swamp where I can find a shed. If I enter it with");
			lines.add("the staff in hand, I should be able to get to Zanaris...");
			lines.add("");
			break;
		case QUEST_COMPLETE:
			lines.add("I have found Zanaris!");
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

	public static ObjectClickHandler handleShedDoor = new ObjectClickHandler(new Object[] { 2406 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			Doors.handleDoor(p, obj);
			if(p.getX() <= obj.getX())
				if(p.getEquipment().getWeaponId() == DRAMEN_STAFF
                        && p.getQuestManager().getStage(Quest.LOST_CITY) >= FIND_ZANARIS)
					WorldTasks.schedule(new WorldTask() {
						int tick;
						@Override
						public void run() {
							if(tick == 1) {
								p.sendMessage("The world starts to shimmer...");
								FairyRings.sendTeleport(p, WorldTile.of(2452, 4473, 0));
								p.lock(4);
							}
							if(tick == 4)
								if(!p.isQuestComplete(Quest.LOST_CITY)) {
									p.lock(3);//so players dont cancel it out by accident and not see it...
									p.getQuestManager().completeQuest(Quest.LOST_CITY);
								}
							if(tick == 5)
								stop();
							tick++;
						}
					}, 0, 1);
		}
	};


	@Override
	public void complete(Player player) {
		getQuest().sendQuestCompleteInterface(player, 772, "Access to Zanaris");
	}

}
