package com.rs.game.content.quests.templeofikov.dialogues;

import static com.rs.game.content.quests.templeofikov.TempleOfIkov.HELP_LUCIEN;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class GaurdianArmadylTempleOfIkov extends Conversation {
	class HandleBaptism extends Conversation {
		public HandleBaptism(Player player, NPC guardian) {
			super(player);
			int NPC = guardian.getId();
			addNPC(NPC, HeadE.CALM_TALK, "Okay, I shall cleanse you with holy water...");
				addSimple("He splashes water on your face...", ()->{
					guardian.setLockedForTicks(4);
					guardian.faceEntity(player);
					WorldTasks.delay(1, () -> {
						guardian.setNextAnimation(new Animation(805));
					});
				});
				addNPC(NPC, HeadE.CALM_TALK, "You have been cleansed!");
				addNPC(NPC, HeadE.CALM_TALK, "Lucien must not get hold of the staff! He would become too powerful!");
				addNPC(NPC, HeadE.CALM_TALK, "Hast thou come across this foul beast? If you know where he is you can help us defeat him.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("Ok! I'll help!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Ok! I'll help!")
								.addNPC(NPC, HeadE.CALM_TALK, "So he is close by?")
								.addPlayer(HeadE.HAPPY_TALKING, "Yes!")
								.addNPC(NPC, HeadE.CALM_TALK, "He must be after the staff. Here, take this pendant;" +
										" wear it to show your allegiance to us, then defeat him in combat. I doubt " +
										"you'll kill him, but it should discourage him for a while.")
								.addItem(87, "The Gaurdian gives you a pendant...", ()->{
									player.getInventory().addItem(new Item(87, 1), true);
									player.getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).setB("KnowsGuardianOffer", true);
								})
						);
						option("No! I shall not turn against my employer!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "No! I shall not turn against my employer!")
								.addNPC(NPC, HeadE.CALM_TALK, "Fool! You will die for your sins!", ()->{
									WorldTasks.delay(3, () -> {
										guardian.setTarget(player);
									});
								})
						);
						option("I need to think.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I need to think.")
								.addNPC(NPC, HeadE.CALM_TALK, "Linger a while and be at peace.")
						);
					}
				});
		}
	}

	public GaurdianArmadylTempleOfIkov(Player player, NPC guardian) {
		super(player);
		int NPC = guardian.getId();
		switch(player.getQuestManager().getStage(Quest.TEMPLE_OF_IKOV)) {
			default -> {
				addPlayer(HeadE.HAPPY_TALKING, "I don't think I belong here");
				addNPC(NPC, HeadE.CALM_TALK, "Perhaps not pilgrim...");
			}
			case HELP_LUCIEN -> {
				if(player.getQuestManager().getAttribs(Quest.TEMPLE_OF_IKOV).getB("KnowsGuardianOffer")) {
					addNPC(NPC, HeadE.CALM_TALK, "Have you rid us of Lucien yet?");
					addPlayer(HeadE.HAPPY_TALKING, "Not yet.");
					addNPC(NPC, HeadE.CALM_TALK, "Hurry friend! Time is against us!");
					if(player.getInventory().containsItem(87, 1))
						return;
					addPlayer(HeadE.HAPPY_TALKING, "Oh, and I lost that pendant...");
					addNPC(NPC, HeadE.CALM_TALK, "Okay, I have another.");
					addItem(87, "The Gaurdian gives you a pendant...", ()->{
						player.getInventory().addItem(new Item(87, 1), true);
					});
					return;
				}
				addNPC(NPC, HeadE.CALM_TALK, "Thou hast ventured deep into the tunnels, you have reached the temple of our master. It is many ages since a pilgrim has come here.");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						option("I seek the Staff of Armadyl.", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "I seek the Staff of Armadyl.")
								.addNPC(NPC, HeadE.CALM_TALK, "We are the guardians of the staff, our fathers were guardians and our father's fathers before that. Why dost thou seek it?")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("Lucien will give me a grand reward for it!", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Lucien will give me a grand reward for it!")
												.addNPC(NPC, HeadE.CALM_TALK, "Thou art working for that spawn of evil?! Fool. You must be cleansed to save your soul!")
												.addOptions("Choose an option:", new Options() {
													@Override
													public void create() {
														option("How dare you call me a fool?", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "How dare you call me a fool?")
																.addNPC(NPC, HeadE.CALM_TALK, "We must cleanse the temple!", ()->{
																	WorldTasks.delay(3, () -> {
																		guardian.setTarget(player);
																	});
																})
														);
														option("I just thought of something I must do!", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "I just thought of something I must do!")
																.addNPC(NPC, HeadE.CALM_TALK, "An agent of evil cannot be allowed to leave!", ()->{
																	WorldTasks.delay(3, () -> {
																		guardian.setTarget(player);
																	});
																})
														);
														option("You're right, it's time for my yearly bath.", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "You're right, it's time for my yearly bath.")
																.addNext(() -> player.startConversation(new HandleBaptism(player, guardian).getStart()))
														);
													}
												})
										);
										option("Give it to me!", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Give it to me!")
												.addNPC(NPC, HeadE.CALM_TALK, "The staff is sacred! You will not have it!")
										);
										option("I collect rare and powerful artefacts.", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "I collect rare and powerful artefacts.")
												.addNPC(NPC, HeadE.CALM_TALK, "Your worldy greed has darkened your soul!")
										);
									}
								})
						);
						option("Out of my way fool!", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "Out of my way fool!")
								.addNPC(NPC, HeadE.CALM_TALK, "I may be a fool but I will not step aside!")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("Why not?", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Why not?")
												.addNPC(NPC, HeadE.CALM_TALK, "Only members of our order are allowed to handle the staff.")
										);
										option("Then you must die!", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Then you must die!")
												.addNPC(NPC, HeadE.CALM_TALK, "So be it!", ()->{
													WorldTasks.delay(3, () -> {
														guardian.setTarget(player);
													});
												})
										);
										option("You're right, I will go now.", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "You're right, I will go now.")
												.addNPC(NPC, HeadE.CALM_TALK, "That is a wise decision. Stay a while and let your soul be cleansed!")
										);
									}
								})
						);
						option("What are your kind and what are you doing here?", new Dialogue()
								.addPlayer(HeadE.HAPPY_TALKING, "What are your kind and what are you doing here?")
								.addNPC(NPC, HeadE.CALM_TALK, "We are the Guardians of Armadyl. We have kept the temple safe for many ages. The evil " +
										"in the dungeons seek what lies here. The Mahjarrat are the worst who seek what is within.")
								.addOptions("Choose an option:", new Options() {
									@Override
									public void create() {
										option("What is the Armadyl?", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "What is the Armadyl?")
												.addNPC(NPC, HeadE.CALM_TALK, "Armadyl is the god we serve. We have been charged with guarding his sacred arctifacts until he requires them.")
												.addOptions("Choose an option:", new Options() {
													@Override
													public void create() {
														option("Ah ok, thanks.", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "Ah ok, thanks.")
																.addNPC(NPC, HeadE.CALM_TALK, "Go in peace.")
														);
														option("Someone told me there were only three gods.", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "Someone told me there were only three gods.")
																.addNPC(NPC, HeadE.CALM_TALK, "Saradominists. Bleh. They only acknowledge those three. " +
																		"There are at least twenty gods!")
														);
													}
												})
										);
										option("Who are the Mahjarrat?", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Who are the Mahjarrat?")
												.addNPC(NPC, HeadE.CALM_TALK, "They are ancient and powerful beings of evil! it is said that they once had " +
														"great influence over this plane of existence, and that Zamorak was once of their kind. ")
												.addNPC(NPC, HeadE.CALM_TALK, "They are far fewer in number now, but there are still some present in this world. " +
														"One such as Lucien would become nigh unstoppable if he were to possess the Staff of Armadyl")
												.addOptions("Choose an option:", new Options() {
													@Override
													public void create() {
														option("Did you say Lucien? It was Lucien that asked me to get the staff!", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "Did you say Lucien? It was Lucien that asked me to get the staff!")
																.addNPC(NPC, HeadE.CALM_TALK, "You are a fool to be working for Lucien! Your soul must be cleansed to save you!")
																.addPlayer(HeadE.HAPPY_TALKING, "You're right, it's time for my yearly bath.")
																.addNext(() -> player.startConversation(new HandleBaptism(player, guardian).getStart()))
														);
														option("I hope you are doing a good job then!", new Dialogue()
																.addPlayer(HeadE.HAPPY_TALKING, "I hope you are doing a good job then!")
																.addNPC(NPC, HeadE.CALM_TALK, "Do not fear! We are devoted to our purpose!")
														);
													}
												})
										);
										option("Wow! You must be really old!", new Dialogue()
												.addPlayer(HeadE.HAPPY_TALKING, "Wow! You must be really old!")
												.addNPC(NPC, HeadE.CALM_TALK, "No! I am not old! my family has guarded the staff for many generations.")
										);
									}
								})
						);
					}
				});
			}
		}
	}

}
