package com.rs.game.content.world.npcs;

import com.rs.game.content.quests.wolfwhistle.WolfWhistle;
import com.rs.game.content.quests.wolfwhistle.WolfWhistleWellCutscene;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Scalectrix extends Conversation {

	// npc id
	final static int SCALECTRIX = 15055;

	public Scalectrix(Player p) {
		super(p);

		switch (p.getQuestManager().getStage(Quest.WOLF_WHISTLE)) {
			case WolfWhistle.NOT_STARTED ->
					addNPC(SCALECTRIX, HeadE.WORRIED, "Oh dear...oh dear...")
							.addPlayer(HeadE.CONFUSED, "Are you alright?")
							.addSimple("The young druidess seems intent on staring into the well and wringing her hands, and does not reply.")
							.addPlayer(HeadE.CONFUSED, "Uh...ok then! Have a nice day!");
			case WolfWhistle.FIND_SCALECTRIX ->
				addPlayer(HeadE.CONFUSED, "Hey, are you Scalectrix; the assistant to Pikkupstix?")
						.addNPC(SCALECTRIX, HeadE.AMAZED, "Yes! How did you know that?")
						.addPlayer(HeadE.CALM_TALK, "He sent me to find you, apparently you were supposed to be back a while ago.")
						.addNPC(SCALECTRIX, HeadE.NERVOUS, "Well, I would have come back, but Bowloftrix has been captured by trolls! I've been waiting here for someone to come and help me!")
						.addPlayer(HeadE.ANGRY, "Trolls! Where are they?")
						.addNPC(SCALECTRIX, HeadE.WORRIED, "They're in this well. But don't go in there! There are way too many to handle!")
						.addNPC(SCALECTRIX, HeadE.WORRIED, "We need something to keep them at bay or break them up; something that will scare them. I tried using a spirit wolf, but they just laughed.")
						.addNPC(SCALECTRIX, HeadE.SAD, "The leaders seem immune to the howl of the spirit wolf...but without something to thin their numbers we'll never get Bowloftrix back!")
						.addNPC(SCALECTRIX, HeadE.WORRIED, "Please, sir, can you let Pikkupstix know what has happened? I'm sure with your help he can come up with something to drive away these trolls.")
						.addOptions("Select an Option", new Options() {
							@Override
							public void create() {
								// TODO:
								option("Look, I'll just go and take a look myself...", new Dialogue()
										.addPlayer(HeadE.CALM_TALK, "Look, I'll just go and take a look myself. I am trained for this sort of thing, after all.")
										.addNPC(SCALECTRIX, HeadE.AMAZED_MILD, "Be careful!", () -> {
											p.getQuestManager().setStage(Quest.WOLF_WHISTLE, WolfWhistle.PIKKUPSTIX_HELP);
										}));
								option("All right. Wait here, I'll be right back.", new Dialogue()
										.addPlayer(HeadE.CALM_TALK, "Alright. Wait here, I'll be right back.", () -> p.getQuestManager().setStage(Quest.WOLF_WHISTLE, WolfWhistle.PIKKUPSTIX_HELP)));
							}
						});
			case WolfWhistle.PIKKUPSTIX_HELP ->
				addNPC(SCALECTRIX, HeadE.CONFUSED, "So what's the plan?")
						.addPlayer(HeadE.CONFUSED, "The...plan?")
						.addNPC(SCALECTRIX, HeadE.AMAZED, "The plan! The plan to save Bowloftrix!")
						.addPlayer(HeadE.CALM, "Oh! That plan! Sorry, I still need to speak to Pikkupstix to find out what he has in mind.")
						.addNPC(SCALECTRIX, HeadE.SAD_MILD_LOOK_DOWN, "Well could you PLEASE hurry up?")
						.addPlayer(HeadE.CALM, "Hey, you can't rush heroism. I'll be right back.");
			case WolfWhistle.WOLPERTINGER_POUCH_CHECK ->
				addNPC(SCALECTRIX, HeadE.CONFUSED, "So what's the plan?")
						.addPlayer(HeadE.CHEERFUL_EXPOSITION, "I have made the giant wolpertinger pouch! I have it here, let's go!")
						.addNPC(SCALECTRIX, HeadE.AMAZED, "Wait, wait!")
						.addNPC(SCALECTRIX, HeadE.CONFUSED, "As pleased as I am that you have accomplished this feat, have you had the pouch checked by Pikkupstix?")
						.addPlayer(HeadE.CONFUSED, "Why do I need to do that? I mean, I'm a big darn hero. I know what I'm doing.")
						.addNPC(SCALECTRIX, HeadE.AMAZED, "But that's a legendary summoning spirit! If something has gone wrong in the creation of the pouch then anything could happen!")
						.addNPC(SCALECTRIX, HeadE.AMAZED, "The last time someone tired to summon a giant wolpertinger, the pouch they used was slightly frayed, and the spirit was so offended...well...it...")
						.addPlayer(HeadE.TERRIFIED, "It what? Didn't work? It was a little upset?")
						.addNPC(SCALECTRIX, HeadE.AMAZED, "Well, if by 'didn't work' you mean his skull melted, then yes!")
						.addPlayer(HeadE.TERRIFIED, "I'm, uh, just going to take this to Pikkupstix...just in case.")
						.addNPC(SCALECTRIX, HeadE.SAD_MILD_LOOK_DOWN, "Please, hurry back!");
			case WolfWhistle.SAVE_BOWLOFTRIX -> {
				if (player.getInventory().containsItem(WolfWhistle.GIANT_WOLPERTINGER_POUCH)) {
					addNPC(SCALECTRIX, HeadE.CONFUSED, "You're back! What's the news?")
							.addPlayer(HeadE.CALM, "I have a giant wolpertinger. Here, take a look.")
							.addNPC(SCALECTRIX, HeadE.AMAZED, "A giant wolpertinger pouch...I never thought I'd see one.")
							.addNPC(SCALECTRIX, HeadE.AMAZED, "With just the two of us it'll be a stretch, and we'll only be able to summon it for a few seconds, but that might be all the time we need.")
							.addPlayer(HeadE.HAPPY_TALKING, "Come on, let's get Bowloftrix before he comes a bowl of Bowloftrix.")
							.addNext(() -> {
								if (player.hasFamiliar() || player.getPet() != null) {
									p.startConversation(new Dialogue()
											.addNPC(SCALECTRIX, HeadE.CALM_TALK, "Wait, wait, we can't do this while you have a follower.")
											.addPlayer(HeadE.CONFUSED, "Why not?")
											.addNPC(SCALECTRIX, HeadE.CALM, "Well you can only have one follower, and if you want to summon the wolpertinger with my help then you'll need to put that one away.")
											.addPlayer(HeadE.CALM, "Fair enough. Give me a moment.")
									);
								} else {
									p.playCutscene(new WolfWhistleWellCutscene());
								}
							});
				} else {
					addNPC(SCALECTRIX, HeadE.CONFUSED, "You're back! What's the news?")
							.addPlayer(HeadE.CALM_TALK, "I have made a giant wolpertinger pouch, and I'm all ready to save Bowloftrix.")
							.addNPC(SCALECTRIX, HeadE.CHEERFUL_EXPOSITION, "That's wonderful news! Can I see the pouch?")
							.addPlayer(HeadE.CHEERFUL_EXPOSITION, "Of course, here it...")
							.addPlayer(HeadE.AMAZED, "...")
							.addPlayer(HeadE.SECRETIVE, "...uh, give me a minute...")
							.addNPC(SCALECTRIX, HeadE.CONFUSED, "Oh...of course.");
				}
			}
		}

	}

	public static NPCClickHandler handleScalectrixDialogue = new NPCClickHandler(new Object[] { SCALECTRIX }, e -> {
		e.getPlayer().startConversation(new Scalectrix(e.getPlayer()));
	});

}
