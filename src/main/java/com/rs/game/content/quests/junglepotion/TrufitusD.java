package com.rs.game.content.quests.junglepotion;

import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class TrufitusD {

	private static int junglePotionStageToGrimyHerb(int stage) {
		return switch (stage) {
			case JunglePotion.FIND_SNAKE_WEED -> JunglePotion.GRIMY_SNAKE_WEED;
			case JunglePotion.FIND_ARDRIGAL -> JunglePotion.GRIMY_ARDRIGAL;
			case JunglePotion.FIND_SITO_FOIL -> JunglePotion.GRIMY_SITO_FOIL;
			case JunglePotion.FIND_VOLENCIA_MOSS -> JunglePotion.GRIMY_VOLENCIA_MOSS;
			case JunglePotion.FIND_ROGUES_PURSE -> JunglePotion.GRIMY_ROGUES_PURSE;
			default -> -1; // Meme
		};
	}

	private static int junglePotionStageToCleanHerb(int stage) {
		int grimyHerb = junglePotionStageToGrimyHerb(stage);
		if (grimyHerb == -1)
			return -1;
		return grimyHerb + 1;
	}

	public static NPCClickHandler HandleTrufitus = new NPCClickHandler(new Integer[] { 740 }, new String[] { "Talk-to" }, e -> {
		Player player = e.getPlayer();
		NPC npc = e.getNPC();
		if (!e.getOption().equals("Talk-to"))
			return;

		final int questStage = player.getQuestStage(Quest.JUNGLE_POTION);
		if (questStage == JunglePotion.QUEST_COMPLETE) {
			player.startConversation(new Dialogue()
					.addNPC(npc, HeadE.HAPPY_TALKING, "My greatest respects, bwana.<br>I  have communed with my gods and the future looks good for my people.")
					.addNPC(npc, HeadE.NERVOUS, "We are happy now that the gods are not angry with us.")
					.addNPC(npc, HeadE.CALM_TALK, "With some blessings, we will be safe here.")
					.addNPC(npc, HeadE.CALM_TALK, "You should deliver the good news to bwana Timfraku, chief of Tai Bwo Wannai.<br>He lives in a raised hut not far from here.")
			);
			return;
		}
		if (questStage == JunglePotion.NOT_STARTED) {
			Dialogue questStart = new Dialogue()
					.addNPC(npc, HeadE.CALM_TALK, "I need to make a special brew! A potion that helps me to commune with the gods. For this potion, I need very special herbs, that are only found in the deep jungle.")
					.addNPC(npc, HeadE.CALM_TALK, "I can only guide you so far, as the herbs are not easy to find. With some luck, you will find each herb in turn and bring it to me.")
					.addNPC(npc, HeadE.CALM_TALK, "I will then give you details of where to find the next herb. In return for this great favour, I will give you training in Herblore.")
					.addQuestStart(Quest.JUNGLE_POTION)
					.addNext(() -> player.getQuestManager().setStage(Quest.JUNGLE_POTION, JunglePotion.FIND_SNAKE_WEED))
					.addPlayer(HeadE.HAPPY_TALKING, "It sounds like just the challenge for me.<br>And it would make a nice break from killing things!")
					.addNPC(npc, HeadE.CHEERFUL, "That is excellent, bwana!")
					.addNPC(npc, HeadE.CALM_TALK, "The first herb that you need to gather is called 'snake weed'.<br>It grows near vines in an area to the south-west, where the ground turns soft and the water kisses your feet.")
					;
			player.startConversation(new Dialogue()
					.addNPC(npc, HeadE.HAPPY_TALKING, "Greetings, bwana!<br>I am Trufitus Shakaya of the Tai Bwo Wannai village.")
					.addNPC(npc, HeadE.HAPPY_TALKING, "Welcome to our humble village.")
					.addOptions((ops) -> {
						ops.add("What does 'bwana' mean?")
								.addNPC(npc, HeadE.HAPPY_TALKING, player.getPronoun("It means 'friend', gracious sir.", "Gracious lady, it means friend."))
								.addNPC(npc, HeadE.CONFUSED, "And friends come in peace.<br>I assume that you come in peace?")
								.addOptions((ops1) -> {
									ops1.add("Yes, of course I do.")
											.addNPC(npc, HeadE.HAPPY_TALKING, "Well, that is good news, as I may have a proposition for you.")
											.addOptions((ops2) -> {
												ops2.add("A proposition, eh? Sounds interesting.")
														.addNPC(npc, HeadE.HAPPY_TALKING, "I hoped you would think so. My people are afraid to stay in the village. They have returned to the jungle and I need to commune with the gods to see what fate befalls us. You can help me by collecting some herbs that I need.")
														.addNext(() -> player.startConversation(questStart));
												ops2.add("I am sorry, but I am very busy.");
											})
											;
									ops1.add("What does a warrior like me know about peace?")
											.addNPC(npc, HeadE.CALM_TALK, "When you grow weary of violence and seek a more enlightened path, please pay me a visit as I may have a proposition for you. For now, I must attend to the plight of my people. Please excuse me.")
											;
								})
								;
						ops.add("Tai Bwo Wannai? What does that mean?")
								.addNPC(npc, HeadE.CALM_TALK, "It means 'small clearing in the jungle', but it is now the name of our village.")
								.addPlayer(HeadE.CONFUSED, "It's a nice village, but where is everyone?")
								.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "My people are afraid to stay in the village. They have returned to the jungle. I need to commune with the gods to see what fate befalls us.")
								.addNPC(npc, HeadE.CONFUSED, "You may be able to help with this.")
								.addOptions((ops1) -> {
									ops1.add("Me? How can I help?")
											.addNext(() -> player.startConversation(questStart));
											;
									ops1.add("I am sorry, but I am very busy.")
											.addNPC(npc, HeadE.CALM_TALK, "Very well, then.<br>May your journeys bring you much joy.")
											.addNPC(npc, HeadE.CONFUSED, "Perhaps you will pass this way again, and then take up my proposal?")
											.addNPC(npc, HeadE.HAPPY_TALKING, "But for now, fare thee well.")
											;
								})
								;
						ops.add("It's a nice village, but where is everyone?")
								.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "My people are afraid to stay in the village. They have returned to the jungle. I need to commune with the gods to see what fate befalls us.")
								.addNPC(npc, HeadE.CONFUSED, "You may be able to help with this.")
								.addOptions((ops1) -> {
									ops1.add("Me? How can I help?")
											.addNext(() -> player.startConversation(questStart));
									ops1.add("I am sorry, but I am very busy.")
											.addNPC(npc, HeadE.CALM_TALK, "Very well, then.<br>May your journeys bring you much joy.")
											.addNPC(npc, HeadE.CONFUSED, "Perhaps you will pass this way again, and then take up my proposal?")
											.addNPC(npc, HeadE.HAPPY_TALKING, "But for now, fare thee well.")
											;
								})
								;
						ops.add("Goodbye.");
					}));
			return;
		}

		Dialogue grimyHerbReject = new Dialogue()
				.addNPC(npc, HeadE.CONFUSED, "I don't recognise that grimy herb, bwana.");

		if (questStage == JunglePotion.FIND_SNAKE_WEED) {
			player.startConversation(new Dialogue()
					.addNPC(npc, HeadE.CONFUSED, "Greetings, bwana.<br>Do you have the snake weed?")
					.addOptions((ops) -> {
						ops.add("Of course!")
								.addNext(() -> {
									if (player.getInventory().containsItem(junglePotionStageToCleanHerb(questStage))) {
										player.startConversation(new Dialogue()
												.addItem(junglePotionStageToCleanHerb(questStage), "You give the snake weed to Trufitus.")
												.addNext(() -> {
													player.getInventory().deleteItem((junglePotionStageToCleanHerb(questStage)), 1);
													player.setQuestStage(Quest.JUNGLE_POTION, player.getQuestStage(Quest.JUNGLE_POTION) + 1);
												})
												.addNPC(npc, HeadE.HAPPY_TALKING, "Great! You have the snake weed. Many thanks.")
												.addNPC(npc, HeadE.CALM_TALK, "The next herb is called 'ardrigal'.<br>It is related to the palm, and grows in its brother's shady profusion.")
												.addNPC(npc, HeadE.CALM_TALK, "To the north-east you will find a small peninsula. It is just after the cliffs come down to meet the sands. That is where you should search for it."))
												;
									} else if (player.getInventory().containsItem(junglePotionStageToGrimyHerb(questStage))) {
										player.startConversation(grimyHerbReject);
									} else {
										player.startConversation(new Dialogue()
												.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "Please don't try to deceive me.<br>I really need that snake weed if I am to make this potion."))
												;
									}
								})
								;
						ops.add("What's the clue again?")
								.addNPC(npc, HeadE.CALM_TALK, "The first herb that you need to gather is called 'snake weed'.<br>It grows near vines in an area to the south-west, where the ground turns soft and the water kisses your feet.")
								.addNPC(npc, HeadE.CALM_TALK, "I really need that snake weed if I am to make this potion.")
								;
						ops.add("Farewell.");
					}));
			return;
		}

		if (questStage == JunglePotion.FIND_ARDRIGAL) {
			player.startConversation(new Dialogue()
					.addNPC(npc, HeadE.CONFUSED, "Have you brought the ardrigal herb?")
					.addOptions((ops) -> {
						ops.add("Of course!")
								.addNext(() -> {
									if (player.getInventory().containsItem(junglePotionStageToCleanHerb(questStage))) {
										player.startConversation(new Dialogue()
												.addItem(junglePotionStageToCleanHerb(questStage), "You give the ardrigal to Trufitus.")
												.addNext(() -> {
													player.getInventory().deleteItem((junglePotionStageToCleanHerb(questStage)), 1);
													player.setQuestStage(Quest.JUNGLE_POTION, player.getQuestStage(Quest.JUNGLE_POTION) + 1);
												})
												.addNPC(npc, HeadE.HAPPY_TALKING, "Ardrigal! Wonderful.<br>You are doing well, bwana.")
												.addNPC(npc, HeadE.CALM_TALK, "Now I want you to find a herb called 'sito foil', and it grows best where the ground has been blackened by the living flame."))
												;
									} else if (player.getInventory().containsItem(junglePotionStageToGrimyHerb(questStage))) {
										player.startConversation(grimyHerbReject);
									} else {
										player.startConversation(new Dialogue()
												.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "Please don't try to deceive me.<br>I really need that ardrigal if I am to make this potion."))
												;
									}
								})
						;
						ops.add("What's the clue again?")
								.addNPC(npc, HeadE.CALM_TALK, "The next herb is called 'ardrigal'.<br>It is related to the palm, and grows in its brother's shady profusion.")
								.addNPC(npc, HeadE.CALM_TALK, "To the north-east you will find a small peninsula. It is just after the cliffs come down to meet the sands. That is where you should search for it.")
						;
						ops.add("Farewell.");
					}));
			return;
		}

		if (questStage == JunglePotion.FIND_SITO_FOIL) {
			player.startConversation(new Dialogue()
					.addNPC(npc, HeadE.CONFUSED, "Hello again, bwana.<br>Have you been successful in getting the sito foil?")
					.addOptions((ops) -> {
						ops.add("Of course!")
								.addNext(() -> {
									if (player.getInventory().containsItem(junglePotionStageToCleanHerb(questStage))) {
										player.startConversation(new Dialogue()
												.addItem(junglePotionStageToCleanHerb(questStage), "You give the sito foil to Trufitus.")
												.addNext(() -> {
													player.getInventory().deleteItem((junglePotionStageToCleanHerb(questStage)), 1);
													player.setQuestStage(Quest.JUNGLE_POTION, player.getQuestStage(Quest.JUNGLE_POTION) + 1);
												})
												.addNPC(npc, HeadE.HAPPY_TALKING, "Well done, bwana.<br>Just two more herbs to collect.")
												.addNPC(npc, HeadE.CALM_TALK, "The next herb is called 'volencia moss'. It clings to rocks for its existence, and is difficult to see, so you must search well for it.")
												.addNPC(npc, HeadE.CALM_TALK, "Volencia moss prefers rocks of high metal content and a frequently disturbed environment. There is some, I believe, to the south east of this village."))
												;
									} else if (player.getInventory().containsItem(junglePotionStageToGrimyHerb(questStage))) {
										player.startConversation(grimyHerbReject);
									} else {
										player.startConversation(new Dialogue()
												.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "Please don't try to deceive me.<br>I really need that sito foil if I am to make this potion."))
												;
									}
								})
								;
						ops.add("What's the clue again?")
								.addNPC(npc, HeadE.CALM_TALK, "Now I want you to find a herb called 'sito foil', and it grows best where the ground has been blackened by the living flame.")
								;
						ops.add("Farewell.");
					}));
			return;
		}

		if (questStage == JunglePotion.FIND_VOLENCIA_MOSS) {
			player.startConversation(new Dialogue()
					.addNPC(npc, HeadE.CONFUSED, "Greetings, bwana.<br>Do you have the volencia moss?")
					.addOptions((ops) -> {
						ops.add("Of course!")
								.addNext(() -> {
									if (player.getInventory().containsItem(junglePotionStageToCleanHerb(questStage))) {
										player.startConversation(new Dialogue()
												.addItem(junglePotionStageToCleanHerb(questStage), "You give the volencia moss to Trufitus.")
												.addNext(() -> {
													player.getInventory().deleteItem((junglePotionStageToCleanHerb(questStage)), 1);
													player.setQuestStage(Quest.JUNGLE_POTION, player.getQuestStage(Quest.JUNGLE_POTION) + 1);
												})
												.addNPC(npc, HeadE.HAPPY_TALKING, "Ah, volencia moss. Beautiful.<br>One final herb and the potion will be complete.")
												.addNPC(npc, HeadE.AMAZED, "This herb is the most difficult to find, as it inhabits the darkness of the underground.")
												.addNPC(npc, HeadE.CALM_TALK, "It is called 'rogue's purse', and is only to be found in pothole caverns in the northern part of this island.")
												.addNPC(npc, HeadE.CALM_TALK, "A secret entrance to the pothole caverns is set into the northern cliffs of this land. Take care, bwana, as it may be dangerous."))
												;
									} else if (player.getInventory().containsItem(junglePotionStageToGrimyHerb(questStage))) {
										player.startConversation(grimyHerbReject);
									} else {
										player.startConversation(new Dialogue()
												.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "Please don't try to deceive me.<br>I really need that volencia moss if I am to make this potion."))
												;
									}
								})
								;
						ops.add("What's the clue again?")
								.addNPC(npc, HeadE.CALM_TALK, "The next herb is called 'volencia moss'. It clings to rocks for its existence, and is difficult to see, so you must search well for it.")
								.addNPC(npc, HeadE.CALM_TALK, "Volencia moss prefers rocks of high metal content and a frequently disturbed environment. There is some, I believe, to the south east of this village.")
								;
						ops.add("Farewell.");
					}));
			return;
		}

		if (questStage == JunglePotion.FIND_ROGUES_PURSE) {
			player.startConversation(new Dialogue()
					.addNPC(npc, HeadE.CONFUSED, "Welcome back.<br>Have you been successful in getting the rogue's purse, bwana?")
					.addOptions((ops) -> {
						ops.add("Of course!")
								.addNext(() -> {
									if (player.getInventory().containsItem(junglePotionStageToCleanHerb(questStage))) {
										player.startConversation(new Dialogue()
												.addItem(junglePotionStageToCleanHerb(questStage), "You give the rogue's purse to Trufitus.")
												.addNext(() -> player.getInventory().deleteItem((junglePotionStageToCleanHerb(questStage)), 1))
												.addNPC(npc, HeadE.CALM_TALK, "Most excellent, bwana! You have returned all the herbs to me.")
												.addNPC(npc, HeadE.HAPPY_TALKING, "I can finish the preparations for the potion, and at last divine with the gods.<br>Many blessings upon you!")
												.addNPC(npc, HeadE.CALM_TALK, "I must now prepare.<br>Please excuse me while I make the arrangements.")
												.addNext(() -> player.getQuestManager().completeQuest(Quest.JUNGLE_POTION)))
												;
									} else if (player.getInventory().containsItem(junglePotionStageToGrimyHerb(questStage))) {
										player.startConversation(grimyHerbReject);
									} else {
										player.startConversation(new Dialogue()
												.addNPC(npc, HeadE.SAD_MILD_LOOK_DOWN, "Please don't try to deceive me.<br>I really need that rogue's purse if I am to make this potion."))
												;
									}
								})
								;
						ops.add("What's the clue again?")
								.addNPC(npc, HeadE.AMAZED, "This herb is the most difficult to find, as it inhabits the darkness of the underground.")
								.addNPC(npc, HeadE.CALM_TALK, "It is called 'rogue's purse', and is only to be found in pothole caverns in the northern part of this island.")
								.addNPC(npc, HeadE.CALM_TALK, "A secret entrance to the pothole caverns is set into the northern cliffs of this land. Take care, bwana, as it may be dangerous.")
								;
						ops.add("Farewell.");
					}));
			return;
		}

	});

}
