package com.rs.game.content.quests.blackknightsfortress;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

import static com.rs.game.content.quests.blackknightsfortress.BlackKnightsFortress.*;

@PluginEventHandler
public class SirAmikVarzeBlackKnightsFortressD extends Conversation {
	private final int QUEST_START_CONVO = 0;
	private final static int SIR_AMIK_VARZE = 608;

	public SirAmikVarzeBlackKnightsFortressD(Player p) {
		super(p);
		switch(p.getQuestManager().getStage(Quest.BLACK_KNIGHTS_FORTRESS)) {
			case NOT_STARTED -> {
				addPlayer(HeadE.SKEPTICAL_THINKING, "Who are you?");
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "I am the leader of the White Knights of Falador. Why do you seek my audience?");
				addOptions("Choose an option:", new Options() {
					@Override
					public void create() {
						if(p.getQuestManager().getQuestPoints() < 12)
							option("I seek a quest", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I seek a quest")
									.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Well I do have a task, but it is very dangerous and it's critical to us that no mistakes " +
											"are made")
									.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "I couldn't possibly let an unexperienced quester like yourself go")
									.addSimple("(You need at least 12 quest points before you may attempt this quest)"));
						else
							option("I seek a quest", new Dialogue()
									.addPlayer(HeadE.HAPPY_TALKING, "I seek a quest")
									.addNPC(SIR_AMIK_VARZE, HeadE.SKEPTICAL_THINKING, "Well, I need some spy work doing. It's quite dangerous")
									.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "You will need to go into the Black Knight's fortress")
									.addOptions("Choose an option:", new Options() {
										@Override
										public void create() {
											option("I laugh in the face of danger", new Dialogue()
													.addPlayer(HeadE.LAUGH, "I laugh in the face of danger")
													.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Well that's good. Don't get too overconfident though.")
													.addNext(() -> {
														p.startConversation(new SirAmikVarzeBlackKnightsFortressD(p, QUEST_START_CONVO).getStart());
													}));

											option("I go and cower in a corner at the first sign of danger", new Dialogue()
													.addPlayer(HeadE.SCARED, "I go and cower in a corner at the first sign of danger")
													.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Err...")
													.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Well...")
													.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Spy work does involve a little hiding in corners I suppose")
													.addOptions("Choose a response", new Options() {
														@Override
														public void create() {
															option("Oh I suppose I'll give it a go then", new Dialogue()
																	.addPlayer(HeadE.HAPPY_TALKING, "Oh I suppose I'll give it a go then")
																	.addNext(() -> {
																		p.startConversation(new SirAmikVarzeBlackKnightsFortressD(p, QUEST_START_CONVO).getStart());
																	}));
															option("No I'm not convinced", new Dialogue().addPlayer(HeadE.SCARED, "No I'm not convinced"));
														}
													}));
										}
									}));
						option("I don't i'm just looking around", new Dialogue()
								.addPlayer(HeadE.SKEPTICAL, "I don't i'm just looking around")
								.addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Ok, don't break anything"));
					}
				});
			}
			case STARTED -> {
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "You should head to the Black Knights Fortress and find out what their secret weapon is...");
				addPlayer(HeadE.CALM_TALK, "Okay, I will do that.");
			}
			case HEARD_PLAN -> {
				addPlayer(HeadE.AMAZED_MILD, "I found out their secret weapon!");
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Let's hear it!");
				addPlayer(HeadE.SKEPTICAL, "It is an invincibility potion...");
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Oh no, then we are surely doomed! Can you ruin the potion somehow?");
				addPlayer(HeadE.SKEPTICAL, "I can try!");
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Please do that!");
			}
			case RUINED_CAULDRON -> {
				addPlayer(HeadE.HAPPY_TALKING, "I have ruined the Black Knight's invincibility potion. That should put a stop to your problem and an end to their" +
						" little schemes.");
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Yes, we have just recieved a message from the Black Knights saying they withdraw their demands, which seems to confirm your" +
						" story.");
				addPlayer(HeadE.CALM_TALK, "Now I believe there was some talk of a cash reward...");
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Absoluely right. Please accept this reward.");
				addNext(()-> {
					p.getQuestManager().completeQuest(Quest.BLACK_KNIGHTS_FORTRESS);
				});
				}
			case QUEST_COMPLETE -> {
				addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Thank you for saving us from the Black Knights!");
				addPlayer(HeadE.HAPPY_TALKING, "Of course!");
			}
		}


	}

	public SirAmikVarzeBlackKnightsFortressD(Player p, int convoID) {
		super(p);
		switch(convoID) {
		case QUEST_START_CONVO -> {
			addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "You have come along just right actually, all of my knights are known to the black knights already. Subtlety isn't " +
					"exactly our strong point");
			addPlayer(HeadE.CALM_TALK, "So what needs doing?");
			addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "Well the black knights have started making strange threats to us, demanding " +
					"large amounts of money and land");
			addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "And threatening to invade Falador if we don't pay. Now normally this " +
					"wouldn't be a problem but they claim to have a powerful new secret weapon.");
			addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "What I want you to do is to get inside their fortress and find out what " +
					"their secret weapon is and then sabotage it.");
			addNPC(SIR_AMIK_VARZE, HeadE.CALM_TALK, "You will be well paid");
			addOptions("Start Black Knights Fortress?", new Options() {
				@Override
				public void create() {
					option("Yes", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "OK I'll give it a try.", ()->{
								p.getQuestManager().setStage(Quest.BLACK_KNIGHTS_FORTRESS, STARTED);
							}));
					option("No", new Dialogue());
				}
			});
		}
		}
	}

	public static NPCClickHandler handleSirAmik = new NPCClickHandler(new Object[] { SIR_AMIK_VARZE }, e -> e.getPlayer().startConversation(new SirAmikVarzeBlackKnightsFortressD(e.getPlayer()).getStart()));
}
