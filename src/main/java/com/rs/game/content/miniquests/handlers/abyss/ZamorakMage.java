package com.rs.game.content.miniquests.handlers.abyss;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.content.miniquests.Miniquest;
import com.rs.game.content.quests.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class ZamorakMage extends Conversation {
	@SuppressWarnings("unused")
	private static final int NPC = 2257;
	public ZamorakMage(Player p) {
		super(p);
		addNPC(NPC, HeadE.CHEERFUL, "Ah, you again. What was it you wanted? The Wilderness is hardly the appropriate place for a conversation now, is it?");

		switch(p.getMiniquestManager().getStage(Miniquest.ENTER_THE_ABYSS)) {
			case 2 -> {
				addNPC(NPC, HeadE.CONFUSED, "Well? Have you managed to use my scrying orb to obtain the information yet?");
				if (!player.getInventory().containsOneItem(5518, 5519)) {
					addPlayer(HeadE.WORRIED, "Uh... No.... I kind of lost that orb thingy that you gave me...");
					addNPC(NPC, HeadE.FRUSTRATED, "What? Incompetent fool. Take this. And do not make me regret allying myself with you.", () -> p.getInventory().addItemDrop(5519, 1));
					create();
					return;
				}
				if (player.getInventory().containsItem(5518, 1)) {
					addPlayer(HeadE.CHEERFUL, "Yes I have! I've got it right here!");
					addNPC(NPC, HeadE.SKEPTICAL, "Excellent. Give it here, and I shall examine the findings. Speak to me in a small while.", () -> {
						p.getInventory().deleteItem(5518, 28);
						p.getInventory().deleteItem(5519, 28);
						p.getMiniquestManager().setStage(Miniquest.ENTER_THE_ABYSS, 3);
					});
					create();
					return;
				}
				addPlayer(HeadE.WORRIED, "No... Actually, I had something I wanted to ask you...");
				addNPC(HeadE.FRUSTRATED, "I assumed the task to be self-explanatory. What is it you wish to know?");
			}
			case 3 -> {
				addPlayer(HeadE.CONFUSED, "So... that's my end of the deal upheld. What do I get in return?");
				addNPC(NPC, HeadE.CALM_TALK, "Indeed. A deal is always a deal. I offer you three things as a reward for your efforts on behalf of my Lord Zamorak; The first is knowledge. I offer you my collected research on the abyss. ");
				addNPC(NPC, HeadE.CALM_TALK, "I also offer you 1,000 points of experience in RuneCrafting for your trouble. Your second gift is convenience. Here, you may take this pouch I discovered amidst my research.");
				addNPC(NPC, HeadE.CALM_TALK, "You will find it to have certain... interesting properties. Your final gift is that of movement. I will from now on offer you a teleport to the abyss whenever you should require it.");
				addPlayer(HeadE.CONFUSED, "Huh? Abyss? What are you talking about? You told me that you would help me with RuneCrafting!");
				addNPC(NPC, HeadE.CALM_TALK, "And so I have done. Read my research notes, they may enlighten you somewhat.");
				addNext(() -> player.getMiniquestManager().complete(Miniquest.ENTER_THE_ABYSS));
				create();
				return;
			}
		}
		addOptions(new Options("startOptions", this) {
			@Override
			public void create() {
				switch(p.getMiniquestManager().getStage(Miniquest.ENTER_THE_ABYSS)) {
					case 1 -> {
						if (p.isQuestComplete(Quest.RUNE_MYSTERIES)) {
							option("Where do you get your runes from?", new Dialogue()
									.addPlayer(HeadE.CONFUSED, "Where do you get your runes from? No offence, but people around here don't exactly like 'your type'.")
									.addNPC(NPC, HeadE.FRUSTRATED, "My 'type'? Explain.")
									.addPlayer(HeadE.SKEPTICAL_THINKING, "You know... Scary bearded men in dark clothing with unhealthy obsessions with destruction and stuff.")
									.addNPC(NPC, HeadE.CALM_TALK, "Hmmm. Well, you may be right, the foolish Saradominists that own this pathetic city don't appreciate loyal Zamorakians, it is true.")
									.addPlayer(HeadE.SKEPTICAL, "So you can't be getting your runes anywhere around here...")
									.addNPC(NPC, HeadE.SECRETIVE, "That is correct, stranger. The Zamorakian Brotherhood has a method of manufacturing runes that it keeps a closely guarded secret.")
									.addPlayer(HeadE.CHEERFUL, "Oh, you mean the whole teleporting to the rune essence mine, mining some essence, then using the talismans to locate the Rune Temples, then binding runes there? I know all about it...")
									.addNPC(NPC, HeadE.AMAZED, "WHAT? I... but... you... Tell me, this is important: You have access to the rune essence mine? The Saradominist wizards will cast their teleport spell for you?")
									.addPlayer(HeadE.CONFUSED, "You mean they won't cast it for you?")
									.addNPC(NPC, HeadE.FRUSTRATED, "No, not at all. Ever since the Saradominist wizards betrayed us a hundred years ago and blamed us for the destruction of the Wizards' Tower, they have refused to share information with our order.")
									.addNPC(NPC, HeadE.FRUSTRATED, "We occasionally manage to plunder small samples of rune essence but we have had to make do without a reliable supply. But if they trust you...this changes everything.")
									.addPlayer(HeadE.CONFUSED, "How do you mean?")
									.addNPC(NPC, HeadE.CALM_TALK, "For many years there has been a struggle for power on this world.")
									.addNPC(NPC, HeadE.CALM_TALK, "You may dispute the morality of each side as you wish, but the stalemate that exists between my Lord Zamorak and that pathetic meddling fool Saradomin has meant that our struggles have become more secretive.")
									.addNPC(NPC, HeadE.CALM_TALK, "We exist in a 'cold war' if you will, each side fearful of letting the other gain too much power, and each side equally fearful of entering into open warfare for fear of bringing our struggles to the attention of... other beings.")
									.addPlayer(HeadE.CONFUSED, "You mean Guthix?")
									.addNPC(NPC, HeadE.CALM_TALK, "Indeed. Amongst others. But since the destruction of the first Tower the Saradominist wizards have had exclusive access to the rune essence mine, which has shifted the balance of power dangerously towards one side.")
									.addNPC(NPC, HeadE.CALM_TALK, "I implore you adventurer, you may or may not agree with my aims, but you cannot allow such a shift in the balance of power to continue.")
									.addNPC(NPC, HeadE.CONFUSED, "Will you help me and my fellow Zamorakians to access the essence mine? In return I will share with you the research we have gathered.")
									.addOptions("Help the Zamorakian mage?", new Options() {
										@Override
										public void create() {
											option("Yes", new Dialogue()
													.addPlayer(HeadE.CALM_TALK, "Okay, I'll help you. What can I do?")
													.addNPC(NPC, HeadE.CALM_TALK, "All I need from you is the spell that will teleport me to this essence mine. That should be sufficient for the armies of Zamorak to once more begin stockpiling magic for war.")
													.addPlayer(HeadE.SKEPTICAL_THINKING, "Oh. Erm.... I don't actually know that spell.")
													.addNPC(NPC, HeadE.CONFUSED, "What? Then how do you access this location?")
													.addPlayer(HeadE.CHEERFUL, "Oh, well, people who do know the spell teleport me there directly. Apparently they wouldn't teach it to me to try and keep the location secret.")
													.addNPC(NPC, HeadE.FRUSTRATED, "Hmmm. Yes, yes I see. Very well then, you may still assist us in finding this mysterious essence mine.")
													.addPlayer(HeadE.CONFUSED, "How would I do that?")
													.addNPC(NPC, HeadE.CALM_TALK, "Here, take this scrying orb. I have cast a standard cypher spell upon it, so that it will absorb mystical energies that it is exposed to.")
													.addNPC(NPC, HeadE.CALM_TALK, "Bring it with you and teleport to the rune essence location, and it will absorb the mechanics of the spell and allow us to reverse-engineer the magic behind it.")
													.addNPC(NPC, HeadE.CALM_TALK, "The important part is that you must teleport to the essence location from three entirely separate locations.")
													.addNPC(NPC, HeadE.CALM_TALK, "More than three may be helpful to us, but we need a minimum of three in order to triangulate the position of this essence mine.")
													.addNPC(NPC, HeadE.CONFUSED, "Is that all clear, stranger?", () -> {
														player.getMiniquestManager().setStage(Miniquest.ENTER_THE_ABYSS, 2);
														player.getInventory().addItemDrop(5519, 1);
													})
													.addPlayer(HeadE.CALM_TALK, "Yeah, I think so.")
													.addNPC(NPC, HeadE.CALM_TALK, "Good. If you encounter any difficulties speak to me again."));
										}
									}));
						} else {
							option("Where do you get your runes from?", new Dialogue()
									.addNPC(NPC, HeadE.CHEERFUL, "I get my runes from the Zamorak Mage Arena. You can find it in the Wilderness. I'm sure you'll be able to find it.")
							);
						}
					}
					case 2 -> {
						option("What am I supposed to be doing again?", new Dialogue()
								.addPlayer(HeadE.CONFUSED, "Please excuse me, I have a very bad short term memory. What exactly am I supposed to be doing again?")
								.addNPC(NPC, HeadE.FRUSTRATED, "I am slightly concerned about your capability for this mission, if you cannot even recall such a simple task...")
								.addNPC(NPC, HeadE.CALM_TALK, "All I wish for you to do is to teleport to this 'rune essence' location from three different locations while carrying the scrying orb I gave you.")
								.addNPC(NPC, HeadE.CALM_TALK, "It will collect the data as you teleport, and if you then bring it to me I will be able to use it to further my own investigations.")
								.addPlayer(HeadE.CONFUSED, "So you want me to teleport to the rune essence mine while carrying the scrying ball?")
								.addNPC(NPC, HeadE.CALM_TALK, "That is correct.")
								.addPlayer(HeadE.CONFUSED, "And I need to teleport into the essence mine from three different locations?")
								.addNPC(NPC, HeadE.CALM_TALK, "That is also correct.")
								.addPlayer(HeadE.SKEPTICAL_THINKING, "Okay... I think I understand that...")
								.addNPC(NPC, HeadE.CONFUSED, "That is good to know. Is there something else you need clarifying?")
								.addGotoStage("startOptions", ZamorakMage.this));

						option("What's in this for me, anyway?", new Dialogue()
								.addPlayer(HeadE.CONFUSED, "I just want to know one thing: What's in this for me?")
								.addNPC(NPC, HeadE.LAUGH, "Well now, I can certainly understand that attitude.")
								.addNPC(NPC, HeadE.CALM_TALK, "From what you tell me, the Saradominist wizards have given you access to the rune essence mine, but your method of reaching the temples used to bind this essence is random and counter-productive.")
								.addNPC(NPC, HeadE.CALM_TALK, "I, on the other hand, have information on quickly accessing these temples, yet my methods of procuring essence are time consuming and useless as a means of mass production.")
								.addNPC(NPC, HeadE.CALM_TALK, "I think you can see for yourself how this may benefit both of us: you allow me the knowledge of finding plentiful essence, and I will in return show you the secret of these temples.")
								.addPlayer(HeadE.CALM_TALK, "Yeah, okay, fair enough.")
								.addNPC(NPC, HeadE.CONFUSED, "Was there anything else?")
								.addGotoStage("startOptions", ZamorakMage.this));

						option("You're not going to use this for evil are you?", new Dialogue()
								.addPlayer(HeadE.CONFUSED, "If I help you with this... I'd just like to keep my conscience clear and know that you're not going to use whatever information I give to you in the pursuit of evil and badness and stuff.")
								.addNPC(NPC, HeadE.SKEPTICAL_THINKING, "Very well, if it makes you feel any better; I promise you that any knowledge you provide me with, absolutely will not be used in the service of anything more evil than what I was already planning on doing anyway. I hope that sets your conscience at rest.")
								.addPlayer(HeadE.CONFUSED, "You know... In a weird way it actually does...")
								.addNPC(NPC, HeadE.CALM_TALK, "Excellent. Was there anything else?")
								.addGotoStage("startOptions", ZamorakMage.this));
					}
					case 3 -> {

					}
				}
				option("All hail Zamorak!", new Dialogue()
						.addPlayer(HeadE.CHUCKLE, "All hail Zamorak! He's the man! If he can't do it, maybe some other guy can!")
						.addNPC(NPC, HeadE.SECRETIVE, "...Okay. I appreciate your enthusiasm for Lord Zamorak stranger, but what exactly was it that you wanted?")
						.addGotoStage("startOptions", ZamorakMage.this));
				option("Nothing, thanks.", new Dialogue()
						.addPlayer(HeadE.CALM_TALK, "I didn't really want anything, thanks. I just like talking to random people I meet around the world.")
						.addNPC(NPC, HeadE.SECRETIVE, "...I see. Well, in the future, do not waste my time, or you will feel the wrath of Zamorak upon you."));
			}
		});
		create();
	}
}
