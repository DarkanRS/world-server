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
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class KingBolrenTreeGnomeVillageD extends Conversation {
	private static final int NPC = 469;
	public KingBolrenTreeGnomeVillageD(Player player) {
		super(player);
		switch(player.getQuestManager().getStage(Quest.TREE_GNOME_VILLAGE)) {
			case NOT_STARTED -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello.");
				addNPC(NPC, HeadE.CALM_TALK, "Well hello stranger. My name's Bolren, I'm the king of the tree gnomes.");
				addNPC(NPC, HeadE.CALM_TALK, "I'm surprised you made it in, maybe I made the maze too easy.");
				addPlayer(HeadE.HAPPY_TALKING, "Maybe.");
				addNPC(NPC, HeadE.CALM_TALK, "I'm afraid I have more serious concerns at the moment. Very serious.");
				addOptions("Choose an option", new Options() {
					@Override
					public void create() {
						option("Can I help at all?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Can I help at all?")
								.addNPC(NPC, HeadE.CALM_TALK, "I'm glad you asked.")
								.addNPC(NPC, HeadE.CALM_TALK, "The truth is my people are in grave danger. We have always been protected by the Spirit Tree. " +
										"No creature of dark can harm us while its three orbs are in place.")
								.addNPC(NPC, HeadE.CALM_TALK, "We are not a violent race, but we fight when we must. Many gnomes have fallen battling the dark " +
										"forces of Khazard to the North.")
								.addNPC(NPC, HeadE.CALM_TALK, "We became desperate, so we took one orb of protection to the battlefield. It was a foolish move.")
								.addNPC(NPC, HeadE.CALM_TALK, "Khazard troops seized the orb. Now we are completely defenceless.")
								.addPlayer(HeadE.HAPPY_TALKING, "How can I help?")
								.addNPC(NPC, HeadE.CALM_TALK, "You would be a huge benefit on the battlefield. If you would go there and try to retrieve the orb, " +
										"my people and I will be forever grateful.")
								.addOptions("Start Tree Gnome Village?", new Options() {
									@Override
									public void create() {
										option("Yes", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "I would be glad to help.", ()->{
													player.getQuestManager().setStage(Quest.TREE_GNOME_VILLAGE, TALK_TO_MONTAI_ABOUT_WOOD);
												})
												.addNPC(NPC, HeadE.CALM_TALK, "Thank you. The battlefield is to the north of the maze. Commander Montai will inform you of their current situation.")
												.addNPC(NPC, HeadE.CALM_TALK, "That is if he's still alive.")
												.addNPC(NPC, HeadE.CALM_TALK, "My assistant shall guide you out. Good luck friend, try your best to return the orb")
												.addSimple("You are guided out of the maze", ()->{
													KingBolrenTreeGnomeVillageD.this.player.lock(3);
													WorldTasks.delay(3, () -> {
														KingBolrenTreeGnomeVillageD.this.player.startConversation(new Dialogue().addNPC(473, HeadE.HAPPY_TALKING, "We're out of the maze now. Please hurry, we must have the orb if we are to survive."));
													});
													KingBolrenTreeGnomeVillageD.this.player.fadeScreen(() -> {
														KingBolrenTreeGnomeVillageD.this.player.sendMessage("Elkoy leads you through the maze...");
														KingBolrenTreeGnomeVillageD.this.player.setNextTile(KingBolrenTreeGnomeVillageD.this.player.getY() > 3177 ? Tile.of(2515, 3160, 0) : Tile.of(2502, 3193, 0));
													});
												})
										);
										option("No", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "I'm sorry but I won't be involved.")
												.addNPC(NPC, HeadE.CALM_TALK, "Ok then, travel safe.")
										);
									}
								})
						);
						option("I'll leave you to it then.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I'll leave you to it then.")
								.addNPC(NPC, HeadE.CALM_TALK, "Ok, take care.")
						);
					}
				});
			}
			case TALK_TO_MONTAI_ABOUT_WOOD, GET_WOOD, TALK_TO_MONTAI_ABOUT_TRACKERS, FIRE_BALLISTA -> {
				addPlayer(HeadE.HAPPY_TALKING, "Hello Bolren.");
				addNPC(NPC, HeadE.CALM_TALK, "Hello traveller, we must retrieve the orb. It's being held by Khazard troops north of here.");
				addPlayer(HeadE.HAPPY_TALKING, "Yes I will be sure to help.");
				addNPC(NPC, HeadE.CALM_TALK, "Talk to Commander Montai for more information.");
				addPlayer(HeadE.HAPPY_TALKING, "Ok, I'll try my best.");

			}
			case ORB1 -> {
				if(this.player.getInventory().containsItem(587)) {
					addPlayer(HeadE.HAPPY_TALKING, "I have the orb.");
					addNPC(NPC, HeadE.SAD_CRYING, "Oh my... The misery, the horror!");
					addPlayer(HeadE.SECRETIVE, "King Bolren, are you OK?");
					addNPC(NPC, HeadE.SAD, "Thank you traveller, but it's too late. We're all doomed.");
					addPlayer(HeadE.CALM_TALK, "What happened?");
					addNPC(NPC, HeadE.SAD_MILD_LOOK_DOWN, "They came in the night. I don't know how many, but enough.");
					addPlayer(HeadE.SECRETIVE, "Who?");
					addNPC(NPC, HeadE.SAD_EXTREME, "Khazard troops. They slaughtered anyone who got in their way. Women, children, my wife.");
					addPlayer(HeadE.SAD, "I'm sorry.");
					addNPC(NPC, HeadE.SAD_SNIFFLE, "They took the other orbs, now we are defenceless.");
					addPlayer(HeadE.SECRETIVE, "Where did they take them?");
					addNPC(NPC, HeadE.SECRETIVE, "They headed north of the stronghold. A warlord carries the orbs.");
					addOptions("Choose an option:", new Options() {
						@Override
						public void create() {
							option("I will go get the orb", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I will find the warlord and bring back the orbs.")
									.addNPC(NPC, HeadE.CALM_TALK, "You are brave, but this task will be tough even for you. I wish you the best of luck. Once again you are our only hope.")
									.addNPC(NPC, HeadE.CALM_TALK, "I will safeguard this orb and pray for your safe return. My assistant will guide you out.")
									.addSimple("Elkoy guides you out of the maze.", ()->{
										KingBolrenTreeGnomeVillageD.this.player.lock(3);
										KingBolrenTreeGnomeVillageD.this.player.getInventory().deleteItem(587, 1);
										KingBolrenTreeGnomeVillageD.this.player.getQuestManager().setStage(Quest.TREE_GNOME_VILLAGE, KILL_WARLORD);
										WorldTasks.delay(3, () -> {
											KingBolrenTreeGnomeVillageD.this.player.startConversation(new Dialogue().addNPC(473, HeadE.HAPPY_TALKING, "Here we are, " + (KingBolrenTreeGnomeVillageD.this.player.getY() > 3177 ? "feel free to have a look around." : "off you go.")));
										});
										KingBolrenTreeGnomeVillageD.this.player.fadeScreen(() -> {
											KingBolrenTreeGnomeVillageD.this.player.sendMessage("Elkoy leads you through the maze...");
											KingBolrenTreeGnomeVillageD.this.player.setNextTile(KingBolrenTreeGnomeVillageD.this.player.getY() > 3177 ? Tile.of(2515, 3160, 0) : Tile.of(2502, 3193, 0));
										});
									})
							);
							option("I'm sorry but I can't help.", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I'm sorry but I can't help.")
									.addNPC(NPC, HeadE.CALM_TALK, "I understand, this isn't your battle.")
							);
						}
					});
					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "Hello Bolren.");
				addNPC(NPC, HeadE.CALM_TALK, "Do you have the orb?");
				addPlayer(HeadE.HAPPY_TALKING, "No, I'm afraid not.");
				addNPC(NPC, HeadE.CALM_TALK, "Please, we must have the orb if we are to survive.");
			}
			case KILL_WARLORD -> {
				if(this.player.getInventory().containsItem(588)) {
					addPlayer(HeadE.HAPPY_TALKING, "Bolren, I have returned.");
					addNPC(NPC, HeadE.CALM_TALK, "You made it back! Do you have the orbs?");
					addPlayer(HeadE.HAPPY_TALKING, "I have them here.");
					addNPC(NPC, HeadE.CALM_TALK, "Hooray, you're amazing. I didn't think it was possible but you've saved us.");
					addNPC(NPC, HeadE.CALM_TALK, "Once the orbs are replaced we will be safe once more.");
					addNext(()->{
						this.player.getQuestManager().completeQuest(Quest.TREE_GNOME_VILLAGE);
					});
					/* We must begin the ceremony immediately.");
					addPlayer(HeadE.HAPPY_TALKING, "What does the ceremony involve?");
					addNPC(NPC, HeadE.CALM_TALK, "The spirit tree has looked over us for centuries. Now we must pay our respects.");
					addNext(()->{TreeGnomeParty(player);});*/

					return;
				}
				addPlayer(HeadE.HAPPY_TALKING, "Hello Bolren.");
				addNPC(NPC, HeadE.CALM_TALK, "The orbs are gone, taken north of the battlefield by a Khazard warlord. We're all doomed.");
			}
			case QUEST_COMPLETE ->  {
				addPlayer(HeadE.HAPPY_TALKING, "Hello again Bolren.");
				addNPC(NPC, HeadE.CALM_TALK, "Well hello, it's good to see you again.");
				if(!this.player.getInventory().containsItem(589)) {
					addPlayer(HeadE.HAPPY_TALKING, "I've lost my amulet.");
					addNPC(NPC, HeadE.CALM_TALK, "Oh dear. Here, take another. We truly are indebted to you.", ()->{
						this.player.getInventory().addItem(new Item(589, 1), true);
					});
				}

			}
		}
	}

	@SuppressWarnings("unused")
	private void treeGnomeParty(Player p) {

	}

    public static NPCClickHandler handleDialogue = new NPCClickHandler(new Object[]{NPC}, e -> e.getPlayer().startConversation(new KingBolrenTreeGnomeVillageD(e.getPlayer()).getStart()));
}
