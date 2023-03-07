package com.rs.game.content.quests.treegnomevillage.dialogues;

import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.FIRE_BALLISTA;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.GET_WOOD;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.KILL_WARLORD;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.NOT_STARTED;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.ORB1;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.QUEST_COMPLETE;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.TALK_TO_MONTAI_ABOUT_TRACKERS;
import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.TALK_TO_MONTAI_ABOUT_WOOD;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class ElkoyTreeGnomeVillageD extends Conversation {
	private static final int NPC = 473;
	public ElkoyTreeGnomeVillageD(Player player) {
		super(player);
		Dialogue goThroughMaze = new Dialogue().addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Yes please", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "Yes please")
						.addSimple("Elkoy guides you through the maze.", () -> {
							player.lock(3);
							WorldTasks.delay(3, () -> {
								player.startConversation(new Dialogue().addNPC(473, HeadE.HAPPY_TALKING, "Here we are, " + (player.getY() > 3177 ? "feel free to have a look around." : "off you go.")));
							});
							player.fadeScreen(() -> {
								player.sendMessage("Elkoy leads you through the maze...");
								player.setNextTile(player.getY() > 3177 ? Tile.of(2515, 3160, 0) : Tile.of(2502, 3193, 0));
							});
						})
				);
				option("No", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "No thanks Elkoy")
						.addNPC(NPC, HeadE.CALM_TALK, "Ok then, take care.")
				);
			}
		});
		switch(player.getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE)) {
			case NOT_STARTED -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello there.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello, welcome to our maze. I'm Elkoy the tree gnome.");
				addPlayer(HeadE.HAPPY_TALKING, "I haven't heard of your sort");
				addNPC(NPC, HeadE.CALM_TALK, "There's not many of us left. Once you could find tree gnomes anywhere in the world, now we hide in small groups" +
						" to avoid capture.");
				addPlayer(HeadE.HAPPY_TALKING, "Capture by whom?");
				addNPC(NPC, HeadE.CALM_TALK, "Tree gnomes have been hunted for so called 'fun' since as long as I can remember.");
				addNPC(NPC, HeadE.CALM_TALK, "Our main threat nowadays are General Khazard's troops. They know no mercy, but are also very dense. They'll " +
						"never find their way through our maze.");
			}
			case TALK_TO_MONTAI_ABOUT_WOOD, GET_WOOD, TALK_TO_MONTAI_ABOUT_TRACKERS, FIRE_BALLISTA, ORB1, KILL_WARLORD -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello");
				addNPC(NPC, HeadE.CALM_TALK, "We are going to need help with those orbs, otherwise our village is doomed.");
				addNPC(NPC, HeadE.CALM_TALK, "Do you want me to show you through the maze?");
				addNext(goThroughMaze);
			}
			case QUEST_COMPLETE ->  {
				addPlayer(HeadE.HAPPY_TALKING, "Hello Elkoy.");
				addNPC(NPC, HeadE.CALM_TALK, "Hi there, I hope life is treating you well. Would you like me to show you the way " + (player.getY() > 3177 ? "out of" : "into") + " the village?");
				addNext(goThroughMaze);
			}
		}
	}
}
