package com.rs.game.content.quests.treegnomevillage.dialogues;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.treegnomevillage.TreeGnomeVillage.*;

@PluginEventHandler
public class CommanderMontaiTreeGnomeVillageD extends Conversation {
	private static final int NPC = 470;
	public CommanderMontaiTreeGnomeVillageD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE)) {
			case NOT_STARTED -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "I can't talk now. Can't you see we're trying to win a battle here? If we can't hold back Khazard's men we're all doomed.");
			}
			case TALK_TO_MONTAI_ABOUT_WOOD -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello traveller, are you here to help or just to watch?");
				addPlayer(HeadE.HAPPY_TALKING, "I've been sent by King Bolren to retrieve the orb of protection.");
				addNPC(NPC, HeadE.CALM_TALK, "Excellent we need all the help we can get.");
				addNPC(NPC, HeadE.CALM_TALK, "I'm commander Montai. The orb is in the Khazard stronghold to the north, but until we weaken their defences we can't get close.");
				addPlayer(HeadE.HAPPY_TALKING, "What can I do?");
				addNPC(NPC, HeadE.CALM_TALK, "Firstly we need to strengthen our own defences. We desperately need wood to make more battlements, once the " +
						"battlements are gone it's all over. Six loads of normal logs should do it.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Sorry, I no longer want to be involved.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Sorry, I no longer want to be involved.")
						);
						option("Ok, I'll gather some wood.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Ok, I'll gather some wood.")
								.addNPC(NPC, HeadE.CALM_TALK, "Please be as quick as you can, I don't know how much longer we can hold out.", ()->{
									player.getQuestManager().setStage(Quest.TREE_GNOME_VILLAGE, GET_WOOD);
								})
						);
					}
				});
			}
			case GET_WOOD -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello again, we're still desperate for wood soldier.");
				if(this.player.getInventory().getAmountOf(1511) >= 6) {
					addPlayer(HeadE.HAPPY_TALKING, "I have some here");
					addNPC(NPC, HeadE.CALM_TALK, "That's excellent, now we can make more defensive battlements.  Give me a moment to organise the troops and then come speak to me. I'll inform you of our next phase of attack.", ()->{
						this.player.getInventory().deleteItem(1511, 6);
						this.player.getQuestManager().setStage(Quest.TREE_GNOME_VILLAGE, TALK_TO_MONTAI_ABOUT_TRACKERS);
					});
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "I'll see what I can do.");
				addNPC(NPC, HeadE.CALM_TALK, "Thank you.");
			}
			case TALK_TO_MONTAI_ABOUT_TRACKERS -> {
				addPlayer(HeadE.HAPPY_TALKING, "How are you doing Montai?");
				addNPC(NPC, HeadE.CALM_TALK, "We're hanging in there soldier. For the next phase of our attack we need to breach their stronghold.");
				addNPC(NPC, HeadE.CALM_TALK, "The ballista can break through the stronghold wall, and then we can advance and seize back the orb.");
				addPlayer(HeadE.HAPPY_TALKING, "So what's the problem?");
				addNPC(NPC, HeadE.CALM_TALK, "From this distance we can't get an accurate enough shot.  We need the correct coordinates of the stronghold for a direct hit.  I've sent out three tracker gnomes to gather them.");
				addPlayer(HeadE.HAPPY_TALKING, "Have they returned?");
				addNPC(NPC, HeadE.CALM_TALK, "I'm afraid not, and we're running out of time. I need you to go into the heart of the battlefield, find the trackers, and bring back the coordinates.");
				addNPC(NPC, HeadE.CALM_TALK, "Do you think you can do it?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("No, I've had enough of your battle.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "No, I've had enough of your battle.")
								.addNPC(NPC, HeadE.CALM_TALK, "I understand, this isn't your fight.")
						);
						option("I'll try my best.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'll try my best.", ()->{
									player.getQuestManager().setStage(Quest.TREE_GNOME_VILLAGE, FIRE_BALLISTA);
								})
								.addNPC(NPC, HeadE.CALM_TALK, "Thank you, you're braver than most.")
								.addNPC(NPC, HeadE.CALM_TALK, "I don't know how long I will be able to hold out. Once you have the coordinates come back and fire " +
										"the ballista right into those monsters.")
								.addNPC(NPC, HeadE.CALM_TALK, "If you can retrieve the orb and bring safety back to my people, none of the blood spilled on this " +
										"field will be in vain.")
						);
					}
				});
			}
			case FIRE_BALLISTA -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello warrior. We need the coordinates for a direct hit from the ballista. Once you have a direct hit you will " +
						"be able to enter the stronghold and retrieve the orb.");
			}
			case ORB1 -> {
				if(this.player.getInventory().containsItem(587)) {
					addNPC(NPC, HeadE.AMAZED, "Oh WOW!");
					addPlayer(HeadE.CALM, "...");
					addNPC(NPC, HeadE.SECRETIVE, "Return the Orb Of Protection to King Bolren, hurry!");
					addPlayer(HeadE.SECRETIVE, "Okay okay");
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello warrior. We need the coordinates for a direct hit from the ballista. Once you have a direct hit you will " +
						"be able to enter the stronghold and retrieve the orb.");
			}
			case KILL_WARLORD -> {
				addPlayer(HeadE.HAPPY_TALKING, "I got the orb of protection.");
				addNPC(NPC, HeadE.CALM_TALK, "Incredible, for a human you really are something.");
				addPlayer(HeadE.HAPPY_TALKING, "Thanks... I think!");
				addNPC(NPC, HeadE.CALM_TALK, "I'll stay here with my troops and try and hold Khazard's men back. You return the orb to the gnome village. Go as quick as you can, the village is still unprotected.");
			}
			case QUEST_COMPLETE ->  {
				addPlayer(HeadE.HAPPY_TALKING, "Hello Montai, how are you?");
				addNPC(NPC, HeadE.CALM_TALK, "I'm ok, this battle is going to take longer to win than I expected. The Khazard troops won't give up even without the orb.");
				addPlayer(HeadE.HAPPY_TALKING, "Hang in there.");

			}
		}
	}

    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new CommanderMontaiTreeGnomeVillageD(e.getPlayer()).getStart()));
}
